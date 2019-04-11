package com.bupocket.fragment.discover;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.bupocket.R;
import com.bupocket.adaptor.SuperNodeAdapter;
import com.bupocket.base.BaseFragment;
import com.bupocket.common.Constants;
import com.bupocket.enums.ExceptionEnum;
import com.bupocket.enums.SuperNodeTypeEnum;
import com.bupocket.enums.TxStatusEnum;
import com.bupocket.fragment.BPSendStatusFragment;
import com.bupocket.fragment.BPTxRequestTimeoutFragment;
import com.bupocket.fragment.home.HomeFragment;
import com.bupocket.http.api.NodePlanService;
import com.bupocket.http.api.RetrofitFactory;
import com.bupocket.http.api.TxService;
import com.bupocket.http.api.dto.resp.ApiResult;
import com.bupocket.http.api.dto.resp.SuperNodeDto;
import com.bupocket.http.api.dto.resp.TxDetailRespDto;
import com.bupocket.model.SuperNodeModel;
import com.bupocket.utils.CommonUtil;
import com.bupocket.utils.LogUtils;
import com.bupocket.utils.SharedPreferencesHelper;
import com.bupocket.wallet.Wallet;
import com.bupocket.wallet.exception.WalletException;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.dialog.QMUIBottomSheet;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.qmuiteam.qmui.widget.popup.QMUIPopup;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.bumo.model.response.TransactionBuildBlobResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class BPNodePlanFragment extends BaseFragment {


    @BindView(R.id.topbar)
    QMUITopBarLayout mTopBar;
    @BindView(R.id.lvPlan)
    ListView lvPlan;
    @BindView(R.id.myNodeCB)
    CheckBox myNodeCB;
    @BindView(R.id.myNodeTv)
    TextView myNodeTv;
    @BindView(R.id.etNodeSearch)
    EditText etNodeSearch;
    @BindView(R.id.addressRecordEmptyLL)
    LinearLayout addressRecordEmptyLL;
    @BindView(R.id.myNodeTipsIv)
    ImageView mMyNodeTipsIv;
    @BindView(R.id.refreshLayout)
    RefreshLayout refreshLayout;

    private String txHash;
    private QMUITipDialog txSendingTipDialog;
    private TxDetailRespDto.TxDeatilRespBoBean txDetailRespBoBean;

    private SharedPreferencesHelper sharedPreferencesHelper;
    private String currentWalletAddress;
    private Boolean whetherIdentityWallet = false;
    private SuperNodeAdapter superNodeAdapter;

    private QMUIPopup myNodeExplainPopup;

    private ArrayList<SuperNodeModel> myVoteInfolist;
    private ArrayList<SuperNodeModel> nodeList;

    @Override
    protected View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_node_plan, null);
        ButterKnife.bind(this, root);
        init();
        return root;
    }

    private void init() {
        initUI();
        initData();
        setListener();
    }

    private void setListener() {
        myNodeCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {

                    setEmpty(myVoteInfolist.size()==0);
                    superNodeAdapter.setNewData(myVoteInfolist);
                } else {
                    setEmpty(nodeList.size()==0);
                    superNodeAdapter.setNewData(nodeList);
                }

                superNodeAdapter.notifyDataSetChanged();
            }
        });

        etNodeSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                searchNode( s.toString());
            }
        });

        etNodeSearch.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                    InputMethodManager manager = (InputMethodManager) getContext().getSystemService(INPUT_METHOD_SERVICE);
                    if (manager.isActive()) {
                        manager.hideSoftInputFromWindow(etNodeSearch.getApplicationWindowToken(), 0);
                    }

                }
                return false;
            }
        });

        superNodeAdapter.setOnItemBtnListener(new SuperNodeAdapter.OnItemBtnListener() {
            @Override
            public void onClick(int position, int btn) {
                SuperNodeModel superNodeModel = nodeList.get(position);
                LogUtils.e("position="+position+"\n"+btn);
                switch (btn) {
                    case R.id.revokeVoteBtn:
                        if("0".equals(superNodeModel.getMyVoteCount())){
                            CommonUtil.showMessageDialog(getContext(),getString(R.string.revoke_no_vote_error_message_txt));
                        }else{
                            showRevokeVoteDialog(superNodeModel);
                        }
                        break;
                    case R.id.shareBtn:

                        Bundle args = new Bundle();
                        args.putParcelable("itemInfo",superNodeModel);
                        BPNodeShareFragment bpNodeShareFragment = new BPNodeShareFragment();
                        bpNodeShareFragment.setArguments(args);
                        startFragment(bpNodeShareFragment);
                        break;
                    case R.id.voteRecordBtn:
                        BPSomeOneVoteRecordFragment fragment = new BPSomeOneVoteRecordFragment();
                        Bundle args1 = new Bundle();
                        args1.putParcelable("itemNodeInfo",superNodeModel);
                        fragment.setArguments(args1);

                        startFragment(fragment);
                        break;
                }
            }
        });

        refreshLayout.setEnableLoadMore(false);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                refreshData();
                refreshLayout.finishRefresh();
                refreshLayout.setNoMoreData(false);
            }
        });

        mMyNodeTipsIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initPopup();
                myNodeExplainPopup.setAnimStyle(QMUIPopup.ANIM_GROW_FROM_CENTER);
                myNodeExplainPopup.setPreferredDirection(QMUIPopup.DIRECTION_BOTTOM);
                myNodeExplainPopup.show(v);
                ImageView arrowUp = myNodeExplainPopup.getDecorView().findViewById(R.id.arrow_up);
                arrowUp.setImageDrawable(getResources().getDrawable(R.mipmap.triangle));
            }
        });
    }

    private void initPopup() {
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
            textView.setText(getString(R.string.node_associated_with_me_help_txt));
            textView.setTextColor(ContextCompat.getColor(getContext(), R.color.app_color_white));
            textView.setBackgroundColor(getResources().getColor(R.color.popup_background_color));
            myNodeExplainPopup.setContentView(textView);
        }
    }

    private void refreshData() {
        getAllNode();
    }

    private void showRevokeVoteDialog(SuperNodeModel itemInfo) {
        @SuppressLint("StringFormatMatches")
        String nodeType = itemInfo.getIdentityType();
        String role = null;
        if(SuperNodeTypeEnum.VALIDATOR.getCode().equals(nodeType)){
            role = SuperNodeTypeEnum.VALIDATOR.getName();
        }else if(SuperNodeTypeEnum.ECOLOGICAL.getCode().equals(nodeType)){
            role = SuperNodeTypeEnum.ECOLOGICAL.getName();
        }
        String nodeAddress = itemInfo.getNodeCapitalAddress();
        final String nodeId = itemInfo.getNodeId();

        String destAddress = String.format(getString(R.string.revoke_vote_dest_address_txt),Constants.CONTRACT_ADDRESS);
        String transactionDetail = String.format(getString(R.string.revoke_vote_tx_details_txt),itemInfo.getNodeName());
        String transactionAmount = "0";

        final JSONObject input = new JSONObject();
        input.put("method","unVote");
        JSONObject params = new JSONObject();
        params.put("role",role);
        params.put("address",nodeAddress);
        input.put("params", params);

        String transactionParams = input.toJSONString();

        final QMUIBottomSheet qmuiBottomSheet = new QMUIBottomSheet(getContext());
        qmuiBottomSheet.setContentView(qmuiBottomSheet.getLayoutInflater().inflate(R.layout.view_transfer_confirm,null));
        LinearLayout mTransactionAmountLl = qmuiBottomSheet.findViewById(R.id.transactionAmountLl);
        mTransactionAmountLl.setVisibility(View.GONE);
        TextView mTransactionDetailTv = qmuiBottomSheet.findViewById(R.id.transactionDetailTv);
        mTransactionDetailTv.setText(transactionDetail);
        TextView mDestAddressTv = qmuiBottomSheet.findViewById(R.id.destAddressTv);
        mDestAddressTv.setText(destAddress);
        TextView mTxFeeTv = qmuiBottomSheet.findViewById(R.id.txFeeTv);
        mTxFeeTv.setText(String.valueOf(Constants.NODE_REVOKE_FEE));

        qmuiBottomSheet.findViewById(R.id.detailBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qmuiBottomSheet.findViewById(R.id.confirmLl).setVisibility(View.GONE);
                qmuiBottomSheet.findViewById(R.id.confirmDetailsLl).setVisibility(View.VISIBLE);
            }
        });

        // confirm details page
        TextView mSourceAddressTv = qmuiBottomSheet.findViewById(R.id.sourceAddressTv);
        mSourceAddressTv.setText(currentWalletAddress);
        TextView mDetailsDestAddressTv = qmuiBottomSheet.findViewById(R.id.detailsDestAddressTv);
        mDetailsDestAddressTv.setText(destAddress);
        TextView mDetailsAmountTv = qmuiBottomSheet.findViewById(R.id.detailsAmountTv);
        mDetailsAmountTv.setText(CommonUtil.addSuffix(transactionAmount,"BU"));
        TextView mDetailsTxFeeTv = qmuiBottomSheet.findViewById(R.id.detailsTxFeeTv);
        mDetailsTxFeeTv.setText(String.valueOf(Constants.NODE_REVOKE_FEE));
        TextView mTransactionParamsTv = qmuiBottomSheet.findViewById(R.id.transactionParamsTv);
        mTransactionParamsTv.setText(transactionParams);

        // title view listener
        qmuiBottomSheet.findViewById(R.id.goBackBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qmuiBottomSheet.findViewById(R.id.confirmLl).setVisibility(View.VISIBLE);
                qmuiBottomSheet.findViewById(R.id.confirmDetailsLl).setVisibility(View.GONE);
            }
        });
        qmuiBottomSheet.findViewById(R.id.cancelBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qmuiBottomSheet.dismiss();
            }
        });
        qmuiBottomSheet.findViewById(R.id.detailsCancelBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qmuiBottomSheet.dismiss();
            }
        });
        qmuiBottomSheet.findViewById(R.id.sendConfirmBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qmuiBottomSheet.dismiss();
                confirmUnVote(input,nodeId);
            }
        });
        qmuiBottomSheet.show();

    }

    private void confirmUnVote(final JSONObject input, final String nodeId) {
        final String amount = "0";
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final TransactionBuildBlobResponse buildBlobResponse = Wallet.getInstance().buildBlob(amount,input.toJSONString(),currentWalletAddress,String.valueOf(Constants.NODE_REVOKE_FEE));
                    String txHash = buildBlobResponse.getResult().getHash();
                    NodePlanService nodePlanService = RetrofitFactory.getInstance().getRetrofit().create(NodePlanService.class);
                    Call<ApiResult> call;
                    Map<String, Object> paramsMap = new HashMap<>();
                    paramsMap.put("hash",txHash);
                    paramsMap.put("nodeId",nodeId);
                    paramsMap.put("initiatorAddress",currentWalletAddress);
                    call = nodePlanService.revokeVote(paramsMap);
                    call.enqueue(new Callback<ApiResult>() {
                        @Override
                        public void onResponse(Call<ApiResult> call, Response<ApiResult> response) {
                            ApiResult respDto = response.body();
                            if(ExceptionEnum.SUCCESS.getCode().equals(respDto.getErrCode())){
                                submitTransaction(buildBlobResponse);
                            }else {
                                CommonUtil.showMessageDialog(getContext(),respDto.getMsg());
//                                Toast.makeText(getContext(),respDto.getMsg(),Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ApiResult> call, Throwable t) {
                            Toast.makeText(getContext(),getString(R.string.network_error_msg),Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (Exception e) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), R.string.checking_password_error, Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }
        }).start();
    }

    private void setEmpty(boolean isVisible) {
        if (isVisible) {
            addressRecordEmptyLL.setVisibility(View.VISIBLE);
        }else{
            addressRecordEmptyLL.setVisibility(View.GONE);
        }
    }

    private ArrayList<SuperNodeModel> searchNode(String s) {


        ArrayList<SuperNodeModel> superNodeModels = new ArrayList<>();
        if (nodeList == null) {
            return superNodeModels;
        }
        Pattern pattern = Pattern.compile(s);
        for (int i = 0; i < nodeList.size(); i++) {
            Matcher matcher = pattern.matcher(nodeList.get(i).getNodeName());
            if (matcher.find()) {
                superNodeModels.add(nodeList.get(i));
            }
        }
        if (superNodeModels.size()>0) {
            superNodeAdapter.setNewData(superNodeModels);
            superNodeAdapter.notifyDataSetChanged();
        }

        return superNodeModels;
    }

    private void initData() {
        refreshLayout.autoRefresh();
    }


    private void getAllNode() {

        sharedPreferencesHelper = new SharedPreferencesHelper(getContext(), "buPocket");
        currentWalletAddress = sharedPreferencesHelper.getSharedPreference("currentWalletAddress", "").toString();
        if (CommonUtil.isNull(currentWalletAddress) || currentWalletAddress.equals(sharedPreferencesHelper.getSharedPreference("currentAccAddr", "").toString())) {
            currentWalletAddress = sharedPreferencesHelper.getSharedPreference("currentAccAddr", "").toString();
            whetherIdentityWallet = true;
        }
        LogUtils.e(currentWalletAddress);
        HashMap<String, Object> listReq = new HashMap<>();

        listReq.put(Constants.ADDRESS, currentWalletAddress);

        NodePlanService nodePlanService = RetrofitFactory.getInstance().getRetrofit().create(NodePlanService.class);

        Call<ApiResult<SuperNodeDto>> superNodeList = nodePlanService.getSuperNodeList(listReq);
        superNodeList.enqueue(new Callback<ApiResult<SuperNodeDto>>() {

            @Override
            public void onResponse(Call<ApiResult<SuperNodeDto>> call, Response<ApiResult<SuperNodeDto>> response) {
                ApiResult<SuperNodeDto> body = response.body();
                if (body==null) {
                    return;
                }
                nodeList = body.getData().getNodeList();
                if (nodeList != null) {
                    setEmpty(nodeList.size()==0);
                    superNodeAdapter.setNewData(nodeList);
                    superNodeAdapter.notifyDataSetChanged();
                    myVoteInfolist = myVoteInfoList(nodeList);

                }

            }

            @Override
            public void onFailure(Call<ApiResult<SuperNodeDto>> call, Throwable t) {
//                LogUtils.e("请求失败" + t.getMessage());

            }
        });


    }

    private ArrayList<SuperNodeModel> myVoteInfoList(@NonNull ArrayList<SuperNodeModel> nodeList) {

        ArrayList<SuperNodeModel> superNodeModels = new ArrayList<>();
        for (int i = 0; i < nodeList.size(); i++) {
            SuperNodeModel superNodeModel = nodeList.get(i);
            if (superNodeModel != null) {
                String myVoteCount = superNodeModel.getMyVoteCount();
                if ((TextUtils.isEmpty(myVoteCount) && (Integer.parseInt(myVoteCount)) > 0)
                        || currentWalletAddress.equals(superNodeModel.getNodeCapitalAddress())) {
                    superNodeModels.add(superNodeModel);
                }
            }
        }

        return superNodeModels;
    }

    private void initUI() {
        initTopBar();
        initListView();
    }

    private void initListView() {
        superNodeAdapter = new SuperNodeAdapter(this.getContext());
        lvPlan.setAdapter(superNodeAdapter);

        if (myVoteInfolist==null) {
            myVoteInfolist=new ArrayList<>();
        }

        if (nodeList==null) {
            nodeList=new ArrayList<>();
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
        mTopBar.addRightImageButton(R.mipmap.icon_vote_record, R.id.topbar_right_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startFragment(new BPVoteRecordFragment());
            }
        });
    }

    private void submitTransaction(final TransactionBuildBlobResponse buildBlobResponse) {
        final QMUIDialog qmuiDialog = new QMUIDialog(getContext());
        qmuiDialog.setCanceledOnTouchOutside(false);
        qmuiDialog.setContentView(R.layout.view_password_comfirm);
        qmuiDialog.show();

        QMUIRoundButton mPasswordConfirmBtn = qmuiDialog.findViewById(R.id.passwordConfirmBtn);

        ImageView mPasswordConfirmCloseBtn = qmuiDialog.findViewById(R.id.passwordConfirmCloseBtn);

        mPasswordConfirmCloseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qmuiDialog.dismiss();
            }
        });

        mPasswordConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txSendingTipDialog = new QMUITipDialog.Builder(getContext())
                        .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                        .setTipWord(getResources().getString(R.string.send_tx_handleing_txt))
                        .create();
                txSendingTipDialog.show();
                txSendingTipDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                    @Override
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {

                        if(event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
                            return true;
                        }
                        return false;
                    }
                });

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String accountBPData = getAccountBPData();
                        EditText mPasswordConfirmEt = qmuiDialog.findViewById(R.id.passwordConfirmEt);
                        final String password = mPasswordConfirmEt.getText().toString().trim();
                        try {
                            txHash = Wallet.getInstance().submitTransaction(password,accountBPData,currentWalletAddress,buildBlobResponse);
                        }catch (WalletException e){
                            e.printStackTrace();
                            Looper.prepare();
                            if(com.bupocket.wallet.enums.ExceptionEnum.FEE_NOT_ENOUGH.getCode().equals(e.getErrCode())){
                                Toast.makeText(getActivity(), R.string.send_tx_fee_not_enough, Toast.LENGTH_SHORT).show();
                            }else if(com.bupocket.wallet.enums.ExceptionEnum.BU_NOT_ENOUGH.getCode().equals(e.getErrCode())){
                                Toast.makeText(getActivity(), R.string.send_tx_bu_not_enough, Toast.LENGTH_SHORT).show();
                            }else {
                                Toast.makeText(getActivity(), R.string.network_error_msg, Toast.LENGTH_SHORT).show();
                            }
                            txSendingTipDialog.dismiss();
                            Looper.loop();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Looper.prepare();
                            Toast.makeText(getActivity(), R.string.network_error_msg, Toast.LENGTH_SHORT).show();
                            txSendingTipDialog.dismiss();
                            Looper.loop();
                        }finally {
                            timer.schedule(timerTask,
                                    1 * 1000,//延迟1秒执行
                                    1000);
                        }
                    }
                }).start();
                qmuiDialog.dismiss();

            }
        });
    }

    private String getAccountBPData(){
        String accountBPData = null;
        if(whetherIdentityWallet) {
            accountBPData = sharedPreferencesHelper.getSharedPreference("BPData", "").toString();
        }else {
            accountBPData = sharedPreferencesHelper.getSharedPreference(currentWalletAddress+ "-BPdata", "").toString();
        }
        return accountBPData;
    }

    private int timerTimes = 0;
    private final Timer timer = new Timer();
    @SuppressLint("HandlerLeak")
    private Handler mHanlder = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    if(timerTimes > Constants.TX_REQUEST_TIMEOUT_TIMES){
                        timerTask.cancel();
                        txSendingTipDialog.dismiss();
                        startFragmentAndDestroyCurrent(new BPTxRequestTimeoutFragment());
                        return;
                    }
                    timerTimes++;
                    System.out.println("timerTimes:" + timerTimes);
                    TxService txService = RetrofitFactory.getInstance().getRetrofit().create(TxService.class);
                    Map<String, Object> paramsMap = new HashMap<>();
                    paramsMap.put("hash",txHash);
                    Call<ApiResult<TxDetailRespDto>> call = txService.getTxDetailByHash(paramsMap);
                    call.enqueue(new retrofit2.Callback<ApiResult<TxDetailRespDto>>(){

                        @Override
                        public void onResponse(Call<ApiResult<TxDetailRespDto>> call, Response<ApiResult<TxDetailRespDto>> response) {
                            ApiResult<TxDetailRespDto> resp = response.body();
                            if(!TxStatusEnum.SUCCESS.getCode().toString().equals(resp.getErrCode())){
                                return;
                            }else{
                                txDetailRespBoBean = resp.getData().getTxDeatilRespBo();
                                timerTask.cancel();
                                txSendingTipDialog.dismiss();
                                if (com.bupocket.wallet.enums.ExceptionEnum.BU_NOT_ENOUGH_FOR_PAYMENT.getCode().equals(txDetailRespBoBean.getErrorCode())) {
                                    Toast.makeText(getActivity(), R.string.balance_not_enough, Toast.LENGTH_SHORT).show();
                                }
                                Bundle argz = new Bundle();
                                argz.putString("destAccAddr",txDetailRespBoBean.getDestAddress());
                                argz.putString("sendAmount",txDetailRespBoBean.getAmount());
                                argz.putString("txFee",txDetailRespBoBean.getFee());
                                argz.putString("tokenCode","BU");
                                argz.putString("note",txDetailRespBoBean.getOriginalMetadata());
                                argz.putString("state",txDetailRespBoBean.getStatus().toString());
                                argz.putString("sendTime",txDetailRespBoBean.getApplyTimeDate());
                                BPSendStatusFragment bpSendStatusFragment = new BPSendStatusFragment();
                                bpSendStatusFragment.setArguments(argz);
                                startFragment(bpSendStatusFragment);
                            }
                        }

                        @Override
                        public void onFailure(Call<ApiResult<TxDetailRespDto>> call, Throwable t) {
                            Toast.makeText(getActivity(), R.string.tx_timeout_err, Toast.LENGTH_SHORT).show();
                        }
                    });
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };

    private TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            if(txHash != null && !txHash.equals("")){
                mHanlder.sendEmptyMessage(1);
            }
        }
    };
}