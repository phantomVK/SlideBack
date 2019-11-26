package com.phantomvk.slideback.support;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.FloatRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.phantomvk.slideback.support.listener.SlideStateListener;
import com.phantomvk.slideback.support.utility.TranslucentConversionListener;
import com.phantomvk.slideback.support.utility.TranslucentHelper;
import com.phantomvk.slideback.support.utility.ViewDragHelper;

import java.util.ArrayList;

import static android.graphics.drawable.GradientDrawable.Orientation.BOTTOM_TOP;
import static android.graphics.drawable.GradientDrawable.Orientation.LEFT_RIGHT;
import static android.graphics.drawable.GradientDrawable.Orientation.RIGHT_LEFT;
import static android.graphics.drawable.GradientDrawable.Orientation.TOP_BOTTOM;
import static android.support.v4.widget.ViewDragHelper.DIRECTION_HORIZONTAL;
import static android.support.v4.widget.ViewDragHelper.DIRECTION_VERTICAL;
import static android.support.v4.widget.ViewDragHelper.EDGE_BOTTOM;
import static android.support.v4.widget.ViewDragHelper.EDGE_LEFT;
import static android.support.v4.widget.ViewDragHelper.EDGE_RIGHT;
import static android.support.v4.widget.ViewDragHelper.EDGE_TOP;
import static android.support.v4.widget.ViewDragHelper.STATE_DRAGGING;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

/**
 * To compute scroll, draw background, and do slide with animations.
 */
public class SlideLayout extends FrameLayout {

    private static final int FULL_ALPHA = 2 << 8 - 1;

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
     * Rect for drawing shadow.
     */
    private static final Rect RECT = new Rect();

    /**
     * Colors for GradientDrawable.
     */
    private static final int[] GRADIENT_COLORS = new int[]{0, 0x60000000};

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
     * True if the background Activity has drawn itself.
     * False if a timeout occurred waiting for the Activity to complete drawing.
     * <p>
     * For more details, see interface called TranslucentConversionListener
     * in {@link android.app.Activity}.
     */
    private boolean mDrawComplete = false;

    /**
     * Sliding percent.
     */
    private float mSlidePercent;

    /**
     * Slide threshold.
     */
    private float mThreshold = SLIDE_OVER_THRESHOLD;

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
     * The opacity of shadow.
     */
    private float mShadowOpacity;

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
     * The target activity.
     */
    private Activity mActivity;

    /**
     * Activities cannot draw during the period that their windows are animating in. In order
     * to know when it is safe to begin drawing they can override this method which will be
     * called when the entering animation has completed.
     * <p>
     * For more details, see onEnterAnimationComplete() in {@link Activity}.
     */
    private boolean mEnterAnimationComplete;

    /**
     * The list of {@link SlideStateListener} to send events.
     */
    private final ArrayList<SlideStateListener> mListeners = new ArrayList<>();

    /**
     * Shadow drawables of different directions.
     */
    private Drawable mShadowLeft;
    private Drawable mShadowRight;
    private Drawable mShadowTop;
    private Drawable mShadowBottom;

    private Interpolation mScrimInterpolation;
    private Interpolation mShadowInterpolation;

    private final TranslucentConversionListener mListener = new TranslucentConversionListener() {
        @Override
        public void onTranslucentConversionComplete(boolean drawComplete) {
            setDrawComplete(drawComplete);
        }
    };

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

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SlideLayout, defStyleAttr, 0);

        int edgeSize = a.getDimensionPixelSize(R.styleable.SlideLayout_slide_back_edge_size, 0);
        if (edgeSize > 0) setEdgeSize(edgeSize);

        setTrackingEdge(a.getInt(R.styleable.SlideLayout_slide_back_edge_flag, EDGE_LEFT));

        int size = (int) (getContext().getResources().getDisplayMetrics().density * 15);
        setShadowDrawable(a, R.styleable.SlideLayout_slide_back_shadow_left, EDGE_LEFT, size);
        setShadowDrawable(a, R.styleable.SlideLayout_slide_back_shadow_right, EDGE_RIGHT, size);
        setShadowDrawable(a, R.styleable.SlideLayout_slide_back_shadow_top, EDGE_TOP, size);
        setShadowDrawable(a, R.styleable.SlideLayout_slide_back_shadow_bottom, EDGE_BOTTOM, size);

        a.recycle();
    }

    public void attach(@NonNull Activity activity) {
        mActivity = activity;
        ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
        ViewGroup decorChild = (ViewGroup) decorView.getChildAt(0);
        if (decorChild instanceof SlideLayout) return;

        TypedArray a = activity.getTheme().obtainStyledAttributes(new int[]{android.R.attr.windowBackground});
        int background = a.getResourceId(0, 0);
        a.recycle();

        decorChild.setBackgroundResource(background);
        decorView.removeView(decorChild);
        decorView.addView(this);
        addView(decorChild);
        setContentView(decorChild);
    }

    public void attachColor(@NonNull Activity activity, @ColorInt int color) {
        ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
        ViewGroup decorChild = (ViewGroup) decorView.getChildAt(0);
        if (decorChild == this) return;

        decorChild.setBackgroundColor(color);
        decorView.removeView(decorChild);
        decorView.addView(this);
        addView(decorChild);
        setContentView(decorChild);
    }

    public void attachColorRes(@NonNull Activity activity, @DrawableRes int colorRes) {
        ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
        ViewGroup decorChild = (ViewGroup) decorView.getChildAt(0);
        if (decorChild == this) return;

        decorChild.setBackgroundResource(colorRes);
        decorView.removeView(decorChild);
        decorView.addView(this);
        addView(decorChild);
        setContentView(decorChild);
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

    private void setShadowDrawable(TypedArray a, int index, int edgeFlag, int size) {
        int resId = a.getResourceId(index, 0);
        if (resId != 0) {
            setShadow(resId, edgeFlag);
            return;
        }

        GradientDrawable.Orientation orientation;
        int width;
        int height;

        switch (edgeFlag) {
            case EDGE_LEFT:
                orientation = LEFT_RIGHT;
                width = size;
                height = MATCH_PARENT;
                break;
            case EDGE_RIGHT:
                orientation = RIGHT_LEFT;
                width = size;
                height = MATCH_PARENT;
                break;
            case EDGE_TOP:
                orientation = TOP_BOTTOM;
                width = MATCH_PARENT;
                height = size;
                break;
            default:
                orientation = BOTTOM_TOP;
                width = MATCH_PARENT;
                height = size;
                break;
        }

        GradientDrawable d = new GradientDrawable(orientation, GRADIENT_COLORS);
        d.setSize(width, height);
        setShadow(d, edgeFlag);
    }

    /**
     * Set slide threshold.
     *
     * @param threshold float value from 0F to 1F
     */
    public void setThreshold(final float threshold) {
        if (threshold >= 1.0F || threshold <= 0F) {
            throw new IllegalArgumentException("The value of threshold must between 0F to 1.0F");
        }
        mThreshold = threshold;
    }

    /**
     * Set the sensitivity.
     *
     * @param context     Context to get ViewConfiguration
     * @param sensitivity value between 0 to 1, the final value for touchSlop =
     *                    ViewConfiguration.getScaledTouchSlop * (1 / s);
     */
    public void setSensitivity(Context context, float sensitivity) {
        mHelper.setSensitivity(context, sensitivity);
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
            left = -width - mOverRangePixel - mShadowRight.getIntrinsicWidth();
        } else if ((mEdge & EDGE_TOP) != 0) {
            top = height + mOverRangePixel + mShadowTop.getIntrinsicHeight();
        } else if ((mEdge & EDGE_BOTTOM) != 0) {
            top = -height - mOverRangePixel - mShadowBottom.getIntrinsicHeight();
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

        if (needDrawn && mHelper.getViewDragState() != ViewDragHelper.STATE_IDLE) {
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
        child.getHitRect(RECT);

        final int alpha = (int) (mShadowOpacity * FULL_ALPHA);

        if ((mEdge & EDGE_LEFT) != 0) {
            mShadowLeft.setBounds(RECT.left - mShadowLeft.getIntrinsicWidth(), RECT.top,
                    RECT.left, RECT.bottom);
            mShadowLeft.setAlpha(alpha);
            mShadowLeft.draw(canvas);
        } else if ((mEdge & EDGE_RIGHT) != 0) {
            mShadowRight.setBounds(RECT.right, RECT.top,
                    RECT.right + mShadowRight.getIntrinsicWidth(), RECT.bottom);
            mShadowRight.setAlpha(alpha);
            mShadowRight.draw(canvas);
        } else if ((mEdge & EDGE_TOP) != 0) {
            mShadowTop.setBounds(RECT.left, RECT.top - mShadowTop.getIntrinsicHeight(),
                    RECT.right, RECT.top);
            mShadowTop.setAlpha(alpha);
            mShadowTop.draw(canvas);
        } else if ((mEdge & EDGE_BOTTOM) != 0) {
            mShadowBottom.setBounds(RECT.left, RECT.bottom, RECT.right,
                    RECT.bottom + mShadowBottom.getIntrinsicHeight());
            mShadowBottom.setAlpha(alpha);
            mShadowBottom.draw(canvas);
        }
    }

    @Override
    public void computeScroll() {
        mScrimOpacity = calScrimOpacity(mSlidePercent);
        mShadowOpacity = calShadowOpacity(mSlidePercent);

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
     * Set Interpolation to scrim.
     *
     * @param interpolation Interpolation
     */
    public void setScrimInterpolation(@Nullable Interpolation interpolation) {
        mScrimInterpolation = interpolation;
    }

    /**
     * @param slidePercent the percent of sliding, from 0.0F to 1.0F
     * @return float opacity value from 0.0F to 1.0F
     */
    private float calScrimOpacity(@FloatRange(from = 0.0F, to = 1.0F) float slidePercent) {
        return mScrimInterpolation != null
                ? mScrimInterpolation.getInterpolation(getContext(), slidePercent) : 0;
    }

    /**
     * Set Interpolation to shadow.
     *
     * @param interpolation Interpolation
     */
    public void setShadowInterpolation(@Nullable Interpolation interpolation) {
        mShadowInterpolation = interpolation;
    }

    /**
     * @param slidePercent the percent of sliding, from 0.0F to 1.0F
     * @return float opacity value from 0.0F to 1.0F
     */
    private float calShadowOpacity(@FloatRange(from = 0.0F, to = 1.0F) float slidePercent) {
        return mShadowInterpolation != null
                ? mShadowInterpolation.getInterpolation(getContext(), slidePercent) : 1 - slidePercent;
    }

    public void setEnable(boolean enable) {
        mSlideEnable = enable;
    }

    public boolean isDrawComplete() {
        return mDrawComplete;
    }

    public void setDrawComplete(boolean drawComplete) {
        mDrawComplete = drawComplete;
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
     * Remove the listener which has been added to subscribe events.
     *
     * @param l SlideStateListener
     */
    public void removeListener(@Nullable SlideStateListener l) {
        if (l != null) mListeners.remove(l);
    }

    /**
     * Clear listeners which have been added to subscribe events.
     */
    public void clearListener() {
        mListeners.clear();
    }

    public void convertToTranslucent() {
        TranslucentHelper.setTranslucent(mActivity, mListener);
    }

    public void convertFromTranslucent() {
        TranslucentHelper.removeTranslucent(mActivity);
        setDrawComplete(false);
    }

    /**
     * Activities cannot draw during the period that their windows are animating in. In order
     * to know when it is safe to begin drawing they can override this method which will be
     * called when the entering animation has completed.
     * <p>
     * For more details, see onEnterAnimationComplete() in {@link Activity}.
     */
    public void onEnterAnimationComplete() {
        mEnterAnimationComplete = true;
    }

    private class ViewDragCallback extends ViewDragHelper.Callback {

        private boolean triggered; // If view has been slided over the threshold.

        @Override
        public boolean tryCaptureView(@NonNull View child, int pointerId) {
            boolean touched = mHelper.isEdgeTouched(mEdge, pointerId);
            if (touched) {
                triggered = true;
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
                direction = false;
            }

            return touched && direction;
        }

        @Override
        public int getViewVerticalDragRange(@NonNull View child) {
            if ((mEdge & EDGE_TOP) != 0) {
                return mChildView.getHeight() + mOverRangePixel + mShadowTop.getIntrinsicHeight();
            } else if ((mEdge & EDGE_BOTTOM) != 0) {
                return mChildView.getHeight() + mOverRangePixel + mShadowBottom.getIntrinsicHeight();
            } else {
                return 0;
            }
        }

        @Override
        public int getViewHorizontalDragRange(@NonNull View child) {
            if ((mEdge & EDGE_LEFT) != 0) {
                return mChildView.getWidth() + mOverRangePixel + mShadowLeft.getIntrinsicWidth();
            } else if ((mEdge & EDGE_RIGHT) != 0) {
                return mChildView.getWidth() + mOverRangePixel + mShadowRight.getIntrinsicWidth();
            } else {
                return 0;
            }
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

            if (mSlidePercent < mThreshold && !triggered) {
                triggered = true;
            }

            final int dragState = mHelper.getViewDragState();
            for (SlideStateListener l : mListeners) {
                l.onDragStateChanged(dragState, mSlidePercent);
            }

            if (dragState == STATE_DRAGGING && triggered && mSlidePercent >= mThreshold) {
                triggered = false;
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
        public void onEdgeTouched(int edgeFlags, int pointerId) {
            if (!isDrawComplete() && mEnterAnimationComplete) convertToTranslucent();
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

        @Override
        public boolean isValidMoveAction() {
            return mDrawComplete && mEnterAnimationComplete;
        }
    }

    /**
     * The Interpolation for shadow and scrim.
     */
    @FunctionalInterface
    public interface Interpolation {
        float getInterpolation(@NonNull Context context,
                               @FloatRange(from = 0.0F, to = 1.0F) float slidePercent);
    }
}
