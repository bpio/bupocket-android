package com.bupocket.utils;

import android.graphics.Camera;
import android.graphics.Matrix;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.Transformation;
import android.view.animation.TranslateAnimation;

public class RedPacketAnimationUtils extends Animation {

    public static boolean isLoop=true;

    int centerX, centerY;
    Camera camera = new Camera();

    @Override
    public void initialize(int width, int height, int parentWidth,
                           int parentHeight) {
        super.initialize(width, height, parentWidth, parentHeight);
        //获取中心点坐标
        centerX = width / 2;
        centerY = height / 2;
        //动画执行时间  自行定义
        setDuration(1200);
        setInterpolator(new DecelerateInterpolator());
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        final Matrix matrix = t.getMatrix();
        camera.save();
        //中心是绕Y轴旋转  这里可以自行设置X轴 Y轴 Z轴
        camera.rotateY(360 * interpolatedTime);
        //把我们的摄像头加在变换矩阵上
        camera.getMatrix(matrix);
        //设置翻转中心点
        matrix.preTranslate(-centerX, -centerY);
        matrix.postTranslate(centerX, centerY);
        camera.restore();
    }


    public static void loopRotateAnimation(final View view) {
        if (isLoop==false) {
            return;
        }
        RotateAnimation animation = new RotateAnimation(3f, -3f, Animation.RELATIVE_TO_SELF,
                0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setInterpolator(new OvershootInterpolator());
        animation.setDuration(100);
        animation.setStartOffset(100);
        animation.setRepeatCount(1);
//        animation.setRepeatMode(Animation.REVERSE);
        view.startAnimation(animation);
        animation.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                TranslateAnimation translateAnimation = new TranslateAnimation(0, 0, 0, 0);
                translateAnimation.setInterpolator(new OvershootInterpolator());
                translateAnimation.setDuration(3000);
                translateAnimation.setRepeatCount(1);
                view.startAnimation(translateAnimation);
                translateAnimation.setAnimationListener(new AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        loopRotateAnimation(view);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });


    }

}
