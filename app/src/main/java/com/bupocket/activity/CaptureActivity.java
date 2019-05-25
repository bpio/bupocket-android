package com.bupocket.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bupocket.R;
import com.bupocket.base.BaseFragment;
import com.bupocket.base.BaseFragmentActivity;
import com.bupocket.common.Constants;
import com.bupocket.enums.TokenActionTypeEnum;
import com.bupocket.fragment.BPIssueTokenFragment;
import com.bupocket.fragment.BPRegisterTokenFragment;
import com.bupocket.fragment.BPSendTokenFragment;
import com.bupocket.utils.CommonUtil;
import com.bupocket.utils.ImageUtil;
import com.bupocket.utils.LocaleUtil;
import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.qmuiteam.qmui.util.QMUIViewHelper;
import com.qmuiteam.qmui.widget.QMUITopBar;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.uuzuche.lib_zxing.activity.CodeUtils;

import java.io.UnsupportedEncodingException;

import io.bumo.encryption.key.PublicKey;


public class CaptureActivity extends BaseFragmentActivity {


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleUtil.attachBaseContext(base));
    }
    /**
     * 条形码扫描管理器
     */
    private CaptureManager mCaptureManager;

    /**
     * 条形码扫描视图
     */
    private DecoratedBarcodeView mBarcodeView;

    /**
     * 标题栏
     */
    private QMUITopBar mTopbar;

    @Override
    protected int getContextViewId() {
        return R.id.captureLayout;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initWindow();
        setContentView(R.layout.activity_qrcode_capture_layout);
        initToolbar();
        mBarcodeView = (DecoratedBarcodeView)findViewById(R.id.zxing_barcode_scanner);
        mCaptureManager = new CaptureManager(this, mBarcodeView);
        mCaptureManager.initializeFromIntent(getIntent(), savedInstanceState);
        mCaptureManager.decode();
    }

    /**
     * 初始化窗口
     */
    private void initWindow() {
        // API_19及其以上透明掉状态栏
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
            layoutParams.flags = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | layoutParams.flags;
        }
    }

    /**
     * 初始化标题栏
     */
    private void initToolbar() {
        mTopbar = findViewById(R.id.topbar);
        mTopbar.setBackgroundAlpha(0);
        mTopbar.setTitle(R.string.capture_activity_title);
        mTopbar.addLeftTextButton(getResources().getString(R.string.capture_activity_cancel), QMUIViewHelper.generateViewId()).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mTopbar.addRightTextButton(getResources().getString(R.string.capture_activity_photo_album),QMUIViewHelper.generateViewId()).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, Constants.REQUEST_READ_STORAGE_PERMISSION);
                    } else {
                        Toast.makeText(getApplicationContext(), "缺少文件读取权限", Toast.LENGTH_SHORT).show();
                    }
                }
                selectImage();
            }
        });
    }

    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, Constants.REQUEST_IMAGE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCaptureManager.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mCaptureManager.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCaptureManager.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mCaptureManager.onSaveInstanceState(outState);
    }

    /**
     * 权限处理
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        mCaptureManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /**
     * 按键处理
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return mBarcodeView.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        popBackStack();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.REQUEST_IMAGE) {
            if (data != null) {
                Uri uri = data.getData();
                try {
                    CodeUtils.analyzeBitmap(ImageUtil.getImageAbsolutePath(getApplicationContext(), uri), new CodeUtils.AnalyzeCallback() {
                        @Override
                        public void onAnalyzeSuccess(Bitmap mBitmap, String result) {
                            Intent intent = new Intent();
                            intent.putExtra("resultFromBitmap",result);
                            setResult(Constants.REQUEST_IMAGE, intent);
                            popBackStack();
                        }

                        @Override
                        public void onAnalyzeFailed() {
                            Toast.makeText(getApplicationContext(), R.string.error_qr_message_txt, Toast.LENGTH_LONG).show();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
