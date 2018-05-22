package com.ghstudios.android.ClickListeners;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.ghstudios.android.features.quests.QuestDetailPagerActivity;

/**
 * Created by Mark on 2/24/2015.
 */
public class QuestClickListener implements View.OnClickListener {
    private Context c;
    private Long id;

    public QuestClickListener(Context context, Long id) {
        super();
        this.id = id;
        this.c = context;
    }

    @Override
    public void onClick(View v) {
        Intent i = new Intent(c, QuestDetailPagerActivity.class);
        i.putExtra(QuestDetailPagerActivity.EXTRA_QUEST_ID, id);
        c.startActivity(i);
    }
}