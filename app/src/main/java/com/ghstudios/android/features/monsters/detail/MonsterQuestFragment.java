package com.ghstudios.android.features.monsters.detail;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.monxalo.android.widget.SectionCursorAdapter;
import com.ghstudios.android.data.classes.MonsterToQuest;
import com.ghstudios.android.data.database.MonsterToQuestCursor;
import com.ghstudios.android.data.database.S;
import com.ghstudios.android.loader.MonsterToQuestListCursorLoader;
import com.ghstudios.android.mhgendatabase.R;
import com.ghstudios.android.ui.ClickListeners.QuestClickListener;

public class MonsterQuestFragment extends ListFragment implements
		LoaderCallbacks<Cursor> {
	private static final String ARG_MONSTER_ID = "MONSTER_ID";

	public static MonsterQuestFragment newInstance(long questId) {
		Bundle args = new Bundle();
		args.putLong(ARG_MONSTER_ID, questId);
		MonsterQuestFragment f = new MonsterQuestFragment();
		f.setArguments(args);
		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Initialize the loader to load the list of runs
		getLoaderManager().initLoader(R.id.monster_quest_fragment, getArguments(), this);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_generic_list, null);
		return v;
	}

	@SuppressLint("NewApi")
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		// You only ever load the runs, so assume this is the case
		long monsterId = args.getLong(ARG_MONSTER_ID, -1);

		return new MonsterToQuestListCursorLoader(getActivity(), 
				MonsterToQuestListCursorLoader.FROM_MONSTER,
				monsterId);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		// Create an adapter to point at this cursor

		MonsterToQuestListCursorAdapter adapter = new MonsterToQuestListCursorAdapter(
				getActivity(), (MonsterToQuestCursor) cursor);
		setListAdapter(adapter);

	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		// Stop using the cursor (via the adapter)
		setListAdapter(null);
	}

	private static class MonsterToQuestListCursorAdapter extends SectionCursorAdapter {

		private MonsterToQuestCursor mMonsterToQuestCursor;

		public MonsterToQuestListCursorAdapter(Context context,
				MonsterToQuestCursor cursor) {
			super(context, cursor, R.layout.listview_generic_header, S.COLUMN_QUESTS_HUB);
			mMonsterToQuestCursor = cursor;
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			// Use a layout inflater to get a row view
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			return inflater.inflate(R.layout.fragment_monster_monstertoquest_listitem,
					parent, false);
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			// Get the item for the current row
			MonsterToQuest monsterToQuest = mMonsterToQuestCursor
					.getMonsterToQuest();

			// Set up the text view
			LinearLayout itemLayout = (LinearLayout) view
					.findViewById(R.id.listitem);
			TextView questTextView = (TextView) view
					.findViewById(R.id.quest_name);
			TextView starsTextView = (TextView) view
					.findViewById(R.id.quest_stars);
			TextView unstableTextView = (TextView) view
					.findViewById(R.id.quest_unstable);

			String cellQuestText = monsterToQuest.getQuest().getName();
			String starsText = monsterToQuest.getQuest().getStars();
			String cellUnstableText = (monsterToQuest.getUnstable()==1?"Unstable":"");

			unstableTextView.setText(cellUnstableText);
			
			questTextView.setText(cellQuestText);
			starsTextView.setText(starsText);


			itemLayout.setTag(monsterToQuest.getQuest().getId());
            itemLayout.setOnClickListener(new QuestClickListener(context,monsterToQuest
                    .getQuest().getId()));
		}
	}

}
