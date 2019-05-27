package com.example.rico.customerview.activity;

import android.animation.Animator;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.example.rico.customerview.R;
import com.example.rico.customerview.fragment.Fragment1;
import com.example.rico.customerview.fragment.Fragment2;
import com.example.rico.customerview.fragment.Fragment3;

/**
 * Created by Tmp on 2019/5/24.
 * 揭示动画变换fragment
 */
public class RevealActivity extends BaseActivity implements View.OnClickListener {
    FrameLayout fl, fl_parent;
    ImageView iv1, iv2, iv3;
    FragmentManager fragmentManager;
    FragmentTransaction transaction;
    Fragment fragment1, fragment2, fragment3;

    @Override
    public int bindLayout() {
        return R.layout.activity_reveal;
    }

    @Override
    public void doBusiness() {
        fl = findViewById(R.id.frame);
        fl_parent = findViewById(R.id.frame_parent);
        iv1 = findViewById(R.id.iv1);
        iv2 = findViewById(R.id.iv2);
        iv3 = findViewById(R.id.iv3);
        iv1.setOnClickListener(this);
        iv2.setOnClickListener(this);
        iv3.setOnClickListener(this);
        fragmentManager = getSupportFragmentManager();
        transaction = fragmentManager.beginTransaction();
        fragment1 = new Fragment1();
        fragment2 = new Fragment2();
        fragment3 = new Fragment3();
        transaction.replace(R.id.frame, fragment1).commit();
    }

    @Override
    public void onClick(View v) {

        Fragment fragment = null;
        transaction = fragmentManager.beginTransaction();
        switch (v.getId()) {
            case R.id.iv1:
                fragment = fragment1;
                break;
            case R.id.iv2:
                fragment = fragment2;
                break;
            case R.id.iv3:
                fragment = fragment3;
                break;
        }
        int finalRadius = Math.max(fl.getWidth(), fl.getHeight());
        fl_parent.setBackground(new BitmapDrawable(getResources(),createBitmap(fl)));
        Animator animator = ViewAnimationUtils.createCircularReveal(fl, v.getLeft(), fl.getHeight(), 0, finalRadius);
        animator.setInterpolator(new AccelerateInterpolator());
        animator.setDuration(300);
        animator.start();
        transaction.replace(R.id.frame, fragment).commit();
    }

    private Bitmap createBitmap(View v) {
        Bitmap bmp = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmp);
//        c.drawColor(Color.WHITE);
        v.draw(c);
        return bmp;
    }
}
