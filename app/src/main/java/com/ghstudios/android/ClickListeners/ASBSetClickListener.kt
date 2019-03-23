package com.ghstudios.android.ClickListeners

import android.content.Context
import android.content.Intent
import android.view.View
import com.ghstudios.android.features.armorsetbuilder.detail.ASBDetailPagerActivity
import com.ghstudios.android.features.armorsetbuilder.list.ASBSetListFragment

/**
 * Click listener used to navigate to an armor set.
 */
class ASBSetClickListener(
        val context: Context,
        val id: Long
) : View.OnClickListener {

    override fun onClick(v: View?) {
        val i = Intent(context, ASBDetailPagerActivity::class.java)
        i.putExtra(ASBSetListFragment.EXTRA_ASB_SET_ID, id)
        context.startActivity(i)
    }
}