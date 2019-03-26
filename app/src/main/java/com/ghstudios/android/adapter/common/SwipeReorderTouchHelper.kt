package com.ghstudios.android.adapter.common

import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper

/**
 * ItemTouchHelper used to add swipe and reorder functionality to recyclerviews.
 */
class SwipeReorderTouchHelper(
        val afterSwiped: (position: RecyclerView.ViewHolder) -> Unit
) : ItemTouchHelper.SimpleCallback(
        0, //ItemTouchHelper.UP or ItemTouchHelper.DOWN,
        ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
) {
    override fun onMove(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        // Called every time order is swapped mid-drag.
        // Update the recyclerview's backing data but don't actually call onMove until done (different callback)
        val originalIdx = viewHolder.adapterPosition
        val targetIdx = target.adapterPosition
        //Log.d("SWIPE", "MOVED $originalIdx to $targetIdx")
        // todo: actually implement
        //return true
        return false
    }

    /**
     * Callback called every time an item has been swiped away
     */
    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        afterSwiped(viewHolder)
    }
}