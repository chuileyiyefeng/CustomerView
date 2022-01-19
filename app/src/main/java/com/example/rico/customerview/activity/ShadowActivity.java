package com.example.rico.customerview.activity;

import android.widget.Toast;

import com.example.rico.customerview.R;
import com.example.rico.customerview.view.SideTextView;

/**
 * create by pan yi on 2020/12/30
 * desc : 阴影
 */
public class ShadowActivity extends BaseActivity {
    @Override
    public int bindLayout() {
        return R.layout.activity_shadow;
    }

    @Override
    public void doBusiness() {
        SideTextView sideTextView=findViewById(R.id.side_text);
        sideTextView.addColorText("先帝创业未半而中道崩殂，今天下三分，益州疲弊，此诚危急存亡之秋也。", R.color.red, () -> Toast.makeText(this, "红红红", Toast.LENGTH_SHORT).show())
                .addColorText("然侍卫之臣不懈于内，忠志之士忘身于外者，盖追先帝之殊遇，欲报之于陛下也。诚宜开张圣听，", R.color.black, () -> Toast.makeText(this, "黑黑黑", Toast.LENGTH_SHORT).show())
                .addColorText("以光先帝遗德，恢弘志士之气，不宜妄自菲薄，引喻失义，以塞忠谏之路也。", R.color.text_select_color, () -> Toast.makeText(this, "蓝蓝蓝", Toast.LENGTH_SHORT).show())
                .create();
    }
}
