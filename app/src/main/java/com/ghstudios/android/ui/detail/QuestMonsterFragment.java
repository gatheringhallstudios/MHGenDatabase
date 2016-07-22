package com.ghstudios.android.ui.detail;

import java.io.IOException;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.ghstudios.android.data.classes.Habitat;
import com.ghstudios.android.data.classes.MonsterToQuest;
import com.ghstudios.android.data.database.MonsterToQuestCursor;
import com.ghstudios.android.loader.MonsterToQuestListCursorLoader;
import com.ghstudios.android.mhgendatabase.R;
import com.ghstudios.android.ui.ClickListeners.MonsterClickListener;

public class QuestMonsterFragment extends ListFragment implements
		LoaderCallbacks<Cursor> {
	private static final String ARG_QUEST_ID = "QUEST_ID";

	public static QuestMonsterFragment newInstance(long questId) {
		Bundle args = new Bundle();
		args.putLong(ARG_QUEST_ID, questId);
		QuestMonsterFragment f = new QuestMonsterFragment();
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

		// Initialize the loader to load the list of runs
		getLoaderManager().initLoader(R.id.quest_monster_fragment, getArguments(), this);
	}

	@SuppressLint("NewApi")
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		// You only ever load the runs, so assume this is the case
		long questId = args.getLong(ARG_QUEST_ID, -1);

		return new MonsterToQuestListCursorLoader(getActivity(), 
				MonsterToQuestListCursorLoader.FROM_QUEST,
				questId);
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

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		// The id argument will be the Monster ID; CursorAdapter gives us this
		// for free
		Intent i = new Intent(getActivity(), MonsterDetailActivity.class);
		i.putExtra(MonsterDetailActivity.EXTRA_MONSTER_ID, (long) v.getTag());
		startActivity(i);
	}

	private static class MonsterToQuestListCursorAdapter extends CursorAdapter {

		private MonsterToQuestCursor mMonsterToQuestCursor;

		public MonsterToQuestListCursorAdapter(Context context,
				MonsterToQuestCursor cursor) {
			super(context, cursor, 0);
			mMonsterToQuestCursor = cursor;
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			// Use a layout inflater to get a row view
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			return inflater.inflate(R.layout.fragment_quest_monstertoquest,
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
			LinearLayout habitatLayout = (LinearLayout)view.findViewById(R.id.habitat_layout);
			ImageView monsterImageView = (ImageView) view
					.findViewById(R.id.detail_monster_image);
			TextView monsterTextView = (TextView) view
					.findViewById(R.id.detail_monster_label);
			TextView unstableTextView = (TextView) view
					.findViewById(R.id.detail_monster_unstable);
			TextView startTextView = (TextView)view.findViewById(R.id.habitat_start);
			TextView travelTextView = (TextView)view.findViewById(R.id.habitat_travel);
			TextView endTextView = (TextView)view.findViewById(R.id.habitat_end);
			
			String cellMonsterText = monsterToQuest.getMonster().getName();
			String cellTraitText = monsterToQuest.getMonster().getTrait(); 
			String cellUnstableText = monsterToQuest.getUnstable()==1?"Unstable":"";
			
			if (!cellTraitText.equals("")) {
				cellMonsterText = cellMonsterText + " (" + cellTraitText + ")";
			}
			
			monsterTextView.setText(cellMonsterText);
			unstableTextView.setText(cellUnstableText);

			Drawable i = null;
			String cellImage = "icons_monster/"
					+ monsterToQuest.getMonster().getFileLocation();
			try {
				i = Drawable.createFromStream(
						context.getAssets().open(cellImage), null);
			} catch (IOException e) {
				e.printStackTrace();
			}

			monsterImageView.setImageDrawable(i);

			Habitat habitat = monsterToQuest.getHabitat();
			if(habitat != null){
				long start = habitat.getStart();
				long[] area = habitat.getAreas();
				long rest = habitat.getRest();

				String areas = "";
				for(int j = 0; j < area.length; j++)
				{
					areas += Long.toString(area[j]);
					if (j != area.length - 1)
					{
						areas += ", ";
					}
				}

				startTextView.setText(Long.toString(start));
				travelTextView.setText(areas);
				endTextView.setText(Long.toString(rest));
				habitatLayout.setVisibility(View.VISIBLE);
			}
			else
				habitatLayout.setVisibility(View.GONE);

			itemLayout.setTag(monsterToQuest.getMonster().getId());
            itemLayout.setOnClickListener(new MonsterClickListener(context,
                    monsterToQuest.getMonster().getId()));
		}
	}

}
