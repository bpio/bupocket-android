package com.bupocket.activity;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.bupocket.R;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public class RedPacketActivity extends Activity {


    private Unbinder bind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_red_packet_home);
        bind = ButterKnife.bind(this);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


}
