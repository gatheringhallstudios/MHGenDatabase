package com.ghstudios.android.features.quests;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ghstudios.android.AssetLoader;
import com.ghstudios.android.ClickListeners.MonsterClickListener;
import com.ghstudios.android.data.classes.Habitat;
import com.ghstudios.android.data.classes.MonsterToQuest;
import com.ghstudios.android.mhgendatabase.R;

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
            TextView hyperTextView = view.findViewById(R.id.detail_monster_hyper);

            String cellMonsterText = monsterToQuest.getMonster().getName();

            if (monsterToQuest.isUnstable()) {
                unstableTextView.setVisibility(View.VISIBLE);
                unstableTextView.setText(R.string.unstable);
            } else unstableTextView.setVisibility(View.GONE);

            if (monsterToQuest.isHyper()) {
                hyperTextView.setVisibility(View.VISIBLE);
                hyperTextView.setText(R.string.hyper);
            } else hyperTextView.setVisibility(View.GONE);

            monsterTextView.setText(cellMonsterText);

            AssetLoader.setIcon(monsterImageView,monsterToQuest.getMonster());

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
