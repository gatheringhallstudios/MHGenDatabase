package com.ghstudios.android.features.quests;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ghstudios.android.AppSettings;
import com.ghstudios.android.AssetLoader;
import com.ghstudios.android.components.ColumnLabelTextCell;
import com.ghstudios.android.components.TitleBarCell;
import com.ghstudios.android.data.classes.Location;
import com.ghstudios.android.data.classes.Quest;
import com.ghstudios.android.data.database.DataManager;
import com.ghstudios.android.mhgendatabase.R;
import com.ghstudios.android.features.locations.LocationDetailPagerActivity;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.ghstudios.android.mhgendatabase.R.id.location;

public class QuestDetailFragment extends Fragment {
    private static final String ARG_QUEST_ID = "QUEST_ID";
    
    private Quest mQuest;
    private View mView;

    @BindView(R.id.location_layout) LinearLayout mQuestLocationLayout;
    @BindView(R.id.location_image) ImageView questLocationImageView;

    @BindView(R.id.titlebar) TitleBarCell titleBarCell;
    @BindView(R.id.level) TextView levelTextView;
    @BindView(R.id.hub) TextView hubTextView;
    @BindView(R.id.reward) ColumnLabelTextCell rewardCell;
    @BindView(R.id.hrp) ColumnLabelTextCell hrpCell;
    @BindView(R.id.fee) ColumnLabelTextCell feeCell;

    @BindView(R.id.goal) TextView goalTextView;
    @BindView(R.id.location) TextView locationTextView;
    @BindView(R.id.subquest) TextView subquestTextView;
    @BindView(R.id.subhrp) TextView subquestHrpTextView;
    @BindView(R.id.subreward) TextView subRewardTextView;
    @BindView(R.id.description) TextView mFlavor;

    public static QuestDetailFragment newInstance(long questId) {
        Bundle args = new Bundle();
        args.putLong(ARG_QUEST_ID, questId);
        QuestDetailFragment f = new QuestDetailFragment();
        f.setArguments(args);
        return f;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        QuestDetailViewModel viewModel = ViewModelProviders.of(getActivity()).get(QuestDetailViewModel.class);

        viewModel.getQuest().observe(this, quest -> {
            mQuest = quest;
            updateUI();
        });

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_quest_detail, container, false);

        ButterKnife.bind(this, view);

        // Click listener for quest location
        mQuestLocationLayout.setOnClickListener(v -> {
            // The id argument will be the Monster ID; CursorAdapter gives us this
            // for free
            Intent i = new Intent(getActivity(), LocationDetailPagerActivity.class);
            long id = (long)v.getTag();
            if(id>100) id = id-100;
            i.putExtra(LocationDetailPagerActivity.EXTRA_LOCATION_ID, id);
            startActivity(i);
        });

        return view;
    }

    
    private void updateUI() {
        // Add list of monsters and habitats
        FragmentManager fm = getFragmentManager();
        fm.beginTransaction().add(
                R.id.monster_habitat_fragment,
                QuestMonsterFragment.newInstance(mQuest.getId())
        ).commitAllowingStateLoss();

        String cellGoal = mQuest.getGoal();
        String cellHrp = "" + mQuest.getHrp();
        String cellReward = "" + mQuest.getReward() + "z";
        String cellFee = "" + mQuest.getFee() + "z";
        String cellLocation = mQuest.getLocation().getName();
        String cellSubGoal = mQuest.getSubGoal();
        String cellSubHrp = "" + mQuest.getSubHrp();
        String cellSubReward = "" + mQuest.getSubReward() + "z";
        String flavor = mQuest.getFlavor();

        // bind title bar
        titleBarCell.setIconDrawable(AssetLoader.loadIconFor(mQuest));
        titleBarCell.setTitleText(mQuest.getName());

        // bind details section
        hubTextView.setText(AssetLoader.localizeHub(mQuest.getHub()));
        levelTextView.setText(mQuest.getStarString());
        hrpCell.setValueText(cellHrp);
        rewardCell.setValueText(cellReward);
        feeCell.setValueText(cellFee);

        goalTextView.setText(cellGoal);
        locationTextView.setText(cellLocation);
        locationTextView.setTag(mQuest.getLocation().getId());
        subquestTextView.setText(cellSubGoal);
        subquestHrpTextView.setText(cellSubHrp);
        subRewardTextView.setText(cellSubReward);
        mQuestLocationLayout.setTag(mQuest.getLocation().getId());
        mFlavor.setText(flavor);

        // Get Location based on ID and set image thumbnail
        AssetLoader.setIcon(questLocationImageView, mQuest.getLocation());
    }
}
