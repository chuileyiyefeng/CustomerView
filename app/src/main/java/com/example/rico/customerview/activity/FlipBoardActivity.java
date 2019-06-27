package com.example.rico.customerview.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.view.ViewTreeObserver;

import com.example.rico.customerview.R;
import com.example.rico.customerview.view.FlipBoardView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * Created by Tmp on 2019/5/25.
 */
public class FlipBoardActivity extends BaseActivity {
    static FlipBoardView fbv;

    @Override
    public int bindLayout() {
        return R.layout.activity_filp_borad;
    }

    static int[] res = new int[]{R.mipmap.flip_1, R.mipmap.flip_2, R.mipmap.flip_3,
            R.mipmap.flip_4, R.mipmap.flip_5};
    static ArrayList<Bitmap> bitmaps;
    static MyAsync myAsync;

    @Override
    public void doBusiness() {
        hideStatusBar();
        fbv = findViewById(R.id.fbv);
        bitmaps = new ArrayList<>();
        myAsync = new MyAsync(getApplication());
        ViewTreeObserver observer = fbv.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                fbv.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                myAsync.execute();
            }
        });


    }

    private static Bitmap scaleBitmap(Bitmap origin, int newWidth, int newHeight) {
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



     static class MyAsync extends AsyncTask {
        private WeakReference<Context> weakReference;

        private MyAsync(Context context) {
            weakReference=new WeakReference<>(context);
        }

        @Override
        protected void onPostExecute(Object o) {
            fbv.setBitmapList(bitmaps);
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            for (int re : res) {
                Bitmap bitmap = BitmapFactory.decodeResource(weakReference.get().getResources(), re);
                bitmaps.add(scaleBitmap(bitmap, fbv.getMeasuredWidth(), fbv.getMeasuredHeight()));
            }
            return null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (myAsync!=null) {
            myAsync.cancel(true);
            myAsync=null;
        }
    }
}
