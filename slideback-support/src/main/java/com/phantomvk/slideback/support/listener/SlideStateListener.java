package com.phantomvk.slideback.support.listener;

import android.support.v4.widget.ViewDragHelper;

public interface SlideStateListener {
    /**
     * Called when one of the subscribed edges in the parent view has been touched
     * by the user while no child view is currently captured.
     *
     * @param edgeFlags A combination of edge flags describing the edge(s) currently touched
     * @see ViewDragHelper#EDGE_LEFT
     * @see ViewDragHelper#EDGE_TOP
     * @see ViewDragHelper#EDGE_RIGHT
     * @see ViewDragHelper#EDGE_BOTTOM
     */
    void onEdgeTouched(int edgeFlags);

    /**
     * Called when the current drag state has been changed.
     *
     * @param state         current drag state
     * @param scrollPercent scroll percent of the current view
     * @see ViewDragHelper#STATE_IDLE
     * @see ViewDragHelper#STATE_DRAGGING
     * @see ViewDragHelper#STATE_SETTLING
     */
    void onDragStateChanged(int state, float scrollPercent);

    /**
     * Called when the current view has been dragged over the threshold by the user.
     */
    void onSlideOverThreshold();

    /**
     * Called when the current view has been dragged and slided out of the parent view by the user.
     */
    void onSlideOverRange();
}
