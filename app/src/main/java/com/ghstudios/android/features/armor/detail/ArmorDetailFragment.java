package com.ghstudios.android.features.armor.detail;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ghstudios.android.ClickListeners.ItemClickListener;
import com.ghstudios.android.ClickListeners.SkillClickListener;
import com.ghstudios.android.components.ColumnLabelTextCell;
import com.ghstudios.android.components.ItemRecipeCell;
import com.ghstudios.android.components.LabelTextRowCell;
import com.ghstudios.android.components.SlotsView;
import com.ghstudios.android.components.TitleBarCell;
import com.ghstudios.android.data.classes.Armor;
import com.ghstudios.android.data.classes.Component;
import com.ghstudios.android.data.classes.Item;
import com.ghstudios.android.data.classes.ItemToSkillTree;
import com.ghstudios.android.features.wishlist.external.WishlistDataAddDialogFragment;
import com.ghstudios.android.mhgendatabase.R;
import com.ghstudios.android.mhgendatabase.databinding.FragmentArmorDetailBinding;

import java.util.List;

import androidx.lifecycle.ViewModelProvider;

public class ArmorDetailFragment extends Fragment {
    private static final String ARG_ARMOR_ID = "ARMOR_ID";

    private static final String DIALOG_WISHLIST_ADD = "wishlist_add";

    private ArmorDetailViewModel viewModel;
    private Armor armor; // set using the viewmodel

    private FragmentArmorDetailBinding binding;

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

        viewModel = new ViewModelProvider(this).get(ArmorDetailViewModel.class);

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
        binding = FragmentArmorDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // bind view model data to view
        viewModel.getArmorData().observe(getViewLifecycleOwner(), this::populateArmor);
        viewModel.getSkillData().observe(getViewLifecycleOwner(), this::populateSkills);
        viewModel.getComponentData().observe(getViewLifecycleOwner(), this::populateRecipe);
    }

    private void populateArmor(Armor armor) {
        if (armor == null) return;

        this.armor = armor;

        binding.titlebar.setTitleText(armor.getName());
        binding.titlebar.setIcon(armor);
        binding.titlebar.setAltTitleText(getString(R.string.value_rare, armor.getRarityString()));

        String cellPart = "" + armor.getSlot();
        String cellDefense = "" + armor.getDefense() + "~" + armor.getMaxDefense();

        binding.partSlots.slots.setSlots(armor.getNumSlots(), 0);
        binding.part.setValueText(cellPart);
        binding.defense.setValueText(cellDefense);

        binding.armorResists.fireRes.setText(String.valueOf(armor.getFireRes()));
        binding.armorResists.waterRes.setText(String.valueOf(armor.getWaterRes()));
        binding.armorResists.iceRes.setText(String.valueOf(armor.getIceRes()));
        binding.armorResists.thunderRes.setText(String.valueOf(armor.getThunderRes()));
        binding.armorResists.dragonRes.setText(String.valueOf(armor.getDragonRes()));
    }

    private void populateSkills(List<ItemToSkillTree> skills) {
        binding.skillList.removeAllViews();
        if (skills.size() == 0)  {
            binding.skillSection.setVisibility(View.GONE);
            return;
        }

        binding.skillSection.setVisibility(View.VISIBLE);
        for (ItemToSkillTree skill : skills) {
            LabelTextRowCell skillItem = new LabelTextRowCell(getContext());
            skillItem.setLabelText(skill.getSkillTree().getName());
            skillItem.setValueText(String.valueOf(skill.getPoints()));

            skillItem.setOnClickListener(
                    new SkillClickListener(getContext(), skill.getSkillTree().getId())
            );

            binding.skillList.addView(skillItem);
        }
    }

    private void populateRecipe(List<Component> recipe) {
        if (recipe == null || recipe.isEmpty()) {
            binding.recipeHeader.setVisibility(View.GONE);
            binding.recipe.setVisibility(View.GONE);
            return;
        }

        binding.recipeHeader.setVisibility(View.VISIBLE);
        binding.recipe.setVisibility(View.VISIBLE);

        for (Component component : recipe) {
            Item item = component.getComponent();
            View itemCell = binding.recipe.addItem(item, item.getName(), component.getQuantity(),component.isKey());
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
