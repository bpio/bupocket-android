package com.bupocket.fragment;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bupocket.R;
import com.bupocket.base.BaseFragment;
import com.bupocket.fragment.home.HomeFragment;
import com.bupocket.utils.CommonUtil;
import com.bupocket.utils.SharedPreferencesHelper;
import com.bupocket.view.DrawableEditText;
import com.bupocket.wallet.Wallet;
import com.bupocket.wallet.enums.CreateWalletStepEnum;
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
    private SharedPreferencesHelper sharedPreferencesHelper;
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

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected View onCreateView() {
        View rootView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_wallet_import, null);
        ButterKnife.bind(this, rootView);
        init();
        return rootView;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void init() {
        initData();
        initUI();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void initUI() {
        initTopBar();
        initTabAndPager();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void initTabAndPager() {
        mContentViewPager.setAdapter(mPagerAdapter);
        mContentViewPager.setCurrentItem(mDestPage.getPosition(), false);
        mTabSegment.setHasIndicator(true);
        mTabSegment.setIndicatorPosition(false);
        mTabSegment.setIndicatorWidthAdjustContent(true);
        mTabSegment.addTab(new QMUITabSegment.Tab(getString(R.string.mnemonic_word_txt)));
        mTabSegment.addTab(new QMUITabSegment.Tab(getString(R.string.keystore_txt)));
        mTabSegment.addTab(new QMUITabSegment.Tab(getString(R.string.private_key_txt)));
        mTabSegment.setDefaultSelectedColor(getContext().getColor(R.color.app_color_main));
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
        sharedPreferencesHelper = new SharedPreferencesHelper(getContext(), "buPocket");
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
        Keystore(1),
        PrivateKey(2);
        private final int position;

        ContentPage(int pos) {
            position = pos;
        }

        public static ContentPage getPage(int position) {
            switch (position) {
                case 0:
                    return MnemonicWord;
                case 1:
                    return Keystore;
                case 2:
                    return PrivateKey;
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
        LayoutInflater inflater = LayoutInflater.from(getContext());
        if (view == null) {
            View contentView = new View(getContext());
            if (page == ContentPage.MnemonicWord) {
                contentView = inflater.inflate(R.layout.view_wallet_import_mnemonic_word, null);
                final EditText mMnemonicWordEt = contentView.findViewById(R.id.mnemonicWordEt);
                final EditText mWalletNameEt = contentView.findViewById(R.id.walletNameEt);
                final EditText mPasswordEt = contentView.findViewById(R.id.passwordEt);
                final EditText mPasswordConfirmEt = contentView.findViewById(R.id.passwordConfirmEt);
                final ImageView mPasswordIv = contentView.findViewById(R.id.passwordIv);
                final ImageView mPasswordConfirmIv = contentView.findViewById(R.id.passwordConfirmIv);
                final QMUIRoundButton mStartImportMnemonicWordBtn = contentView.findViewById(R.id.startImportMnemonicWordBtn);
                final LinearLayout mUnderstandMnemonicWordLl = contentView.findViewById(R.id.understandMnemonicWordLl);

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
                        if (!mneonicFlag()) {
                            return;
                        } else if (!walletNameFlag()) {
                            return;
                        } else if (!pwdFlag()) {
                            return;
                        } else if (!confirmPwdFlag()) {
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
                                    String address = walletBPData.getAccounts().get(1).getAddress();
                                    String walletName = mWalletNameEt.getText().toString();
                                    String bpData = JSON.toJSONString(walletBPData.getAccounts());
                                    importedWallets = JSONObject.parseArray(sharedPreferencesHelper.getSharedPreference("importedWallets","[]").toString(),String.class);
                                    if(address.equals(sharedPreferencesHelper.getSharedPreference("currentAccAddr","")) || importedWallets.contains(address)){
                                        Looper.prepare();
                                        Toast.makeText(getActivity(), R.string.error_already_import_meaaage_txt, Toast.LENGTH_SHORT).show();
                                        tipDialog.dismiss();
                                        Looper.loop();
                                    }else {
                                        sharedPreferencesHelper.put(address + "-walletName", walletName);
                                        sharedPreferencesHelper.put(address + "-BPdata", bpData);
                                        importedWallets.add(address);
                                        sharedPreferencesHelper.put("importedWallets",JSONObject.toJSONString(importedWallets));
                                        Looper.prepare();
                                        Toast.makeText(getActivity(), R.string.import_success_message_txt, Toast.LENGTH_SHORT).show();
                                        tipDialog.dismiss();
                                        startFragmentAndDestroyCurrent(new BPWalletsHomeFragment());
                                        Looper.loop();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Looper.prepare();
                                    Toast.makeText(getActivity(), R.string.error_import_message_txt, Toast.LENGTH_SHORT).show();
                                    tipDialog.dismiss();
                                    Looper.loop();
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

                    private boolean confirmPwdFlag() {
                        String pwd = mPasswordEt.getText().toString().trim();
                        String confirmPwd = mPasswordConfirmEt.getText().toString().trim();
                        String regex = ".{8,20}";
                        if ("".equals(confirmPwd)) {
                            Toast.makeText(getActivity(), R.string.recover_confirm_pwd_hint, Toast.LENGTH_SHORT).show();
                            return false;
                        } else if (!confirmPwd.matches(regex)) {
                            Toast.makeText(getActivity(), R.string.recover_set_pwd_error, Toast.LENGTH_SHORT).show();
                            return false;
                        } else if (!confirmPwd.equals(pwd)) {
                            Toast.makeText(getActivity(), R.string.recover_confirm_pwd_error, Toast.LENGTH_SHORT).show();
                            return false;
                        }
                        return true;
                    }

                    private boolean pwdFlag() {
                        String password = mPasswordEt.getText().toString().trim();
                        if ("".equals(password)) {
                            Toast.makeText(getActivity(), R.string.wallet_create_form_input_password_empty, Toast.LENGTH_SHORT).show();
                            return false;
                        }
                        if (password.length() < 8) {
                            Toast.makeText(getActivity(), R.string.wallet_create_form_error2, Toast.LENGTH_SHORT).show();
                            return false;
                        }
                        if (password.length() > 20) {
                            Toast.makeText(getActivity(), R.string.wallet_create_form_error2, Toast.LENGTH_SHORT).show();
                            return false;
                        }
                        if (!CommonUtil.validatePassword(password)) {
                            Toast.makeText(getActivity(), R.string.wallet_create_form_error2, Toast.LENGTH_SHORT).show();
                            return false;
                        }
                        return true;
                    }

                    private boolean walletNameFlag() {
                        String walletName = mWalletNameEt.getText().toString().trim();
                        if ("".equals(walletName)) {
                            Toast.makeText(getActivity(), R.string.wallet_import_wallet_name_et_hint_txt, Toast.LENGTH_SHORT).show();
                            return false;
                        } else if (!CommonUtil.validateNickname(walletName)) {
                            Toast.makeText(getActivity(), R.string.error_import_wallet_name_message_txt, Toast.LENGTH_SHORT).show();
                            return false;
                        }
                        return true;
                    }

                    private boolean mneonicFlag() {
                        String mneonic = mMnemonicWordEt.getText().toString().trim();
                        String regex = "[a-zA-Z\\s]+";
                        if ("".equals(mneonic)) {
                            Toast.makeText(getActivity(), R.string.recover_edit_mneonic_code_hint, Toast.LENGTH_SHORT).show();
                            return false;
                        } else if (!mneonic.matches(regex)) {
                            Toast.makeText(getActivity(), R.string.error_import_mneonic_input_txt, Toast.LENGTH_SHORT).show();
                            return false;
                        }
                        return true;
                    }
                });


            } else if (page == ContentPage.Keystore) {
                contentView = inflater.inflate(R.layout.view_wallet_import_keystore, null);
                final EditText mKeystoreEt = contentView.findViewById(R.id.keystoreEt);
                final EditText mWalletNameEt = contentView.findViewById(R.id.walletNameEt);
                final EditText mPasswordEt = contentView.findViewById(R.id.passwordEt);
                final ImageView mPasswordIv = contentView.findViewById(R.id.passwordIv);
                final QMUIRoundButton mStartImportKeystoreBtn = contentView.findViewById(R.id.startImportKeystoreBtn);
                final LinearLayout mUnderstandKeystoreLl = contentView.findViewById(R.id.understandKeystoreLl);

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
                        if (!walletNameFlag()) {
                            return;
                        } else if (!pwdFlag()) {
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
                                    WalletBPData walletBPData = Wallet.getInstance().importKeystore(password,keystore);
                                    String address = walletBPData.getAccounts().get(0).getAddress();
                                    String walletName = mWalletNameEt.getText().toString();
                                    String bpData = JSON.toJSONString(walletBPData.getAccounts());
                                    importedWallets = JSONObject.parseArray(sharedPreferencesHelper.getSharedPreference("importedWallets","[]").toString(),String.class);
                                    if(address.equals(sharedPreferencesHelper.getSharedPreference("currentAccAddr","")) || importedWallets.contains(address)){
                                        Looper.prepare();
                                        Toast.makeText(getActivity(), R.string.error_already_import_meaaage_txt, Toast.LENGTH_SHORT).show();
                                        tipDialog.dismiss();
                                        Looper.loop();
                                    }else {
                                        sharedPreferencesHelper.put(address + "-walletName", walletName);
                                        sharedPreferencesHelper.put(address + "-BPdata", bpData);
                                        importedWallets.add(address);
                                        sharedPreferencesHelper.put("importedWallets",JSONObject.toJSONString(importedWallets));
                                        Looper.prepare();
                                        Toast.makeText(getActivity(), R.string.import_success_message_txt, Toast.LENGTH_SHORT).show();
                                        tipDialog.dismiss();
                                        startFragmentAndDestroyCurrent(new BPWalletsHomeFragment());
                                        Looper.loop();
                                    }

                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Looper.prepare();
                                    Toast.makeText(getActivity(), R.string.error_import_keystore_message_txt, Toast.LENGTH_SHORT).show();
                                    tipDialog.dismiss();
                                    Looper.loop();
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
                        return true;
                    }

                    private boolean walletNameFlag() {
                        String walletName = mWalletNameEt.getText().toString().trim();
                        if ("".equals(walletName)) {
                            Toast.makeText(getActivity(), R.string.wallet_import_wallet_name_et_hint_txt, Toast.LENGTH_SHORT).show();
                            return false;
                        } else if (!CommonUtil.validateNickname(walletName)) {
                            Toast.makeText(getActivity(), R.string.error_import_wallet_name_message_txt, Toast.LENGTH_SHORT).show();
                            return false;
                        }
                        return true;
                    }
                });

            } else if (page == ContentPage.PrivateKey) {
                contentView = inflater.inflate(R.layout.view_wallet_import_private, null);
                final EditText mPrivateKeyEt = contentView.findViewById(R.id.privateKeyEt);
                final EditText mWalletNameEt = contentView.findViewById(R.id.walletNameEt);
                final EditText mPasswordEt = contentView.findViewById(R.id.passwordEt);
                final EditText mPasswordConfirmEt = contentView.findViewById(R.id.passwordConfirmEt);
                final ImageView mPasswordIv = contentView.findViewById(R.id.passwordIv);
                final ImageView mPasswordConfirmIv = contentView.findViewById(R.id.passwordConfirmIv);
                final QMUIRoundButton mStartImportPrivateBtn = contentView.findViewById(R.id.startImportPrivateBtn);
                final LinearLayout mUnderstandPrivateLl = contentView.findViewById(R.id.understandPrivateLl);

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

                mStartImportPrivateBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!privateKeyFlag()) {
                            return;
                        } else if (!walletNameFlag()) {
                            return;
                        } else if (!pwdFlag()) {
                            return;
                        } else if (!confirmPwdFlag()) {
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
                                    String address = walletBPData.getAccounts().get(0).getAddress();
                                    String walletName = mWalletNameEt.getText().toString();
                                    String bpData = JSON.toJSONString(walletBPData.getAccounts());
                                    importedWallets = JSONObject.parseArray(sharedPreferencesHelper.getSharedPreference("importedWallets","[]").toString(),String.class);
                                    if(address.equals(sharedPreferencesHelper.getSharedPreference("currentAccAddr","")) || importedWallets.contains(address)){
                                        Looper.prepare();
                                        Toast.makeText(getActivity(), R.string.error_already_import_meaaage_txt, Toast.LENGTH_SHORT).show();
                                        tipDialog.dismiss();
                                        Looper.loop();
                                    }else {
                                        sharedPreferencesHelper.put(address + "-walletName", walletName);
                                        sharedPreferencesHelper.put(address + "-BPdata", bpData);
                                        importedWallets.add(address);
                                        sharedPreferencesHelper.put("importedWallets",JSONObject.toJSONString(importedWallets));
                                        Looper.prepare();
                                        Toast.makeText(getActivity(), R.string.import_success_message_txt, Toast.LENGTH_SHORT).show();
                                        tipDialog.dismiss();
                                        startFragmentAndDestroyCurrent(new BPWalletsHomeFragment());
                                        Looper.loop();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Looper.prepare();
                                    Toast.makeText(getActivity(), R.string.error_import_message_txt, Toast.LENGTH_SHORT).show();
                                    tipDialog.dismiss();
                                    Looper.loop();
                                    return;
                                }
                            }
                        }).start();
                    }

                    private boolean confirmPwdFlag() {
                        String pwd = mPasswordEt.getText().toString().trim();
                        String confirmPwd = mPasswordConfirmEt.getText().toString().trim();
                        String regex = ".{8,20}";
                        if ("".equals(confirmPwd)) {
                            Toast.makeText(getActivity(), R.string.recover_confirm_pwd_hint, Toast.LENGTH_SHORT).show();
                            return false;
                        } else if (!confirmPwd.matches(regex)) {
                            Toast.makeText(getActivity(), R.string.recover_set_pwd_error, Toast.LENGTH_SHORT).show();
                            return false;
                        } else if (!confirmPwd.equals(pwd)) {
                            Toast.makeText(getActivity(), R.string.recover_confirm_pwd_error, Toast.LENGTH_SHORT).show();
                            return false;
                        }
                        return true;
                    }

                    private boolean pwdFlag() {
                        String password = mPasswordEt.getText().toString().trim();
                        if ("".equals(password)) {
                            Toast.makeText(getActivity(), R.string.wallet_create_form_input_password_empty, Toast.LENGTH_SHORT).show();
                            return false;
                        }
                        if (password.length() < 8) {
                            Toast.makeText(getActivity(), R.string.wallet_create_form_error2, Toast.LENGTH_SHORT).show();
                            return false;
                        }
                        if (password.length() > 20) {
                            Toast.makeText(getActivity(), R.string.wallet_create_form_error2, Toast.LENGTH_SHORT).show();
                            return false;
                        }
                        if (!CommonUtil.validatePassword(password)) {
                            Toast.makeText(getActivity(), R.string.wallet_create_form_error2, Toast.LENGTH_SHORT).show();
                            return false;
                        }
                        return true;
                    }

                    private boolean walletNameFlag() {
                        String walletName = mWalletNameEt.getText().toString().trim();
                        if ("".equals(walletName)) {
                            Toast.makeText(getActivity(), R.string.wallet_import_wallet_name_et_hint_txt, Toast.LENGTH_SHORT).show();
                            return false;
                        } else if (!CommonUtil.validateNickname(walletName)) {
                            Toast.makeText(getActivity(), R.string.error_import_wallet_name_message_txt, Toast.LENGTH_SHORT).show();
                            return false;
                        }
                        return true;
                    }

                    private boolean privateKeyFlag() {
                        String privateKey = mPrivateKeyEt.getText().toString().trim();
                        if(!PrivateKey.isPrivateKeyValid(privateKey)){
                            Toast.makeText(getActivity(), R.string.error_import_private_message_txt, Toast.LENGTH_SHORT).show();
                            return false;
                        }
                        return true;
                    }
                });
            }
            view = contentView;
            mPageMap.put(page, view);
        }
        return view;
    }
}
