package com.bupocket.fragment;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bupocket.R;
import com.bupocket.base.BaseFragment;
import com.bupocket.utils.SharedPreferencesHelper;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BPUserTermsFragment extends BaseFragment {

    @BindView(R.id.topbar)
    QMUITopBarLayout mTopBar;

//    @BindView(R.id.userTermsContentTv)
//    TextView mUserTermsContentTv;

    @BindView(R.id.agreeUserTermsCheckbox)
    CheckBox mAgreeUserTerms;

    @BindView(R.id.userTermsNextBtn)
    TextView mUserTermsNext;

    @BindView(R.id.useTermsContentWv)
    WebView mUseTermsContentWv;

    private Boolean isAgreeTerms = false;
    private int currentLanguage = -1;
    String myLocaleStr;

    @Override
    protected View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_use_terms, null);
        ButterKnife.bind(this, root);
        initTopBar();
        eventListeners();
        int language = SharedPreferencesHelper.getInstance().getInt("currentLanguage", currentLanguage);
        if (language == -1) {
            myLocaleStr = getContext().getResources().getConfiguration().locale.getLanguage();
            switch (myLocaleStr) {
                case "zh": {
                    language = 0;
                    break;
                }
                case "en": {
                    language = 1;
                    break;
                }
                default: {
                    language = 1;
                }
            }
        }
        changeLang(language);
        QMUIStatusBarHelper.setStatusBarLightMode(getBaseFragmentActivity());

        WebSettings webSettings = mUseTermsContentWv.getSettings();
        webSettings.setDefaultTextEncodingName("UTF-8");
        return root;
    }

    private String getRawFileFromResource(int resourceId) {
        StringBuilder sb = new StringBuilder();
        Scanner s = new Scanner(getResources().openRawResource(resourceId));
        while (s.hasNextLine()) {
            sb.append(s.nextLine() + "\n");
        }
        return sb.toString();
    }

    private void changeLang(int lang) {

        switch (lang) {
            case 0: {
                mUseTermsContentWv.loadData(getRawFileFromResource(R.raw.use_terms_html_cn), "text/html; charset=UTF-8", null);
                break;
            }
            case 1: {
                mUseTermsContentWv.loadData(getRawFileFromResource(R.raw.use_terms_html_en), "text/html; charset=UTF-8", null);
                break;
            }
            default: {
                mUseTermsContentWv.loadData(getRawFileFromResource(R.raw.use_terms_html_cn), "text/html; charset=UTF-8", null);
            }
        }
    }

    private void eventListeners() {
        mAgreeUserTerms.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                isAgreeTerms = isChecked;
                if (isAgreeTerms) {
                    mUserTermsNext.setEnabled(true);
                    mUserTermsNext.setBackgroundColor(getResources().getColor(R.color.app_color_main));
                } else {
                    mUserTermsNext.setEnabled(false);
                    mUserTermsNext.setBackgroundColor(getResources().getColor(R.color.terms_unagree_btn_grey));
                }
            }
        });
        mUserTermsNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (getArguments().getString("tag").equals(BPRecoverWalletFormFragment.class.getSimpleName())) {
                    startFragment(new BPRecoverWalletFormFragment());
                } else {
                    startFragment(new BPCreateWalletFormFragment());
                }
            }
        });

    }

    private void initTopBar() {
        mTopBar.setBackgroundDividerEnabled(false);
        mTopBar.addLeftImageButton(R.mipmap.icon_tobar_left_arrow, R.id.topbar_left_arrow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QMUIStatusBarHelper.setStatusBarDarkMode(getBaseFragmentActivity());
//                startFragmentAndDestroyCurrent(new HomeFragment());
                popBackStack();
            }
        });
    }
}
