package com.ghstudios.android.features.decorations;

import android.app.Activity;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ghstudios.android.ClickListeners.SkillClickListener;
import com.ghstudios.android.MHUtils;
import com.ghstudios.android.components.ColumnLabelTextCell;
import com.ghstudios.android.components.IconLabelTextCell;
import com.ghstudios.android.data.classes.Decoration;
import com.ghstudios.android.loader.DecorationLoader;
import com.ghstudios.android.mhgendatabase.R;

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

        setRetainInstance(true);

        // Check for a Item ID as an argument, and find the item
        Bundle args = getArguments();
        if (args == null) {
            return;
        }

        long decorationId = args.getLong(ARG_DECORATION_ID, -1);
        if (decorationId == -1) {
            return;
        }

        LoaderManager lm = getLoaderManager();
        lm.initLoader(R.id.decoration_detail_fragment, args,
                new DecorationLoaderCallbacks());
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

    private void addSkillListItem(long skillId, String skillName, int points) {
        IconLabelTextCell skillItem = new IconLabelTextCell(getContext());
        skillItem.setLabelText(skillName);
        skillItem.setValueText(String.valueOf(points));
        skillItem.setOnClickListener(new SkillClickListener(getContext(), skillId));

        skillListView.addView(skillItem);
    }

    private class DecorationLoaderCallbacks implements
            LoaderCallbacks<Decoration> {

        @Override
        public Loader<Decoration> onCreateLoader(int id, Bundle args) {
            long decorationId = args.getLong(ARG_DECORATION_ID);
            return new DecorationLoader(getActivity(), decorationId);
        }

        @Override
        public void onLoadFinished(Loader<Decoration> loader, Decoration deco) {
            populateDecoration(deco);
        }

        @Override
        public void onLoaderReset(Loader<Decoration> loader) {
            // Do nothing
        }
    }
}
