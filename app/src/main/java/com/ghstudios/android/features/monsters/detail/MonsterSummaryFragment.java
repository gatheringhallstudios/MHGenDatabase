package com.ghstudios.android.features.monsters.detail;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ghstudios.android.AppSettings;
import com.ghstudios.android.MHUtils;
import com.ghstudios.android.components.SectionHeaderCell;
import com.ghstudios.android.components.TitleBarCell;
import com.ghstudios.android.data.classes.MonsterAilment;
import com.ghstudios.android.data.classes.MonsterWeakness;
import com.ghstudios.android.data.cursors.MonsterAilmentCursor;
import com.ghstudios.android.features.monsters.MonsterDetailViewModel;
import com.ghstudios.android.mhgendatabase.R;

import org.apmem.tools.layouts.FlowLayout;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MonsterSummaryFragment extends Fragment {
    private static final String ARG_MONSTER_ID = "MONSTER_ID";

    @BindView(R.id.monster_header)
    TitleBarCell headerView;

    @BindView(R.id.monster_state_list)
    LinearLayout statesListView;

    @BindView(R.id.ailments_data)
    LinearLayout mAilments;

    // Need to add dividers
    //private View mDividerView;

    public static MonsterSummaryFragment newInstance(long monsterId) {
        Bundle args = new Bundle();
        args.putLong(ARG_MONSTER_ID, monsterId);
        MonsterSummaryFragment f = new MonsterSummaryFragment();
        f.setArguments(args);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_monster_summary, container, false);

        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        MonsterDetailViewModel viewModel = ViewModelProviders.of(getActivity()).get(MonsterDetailViewModel.class);

        viewModel.getMonsterData().observe(this, (monster) -> {
            String cellImage = "icons_monster/" + monster.getFileLocation();
            Drawable monsterImage = MHUtils.loadAssetDrawable(getContext(), cellImage);

            headerView.setIconDrawable(monsterImage);
            headerView.setTitleText(monster.getName());
            headerView.setAltTitleText(monster.getJpnName());
            headerView.setAltTitleEnabled(AppSettings.isJapaneseEnabled());
        });

        viewModel.getMonsterWeaknessData().observe(this, this::updateWeaknesses);

        viewModel.getAilments().observe(this, (ailmentCursor) -> {
            MonsterSummaryFragment.MonsterAilmentsCursorAdapter adapter
                    = new MonsterSummaryFragment.MonsterAilmentsCursorAdapter(getActivity(), ailmentCursor);

            // mAilmentsListView.setAdapter(adapter);
            // Assign list items to LinearLayout instead of ListView

            // mAilmentsLinearLayout should be the vertical LinearLayout that you substituted the listview with
            for (int i = 0; i < adapter.getCount(); i++) {
                View v = adapter.getView(i, null, mAilments);
                mAilments.addView(v);
            }
        });
    }

    // Update weakness display after loader callback
    private void updateWeaknesses(List<MonsterWeakness> weaknesses) {
        if (weaknesses == null || weaknesses.size() == 0) return;

        statesListView.removeAllViews();

        for (MonsterWeakness weakness : weaknesses) {
            addWeakness(weakness);
        }
    }

    private void addWeakness(MonsterWeakness mWeakness) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View weaknessView = inflater.inflate(R.layout.fragment_monster_summary_state, statesListView, false);

        // Set title
        SectionHeaderCell header = weaknessView.findViewById(R.id.state_name);
        header.setLabelText(mWeakness.getState());

        FlowLayout mWeaknessData = weaknessView.findViewById(R.id.weakness_data);
        FlowLayout mTrapData = weaknessView.findViewById(R.id.trap_data);
        FlowLayout mBombData = weaknessView.findViewById(R.id.bomb_data);

        int AmountShown = 0;
        //Loop through tier until an element is found. (All elements at that tier will be shown)
        for (int i = 7; i > 0 && AmountShown < 2; i--) {
            if (mWeakness.getFire() == i) {
                AmountShown++;
                evalWeakness(mWeakness.getFire(), mWeaknessData, getResources().getString(R.string.image_location_fire));
            }
            if (mWeakness.getWater() == i) {
                AmountShown++;
                evalWeakness(mWeakness.getWater(), mWeaknessData, getResources().getString(R.string.image_location_water));
            }
            if (mWeakness.getThunder() == i) {
                AmountShown++;
                evalWeakness(mWeakness.getThunder(), mWeaknessData, getResources().getString(R.string.image_location_thunder));
            }
            if (mWeakness.getIce() == i) {
                AmountShown++;
                evalWeakness(mWeakness.getIce(), mWeaknessData, getResources().getString(R.string.image_location_ice));
            }
            if (mWeakness.getDragon() == i) {
                AmountShown++;
                evalWeakness(mWeakness.getDragon(), mWeaknessData, getResources().getString(R.string.image_location_dragon));
            }
        }

        boolean shown = false;
        //Loop through tier until an element is found. (All elements at that tier will be shown)
        for (int i = 7; i > 0 && !shown; i--) {
            if (mWeakness.getPoison() == i) {
                shown = true;
                evalWeakness(mWeakness.getPoison(), mWeaknessData, getResources().getString(R.string.image_location_poison));
            }
            if (mWeakness.getParalysis() == i) {
                shown = true;
                evalWeakness(mWeakness.getParalysis(), mWeaknessData, getResources().getString(R.string.image_location_paralysis));
            }
            if (mWeakness.getSleep() == i) {
                shown = true;
                evalWeakness(mWeakness.getSleep(), mWeaknessData, getResources().getString(R.string.image_location_sleep));
            }
        }

        // Pitfall Trap
        if (mWeakness.getPitfalltrap() != 0)
            addIcon(mTrapData, getResources().getString(R.string.image_location_pitfall_trap), null);
        // Shock Trap
        if (mWeakness.getShocktrap() != 0)
            addIcon(mTrapData, getResources().getString(R.string.image_location_shock_trap), null);
        // Meat
        if (mWeakness.getMeat() != 0)
            addIcon(mTrapData, getResources().getString(R.string.image_location_meat), null);

        // Flash Bomb
        if (mWeakness.getFlashbomb() != 0)
            addIcon(mBombData, getResources().getString(R.string.image_location_flash_bomb), null);
        // Sonic Bomb
        if (mWeakness.getSonicbomb() != 0)
            addIcon(mBombData, getResources().getString(R.string.image_location_sonic_bomb), null);
        // Dung Bomb
        if (mWeakness.getDungbomb() != 0)
            addIcon(mBombData, getResources().getString(R.string.image_location_dung_bomb), null);


        statesListView.addView(weaknessView);
    }

    private void evalWeakness(int weaknessvalue, FlowLayout parentview, String imagelocation) {
        // Add icon and modifier to show effectiveness
        switch (weaknessvalue) {
            case 4:
                addIcon(parentview, imagelocation, null);
                break;
            case 5:
                addIcon(parentview, imagelocation, getResources().getString(R.string.image_location_effectiveness_2));
                break;
            case 6:
            case 7:
                addIcon(parentview, imagelocation, getResources().getString(R.string.image_location_effectiveness_3));
                break;
            default:
                // Do nothing
                break;
        }
    }

    // Add small_icon to a particular LinearLayout
    private void addIcon(FlowLayout parentview, String imagelocation, String imagemodlocation) {
        LayoutInflater inflater = LayoutInflater.from(getContext());

        ImageView mImage; // Generic image holder
        ImageView mImageMod; // Modifier image holder
        View view; // Generic icon view holder

        // Create new small_icon layout
        view = inflater.inflate(R.layout.small_icon, parentview, false);

        // Get reference to image in small_icon layout
        mImage = (ImageView) view.findViewById(R.id.image);
        mImageMod = (ImageView) view.findViewById(R.id.image_mod);

        // Open Image
        String cellImage = imagelocation;
        AssetManager manager = getActivity().getAssets();
        try {
            InputStream open = manager.open(cellImage);
            Bitmap bitmap = BitmapFactory.decodeStream(open);
            // Assign the bitmap to an ImageView in this layout
            mImage.setImageBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Open Image Mod if applicable
        if (imagemodlocation != null) {
            cellImage = imagemodlocation;
            manager = getActivity().getAssets();
            try {
                InputStream open = manager.open(cellImage);
                Bitmap bitmap = BitmapFactory.decodeStream(open);
                // Assign the bitmap to an ImageView in this layout
                mImageMod.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
            mImageMod.setVisibility(View.VISIBLE);
        }

        // Add small_icon to appropriate layout
        parentview.addView(view);
    }

    // Adapter to populate the Ailments Listview
    private class MonsterAilmentsCursorAdapter extends CursorAdapter {

        private MonsterAilmentCursor mMonsterAilmentsCursor;

        public MonsterAilmentsCursorAdapter(Context context,
                                            MonsterAilmentCursor cursor) {
            super(context, cursor, 0);
            mMonsterAilmentsCursor = cursor;
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            // Use a layout inflater to get a row view
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            return inflater.inflate(R.layout.fragment_ailment_listitem,
                    parent, false);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            // Get the Ailment for the current row
            MonsterAilment mMonsterAilment = mMonsterAilmentsCursor.getAilment();

            // Locate textview
            TextView mAilment = (TextView) view.findViewById(R.id.ailment_text);

            // Set ailment text
            mAilment.setText(mMonsterAilment.getAilment());
        }
    }
}
