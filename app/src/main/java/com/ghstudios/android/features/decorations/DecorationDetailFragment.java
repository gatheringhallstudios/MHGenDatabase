package com.ghstudios.android.features.decorations;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ghstudios.android.ClickListeners.ItemClickListener;
import com.ghstudios.android.ClickListeners.SkillClickListener;
import com.ghstudios.android.MHUtils;
import com.ghstudios.android.components.ColumnLabelTextCell;
import com.ghstudios.android.components.IconLabelTextCell;
import com.ghstudios.android.components.ItemRecipeCell;
import com.ghstudios.android.data.classes.Component;
import com.ghstudios.android.data.classes.Decoration;
import com.ghstudios.android.data.classes.Item;
import com.ghstudios.android.mhgendatabase.R;

import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DecorationDetailFragment extends Fragment {
    private static final String ARG_DECORATION_ID = "DECORATION_ID";

    @BindView(R.id.detail_decoration_label) TextView mDecorationLabelTextView;
    @BindView(R.id.detail_decoration_image) ImageView mDecorationIconImageView;
    @BindView(R.id.rare) ColumnLabelTextCell rareView;
    @BindView(R.id.buy) ColumnLabelTextCell buyView;
    @BindView(R.id.sell) ColumnLabelTextCell sellView;
    @BindView(R.id.slots) ColumnLabelTextCell slotsReqView;
    @BindView(R.id.skill_list) LinearLayout skillListView;
    @BindView(R.id.recipe_list) LinearLayout recipeListView;

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

        // Check for a Item ID as an argument, and find the item
        Bundle args = getArguments();
        if (args == null) {
            return;
        }

        long decorationId = args.getLong(ARG_DECORATION_ID, -1);
        if (decorationId == -1) {
            return;
        }

        DecorationViewModel viewModel = ViewModelProviders.of(this).get(DecorationViewModel.class);
        viewModel.setDecoration(decorationId);

        viewModel.getDecorationData().observe(this, this::populateDecoration);
        viewModel.getComponentData().observe(this, this::populateRecipes);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_decoration_detail,
                container, false);

        ButterKnife.bind(this, view);

        return view;
    }

    /**
     * Updates the UI to set the decoration data.
     * @param decoration
     */
    private void populateDecoration(Decoration decoration) {
        getActivity().setTitle(decoration.getName());

        String cellText = decoration.getName();
        String cellImage = "icons_items/" + decoration.getFileLocation();
        String cellRare = "" + decoration.getRarity();
        String cellBuy = "" + decoration.getBuy() + "z";
        String cellSell = "" + decoration.getSell() + "z";
        String cellSlotsReq = "" + decoration.getSlotsString();

        if (cellBuy.equals("0z")) {
            cellBuy = "-";
        }
        if (cellSell.equals("0z")) {
            cellSell = "-";
        }

        mDecorationLabelTextView.setText(cellText);
        rareView.setValueText(cellRare);
        buyView.setValueText(cellBuy);
        sellView.setValueText(cellSell);
        slotsReqView.setValueText(cellSlotsReq);

        Drawable image = MHUtils.loadAssetDrawable(getContext(), cellImage);
        mDecorationIconImageView.setImageDrawable(image);

        skillListView.removeAllViews();

        addSkillListItem(decoration.getSkill1Id(), decoration.getSkill1Name(), decoration.getSkill1Point());
        if (decoration.getSkill2Point() != 0) {
            addSkillListItem(decoration.getSkill2Id(), decoration.getSkill2Name(), decoration.getSkill2Point());
        }
    }

    private void populateRecipes(Map<String, List<Component>> recipes) {
        recipeListView.removeAllViews();

        for (List<Component> recipe : recipes.values()) {
            ItemRecipeCell cell = new ItemRecipeCell(getContext());
            cell.setTitleText(recipe.get(0).getType());

            for (Component component : recipe) {
                Item item = component.getComponent();
                Drawable itemIcon = MHUtils.loadAssetDrawable(getContext(), item.getItemImage());

                View itemCell = cell.addItem(itemIcon, item.getName(), component.getQuantity());
                itemCell.setOnClickListener(new ItemClickListener(getContext(), item));
            }

            recipeListView.addView(cell);
        }
    }

    private void addSkillListItem(long skillId, String skillName, int points) {
        IconLabelTextCell skillItem = new IconLabelTextCell(getContext());
        skillItem.setLabelText(skillName);
        skillItem.setValueText(String.valueOf(points));
        skillItem.setOnClickListener(new SkillClickListener(getContext(), skillId));

        skillListView.addView(skillItem);
    }
}
