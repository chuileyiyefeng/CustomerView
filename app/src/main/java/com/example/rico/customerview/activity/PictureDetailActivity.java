package com.example.rico.customerview.activity;

import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import com.example.rico.customerview.R;
import com.example.rico.customerview.StatusBarUtil;
import com.example.rico.customerview.adapter.PicDetailAdapter;
import com.example.rico.customerview.adapter.PointAdapter;
import com.example.rico.customerview.bean.PicInfoBean;

/**
 * Created by Tmp on 2019/11/4.
 */
public class PictureDetailActivity extends BaseActivity {
    PicInfoBean bean;
    RecyclerView rvPic, rvPoint;
    PicDetailAdapter picAdapter;
    PointAdapter pointAdapter;
    LinearLayoutManager manager;
    PagerSnapHelper helper;

    @Override
    public int bindLayout() {
        return R.layout.activity_picture_detail;
    }

    @Override
    public void doBusiness() {
        StatusBarUtil.hideStatusBar(PictureDetailActivity.this);
        rvPic = findViewById(R.id.rv_pic_detail);
        rvPoint = findViewById(R.id.rv_point);
        helper = new PagerSnapHelper();
        picAdapter = new PicDetailAdapter(this);
        pointAdapter = new PointAdapter(this);
        manager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);

        rvPic.setLayoutManager(manager);
        helper.attachToRecyclerView(rvPic);
        bean = (PicInfoBean) getIntent().getSerializableExtra("picInfoBean");
        lastPosition = bean.getPosition();
        picAdapter.addItem(bean.getStrings());
        rvPic.setAdapter(picAdapter);
        rvPic.scrollToPosition(lastPosition);
        rvPic.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                changePosition(recyclerView, newState);
            }
        });

        rvPoint.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        for (int i = 0; i < bean.getStrings().size(); i++) {
            pointAdapter.addItem(bean.getPosition() == i);
        }
        rvPoint.setAdapter(pointAdapter);

        colorDrawable = new ColorDrawable(Color.BLACK);
        View contentView = findViewById(android.R.id.content);
        contentView.setBackground(colorDrawable);
        enterAnimation();
    }

    boolean isDragging = false;
    int lastPosition;

    private void changePosition(RecyclerView recyclerView, int newState) {
        View view = helper.findSnapView(manager);
        int position;
        if (view != null) {
            position = recyclerView.getChildLayoutPosition(view);
        } else {
            return;
        }
        if (newState == RecyclerView.SCROLL_STATE_IDLE && isDragging) {
            isDragging = false;
            pointAdapter.notifyItemChanged(lastPosition, false);
            pointAdapter.notifyItemChanged(position, true);
            lastPosition = position;
        } else if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
            isDragging = true;
        }
    }

    int DURATION = 300;
    ColorDrawable colorDrawable;

    public void enterAnimation() {
        Point point = new Point();
        getWindowManager().getDefaultDisplay().getSize(point);
        float scaleX = (bean.getMyRectList().get(lastPosition).getRight() - bean.getMyRectList().get(lastPosition).getLeft()) / (float) point.x;
        float scaleY = (bean.getMyRectList().get(lastPosition).getBottom() - bean.getMyRectList().get(lastPosition).getTop()) / (float) point.y;
        //设置view缩放中心
        rvPic.setPivotX(0);
        rvPic.setPivotY(0);
        rvPic.setTranslationX(bean.getMyRectList().get(lastPosition).getLeft());
        rvPic.setTranslationY(bean.getMyRectList().get(lastPosition).getTop());
        rvPic.setScaleX(scaleX);
        rvPic.setScaleY(scaleY);
        //设置动画
        TimeInterpolator sDecelerator = new DecelerateInterpolator();
        //设置imageview缩放动画
        rvPic.animate().setDuration(DURATION).scaleX(1).scaleY(1).
                translationX(0).translationY(0).setInterpolator(sDecelerator);

        // 设置activity主布局背景颜色DURATION毫秒内透明度从透明到不透明
        ObjectAnimator bgAnim = ObjectAnimator.ofInt(colorDrawable, "alpha", 0, 255);
        bgAnim.setDuration(DURATION);
        bgAnim.start();
    }

    public void exitAnimation(final Runnable endAction) {

//        TimeInterpolator sInterpolator = new AccelerateInterpolator();
//        //设置imageview缩放动画，以及缩放结束位置
//        iv.animate().setDuration(DURATION).scaleX(mWidthScale).scaleY(mHeightScale).
//                translationX(mLeftDelta).translationY(mTopDelta)
//                .setInterpolator(sInterpolator).withEndAction(endAction);
//
//        // 设置activity主布局背景颜色DURATION毫秒内透明度从不透明到透明
//        ObjectAnimator bgAnim = ObjectAnimator.ofInt(colorDrawable, "alpha", 0);
//        bgAnim.setDuration(DURATION);
//        bgAnim.start();
    }
}
