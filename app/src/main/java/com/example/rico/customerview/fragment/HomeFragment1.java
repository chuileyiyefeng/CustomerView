package com.example.rico.customerview.fragment;

import android.content.Intent;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rico.customerview.R;
import com.example.rico.customerview.activity.AllViewActivity;
import com.example.rico.customerview.activity.AnimatorSwitchActivity;
import com.example.rico.customerview.activity.BezierMoveActivity;
import com.example.rico.customerview.activity.CornerImageActivity;
import com.example.rico.customerview.activity.CurveActivity;
import com.example.rico.customerview.activity.DrawBitmapActivity;
import com.example.rico.customerview.activity.FlipBoardActivity;
import com.example.rico.customerview.activity.LoadingActivity;
import com.example.rico.customerview.activity.PuzzleActivity;
import com.example.rico.customerview.activity.RadarActivity;
import com.example.rico.customerview.activity.ShadowActivity;
import com.example.rico.customerview.activity.TextMoveActivity;
import com.example.rico.customerview.adapter.BaseAdapter;
import com.example.rico.customerview.adapter.FirstAdapter;
import com.example.rico.customerview.bean.ItemInfo;
import com.example.rico.customerview.view.MyItemDecoration;
import com.example.rico.util.itemdrag.ItemDragHelperCallback;

/**
 * Created by Tmp on 2019/6/27.
 */
public class HomeFragment1 extends BaseFragment implements BaseAdapter.ItemClick {
    RecyclerView rv;
    FirstAdapter adapter;

    @Override
    protected int bindLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        rv = (RecyclerView) findViewById(R.id.rv);
        adapter = new FirstAdapter(getActivity());
        rv.addItemDecoration(new MyItemDecoration());
        rv.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        rv.setAdapter(adapter);
        ItemDragHelperCallback callback = new ItemDragHelperCallback(adapter);
         ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(rv);

        addItem("圆角ImageView", CornerImageActivity.class);
        addItem("文字滚动", TextMoveActivity.class);
        addItem("drawBitmap动画", DrawBitmapActivity.class);
        addItem("雷达网图", RadarActivity.class);
        addItem("贝塞尔曲线", BezierMoveActivity.class);
        addItem("上滑翻页", FlipBoardActivity.class);
        addItem("switch动画", AnimatorSwitchActivity.class);
        addItem("拼图", PuzzleActivity.class);
        addItem("曲线统计图", CurveActivity.class);
        addItem("加载动画", LoadingActivity.class);
        addItem("阴影控件", ShadowActivity.class);

        addItem2("小鱼游泳(静止)", 1);
        addItem2("path填充模式", 2);
        addItem2("不同区域点击", 3);
        addItem2("写字板", 4);
        addItem2("圆弧SeekBar", 5);
        addItem2("气泡波浪", 6);
        addItem2("Evaluator(Value)", 7);
        addItem2("Evaluator(Object)", 8);
        addItem2("翻页", 9);
        addItem2("网状view", 10);
        addItem2("太阳动画", 11);
        addItem2("圆环统计图", 12);
        addItem2("平行四边形", 13);
        addItem2("图片文字", 14);
        addItem2("Object属性动画", 15);
        addItem2("Camera旋转", 16);
        addItem2("文字对齐", 17);
        adapter.addItemClick(this);
    }

    public void addItem(String name, Class<?> cls) {
        adapter.addItem(new ItemInfo(name, new Intent(getActivity(), cls)));
    }

    public void addItem2(String name, int type) {
        adapter.addItem(new ItemInfo(name, type));
    }

    @Override
    public void itemClick(int position) {
        ItemInfo info = adapter.getItem(position);
        if (info.intent != null) {
            startActivity(info.intent);
        } else {
            startActivity(new Intent(getActivity(), AllViewActivity.class).putExtra("type", info.type));
        }
    }


}
