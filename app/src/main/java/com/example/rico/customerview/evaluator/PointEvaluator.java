package com.example.rico.customerview.evaluator;

import android.animation.TypeEvaluator;
import android.graphics.Point;

/**
 * Created by Tmp on 2019/3/29.
 * valueAnimator所用到的类
 */
public class PointEvaluator implements TypeEvaluator {
    @Override
    public Object evaluate(float fraction, Object startValue, Object endValue) {
        Point startPoint= (Point) startValue;
        Point endPoint= (Point) endValue;
        Point point=new Point();
        point.x= (int) (startPoint.x+fraction*(endPoint.x-startPoint.x));
        point.y= (int) (startPoint.y+fraction*(endPoint.y-startPoint.y));
        return point;
    }
}
