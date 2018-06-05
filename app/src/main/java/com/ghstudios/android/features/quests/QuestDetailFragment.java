package com.ghstudios.android.features.quests;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ghstudios.android.AppSettings;
import com.ghstudios.android.components.ColumnLabelTextCell;
import com.ghstudios.android.components.TitleBarCell;
import com.ghstudios.android.data.classes.Location;
import com.ghstudios.android.data.classes.Quest;
import com.ghstudios.android.data.database.DataManager;
import com.ghstudios.android.loader.QuestLoader;
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
    private LinearLayout mQuestLocationLayout;

    @BindView(R.id.titlebar) TitleBarCell titleBarCell;

    @BindView(R.id.reward) ColumnLabelTextCell rewardCell;
    @BindView(R.id.hrp) ColumnLabelTextCell hrpCell;
    @BindView(R.id.fee) ColumnLabelTextCell feeCell;

	TextView questtv1;
	TextView questtv2;
	TextView questtv7;
    TextView questtv8;
    TextView questtv9;
    TextView questtv10;
	TextView mFlavor;

	public static QuestDetailFragment newInstance(long questId) {
		Bundle args = new Bundle();
		args.putLong(ARG_QUEST_ID, questId);
		QuestDetailFragment f = new QuestDetailFragment();
		f.setArguments(args);
		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		// Check for a Quest ID as an argument, and find the monster
		Bundle args = getArguments();
		if (args != null) {
			long questId = args.getLong(ARG_QUEST_ID, -1);
			if (questId != -1) {
				LoaderManager lm = getLoaderManager();
				lm.initLoader(R.id.quest_detail_fragment, args, new QuestLoaderCallbacks());
			}
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_quest_detail, container, false);

        // Get member reference
        mView = view;

        ButterKnife.bind(this, view);
		
		questtv1 = (TextView) view.findViewById(R.id.level);
		questtv2 = (TextView) view.findViewById(R.id.goal);
		questtv7 = (TextView) view.findViewById(location);
        questtv8 = (TextView) view.findViewById(R.id.subquest);
        questtv9 = (TextView) view.findViewById(R.id.subhrp);
        questtv10 = (TextView) view.findViewById(R.id.subreward);
        mQuestLocationLayout = (LinearLayout) mView.findViewById(R.id.location_layout);
		mFlavor = (TextView) view.findViewById(R.id.description);

        // Click listener for quest location
        mQuestLocationLayout.setOnClickListener(new View.OnClickListener() {
		    @Override
		    public void onClick(View v) {
				// The id argument will be the Monster ID; CursorAdapter gives us this
				// for free
				Intent i = new Intent(getActivity(), LocationDetailPagerActivity.class);
				long id = (long)v.getTag();
				if(id>100) id = id-100;
				i.putExtra(LocationDetailPagerActivity.EXTRA_LOCATION_ID, id);
				startActivity(i);
		    }
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

		String cellQuest = mQuest.getName();
		String cellLevels = mQuest.getHub() + " " + mQuest.getStars();
		String cellGoal = mQuest.getGoal();
		String cellHrp = "" + mQuest.getHrp();
		String cellReward = "" + mQuest.getReward() + "z";
		String cellFee = "" + mQuest.getFee() + "z";
		//String time = mQuest.getLocationTime().equals("") ? "" : " (" + mQuest.getLocationTime() + ")";
		String cellLocation = mQuest.getLocation().getName();
        String cellSubGoal = mQuest.getSubGoal();
        String cellSubHrp = "" + mQuest.getSubHrp();
        String cellSubReward = "" + mQuest.getSubReward() + "z";
		String flavor = mQuest.getFlavor();

		titleBarCell.setIconResource(getIconForQuest(mQuest));
		titleBarCell.setTitleText(mQuest.getName());
		titleBarCell.setAltTitleText(mQuest.getJpnName());
		titleBarCell.setAltTitleEnabled(AppSettings.isJapaneseEnabled());

		hrpCell.setValueText(cellHrp);
		rewardCell.setValueText(cellReward);
		feeCell.setValueText(cellFee);
		
		questtv1.setText(cellLevels);
		questtv2.setText(cellGoal);
		questtv7.setText(cellLocation);
		questtv7.setTag(mQuest.getLocation().getId());
        questtv8.setText(cellSubGoal);
        questtv9.setText(cellSubHrp);
        questtv10.setText(cellSubReward);
        mQuestLocationLayout.setTag(mQuest.getLocation().getId());
		mFlavor.setText(flavor);

        ImageView questLocationImageView = (ImageView) mView.findViewById(R.id.location_image);

        // Get Location based on ID and set image thumbnail
        DataManager dm = DataManager.get(getContext());
        Location loc = dm.getLocation(mQuest.getLocation().getId());
        String cellImage = "icons_location/" + loc.getFileLocationMini();

        questLocationImageView.setTag(mQuest.getLocation().getId());
        new LoadImage(questLocationImageView, cellImage, getContext()).execute();
		
	}

	/**
	 * TODO: Needs to be defined in a better way that avoids repetition,
	 * but this is the easiest way for now.
	 * @param quest
	 * @return
	 */
	private int getIconForQuest(Quest quest) {
		if (quest.getHunterType() == 1) {
			return R.drawable.quest_cat;
		}

		switch (quest.getGoalType()) {
			case Quest.QUEST_GOAL_DELIVER:
				return R.drawable.quest_icon_green;
			case Quest.QUEST_GOAL_CAPTURE:
				return R.drawable.quest_icon_grey;
			default:
				return R.drawable.quest_icon_red;
		}
	}
	
	private class QuestLoaderCallbacks implements LoaderCallbacks<Quest> {
		
		@Override
		public Loader<Quest> onCreateLoader(int id, Bundle args) {
			return new QuestLoader(getActivity(), args.getLong(ARG_QUEST_ID));
		}
		
		@Override
		public void onLoadFinished(Loader<Quest> loader, Quest run) {
			mQuest = run;
			updateUI();
		}
		
		@Override
		public void onLoaderReset(Loader<Quest> loader) {
			// Do nothing
		}
	}

    protected class LoadImage extends AsyncTask<Void,Void,Drawable> {
        private ImageView mImage;
        private String path;
        private String imagePath;
        private Context context;

        public LoadImage(ImageView imv, String imagePath, Context c) {
            this.mImage = imv;
            this.path = imv.getTag().toString();
            this.imagePath = imagePath;
            this.context = c;
        }

        @Override
        protected Drawable doInBackground(Void... arg0) {
            Drawable d = null;

            try {
                d = Drawable.createFromStream(context.getAssets().open(imagePath),
                        null);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return d;
        }

        protected void onPostExecute(Drawable result) {
            if (mImage.getTag().toString().equals(path)) {
                mImage.setImageDrawable(result);
            }
        }
    }
}
