package com.ghstudios.android.util

/**
 * A class that encapsulates an operation that can either be completed or undone.
 * Returned by methods that have those 2 potential branching paths.
 */
class UndoableOperation(
        val onComplete: () -> Unit,
        val onUndo: () -> Unit
) {
    /**
     * Returns true if complete() or undo() have been called, otherwise returns false.
     */
    var finished = false
        private set

    /**
     * Completes the encapsulated operation as a success and flags as complete.
     * Does nothing if the operation has been completed.
     */
    fun complete() {
        if (!finished) {
            finished = true
            onComplete()
        }
    }

    /**
     * Requests a reversal in the encapsulated operation and flags as complete.
     * Does nothing if the operation has been completed.
     */
    fun undo() {
        if (!finished) {
            finished = true
            onUndo()
        }
    }
}