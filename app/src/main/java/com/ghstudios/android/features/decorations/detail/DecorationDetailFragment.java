package com.ghstudios.android.features.decorations.detail;

import androidx.lifecycle.ViewModelProviders;
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

import com.ghstudios.android.ClickListeners.ItemClickListener;
import com.ghstudios.android.ClickListeners.SkillClickListener;
import com.ghstudios.android.components.ItemRecipeCell;
import com.ghstudios.android.components.LabelTextRowCell;
import com.ghstudios.android.data.classes.Component;
import com.ghstudios.android.data.classes.Decoration;
import com.ghstudios.android.data.classes.Item;
import com.ghstudios.android.features.wishlist.external.WishlistDataAddDialogFragment;
import com.ghstudios.android.mhgendatabase.R;
import com.ghstudios.android.mhgendatabase.databinding.FragmentDecorationDetailBinding;

import java.util.List;
import java.util.Map;

public class DecorationDetailFragment extends Fragment {
    private static final String ARG_DECORATION_ID = "DECORATION_ID";

    private static final String DIALOG_WISHLIST_ADD = "wishlist_add";

    private FragmentDecorationDetailBinding binding;

    // stored to allow add to wishlist to work
    long decorationId;
    String decorationName;

    public static DecorationDetailFragment newInstance(long decorationId) {
        Bundle args = new Bundle();
        args.putLong(ARG_DECORATION_ID, decorationId);
        DecorationDetailFragment f = new DecorationDetailFragment();
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        // Check for a Item ID as an argument, and find the item
        Bundle args = getArguments();
        if (args == null) {
            return;
        }

        decorationId = args.getLong(ARG_DECORATION_ID, -1);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        binding = FragmentDecorationDetailBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (decorationId == -1) {
            return;
        }

        DecorationDetailViewModel viewModel = ViewModelProviders.of(this).get(DecorationDetailViewModel.class);
        viewModel.setDecoration(decorationId);

        viewModel.getDecorationData().observe(this, this::populateDecoration);
        viewModel.getDecorationSkillData().observe(this, this::populateSkills);
        viewModel.getComponentData().observe(this, this::populateRecipes);
    }

    /**
     * Updates the UI to set the decoration data.
     * @param decoration
     */
    private void populateDecoration(Decoration decoration) {
        if (decoration == null) return;

        decorationId = decoration.getId();
        decorationName = decoration.getName();

        getActivity().setTitle(decorationName);

        String cellRare = decoration.getRarityString();
        String cellBuy = "" + decoration.getBuy() + "z";
        String cellSell = "" + decoration.getSell() + "z";

        if (cellBuy.equals("0z")) {
            cellBuy = "-";
        }
        if (cellSell.equals("0z")) {
            cellSell = "-";
        }

        binding.titlebar.setIcon(decoration);
        binding.titlebar.setTitleText(decorationName);

        binding.rare.setValueText(cellRare);
        binding.buy.setValueText(cellBuy);
        binding.sell.setValueText(cellSell);
        binding.slotsSection.slots.setHideExtras(true);
        binding.slotsSection.slots.setSlots(decoration.getNumSlots(), decoration.getNumSlots());
    }

    private void populateSkills(List<SkillPoints> skills) {
        binding.skillList.removeAllViews();

        for (SkillPoints skill : skills) {
            LabelTextRowCell skillItem = new LabelTextRowCell(getContext());
            skillItem.setLabelText(skill.getSkillName());
            skillItem.setValueText(String.valueOf(skill.getPoints()));
            skillItem.setOnClickListener(new SkillClickListener(getContext(), skill.getSkillId()));

            binding.skillList.addView(skillItem);
        }
    }

    private void populateRecipes(Map<String, List<Component>> recipes) {
        binding.recipeList.removeAllViews();

        for (List<Component> recipe : recipes.values()) {
            ItemRecipeCell cell = new ItemRecipeCell(getContext());

            // only show recipe title if there's more than one
            if (recipes.size() > 1) {
                cell.setTitleText(recipe.get(0).getType());
            }

            for (Component component : recipe) {
                Item item = component.getComponent();
                View itemCell = cell.addItem(item, item.getName(), component.getQuantity(), component.isKey());
                itemCell.setOnClickListener(new ItemClickListener(getContext(), item));
            }

            binding.recipeList.addView(cell);
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
                FragmentManager fm = this.getFragmentManager();
                WishlistDataAddDialogFragment dialogCopy = WishlistDataAddDialogFragment
                        .newInstance(decorationId, decorationName);
                dialogCopy.show(fm, DIALOG_WISHLIST_ADD);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
