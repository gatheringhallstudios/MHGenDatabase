package com.ghstudios.android.adapter.common

import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.ItemTouchHelper

/**
 * ItemTouchHelper used to add swipe and reorder functionality to recyclerviews.
 */
class SwipeReorderTouchHelper(
        val afterSwiped: (position: androidx.recyclerview.widget.RecyclerView.ViewHolder) -> Unit
) : ItemTouchHelper.SimpleCallback(
        0, //ItemTouchHelper.UP or ItemTouchHelper.DOWN,
        ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
) {
    override fun onMove(recyclerView: androidx.recyclerview.widget.RecyclerView, viewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder, target: androidx.recyclerview.widget.RecyclerView.ViewHolder): Boolean {
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
    override fun onSwiped(viewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder, direction: Int) {
        afterSwiped(viewHolder)
    }
}