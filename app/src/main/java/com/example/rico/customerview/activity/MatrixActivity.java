package com.example.rico.customerview.activity;

import android.widget.RadioGroup;

import com.example.rico.customerview.R;
import com.example.rico.customerview.view.MatrixSetPolyView;

/**
 * Created by Tmp on 2019/4/25.
 */
public class MatrixActivity extends BaseActivity {
    @Override
    public int bindLayout() {
        return R.layout.activity_matrix;
    }

    @Override
    public void doBusiness() {
        final MatrixSetPolyView poly = findViewById(R.id.poly);

        RadioGroup group = findViewById(R.id.group);
        assert group != null;
        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (group.getCheckedRadioButtonId()) {
                    case R.id.point0:
                        poly.setTestPoint(0);
                        break;
                    case R.id.point1:
                        poly.setTestPoint(1);
                        break;
                    case R.id.point2:
                        poly.setTestPoint(2);
                        break;
                    case R.id.point3:
                        poly.setTestPoint(3);
                        break;
                    case R.id.point4:
                        poly.setTestPoint(4);
                        break;
                }
            }
        });
    }
}
