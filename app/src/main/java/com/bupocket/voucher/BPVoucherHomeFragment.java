package com.bupocket.voucher;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Html;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bupocket.R;
import com.bupocket.base.AbsBaseFragment;
import com.bupocket.common.ConstantsType;
import com.bupocket.enums.ExceptionEnum;
import com.bupocket.fragment.BPCollectionFragment;
import com.bupocket.fragment.BPWalletsHomeFragment;
import com.bupocket.http.api.RetrofitFactory;
import com.bupocket.http.api.dto.resp.ApiResult;
import com.bupocket.utils.WalletCurrentUtils;
import com.bupocket.utils.WalletLocalInfoUtil;
import com.bupocket.voucher.adapter.VoucherAdapter;
import com.bupocket.voucher.http.VoucherService;
import com.bupocket.voucher.model.VoucherDetailModel;
import com.bupocket.voucher.model.VoucherListModel;
import com.qmuiteam.qmui.widget.QMUIEmptyView;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BPVoucherHomeFragment extends AbsBaseFragment {


    @BindView(R.id.topbar)
    QMUITopBarLayout mTopBar;
    @BindView(R.id.refreshComLv)
    ListView voucherListLv;
    @BindView(R.id.reloadBtn)
    QMUIRoundButton reloadBtn;
    @BindView(R.id.loadFailedLL)
    LinearLayout loadFailedLL;
    @BindView(R.id.shareVoucherQrCodeBtn)
    Button shareVoucherQrCodeBtn;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.qmuiEmptyView)
    QMUIEmptyView qmuiEmptyView;
    @BindView(R.id.voucherEmptyLL)
    LinearLayout voucherEmptyLL;
    @BindView(R.id.sendNameTv)
    TextView sendNameTv;


    private VoucherService voucherService;

    private int pageStart;
    private final static int pageSize = 10;

    private VoucherAdapter adapter;
    private VoucherListModel voucherListModel;
    private boolean isSendVoucher;
    private SelectedVoucherListener mSeletedVoucherListener;
    private VoucherDetailModel selectedVoucherDetail;

    @Override
    protected int getLayoutView() {
        return R.layout.fragment_voucher_home_fragment;
    }

    @Override
    protected void initView() {

        if (getArguments() != null) {
            isSendVoucher = getArguments().getString(ConstantsType.FRAGMENT_TAG, "").equals(BPSendTokenVoucherFragment.class.getSimpleName());
            selectedVoucherDetail = ((VoucherDetailModel) getArguments().getSerializable("selectedVoucherDetail"));
        }
        initTopbar();
        initListView();
        refreshLayout.setEnableLoadMore(false);

    }

    private void initListView() {
        adapter = new VoucherAdapter(mContext);
        adapter.setSelectedVoucherDetailModel(selectedVoucherDetail);
        voucherListLv.setAdapter(adapter);

    }

    private void initTopbar() {

        if (isSendVoucher) {
            mTopBar.addLeftImageButton(R.mipmap.icon_tobar_left_arrow, R.id.topbar_left_arrow).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popBackStack();
                }
            });

            sendNameTv.setVisibility(View.VISIBLE);
            sendNameTv.setText(Html.fromHtml(String.format(mContext.getString(R.string.send_name_hint), WalletCurrentUtils.getWalletName(getWalletAddress(),spHelper))));
        } else {
            mTopBar.addLeftImageButton(R.mipmap.icon_voucher_qrcode, R.id.topbar_left_arrow).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goCollectionFragment();
                }
            });
            mTopBar.addRightImageButton(R.mipmap.icon_voucher_green, R.id.topbar_right_button).setOnClickListener(new View.OnClickListener() {


                @Override
                public void onClick(View v) {
                    BPWalletsHomeFragment fragment = new BPWalletsHomeFragment();
                    Bundle args = new Bundle();
                    args.putString(ConstantsType.FRAGMENT_TAG, BPVoucherHomeFragment.class.getSimpleName());
                    fragment.setArguments(args);
                    startFragment(fragment);
                }
            });
        }


    }

    private void goCollectionFragment() {
        BPCollectionFragment fragment = new BPCollectionFragment();
        Bundle args = new Bundle();
        args.putString(ConstantsType.FRAGMENT_TAG, BPVoucherHomeFragment.class.getSimpleName());
        fragment.setArguments(args);
        startFragment(fragment);
    }

    @Override
    protected void initData() {
        reqVoucherAllData(1);
    }

    @Override
    protected void setListeners() {

        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull final RefreshLayout refreshLayout) {
                refreshData();
                refreshLayout.finishRefresh();
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull final RefreshLayout refreshLayout) {

                loadMoreData();
            }
        });

        reloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshLayout.autoRefresh(0, 200, 1, false);
                reqVoucherAllData(1);
            }
        });

        voucherListLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (isSendVoucher) {
                    mSeletedVoucherListener.getSelectedDetail(adapter.getData().get(position));
                    popBackStack();

                } else {
                    goDetailFragment(position);

                }


            }
        });


    }

    private void goDetailFragment(int position) {
        VoucherDetailModel voucherDetailModel = adapter.getData().get(position);
        BPVoucherDetailFragment fragment = new BPVoucherDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable(ConstantsType.VOUCHER_DETAIL, voucherDetailModel);
        fragment.setArguments(args);
        startFragment(fragment);
    }

    private void refreshData() {
        reqVoucherAllData(1);
    }

    private void loadMoreData() {
        refreshLayout.getLayout().postDelayed(new Runnable() {


            @Override
            public void run() {

                if (voucherListModel.getPage().getPageStart() == voucherListModel.getPage().getPageTotal()) {
                    refreshLayout.finishLoadMore();
                    refreshLayout.finishLoadMoreWithNoMoreData();
                    return;
                }
                int curPageStart = pageStart + 1;
                reqVoucherAllData(curPageStart);
                refreshLayout.finishLoadMore();

            }
        }, 500);

    }


    private void reqVoucherAllData(final int index) {

        HashMap<String, Object> map = new HashMap<>();
        map.put(ConstantsType.ADDRESS, WalletLocalInfoUtil.getInstance(spHelper).getWalletAddress());
//        map.put(ConstantsType.ADDRESS, "buQrp3BCVdfbb5mJjNHZQwHvecqe7CCcounY");
        map.put(ConstantsType.PAGE_START, index);
        map.put(ConstantsType.PAGE_SIZE, pageSize);

        voucherService = RetrofitFactory.getInstance().getRetrofit().create(VoucherService.class);

        voucherService.getVoucherPackage(map).enqueue(new Callback<ApiResult<VoucherListModel>>() {
            @Override
            public void onResponse(Call<ApiResult<VoucherListModel>> call, Response<ApiResult<VoucherListModel>> response) {
                ApiResult<VoucherListModel> body = response.body();
                if (loadFailedLL != null) {
                    loadFailedLL.setVisibility(View.GONE);
                }
                if (body != null && ExceptionEnum.SUCCESS.getCode().equals(body.getErrCode()) && body.getData().getVoucherList().size() > 0) {
                    voucherListModel = body.getData();
                    if (index == 1) {
                        adapter.setNewData(body.getData().getVoucherList());
                        refreshLayout.setEnableLoadMore(true);
                        pageStart = index;
                    } else {
                        adapter.addMoreDataList(body.getData().getVoucherList());
                        pageStart = index;
                    }

                    if (voucherEmptyLL != null) {
                        voucherEmptyLL.setVisibility(View.GONE);
                    }

                } else {
                    if (index==1) {
                        if (voucherEmptyLL != null) {
                            voucherEmptyLL.setVisibility(View.VISIBLE);
                        }
                    }


                }

            }

            @Override
            public void onFailure(Call<ApiResult<VoucherListModel>> call, Throwable t) {
                if (loadFailedLL != null) {
                    loadFailedLL.setVisibility(View.VISIBLE);
                    adapter.setNewData(new ArrayList<VoucherDetailModel>());
                }

            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
        if (isSendVoucher) {
            mTopBar.setTitle(R.string.send_voucher_title2);
        } else {
            String walletName = WalletCurrentUtils.getWalletName(WalletCurrentUtils.getWalletAddress(spHelper), spHelper);
            mTopBar.setTitle(walletName);
        }
    }


    @OnClick(R.id.shareVoucherQrCodeBtn)
    public void onViewClicked() {
        goCollectionFragment();
    }


    public interface SelectedVoucherListener {

        void getSelectedDetail(VoucherDetailModel voucherDetailModel);
    }

    public void setSelectedVoucherListener(SelectedVoucherListener mSeletedVoucherListener) {
        this.mSeletedVoucherListener = mSeletedVoucherListener;
    }
}
