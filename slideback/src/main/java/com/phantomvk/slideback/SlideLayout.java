package com.phantomvk.slideback;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.FloatRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.ViewCompat;

import com.phantomvk.slideback.listener.SlideStateListener;
import com.phantomvk.slideback.utility.TranslucentHelper;
import com.phantomvk.slideback.utility.ViewDragHelper;

import java.util.ArrayList;
import java.util.List;

import static android.graphics.drawable.GradientDrawable.Orientation.BOTTOM_TOP;
import static android.graphics.drawable.GradientDrawable.Orientation.LEFT_RIGHT;
import static android.graphics.drawable.GradientDrawable.Orientation.RIGHT_LEFT;
import static android.graphics.drawable.GradientDrawable.Orientation.TOP_BOTTOM;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static androidx.customview.widget.ViewDragHelper.DIRECTION_HORIZONTAL;
import static androidx.customview.widget.ViewDragHelper.DIRECTION_VERTICAL;
import static androidx.customview.widget.ViewDragHelper.EDGE_BOTTOM;
import static androidx.customview.widget.ViewDragHelper.EDGE_LEFT;
import static androidx.customview.widget.ViewDragHelper.EDGE_RIGHT;
import static androidx.customview.widget.ViewDragHelper.EDGE_TOP;
import static androidx.customview.widget.ViewDragHelper.STATE_DRAGGING;

/**
 * To compute scroll, draw background, and do slide with animations.
 */
public class SlideLayout extends FrameLayout {
    /**
     * Rect for drawing shadow.
     */
    private static final Rect RECT = new Rect();

    /**
     * Colors for GradientDrawable.
     */
    private static final int[] GRADIENT_COLORS = new int[]{0, 0x60000000};

    /**
     * Background attribute id.
     */
    private static final int[] ATTR_BACKGROUND = new int[]{android.R.attr.windowBackground};

    /**
     * ViewDragHelper
     */
    private final ViewDragHelper mHelper;

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
     * Sliding percent.
     */
    private float mSlidePercent;

    /**
     * Slide threshold.
     */
    private float mThreshold = 0.33F;

    /**
     * The value of slide over range in pixel.
     */
    private final int mOverRangePixel;

    /**
     * The default size of the shadow drawable;
     */
    private final int mShadowSize;

    /**
     * The alpha of shadow.
     */
    private int mShadowAlpha;

    /**
     * The base color of scrim.
     */
    @ColorInt
    private int mScrimBaseColor = 0x99000000;

    /**
     * The actual color of scrim
     */
    private int mScrimColor;

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
     * Flag, Slide back function is enabled.
     */
    private static final int FLAG_SLIDE_ENABLE = 1;

    /**
     * Flag, the Activity has drawn it background to translucent.
     * <p>
     * For more details, see interface called TranslucentConversionListener
     * in {@link android.app.Activity}.
     */
    private static final int FLAG_DRAW_COMPLETE = 1 << 1;

    /**
     * Activities cannot draw during the period that their windows are animating in. In order
     * to know when it is safe to begin drawing they can override this method which will be
     * called when the entering animation has completed.
     * <p>
     * For more details, see onEnterAnimationComplete() in {@link Activity}.
     */
    private static final int FLAG_ANIMATION_COMPLETE = 1 << 2;

    /**
     * Flag, the combination of {@value FLAG_DRAW_COMPLETE} and {@value FLAG_ANIMATION_COMPLETE}.
     */
    private static final int FLAG_BOTH_DRAW_ANIMATION_COMPLETE = FLAG_DRAW_COMPLETE | FLAG_ANIMATION_COMPLETE;

    /**
     * Flag, the view has been slided over the range.
     */
    private static final int FLAG_OVER_RANGE_TRIGGERED = 1 << 3;

    /**
     * Flag to save memory.
     * <p>
     * {@value SlideLayout#FLAG_SLIDE_ENABLE}
     * {@value SlideLayout#FLAG_DRAW_COMPLETE}
     * {@value SlideLayout#FLAG_ANIMATION_COMPLETE}
     * {@value SlideLayout#FLAG_OVER_RANGE_TRIGGERED}
     */
    private int flags = FLAG_SLIDE_ENABLE;

    /**
     * The list of {@link SlideStateListener} to receive events.
     */
    private final List<SlideStateListener> mListeners = new ArrayList<>(1);

    /**
     * Shadow drawables of different directions.
     */
    private Drawable mShadowLeft;
    private Drawable mShadowRight;
    private Drawable mShadowTop;
    private Drawable mShadowBottom;

    private Interpolation mScrimInterpolation;
    private Interpolation mShadowInterpolation;

    public SlideLayout(@NonNull Context context) {
        this(context, null, 0);
    }

    public SlideLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlideLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        final int density = (int) getContext().getResources().getDisplayMetrics().density;
        mHelper = ViewDragHelper.create(this, new ViewDragCallback());
        mOverRangePixel = density * 3;
        mShadowSize = density * 15;

        setTrackingEdge(EDGE_LEFT);
    }

    public void attach(@NonNull Activity activity) {
        mActivity = activity;
        ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
        ViewGroup decorChild = (ViewGroup) decorView.getChildAt(0);
        if (decorChild instanceof SlideLayout) return;

        TypedArray a = activity.getTheme().obtainStyledAttributes(ATTR_BACKGROUND);
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
        if (decorChild instanceof SlideLayout) return;

        decorChild.setBackgroundColor(color);
        decorView.removeView(decorChild);
        decorView.addView(this);
        addView(decorChild);
        setContentView(decorChild);
    }

    public void attachColorRes(@NonNull Activity activity, @DrawableRes int colorRes) {
        ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
        ViewGroup decorChild = (ViewGroup) decorView.getChildAt(0);
        if (decorChild instanceof SlideLayout) return;

        decorChild.setBackgroundResource(colorRes);
        decorView.removeView(decorChild);
        decorView.addView(this);
        addView(decorChild);
        setContentView(decorChild);
    }

    public void setEdgeSize(int size) {
        if (size > 0) mHelper.setEdgeSize(size);
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
        setShadow();
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

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (!getFlag(FLAG_SLIDE_ENABLE)) return false;
        try {
            return mHelper.shouldInterceptTouchEvent(event);
        } catch (ArrayIndexOutOfBoundsException e) {
            return false;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!getFlag(FLAG_SLIDE_ENABLE)) return false;
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
        boolean drawChild = super.drawChild(canvas, child, drawingTime);

        if (child == mChildView && mHelper.getViewDragState() != ViewDragHelper.STATE_IDLE) {
            drawShadow(canvas, child);
            drawScrim(canvas, child);
        }

        return drawChild;
    }

    private void drawShadow(Canvas canvas, View child) {
        child.getHitRect(RECT);

        if ((mEdge & EDGE_LEFT) != 0) {
            mShadowLeft.setBounds(RECT.left - mShadowLeft.getIntrinsicWidth(), RECT.top,
                    RECT.left, RECT.bottom);
            mShadowLeft.setAlpha(mShadowAlpha);
            mShadowLeft.draw(canvas);
        } else if ((mEdge & EDGE_RIGHT) != 0) {
            mShadowRight.setBounds(RECT.right, RECT.top,
                    RECT.right + mShadowRight.getIntrinsicWidth(), RECT.bottom);
            mShadowRight.setAlpha(mShadowAlpha);
            mShadowRight.draw(canvas);
        } else if ((mEdge & EDGE_TOP) != 0) {
            mShadowTop.setBounds(RECT.left, RECT.top - mShadowTop.getIntrinsicHeight(),
                    RECT.right, RECT.top);
            mShadowTop.setAlpha(mShadowAlpha);
            mShadowTop.draw(canvas);
        } else if ((mEdge & EDGE_BOTTOM) != 0) {
            mShadowBottom.setBounds(RECT.left, RECT.bottom, RECT.right,
                    RECT.bottom + mShadowBottom.getIntrinsicHeight());
            mShadowBottom.setAlpha(mShadowAlpha);
            mShadowBottom.draw(canvas);
        }
    }

    private void drawScrim(Canvas canvas, View child) {
        if ((mEdge & EDGE_LEFT) != 0) {
            canvas.clipRect(getLeft(), getTop(), child.getLeft(), getHeight());
        } else if ((mEdge & EDGE_RIGHT) != 0) {
            canvas.clipRect(child.getRight(), 0, getRight(), getHeight());
        } else if ((mEdge & EDGE_TOP) != 0) {
            canvas.clipRect(0, 0, child.getRight(), child.getTop());
        } else if ((mEdge & EDGE_BOTTOM) != 0) {
            canvas.clipRect(child.getLeft(), child.getBottom(), getRight(), getHeight());
        }

        canvas.drawColor(mScrimColor);
    }

    @Override
    public void computeScroll() {
        final float scrimOpacity = calScrimOpacity(mSlidePercent);
        final int baseAlpha = (mScrimBaseColor & 0xFF000000) >>> 24;
        final int slideAlpha = (int) (baseAlpha * scrimOpacity);
        mScrimColor = (slideAlpha << 24) | (mScrimBaseColor & 0xFFFFFF);

        final float shadowOpacity = calShadowOpacity(mSlidePercent);
        mShadowAlpha = (int) (shadowOpacity * (2 << 8 - 1));

        if (mHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    /**
     * Set a default shadow drawable to the current tracking edge if it has not been used before.
     * <p>
     * It is possible to use method {@link SlideLayout#setShadow(int, int)}
     * and method {@link SlideLayout#setShadow(Drawable, int)} to assign a custom shadow drawable
     * to replace the previous one.
     */
    private void setShadow() {
        GradientDrawable.Orientation orientation;
        Drawable drawable;
        int width, height;

        switch (mEdge) {
            case EDGE_LEFT:
                drawable = mShadowLeft;
                orientation = LEFT_RIGHT;
                width = mShadowSize;
                height = MATCH_PARENT;
                break;
            case EDGE_RIGHT:
                drawable = mShadowRight;
                orientation = RIGHT_LEFT;
                width = mShadowSize;
                height = MATCH_PARENT;
                break;
            case EDGE_TOP:
                drawable = mShadowTop;
                orientation = TOP_BOTTOM;
                width = MATCH_PARENT;
                height = mShadowSize;
                break;
            default:
                drawable = mShadowBottom;
                orientation = BOTTOM_TOP;
                width = MATCH_PARENT;
                height = mShadowSize;
                break;
        }

        if (drawable == null) {
            GradientDrawable d = new GradientDrawable(orientation, GRADIENT_COLORS);
            d.setSize(width, height);
            setShadow(d, mEdge);
        }
    }

    /**
     * Set a shadow drawable to the edge.
     *
     * @param resId    drawable resource id
     * @param edgeFlag the edge to set shadow
     */
    public void setShadow(int resId, int edgeFlag) {
        Drawable d = ResourcesCompat.getDrawable(mActivity.getResources(), resId, null);
        setShadow(d, edgeFlag);
    }

    /**
     * Set a shadow drawable to the edge.
     *
     * @param shadow   drawable
     * @param edgeFlag the edge to set shadow
     */
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
        return mScrimInterpolation == null ? 0
                : mScrimInterpolation.getInterpolation(getContext(), slidePercent);
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
        return mShadowInterpolation == null ? 1 - slidePercent
                : mShadowInterpolation.getInterpolation(getContext(), slidePercent);
    }

    public void setEnable(boolean enable) {
        setFlag(FLAG_SLIDE_ENABLE, enable);
    }

    public boolean isDrawComplete() {
        return getFlag(FLAG_DRAW_COMPLETE);
    }

    public void setScrimColor(@ColorInt int color) {
        mScrimBaseColor = color;
    }

    public void setContentView(@NonNull ViewGroup view) {
        mChildView = view;
    }

    /**
     * Add a new {@link SlideStateListener} to subscribe slide state events.
     *
     * @param listener a SlideStateListener
     */
    public void addListener(@Nullable SlideStateListener listener) {
        if (listener != null) mListeners.add(listener);
    }

    /**
     * Add some new {@link SlideStateListener} to subscribe slide state events.
     *
     * @param listeners ths list should not contain null object
     */
    public void addListeners(@Nullable List<SlideStateListener> listeners) {
        if (listeners == null || listeners.isEmpty()) return;
        mListeners.addAll(listeners);
    }

    /**
     * Remove the listener which has been added to subscribe events.
     *
     * @param listener SlideStateListener
     */
    public void removeListener(@Nullable SlideStateListener listener) {
        if (listener != null) mListeners.remove(listener);
    }

    /**
     * Clear listeners which have been added to subscribe events.
     */
    public void clearListener() {
        mListeners.clear();
    }

    public void convertToTranslucent() {
        TranslucentHelper.setTranslucent(mActivity, (v) -> setFlag(FLAG_DRAW_COMPLETE, v));
    }

    public void convertFromTranslucent() {
        TranslucentHelper.removeTranslucent(mActivity);
        setFlag(FLAG_DRAW_COMPLETE, false);
    }

    /**
     * Activities cannot draw during the period that their windows are animating in. In order
     * to know when it is safe to begin drawing they can override this method which will be
     * called when the entering animation has completed.
     * <p>
     * For more details, see {@link Activity#onEnterAnimationComplete()}.
     */
    public void onEnterAnimationComplete() {
        setFlag(FLAG_ANIMATION_COMPLETE, true);
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

            if (mSlidePercent >= 1F && !getFlag(FLAG_OVER_RANGE_TRIGGERED)) {
                for (SlideStateListener l : mListeners) {
                    l.onSlideOverRange();
                    setFlag(FLAG_OVER_RANGE_TRIGGERED, true);
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
            if (!isDrawComplete() && getFlag(FLAG_ANIMATION_COMPLETE)) convertToTranslucent();
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
            return getFlag(FLAG_BOTH_DRAW_ANIMATION_COMPLETE);
        }
    }

    private boolean getFlag(int flag) {
        return (flags & flag) == flag;
    }

    private void setFlag(int flag, boolean enable) {
        if (enable) {
            flags |= flag;
        } else {
            flags &= ~flag;
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
