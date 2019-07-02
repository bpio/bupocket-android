package com.bupocket.voucher;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bupocket.R;
import com.bupocket.base.AbsBaseFragment;
import com.bupocket.common.Constants;
import com.bupocket.common.ConstantsType;
import com.bupocket.enums.ExceptionEnum;
import com.bupocket.http.api.RetrofitFactory;
import com.bupocket.http.api.dto.resp.ApiResult;
import com.bupocket.utils.TimeUtil;
import com.bupocket.voucher.http.VoucherService;
import com.bupocket.voucher.model.VoucherDetailModel;
import com.bupocket.voucher.model.VoucherPackageDetailModel;
import com.makeramen.roundedimageview.RoundedImageView;
import com.qmuiteam.qmui.widget.QMUITopBar;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.bumo.common.ToBaseUnit;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BPVoucherDetailFragment extends AbsBaseFragment {

    @BindView(R.id.topbar)
    QMUITopBarLayout topbar;
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


    private VoucherDetailModel voucherDetailModel;

    @Override
    protected int getLayoutView() {
        return R.layout.fragment_voucher_detail;
    }

    @Override
    protected void initView() {

        initTopBar();
    }

    @SuppressLint("ResourceAsColor")
    private void initTopBar() {
        topbar.setTitle(R.string.voucher_detail_title);
        topbar.addLeftImageButton(R.mipmap.icon_tobar_left_arrow, R.id.topbar_left_arrow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popBackStack();
            }
        });
        topbar.setBackgroundColor(R.color.app_color_blue1);

    }

    @Override
    protected void initData() {

        Bundle arguments = getArguments();
        if (arguments != null) {
            voucherDetailModel = (VoucherDetailModel) arguments.getSerializable(ConstantsType.VOUCHER_DETAIL);
        }

        reqDetailData();

    }

    private void reqDetailData() {

        VoucherService voucherService = RetrofitFactory.getInstance().getRetrofit().create(VoucherService.class);
        HashMap<String, Object> map = new HashMap<>();
//        map.put(ConstantsType.ADDRESS,getWalletAddress());
        map.put(ConstantsType.ADDRESS,"buQrp3BCVdfbb5mJjNHZQwHvecqe7CCcounY");
        map.put(ConstantsType.VOUCHER_ID,voucherDetailModel.getVoucherId());
        map.put(ConstantsType.TRANCHE_ID,voucherDetailModel.getTrancheId());
        map.put(ConstantsType.SPU_ID,voucherDetailModel.getSpuId());
        map.put(ConstantsType.CONTRACT_ADDRESS,voucherDetailModel.getContractAddress());


        voucherService.getVoucherPackageDeatil(map).enqueue(new Callback<ApiResult<VoucherPackageDetailModel>>() {
            @Override
            public void onResponse(Call<ApiResult<VoucherPackageDetailModel>> call,
                                   Response<ApiResult<VoucherPackageDetailModel>> response) {
                ApiResult<VoucherPackageDetailModel> body = response.body();
                if (body != null && ExceptionEnum.SUCCESS.getCode().equals(body.getErrCode())) {

                    VoucherPackageDetailModel detailModel = body.getData();



                    VoucherPackageDetailModel.VoucherAcceptanceBean voucherAcceptance = detailModel.getVoucherAcceptance();
                    if (voucherAcceptance!=null) {
                        Glide.with(mContext)
                                .load(voucherAcceptance.getIcon())
                                .into(acceptanceIconRiv);
                        acceptanceNameTv.setText(voucherAcceptance.getName());
                    }
                    Glide.with(mContext)
                            .load(voucherAcceptance)
                            .into(voucherGoodsIv);
                    goodsNameTv.setText(detailModel.getVoucherName());
                    goodsPriceTv.setText(getString(R.string.goods_price)+detailModel.getFaceValue());

                    validityDateInfoTv.setText(String.format(mContext.getString(R.string.goods_validity_date),
                            TimeUtil.timeStamp2Date(detailModel.getStartTime(),TimeUtil.TIME_TYPE_YYYYY_MM_DD),
                            TimeUtil.timeStamp2Date(detailModel.getEndTime(),TimeUtil.TIME_TYPE_YYYYY_MM_DD)));

                    voucherCodeInfoTv.setText(detailModel.getVoucherId());
                    voucherSizeInfoTv.setText(detailModel.getVoucherSpec());
                    voucherDescribeInfoTv.setText(detailModel.getDescription());
                    voucherNumInfoTv.setText(voucherDetailModel.getBalance());

                    Glide.with(mContext)
                            .load(detailModel.getVoucherAcceptance().getIcon())
                            .into(dPartyRiv);

                    dPartyTv.setText(detailModel.getVoucherAcceptance().getName());


                    Glide.with(mContext)
                            .load(detailModel.getVoucherIssuer().getIcon())
                            .into(assetIssuerRiv);

                    assetIssuerTv.setText(detailModel.getVoucherIssuer().getName());



                }


            }

            @Override
            public void onFailure(Call<ApiResult<VoucherPackageDetailModel>> call, Throwable t) {

            }
        });
    }

    @Override
    protected void setListeners() {

    }


    @OnClick({R.id.transferVoucherBtn, R.id.dPartyLL, R.id.assetIssuerLL})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.transferVoucherBtn:


                break;
            case R.id.dPartyLL:
                break;
            case R.id.assetIssuerLL:
                break;
        }
    }
}
