package com.example.rico.customerview.layoutManager;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Tmp on 2019/7/3.
 * 简单的竖直linearLayoutManager
 */
public class SimpleLinearManager extends RecyclerView.LayoutManager {

//    LayoutManager的布局Api
    //找recycler要一个childItemView,我们不管它是从scrap里取，还是从RecyclerViewPool里取，亦或是onCreateViewHolder里拿。
//    View view = recycler.getViewForPosition(xxx);  //获取postion为xxx的View
//    addView(view);//将View添加至RecyclerView中，
//    addView(child, 0);//将View添加至RecyclerView中，childIndex为0，但是View的位置还是由layout的位置决定，该方法在逆序layout子View时有大用
//    measureChildWithMargins(scrap, 0, 0);//测量View,这个方法会考虑到View的ItemDecoration以及Margin

    //    //将ViewLayout出来，显示在屏幕上，内部会自动追加上该View的ItemDecoration和Margin。此时我们的View已经可见了
//    layoutDecoratedWithMargins(view, leftOffset, topOffset,
//                               leftOffset + getDecoratedMeasuredWidth(view),
//    topOffset + getDecoratedMeasuredHeight(view));
    //    getChildCount是查看当前屏幕的item数量

//    LayoutManager的回收Api
//    detachAndScrapAttachedViews(recycler);//detach轻量回收所有View
//
//    detachAndScrapView(view, recycler);//detach轻量回收指定View
//
//    // recycle真的回收一个View ，该View再次回来需要执行onBindViewHolder方法
//    removeAndRecycleView(View child, Recycler recycler)
//    removeAndRecycleAllViews(Recycler recycler);
//    detachView(view);//超级轻量回收一个View,马上就要添加回来
//    attachView(view);//将上个方法detach的View attach回来
//    recycler.recycleView(viewCache.valueAt(i));//detachView 后 没有attachView的话 就要真的回收掉他们


    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    //    第一个可见的view，最后一个可见的view 下标
    private int firstPos, lastPos,firstTop;

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        // 先判断itemCount是否为空，state.isPreLayout()是判断之前布局时动画有没有处理结束
        if (getItemCount() == 0 && state.isPreLayout()) {
            return;
        }
        detachAndScrapAttachedViews(recycler);
        lastPos = getItemCount();
        int childOffset = getPaddingTop() + firstTop;
        int paddingLeft = getPaddingLeft();
        int parentHeight = getHeight() - getPaddingBottom();
        for (int i = firstPos; i < lastPos; i++) {

            View childView = recycler.getViewForPosition(i);
            addView(childView);
            measureChildWithMargins(childView, 0, 0);
            int width = getDecoratedMeasuredWidth(childView);
            int height = getDecoratedMeasuredHeight(childView);

            layoutDecorated(childView, paddingLeft, childOffset, width - getPaddingRight(), childOffset + height);
            childOffset += height;
            if (childOffset > parentHeight) {
                lastPos = i;
            }
        }
    }


    @Override
    public boolean canScrollVertically() {
        return true;
    }

    private int scrollY;

    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (getChildCount() == 0) {
            return 0;
        }

//    ---注释别删---   getDecoratedLeft(childView) 子view的左边，基于recyclerView的位置，以此类推 左上右下

//        向上滑
        if (dy > 0) {
            View lastChild = getChildAt(getChildCount() - 1);
//            如果最后一个子view的位置等于itemCount的长度减一，就是view滑到底了
            if (lastChild != null) {
                if (getPosition(lastChild) == getItemCount() - 1) {
                    int gap = getHeight() - getPaddingBottom() - getDecoratedBottom(lastChild);
                    if (gap > 0) {
                        dy = -gap;
                    } else if (gap == 0) {
                        dy = 0;
                    }
                }
            }
        } else if (scrollY + dy < 0) {
            //        向下滑
//            假如之前向上滑了一段距离，scrollY为50，这时候dy滑动为-100的话，就会滑动到比view顶部还要高的位置
//            赋值dy为-scrollY，还原滑动为0（就是拉到顶部时的状态）
            dy = -scrollY;
        }
        dy = fillViews(recycler, dy);
        offsetChildrenVertical(-dy);
        scrollY += dy;
        return dy;
    }

    private int fillViews(RecyclerView.Recycler recycler, int dy) {
//        先做回收越界view的操作
        int top = getPaddingTop();
        if (getChildCount() > 0) {
            for (int i = getChildCount() - 1; i >= 0; i--) {
                View childView = getChildAt(i);
                if (childView != null) {
//                    dy>0表示此时向上滑
//                    回收顶部不在屏幕范围内的view
//                    当前view的底部减去要滑动的距离比recyclerView的顶部还要小，这时候要回收这个view
//                    回收之后firstPosition要++
                    if (dy > 0 && getDecoratedBottom(childView) - dy < top) {
                        removeAndRecycleView(childView, recycler);
                        firstPos++;
                    } else if (dy < 0) {
//                        dy<0表示此时向下滑
//                        回收底部不在屏幕范围内的view
//                        当此时view的顶部比recyclerView的底部还要大时，回收这个子view
                        if (getDecoratedTop(childView) - dy > getHeight() - getPaddingBottom()) {
                            removeAndRecycleView(childView, recycler);
                            lastPos--;
                        }
                    }
                }
            }
        }
//        重新布局view
        int left = getPaddingLeft();
        if (dy > 0) {
//            这时候假设从firstPos下标的item布局到最后一个item
            int minPos = 0;
            if (getChildCount() > 0) {
                View lastView = getChildAt(getChildCount() - 1);
                if (lastView != null) {
//                    此时是向上滑的，以当前可见的最后一个view的下标加一为起始下标，然后添加view
                    minPos = getPosition(lastView) + 1;
                    top = getDecoratedBottom(lastView);
                }
            }
            for (int i = minPos; i < getItemCount(); i++) {
//                滑动dy距离后，当前的view顶部依旧比recyclerView的底部大，这时候回收这个view
                if (top - dy > getHeight() - getPaddingBottom()) {
                    break;
                } else {
                    View view = recycler.getViewForPosition(i);
                    measureChildWithMargins(view, 0, 0);
                    int width = getDecoratedMeasuredWidth(view);
                    int height = getDecoratedMeasuredHeight(view);
                    addView(view);
                    layoutDecorated(view, left, top, left + width, top + height);
                    top += height;
                }
            }
            View lastChild = getChildAt(getChildCount() - 1);
            if (lastChild != null) {
                if (getPosition(lastChild) == getItemCount() - 1) {
                    int gap = getHeight() - getPaddingBottom() - getDecoratedBottom(lastChild);
                    if (gap > 0) {
                        dy -= gap;
                    }
                }
            }
        } else {
//           此时向下滑，要把已回收的view显示出来
            int maxPos;
//            firstPos = 0;
            if (getChildCount() > 0) {
                View firstView = getChildAt(0);
                if (firstView != null) {
                    maxPos = getPosition(firstView) - 1;
                    top = getDecoratedTop(firstView);
                    if (maxPos > -1) {
                        for (int i = maxPos; i >= 0; i--) {
                            if (top - dy > getPaddingTop()) {
                                View lastView = recycler.getViewForPosition(i);
                                measureChildWithMargins(lastView, 0, 0);
                                int height = getDecoratedMeasuredHeight(lastView);
                                int width = getDecoratedMeasuredWidth(lastView);
                                addView(lastView, 0);
                                layoutDecoratedWithMargins(lastView, left, top - height, left + width, top);
                                top -= height;
                            }else {
                                break;
                            }
                        }
                    }
                }
            }
        }

        View firstView = getChildAt(0);
        if (firstView != null) {
            firstPos = getPosition(firstView);
            firstTop = getDecoratedTop(firstView) - getPaddingTop();
        }
        return dy;
    }


}
