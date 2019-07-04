package com.bupocket.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.GridLayout;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.bupocket.R;
import com.bupocket.base.BaseFragment;
import com.bupocket.utils.SharedPreferencesHelper;
import com.bupocket.utils.TO;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BPCreateWalletShowMneonicCodeFragment extends BaseFragment {
    @BindView(R.id.topbar)
    QMUITopBarLayout mTopBar;
    @BindView(R.id.showMneonicCodeGL)
    GridLayout mShowMneonicCodeGL;
    @BindView(R.id.go2ConfirmMneonicCodeBtn)
    QMUIRoundButton mGo2ConfirmMneonicCodeBtn;

    private SharedPreferencesHelper sharedPreferencesHelper;

    @Override
    protected View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_create_wallet_show_mneonic_code, null);
        ButterKnife.bind(this, root);
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
        initTopBar();
        initData();
        printMnemonicCode();
        submit();
        return root;
    }

    private void initData() {
        sharedPreferencesHelper = new SharedPreferencesHelper(getContext(), "buPocket");
    }


    private void submit(){
        mGo2ConfirmMneonicCodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                BPCreateWalletConfirmMnemonicCodeFragment createWalletConfirmMneonicCodeFragment = new BPCreateWalletConfirmMnemonicCodeFragment();
                Bundle argz = new Bundle();
                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_SECURE);
                List<String> mneonicCodeList = getArguments().getStringArrayList("mneonicCodeList");
                argz.putStringArrayList("mneonicCodeList", (ArrayList<String>) mneonicCodeList);
                createWalletConfirmMneonicCodeFragment.setArguments(argz);
                startFragment(createWalletConfirmMneonicCodeFragment);
            }
        });
    }


    private List<String> getMneonicCode(){
        return getArguments().getStringArrayList("mneonicCodeList");
    }

    private void printMnemonicCode() {
        mShowMneonicCodeGL.setColumnCount(4);
        mShowMneonicCodeGL.setRowCount(3);
        for (int i = 0; i < 12; i++) {
            Context mContext = getContext();
            TextView textView = new TextView(mContext);
            textView.setGravity(Gravity.CENTER);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            textView.setText(getMneonicCode().get(i));
            textView.setTextColor(getResources().getColor(R.color.app_txt_color_gray));
            textView.setBackgroundColor(getResources().getColor(R.color.app_bg_color_gray));
            GridLayout.Spec rowSpec = GridLayout.spec(i / 4, 1.0f);
            GridLayout.Spec columnSpec = GridLayout.spec(i % 4, 1.0f);
            GridLayout.LayoutParams params = new GridLayout.LayoutParams(rowSpec, columnSpec);
            params.height = TO.dip2px(mContext, 40f);
            params.width = TO.dip2px(mContext, 60f);
            params.bottomMargin = TO.dip2px(mContext, 10f);
            params.leftMargin = TO.dip2px(mContext, 5f);
            params.rightMargin = TO.dip2px(mContext, 5f);

            mShowMneonicCodeGL.addView(textView, params);

        }

    }
    private void initTopBar() {
        mTopBar.setBackgroundDividerEnabled(false);
        mTopBar.addLeftImageButton(R.mipmap.icon_tobar_left_arrow, R.id.topbar_left_arrow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_SECURE);
                popBackStack();
            }
        });
    }

}
