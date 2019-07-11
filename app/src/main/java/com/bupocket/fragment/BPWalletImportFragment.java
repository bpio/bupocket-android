package com.bupocket.fragment;

import android.annotation.TargetApi;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bupocket.R;
import com.bupocket.base.BaseFragment;
import com.bupocket.common.ConstantsType;
import com.bupocket.utils.CommonUtil;
import com.bupocket.utils.DialogUtils;
import com.bupocket.utils.TO;
import com.bupocket.utils.ToastUtil;
import com.bupocket.utils.WalletCurrentUtils;
import com.bupocket.wallet.Wallet;
import com.bupocket.wallet.exception.WalletException;
import com.bupocket.wallet.model.WalletBPData;
import com.qmuiteam.qmui.widget.QMUITabSegment;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.bumo.encryption.key.PrivateKey;

public class BPWalletImportFragment extends BaseFragment {

    @BindView(R.id.topbar)
    QMUITopBarLayout mTopBar;
    @BindView(R.id.tabSegment)
    QMUITabSegment mTabSegment;
    @BindView(R.id.contentViewPager)
    ViewPager mContentViewPager;

    private boolean isPwdHideFirst = false;
    private boolean isConfirmPwdHideFirst = false;
    private List<String> importedWallets = new ArrayList<>();
    private Map<ContentPage, View> mPageMap = new HashMap<>();
    private ContentPage mDestPage = ContentPage.MnemonicWord;
    private int mCurrentItemCount = 3;

    private PagerAdapter mPagerAdapter = new PagerAdapter() {
        @Override
        public int getCount() {
            return mCurrentItemCount;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(final ViewGroup container, int position) {
            ContentPage page = ContentPage.getPage(position);
            View view = getPageView(page);
            view.setTag(page);
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            container.addView(view, params);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getItemPosition(@NonNull Object object) {
            View view = (View) object;
            Object page = view.getTag();
            if (page instanceof ContentPage) {
                int pos = ((ContentPage) page).getPosition();
                if (pos >= mCurrentItemCount) {
                    return POSITION_NONE;
                }
                return POSITION_UNCHANGED;
            }
            return POSITION_NONE;
        }
    };

    @Override
    protected View onCreateView() {
        View rootView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_wallet_import, null);
        ButterKnife.bind(this, rootView);
        init();
        return rootView;
    }

    private void init() {
        initView();
        initData();
    }

    private void initView() {
        initTopBar();
        initTabAndPager();
    }

    private void initTabAndPager() {
        mContentViewPager.setAdapter(mPagerAdapter);
        mContentViewPager.setCurrentItem(mDestPage.getPosition(), false);
        mTabSegment.setHasIndicator(true);
        mTabSegment.setIndicatorPosition(false);
        mTabSegment.setIndicatorWidthAdjustContent(true);
        mTabSegment.addTab(new QMUITabSegment.Tab(getString(R.string.mnemonic_word_txt)));
        mTabSegment.addTab(new QMUITabSegment.Tab(getString(R.string.private_key_txt)));
        mTabSegment.addTab(new QMUITabSegment.Tab(getString(R.string.keystore_txt)));
        mTabSegment.setDefaultSelectedColor(Color.parseColor("#02CA71"));
        mTabSegment.setTabTextSize(TO.dip2px(mContext, 16));
        mTabSegment.setupWithViewPager(mContentViewPager, false);
        mTabSegment.setMode(QMUITabSegment.MODE_FIXED);
        mTabSegment.addOnTabSelectedListener(new QMUITabSegment.OnTabSelectedListener() {
            @Override
            public void onTabSelected(int index) {
                mTabSegment.hideSignCountView(index);
            }

            @Override
            public void onTabUnselected(int index) {

            }

            @Override
            public void onTabReselected(int index) {
                mTabSegment.hideSignCountView(index);
            }

            @Override
            public void onDoubleTap(int index) {

            }
        });
    }

    private void initData() {

    }

    private void initTopBar() {
        mTopBar.addLeftImageButton(R.mipmap.icon_tobar_left_arrow, R.id.topbar_left_arrow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popBackStack();
            }
        });

        mTopBar.setTitle(getString(R.string.import_wallet_title_txt));
    }

    public enum ContentPage {
        MnemonicWord(0),
        PrivateKey(1),
        Keystore(2);
        private final int position;

        ContentPage(int pos) {
            position = pos;
        }

        public static ContentPage getPage(int position) {
            switch (position) {
                case 0:
                    return MnemonicWord;
                case 1:
                    return PrivateKey;
                case 2:
                    return Keystore;

                default:
                    return MnemonicWord;
            }
        }

        public int getPosition() {
            return position;
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private View getPageView(ContentPage page) {
        View view = mPageMap.get(page);
        if (view == null) {
            if (page == ContentPage.MnemonicWord) {
                view = initMnemonicView();
            } else if (page == ContentPage.Keystore) {
                view = initKeyStoreView();
            } else if (page == ContentPage.PrivateKey) {
                view = initPrivateKeyView();
            }
            mPageMap.put(page, view);
        }
        return view;
    }


    private View initKeyStoreView() {
        View contentView = LayoutInflater.from(getContext()).inflate(R.layout.view_wallet_import_keystore, null);
        final EditText mKeystoreEt = contentView.findViewById(R.id.keystoreEt);
        final EditText mWalletNameEt = contentView.findViewById(R.id.walletNameEt);
        final EditText mPasswordEt = contentView.findViewById(R.id.passwordEt);
        final ImageView mPasswordIv = contentView.findViewById(R.id.passwordIv);
        ((TextView) contentView.findViewById(R.id.hintTitleTv)).setText(R.string.keystore_hint_title);
        ((TextView) contentView.findViewById(R.id.hintInfoTv)).setText(R.string.understand_keystore_txt);
        final QMUIRoundButton mStartImportKeystoreBtn = contentView.findViewById(R.id.startImportKeystoreBtn);
        final LinearLayout mUnderstandKeystoreLl = contentView.findViewById(R.id.importInfoHintLl);

        mUnderstandKeystoreLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startFragment(new BPWalletUnderstandKeystoreFragment());
            }
        });

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                mStartImportKeystoreBtn.setEnabled(false);
                mStartImportKeystoreBtn.setBackground(getResources().getDrawable(R.drawable.radius_button_disable_bg));
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mStartImportKeystoreBtn.setEnabled(false);
                mStartImportKeystoreBtn.setBackground(getResources().getDrawable(R.drawable.radius_button_disable_bg));
            }

            @Override
            public void afterTextChanged(Editable s) {
                boolean signMnemonicCode = mKeystoreEt.getText().toString().trim().length() > 0;
                boolean signWalletName = mWalletNameEt.getText().toString().trim().length() > 0;
                boolean signPwd = mPasswordEt.getText().toString().trim().length() > 0;
                if (signMnemonicCode && signWalletName && signPwd) {
                    mStartImportKeystoreBtn.setEnabled(true);
                    mStartImportKeystoreBtn.setBackground(getResources().getDrawable(R.drawable.radius_button_able_bg));
                } else {
                    mStartImportKeystoreBtn.setEnabled(false);
                    mStartImportKeystoreBtn.setBackground(getResources().getDrawable(R.drawable.radius_button_disable_bg));
                }
            }
        };
        mKeystoreEt.addTextChangedListener(textWatcher);
        mWalletNameEt.addTextChangedListener(textWatcher);
        mPasswordEt.addTextChangedListener(textWatcher);

        mPasswordIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isPwdHideFirst) {
                    mPasswordIv.setImageDrawable(ContextCompat.getDrawable(getContext(), R.mipmap.icon_open_eye));
                    mPasswordEt.setInputType(InputType.TYPE_CLASS_TEXT);
                    mPasswordEt.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    mPasswordEt.setSelection(mPasswordEt.getText().length());
                    isPwdHideFirst = true;
                } else {
                    mPasswordIv.setImageDrawable(ContextCompat.getDrawable(getContext(), R.mipmap.icon_close_eye));
                    mPasswordEt.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    mPasswordEt.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    mPasswordEt.setSelection(mPasswordEt.getText().length());
                    isPwdHideFirst = false;
                }
            }
        });

        mStartImportKeystoreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!walletNameFlag(mWalletNameEt)) {
                    return;
                }
                if (!pwdFlag()) {
                    return;
                }
                final String password = mPasswordEt.getText().toString().trim();
                final QMUITipDialog tipDialog = new QMUITipDialog.Builder(getContext())
                        .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                        .setTipWord(getResources().getString(R.string.importing_loading_txt))
                        .create();
                tipDialog.show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String keystore = mKeystoreEt.getText().toString().trim();
                            WalletBPData walletBPData = Wallet.getInstance().importKeystore(password, keystore);
                            saveWalletData(walletBPData, mWalletNameEt.getText().toString().trim(), true);
                            tipDialog.dismiss();
                        } catch (WalletException e) {
                            e.printStackTrace();
                            ToastUtil.showToast(getActivity(), R.string.error_import_keystore_message_txt, Toast.LENGTH_SHORT);
                            tipDialog.dismiss();

                            return;
                        } catch (Exception e) {
                            e.printStackTrace();
                            ToastUtil.showToast(getActivity(), R.string.error_import_keystore_message_txt, Toast.LENGTH_SHORT);
                            tipDialog.dismiss();
                            return;
                        }
                    }
                }).start();

            }

            private boolean pwdFlag() {
                String password = mPasswordEt.getText().toString().trim();
                if ("".equals(password)) {
                    Toast.makeText(getActivity(), R.string.wallet_create_form_input_password_empty, Toast.LENGTH_SHORT).show();
                    return false;
                }

                if (!CommonUtil.validateOldPassword(password)) {
                    DialogUtils.showTitleDialog(mContext, R.string.wallet_create_form_error2, R.string.error_hint);
                    return false;
                }
                return true;
            }

        });

        return contentView;
    }

    private void saveWalletData(WalletBPData walletBPData, String walletName, boolean isPrivate) {

        String address = walletBPData.getAccounts().get(0).getAddress();
        String bpData = JSON.toJSONString(walletBPData.getAccounts());
        importedWallets = JSONObject.parseArray(spHelper.getSharedPreference("importedWallets", "[]").toString(), String.class);
        if (address.equals(spHelper.getSharedPreference("currentAccAddr", "")) || importedWallets.contains(address)) {
            ToastUtil.showToast(getActivity(), R.string.error_already_import_meaaage_txt, Toast.LENGTH_SHORT);
        } else {
            spHelper.put(address + "-walletName", walletName);
            spHelper.put(address + "-BPdata", bpData);
            importedWallets.add(address);
            spHelper.put("importedWallets", JSONObject.toJSONString(importedWallets));
            if (isPrivate) {
                spHelper.put(address + ConstantsType.WALLET_SKEY_PRIV, walletBPData.getSkey());
            } else {
                spHelper.put(address + ConstantsType.WALLET_SKEY, walletBPData.getSkey());
            }
            WalletCurrentUtils.saveInitHeadIcon(spHelper,address);
            ToastUtil.showToast(getActivity(), R.string.import_success_message_txt, Toast.LENGTH_SHORT);
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    popBackStack();
                }
            });
        }
    }

    private View initMnemonicView() {
        View contentView = LayoutInflater.from(getContext()).inflate(R.layout.view_wallet_import_mnemonic_word, null);
        final EditText mMnemonicWordEt = contentView.findViewById(R.id.mnemonicWordEt);
        final EditText mWalletNameEt = contentView.findViewById(R.id.walletNameEt);
        final EditText mPasswordEt = contentView.findViewById(R.id.passwordEt);
        final EditText mPasswordConfirmEt = contentView.findViewById(R.id.passwordConfirmEt);
        final ImageView mPasswordIv = contentView.findViewById(R.id.passwordIv);
        final ImageView mPasswordConfirmIv = contentView.findViewById(R.id.passwordConfirmIv);
        final QMUIRoundButton mStartImportMnemonicWordBtn = contentView.findViewById(R.id.startImportMnemonicWordBtn);
        final LinearLayout mUnderstandMnemonicWordLl = contentView.findViewById(R.id.importInfoHintLl);

        mUnderstandMnemonicWordLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startFragment(new BPWalletUnderstandMnemonicWordFragment());
            }
        });

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                mStartImportMnemonicWordBtn.setEnabled(false);
                mStartImportMnemonicWordBtn.setBackground(getResources().getDrawable(R.drawable.radius_button_disable_bg));
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mStartImportMnemonicWordBtn.setEnabled(false);
                mStartImportMnemonicWordBtn.setBackground(getResources().getDrawable(R.drawable.radius_button_disable_bg));
            }

            @Override
            public void afterTextChanged(Editable s) {
                boolean signMnemonicCode = mMnemonicWordEt.getText().toString().trim().length() > 0;
                boolean signWalletName = mWalletNameEt.getText().toString().trim().length() > 0;
                boolean signPwd = mPasswordEt.getText().toString().trim().length() > 0;
                boolean signConfirm = mPasswordConfirmEt.getText().toString().trim().length() > 0;
                if (signMnemonicCode && signWalletName && signPwd && signConfirm) {
                    mStartImportMnemonicWordBtn.setEnabled(true);
                    mStartImportMnemonicWordBtn.setBackground(getResources().getDrawable(R.drawable.radius_button_able_bg));
                } else {
                    mStartImportMnemonicWordBtn.setEnabled(false);
                    mStartImportMnemonicWordBtn.setBackground(getResources().getDrawable(R.drawable.radius_button_disable_bg));
                }
            }
        };
        mMnemonicWordEt.addTextChangedListener(textWatcher);
        mWalletNameEt.addTextChangedListener(textWatcher);
        mPasswordEt.addTextChangedListener(textWatcher);
        mPasswordConfirmEt.addTextChangedListener(textWatcher);

        mPasswordIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isPwdHideFirst) {
                    mPasswordIv.setImageDrawable(ContextCompat.getDrawable(getContext(), R.mipmap.icon_open_eye));
                    mPasswordEt.setInputType(InputType.TYPE_CLASS_TEXT);
                    mPasswordEt.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    mPasswordEt.setSelection(mPasswordEt.getText().length());
                    isPwdHideFirst = true;
                } else {
                    mPasswordIv.setImageDrawable(ContextCompat.getDrawable(getContext(), R.mipmap.icon_close_eye));
                    mPasswordEt.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    mPasswordEt.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    mPasswordEt.setSelection(mPasswordEt.getText().length());
                    isPwdHideFirst = false;
                }
            }
        });
        mPasswordConfirmIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isConfirmPwdHideFirst) {
                    mPasswordConfirmIv.setImageDrawable(ContextCompat.getDrawable(getContext(), R.mipmap.icon_open_eye));
                    mPasswordConfirmEt.setInputType(InputType.TYPE_CLASS_TEXT);
                    mPasswordConfirmEt.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    mPasswordConfirmEt.setSelection(mPasswordConfirmEt.getText().length());
                    isConfirmPwdHideFirst = true;
                } else {
                    mPasswordConfirmIv.setImageDrawable(ContextCompat.getDrawable(getContext(), R.mipmap.icon_close_eye));
                    mPasswordConfirmEt.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    mPasswordConfirmEt.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    mPasswordConfirmEt.setSelection(mPasswordConfirmEt.getText().length());
                    isConfirmPwdHideFirst = false;
                }
            }
        });

        mStartImportMnemonicWordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!walletNameFlag(mWalletNameEt)) {
                    return;
                }
                if (!pwdConfirmFlag(mPasswordEt, mPasswordConfirmEt)) {
                    return;
                }

                if (!mnemonicFlag(mMnemonicWordEt)) {
                    return;
                }

                final String password = mPasswordEt.getText().toString().trim();
                final QMUITipDialog tipDialog = new QMUITipDialog.Builder(getContext())
                        .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                        .setTipWord(getResources().getString(R.string.importing_loading_txt))
                        .create();
                tipDialog.show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            List<String> mnemonicCodes = getMnemonicCode();
                            WalletBPData walletBPData = Wallet.getInstance().importMnemonicCode(mnemonicCodes, password, getContext());
                            spHelper.put(walletBPData.getAccounts().get(0).getAddress() + "-mnemonicCodes", "yes");
                            saveWalletData(walletBPData, mWalletNameEt.getText().toString().trim(), false);

                            tipDialog.dismiss();
                        } catch (Exception e) {
                            e.printStackTrace();
                            ToastUtil.showToast(getActivity(), R.string.error_import_message_txt, Toast.LENGTH_SHORT);
                            tipDialog.dismiss();

                            return;
                        }
                    }
                }).start();
            }

            private List<String> getMnemonicCode() {
                String inputMneonicCodeStr = mMnemonicWordEt.getText().toString().trim();
                String regex = "\\s+";
                String[] mneonicCodeArr = inputMneonicCodeStr.replaceAll(regex, " ").split(" ");
                return Arrays.asList(mneonicCodeArr);
            }

        });


        return contentView;
    }


    private View initPrivateKeyView() {
        View contentView = LayoutInflater.from(getContext()).inflate(R.layout.view_wallet_import_private, null);
        final EditText mPrivateKeyEt = contentView.findViewById(R.id.privateKeyEt);
        final EditText mWalletNameEt = contentView.findViewById(R.id.walletNameEt);
        final EditText mPasswordEt = contentView.findViewById(R.id.passwordEt);
        final EditText mPasswordConfirmEt = contentView.findViewById(R.id.passwordConfirmEt);
        final ImageView mPasswordIv = contentView.findViewById(R.id.passwordIv);
        final ImageView mPasswordConfirmIv = contentView.findViewById(R.id.passwordConfirmIv);
        final QMUIRoundButton mStartImportPrivateBtn = contentView.findViewById(R.id.startImportPrivateBtn);
        final LinearLayout mUnderstandPrivateLl = contentView.findViewById(R.id.importInfoHintLl);
        ((TextView) contentView.findViewById(R.id.hintTitleTv)).setText(R.string.wallet_import_private_et_hint_txt);
        ((TextView) contentView.findViewById(R.id.hintInfoTv)).setText(R.string.understand_private_txt);
        mUnderstandPrivateLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startFragment(new BPWalletUnderstandPrivateKeyFragment());
            }
        });

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                mStartImportPrivateBtn.setEnabled(false);
                mStartImportPrivateBtn.setBackground(getResources().getDrawable(R.drawable.radius_button_disable_bg));
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mStartImportPrivateBtn.setEnabled(false);
                mStartImportPrivateBtn.setBackground(getResources().getDrawable(R.drawable.radius_button_disable_bg));
            }

            @Override
            public void afterTextChanged(Editable s) {
                boolean signMnemonicCode = mPrivateKeyEt.getText().toString().trim().length() > 0;
                boolean signWalletName = mWalletNameEt.getText().toString().trim().length() > 0;
                boolean signPwd = mPasswordEt.getText().toString().trim().length() > 0;
                boolean signConfirm = mPasswordConfirmEt.getText().toString().trim().length() > 0;
                if (signMnemonicCode && signWalletName && signPwd && signConfirm) {
                    mStartImportPrivateBtn.setEnabled(true);
                    mStartImportPrivateBtn.setBackground(getResources().getDrawable(R.drawable.radius_button_able_bg));
                } else {
                    mStartImportPrivateBtn.setEnabled(false);
                    mStartImportPrivateBtn.setBackground(getResources().getDrawable(R.drawable.radius_button_disable_bg));
                }
            }
        };
        mPrivateKeyEt.addTextChangedListener(textWatcher);
        mWalletNameEt.addTextChangedListener(textWatcher);
        mPasswordEt.addTextChangedListener(textWatcher);
        mPasswordConfirmEt.addTextChangedListener(textWatcher);

        mPasswordIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isPwdHideFirst) {
                    mPasswordIv.setImageDrawable(ContextCompat.getDrawable(getContext(), R.mipmap.icon_open_eye));
                    mPasswordEt.setInputType(InputType.TYPE_CLASS_TEXT);
                    mPasswordEt.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    mPasswordEt.setSelection(mPasswordEt.getText().length());
                    isPwdHideFirst = true;
                } else {
                    mPasswordIv.setImageDrawable(ContextCompat.getDrawable(getContext(), R.mipmap.icon_close_eye));
                    mPasswordEt.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                    mPasswordEt.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    mPasswordEt.setSelection(mPasswordEt.getText().length());
                    isPwdHideFirst = false;
                }
            }
        });
        mPasswordConfirmIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isConfirmPwdHideFirst) {
                    mPasswordConfirmIv.setImageDrawable(ContextCompat.getDrawable(getContext(), R.mipmap.icon_open_eye));
                    mPasswordConfirmEt.setInputType(InputType.TYPE_CLASS_TEXT);
                    mPasswordConfirmEt.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    mPasswordConfirmEt.setSelection(mPasswordConfirmEt.getText().length());
                    isConfirmPwdHideFirst = true;
                } else {
                    mPasswordConfirmIv.setImageDrawable(ContextCompat.getDrawable(getContext(), R.mipmap.icon_close_eye));
                    mPasswordConfirmEt.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    mPasswordConfirmEt.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    mPasswordConfirmEt.setSelection(mPasswordConfirmEt.getText().length());
                    isConfirmPwdHideFirst = false;
                }
            }
        });

        mStartImportPrivateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!walletNameFlag(mWalletNameEt)) {
                    return;
                }
                if (!pwdConfirmFlag(mPasswordEt, mPasswordConfirmEt)) {
                    return;
                }

                if (!privateKeyFlag()) {
                    return;
                }
                final String password = mPasswordEt.getText().toString().trim();
                final QMUITipDialog tipDialog = new QMUITipDialog.Builder(getContext())
                        .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                        .setTipWord(getResources().getString(R.string.importing_loading_txt))
                        .create();
                tipDialog.show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String privateKey = mPrivateKeyEt.getText().toString().trim();
                            WalletBPData walletBPData = Wallet.getInstance().importPrivateKey(password, privateKey);
                            saveWalletData(walletBPData, mWalletNameEt.getText().toString(), true);
                            tipDialog.dismiss();

                        } catch (Exception e) {
                            e.printStackTrace();
                            ToastUtil.showToast(getActivity(), R.string.error_import_message_txt, Toast.LENGTH_SHORT);
                            tipDialog.dismiss();
                            return;
                        }
                    }
                }).start();
            }


            private boolean privateKeyFlag() {
                String privateKey = mPrivateKeyEt.getText().toString().trim();
                if (!PrivateKey.isPrivateKeyValid(privateKey)) {
                    DialogUtils.showTitleDialog(mContext, getString(R.string.error_import_private_message_txt), getString(R.string.error_hint));
                    return false;
                }
                return true;
            }
        });

        return contentView;

    }


    private boolean walletNameFlag(EditText nameEt) {
        String walletName = nameEt.getText().toString().trim();
        if (!CommonUtil.validateNickname(walletName)) {
            DialogUtils.showTitleDialog(mContext, getString(R.string.wallet_create_form_error4), getString(R.string.error_hint));
            return false;
        }
        return true;
    }


    private boolean pwdFlag(EditText pwdEt) {
        String password = pwdEt.getText().toString().trim();
        if (!CommonUtil.validatePassword(password)) {
            DialogUtils.showTitleDialog(mContext, R.string.wallet_create_form_error2, R.string.error_hint);
            return false;
        }
        return true;
    }

    private boolean pwdConfirmFlag(EditText pwdEt, EditText pwdConfirmEt) {
        String password = pwdEt.getText().toString().trim();
        if (!CommonUtil.validatePassword(password)) {
            DialogUtils.showTitleDialog(mContext, R.string.wallet_create_form_error2, R.string.error_hint);
            return false;
        }

        if (!password.equals(pwdConfirmEt.getText().toString().trim())) {
            DialogUtils.showTitleDialog(mContext, R.string.wallet_create_form_error1, R.string.error_hint);
            return false;
        }

        return true;
    }


    private boolean mnemonicFlag(EditText mneonicEt) {
        String mneonic = mneonicEt.getText().toString().trim();
        String regex = "[a-zA-Z\\s]+";
        if (TextUtils.isEmpty(mneonic) || !mneonic.matches(regex)) {
            DialogUtils.showTitleDialog(mContext, R.string.wallet_create_form_error1, R.string.error_hint);
            return false;
        }
        return true;
    }
}
