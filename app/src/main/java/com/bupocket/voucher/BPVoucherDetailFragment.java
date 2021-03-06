package com.bupocket.voucher;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bupocket.R;
import com.bupocket.base.AbsBaseFragment;
import com.bupocket.common.ConstantsType;
import com.bupocket.database.greendao.VoucherPackageDetailModelDao;
import com.bupocket.enums.ExceptionEnum;
import com.bupocket.enums.VoucherStatusEnum;
import com.bupocket.http.api.RetrofitFactory;
import com.bupocket.http.api.dto.resp.ApiResult;
import com.bupocket.utils.ToastUtil;
import com.bupocket.utils.WalletCurrentUtils;
import com.bupocket.voucher.http.VoucherService;
import com.bupocket.voucher.model.VoucherAcceptanceBean2;
import com.bupocket.voucher.model.VoucherDetailModel;
import com.bupocket.voucher.model.VoucherIssuerBean2;
import com.bupocket.voucher.model.VoucherPackageDetailModel;
import com.makeramen.roundedimageview.RoundedImageView;
import com.qmuiteam.qmui.widget.QMUIEmptyView;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BPVoucherDetailFragment extends AbsBaseFragment {


    @BindView(R.id.leftTopbarIv)
    ImageView leftTopbarIv;
    @BindView(R.id.titleTopbarTv)
    TextView titleTopbarTv;
    @BindView(R.id.acceptanceIconRiv)
    RoundedImageView acceptanceIconRiv;
    @BindView(R.id.topLineV)
    View topLineV;
    @BindView(R.id.acceptanceNameTv)
    TextView acceptanceNameTv;
    @BindView(R.id.voucherGoodsIv)
    ImageView voucherGoodsIv;
    @BindView(R.id.goodsNameTv)
    TextView goodsNameTv;
    @BindView(R.id.goodsPriceTv)
    TextView goodsPriceTv;
    @BindView(R.id.validityDateTv)
    TextView validityDateTv;
    @BindView(R.id.validityDateInfoTv)
    TextView validityDateInfoTv;
    @BindView(R.id.voucherCodeTv)
    TextView voucherCodeTv;
    @BindView(R.id.voucherCodeInfoTv)
    TextView voucherCodeInfoTv;
    @BindView(R.id.voucherSizeTv)
    TextView voucherSizeTv;
    @BindView(R.id.voucherSizeInfoTv)
    TextView voucherSizeInfoTv;
    @BindView(R.id.voucherDescribeTv)
    TextView voucherDescribeTv;
    @BindView(R.id.voucherDescribeInfoTv)
    TextView voucherDescribeInfoTv;
    @BindView(R.id.numLineTv)
    View numLineTv;
    @BindView(R.id.voucherNumTv)
    TextView voucherNumTv;
    @BindView(R.id.voucherNumInfoTv)
    TextView voucherNumInfoTv;
    @BindView(R.id.transferVoucherBtn)
    Button transferVoucherBtn;
    @BindView(R.id.dPartyRiv)
    RoundedImageView dPartyRiv;
    @BindView(R.id.dPartyTv)
    TextView dPartyTv;
    @BindView(R.id.dPartyLL)
    LinearLayout dPartyLL;
    @BindView(R.id.assetIssuerRiv)
    RoundedImageView assetIssuerRiv;
    @BindView(R.id.assetIssuerTv)
    TextView assetIssuerTv;
    @BindView(R.id.assetIssuerLL)
    LinearLayout assetIssuerLL;
    @BindView(R.id.detailEmptyView)
    QMUIEmptyView detailEmptyView;
    @BindView(R.id.voucherDetailLl)
    LinearLayout voucherDetailLl;
    @BindView(R.id.voucherEmptyIv)
    ImageView voucherEmptyIv;


    private VoucherDetailModel voucherDetailModel;
    private VoucherPackageDetailModel detailModel;
    private VoucherPackageDetailModelDao voucherPackageDetailModelDao;
    private VoucherPackageDetailModel voucherPackageDetailModelDataBase;


    @Override
    protected int getLayoutView() {
        return R.layout.fragment_voucher_detail;
    }

    @Override
    protected void initView() {

        initTopBar();

    }


    private void initTopBar() {

        titleTopbarTv.setText(R.string.voucher_detail_title);
    }

    @Override
    protected void initData() {

        Bundle arguments = getArguments();
        if (arguments != null) {
            voucherDetailModel = (VoucherDetailModel) arguments.getSerializable(ConstantsType.VOUCHER_DETAIL);
        }
        if (voucherDetailModel != null) {
            reqDetailData();
        }
    }

    private void reqDetailData() {
        voucherPackageDetailModelDao = mApplication.getDaoSession().getVoucherPackageDetailModelDao();


        voucherPackageDetailModelDataBase = queryDataBase();
        if (voucherPackageDetailModelDataBase !=null) {
            refreshView(voucherPackageDetailModelDataBase);
        }

        VoucherService voucherService = RetrofitFactory.getInstance().getRetrofit().create(VoucherService.class);
        HashMap<String, Object> map = new HashMap<>();
        map.put(ConstantsType.ADDRESS, getWalletAddress());
        map.put(ConstantsType.VOUCHER_ID, voucherDetailModel.getVoucherId());
        map.put(ConstantsType.TRANCHE_ID, voucherDetailModel.getTrancheId());
        map.put(ConstantsType.SPU_ID, voucherDetailModel.getSpuId());
        map.put(ConstantsType.CONTRACT_ADDRESS, voucherDetailModel.getContractAddress());

        detailEmptyView.show(true);
        voucherService.getVoucherPackageDeatil(map).enqueue(new Callback<ApiResult<VoucherPackageDetailModel>>() {
            @Override
            public void onResponse(Call<ApiResult<VoucherPackageDetailModel>> call,
                                   Response<ApiResult<VoucherPackageDetailModel>> response) {
                if (detailEmptyView == null) {
                    return;
                }
                detailEmptyView.show("", "");

                ApiResult<VoucherPackageDetailModel> body = response.body();
                if (body != null && ExceptionEnum.SUCCESS.getCode().equals(body.getErrCode())) {
                    detailModel = body.getData();

                    insetDataBase(detailModel);
                    refreshView(detailModel);
                }


            }

            @Override
            public void onFailure(Call<ApiResult<VoucherPackageDetailModel>> call, Throwable t) {
                if (detailEmptyView == null) {
                    return;
                }
                detailEmptyView.show("", "");

                ToastUtil.showToast(getActivity(),R.string.network_error_msg, Toast.LENGTH_SHORT);

            }
        });
    }

    private void insetDataBase(VoucherPackageDetailModel detailModel) {

        if (voucherPackageDetailModelDataBase!=null&&voucherPackageDetailModelDataBase.getContractAddress().equals(detailModel.getContractAddress())) {
            return;
        }

        detailModel.setAddress(getWalletAddress());
        voucherPackageDetailModelDao.insert(detailModel);
    }



    private VoucherPackageDetailModel queryDataBase() {

        VoucherPackageDetailModel voucherPackageDetailModel = voucherPackageDetailModelDao.queryBuilder().where(
                VoucherPackageDetailModelDao.Properties.Address.eq(getWalletAddress()),
                VoucherPackageDetailModelDao.Properties.VoucherId.eq(voucherDetailModel.getVoucherId()),
                VoucherPackageDetailModelDao.Properties.TrancheId.eq(voucherDetailModel.getTrancheId()),
                VoucherPackageDetailModelDao.Properties.SpuId.eq(voucherDetailModel.getSpuId()),
                VoucherPackageDetailModelDao.Properties.ContractAddress.eq(voucherDetailModel.getContractAddress())).unique();


        return voucherPackageDetailModel;
    }

    private void refreshView(VoucherPackageDetailModel detailModel) {
        this.detailModel=detailModel;
        VoucherAcceptanceBean2 voucherAcceptance = detailModel.getVoucherAcceptance();
        if (voucherAcceptance != null) {
            String icon = voucherAcceptance.getIcon();
            if (!TextUtils.isEmpty(icon) && acceptanceIconRiv != null) {
                Glide.with(mContext)
                        .load(icon)
                        .into(acceptanceIconRiv);
            }
            String name = voucherAcceptance.getName();
            if (!TextUtils.isEmpty(name)) {
                acceptanceNameTv.setText(name);
            }

            voucherDetailLl.setVisibility(View.VISIBLE);
            voucherEmptyIv.setBackgroundResource(R.mipmap.icon_voucher_detail_bg);
        }
        String voucherIcon = detailModel.getVoucherIcon();
        if (!TextUtils.isEmpty(voucherIcon) && voucherGoodsIv != null) {
            Glide.with(mContext)
                    .load(voucherIcon)
                    .into(voucherGoodsIv);
        }

        String voucherName = detailModel.getVoucherName();
        if (!TextUtils.isEmpty(voucherName)) {
            goodsNameTv.setText(voucherName);
        }

        String faceValue = detailModel.getFaceValue();
        if (!TextUtils.isEmpty(faceValue)) {
            goodsPriceTv.setText(getString(R.string.goods_price) + faceValue);
        }


        String startTime = detailModel.getStartTime();
        String endTime = detailModel.getEndTime();
        String date = WalletCurrentUtils.voucherDate(startTime, endTime, mContext);
        validityDateInfoTv.setText(date);

        voucherCodeInfoTv.setText(detailModel.getVoucherId());
        String voucherSpecNormal = getString(R.string.no_description);

        String voucherSpec = detailModel.getVoucherSpec();
        if (!TextUtils.isEmpty(voucherSpec)) {
            voucherSpecNormal = voucherSpec;
        }
        voucherSizeInfoTv.setText(voucherSpecNormal);
        String description = detailModel.getDescription();
        if (!TextUtils.isEmpty(description)) {
            voucherDescribeInfoTv.setText(description);
        }
        voucherNumInfoTv.setText(voucherDetailModel.getBalance());
        String acceptanceIcon = detailModel.getVoucherAcceptance().getIcon();
        if (!TextUtils.isEmpty(acceptanceIcon)) {
            Glide.with(mContext)
                    .load(acceptanceIcon)
                    .into(dPartyRiv);
        }
        dPartyTv.setText(detailModel.getVoucherAcceptance().getName());

        String icon = detailModel.getVoucherIssuer().getIcon();
        if (!TextUtils.isEmpty(icon)) {
            Glide.with(mContext)
                    .load(icon)
                    .into(assetIssuerRiv);
        }
        assetIssuerTv.setText(detailModel.getVoucherIssuer().getName());

    }

    @Override
    protected void setListeners() {
        leftTopbarIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popBackStack();
            }
        });
    }


    @OnClick({R.id.transferVoucherBtn, R.id.dPartyLL, R.id.assetIssuerLL})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.transferVoucherBtn:
                goSendVoucherFragment();
                break;
            case R.id.dPartyLL:
                goAcceptanceFragment();
                break;
            case R.id.assetIssuerLL:
                goAssetIssuer();
                break;
        }
    }

    private void goSendVoucherFragment() {
        getFragmentManager().findFragmentByTag(BPVoucherDetailFragment.class.getSimpleName());
        BPSendTokenVoucherFragment fragment = new BPSendTokenVoucherFragment();
        fragment.setDetailVoucher(voucherDetailModel);
        Bundle args = new Bundle();
        args.putString(ConstantsType.FRAGMENT_TAG, VoucherStatusEnum.VOUCHER_HOME_FRAGMENT.getCode());
        fragment.setArguments(args);
        startFragment(fragment);

    }

    private void goAssetIssuer() {
        BPVoucherIssuerFragment fragment = new BPVoucherIssuerFragment();
        VoucherIssuerBean2 voucherIssuer = detailModel.getVoucherIssuer();
        Bundle args = new Bundle();
        if (voucherIssuer != null) {
            args.putSerializable("voucherIssuer", voucherIssuer);
        }
        fragment.setArguments(args);
        startFragment(fragment);
    }

    private void goAcceptanceFragment() {

        VoucherAcceptanceBean2 voucherAcceptance = detailModel.getVoucherAcceptance();
        BPAcceptanceFragment fragment = new BPAcceptanceFragment();
        Bundle args = new Bundle();
        if (voucherAcceptance != null) {
            args.putSerializable("voucherAcceptance", voucherAcceptance);
        }
        fragment.setArguments(args);
        startFragment(fragment);
    }

}
