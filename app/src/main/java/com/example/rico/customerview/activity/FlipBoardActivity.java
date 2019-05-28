package com.example.rico.customerview.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.Toast;

import com.example.rico.customerview.R;
import com.example.rico.customerview.view.FlipBoardView;

import java.util.ArrayList;

/**
 * Created by Tmp on 2019/5/25.
 */
public class FlipBoardActivity extends BaseActivity {
    FlipBoardView fbv;

    @Override
    public int bindLayout() {
        return R.layout.activity_filp_borad;
    }

    int [] res=new int[]{R.mipmap.flip_1,R.mipmap.flip_2,R.mipmap.flip_3,R.mipmap.flip_4,R.mipmap.flip_5};
    ArrayList<Bitmap> bitmaps;
    @Override
    public void doBusiness() {
        hideStatusBar();
        fbv = findViewById(R.id.fbv);
        bitmaps=new ArrayList<>();
        for (int resId : res) {
            bitmaps.add(BitmapFactory.decodeResource(getResources(),resId));
        }
        fbv.setBitmapList(bitmaps);
        fbv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fbv.nextPage();
            }
        });
    }
}
