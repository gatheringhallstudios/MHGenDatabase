package com.ghstudios.android.features.items.detail;

import androidx.lifecycle.ViewModelProviders;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.ListFragment;
import androidx.cursoradapter.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ghstudios.android.AssetLoader;
import com.ghstudios.android.data.classes.HuntingReward;
import com.ghstudios.android.data.cursors.HuntingRewardCursor;
import com.ghstudios.android.mhgendatabase.R;
import com.ghstudios.android.ClickListeners.MonsterClickListener;

public class ItemMonsterFragment extends ListFragment {
    private static final String ARG_ITEM_ID = "ITEM_ID";
    
    public static ItemMonsterFragment newInstance(long itemId) {
        Bundle args = new Bundle();
        args.putLong(ARG_ITEM_ID, itemId);
        ItemMonsterFragment f = new ItemMonsterFragment();
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
        viewModel.getMonsterRewardsData().observe(this, (cursor) -> {
            // Create an adapter to point at this cursor
            ItemHuntingRewardListCursorAdapter adapter = new ItemHuntingRewardListCursorAdapter(
                    getActivity(), cursor);
            setListAdapter(adapter);
        });
    }

    private static class ItemHuntingRewardListCursorAdapter extends CursorAdapter {

        private HuntingRewardCursor mHuntingRewardCursor;

        public ItemHuntingRewardListCursorAdapter(Context context, HuntingRewardCursor cursor) {
            super(context, cursor, 0);
            mHuntingRewardCursor = cursor;
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            // Use a layout inflater to get a row view
            LayoutInflater inflater = LayoutInflater.from(context);
            return inflater.inflate(R.layout.fragment_item_monster_listitem,
                    parent, false);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            // Get the item for the current row
            HuntingReward huntingReward = mHuntingRewardCursor.getHuntingReward();

            // Set up the text view
            RelativeLayout itemLayout = view.findViewById(R.id.listitem);
            
            TextView rankTextView = view.findViewById(R.id.rank);
            TextView monsterTextView = view.findViewById(R.id.monster);
            TextView methodTextView = view.findViewById(R.id.method);
            TextView amountTextView = view.findViewById(R.id.amount);
            TextView percentageTextView = view.findViewById(R.id.percentage);
            ImageView monsterImageView = view.findViewById(R.id.monster_image);

            String cellRankText = huntingReward.getRank();
            String cellMonsterText = huntingReward.getMonster().getName();
            String cellMethodText = huntingReward.getCondition();
            int cellAmountText = huntingReward.getStackSize();
            int cellPercentageText = huntingReward.getPercentage();

            rankTextView.setText(cellRankText);
            monsterTextView.setText(cellMonsterText);
            methodTextView.setText(cellMethodText);
            amountTextView.setText("x" + cellAmountText);

            String percent = "" + cellPercentageText + "%";
            percentageTextView.setText(percent);

            itemLayout.setTag(huntingReward.getMonster().getId());
            itemLayout.setOnClickListener(new MonsterClickListener(context,
                    huntingReward.getMonster().getId()));

            AssetLoader.setIcon(monsterImageView,huntingReward.getMonster());
        }
    }
}
