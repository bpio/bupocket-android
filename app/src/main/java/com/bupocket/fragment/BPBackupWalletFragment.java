package com.bupocket.fragment;

import android.os.Bundle;
import android.os.Looper;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bupocket.R;
import com.bupocket.base.AbsBaseFragment;
import com.bupocket.fragment.home.HomeFragment;
import com.bupocket.utils.SharedPreferencesHelper;
import com.bupocket.wallet.Wallet;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;

import org.bitcoinj.crypto.MnemonicCode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class BPBackupWalletFragment extends AbsBaseFragment {
    @BindView(R.id.backupWalletBtn)
    QMUIRoundButton mBackupWalletBtn;
    @BindView(R.id.skipBackupBtn)
    QMUIRoundButton mSkipBackupBtn;
    @BindView(R.id.topbar)
    QMUITopBarLayout mTopBar;

    private List<String> mnemonicCodeList;
    private SharedPreferencesHelper sharedPreferencesHelper;

    private long exitTime = 0;


    @Override
    protected int getLayoutView() {
        return R.layout.fragment_backup_wallet;
    }

    @Override
    protected void initView() {
        QMUIStatusBarHelper.setStatusBarLightMode(getBaseFragmentActivity());
        initTopBar();
    }

    private void initTopBar() {

        mTopBar.setBackgroundDividerEnabled(false);
//        mTopBar.addLeftImageButton(R.mipmap.icon_tobar_left_arrow, R.id.topbar_left_arrow).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                popBackStack();
//            }
//        });
        mTopBar.setTitle(R.string.backup_wallet_btn_txt);
        mTopBar.addRightTextButton(R.string.skip_backup_mneonic_btn_code, R.id.skipBackupBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedPreferencesHelper.put("isFirstCreateWallet", "0");
                startFragment(new HomeFragment());
            }
        });
        Button skipBackuoBtn = mTopBar.findViewById(R.id.skipBackupBtn);
        skipBackuoBtn.setTextColor(ContextCompat.getColor(getContext(),R.color.app_color_main));
    }


    public void initData(){
        sharedPreferencesHelper = new SharedPreferencesHelper(getContext(), "buPocket");
        if(getArguments() != null){
            mnemonicCodeList = getArguments().getStringArrayList("mneonicCodeList");
        }
    }

    @Override
    protected void setListeners() {
        mBackupWalletBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mnemonicCodeList == null){

                    final QMUIDialog qmuiDialog = new QMUIDialog(getContext());
                    qmuiDialog.setCanceledOnTouchOutside(false);
                    qmuiDialog.setContentView(R.layout.view_password_comfirm);
                    qmuiDialog.show();
                    QMUIRoundButton mPasswordConfirmBtn = qmuiDialog.findViewById(R.id.passwordConfirmBtn);

                    ImageView mPasswordConfirmCloseBtn = qmuiDialog.findViewById(R.id.passwordConfirmCloseBtn);
                    TextView mPasswordConfirmNotice = qmuiDialog.findViewById(R.id.passwordConfirmNotice);
                    mPasswordConfirmNotice.setText(R.string.password_comfirm_dialog_to_backup);

                    mPasswordConfirmCloseBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            qmuiDialog.dismiss();
                        }
                    });

                    mPasswordConfirmBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // 检查合法性
                            EditText mPasswordConfirmEt = qmuiDialog.findViewById(R.id.passwordConfirmEt);
                            final String password = mPasswordConfirmEt.getText().toString().trim();
                            final QMUITipDialog tipDialog = new QMUITipDialog.Builder(getContext())
                                    .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                                    .setTipWord(getResources().getString(R.string.wallet_create_creating_txt))
                                    .create();
                            tipDialog.show();
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    String ciphertextSkeyData = getSkeyStr();
                                    try {
                                        byte[] skeyByte = Wallet.getInstance().getSkey(password,ciphertextSkeyData);
                                        mnemonicCodeList = new MnemonicCode().toMnemonic(skeyByte);
                                        tipDialog.dismiss();

                                        go2BPCreateWalletShowMneonicCodeFragment();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        Looper.prepare();
                                        Toast.makeText(getActivity(), R.string.checking_password_error, Toast.LENGTH_SHORT).show();
                                        tipDialog.dismiss();
                                        Looper.loop();
                                        return;
                                    }
                                }
                            }).start();
                            qmuiDialog.dismiss();

                        }
                    });
                }else{
                    go2BPCreateWalletShowMneonicCodeFragment();
                }
            }
        });
    }

    private String getSkeyStr(){
        return sharedPreferencesHelper.getSharedPreference("skey","").toString();
    }

    private void go2BPCreateWalletShowMneonicCodeFragment(){
        BPCreateWalletShowMneonicCodeFragment createWalletShowMneonicCodeFragment = new BPCreateWalletShowMneonicCodeFragment();
        Bundle argz = new Bundle();
        argz.putStringArrayList("mneonicCodeList", (ArrayList<String>) mnemonicCodeList);
        createWalletShowMneonicCodeFragment.setArguments(argz);
        startFragment(createWalletShowMneonicCodeFragment);
    }


    public void onBackPressed() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(getContext(), getResources().getText(R.string.next_key_down_err), Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            getActivity().finish();
        }

    }

}
