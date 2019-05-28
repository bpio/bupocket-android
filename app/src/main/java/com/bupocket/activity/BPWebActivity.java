package com.bupocket.activity;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.bupocket.R;
import com.bupocket.base.BaseFragmentActivity;
import com.bupocket.fragment.discover.BPBannerFragment;
import com.bupocket.utils.LogUtils;
import com.qmuiteam.qmui.widget.QMUITopBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class BPWebActivity extends AppCompatActivity {


    @BindView(R.id.topbar)
    QMUITopBar mTopBar;
    @BindView(R.id.wvBanner)
    WebView wvBanner;
    @BindView(R.id.pbWeb)
    ProgressBar progressBar;


    private Unbinder bind;
    private String url;


//    @Override
//    protected int getContextViewId() {
//        return R.id.webActivity;
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_bpbanner);
        bind = ButterKnife.bind(this);
        init();
    }


    private void init() {
        initTopBar();
        initData();
        initView();
//        setListener();
    }

    private void initData() {
        url = getIntent().getExtras().getString("url");
    }

    private void initView() {
        WebSettings webSettings = wvBanner.getSettings();
        //设置WebView属性，能够执行Javascript脚本
        webSettings.setJavaScriptEnabled(true);
        //设置可以访问文件
        webSettings.setAllowFileAccess(true);
        //设置支持缩放
        webSettings.setBuiltInZoomControls(true);
//        wvBanner.setWebViewClient(new BPBannerFragment.webViewClient());
        webSettings.setAppCacheEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.supportMultipleWindows();
        webSettings.setAllowContentAccess(true);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setSavePassword(true);
        webSettings.setSaveFormData(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setLoadsImagesAutomatically(true);

        wvBanner.setWebChromeClient(new WebChromeClient());//这行最好不要丢掉
        wvBanner.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                LogUtils.e("newProgress:" + newProgress);
                if (newProgress == 100) {
                    progressBar.setVisibility(View.GONE);
                    //progressBar.setProgress(newProgress);
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    progressBar.setProgress(newProgress);//设置加载进度
                }

            }
        });//这行最好不要丢掉

        wvBanner.loadUrl(url);
    }


    private void initTopBar() {
        mTopBar.setBackgroundDividerEnabled(false);
        mTopBar.addLeftImageButton(R.mipmap.icon_tobar_left_arrow, R.id.topbar_left_arrow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }
        });
//         mTopBar.setTitle(getResources().getString(R.string.build_node_txt));

    }


    private class webViewClient extends WebViewClient {
//        public boolean shouldOverrideUrlLoading(WebView view, String url) {
//            view.loadurl(url)//返回true代表在当前webview中打开，返回false表示打开浏览器
//            return super.shouldOverrideUrlLoading(view,url);       }
//
//        @Override
//        public void onPageStarted(WebView view, String url, Bitmap favicon) {
//            if(!dialog.isShowing()) {
//                dialog.show();
//            }
//            super.onPageStarted(view, url, favicon);
//        }
//
//        @Override
//        public void onPageFinished(WebView view, String url) {
//            if(dialog.isShowing()){
//                dialog.dismiss();
//            }
//            super.onPageFinished(view, url);
//        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        wvBanner.destroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
