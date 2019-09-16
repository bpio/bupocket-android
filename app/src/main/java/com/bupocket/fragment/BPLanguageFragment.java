package com.bupocket.fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bupocket.R;
import com.bupocket.base.BaseFragment;
import com.bupocket.enums.LanguageEnum;
import com.bupocket.utils.LocaleUtil;
import com.bupocket.utils.SharedPreferencesHelper;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BPLanguageFragment extends BaseFragment{
    @BindView(R.id.topbar)
    QMUITopBarLayout mTopBar;
    @BindView(R.id.languageCNRL)
    RelativeLayout mLanguageCNRL;
    @BindView(R.id.languageENRL)
    RelativeLayout mLanguageENRL;
    @BindView(R.id.cnSelectedIV)
    ImageView mCnSelectedIV;
    @BindView(R.id.enSelectedIV)
    ImageView mEnSelectedIV;

    private int currentLanguage = LanguageEnum.UNDEFINED.getId();

    @Override
    protected View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_language, null);
        QMUIStatusBarHelper.setStatusBarLightMode(getBaseFragmentActivity());
        ButterKnife.bind(this, root);
        getLanguage();
        initTopBar();
        mLanguageCNRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentLanguage = LanguageEnum.CHINESE.getId();
                LocaleUtil.changeAppLanguage(getContext(), currentLanguage);
                mCnSelectedIV.setBackgroundResource(R.mipmap.icon_language_selected);
                mEnSelectedIV.setBackgroundResource(R.color.app_color_white);
            }
        });
        mLanguageENRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentLanguage = LanguageEnum.ENGLISH.getId();
                LocaleUtil.changeAppLanguage(getContext(), currentLanguage);
                mCnSelectedIV.setBackgroundResource(R.color.app_color_white);
                mEnSelectedIV.setBackgroundResource(R.mipmap.icon_language_selected);
            }
        });
        return root;
    }

    private void getLanguage() {
        int language = SharedPreferencesHelper.getInstance().getInt("currentLanguage", currentLanguage);
        if(LanguageEnum.UNDEFINED.getId() == language){
            String myLocaleStr = getContext().getResources().getConfiguration().locale.getLanguage();
            switch (myLocaleStr){
                case "zh": {
                    language = LanguageEnum.CHINESE.getId();
                    break;
                }
                case "en": {
                    language = LanguageEnum.ENGLISH.getId();
                    break;
                }
                default : {
                    language = LanguageEnum.ENGLISH.getId();
                }
            }

        }

        if(LanguageEnum.CHINESE.getId() == language){
            mCnSelectedIV.setBackgroundResource(R.mipmap.icon_language_selected);
            mLanguageCNRL.setEnabled(false);
        }else if(LanguageEnum.ENGLISH.getId() == language){
            mEnSelectedIV.setBackgroundResource(R.mipmap.icon_language_selected);
            mLanguageENRL.setEnabled(false);
        }
    }

    private void initTopBar() {
        mTopBar.setBackgroundDividerEnabled(false);
        mTopBar.setTitle(R.string.language_txt);
        mTopBar.addLeftImageButton(R.mipmap.icon_tobar_left_arrow, R.id.topbar_left_arrow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popBackStack();
            }
        });
    }
}
