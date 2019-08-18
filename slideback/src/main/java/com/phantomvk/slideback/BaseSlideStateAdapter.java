package com.phantomvk.slideback;

public abstract class BaseSlideStateAdapter implements SlideStateListener {
    @Override
    public void onEdgeTouched(int edgeFlags) {
    }

    @Override
    public void onDragStateChanged(int state, float scrollPercent) {
    }

    @Override
    public void onSlideOverThreshold() {
    }

    @Override
    public void onSlideOverRange() {
    }
}
