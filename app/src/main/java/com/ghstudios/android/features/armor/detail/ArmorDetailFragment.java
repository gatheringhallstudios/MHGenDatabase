package com.ghstudios.android.features.armor.detail;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ghstudios.android.AppSettings;
import com.ghstudios.android.ClickListeners.ItemClickListener;
import com.ghstudios.android.ClickListeners.SkillClickListener;
import com.ghstudios.android.components.ColumnLabelTextCell;
import com.ghstudios.android.components.ItemRecipeCell;
import com.ghstudios.android.components.LabelTextCell;
import com.ghstudios.android.components.TitleBarCell;
import com.ghstudios.android.data.classes.Armor;
import com.ghstudios.android.data.classes.Component;
import com.ghstudios.android.data.classes.Item;
import com.ghstudios.android.data.classes.ItemToSkillTree;
import com.ghstudios.android.features.wishlist.external.WishlistDataAddDialogFragment;
import com.ghstudios.android.mhgendatabase.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ArmorDetailFragment extends Fragment {
    private static final String ARG_ARMOR_ID = "ARMOR_ID";

    private static final String DIALOG_WISHLIST_ADD = "wishlist_add";

    private ArmorDetailViewModel viewModel;
    private Armor armor; // set using the viewmodel

    @BindView(R.id.titlebar)
    TitleBarCell titleBar;

    @BindView(R.id.slots) ColumnLabelTextCell slotsReqView;
    @BindView(R.id.defense) ColumnLabelTextCell defenseView;
    @BindView(R.id.part) ColumnLabelTextCell partView;

    @BindView(R.id.skill_section) ViewGroup skillSection;
    @BindView(R.id.skill_list) LinearLayout skillListView;

    @BindView(R.id.recipe_header) View recipeHeader;
    @BindView(R.id.recipe) ItemRecipeCell recipeView;
    
    private TextView fireResTextView;
    private TextView waterResTextView;
    private TextView iceResTextView;
    private TextView thunderResTextView;
    private TextView dragonResTextView;

    public static ArmorDetailFragment newInstance(long armorId) {
        Bundle args = new Bundle();
        args.putLong(ARG_ARMOR_ID, armorId);
        ArmorDetailFragment f = new ArmorDetailFragment();
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        viewModel = ViewModelProviders.of(this).get(ArmorDetailViewModel.class);

        // Check for a Item ID as an argument, and find the item
        Bundle args = getArguments();
        if (args == null) {
            return;
        }

        long armorId = args.getLong(ARG_ARMOR_ID, -1);
        viewModel.loadArmor(armorId);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_armor_detail,
                container, false);

        ButterKnife.bind(this, view);

        fireResTextView = view.findViewById(R.id.fire_res);
        waterResTextView = view.findViewById(R.id.water_res);
        iceResTextView = view.findViewById(R.id.ice_res);
        thunderResTextView = view.findViewById(R.id.thunder_res);
        dragonResTextView = view.findViewById(R.id.dragon_res);
        
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // bind view model data to view
        viewModel.getArmorData().observe(this, this::populateArmor);
        viewModel.getSkillData().observe(this, this::populateSkills);
        viewModel.getComponentData().observe(this, this::populateRecipe);
    }

    private void populateArmor(Armor armor) {
        if (armor == null) return;

        this.armor = armor;

        titleBar.setTitleText(armor.getName());
        titleBar.setIcon(armor);
        titleBar.setAltTitleText(getString(R.string.value_rare, armor.getRarityString()));

        String cellPart = "" + armor.getSlot();
        String cellDefense = "" + armor.getDefense() + "~" + armor.getMaxDefense();
        String cellSlot = armor.getSlotString();

        slotsReqView.setValueText(cellSlot);
        partView.setValueText(cellPart);
        defenseView.setValueText(cellDefense);

        fireResTextView.setText(String.valueOf(armor.getFireRes()));
        waterResTextView.setText(String.valueOf(armor.getWaterRes()));
        iceResTextView.setText(String.valueOf(armor.getIceRes()));
        thunderResTextView.setText(String.valueOf(armor.getThunderRes()));
        dragonResTextView.setText(String.valueOf(armor.getDragonRes()));
    }

    private void populateSkills(List<ItemToSkillTree> skills) {
        skillListView.removeAllViews();
        if (skills.size() == 0)  {
            skillSection.setVisibility(View.GONE);
            return;
        }

        skillSection.setVisibility(View.VISIBLE);
        for (ItemToSkillTree skill : skills) {
            LabelTextCell skillItem = new LabelTextCell(getContext());
            skillItem.setLabelText(skill.getSkillTree().getName());
            skillItem.setValueText(String.valueOf(skill.getPoints()));

            skillItem.setOnClickListener(
                    new SkillClickListener(getContext(), skill.getSkillTree().getId())
            );

            skillListView.addView(skillItem);
        }
    }

    private void populateRecipe(List<Component> recipe) {
        if (recipe == null || recipe.isEmpty()) {
            recipeHeader.setVisibility(View.GONE);
            recipeView.setVisibility(View.GONE);
            return;
        }

        recipeHeader.setVisibility(View.VISIBLE);
        recipeView.setVisibility(View.VISIBLE);

        for (Component component : recipe) {
            Item item = component.getComponent();
            View itemCell = recipeView.addItem(item, item.getName(), component.getQuantity());
            itemCell.setOnClickListener(new ItemClickListener(getContext(), item));
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_add_to_wishlist, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_to_wishlist:
                if (armor != null) {
                    FragmentManager fm = this.getFragmentManager();
                    WishlistDataAddDialogFragment dialogCopy = WishlistDataAddDialogFragment
                            .newInstance(armor.getId(), armor.getName());
                    dialogCopy.show(fm, DIALOG_WISHLIST_ADD);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
