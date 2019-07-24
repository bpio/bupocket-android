package com.bupocket.fragment;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayout;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bupocket.R;
import com.bupocket.base.BaseFragment;
import com.bupocket.fragment.home.HomeFragment;
import com.bupocket.utils.SharedPreferencesHelper;
import com.bupocket.utils.TO;
import com.bupocket.wallet.Wallet;
import com.bupocket.wallet.enums.CreateWalletStepEnum;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BPCreateWalletConfirmMnemonicCodeFragment extends BaseFragment {
    @BindView(R.id.topbar)
    QMUITopBarLayout mTopBar;
    @BindView(R.id.confirmMneonicCodeGL)
    GridLayout mConfirmMneonicCodeGL;
    @BindView(R.id.mnemonicCodeListSelected)
    TextView mMnemonicCodeListSelected;
    @BindView(R.id.completeMnemonicCodeBtn)
    QMUIRoundButton mCompleteMnemonicCodeBtn;

    private SharedPreferencesHelper sharedPreferencesHelper;
    private List<String> srcMnemonicCodeList = new ArrayList<>();

    private List<MnemonicWord> mnemonicCodeList = new ArrayList<>();
    private List<MnemonicWord> mnemonicCodeListSelected = new ArrayList<>();
    private List<String> strMnemonicCodeListSelected = new ArrayList<>();

    @Override
    protected View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_create_wallet_confirm_mneonic_code, null);
        ButterKnife.bind(this, root);
        initData();
        initTopBar();
        printMnemonicCode();
        mCompleteMnemonicCodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 校验助记词是否合法
                strMnemonicCodeListSelected.clear();
                for (MnemonicWord word : mnemonicCodeListSelected) {
                    strMnemonicCodeListSelected.add(word.getCode());
                }
                if (strMnemonicCodeListSelected.size() < 12) {
                    Toast.makeText(getActivity(), R.string.check_mneonic_code_err1, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!strMnemonicCodeListSelected.equals(srcMnemonicCodeList)) {
                    Toast.makeText(getActivity(), R.string.check_mneonic_code_err1, Toast.LENGTH_SHORT).show();
                    return;
                }
                sharedPreferencesHelper.put("createWalletStep", CreateWalletStepEnum.BACKUPED_MNEONIC_CODE.getCode());
                sharedPreferencesHelper.put("isFirstCreateWallet", "0");
                sharedPreferencesHelper.put("mnemonicWordBackupState", "0");


                if (BPCreateWalletFormFragment.isCreateWallet) {
                    popBackStackFragment();

                } else {
                    startFragment(new HomeFragment());
                }
            }
        });

        return root;
    }

    private void popBackStackFragment() {

        if (BPWalletManageFragment.isManagerFragment) {
            getFragmentManager().popBackStack(BPWalletManageFragment.class.getSimpleName(), 0);
        }else{
            getFragmentManager().popBackStack(BPCreateWalletFormFragment.class.getSimpleName(), 1);
            getFragmentManager().popBackStack(BPUserInfoFragment.class.getSimpleName(), 0);
        }

    }

    private void getMnemonicCode() {
        srcMnemonicCodeList = getArguments().getStringArrayList("mneonicCodeList");
        assert srcMnemonicCodeList != null;
        for (String code : srcMnemonicCodeList) {
            MnemonicWord mnemonicWord = new MnemonicWord();
            mnemonicWord.setWordId(UUID.randomUUID().toString());
            mnemonicWord.setCode(code);
            mnemonicCodeList.add(mnemonicWord);
        }
        Collections.shuffle(mnemonicCodeList);
    }

    private void initData() {
        sharedPreferencesHelper = new SharedPreferencesHelper(getContext(), "buPocket");
        getMnemonicCode();
    }

    private void printMnemonicCode() {


        mConfirmMneonicCodeGL.setColumnCount(4);
        mConfirmMneonicCodeGL.setRowCount(3);
        for (int i = 0; i < 12; i++) {
            Context mContext = getContext();
            TextView textView = new TextView(mContext);
            textView.setGravity(Gravity.CENTER);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            textView.setText(mnemonicCodeList.get(i).getCode());
            textView.setHint(mnemonicCodeList.get(i).getWordId());
            textView.setTextColor(getResources().getColor(R.color.app_color_green));
            textView.setBackground(getResources().getDrawable(R.drawable.shape_corner_gray_tv2));
            textView.setOnClickListener(clickListener);
            GridLayout.Spec rowSpec = GridLayout.spec(i / 4, 1.0f);
            GridLayout.Spec columnSpec = GridLayout.spec(i % 4, 1.0f);
            GridLayout.LayoutParams params = new GridLayout.LayoutParams(rowSpec, columnSpec);

            params.height = TO.dip2px(mContext, 40f);
            params.width = TO.dip2px(mContext, 60f);
            params.bottomMargin = TO.dip2px(mContext, 10f);
            params.leftMargin = TO.dip2px(mContext, 5f);
            params.rightMargin = TO.dip2px(mContext, 5f);
            mConfirmMneonicCodeGL.addView(textView, params);
        }
    }


    private void printMneonicCodeSelected() {
        StringBuffer sb = new StringBuffer();
        for (MnemonicWord mnemonicWord : mnemonicCodeListSelected
        ) {
            sb.append(mnemonicWord.getCode() + "\t\t");
        }
        mMnemonicCodeListSelected.setText(sb.toString());
    }

    private void addMneonicCodeBtn(String text) {
        TextView textView = new TextView(getContext());
        textView.setText(text);
        mConfirmMneonicCodeGL.addView(textView);
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void onClick(View v) {
            TextView textView = (TextView) v;
            String code = textView.getText().toString();
            String wordId = textView.getHint().toString();
            MnemonicWord mnemonicWord = new MnemonicWord();
            mnemonicWord.setCode(code);
            mnemonicWord.setWordId(wordId);
            if (!mnemonicCodeListSelected.contains(mnemonicWord)) {
                textView.setTextColor(getResources().getColor(R.color.app_color_white));
                textView.setBackground(getResources().getDrawable(R.drawable.shape_corner_green_tv2_mnemonic));
                mnemonicCodeListSelected.add(mnemonicWord);
            } else {
                textView.setTextColor(getResources().getColor(R.color.app_color_green));
                textView.setBackground(getResources().getDrawable(R.drawable.shape_corner_gray_tv2));
                mnemonicCodeListSelected.remove(mnemonicWord);
            }
            printMneonicCodeSelected();

            strMnemonicCodeListSelected.clear();
            for (MnemonicWord word : mnemonicCodeListSelected) {
                strMnemonicCodeListSelected.add(word.getCode());
            }
            if (strMnemonicCodeListSelected.size() == 12 && strMnemonicCodeListSelected.equals(srcMnemonicCodeList)) {
                mCompleteMnemonicCodeBtn.setEnabled(true);
                mCompleteMnemonicCodeBtn.setBackground(getResources().getDrawable(R.drawable.radius_button_able_bg));
            } else {
                mCompleteMnemonicCodeBtn.setEnabled(false);
                mCompleteMnemonicCodeBtn.setBackground(getResources().getDrawable(R.drawable.radius_button_disable_bg));
            }
        }
    };


    private void initTopBar() {
        mTopBar.setBackgroundDividerEnabled(true);
        mTopBar.setTitle(R.string.view_title_confirm_mneonic_code);
        mTopBar.addLeftImageButton(R.mipmap.icon_tobar_left_arrow, R.id.topbar_left_arrow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popBackStack();
            }
        });
        mTopBar.addRightTextButton(R.string.skip_backup_mneonic_btn_code, R.id.skipBackupBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (BPCreateWalletFormFragment.isCreateWallet) {
                    popBackStackFragment();
                } else {
                    sharedPreferencesHelper.put("isFirstCreateWallet", "0");
                    startFragment(new HomeFragment());
                }


            }
        });
        Button skipBackuoBtn = mTopBar.findViewById(R.id.skipBackupBtn);
        skipBackuoBtn.setTextColor(ContextCompat.getColor(getContext(), R.color.app_color_main));
    }

    private class MnemonicWord {
        private String wordId;
        private String code;

        public String getWordId() {
            return wordId;
        }

        public void setWordId(String wordId) {
            this.wordId = wordId;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof MnemonicWord) {
                MnemonicWord mnemonicWord = (MnemonicWord) obj;
                return this.getCode().equals(mnemonicWord.getCode()) && this.getWordId().equals(mnemonicWord.getWordId());
            }
            return super.equals(obj);
        }
    }

}
