package com.ghstudios.android.ui.ClickListeners;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.ghstudios.android.features.quests.QuestDetailActivity;

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
        Intent i = new Intent(c, QuestDetailActivity.class);
        i.putExtra(QuestDetailActivity.EXTRA_QUEST_ID, id);
        c.startActivity(i);
    }
}