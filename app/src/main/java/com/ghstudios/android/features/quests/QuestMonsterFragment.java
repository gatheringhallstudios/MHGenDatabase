package com.ghstudios.android.features.quests;

import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ghstudios.android.data.classes.Habitat;
import com.ghstudios.android.data.classes.MonsterToQuest;
import com.ghstudios.android.data.cursors.MonsterToQuestCursor;
import com.ghstudios.android.loader.MonsterToQuestListCursorLoader;
import com.ghstudios.android.mhgendatabase.R;
import com.ghstudios.android.ClickListeners.MonsterClickListener;

import java.io.IOException;
import java.util.List;

public class QuestMonsterFragment extends Fragment {
	private static final String ARG_QUEST_ID = "QUEST_ID";

	public static QuestMonsterFragment newInstance(long questId) {
		Bundle args = new Bundle();
		args.putLong(ARG_QUEST_ID, questId);
		QuestMonsterFragment f = new QuestMonsterFragment();
		f.setArguments(args);
		return f;
	}

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        QuestDetailViewModel viewModel = ViewModelProviders.of(getActivity()).get(QuestDetailViewModel.class);
        viewModel.getMonsters().observe(this, this::buildView);
    }


    private void buildView(List<MonsterToQuest> monsters){
        MonsterToQuestListAdapter adapter = new MonsterToQuestListAdapter(
                getActivity(), monsters);

        LinearLayout monsterLayout = getActivity().findViewById(R.id.monster_habitat_fragment);

        //If this has already been called, no need to do it again.
        if(monsterLayout.getChildCount() > 0) return;

        // Use adapter to manually populate a LinearLayout
        for(int i=0;i<adapter.getCount();i++) {
            LinearLayout v = (LinearLayout) adapter.getView(i, null, monsterLayout);
            monsterLayout.addView(v);
        }
    }


	private static class MonsterToQuestListAdapter extends ArrayAdapter<MonsterToQuest> {

		public MonsterToQuestListAdapter(Context context,
				List<MonsterToQuest> items) {
			super(context,0,items);
		}

        @NonNull
        @Override
        public View getView(int position, @Nullable View view, @NonNull ViewGroup parent) {
            if(view == null){
                LayoutInflater inflater = LayoutInflater.from(getContext());
                view = inflater.inflate(R.layout.fragment_quest_monstertoquest, parent, false);
            }
            MonsterToQuest monsterToQuest = getItem(position);

            // Set up the text view
            LinearLayout itemLayout = view.findViewById(R.id.listitem);
            LinearLayout habitatLayout = view.findViewById(R.id.habitat_layout);
            ImageView monsterImageView = view.findViewById(R.id.detail_monster_image);
            TextView monsterTextView = view.findViewById(R.id.detail_monster_label);
            TextView unstableTextView = view.findViewById(R.id.detail_monster_unstable);
            TextView startTextView = view.findViewById(R.id.habitat_start);
            TextView travelTextView = view.findViewById(R.id.habitat_travel);
            TextView endTextView = view.findViewById(R.id.habitat_end);

            String cellMonsterText = monsterToQuest.getMonster().getName();
            String cellUnstableText = monsterToQuest.getUnstable()==1?"Unstable":"";

            monsterTextView.setText(cellMonsterText);
            unstableTextView.setText(cellUnstableText);

            Drawable i = null;
            String cellImage = "icons_monster/"
                    + monsterToQuest.getMonster().getFileLocation();
            try {
                i = Drawable.createFromStream(
                        getContext().getAssets().open(cellImage), null);
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
            itemLayout.setOnClickListener(new MonsterClickListener(getContext(),
                    monsterToQuest.getMonster().getId()));
            return view;
        }

	}

}
