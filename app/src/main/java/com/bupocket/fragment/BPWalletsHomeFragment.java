package com.bupocket.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.bupocket.R;
import com.bupocket.adaptor.ImportedWalletAdapter;
import com.bupocket.base.BaseFragment;
import com.bupocket.fragment.home.HomeFragment;
import com.bupocket.model.WalletInfo;
import com.bupocket.utils.AddressUtil;
import com.bupocket.utils.CommonUtil;
import com.bupocket.utils.SharedPreferencesHelper;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.dialog.QMUIBottomSheet;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class BPWalletsHomeFragment extends BaseFragment {

    @BindView(R.id.topbar)
    QMUITopBarLayout mTopBar;
    @BindView(R.id.currentIdentityWalletNameTv)
    TextView mCurrentIdentityWalletNameTv;
    @BindView(R.id.currentIdentityWalletAddressTv)
    TextView mCurrentIdentityWalletAddressTv;
    @BindView(R.id.manageIdentityWalletBtn)
    QMUIRoundButton mManageIdentityWalletBtn;
    @BindView(R.id.currentIdentityWalletSignTv)
    TextView mCurrentIdentityWalletSignTv;
    @BindView(R.id.importSmallWalletBtnIv)
    ImageView mImportSmallWalletBtnIv;
    @BindView(R.id.importBigWalletBtn)
    QMUIRoundButton mImportBigWalletBtn;
    @BindView(R.id.importWalletsLv)
    ListView mImportWalletsLv;
    @BindView(R.id.identityWalletInfoRl)
    RelativeLayout mIdentityWalletInfoRl;

    private SharedPreferencesHelper sharedPreferencesHelper;
    private String currentIdentityWalletName;
    private String currentIdentityWalletAddress;
    private String currentWalletAddress;
    private List<String> importedWallets = new ArrayList<>();
    private List<WalletInfo> walletInfoList;
    private ImportedWalletAdapter importedWalletAdapter;
    private Unbinder bind;

    @Override
    protected View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_wallets_home, null);
        bind = ButterKnife.bind(this, root);
        init();
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        initData();
        initUI();
        setListener();
    }

    private void init() {
        initTopBar();
    }

    private void initData() {
        sharedPreferencesHelper = new SharedPreferencesHelper(getContext(), "buPocket");
        currentIdentityWalletAddress = sharedPreferencesHelper.getSharedPreference("currentAccAddr","").toString();
        currentIdentityWalletName = sharedPreferencesHelper.getSharedPreference("currentIdentityWalletName","Wallet-1").toString();
        currentWalletAddress = sharedPreferencesHelper.getSharedPreference("currentWalletAddress","").toString();
        if(CommonUtil.isNull(currentWalletAddress)){
            currentWalletAddress = sharedPreferencesHelper.getSharedPreference("currentAccAddr","").toString();
        }
        importedWallets = JSONObject.parseArray(sharedPreferencesHelper.getSharedPreference("importedWallets","[]").toString(),String.class);
    }

    private void initUI() {
        QMUIStatusBarHelper.setStatusBarLightMode(getBaseFragmentActivity());
        initCurrentIdentityView();
        initImportedWalletView();
    }

    private void initImportedWalletView() {
        if(importedWallets == null || importedWallets.size() == 0){
            mImportSmallWalletBtnIv.setVisibility(View.GONE);
            mImportWalletsLv.setVisibility(View.GONE);
            mImportBigWalletBtn.setVisibility(View.VISIBLE);
        }else{
            mImportSmallWalletBtnIv.setVisibility(View.VISIBLE);
            mImportWalletsLv.setVisibility(View.VISIBLE);
            mImportBigWalletBtn.setVisibility(View.GONE);

            walletInfoList = new ArrayList<>();

            for(String address : importedWallets){
                WalletInfo walletInfo = new WalletInfo();
                String walletName = sharedPreferencesHelper.getSharedPreference(address + "-walletName","").toString();
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
                    argz.putString("walletAddress",walletInfo.getWalletAddress());
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
                    sharedPreferencesHelper.put("currentWalletAddress",address);
                    currentWalletAddress = address;
                    mCurrentIdentityWalletSignTv.setVisibility(View.GONE);
                    importedWalletAdapter = new ImportedWalletAdapter(walletInfoList, getContext(), currentWalletAddress);
                    mImportWalletsLv.setAdapter(importedWalletAdapter);
                    importedWalletAdapter.setOnManageWalletBtnListener(new ImportedWalletAdapter.OnManageWalletBtnListener() {
                        @Override
                        public void onClick(int i) {
                            Bundle argz = new Bundle();
                            WalletInfo walletInfo = walletInfoList.get(i);
                            argz.putString("walletAddress",walletInfo.getWalletAddress());
                            BPWalletManageFragment bpWalletManageFragment = new BPWalletManageFragment();
                            bpWalletManageFragment.setArguments(argz);
                            startFragment(bpWalletManageFragment);
                        }
                    });
                    startFragment(new HomeFragment());
                }
            });

        }
    }

    private void initCurrentIdentityView() {
        mCurrentIdentityWalletNameTv.setText(currentIdentityWalletName);
        mCurrentIdentityWalletAddressTv.setText(AddressUtil.anonymous(currentIdentityWalletAddress));
        if(currentWalletAddress.equals(currentIdentityWalletAddress)){
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
        mTopBar.addRightImageButton(R.mipmap.icon_import_wallet,R.id.topbar_right_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final QMUIBottomSheet walletBottom = new QMUIBottomSheet(mContext);
                walletBottom.setContentView(R.layout.view_create_wallet);
                walletBottom.findViewById(R.id.tvCreateWallet).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

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

    private void setListener() {
        mManageIdentityWalletBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle argz = new Bundle();
                argz.putString("walletAddress",currentIdentityWalletAddress);
                BPWalletManageFragment bpWalletManageFragment = new BPWalletManageFragment();
                bpWalletManageFragment.setArguments(argz);
                startFragment(bpWalletManageFragment);
            }
        });

        mImportSmallWalletBtnIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                importWallet();
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
                sharedPreferencesHelper.put("currentWalletAddress",currentIdentityWalletAddress);
                mCurrentIdentityWalletSignTv.setVisibility(View.VISIBLE);
                currentWalletAddress = currentIdentityWalletAddress;
                if (walletInfoList != null && walletInfoList.size() > 0){
                    importedWalletAdapter = new ImportedWalletAdapter(walletInfoList, getContext(), currentWalletAddress);
                    mImportWalletsLv.setAdapter(importedWalletAdapter);
                    importedWalletAdapter.setOnManageWalletBtnListener(new ImportedWalletAdapter.OnManageWalletBtnListener() {
                        @Override
                        public void onClick(int i) {
                            Bundle argz = new Bundle();
                            WalletInfo walletInfo = walletInfoList.get(i);
                            argz.putString("walletAddress",walletInfo.getWalletAddress());
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

    private void importWallet() {
        startFragment(new BPWalletImportFragment());
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        bind.unbind();
    }
}
