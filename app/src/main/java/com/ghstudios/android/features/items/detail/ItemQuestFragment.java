package com.ghstudios.android.features.items.detail;

import androidx.lifecycle.ViewModelProviders;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ghstudios.android.data.classes.QuestReward;
import com.ghstudios.android.data.cursors.QuestRewardCursor;
import com.ghstudios.android.mhgendatabase.R;
import com.ghstudios.android.ClickListeners.QuestClickListener;
import com.github.monxalo.android.widget.SectionCursorAdapter;

public class ItemQuestFragment extends ListFragment {
    private static final String ARG_ITEM_ID = "ITEM_ID";

    public static ItemQuestFragment newInstance(long itemId) {
        Bundle args = new Bundle();
        args.putLong(ARG_ITEM_ID, itemId);
        ItemQuestFragment f = new ItemQuestFragment();
        f.setArguments(args);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_generic_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ItemDetailViewModel viewModel = ViewModelProviders.of(getActivity()).get(ItemDetailViewModel.class);
        viewModel.getQuestRewardsData().observe(this, (cursor) -> {
            QuestRewardListCursorAdapter adapter = new QuestRewardListCursorAdapter(
                    getActivity(), cursor);
            setListAdapter(adapter);
        });
    }

    private static class QuestRewardListCursorAdapter extends SectionCursorAdapter {

        private QuestRewardCursor mQuestRewardCursor;

        public QuestRewardListCursorAdapter(Context context,
                QuestRewardCursor cursor) {
            super(context, cursor, R.layout.listview_generic_header,1);
            mQuestRewardCursor = cursor;
        }

        @Override
        protected String getCustomGroup(Cursor c) {
            QuestReward questReward = ((QuestRewardCursor)c).getQuestReward();
            return questReward.getQuest().getHub().toString();
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            // Use a layout inflater to get a row view
            LayoutInflater inflater = LayoutInflater.from(context);
            return inflater.inflate(R.layout.fragment_item_quest_listitem,
                    parent, false);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            // Get the item for the current row
            QuestReward questReward = mQuestRewardCursor.getQuestReward();

            // Set up the text view
            LinearLayout itemLayout = view.findViewById(R.id.listitem);

            TextView questTextView = view.findViewById(R.id.quest_name);
            TextView levelTextView = view.findViewById(R.id.quest_stars);
            TextView slotTextView = view.findViewById(R.id.slot);
            TextView amountTextView = view.findViewById(R.id.amount);
            TextView percentageTextView = view.findViewById(R.id.percentage);

            String cellQuestText = questReward.getQuest().getName();
            String cellLevelText = questReward.getQuest().getStarString();
            String cellSlotText = questReward.getRewardSlot();
            int cellAmountText = questReward.getStackSize();
            int cellPercentageText = questReward.getPercentage();

            questTextView.setText(cellQuestText);
            levelTextView.setText(cellLevelText);
            slotTextView.setText(cellSlotText);
            amountTextView.setText("x" + cellAmountText);

            String percent = "" + cellPercentageText + "%";
            percentageTextView.setText(percent);

            itemLayout.setTag(questReward.getQuest().getId());
            itemLayout.setOnClickListener(new QuestClickListener(context,
                    questReward.getQuest().getId()));
        }
    }

}
