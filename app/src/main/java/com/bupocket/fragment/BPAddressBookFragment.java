package com.bupocket.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.bupocket.R;
import com.bupocket.adaptor.AddressAdapter;
import com.bupocket.base.BaseFragment;
import com.bupocket.enums.AddressClickEventEnum;
import com.bupocket.http.api.AddressBookService;
import com.bupocket.http.api.RetrofitFactory;
import com.bupocket.http.api.dto.resp.ApiResult;
import com.bupocket.http.api.dto.resp.GetAddressBookRespDto;
import com.bupocket.utils.CommonUtil;
import com.bupocket.utils.SharedPreferencesHelper;
import com.bupocket.wallet.Wallet;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.QMUIEmptyView;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.google.zxing.integration.android.IntentIntegrator.REQUEST_CODE;

public class BPAddressBookFragment extends BaseFragment {
    @BindView(R.id.topbar)
    QMUITopBarLayout mTopBar;
    @BindView(R.id.addressEv)
    QMUIEmptyView mAddressEv;
    @BindView(R.id.refreshLayout)
    RefreshLayout refreshLayout;
    @BindView(R.id.addressBookLv)
    ListView mAddressBookLv;
    @BindView(R.id.addressRecordEmptyLL)
    LinearLayout mAddressRecordEmptyLL;

    private String flag;
    private SharedPreferencesHelper sharedPreferencesHelper;
    private String identityAddress;
    private String pageSize = "10";
    private Integer pageStart = 1;
    private AddressAdapter addressAdapter;
    private GetAddressBookRespDto.PageBean page;
    private String tokenBalance;
    private String tokenCode;
    private String tokenDecimals;
    private String tokenIssuer;
    private String tokenType;
    private String currentWalletAddress;
    private List<GetAddressBookRespDto.AddressBookListBean> addressList = new ArrayList<>();

    @Override
    protected View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_address_book, null);
        ButterKnife.bind(this, root);
        init();
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        initData();
        refreshData();
        initListView();
    }

    private void init() {
        initUI();
    }

    private void initData() {
        sharedPreferencesHelper = new SharedPreferencesHelper(getContext(), "buPocket");
        identityAddress = sharedPreferencesHelper.getSharedPreference("identityId","").toString();
        Bundle bundle = getArguments();
        flag = bundle != null ? bundle.getString("flag") : AddressClickEventEnum.EDIT.getCode();
        if(AddressClickEventEnum.CHOOSE.getCode().equals(flag)){
            tokenCode = bundle.getString("tokenCode");
            tokenDecimals = bundle.getString("tokenDecimals");
            tokenIssuer = bundle.getString("tokenIssuer");
            tokenType = bundle.getString("tokenType");
        }
        currentWalletAddress = sharedPreferencesHelper.getSharedPreference("currentWalletAddress","").toString();
        if(CommonUtil.isNull(currentWalletAddress) || currentWalletAddress.equals(sharedPreferencesHelper.getSharedPreference("currentAccAddr","").toString())){
            currentWalletAddress = sharedPreferencesHelper.getSharedPreference("currentAccAddr","").toString();
        }
        tokenBalance = sharedPreferencesHelper.getSharedPreference(currentWalletAddress + "tokenBalance","0").toString();
        Runnable getBalanceRunnable = new Runnable() {
            @Override
            public void run() {
                tokenBalance = Wallet.getInstance().getAccountBUBalance(currentWalletAddress);
                if(!CommonUtil.isNull(tokenBalance)){
                    sharedPreferencesHelper.put(currentWalletAddress + "tokenBalance",tokenBalance);
                }
            }
        };
        new Thread(getBalanceRunnable).start();
    }

    private void initUI() {
        QMUIStatusBarHelper.setStatusBarLightMode(getBaseFragmentActivity());
        initTopBar();
    }

    private void initListView() {
        refreshLayout.setNoMoreData(false);
        mAddressEv.show(true);
        refreshLayout.setEnableLoadMore(false);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(final RefreshLayout refreshlayout) {
                refreshlayout.getLayout().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshData();
                        refreshlayout.finishRefresh();
                        refreshLayout.setNoMoreData(false);
                        initData();
                    }
                }, 500);

            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(final RefreshLayout refreshlayout) {
                refreshlayout.getLayout().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(addressAdapter == null){
                            refreshlayout.finishRefresh();
                            return;
                        }

                        loadMoreData();
                        refreshlayout.finishLoadMore(500);

                        if(!page.isNextFlag()){
                            refreshLayout.finishLoadMoreWithNoMoreData();
                        }
                    }
                }, 500);
            }
        });
    }

    private void refreshData() {
        pageStart = 1;
        addressList.clear();
        loadAddressList();
    }

    private void loadMoreData() {
        if(page.isNextFlag()){
            pageStart ++;
            loadAddressList();
        }
    }

    private void loadAddressList() {
        AddressBookService addressBookService = RetrofitFactory.getInstance().getRetrofit().create(AddressBookService.class);
        Call<ApiResult<GetAddressBookRespDto>> call;
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("identityAddress",identityAddress);
        paramsMap.put("startPage",pageStart);
        paramsMap.put("pageSize",pageSize);
        call = addressBookService.getMyAddressBook(paramsMap);
        call.enqueue(new Callback<ApiResult<GetAddressBookRespDto>>() {
            @Override
            public void onResponse(Call<ApiResult<GetAddressBookRespDto>> call, Response<ApiResult<GetAddressBookRespDto>> response) {
                ApiResult<GetAddressBookRespDto> respDto = response.body();
                if(null != respDto && null != respDto.getData()){
                    mAddressEv.show(null,null);
                    if(isAdded() && null != respDto.getData().getAddressBookList()){
                        mAddressBookLv.setVisibility(View.VISIBLE);
                        mAddressRecordEmptyLL.setVisibility(View.GONE);
                        handleAddressList(respDto.getData());
                    }else{
                        mAddressBookLv.setVisibility(View.GONE);
                        mAddressRecordEmptyLL.setVisibility(View.VISIBLE);
                    }
                }else{
                    mAddressEv.show(getResources().getString(R.string.emptyView_mode_desc_fail_title), null);
                }
            }

            @Override
            public void onFailure(Call<ApiResult<GetAddressBookRespDto>> call, Throwable t) {
                t.printStackTrace();
                if(isAdded()){
                    mAddressEv.show(getResources().getString(R.string.emptyView_mode_desc_fail_title), null);
                }
            }
        });
    }

    private void handleAddressList(GetAddressBookRespDto getAddressBookRespDto) {
        refreshLayout.setEnableLoadMore(true);
        page = getAddressBookRespDto.getPage();
        addressList.addAll(getAddressBookRespDto.getAddressBookList());
        if(addressAdapter == null || pageStart == 1){
            addressAdapter = new AddressAdapter(addressList,getContext());
            addressAdapter.setPage(page);
            mAddressBookLv.setAdapter(addressAdapter);
        }else {
            addressAdapter.loadMore(getAddressBookRespDto.getAddressBookList());
        }
        if(AddressClickEventEnum.CHOOSE.getCode().equals(flag)){
            mAddressBookLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    GetAddressBookRespDto.AddressBookListBean currentItem = (GetAddressBookRespDto.AddressBookListBean)addressAdapter.getItem(position);
                    Intent intent = new Intent();
                    intent.putExtra("destAddress",currentItem.getLinkmanAddress());
                    setFragmentResult(RESULT_OK, intent);
                    popBackStack();
                }
            });
        }else if(AddressClickEventEnum.EDIT.getCode().equals(flag)){
            mAddressBookLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    GetAddressBookRespDto.AddressBookListBean currentItem = (GetAddressBookRespDto.AddressBookListBean)addressAdapter.getItem(position);
                    Bundle argz = new Bundle();
                    argz.putString("oldAddressName",currentItem.getNickName());
                    argz.putString("oldLinkmanAddress",currentItem.getLinkmanAddress());
                    argz.putString("oldAddressDescribe",currentItem.getRemark());
                    argz.putString("flag",flag);
                    BPAddressEditFragment bpAddressEditFragment = new BPAddressEditFragment();
                    bpAddressEditFragment.setArguments(argz);
                    startFragment(bpAddressEditFragment);
                }
            });
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
                Bundle argz = new Bundle();
                argz.putString("flag",flag);
                BPAddressAddFragment bpAddressAddFragment = new BPAddressAddFragment();
                bpAddressAddFragment.setArguments(argz);
                startFragment(bpAddressAddFragment);
            }
        });
    }
}
