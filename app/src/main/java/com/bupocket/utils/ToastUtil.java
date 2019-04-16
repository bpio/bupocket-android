package com.bupocket.utils;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bupocket.R;

/**
 * MainThread   Toast
 * WorkThread   Toast
 */
public class ToastUtil {



    public static void showToast(final Activity context, final String titles, final int duration) {
        if ("main".equals(Thread.currentThread().getName())) {
            Toast.makeText(context, titles, duration).show();
        } else {
            context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, titles, duration).show();
                }
            });
        }
    }


    public static void showToast(final Activity context, final int titles, final int duration) {
        if ("main".equals(Thread.currentThread().getName())) {
            Toast.makeText(context, titles, duration).show();
        } else {
            context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, titles, duration).show();
                }
            });
        }
    }

}