package com.example.rico.customerview.activity;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.rico.customerview.R;
import com.example.rico.customerview.view.ArcSeekBarView;
import com.example.rico.customerview.view.CameraChangeView;
import com.example.rico.customerview.view.EvaluatorAttrView;
import com.example.rico.customerview.view.EvaluatorMoveView;
import com.example.rico.customerview.view.FillTypeView;
import com.example.rico.customerview.view.FishSwimView;
import com.example.rico.customerview.view.HandWritingView;
import com.example.rico.customerview.view.ImageTextView;
import com.example.rico.customerview.view.MiniSunView;
import com.example.rico.customerview.view.NetColorView;
import com.example.rico.customerview.view.ObjectAnimView;
import com.example.rico.customerview.view.PageTurningView;
import com.example.rico.customerview.view.ParallelogramView;
import com.example.rico.customerview.view.RegionClickView;
import com.example.rico.customerview.view.RingView;
import com.example.rico.customerview.view.SideTextView;
import com.example.rico.customerview.view.WaveBubbleView;

/**
 * Created by Tmp on 2019/1/8.
 */
public class AllViewActivity extends BaseActivity {
    LinearLayout llALl;

    @Override
    public int bindLayout() {
        return R.layout.activity_all;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void doBusiness() {
        llALl = findViewById(R.id.ll_content);
        int type = getIntent().getIntExtra("type", -1);
        View view;
        Context context = AllViewActivity.this;
        switch (type) {
            case 1:
                view = new FishSwimView(context);
                break;
            case 2:
                view = new FillTypeView(context);
                break;
            case 3:
                view = new RegionClickView(context);
                break;
            case 4:
                view = new HandWritingView(context);
                break;
            case 5:
                view = new ArcSeekBarView(context);
                break;
            case 6:
                view = new WaveBubbleView(context);
                break;
            case 7:
                view = new EvaluatorMoveView(context);
                break;
            case 8:
                view = new EvaluatorAttrView(context);
                break;
            case 9:
                view = new PageTurningView(context);
                break;
            case 10:
                view = new NetColorView(context);
                break;
            case 11:
                view = new MiniSunView(context);
                break;
            case 12:
                view = new RingView(context);
                RingView view1 = (RingView) view;
                view1.addData(R.color.colorAccent, 50)
                        .addData(R.color.button_bg, 60)
                        .addData(R.color.gray_normal, 88)
                        .addData(R.color.blue_thumb, 50)
                        .addData(R.color.darkgray, 12)
                        .addData(R.color.blueviolet, 40)
                        .refreshView();
                llALl.addView(view1);
                return;
            case 13:
                view = new ParallelogramView(context);
                break;
            case 14:
                view = new ImageTextView(context);
                break;
            case 15:
                view = new ObjectAnimView(context);
                ObjectAnimView animView = (ObjectAnimView) view;
                ObjectAnimator animator = ObjectAnimator.ofInt(animView, "radius", dpToPx(50));
                animator.setStartDelay(300);
                animator.start();
                break;
            case 16:
                view = new CameraChangeView(context);
                CameraChangeView cameraChangeView = (CameraChangeView) view;
                cameraChangeView.startAnim();
                break;
            case 17:
                view = new SideTextView(context);
                SideTextView sideTextView = (SideTextView) view;
                sideTextView.addColorText("先帝创业未半而中道崩殂，今天下三分，益州疲弊，此诚危急存亡之秋也。", R.color.red, () -> Toast.makeText(context, "红红红", Toast.LENGTH_SHORT).show())
                        .addColorText("然侍卫之臣不懈于内，忠志之士忘身于外者，盖追先帝之殊遇，欲报之于陛下也。诚宜开张圣听，", R.color.black, () -> Toast.makeText(context, "黑黑黑", Toast.LENGTH_SHORT).show())
                        .addColorText("<test english>",R.color.black)
                        .addColorText("以光先帝遗德，恢弘志士之气，不宜妄自菲薄，引喻失义，以塞忠谏之路也。", R.color.text_select_color, () -> Toast.makeText(context, "蓝蓝蓝", Toast.LENGTH_SHORT).show())
                        .addColorText("无点击事件。",R.color.black)
                        .create();
                break;
            default:
                view = new View(context);
                break;
        }
        llALl.addView(view);
    }

    private int dpToPx(int dp) {
        return (int) (getResources().getDisplayMetrics().density * dp + 0.5f);
    }

}
