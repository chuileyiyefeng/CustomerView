package com.example.rico.customerview.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class JumpLoadView extends ViewGroup {
    private List<RectF> originRectList;
    private int topDistance, bottomDistance, canMoveTopDistance, canMoveBottomDistance;
    private float mTouchSlop;
    // canMove 判断 如果是false  拦截子view的事件，并且本view不做touch事件
    public boolean canMove = true;

    public JumpLoadView(Context context) {
        this(context, null);
    }

    public JumpLoadView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public JumpLoadView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        originRectList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            originRectList.add(new RectF());
        }
        ViewConfiguration configuration = ViewConfiguration.get(context);
        mTouchSlop = configuration.getScaledTouchSlop();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        //获取布局的高度和模式
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int realWidth = 0, realHeight = 0;
//            当前控件所占宽高,当前顶部的高度，当前行最高高度
        int currentTop = 0;
        int count = getChildCount();
        if (count != 3) {
            return;
        }
        widthMeasureSpec = MeasureSpec.makeMeasureSpec(widthSize, widthMode);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(heightSize, heightMode);
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
            int childWidth = child.getMeasuredWidth();
            int childHeight = child.getMeasuredHeight();
//            当前子控件不超过规定宽度
            if (realWidth < childWidth) {
                realWidth = childWidth;
            }
            int top = 0, bottom = 0;
            switch (i) {
                case 0:
                    topDistance = childHeight;
                    canMoveTopDistance = (int) (topDistance * 3f);
                    top = currentTop - childHeight;
                    bottom = top + childHeight;
                    currentTop = bottom;
                    break;
                case 1:
                    top = currentTop;
                    bottom = top + childHeight;
                    currentTop = bottom;
                    realHeight = childHeight;
                    break;
                case 2:
                    bottomDistance = childHeight;
                    canMoveBottomDistance = (int) (bottomDistance * 3f);
                    top = currentTop;
                    bottom = top + childHeight;
                    break;
            }
            originRectList.get(i).set(0, top, childWidth, bottom);
        }
        realWidth = widthMode == MeasureSpec.EXACTLY ? widthSize : realWidth;
        realHeight = heightMode == MeasureSpec.EXACTLY ? heightSize : realHeight;

        setMeasuredDimension(realWidth, realHeight);
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            RectF rect = originRectList.get(i);
            child.layout((int) rect.left, (int) rect.top, (int) rect.right, (int) rect.bottom);
        }
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!canMove) {
            return  super.onInterceptTouchEvent(event);
        }
        performClick();
        if (isOnAnim) {
            return false;
        }
        if (isOnLoadMore || isOnRefresh) {
            return false;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                float moveY = downY - event.getY();
                moveView(moveY);
                downY = event.getY();
                break;
            case MotionEvent.ACTION_UP:
                int scrollY = getScrollY();
                if (scrollY > 0) {
                    // 上拉状态
                    if (scrollY > bottomDistance) {
                        isOnLoadMore = true;
                        startAnim(bottomDistance - scrollY);
                    } else {
                        // 还原
                        isOnLoadMore = false;
                        startAnim(-scrollY);
                    }
                } else {
                    //下拉状态
                    if (-scrollY > topDistance) {
                        startAnim(-scrollY - topDistance);
                        isOnRefresh = true;
                    } else {
                        // 还原
                        startAnim(-scrollY);
                        isOnRefresh = false;
                    }
                }
                if (inTouchListener != null) {
                    inTouchListener.up();
                }
                break;
        }
        return true;
    }


    ValueAnimator scrollAnim;


    // 是否在动画状态、是否在加载、是否在刷新
    boolean isOnAnim, isOnLoadMore, isOnRefresh;

    // 还原状态
    public void reductionScroll() {
        if (rv != null) {
            rv.post(() -> startAnim(-getScrollY(), true));
        }
    }

    // 偏移值
    int animScrollValue;

    private void startAnim(int distance) {
        startAnim(distance, false);
    }

    // 是否是还原状态
    static boolean mReduction;
    int duration = 300;


    static int myDistance;

    private void startAnim(int distance, boolean isReduction) {
        mReduction = isReduction;
        myDistance = distance;
        isOnAnim = true;
        if (scrollAnim == null) {
            scrollAnim = ValueAnimator.ofInt(0, distance);
            scrollAnim.addUpdateListener(valueAnimator -> {
                int value = (int) valueAnimator.getAnimatedValue();
                animScrollValue = value - animScrollValue;
                int offsetY = rv.computeVerticalScrollOffset();
                if (mReduction && isOnLoadMore && offsetY != 0) {
                    rv.layout(rv.getLeft(), rv.getTop() + animScrollValue, rv.getRight(), rv.getBottom() + animScrollValue);
                }
                scrollBy(0, animScrollValue);
                animScrollValue = value;
            });
            scrollAnim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    isOnAnim = false;
                    animScrollValue = 0;
                    if (mReduction) {
                        int offsetY = rv.computeVerticalScrollOffset();
                        if (isOnLoadMore && offsetY != 0) {
                            RectF rectF = originRectList.get(1);
                            rv.layout((int) rectF.left, (int) rectF.top, (int) rectF.right, (int) rectF.bottom);
                            rv.scrollBy(0, -myDistance);
                        }
                        isOnLoadMore = false;
                        isOnRefresh = false;
                        scrollBy(0, -getScrollY());
                    } else {
                        if (isOnLoadMore && loadListener != null) {
                            loadListener.loadMore();
                        }
                        if (isOnRefresh && loadListener != null) {
                            loadListener.refresh();
                        }
                    }
                }
            });
            scrollAnim.setDuration(duration);
            scrollAnim.setInterpolator(new DecelerateInterpolator());
        } else {
            scrollAnim.setIntValues(0, distance);
        }
        scrollAnim.start();
    }

    final int lastDown = 1, lastUp = 2;
    int lastScrollType;
    float moveScale, minScale = 0.3f;

    private void moveView(float distanceY) {
        if (inTouchListener != null) {
            inTouchListener.move();
        }
        // 向下滑动
        if (distanceY < 0) {
            if (lastScrollType == lastDown) {
                moveScale -= 0.5f;
                if (moveScale <= minScale) {
                    moveScale = minScale;
                }
                distanceY = distanceY * moveScale;
            } else {
                moveScale = 1.0f;
            }
            lastScrollType = lastDown;
            if (getScrollY() + distanceY < -canMoveTopDistance) {
                distanceY = -canMoveTopDistance - getScrollY();
            }
        } else {
            if (lastScrollType == lastUp) {
                moveScale -= 0.5f;
                if (moveScale <= minScale) {
                    moveScale = minScale;
                }
                distanceY = distanceY * moveScale;
            } else {
                moveScale = 1.0f;
            }
            lastScrollType = lastUp;
            if (getScrollY() + distanceY > canMoveBottomDistance) {
                distanceY = canMoveBottomDistance - getScrollY();
            }
        }
        scrollBy(0, (int) (distanceY + 0.5f));
    }

    float downY;
    boolean isIntercept = true;

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (scrollAnim != null) {
            scrollAnim.cancel();
        }
    }

    RecyclerView rv;

    public void connect(RecyclerView rv) {
        this.rv = rv;
    }

    float interceptDownY;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (!canMove) {
            return  true;
        }
        if (isOnRefresh || isOnLoadMore) {
            return true;
        }
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isIntercept = false;
                interceptDownY = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                downY = ev.getY();
                if (Math.abs(interceptDownY - ev.getY()) < mTouchSlop) {
                    return false;
                }
                if (rv != null) {
                    // 向下滑动
                    // rv滑动的距离
                    int offsetY = rv.computeVerticalScrollOffset();
                    //   竖直方向的范围
                    int extentY = rv.computeVerticalScrollExtent();
                    // 可以滑动的Y范围
                    int scrollRangeY = rv.computeVerticalScrollRange();
                    if (ev.getY() - interceptDownY > 0) {
                        if (topDistance == 0) {// 头view=0表示下拉无效
                            return false;
                        }
                        if (offsetY == 0) {
                            isIntercept = true;
                        }
                    }
                    // 向上滑动
                    else {
                        if (offsetY + extentY >= scrollRangeY) {
                            isIntercept = true;
                        }
                    }
                }
                interceptDownY = ev.getY();
                break;
        }

        return isIntercept;
    }

    private LoadListener loadListener;

    public void setLoadListener(LoadListener loadListener) {
        this.loadListener = loadListener;
    }

    private InTouchListener inTouchListener;

    public void setInTouchListener(InTouchListener inTouchListener) {
        this.inTouchListener = inTouchListener;
    }

    public interface LoadListener {
        void loadMore();

        void refresh();
    }

    public interface InTouchListener {
        void move();

        void up();
    }
}
