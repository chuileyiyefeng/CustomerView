package com.example.rico.customerview.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.rico.customerview.R;
import com.example.rico.customerview.bean.SignData;

import java.util.ArrayList;

public class ScrollSignView extends ViewGroup implements View.OnClickListener {
    int defaultMargin = dpToPx(20);
    int leftMargin = defaultMargin, topMargin = defaultMargin, rightMargin = defaultMargin, bottomMargin = defaultMargin;

    public ScrollSignView(Context context) {
        this(context, null);
    }

    public ScrollSignView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScrollSignView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    // 当前view绘制的rect,view移动时的参照rect
    private ArrayList<RectF> rectS;

    // 显示的数据

    private ArrayList<SignData> signDataList;

    // 最左、最上、最右、最下的点
    private float minLeftPoint = Integer.MAX_VALUE, minTopPoint = Integer.MAX_VALUE, maxRightPoint = Integer.MIN_VALUE, maxBottomPoint = Integer.MIN_VALUE;
    // 最小的左边距、上边距 最大的右边距、下边距
    private int minLeftPosition, minTopPosition, maxRightPosition, maxBottomPosition;

    private RectF itemRectF;
    // 父控件是否包含子控件,即子控件都在父控件范围内
    boolean isContainChild;
    // 子控件范围大于父控件,即子控件都在父控件范围外
    boolean isLargeParent;


    // 子控件之间边距
    int childRightMargin, childTopMargin, childBottomMargin;

    // 父控件宽度中点、高度中点
    int centerX, centerY;

    // 连线的点集合
    private ArrayList<PointF> pointList;

    Path path;
    Paint paint;


    public void setSignDataList(ArrayList<SignData> signDataList) {
        this.signDataList = signDataList;
        childRightMargin = dpToPx(10);
        childTopMargin = dpToPx(20);
        childBottomMargin = dpToPx(20);
        post(() -> {
            parentWidth = getRight() - getLeft();
            parentHeight = getBottom() - getTop();
            centerX = parentWidth / 2;
            centerY = parentHeight / 2;

            for (int i = 0; i < signDataList.size(); i++) {
                // 先得到子view的宽高，再来排布
                SignData data = signDataList.get(i);
                data.setPosition(i);
                View view = inflate(getContext(), R.layout.item_sign, null);
                TextView tvContentTop = view.findViewById(R.id.tv_content_top);
                TextView tvContentBottom = view.findViewById(R.id.tv_content_bottom);
                TextView tvTitle = view.findViewById(R.id.tv_title);
                tvTitle.setText(data.getTitle());
                tvContentTop.setText(data.getMessage());
                tvContentBottom.setText(data.getMessage());
                tvContentBottom.setTag(data);
                tvContentTop.setTag(data);
                tvContentBottom.setOnClickListener(this);
                tvContentTop.setOnClickListener(this);
                int randomValue = get20Random();
                if (randomValue > dpToPx(10)) {
                    tvContentBottom.setVisibility(GONE);
                }// 在下半部分
                else {
                    tvContentTop.setVisibility(GONE);
                }
                addView(view);
                measureChild(view, parentWidth, parentHeight);
                int childWidth = view.getMeasuredWidth();
                int childHeight = view.getMeasuredHeight();
                View signView = view.findViewById(R.id.view_sign);
                // 当前的连线点这个数据是偏移量
                PointF pointF = new PointF();
                pointF.x = signView.getMeasuredWidth() / 2.0f;
                View childParent = (View) signView.getParent();
                if (tvContentBottom.getVisibility() == View.GONE) {
                    pointF.y = childHeight - childParent.getMeasuredHeight() / 2.0f;
                } else {
                    pointF.y = childParent.getMeasuredHeight() / 2.0f;
                }
                pointList.add(pointF);
                // 设置子view的起始位置
                if (i == 0) {
                    data.setLeft(-leftMargin);
                    data.setTop((parentHeight - childHeight) / 2 + get20Random());
                }//后续子view的位置
                else {
                    // 上一个子view的位置
                    RectF lastRect = rectS.get(i - 1);
                    // 随机排序在父view的上半部分还是下半部分

                    // 把当前这个view放在上半部分

                    if (randomValue > dpToPx(10)) {
                        //  比当前view更加上
                        if (lastRect.top <= centerY) {
                            data.setTop((int) (lastRect.top - childHeight - get20Random() - childTopMargin));
                            data.setLeft((int) lastRect.right + childRightMargin);
                        } else {

                            data.setTop(centerY - childHeight - get20Random());
                            data.setLeft((int) ((int) lastRect.left + childRightMargin + (lastRect.right - lastRect.left) / 2));
                            resetContain(i, data, childWidth, childHeight);
                        }

                    }// 在下半部分
                    else {
                        // 比当前view更加下
                        if (lastRect.bottom >= centerY) {
                            data.setTop((int) (lastRect.bottom + get20Random() + childBottomMargin));
                            data.setLeft((int) lastRect.right + childRightMargin);
                        } else {
                            data.setTop(centerY + get20Random());
                            data.setLeft((int) ((int) lastRect.left + childRightMargin + (lastRect.right - lastRect.left) / 2));
                            resetContain(i, data, childWidth, childHeight);
                        }
                    }
                }
                if (minLeftPoint >= data.getLeft()) {
                    minLeftPoint = data.getLeft();
                    minLeftPosition = i;
                }
                if (maxRightPoint <= data.getLeft() + childWidth) {
                    maxRightPoint = data.getLeft() + childWidth;
                    maxRightPosition = i;
                }
                if (minTopPoint >= data.getTop()) {
                    minTopPoint = data.getTop();
                    minTopPosition = i;
                }
                if (maxBottomPoint <= data.getTop() + childHeight) {
                    maxBottomPoint = data.getTop() + childHeight;
                    maxBottomPosition = i;
                }
                RectF rect = new RectF();
                int left = data.getLeft();
                int top = data.getTop();
                rect.set(left, top, left + childWidth, top + childHeight);

                rectS.add(rect);
            }
            checkContain();

            // 添加完子view后要判断子view的范围，然后让子view是否在屏幕外或者在屏幕内
            // 都不在的话，重新设置范围
            resetChildRange();
            requestLayout();
            // 设置连线点的位置
            setPointPosition();
        });
    }


    // 重新设置数据
    public void reSetSignDataList(ArrayList<SignData> signDataList) {
        removeAllViews();
        downX = 0;
        downY = 0;
        mDownX = 0;
        mDownY = 0;
        // 重置最大最小位置
        minLeftPoint = Integer.MAX_VALUE;
        minTopPoint = Integer.MAX_VALUE;
        maxRightPoint = Integer.MIN_VALUE;
        maxBottomPoint = Integer.MIN_VALUE;
        lastClickPosition = -1;
        
        this.signDataList = signDataList;
        rectS = new ArrayList<>();
        path = new Path();
        pointList = new ArrayList<>();
        setSignDataList(signDataList);
    }

    // 重新设置子view的位置
    private void resetChildRange() {
        if (!isContainChild && !isLargeParent) {
            float itemRowWidth = itemRectF.right - itemRectF.left;
            float itemRowHeight = itemRectF.bottom - itemRectF.top;
            // 每个子view需要移动的距离
            float distanceX, distanceY;

            // 如果item范围比父view小，只需要整体移动就行，并把子view居中放置
            // 居中起始点 x、y
            float startX = (parentWidth - itemRowWidth) / 2;
            float startY = (parentHeight - itemRowHeight) / 2;

            if (itemRowWidth <= parentWidth && itemRowHeight <= parentHeight) {
                centerItem(startX, startY);
                isContainChild = true;
            } else {
                if (rectS.size() < 1) {
                    return;
                }
                boolean needStretchX = false, needStretchY = false;

                // y轴的拉伸距离，分两个方向
                float distanceTopY = 0, distanceBottomY = 0;

                // 需要拉伸宽度
                if (parentWidth > itemRowWidth) {
                    needStretchX = true;
                    distanceX = (parentWidth - itemRowWidth) / rectS.size() - 1;
                }// 不需要拉伸
                else {
                    distanceX = startX - rectS.get(minLeftPosition).left;
                }
                // 需要拉伸高度
                if (parentHeight > itemRowHeight) {
                    needStretchY = true;
                    float minTop = rectS.get(minTopPosition).top;
                    float maxBottom = rectS.get(maxBottomPosition).bottom;
                    if (minTop > centerY || maxBottom < centerY) {
                        centerY = (int) ((maxBottom + minTop) / 2);
                    }
                    distanceTopY = -topMargin - minTop;
                    distanceBottomY = parentHeight + bottomMargin - maxBottom;
                    distanceY = distanceTopY + distanceBottomY;
                }// 不需要拉伸
                else {
                    distanceY = -rectS.get(minTopPosition).top;
                }

                // 重置最大最小位置
                minLeftPoint = Integer.MAX_VALUE;
                minTopPoint = Integer.MAX_VALUE;
                maxRightPoint = Integer.MIN_VALUE;
                maxBottomPoint = Integer.MIN_VALUE;

                for (int i = 0; i < rectS.size(); i++) {
                    RectF rectF = rectS.get(i);
                    if (rectF.right < centerX && needStretchX) {
                        distanceX = -distanceX;
                    }
                    if (needStretchY) {
                        if (rectF.bottom < centerY) {
                            distanceY = distanceTopY;
                        } else {
                            distanceY = distanceBottomY;
                        }
                    }
                    rectF.left = rectF.left + distanceX;
                    rectF.right = rectF.right + distanceX;
                    rectF.top = rectF.top + distanceY;
                    rectF.bottom = rectF.bottom + distanceY;

                    if (minLeftPoint >= rectF.left) {
                        minLeftPoint = rectF.left;
                        minLeftPosition = i;
                    }
                    if (maxRightPoint <= rectF.right) {
                        maxRightPoint = rectF.right;
                        maxRightPosition = i;
                    }
                    if (minTopPoint >= rectF.top) {
                        minTopPoint = rectF.top;
                        minTopPosition = i;
                    }
                    if (maxBottomPoint <= rectF.bottom) {
                        maxBottomPoint = rectF.bottom;
                        maxBottomPosition = i;
                    }

                }
                itemRectF = new RectF(minLeftPoint, minTopPoint, maxRightPoint, maxBottomPoint);
                itemRowWidth = itemRectF.right - itemRectF.left;
                itemRowHeight = itemRectF.bottom - itemRectF.top;
                // 把子view居中放置
                // 居中起始点 x、y
                startX = (parentWidth - itemRowWidth) / 2;
                startY = (parentHeight - itemRowHeight) / 2;
                centerItem(startX, startY);
                isLargeParent = true;
            }
        }
    }

    private void setPointPosition() {
        for (int i = 0; i < pointList.size(); i++) {
            PointF pointF = pointList.get(i);
            pointF.x = rectS.get(i).left + pointF.x;
            pointF.y = rectS.get(i).top + pointF.y;
        }
    }

    // 把子view居中
    private void centerItem(float startX, float startY) {
        float distanceX = startX - rectS.get(minLeftPosition).left;
        float distanceY = startY - rectS.get(minTopPosition).top;
        for (int i = 0; i < rectS.size(); i++) {
            RectF rectF = rectS.get(i);
            rectF.left = rectF.left + distanceX;
            rectF.right = rectF.right + distanceX;
            rectF.top = rectF.top + distanceY;
            rectF.bottom = rectF.bottom + distanceY;
        }
    }

    // 判断子view对于父view属性
    private void checkContain() {
        itemRectF = new RectF(minLeftPoint, minTopPoint, maxRightPoint, maxBottomPoint);

        isContainChild = itemRectF.left >= leftMargin && itemRectF.right <= parentWidth - rightMargin
                && itemRectF.top >= topMargin && itemRectF.bottom <= parentHeight - bottomMargin;

        isLargeParent = itemRectF.left < leftMargin && itemRectF.right > parentWidth - rightMargin
                && itemRectF.top < topMargin && itemRectF.bottom > parentHeight - bottomMargin;
    }

    // 判断是否有相交点，然后重新设置位置
    private void resetContain(int i, SignData data, int childWidth, int childHeight) {
        boolean isIntersect = false;
        int maxRight = Integer.MIN_VALUE;
        // 判断矩形是否相交 可能有多个相交点
        // 当前view的左上 右上 左下 右下4个点
        Point point1 = new Point(data.getLeft(), data.getTop());
        Point point2 = new Point(data.getLeft() + childWidth, data.getTop());
        Point point3 = new Point(data.getLeft(), data.getTop() + childHeight);
        Point point4 = new Point(data.getLeft() + childWidth, data.getTop() + childHeight);
        for (int k = 0; k < i; k++) {
            RectF rectF = rectS.get(k);
            boolean b1 = rectContainPoint(rectF, point1);
            boolean b2 = rectContainPoint(rectF, point2);
            boolean b3 = rectContainPoint(rectF, point3);
            boolean b4 = rectContainPoint(rectF, point4);
            // 有一个点在，说明包含了
            if (!isIntersect) {
                isIntersect = b1 || b2 || b3 || b4;
            }
            if (maxRight <= rectF.right) {
                maxRight = (int) (rectF.right + (rectF.right - rectF.left) / 2);
            }
        }
        if (isIntersect) {
            data.setLeft(maxRight + childRightMargin);
        }
    }

    private boolean rectContainPoint(RectF rectF, Point point) {
        return point.x >= rectF.left && point.x <= rectF.right && point.y >= rectF.top && point.y <= rectF.bottom;
    }

    private void init(Context context) {
        rectS = new ArrayList<>();
        signDataList = new ArrayList<>();
        pointList = new ArrayList<>();
        path = new Path();
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.parseColor("#ffffff"));
        DashPathEffect pathEffect = new DashPathEffect(new float[]{6, 10}, 20);
        paint.setPathEffect(pathEffect);
        paint.setStyle(Paint.Style.STROKE);
        ViewConfiguration configuration = ViewConfiguration.get(context);
        mTouchSlop = configuration.getScaledTouchSlop();
    }

    // 获取 dp 20以内的随机距离
    private int get20Random() {
        return dpToPx((int) (Math.random() * 20));
    }

    int parentWidth, parentHeight;

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        parentWidth = w;
        parentHeight = h;
    }


    //  用view的TranslationZ属性来实现层叠效果
    //  onLayout先排列view，然后置顶view
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            RectF rect = rectS.get(i);
            child.layout((int) rect.left, (int) rect.top, (int) rect.right, (int) rect.bottom);
        }
    }


    float downX, downY;

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        performClick();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = event.getX();
                downY = event.getY();
                Log.e("down", "onTouchEvent: " + downY + " " + downY);
                break;
            case MotionEvent.ACTION_MOVE:
                float moveX = downX - event.getX();
                float moveY = downY - event.getY();
                downX = event.getX();
                downY = event.getY();
                moveView(-moveX, -moveY);
                break;
        }
        return true;
    }

    float mDownX, mDownY, mTouchSlop;
    boolean isIntercept;

    //     滑动的时候拦截事件
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_UP:
                isIntercept = false;
                mDownX = ev.getX();
                mDownY = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                isIntercept = isIntercept(ev);
                downX = ev.getX();
                downY = ev.getY();
                break;
        }
        return isIntercept;
    }


    public boolean isIntercept(MotionEvent ev) {
        float moveX = ev.getX();
        float moveY = ev.getY();
        Log.e("isIntercept", "moveView: " + (moveX - mDownX) + " " + (moveY - mDownY));
        return (Math.abs(moveY - mDownY) > mTouchSlop || (Math.abs(moveX - mDownX) > mTouchSlop));
    }

    private int dpToPx(int dp) {
        return (int) (getResources().getDisplayMetrics().density * dp + 0.5f);
    }

    // 改变view的位置  手指向右下参数为正数 左上为负数
    private void moveView(float distanceX, float distanceY) {
        Log.e("moveDistance", "moveView: " + distanceX + " " + distanceY);
        for (int i = 0; i < getChildCount(); i++) {
            RectF rect = rectS.get(i);
            float left = rect.left;
            float top = rect.top;
            float right = rect.right;
            float bottom = rect.bottom;
            // 如果子view在父控件里面
            if (isContainChild) {
                if (i == minLeftPosition) {
                    if (left + distanceX < leftMargin) {
                        distanceX = defaultMargin - left;
                    }
                }
                if (i == minTopPosition) {
                    if (top + distanceY < topMargin) {
                        distanceY = topMargin - top;
                    }
                }
                if (i == maxRightPosition) {
                    if (right + distanceX > parentWidth - rightMargin) {
                        distanceX = parentWidth - defaultMargin - right;
                    }
                }
                if (i == maxBottomPosition) {
                    if (bottom + distanceY > parentHeight - bottomMargin) {
                        distanceY = parentHeight - bottomMargin - bottom;
                    }
                }
            }// 如果父控件小于子控件
            else if (isLargeParent) {
                if (i == minLeftPosition) {
                    if (left + distanceX > leftMargin) {
                        distanceX = defaultMargin - left;
                    }
                }
                if (i == minTopPosition) {
                    if (top + distanceY > topMargin) {
                        distanceY = topMargin - top;
                    }
                }
                if (i == maxRightPosition) {
                    if (right + distanceX < parentWidth - rightMargin) {
                        distanceX = parentWidth - defaultMargin - right;
                    }
                }
                if (i == maxBottomPosition) {
                    if (bottom + distanceY < parentHeight - bottomMargin) {
                        distanceY = parentHeight - bottomMargin - bottom;
                    }
                }
            } else {
                // 左在屏幕内，右在屏幕外
//                if (i == maxRightPosition) {
//                    boolean isLeft;
//                    if (itemRectF.left > leftMargin && itemRectF.right > parentWidth - rightMargin) {
//                        if (right + distanceX < parentWidth - rightMargin) {
//                            distanceX = parentWidth - defaultMargin - right;
//                        }
//                    }
//                }
                // 右在屏幕内，左在屏幕外
//                if (i == minLeftPosition) {
//                    if (itemRectF.left < leftMargin && itemRectF.right > parentWidth - rightMargin) {
//
//                    }
//                }
                // 左右都在屏幕外

                // 左右都在屏幕内
            }
        }
        for (int i = 0; i < getChildCount(); i++) {
            RectF rect = rectS.get(i);
            float width = rect.right - rect.left;
            float height = rect.bottom - rect.top;
            rect.left = rect.left + distanceX;
            rect.right = rect.left + width;
            rect.top = rect.top + distanceY;
            rect.bottom = rect.top + height;
            PointF pointF = pointList.get(i);
            pointF.x += distanceX;
            pointF.y += distanceY;
        }
        requestLayout();
    }


    @Override
    protected void dispatchDraw(Canvas canvas) {
        path.reset();
        for (int i = 0; i < pointList.size(); i++) {
            PointF point = pointList.get(i);
            if (i == 0) {
                path.moveTo(point.x, point.y);
            } else {
                path.lineTo(point.x, point.y);
            }
        }
        canvas.drawPath(path, paint);
        super.dispatchDraw(canvas);
    }

    /**
     * 让ViewGroup能够支持margin属性
     */
    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new ViewGroup.MarginLayoutParams(getContext(), attrs);
    }

    private int lastClickPosition = -1;

    @Override
    public void onClick(View view) {
        SignData data = (SignData) view.getTag();
        int currentPosition = data.getPosition();
        if (listener != null) {
            listener.click(data);
        }
        if (lastClickPosition == currentPosition) {
            return;
        }
        setColor(currentPosition, "#90FF0000");
        if (lastClickPosition != -1) {
            setColor(lastClickPosition, "#90000000");
        }
        lastClickPosition = currentPosition;
    }


    private void setColor(int position, String color) {
        View lastParentView = getChildAt(position);
        TextView tvContentTop = lastParentView.findViewById(R.id.tv_content_top);
        TextView tvContentBottom = lastParentView.findViewById(R.id.tv_content_bottom);
        tvContentTop.setBackgroundColor(Color.parseColor(color));
        tvContentBottom.setBackgroundColor(Color.parseColor(color));
    }

    SignClickListener listener;

    // 设置view点击监听
    public void setListener(SignClickListener listener) {
        this.listener = listener;
    }

    public interface SignClickListener {
        void click(SignData data);
    }
}
