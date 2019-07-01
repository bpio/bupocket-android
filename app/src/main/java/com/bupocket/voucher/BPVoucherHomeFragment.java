package com.bupocket.voucher;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.bupocket.R;
import com.bupocket.base.AbsBaseFragment;
import com.bupocket.common.ConstantsType;
import com.bupocket.enums.ExceptionEnum;
import com.bupocket.fragment.BPCollectionFragment;
import com.bupocket.fragment.BPWalletsHomeFragment;
import com.bupocket.http.api.RetrofitFactory;
import com.bupocket.http.api.dto.resp.ApiResult;
import com.bupocket.utils.WalletLocalInfoUtil;
import com.bupocket.voucher.adapter.VoucherAdapter;
import com.bupocket.voucher.http.VoucherService;
import com.bupocket.voucher.model.VoucherListModel;
import com.qmuiteam.qmui.widget.QMUIEmptyView;
import com.qmuiteam.qmui.widget.QMUITopBar;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BPVoucherHomeFragment extends AbsBaseFragment {


    @BindView(R.id.topbar)
    QMUITopBar mTopBar;
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



    private VoucherService voucherService;


    private final static int pageSize = 10;
    private VoucherAdapter adapter;

    @Override
    protected int getLayoutView() {
        return R.layout.fragment_voucher_home_fragment;
    }

    @Override
    protected void initView() {

        initTopbar();
        initListView();

    }

    private void initListView() {
        adapter = new VoucherAdapter(mContext);
        voucherListLv.setAdapter(adapter);
    }

    private void initTopbar() {
        mTopBar.addLeftImageButton(R.mipmap.icon_voucher_qrcode, R.id.topbar_left_arrow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startFragment(new BPCollectionFragment());
            }
        });
        mTopBar.setTitle(getResources().getString(R.string.invite_share_txt));
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

    @Override
    protected void initData() {


        reqVoucherAllData(1);




    }

    private void reqVoucherAllData(int index) {
        HashMap<String, Object> map = new HashMap<>();
//        map.put(ConstantsType.ADDRESS, WalletLocalInfoUtil.getInstance(spHelper).getWalletAddress());
        map.put(ConstantsType.ADDRESS, "buQrp3BCVdfbb5mJjNHZQwHvecqe7CCcounY");
        map.put(ConstantsType.PAGE_START, index);
        map.put(ConstantsType.PAGE_SIZE, pageSize);

        voucherService = RetrofitFactory.getInstance().getRetrofit().create(VoucherService.class);

        voucherService.getVoucherPackage(map).enqueue(new Callback<ApiResult<VoucherListModel>>() {
            @Override
            public void onResponse(Call<ApiResult<VoucherListModel>> call, Response<ApiResult<VoucherListModel>> response) {
                ApiResult<VoucherListModel> body = response.body();

                if (body!=null&&ExceptionEnum.SUCCESS.getCode().equals(body.getErrCode())&&body.getData().getVoucherList().size()>0) {
                    adapter.setNewData(body.getData().getVoucherList());
                    if (voucherEmptyLL!=null) {
                        voucherEmptyLL.setVisibility(View.GONE);
                    }
                    if (loadFailedLL!=null) {
                        loadFailedLL.setVisibility(View.GONE);
                    }

                }else{
                    if (voucherEmptyLL!=null) {
                        voucherEmptyLL.setVisibility(View.VISIBLE);
                    }

                }

            }

            @Override
            public void onFailure(Call<ApiResult<VoucherListModel>> call, Throwable t) {
                if (loadFailedLL!=null) {
                    loadFailedLL.setVisibility(View.VISIBLE);
                }

            }
        });
    }

    @Override
    protected void setListeners() {

    }


    @Override
    public void onResume() {
        super.onResume();
        mTopBar.setTitle(WalletLocalInfoUtil.getInstance(spHelper).getWalletName());
    }


    @OnClick(R.id.shareVoucherQrCodeBtn)
    public void onViewClicked() {
    }
}
