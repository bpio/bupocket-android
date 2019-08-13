package com.bupocket.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import com.bupocket.R;
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
    @BindView(R.id.loadFailedLL)
    LinearLayout loadFailedLL;

    private Unbinder bind;
    private String url;
    private static boolean isNetError=false;

    private boolean isSuccess = false;
    private boolean isError = false;

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
    }

    private void initData() {
        url = getIntent().getExtras().getString("url");
    }

    private void initView() {
        WebSettings webSettings = wvBanner.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setPluginState(WebSettings.PluginState.ON);
        webSettings.setAllowFileAccess(true);
        webSettings.setBuiltInZoomControls(true);
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

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
            webSettings.setDisplayZoomControls(false);
        }


        wvBanner.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (newProgress == 100) {
                    progressBar.setVisibility(View.GONE);
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    progressBar.setProgress(newProgress);
                }

            }
        });

        wvBanner.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                isError = true;
                isSuccess = false;
                if (wvBanner.getVisibility()== View.GONE) {
                    loadFailedLL.setVisibility(View.VISIBLE);
                }
            }


            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                isError = true;
                isSuccess = false;
                if (wvBanner.getVisibility()== View.GONE) {
                    loadFailedLL.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (!isError) {
                    isSuccess = true;
                    loadFailedLL.setVisibility(View.GONE);
                    wvBanner.setVisibility(View.VISIBLE);

                }
            }
        });

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
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        wvBanner.destroy();
    }
}
