package com.example.rico.customerview.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.support.annotation.IdRes;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
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

    int[] res = new int[]{R.mipmap.flip_1, R.mipmap.flip_2, R.mipmap.flip_3,
            R.mipmap.flip_4, R.mipmap.flip_5};
    ArrayList<Bitmap> bitmaps;

    @Override
    public void doBusiness() {
        hideStatusBar();
        fbv = findViewById(R.id.fbv);
        bitmaps = new ArrayList<>();
        ViewTreeObserver observer=fbv.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                fbv.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                for (int i = 0; i <res.length; i++) {
                    Bitmap bitmap = BitmapFactory.decodeResource(getResources(), res[i]);
                    bitmaps.add(scaleBitmap(bitmap,fbv.getMeasuredWidth(),fbv.getMeasuredHeight()));
                }
                fbv.setBitmapList(bitmaps);
            }
        });
    }

    private Bitmap scaleBitmap(Bitmap origin, int newWidth, int newHeight) {
        if (origin == null) {
            return null;
        }
        int height = origin.getHeight();
        int width = origin.getWidth();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        return Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false);
    }
}
