package com.bupocket.fragment.discover;

import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bupocket.R;
import com.bupocket.adaptor.NodeBuildDetailAdapter;
import com.bupocket.base.BaseFragment;
import com.bupocket.common.Constants;
import com.bupocket.common.SingatureListener;
import com.bupocket.enums.CoBuildDetailStatusEnum;
import com.bupocket.enums.ExceptionEnum;
import com.bupocket.http.api.NodeBuildService;
import com.bupocket.http.api.RetrofitFactory;
import com.bupocket.http.api.dto.resp.ApiResult;
import com.bupocket.http.api.dto.resp.GetTokensRespDto;
import com.bupocket.model.NodeBuildDetailModel;
import com.bupocket.model.NodeBuildSupportModel;
import com.bupocket.model.TransConfirmModel;
import com.bupocket.utils.CommonUtil;
import com.bupocket.utils.LogUtils;
import com.bupocket.utils.ToastUtil;
import com.bupocket.utils.TransferUtils;
import com.bupocket.wallet.Wallet;
import com.bupocket.wallet.exception.WalletException;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.qmuiteam.qmui.widget.QMUITopBar;
import com.qmuiteam.qmui.widget.dialog.QMUIBottomSheet;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.qmuiteam.qmui.widget.popup.QMUIPopup;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.bumo.model.response.TransactionBuildBlobResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class BPNodeBuildDetailFragment extends BaseFragment {

    @BindView(R.id.topbar)
    QMUITopBar mTopBar;
    @BindView(R.id.lvRefresh)
    ListView lvBuildDetail;
    @BindView(R.id.btnBuildExit)
    Button btnBuildExit;
    @BindView(R.id.btnBuildSupport)
    Button btnBuildSupport;
    @BindView(R.id.llBtnBuild)
    LinearLayout llBtnBuild;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.llLoadFailed)
    LinearLayout llLoadFailed;
    @BindView(R.id.addressRecordEmptyLL)
    LinearLayout addressRecordEmptyLL;
    @BindView(R.id.copyCommandBtn)
    QMUIRoundButton copyCommandBtn;


    private Unbinder bind;
    private NodeBuildDetailAdapter nodeBuildDetailAdapter;
    private TextView tvStutas;
    private View addBtn;
    private View subBtn;
    private TextView tvDialogTotalAmount;
    private TextView tvDialogAmount;
    private EditText numSupport;
    private TextView tvName;
    private TextView tvBuildDetailAmount;
    private TextView tvTotalAmount;
    private ProgressBar pbDetail;
    private TextView tvBuildDetailLeftCopies;
    private TextView tvBuildDetailSharingRatio;
    private TextView tvBuildDetailOriginAmount;
    private TextView tvBuildDetailSupportNum;
    private TextView tvBuildDetailSupportAmount;
    private NodeBuildDetailModel detailModel;
    private String nodeId;
    private GetTokensRespDto.TokenListBean tokenListBean;
    private boolean isExit;

    final String inputExit = "{\"method\":\"revoke\"}";
    private String inputSupport;
    private View ivSheetHint;
    private QMUIPopup myNodeExplainPopup;
    private TextView tvProgress;
    private View headerView;
    private String transMetaData;
    private String supportTransMetaData;
    private String amountExit;

    @Override
    protected View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_bpnode_build_detail, null);
        bind = ButterKnife.bind(this, root);
        init();
        return root;
    }

    private void init() {
        initTopBar();
        initData();
        initListener();
    }

    private void initListener() {
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(final RefreshLayout refreshlayout) {

                getBuildData();
                refreshlayout.finishRefresh();
                refreshLayout.setNoMoreData(false);
            }
        });
        refreshLayout.setEnableLoadMore(false);

        copyCommandBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshLayout.autoRefresh(200);
            }
        });
    }

    private void initData() {
        GetTokensRespDto tokensCache = JSON.parseObject(spHelper.getSharedPreference(getWalletAddress() + "tokensInfoCache", "").toString(), GetTokensRespDto.class);
        tokenListBean = tokensCache.getTokenList().get(0);
        if (nodeBuildDetailAdapter == null) {
            nodeBuildDetailAdapter = new NodeBuildDetailAdapter(getContext());
        }
        lvBuildDetail.setAdapter(nodeBuildDetailAdapter);
        refreshLayout.setEnableLoadMore(false);

        headerView = LayoutInflater.from(getActivity()).inflate(R.layout.node_build_header, null);
        tvStutas = ((TextView) headerView.findViewById(R.id.tvNodeBuilding));
        tvName = headerView.findViewById(R.id.tvBuildDetailName);
        tvBuildDetailAmount = headerView.findViewById(R.id.tvBuildDetailAmount);
        tvTotalAmount = ((TextView) headerView.findViewById(R.id.tvDetailTotalAmount));
        pbDetail = ((ProgressBar) headerView.findViewById(R.id.pbBuild));
        tvProgress = ((TextView) headerView.findViewById(R.id.tvProgress));

        tvBuildDetailLeftCopies = ((TextView) headerView.findViewById(R.id.tvBuildDetailLeftCopies));
        tvBuildDetailSharingRatio = ((TextView) headerView.findViewById(R.id.tvBuildDetailSharingRatio));
        tvBuildDetailOriginAmount = ((TextView) headerView.findViewById(R.id.tvBuildDetailOriginAmount));
        tvBuildDetailSupportNum = ((TextView) headerView.findViewById(R.id.tvBuildDetailSupportNum));
        tvBuildDetailSupportAmount = ((TextView) headerView.findViewById(R.id.tvBuildDetailSupportAmount));
        tvTotalAmount = ((TextView) headerView.findViewById(R.id.tvDetailTotalAmount));
        tvTotalAmount = ((TextView) headerView.findViewById(R.id.tvDetailTotalAmount));
        ivSheetHint = headerView.findViewById(R.id.ivSheetHint);
        ivSheetHint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myNodeExplainPopup == null) {
                    myNodeExplainPopup = new QMUIPopup(getContext(), QMUIPopup.DIRECTION_NONE);
                    TextView textView = new TextView(getContext());
                    textView.setLayoutParams(myNodeExplainPopup.generateLayoutParam(
                            QMUIDisplayHelper.dp2px(getContext(), 280),
                            WRAP_CONTENT
                    ));
                    textView.setLineSpacing(QMUIDisplayHelper.dp2px(getContext(), 4), 1.0f);
                    int padding = QMUIDisplayHelper.dp2px(getContext(), 10);
                    textView.setPadding(padding, padding, padding, padding);
                    textView.setText(getString(R.string.build_cooperate_bond));
                    textView.setTextColor(ContextCompat.getColor(getContext(), R.color.app_color_white));
                    textView.setBackgroundColor(getResources().getColor(R.color.popup_background_color));
                    myNodeExplainPopup.setContentView(textView);
                }
                myNodeExplainPopup.setAnimStyle(QMUIPopup.ANIM_GROW_FROM_CENTER);
                myNodeExplainPopup.setPreferredDirection(QMUIPopup.DIRECTION_BOTTOM);
                myNodeExplainPopup.show(v);
                ImageView arrowUp = myNodeExplainPopup.getDecorView().findViewById(R.id.arrow_up);
                arrowUp.setImageDrawable(getResources().getDrawable(R.mipmap.triangle));
            }
        });

        lvBuildDetail.addHeaderView(headerView);
        lvBuildDetail.setVisibility(View.GONE);
//        addressRecordEmptyLL.setVisibility(View.VISIBLE);

        getBuildData();
    }


    private void getBuildData() {

        nodeId = getArguments().getString("nodeId", "");
        if (TextUtils.isEmpty(nodeId)) {
            return;
        }
        HashMap<String, Object> map = new HashMap<>();
        map.put("nodeId", nodeId);

        final NodeBuildService nodeBuildService = RetrofitFactory.getInstance().getRetrofit().create(NodeBuildService.class);

        nodeBuildService.getNodeBuildDetail(map).enqueue(new Callback<ApiResult<NodeBuildDetailModel>>() {
            @Override
            public void onResponse(Call<ApiResult<NodeBuildDetailModel>> call, Response<ApiResult<NodeBuildDetailModel>> response) {
                ApiResult<NodeBuildDetailModel> body = response.body();

                llLoadFailed.setVisibility(View.GONE);
                if (body != null && ExceptionEnum.SUCCESS.getCode().equals(body.getErrCode())) {

                    detailModel = body.getData();
                    if (detailModel == null) {
                        return;
                    }
                    tvName.setText(detailModel.getTitle());
                    tvBuildDetailAmount.setText(CommonUtil.format(detailModel.getPerAmount()));

                    pbDetail.setMax(detailModel.getCobuildCopies());
                    int supportCopies = detailModel.getCobuildCopies() - detailModel.getLeftCopies();
                    pbDetail.setProgress(supportCopies);

                    tvProgress.setText(CommonUtil.setRatio(supportCopies, detailModel.getCobuildCopies()));


                    tvBuildDetailSharingRatio.setText(detailModel.getRewardRate() + "%");

                    tvTotalAmount.setText(CommonUtil.format(detailModel.getTotalAmount()) + getString(R.string.build_bu));
                    tvBuildDetailOriginAmount.setText(CommonUtil.format(detailModel.getInitiatorAmount()) + getString(R.string.build_bu));
                    tvBuildDetailSupportAmount.setText(CommonUtil.format(detailModel.getSupportAmount()) + getString(R.string.build_bu));


                    int build_support_copies;
                    int build_left_copies;
                    if (CommonUtil.isSingle(supportCopies)) {
                        build_support_copies = R.string.build_support_copies;
                    } else {
                        build_support_copies = R.string.build_support_copies_s;

                    }
                    if (CommonUtil.isSingle(detailModel.getLeftCopies())) {
                        build_left_copies = R.string.build_left_copies;
                    } else {
                        build_left_copies = R.string.build_left_copies_s;
                    }


                    tvBuildDetailLeftCopies.setText(Html.fromHtml(String.format(getString(build_support_copies), supportCopies + "")));

                    tvBuildDetailSupportNum.setText(Html.fromHtml(String.format(getString(build_left_copies), detailModel.getLeftCopies() + "")));


                    if (CoBuildDetailStatusEnum.CO_BUILD_RUNING.getCode().equals(detailModel.getStatus())) {
                        tvStutas.setSelected(false);
                        tvStutas.setText(CoBuildDetailStatusEnum.CO_BUILD_RUNING.getMsg());
                    } else {
                        if (CoBuildDetailStatusEnum.CO_BUILD_SUCCESS.getCode().equals(detailModel.getStatus())) {
                            tvStutas.setSelected(true);
                            tvStutas.setText(CoBuildDetailStatusEnum.CO_BUILD_SUCCESS.getMsg());
                        } else if (CoBuildDetailStatusEnum.CO_BUILD_FAILURE.getCode().equals(detailModel.getStatus())) {
                            tvStutas.setSelected(true);
                            tvStutas.setText(CoBuildDetailStatusEnum.CO_BUILD_FAILURE.getMsg());
                        }
                    }

                    isExit = CoBuildDetailStatusEnum.CO_BUILD_FAILURE.getCode().equals(detailModel.getStatus());

                    //origin
                    if (CoBuildDetailStatusEnum.CO_BUILD_RUNING.getCode().equals(detailModel.getStatus()) &&
                            !getWalletAddress().equals(detailModel.getOriginatorAddress())) {
                        llBtnBuild.setVisibility(View.VISIBLE);
                    } else if (CoBuildDetailStatusEnum.CO_BUILD_FAILURE.getCode().equals(detailModel.getStatus())) {
                        llBtnBuild.setVisibility(View.VISIBLE);
                        btnBuildSupport.setVisibility(View.GONE);
                        btnBuildExit.setText(getString(R.string.build_exit_all));
                    }


                    ArrayList<NodeBuildSupportModel> nodelist = body.getData().getSupportList();

                    if (nodelist == null || nodelist.size() == 0) {
                        addressRecordEmptyLL.setVisibility(View.VISIBLE);
                    } else {
                        addressRecordEmptyLL.setVisibility(View.GONE);
                    }
                    lvBuildDetail.setVisibility(View.VISIBLE);
                    nodeBuildDetailAdapter.setNewData(nodelist);
                    nodeBuildDetailAdapter.notifyDataSetChanged();

                }


            }

            @Override
            public void onFailure(Call<ApiResult<NodeBuildDetailModel>> call, Throwable t) {

                llLoadFailed.setVisibility(View.VISIBLE);
                lvBuildDetail.setVisibility(View.GONE);
                llBtnBuild.setVisibility(View.GONE);
            }


        });


    }


    private void initTopBar() {
        mTopBar.setBackgroundDividerEnabled(false);
        mTopBar.addLeftImageButton(R.mipmap.icon_tobar_left_arrow, R.id.topbar_left_arrow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popBackStack();
            }
        });
        mTopBar.setTitle(getResources().getString(R.string.building_information_details));

    }


    @OnClick({R.id.btnBuildExit, R.id.btnBuildSupport})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnBuildExit:

                if (isExit) {
                    showExit();
                } else {
                    showMessagePositiveDialog();
                }

                break;
            case R.id.btnBuildSupport:
                if (detailModel != null) {
                    String accountBUBalance = spHelper.getSharedPreference(getWalletAddress() + "tokenBalance", "0").toString();
                    if (TextUtils.isEmpty(accountBUBalance) || (Double.parseDouble(accountBUBalance) < Double.parseDouble(detailModel.getPerAmount()))) {
                        ToastUtil.showToast(getActivity(), R.string.balance_not_enough, Toast.LENGTH_SHORT);
                        return;
                    }
                    ShowSupport();
                }

                break;
        }
    }

    private void showExit() {
        amountExit = "0";
        transMetaData = String.format(getString(R.string.build_exit_confirm_title), detailModel.getTitle());

        TransferUtils.confirmTxSheet(mContext, detailModel.getOriginatorAddress(), detailModel.getContractAddress(),
                amountExit, Constants.NODE_REVOKE_FEE, transMetaData, inputExit, new TransferUtils.TransferListener() {
                    @Override
                    public void confirm() {
                        confirmExit();
                    }
                });
    }

    private void showMessagePositiveDialog() {
        new QMUIDialog.MessageDialogBuilder(getActivity())
                .setMessage(R.string.confirm_build_exit)
                .addAction(R.string.common_dialog_cancel, new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        dialog.dismiss();
                    }
                })
                .addAction(R.string.common_dialog_confirm, new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        dialog.dismiss();
                        showExit();
                    }
                })
                .create(com.qmuiteam.qmui.R.style.QMUI_Dialog).show();
    }

    private void confirmExit() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final TransactionBuildBlobResponse transBlob = Wallet.getInstance().buildBlob(amountExit, inputExit, getWalletAddress(), String.valueOf(Constants.NODE_CO_BUILD_FEE), detailModel.getContractAddress(), transMetaData);

                    final String hash = transBlob.getResult().getHash();
                    LogUtils.e(hash);
                    if (TextUtils.isEmpty(hash)) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                CommonUtil.showMessageDialog(mContext, transBlob.getErrorDesc());
                            }
                        });
                        return;
                    }

                    getSignatureInfo(new SingatureListener() {
                        @Override
                        public void success(final String privateKey) {
                            final QMUITipDialog txSendingTipDialog = new QMUITipDialog.Builder(getContext())
                                    .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                                    .setTipWord(getResources().getString(R.string.send_tx_verify))
                                    .create();
                            txSendingTipDialog.show();


                            HashMap<String, Object> map = new HashMap<>();
                            map.put("nodeId", detailModel.getNodeId());
                            map.put("hash", hash);
                            map.put("initiatorAddress", getWalletAddress());
                            NodeBuildService nodeBuildService = RetrofitFactory.getInstance().getRetrofit().create(NodeBuildService.class);
                            nodeBuildService.verifyExit(map).enqueue(new Callback<ApiResult<TransConfirmModel>>() {
                                @Override
                                public void onResponse(Call<ApiResult<TransConfirmModel>> call, Response<ApiResult<TransConfirmModel>> response) {
                                    ApiResult<TransConfirmModel> body = response.body();
                                    txSendingTipDialog.dismiss();

                                    if (ExceptionEnum.SUCCESS.getCode().equals(body.getErrCode())) {
                                        submitTransactionBase(privateKey, transBlob);
                                    } else {

                                        if (ExceptionEnum.ERROR_TRANSACTION_OTHER_1011.getCode().equals(body.getErrCode())) {
                                            String expiryTime = body.getData().getExpiryTime();
                                            CommonUtil.setExpiryTime(expiryTime, mContext);

                                        } else {
                                            CommonUtil.showMessageDialog(mContext, body.getMsg(), body.getErrCode());
                                        }

                                    }
                                }

                                @Override
                                public void onFailure(Call<ApiResult<TransConfirmModel>> call, Throwable t) {
                                    txSendingTipDialog.dismiss();
                                }
                            });


                        }
                    });

                } catch (WalletException e) {
                    ToastUtil.showToast(getActivity(), e.getErrMsg(), Toast.LENGTH_SHORT);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    private void ShowSupport() {
        final QMUIBottomSheet supportDialog = new QMUIBottomSheet(getContext());
        supportDialog.setContentView(supportDialog.getLayoutInflater().inflate(R.layout.view_dialog_node_detail_support, null));
        addBtn = supportDialog.findViewById(R.id.tvDialogAdd);
        subBtn = supportDialog.findViewById(R.id.tvDialogSub);
        tvDialogTotalAmount = supportDialog.findViewById(R.id.tvDialogTotalAmount);
        tvDialogAmount = supportDialog.findViewById(R.id.tvDialogAmount);

        supportDialog.findViewById(R.id.ivDialogCancle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                supportDialog.cancel();
            }
        });
        numSupport = supportDialog.findViewById(R.id.tvDialogNum);


        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String addStr = numSupport.getText().toString();
                int addNum = 0;
                if (TextUtils.isEmpty(addStr)) {
                    addNum += 1;
                } else {
                    addNum = Integer.parseInt(addStr) + 1;
                }

                setBtnStatus(addNum);
            }
        });
        addBtn.setSelected(true);


        subBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String subStr = numSupport.getText().toString();
                if (TextUtils.isEmpty(subStr)) {
                    return;
                }
                int numSub = Integer.parseInt(subStr) - 1;
                setBtnStatus(numSub);
            }
        });

        numSupport.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String supportAmount = s.toString();
                if (!TextUtils.isEmpty(supportAmount)) {
                    setNumStatus(Integer.parseInt(supportAmount));
                }
            }
        });

        //confirm
        supportDialog.findViewById(R.id.tvDialogSupport).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                supportDialog.cancel();

                confirmSheet();


            }
        });

        //init
        tvDialogAmount.setText(detailModel.getPerAmount());
        numSupport.setText("1");
        tvDialogTotalAmount.setText(detailModel.getPerAmount());

        supportDialog.show();

    }

    private void confirmSheet() {
        supportTransMetaData = String.format(getString(R.string.build_support_confirm_title), detailModel.getTitle());
        final String num = numSupport.getText().toString();
        JSONObject params = new JSONObject();
        params.put("shares", num);
        inputSupport = "{\"method\":\"subscribe\",\"params\":" + params.toJSONString() + " }";
        TransferUtils.confirmTxSheet(mContext, getWalletAddress(), detailModel.getContractAddress(),
                tvDialogTotalAmount.getText().toString(),
                Constants.NODE_REVOKE_FEE, supportTransMetaData,
                inputSupport, new TransferUtils.TransferListener() {
                    @Override
                    public void confirm() {
                        confirmSupport();
                    }
                });
    }

    private void confirmSupport() {
        final String amount = tvDialogTotalAmount.getText().toString();
        final String num = numSupport.getText().toString();
        final String contractAddress = detailModel.getContractAddress();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    final TransactionBuildBlobResponse transBlob = Wallet.getInstance().buildBlob(amount, inputSupport, getWalletAddress(), String.valueOf(Constants.NODE_CO_BUILD_MIN_FEE), contractAddress, supportTransMetaData);

                    final String hash = transBlob.getResult().getHash();
                    if (TextUtils.isEmpty(hash)) {

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                CommonUtil.showMessageDialog(mContext, transBlob.getErrorDesc());
                            }
                        });
                        return;
                    }


                    getSignatureInfo(new SingatureListener() {
                        @Override
                        public void success(final String privateKey) {

                            final QMUITipDialog txSendingTipDialog = new QMUITipDialog.Builder(getContext())
                                    .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                                    .setTipWord(getResources().getString(R.string.send_tx_verify))
                                    .create();
                            txSendingTipDialog.show();

                            HashMap<String, Object> map = new HashMap<>();
                            map.put("nodeId", detailModel.getNodeId());
                            map.put("hash", hash);
                            map.put("copies", num + "");
                            map.put("initiatorAddress", getWalletAddress());
                            NodeBuildService nodeBuildService = RetrofitFactory.getInstance().getRetrofit().create(NodeBuildService.class);
                            nodeBuildService.verifySupport(map).enqueue(new Callback<ApiResult<TransConfirmModel>>() {
                                @Override
                                public void onResponse(Call<ApiResult<TransConfirmModel>> call, Response<ApiResult<TransConfirmModel>> response) {
                                    ApiResult<TransConfirmModel> body = response.body();
                                    txSendingTipDialog.dismiss();
                                    if (ExceptionEnum.SUCCESS.getCode().equals(body.getErrCode())) {
                                        submitTransactionBase(privateKey, transBlob);
                                    } else {
                                        if (ExceptionEnum.ERROR_TRANSACTION_OTHER_1011.getCode().equals(body.getErrCode())) {
                                            String expiryTime = body.getData().getExpiryTime();
                                            CommonUtil.setExpiryTime(expiryTime, mContext);

                                        } else {
                                            CommonUtil.showMessageDialog(mContext, body.getMsg(), body.getErrCode());
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<ApiResult<TransConfirmModel>> call, Throwable t) {
                                    txSendingTipDialog.dismiss();
                                }
                            });


                        }
                    });


                } catch (WalletException walletException) {
                    ToastUtil.showToast(getActivity(), walletException.getErrMsg(), Toast.LENGTH_SHORT);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();


    }

    private void setBtnStatus(int num) {
        setNumStatus(num);
        numSupport.setText(num + "");

    }

    private int setNumStatus(int num) {

        int leftCopies = detailModel.getLeftCopies();
        double myAmount = Double.parseDouble(tokenListBean.getAmount());
        int perAmount = Integer.parseInt(tvDialogAmount.getText().toString());
        int myLeftCopies = (int) (myAmount / perAmount);

        if (myLeftCopies < leftCopies) {
            leftCopies = myLeftCopies;
        }
        if (num < 1) {
            subBtn.setSelected(false);
            num = 1;
            numSupport.setText(num + "");
        } else if (num == 1) {
            subBtn.setSelected(false);
        } else if (num == leftCopies) {
            addBtn.setSelected(false);
        } else if (num > leftCopies) {
            addBtn.setSelected(false);
            num = leftCopies;
            numSupport.setText(num + "");
        } else {
            if (!subBtn.isSelected()) {
                subBtn.setSelected(true);
            }
            if (!addBtn.isSelected()) {
                addBtn.setSelected(true);
            }

        }


        int tvAmount = num * perAmount;
        tvDialogTotalAmount.setText(tvAmount + "");

        return num;
    }

}
