package com.bupocket.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.bupocket.R;
import com.bupocket.adaptor.ImportedWalletAdapter;
import com.bupocket.base.AbsBaseFragment;
import com.bupocket.base.BaseFragment;
import com.bupocket.common.Constants;
import com.bupocket.common.ConstantsType;
import com.bupocket.fragment.home.HomeFragment;
import com.bupocket.model.WalletInfo;
import com.bupocket.utils.AddressUtil;
import com.bupocket.utils.CommonUtil;
import com.bupocket.utils.SharedPreferencesHelper;
import com.bupocket.voucher.BPVoucherHomeFragment;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.dialog.QMUIBottomSheet;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class BPWalletsHomeFragment extends AbsBaseFragment {

    @BindView(R.id.topbar)
    QMUITopBarLayout mTopBar;
    @BindView(R.id.walletNameTv)
    TextView mCurrentIdentityWalletNameTv;
    @BindView(R.id.walletAddressTv)
    TextView mCurrentIdentityWalletAddressTv;
    @BindView(R.id.manageWalletBtn)
    QMUIRoundButton mManageIdentityWalletBtn;
    @BindView(R.id.walletSignTv)
    TextView mCurrentIdentityWalletSignTv;
    @BindView(R.id.identityWalletInfoLl)
    LinearLayout mIdentityWalletInfoRl;

    @BindView(R.id.importBigWalletBtn)
    QMUIRoundButton mImportBigWalletBtn;
    @BindView(R.id.importWalletsLv)
    ListView mImportWalletsLv;
    @BindView(R.id.walletHeadRiv)
    ImageView walletHeadRiv;


    private SharedPreferencesHelper sharedPreferencesHelper;
    private String currentIdentityWalletName;
    private String currentIdentityWalletAddress;
    private String currentWalletAddress;
    private List<String> importedWallets = new ArrayList<>();
    private List<WalletInfo> walletInfoList;
    private ImportedWalletAdapter importedWalletAdapter;


    @Override
    protected int getLayoutView() {
        return R.layout.fragment_wallets_home;
    }

    @Override
    protected void initView() {
        initTopBar();
    }

    @Override
    public void onStart() {
        super.onStart();
    }


    public void initData() {

    }

    @Override
    public void onResume() {
        super.onResume();
        sharedPreferencesHelper = new SharedPreferencesHelper(getContext(), "buPocket");
        currentIdentityWalletAddress = sharedPreferencesHelper.getSharedPreference("currentAccAddr", "").toString();
        currentIdentityWalletName = sharedPreferencesHelper.getSharedPreference("currentIdentityWalletName", Constants.NORMAL_WALLET_NAME).toString();
        currentWalletAddress = sharedPreferencesHelper.getSharedPreference("currentWalletAddress", "").toString();
        if (CommonUtil.isNull(currentWalletAddress)) {
            currentWalletAddress = sharedPreferencesHelper.getSharedPreference("currentAccAddr", "").toString();
        }
        importedWallets = JSONObject.parseArray(sharedPreferencesHelper.getSharedPreference("importedWallets", "[]").toString(), String.class);

        QMUIStatusBarHelper.setStatusBarLightMode(getBaseFragmentActivity());
        initCurrentIdentityView();
        initImportedWalletView();

        CommonUtil.setHeadIvRes(currentIdentityWalletAddress, walletHeadRiv, spHelper);
    }

    @Override
    protected void setListeners() {
        mManageIdentityWalletBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle argz = new Bundle();
                argz.putString("walletAddress", currentIdentityWalletAddress);
                BPWalletManageFragment bpWalletManageFragment = new BPWalletManageFragment();
                bpWalletManageFragment.setArguments(argz);
                startFragment(bpWalletManageFragment);
            }
        });

        mImportBigWalletBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                importWallet();
            }
        });

        mIdentityWalletInfoRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedPreferencesHelper.put("currentWalletAddress", currentIdentityWalletAddress);
                mCurrentIdentityWalletSignTv.setVisibility(View.VISIBLE);
                currentWalletAddress = currentIdentityWalletAddress;
                if (walletInfoList != null && walletInfoList.size() > 0) {
                    importedWalletAdapter = new ImportedWalletAdapter(walletInfoList, getContext(), currentWalletAddress);
                    mImportWalletsLv.setAdapter(importedWalletAdapter);
                    importedWalletAdapter.setOnManageWalletBtnListener(new ImportedWalletAdapter.OnManageWalletBtnListener() {
                        @Override
                        public void onClick(int i) {
                            Bundle argz = new Bundle();
                            WalletInfo walletInfo = walletInfoList.get(i);
                            argz.putString("walletAddress", walletInfo.getWalletAddress());
                            BPWalletManageFragment bpWalletManageFragment = new BPWalletManageFragment();
                            bpWalletManageFragment.setArguments(argz);
                            startFragment(bpWalletManageFragment);
                        }
                    });
                }
                startFragment(new HomeFragment());
            }
        });
    }


    private void initImportedWalletView() {
        if (importedWallets == null || importedWallets.size() == 0) {

            mImportWalletsLv.setVisibility(View.GONE);
            mImportBigWalletBtn.setVisibility(View.VISIBLE);
        } else {
            mImportWalletsLv.setVisibility(View.VISIBLE);
            mImportBigWalletBtn.setVisibility(View.GONE);

            walletInfoList = new ArrayList<>();

            for (String address : importedWallets) {
                WalletInfo walletInfo = new WalletInfo();
                String walletName = sharedPreferencesHelper.getSharedPreference(address + "-walletName", "").toString();
                walletInfo.setWalletName(walletName);
                walletInfo.setWalletAddress(address);
                walletInfoList.add(walletInfo);
            }

            importedWalletAdapter = new ImportedWalletAdapter(walletInfoList, getContext(), currentWalletAddress);
            mImportWalletsLv.setAdapter(importedWalletAdapter);
            importedWalletAdapter.setOnManageWalletBtnListener(new ImportedWalletAdapter.OnManageWalletBtnListener() {
                @Override
                public void onClick(int i) {
                    Bundle argz = new Bundle();
                    WalletInfo walletInfo = walletInfoList.get(i);
                    argz.putString("walletAddress", walletInfo.getWalletAddress());
                    BPWalletManageFragment bpWalletManageFragment = new BPWalletManageFragment();
                    bpWalletManageFragment.setArguments(argz);
                    startFragment(bpWalletManageFragment);
                }
            });


            mImportWalletsLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    WalletInfo walletInfo = (WalletInfo) importedWalletAdapter.getItem(position);
                    String address = walletInfo.getWalletAddress();
                    sharedPreferencesHelper.put("currentWalletAddress", address);
                    currentWalletAddress = address;
                    mCurrentIdentityWalletSignTv.setVisibility(View.GONE);
                    importedWalletAdapter = new ImportedWalletAdapter(walletInfoList, getContext(), currentWalletAddress);
                    mImportWalletsLv.setAdapter(importedWalletAdapter);
                    importedWalletAdapter.setOnManageWalletBtnListener(new ImportedWalletAdapter.OnManageWalletBtnListener() {
                        @Override
                        public void onClick(int i) {
                            Bundle argz = new Bundle();
                            WalletInfo walletInfo = walletInfoList.get(i);
                            argz.putString("walletAddress", walletInfo.getWalletAddress());
                            BPWalletManageFragment bpWalletManageFragment = new BPWalletManageFragment();
                            bpWalletManageFragment.setArguments(argz);
                            startFragment(bpWalletManageFragment);
                        }
                    });

                    if (getArguments() != null) {
                        if (BPVoucherHomeFragment.class.getSimpleName().equals(getArguments().getString(ConstantsType.FRAGMENT_TAG))) {
                            HomeFragment fragment = new HomeFragment();
                            startFragment(fragment);
                        }
                    } else {
                        startFragment(new HomeFragment());
                    }

                }
            });

        }
    }

    private void initCurrentIdentityView() {
        mCurrentIdentityWalletNameTv.setText(currentIdentityWalletName);
        mCurrentIdentityWalletAddressTv.setText(AddressUtil.anonymous(currentIdentityWalletAddress));
        if (currentWalletAddress.equals(currentIdentityWalletAddress)) {
            mCurrentIdentityWalletSignTv.setVisibility(View.VISIBLE);
        }
    }

    private void initTopBar() {
        mTopBar.setBackgroundDividerEnabled(false);
        mTopBar.addLeftImageButton(R.mipmap.icon_tobar_left_arrow, R.id.topbar_left_arrow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popBackStack();
            }
        });
        mTopBar.addRightImageButton(R.mipmap.icon_import_wallet, R.id.topbar_right_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final QMUIBottomSheet walletBottom = new QMUIBottomSheet(mContext);
                walletBottom.setContentView(R.layout.view_create_wallet);
                walletBottom.findViewById(R.id.tvCreateWallet).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        BPCreateWalletFormFragment fragment = new BPCreateWalletFormFragment();
                        Bundle args = new Bundle();
                        args.putString("jumpPage", BPWalletsHomeFragment.class.getSimpleName());
                        fragment.setArguments(args);
                        startFragment(fragment);
                        walletBottom.dismiss();
                    }
                });
                walletBottom.findViewById(R.id.tvRecoverWallet).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        importWallet();
                        walletBottom.dismiss();
                    }
                });
                walletBottom.findViewById(R.id.tvDeleteWallet).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        walletBottom.dismiss();
                    }
                });
                walletBottom.show();
            }
        });
    }


    private void importWallet() {
        startFragment(new BPWalletImportFragment());
    }

}
