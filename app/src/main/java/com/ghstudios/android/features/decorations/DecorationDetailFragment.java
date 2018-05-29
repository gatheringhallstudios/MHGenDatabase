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
import android.widget.TextView;

import com.ghstudios.android.MHUtils;
import com.ghstudios.android.data.classes.Decoration;
import com.ghstudios.android.loader.DecorationLoader;
import com.ghstudios.android.mhgendatabase.R;
import com.ghstudios.android.features.armorsetbuilder.ASBPagerActivity;

public class DecorationDetailFragment extends Fragment {
    private static final String ARG_DECORATION_ID = "DECORATION_ID";

    private Decoration mDecoration;

    private TextView mDecorationLabelTextView;
    private ImageView mDecorationIconImageView;
    private TextView rareTextView;
    private TextView maxTextView;
    private TextView buyTextView;
    private TextView sellTextView;
    private TextView slotsReqTextView;

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

        mDecorationLabelTextView = view.findViewById(R.id.detail_decoration_label);
        mDecorationIconImageView = view.findViewById(R.id.detail_decoration_image);

        rareTextView = view.findViewById(R.id.rare);
        maxTextView = view.findViewById(R.id.max);
        sellTextView = view.findViewById(R.id.sell);
        buyTextView = view.findViewById(R.id.buy);
        slotsReqTextView = view.findViewById(R.id.slots_req);

        // If the originator of this fragment's activity was the Armor Set Builder...
        if (getActivity().getIntent().getBooleanExtra(ASBPagerActivity.EXTRA_FROM_SET_BUILDER, false)) {
            Button selectButton = view.findViewById(R.id.select_button);
            selectButton.setVisibility(View.VISIBLE);
            selectButton.setOnClickListener(v -> {
                Intent intent = getActivity().getIntent();
                intent.putExtra(DecorationDetailPagerActivity.EXTRA_DECORATION_ID, getArguments().getLong(ARG_DECORATION_ID)); // We put the armor's ID number as an extra of the intent.
                getActivity().setResult(Activity.RESULT_OK, intent);
                getActivity().finish();
            });
        }

        return view;
    }

    private void updateUI() {
        String cellText = mDecoration.getName();
        String cellImage = "icons_items/" + mDecoration.getFileLocation();
        String cellRare = "" + mDecoration.getRarity();
        String cellMax = "" + mDecoration.getCarryCapacity();
        String cellBuy = "" + mDecoration.getBuy() + "z";
        String cellSell = "" + mDecoration.getSell() + "z";
        String cellSlotsReq = "" + mDecoration.getSlotsString();

        if (cellBuy.equals("0z")) {
            cellBuy = "-";
        }
        if (cellSell.equals("0z")) {
            cellSell = "-";
        }

        mDecorationLabelTextView.setText(cellText);
        rareTextView.setText(cellRare);
        maxTextView.setText(cellMax);
        buyTextView.setText(cellBuy);
        sellTextView.setText(cellSell);
        slotsReqTextView.setText(cellSlotsReq);

        Drawable image = MHUtils.loadAssetDrawable(getContext(), cellImage);
        mDecorationIconImageView.setImageDrawable(image);
    }

    private class DecorationLoaderCallbacks implements
            LoaderCallbacks<Decoration> {

        @Override
        public Loader<Decoration> onCreateLoader(int id, Bundle args) {
            return new DecorationLoader(getActivity(),
                    args.getLong(ARG_DECORATION_ID));
        }

        @Override
        public void onLoadFinished(Loader<Decoration> loader, Decoration run) {
            mDecoration = run;
            updateUI();
        }

        @Override
        public void onLoaderReset(Loader<Decoration> loader) {
            // Do nothing
        }
    }
}
