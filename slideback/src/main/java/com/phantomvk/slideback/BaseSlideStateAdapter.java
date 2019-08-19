package com.phantomvk.slideback;

public abstract class BaseSlideStateAdapter implements SlideStateListener {
    /**
     * {@inheritDoc}
     */
    @Override
    public void onEdgeTouched(int edgeFlags) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onDragStateChanged(int state, float scrollPercent) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onSlideOverThreshold() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onSlideOverRange() {
    }
}
