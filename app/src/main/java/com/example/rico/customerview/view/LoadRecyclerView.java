package com.example.rico.customerview.view;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rico.customerview.R;
import com.example.rico.customerview.adapter.BaseAdapter;

import static androidx.recyclerview.widget.RecyclerView.ItemDecoration;

/**
 * create by pan yi on 2020/10/17
 * desc : 刷新recyclerview
 */
public class LoadRecyclerView extends LinearLayout implements BaseAdapter.DataChangeListener {
    public LoadRecyclerView(Context context) {
        this(context, null);
    }

    public LoadRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private RecyclerView rv;
    private ImageView ivLoading;
    private ConstraintLayout conEmpty;
    private JumpLoadView jumpLoadView;
    private ObjectAnimator animator;

    private void initView(Context context) {
        View contentView = View.inflate(context, R.layout.load_recycler, null);
        rv = contentView.findViewById(R.id.rv);
        ivLoading = contentView.findViewById(R.id.iv_loading);
        conEmpty = contentView.findViewById(R.id.con_empty);
        jumpLoadView = contentView.findViewById(R.id.jump_load);
        jumpLoadView.connect(rv);
        addView(contentView);
        jumpLoadView.setLoadListener(new JumpLoadView.LoadListener() {
            @Override
            public void loadMore() {
                if (loadDataListener != null) {
                    loadDataListener.loadMore();
                }
            }

            @Override
            public void refresh() {
                if (loadDataListener != null) {
                    loadDataListener.refresh();
                }
            }
        });
        conEmpty.setOnClickListener(view -> {
            if (loadDataListener != null) {
                loadDataListener.reload();
                startRefresh();
            }
        });
        animator = ObjectAnimator.ofFloat(ivLoading, "rotation", 0, 359);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setDuration(1000);
        animator.setInterpolator(new LinearInterpolator());
    }

    private void startRefresh() {
        if (animator.isStarted()) {
            animator.resume();
        }else {

            animator.start();
        }
    }

    private void stopRefresh() {
        animator.cancel();
    }

    public void showEmpty() {
        conEmpty.setVisibility(View.VISIBLE);
        stopRefresh();
    }

    public void showData() {
        conEmpty.setVisibility(View.GONE);
        jumpLoadView.reductionScroll();
    }

    public void addItemDecoration(ItemDecoration decor) {
        rv.addItemDecoration(decor);
    }

    public void setLayoutManager(RecyclerView.LayoutManager layout) {
        rv.setLayoutManager(layout);
    }


    public void setAdapter(BaseAdapter adapter) {
        rv.setAdapter(adapter);
        adapter.setDataChangeListener(this);
    }


    private LoadDataListener loadDataListener;

    public void setLoadDataListener(LoadDataListener loadDataListener) {
        this.loadDataListener = loadDataListener;
    }

    @Override
    public void change() {
        jumpLoadView.reductionScroll();
    }

    public interface LoadDataListener {
        void loadMore();

        void refresh();

        void reload();
    }
}
