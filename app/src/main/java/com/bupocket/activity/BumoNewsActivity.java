package com.bupocket.activity;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.bupocket.common.Constants;
import com.qmuiteam.qmui.widget.QMUITopBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class BumoNewsActivity extends AppCompatActivity {
    @BindView(R.id.topbar)
    QMUITopBar mTopBar;
    @BindView(R.id.pbWeb)
    ProgressBar progressBar;
    @BindView(R.id.webView)
    WebView mWebView;
    @BindView(R.id.loadFailedLL)
    LinearLayout loadFailedLL;


    private boolean isSuccess = false;
    private boolean isError = false;
    private static final String APP_CACHE_DIRNAME = "/webCache";

    private Unbinder bind;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bumo_news);

        bind = ButterKnife.bind(this);
        initTopBar();
        mWebView.loadUrl(Constants.NEWS_URL);
        mWebView.setWebViewClient(new WebViewClient() {
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
                if (mWebView.getVisibility()== View.GONE) {
                    loadFailedLL.setVisibility(View.VISIBLE);
                }

            }


            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                isError = true;
                isSuccess = false;
                if (mWebView.getVisibility()== View.GONE) {
                    loadFailedLL.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (!isError) {
                    isSuccess = true;
                    loadFailedLL.setVisibility(View.GONE);
                    mWebView.setVisibility(View.VISIBLE);

                }
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mWebView.getSettings().setSafeBrowsingEnabled(false);
        }

        WebSettings settings = mWebView.getSettings();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
        }
        mWebView.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (progressBar == null) {
                    return;
                }
                if (newProgress == 100) {
                    progressBar.setVisibility(View.GONE);
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    progressBar.setProgress(newProgress);
                }

            }
        }
        );


        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        settings.setJavaScriptEnabled(true);

        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        String cacheDirPath = getFilesDir().getAbsolutePath() + APP_CACHE_DIRNAME;
        settings.setAppCachePath(cacheDirPath);
        settings.setAppCacheEnabled(true);


    }


    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            super.onBackPressed();
        }
    }


    private void initTopBar() {
        mTopBar.setBackgroundDividerEnabled(false);
        mTopBar.addLeftImageButton(R.mipmap.icon_tobar_left_arrow, R.id.topbar_left_arrow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        mTopBar.setTitle(R.string.information_txt);
    }

}
