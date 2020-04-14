package com.ghstudios.android.features.quests;

import androidx.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ghstudios.android.AssetLoader;
import com.ghstudios.android.ClickListeners.BasicItemClickListener;
import com.ghstudios.android.SectionArrayAdapter;
import com.ghstudios.android.data.classes.QuestReward;
import com.ghstudios.android.mhgendatabase.R;

import java.util.List;

public class QuestRewardFragment extends ListFragment{
	private static final String ARG_QUEST_ID = "QUEST_ID";

	public static QuestRewardFragment newInstance(long questId) {
		Bundle args = new Bundle();
		args.putLong(ARG_QUEST_ID, questId);
		QuestRewardFragment f = new QuestRewardFragment();
		f.setArguments(args);
		return f;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_generic_list, container,false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		QuestDetailViewModel viewModel = ViewModelProviders.of(getActivity()).get(QuestDetailViewModel.class);
		viewModel.getRewards().observe(this, this::populateRewards);
	}

	private void populateRewards(List<QuestReward> rewards){
		setListAdapter(new QuestRewardAdapter(getContext(),rewards));
	}

	private static class QuestRewardAdapter extends SectionArrayAdapter<QuestReward> {

		public QuestRewardAdapter(Context context, List<QuestReward> rewards) {
			super(context,rewards);
		}

		@Override
		public String getGroupName(QuestReward item) {
			return item.getRewardSlot();
		}

		@Override
		public View newView(Context context, QuestReward reward, ViewGroup parent) {
			// Use a layout inflater to get a row view
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			return inflater.inflate(R.layout.fragment_quest_reward_listitem,
					parent, false);
		}

		@Override
		public void bindView(View view, Context context, QuestReward questReward) {
			// Set up the text view
			LinearLayout itemLayout = view.findViewById(R.id.listitem);
			ImageView itemImageView = view.findViewById(R.id.item_image);

			TextView itemTextView = view.findViewById(R.id.item);
			TextView amountTextView = view.findViewById(R.id.amount);
			TextView percentageTextView = view.findViewById(R.id.percentage);

			String cellItemText = questReward.getItem().getName();
			int cellAmountText = questReward.getStackSize();
			int cellPercentageText = questReward.getPercentage();

			itemTextView.setText(cellItemText);
			amountTextView.setText("x" + cellAmountText);

			String percent = "" + cellPercentageText + "%";
			percentageTextView.setText(percent);

			AssetLoader.setIcon(itemImageView,questReward.getItem());

			itemLayout.setTag(questReward.getItem().getId());
            itemLayout.setOnClickListener(new BasicItemClickListener(context, questReward.getItem()
                    .getId()));
		}
	}

}
