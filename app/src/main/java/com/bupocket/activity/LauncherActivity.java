package com.bupocket.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.bupocket.BPMainActivity;
import com.bupocket.R;

public class LauncherActivity extends Activity {
    private static final int GOTO_MAIN_ACTIVITY = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            finish();
            return;
        }
        setContentView(R.layout.launcher_activity);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                    Intent intent = new Intent();
                    intent.setClass(LauncherActivity.this, BPMainActivity.class);
                    startActivity(intent);
                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();




    }
}
