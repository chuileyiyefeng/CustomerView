package com.example.rico.customerview.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

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
        for (int i = 0; i < res.length; i++) {
            bitmaps.add(BitmapFactory.decodeResource(getResources(),res[i]));
        }
        fbv.setBitmapList(bitmaps);
    }
}
