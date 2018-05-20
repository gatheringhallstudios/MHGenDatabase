package com.ghstudios.android.ui.ClickListeners;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.ghstudios.android.features.skills.SkillTreeDetailActivity;

/**
 * Created by Mark on 2/24/2015.
 */
public class SkillClickListener implements View.OnClickListener {
    private Context c;
    private Long id;

    public SkillClickListener(Context context, Long id) {
        super();
        this.id = id;
        this.c = context;
    }

    @Override
    public void onClick(View v) {
        Intent i = new Intent(c, SkillTreeDetailActivity.class);
        i.putExtra(SkillTreeDetailActivity.EXTRA_SKILLTREE_ID, id);
        c.startActivity(i);
    }
}
