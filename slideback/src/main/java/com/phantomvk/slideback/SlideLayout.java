package com.phantomvk.slideback;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;

import com.phantomvk.slideback.listener.SlideStateListener;
import com.phantomvk.slideback.utility.ViewDragHelper;

import java.util.ArrayList;
import java.util.List;

import static androidx.customview.widget.ViewDragHelper.DIRECTION_HORIZONTAL;
import static androidx.customview.widget.ViewDragHelper.DIRECTION_VERTICAL;
import static androidx.customview.widget.ViewDragHelper.EDGE_BOTTOM;
import static androidx.customview.widget.ViewDragHelper.EDGE_LEFT;
import static androidx.customview.widget.ViewDragHelper.EDGE_RIGHT;
import static androidx.customview.widget.ViewDragHelper.EDGE_TOP;
import static androidx.customview.widget.ViewDragHelper.STATE_DRAGGING;

public class SlideLayout extends FrameLayout {

    private static final int FULL_ALPHA = 255;

    /**
     * Default scrim color.
     */
    private static final int COLOR_SCRIM = 0x99000000;

    /**
     * Slide over threshold value.
     */
    private static final float SLIDE_OVER_THRESHOLD = 0.33F;

    /**
     * Slide over range in dp, the view has been slided out of the parent view.
     */
    private static final int SLIDE_OVER_RANGE = 3;

    /**
     * ViewDragHelper
     */
    private ViewDragHelper mHelper;

    /**
     * The target edge to track when view is sliding.
     *
     * @see ViewDragHelper#EDGE_LEFT
     * @see ViewDragHelper#EDGE_RIGHT
     * @see ViewDragHelper#EDGE_TOP
     * @see ViewDragHelper#EDGE_BOTTOM
     */
    private int mEdge;

    /**
     * If slide action is enabled.
     */
    private boolean mSlideEnable = true;

    /**
     * Sliding percent.
     */
    private float mSlidePercent;

    /**
     * Default slide threshold.
     */
    private float mThreshold = SLIDE_OVER_THRESHOLD;

    /**
     * If view has been slided over the threshold.
     */
    private boolean mThresholdTrigger;

    /**
     * If view has been slided over the range.
     */
    private boolean mOverRangeTrigger;

    /**
     * The value of slide over range in pixel.
     */
    private int mOverRangePixel;

    /**
     * The opacity of scrim.
     */
    private float mScrimOpacity;

    /**
     * The color of scrim.
     */
    @ColorInt
    private int mScrimColor = COLOR_SCRIM;

    /**
     * The child view.
     */
    private View mChildView;

    /**
     * The left of the child view.
     */
    private int mViewLeft;

    /**
     * The top of the child view.
     */
    private int mViewTop;

    /**
     * Rect for drawing shadow.
     */
    private final Rect mRect = new Rect();

    /**
     * The set of {@link SlideStateListener} to send events.
     */
    private List<SlideStateListener> mListeners = new ArrayList<>();

    /**
     * Shadow drawables of different directions.
     */
    private Drawable mShadowLeft;
    private Drawable mShadowRight;
    private Drawable mShadowTop;
    private Drawable mShadowBottom;

    public SlideLayout(@NonNull Context context) {
        this(context, null);
    }

    public SlideLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlideLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mHelper = ViewDragHelper.create(this, new ViewDragCallback());
        mOverRangePixel = (int) (getResources().getDisplayMetrics().density * SLIDE_OVER_RANGE);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SlideLayout,
                defStyleAttr, R.style.SlideBackLayout);

        int edgeSize = a.getDimensionPixelSize(R.styleable.SlideLayout_edge_size, -1);
        if (edgeSize > 0) setEdgeSize(edgeSize);

        setTrackingEdge(a.getInt(R.styleable.SlideLayout_edge_flag, 0));

        int shadowLeft = a.getResourceId(
                R.styleable.SlideLayout_slide_shadow_left,
                R.drawable.drawable_slide_left);
        setShadow(shadowLeft, EDGE_LEFT);

        int shadowRight = a.getResourceId(
                R.styleable.SlideLayout_slide_shadow_right,
                R.drawable.drawable_slide_right);
        setShadow(shadowRight, EDGE_RIGHT);

        int shadowTop = a.getResourceId(
                R.styleable.SlideLayout_slide_shadow_top,
                R.drawable.drawable_slide_top);
        setShadow(shadowTop, EDGE_TOP);

        int shadowBottom = a.getResourceId(
                R.styleable.SlideLayout_slide_shadow_bottom,
                R.drawable.drawable_slide_bottom);
        setShadow(shadowBottom, EDGE_BOTTOM);

        a.recycle();
    }

    @SuppressWarnings("ConstantConditions")
    public void attach(@NonNull Activity activity) {
        int[] attrs = new int[]{android.R.attr.windowBackground};
        TypedArray a = activity.getTheme().obtainStyledAttributes(attrs);
        int background = a.getResourceId(0, 0);
        a.recycle();

        ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
        ViewGroup decorChild = (ViewGroup) decorView.getChildAt(0);
        decorChild.setBackgroundResource(background);
        decorView.removeView(decorChild);
        addView(decorChild);
        setContentView(decorChild);
        decorView.addView(this);
    }

    public void setEdgeSize(int size) {
        mHelper.setEdgeSize(size);
    }

    /**
     * Enable edge tracking for the selected edges of the parent view.
     * The callback's {@link ViewDragHelper.Callback#onEdgeTouched(int, int)} and
     * {@link ViewDragHelper.Callback#onEdgeDragStarted(int, int)} methods will only be invoked
     * for edges for which edge tracking has been enabled.
     *
     * @param edgeFlags Combination of edge flags describing the edges to watch
     * @see ViewDragHelper#EDGE_LEFT
     * @see ViewDragHelper#EDGE_TOP
     * @see ViewDragHelper#EDGE_RIGHT
     * @see ViewDragHelper#EDGE_BOTTOM
     */
    public void setTrackingEdge(int edgeFlags) {
        mEdge = edgeFlags;
        mHelper.setEdgeTrackingEnabled(mEdge);
    }

    /**
     * Set slide threshold.
     *
     * @param threshold float value from 0F to 1F
     */
    public void setThreshold(final float threshold) {
        if (threshold >= 1.0F || threshold <= 0) {
            throw new IllegalArgumentException("The value of threshold must between 0F to 1.0F");
        }
        mThreshold = threshold;
    }

    /**
     * Slide ContentView out of parent view.
     */
    public void slideExit() {
        final int width = mChildView.getWidth();
        final int height = mChildView.getHeight();

        int left = 0;
        int top = 0;
        if ((mEdge & EDGE_LEFT) != 0) {
            left = width + mOverRangePixel + mShadowLeft.getIntrinsicWidth();
        } else if ((mEdge & EDGE_RIGHT) != 0) {
            left = -width - mOverRangePixel + mShadowRight.getIntrinsicWidth();
        } else if ((mEdge & EDGE_TOP) != 0) {
            top = height + mOverRangePixel + mShadowTop.getIntrinsicHeight();
        } else if ((mEdge & EDGE_BOTTOM) != 0) {
            top = -height - mOverRangePixel + mShadowBottom.getIntrinsicHeight();
        }

        mHelper.smoothSlideViewTo(mChildView, left, top);
        invalidate();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (!mSlideEnable) return false;
        try {
            return mHelper.shouldInterceptTouchEvent(event);
        } catch (ArrayIndexOutOfBoundsException e) {
            return false;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!mSlideEnable) return false;
        try {
            mHelper.processTouchEvent(event);
            return true;
        } catch (ArrayIndexOutOfBoundsException e) {
            return false;
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (mChildView == null) return;
        mChildView.layout(mViewLeft, mViewTop,
                mViewLeft + mChildView.getMeasuredWidth(),
                mViewTop + mChildView.getMeasuredHeight());
    }

    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        boolean needDrawn = (child == mChildView);
        boolean drawChild = super.drawChild(canvas, child, drawingTime);

        if (needDrawn && mScrimOpacity > 0
                && mHelper.getViewDragState() != ViewDragHelper.STATE_IDLE) {
            drawShadow(canvas, child);
            drawScrim(canvas, child);
        }

        return drawChild;
    }

    private void drawScrim(Canvas canvas, View child) {
        final int baseAlpha = (mScrimColor & 0xFF000000) >>> 24;
        final int slideAlpha = (int) (baseAlpha * mScrimOpacity);
        final int color = (slideAlpha << 24) | (mScrimColor & 0xFFFFFF);

        if ((mEdge & EDGE_LEFT) != 0) {
            canvas.clipRect(getLeft(), getTop(), child.getLeft(), getHeight());
        } else if ((mEdge & EDGE_RIGHT) != 0) {
            canvas.clipRect(child.getRight(), 0, getRight(), getHeight());
        } else if ((mEdge & EDGE_TOP) != 0) {
            canvas.clipRect(0, 0, child.getRight(), child.getTop());
        } else if ((mEdge & EDGE_BOTTOM) != 0) {
            canvas.clipRect(child.getLeft(), child.getBottom(), getRight(), getHeight());
        }

        canvas.drawColor(color);
    }

    private void drawShadow(Canvas canvas, View child) {
        child.getHitRect(mRect);

        if ((mEdge & EDGE_LEFT) != 0) {
            mShadowLeft.setBounds(mRect.left - mShadowLeft.getIntrinsicWidth(), mRect.top,
                    mRect.left, mRect.bottom);
            mShadowLeft.setAlpha((int) (mScrimOpacity * FULL_ALPHA));
            mShadowLeft.draw(canvas);
        }

        if ((mEdge & EDGE_RIGHT) != 0) {
            mShadowRight.setBounds(mRect.right, mRect.top,
                    mRect.right + mShadowRight.getIntrinsicWidth(), mRect.bottom);
            mShadowRight.setAlpha((int) (mScrimOpacity * FULL_ALPHA));
            mShadowRight.draw(canvas);
        }

        if ((mEdge & EDGE_TOP) != 0) {
            mShadowTop.setBounds(mRect.left, mRect.top - mShadowTop.getIntrinsicHeight(),
                    mRect.right, mRect.top);
            mShadowTop.setAlpha((int) (mScrimOpacity * FULL_ALPHA));
            mShadowTop.draw(canvas);
        }

        if ((mEdge & EDGE_BOTTOM) != 0) {
            mShadowBottom.setBounds(mRect.left, mRect.bottom, mRect.right,
                    mRect.bottom + mShadowBottom.getIntrinsicHeight());
            mShadowBottom.setAlpha((int) (mScrimOpacity * FULL_ALPHA));
            mShadowBottom.draw(canvas);
        }
    }

    @Override
    public void computeScroll() {
        mScrimOpacity = calScrimOpacity(getContext(), mSlidePercent);
        if (mHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    public void setShadow(int resId, int edgeFlag) {
        setShadow(getResources().getDrawable(resId), edgeFlag);
    }

    public void setShadow(Drawable shadow, int edgeFlag) {
        if ((edgeFlag & EDGE_LEFT) != 0) {
            mShadowLeft = shadow;
        } else if ((edgeFlag & EDGE_RIGHT) != 0) {
            mShadowRight = shadow;
        } else if ((edgeFlag & EDGE_TOP) != 0) {
            mShadowTop = shadow;
        } else if ((edgeFlag & EDGE_BOTTOM) != 0) {
            mShadowBottom = shadow;
        }
        invalidate();
    }

    /**
     * @param context      context for getting resources to calculate opacity
     * @param slidePercent the percent of sliding, from 0F to 1F
     * @return opacity value
     */
    public float calScrimOpacity(@NonNull Context context, float slidePercent) {
        return 1 - slidePercent;
    }

    public void setEnable(boolean enable) {
        mSlideEnable = enable;
    }

    public void setScrimColor(@ColorInt int color) {
        mScrimColor = color;
    }

    public void setContentView(@NonNull ViewGroup view) {
        mChildView = view;
    }

    /**
     * Add a new {@link SlideStateListener} to subscribe events.
     *
     * @param l SlideStateListener
     */
    public void addListener(@Nullable SlideStateListener l) {
        if (l != null) mListeners.add(l);
    }

    /**
     * Remove listener which has been added to subscribe events.
     *
     * @param l SlideStateListener
     */
    public void removeListener(@Nullable SlideStateListener l) {
        if (l != null) mListeners.remove(l);
    }

    private class ViewDragCallback extends ViewDragHelper.Callback {
        @Override
        public boolean tryCaptureView(@NonNull View child, int pointerId) {
            boolean touched = mHelper.isEdgeTouched(mEdge, pointerId);
            if (touched) {
                mThresholdTrigger = true;
                for (SlideStateListener l : mListeners) {
                    l.onEdgeTouched(mEdge);
                }
            }

            boolean direction;
            if ((mEdge & (EDGE_LEFT | EDGE_RIGHT)) != 0) {
                direction = !mHelper.checkTouchSlop(DIRECTION_VERTICAL, pointerId);
            } else if ((mEdge & (EDGE_TOP | EDGE_BOTTOM)) != 0) {
                direction = !mHelper.checkTouchSlop(DIRECTION_HORIZONTAL, pointerId);
            } else {
                direction = true;
            }

            return touched && direction;
        }

        @Override
        public int getViewVerticalDragRange(@NonNull View child) {
            return mEdge & (EDGE_LEFT | EDGE_RIGHT);
        }

        @Override
        public int getViewHorizontalDragRange(@NonNull View child) {
            return mEdge & (EDGE_TOP | EDGE_BOTTOM);
        }

        @Override
        public void onViewPositionChanged(@NonNull View changedView,
                                          int left, int top, int dx, int dy) {
            super.onViewPositionChanged(changedView, left, top, dx, dy);

            if ((mEdge & EDGE_LEFT) != 0) {
                int width = mChildView.getWidth() + mShadowLeft.getIntrinsicWidth();
                mSlidePercent = Math.abs(((float) left) / width);
            } else if ((mEdge & EDGE_RIGHT) != 0) {
                int width = mChildView.getWidth() + mShadowRight.getIntrinsicWidth();
                mSlidePercent = Math.abs(((float) left) / width);
            } else if ((mEdge & EDGE_TOP) != 0) {
                int height = mChildView.getHeight() + mShadowTop.getIntrinsicHeight();
                mSlidePercent = Math.abs(((float) top) / height);
            } else if ((mEdge & EDGE_BOTTOM) != 0) {
                int height = mChildView.getHeight() + mShadowBottom.getIntrinsicHeight();
                mSlidePercent = Math.abs(((float) top) / height);
            }

            mViewLeft = left;
            mViewTop = top;
            invalidate();

            mThresholdTrigger = (mSlidePercent < mThreshold && !mThresholdTrigger);

            final int dragState = mHelper.getViewDragState();
            for (SlideStateListener l : mListeners) {
                l.onDragStateChanged(dragState, mSlidePercent);
            }

            if (dragState == STATE_DRAGGING && mThresholdTrigger && mSlidePercent >= mThreshold) {
                mThresholdTrigger = false;
                for (SlideStateListener l : mListeners) {
                    l.onSlideOverThreshold();
                }
            }

            if (mSlidePercent >= 1F && !mOverRangeTrigger) {
                for (SlideStateListener listener : mListeners) {
                    listener.onSlideOverRange();
                    mOverRangeTrigger = true;
                }
            }
        }

        @Override
        public void onViewReleased(@NonNull View releasedChild, float xvel, float yvel) {
            int width = releasedChild.getWidth();
            int height = releasedChild.getHeight();
            int left = 0, top = 0;

            if ((mEdge & EDGE_LEFT) != 0) {
                left = ((xvel > 0) || ((xvel == 0) && (mSlidePercent > mThreshold)))
                        ? (width + mOverRangePixel + mShadowLeft.getIntrinsicWidth()) : 0;
            } else if ((mEdge & EDGE_RIGHT) != 0) {
                left = ((xvel < 0) || ((xvel == 0) && (mSlidePercent > mThreshold)))
                        ? -(width + mOverRangePixel + mShadowRight.getIntrinsicWidth()) : 0;
            } else if ((mEdge & EDGE_TOP) != 0) {
                top = ((yvel > 0) || ((yvel == 0) && (mSlidePercent > mThreshold)))
                        ? (height + mOverRangePixel + mShadowTop.getIntrinsicHeight()) : 0;
            } else if ((mEdge & EDGE_BOTTOM) != 0) {
                top = ((yvel < 0) || ((yvel == 0) && (mSlidePercent > mThreshold)))
                        ? -(height + mOverRangePixel + mShadowBottom.getIntrinsicHeight()) : 0;
            }

            mHelper.settleCapturedViewAt(left, top);
            invalidate();
        }

        @Override
        public int clampViewPositionVertical(@NonNull View child, int top, int dy) {
            int position = 0;
            if ((mEdge & EDGE_TOP) != 0) {
                position = Math.min(child.getHeight(), Math.max(top, 0));
            } else if ((mEdge & EDGE_BOTTOM) != 0) {
                position = Math.min(0, Math.max(top, -child.getHeight()));
            }
            return position;
        }

        @Override
        public int clampViewPositionHorizontal(@NonNull View child, int left, int dx) {
            int position = 0;
            if ((mEdge & EDGE_LEFT) != 0) {
                position = Math.min(child.getWidth(), Math.max(left, 0));
            } else if ((mEdge & EDGE_RIGHT) != 0) {
                position = Math.min(0, Math.max(left, -child.getWidth()));
            }
            return position;
        }
    }
}
