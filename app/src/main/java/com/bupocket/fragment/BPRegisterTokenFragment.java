package com.bupocket.fragment;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.bupocket.BPApplication;
import com.bupocket.R;
import com.bupocket.base.BaseFragment;
import com.bupocket.common.Constants;
import com.bupocket.enums.AssetTypeEnum;
import com.bupocket.enums.TxStatusEnum;
import com.bupocket.fragment.home.HomeFragment;
import com.bupocket.http.api.RetrofitFactory;
import com.bupocket.http.api.TokenService;
import com.bupocket.http.api.TxService;
import com.bupocket.http.api.dto.resp.ApiResult;
import com.bupocket.http.api.dto.resp.GetTokenDetailRespDto;
import com.bupocket.http.api.dto.resp.TxDetailRespDto;
import com.bupocket.model.RegisterStatusInfo;
import com.bupocket.model.RegisterTokenInfo;
import com.bupocket.utils.CommonUtil;
import com.bupocket.utils.SharedPreferencesHelper;
import com.bupocket.wallet.Wallet;
import com.bupocket.wallet.enums.ExceptionEnum;
import com.bupocket.wallet.exception.WalletException;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BPRegisterTokenFragment extends BaseFragment {
    @BindView(R.id.topbar)
    QMUITopBarLayout mTopBar;
    @BindView(R.id.registerConfirmBtn)
    QMUIRoundButton mRegisterConfirmBtn;
    @BindView(R.id.registerCancelBtn)
    QMUIRoundButton mRegisterCancelBtn;
    @BindView(R.id.tokenNameTv)
    TextView mTokenNameTv;
    @BindView(R.id.tokenCodeTv)
    TextView mTokenCodeTv;
    @BindView(R.id.tokenAmountTv)
    TextView mTokenAmountTv;
    @BindView(R.id.registerFeeTv)
    TextView mRegisterFeeTv;
//    @BindView(R.id.issueTypeTv)
//    TextView mIssueTypeTv;
    @BindView(R.id.registerLl)
    LinearLayout mRegisterLl;

    private Socket mSocket;
    private String tokenName;
    private String tokenCode;
    private String issueAmount;
    private String decimals;
    private String desc;
//    private String issueType;
    private String issueAddress;
    private String getTokenDetailErrorCode;
    private String balance;
    protected SharedPreferencesHelper sharedPreferencesHelper;
    QMUITipDialog txSendingTipDialog;
    private String hash;
    private TxDetailRespDto.TxDeatilRespBoBean txDeatilRespBoBean;

    public BPRegisterTokenFragment(){
        super();
    }
    private RegisterStatusInfo.DataBean registerData = new RegisterStatusInfo.DataBean();

    @Override
    protected View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_register_token_confirm, null);
        ButterKnife.bind(this, root);
        QMUIStatusBarHelper.setStatusBarLightMode(getBaseFragmentActivity());
        initTopbar();
        initData();
        setListener();
        return root;
    }

    private void initData() {
        sharedPreferencesHelper = new SharedPreferencesHelper(getContext(), "buPocket");
        Bundle bundle = getArguments();
        String data = bundle.getString("tokenData");
        RegisterTokenInfo registerTokenInfo = RegisterTokenInfo.objectFromData(data);
        tokenName = registerTokenInfo.getName();
        tokenCode = registerTokenInfo.getCode();
        issueAmount = registerTokenInfo.getAmount();
        decimals = registerTokenInfo.getDecimals();
        desc = registerTokenInfo.getDesc();
//        issueType = registerTokenInfo.getType();
        mTokenNameTv.setText(tokenName);
        mTokenCodeTv.setText(tokenCode);
        if(issueAmount.equals("0")){
            mTokenAmountTv.setText(getString(R.string.issue_unlimited_amount_txt));
        }else{
            mTokenAmountTv.setText(issueAmount);
        }
        mRegisterFeeTv.setText(CommonUtil.addSuffix(Constants.REGISTER_TOKEN_FEE,"BU"));
        issueAddress = sharedPreferencesHelper.getSharedPreference("currentAccAddr", "").toString();
        balance = bundle.getString("buBalance");
//        if(issueType.equals(AssetTypeEnum.ATP_FIXED.getCode())){
//            mIssueTypeTv.setText(getString(R.string.issue_type_disposable_txt));
//        }else if(issueType.equals(AssetTypeEnum.ATP_ADD.getCode())){
//            mIssueTypeTv.setText(getString(R.string.issue_type_increment_txt));
//        }else if(issueType.equals(AssetTypeEnum.ATP_INFINITE.getCode())){
//            mIssueTypeTv.setText(getString(R.string.issue_type_unlimited_txt));
//            mRegisterLl.removeView(mRegisterLl.findViewById(R.id.tokenAmountRl));
//            issueAmount = "0";
//        }


        TokenService tokenService = RetrofitFactory.getInstance().getRetrofit().create(TokenService.class);
        Map<String, Object> parmasMap = new HashMap<>();
        parmasMap.put("assetCode",tokenCode);
        parmasMap.put("issueAddress",issueAddress);
        Call<ApiResult<GetTokenDetailRespDto>> call = tokenService.getTokenDetail(parmasMap);
        call.enqueue(new Callback<ApiResult<GetTokenDetailRespDto>>() {
            @Override
            public void onResponse(Call<ApiResult<GetTokenDetailRespDto>> call, Response<ApiResult<GetTokenDetailRespDto>> response) {
                getTokenDetailErrorCode = response.body().getErrCode();
            }

            @Override
            public void onFailure(Call<ApiResult<GetTokenDetailRespDto>> call, Throwable t) {
                Toast.makeText(getActivity(), R.string.network_error_msg, Toast.LENGTH_SHORT).show();
            }
        });

        registerData.setName(tokenName);
        registerData.setCode(tokenCode);
//        registerData.setType(issueType);
        registerData.setTotal(issueAmount);
        registerData.setDecimals(decimals);
        registerData.setVersion(getString(R.string.token_version));
        registerData.setDesc(desc);
        registerData.setAddress(issueAddress);
        registerData.setFee(Constants.REGISTER_TOKEN_FEE);

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        final String uuID = bundle.getString("uuID");
        System.out.println(uuID);

        BPApplication application = (BPApplication)getActivity().getApplication();
        mSocket = application.getSocket();
        mSocket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {

            @Override
            public void call(Object... args) {
                System.out.println("socket id :" + mSocket.id());
                mSocket.emit("token.register.join",uuID);
//                mSocket.disconnect();
            }

        }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {

            @Override
            public void call(Object... args) {}

        }).on(Socket.EVENT_RECONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                mSocket.emit("token.register.join",uuID);
            }
        });
        mSocket.emit("token.register.scanSuccess","");
        mSocket.connect();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSocket.disconnect();
    }

    private void setListener() {
        mRegisterConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Double.valueOf(balance) < Double.valueOf(Constants.REGISTER_TOKEN_FEE)){
                    Toast.makeText(getActivity(), R.string.register_token_balance_insufficient_message_txt, Toast.LENGTH_SHORT).show();
                }else if(getTokenDetailErrorCode.equals("0")){
                    Toast.makeText(getActivity(), R.string.register_already_have_message_txt, Toast.LENGTH_SHORT).show();
                }else{
                    showPasswordComfirmDialog();
                }
            }
        });
        mRegisterCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSocket.emit("token.register.cancel","");
                startFragment(new HomeFragment());
            }
        });
    }

    private void showPasswordComfirmDialog() {
        final QMUIDialog qmuiDialog = new QMUIDialog(getContext());
        qmuiDialog.setCanceledOnTouchOutside(false);
        qmuiDialog.setContentView(R.layout.password_comfirm_layout);
        qmuiDialog.show();
        QMUIRoundButton mPasswordConfirmBtn = qmuiDialog.findViewById(R.id.passwordConfirmBtn);
        ImageView mPasswordConfirmCloseBtn = qmuiDialog.findViewById(R.id.passwordConfirmCloseBtn);
        TextView mPasswordConfirmNotice = qmuiDialog.findViewById(R.id.passwordConfirmNotice);
        TextView mPpasswordConfirmTitle = qmuiDialog.findViewById(R.id.passwordConfirmTitle);

        mPasswordConfirmNotice.setText(getString(R.string.register_token_password_confirm_txt));
        mPpasswordConfirmTitle.setText(getString(R.string.password_comfirm_dialog_title));

        mPasswordConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText mPasswordConfirmEt = qmuiDialog.findViewById(R.id.passwordConfirmEt);
                final String password = mPasswordConfirmEt.getText().toString().trim();
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
                        try {
                            hash = Wallet.getInstance().registerATP10Token(password,accountBPData,issueAddress,tokenName,tokenCode,decimals,desc,Constants.REGISTER_TOKEN_FEE,issueAmount);
                        } catch (WalletException e){
                            e.printStackTrace();
                            Looper.prepare();
                            if(ExceptionEnum.FEE_NOT_ENOUGH.getCode().equals(e.getErrCode())){
                                Toast.makeText(getActivity(), R.string.send_tx_fee_not_enough, Toast.LENGTH_SHORT).show();
                            }else if(ExceptionEnum.BU_NOT_ENOUGH.getCode().equals(e.getErrCode())){
                                Toast.makeText(getActivity(), R.string.send_tx_bu_not_enough, Toast.LENGTH_SHORT).show();
                            }else {
                                Toast.makeText(getActivity(), R.string.network_error_msg, Toast.LENGTH_SHORT).show();
                            }
                            txSendingTipDialog.dismiss();
                            Looper.loop();
                        } catch (NumberFormatException e){
                            e.printStackTrace();
                            Looper.prepare();
                            Toast.makeText(getActivity(), R.string.error_issue_amount_message_txt, Toast.LENGTH_SHORT).show();
                            txSendingTipDialog.dismiss();
                            Looper.loop();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Looper.prepare();
                            Toast.makeText(getActivity(), R.string.checking_password_error, Toast.LENGTH_SHORT).show();
                            txSendingTipDialog.dismiss();
                            Looper.loop();
                        } finally {
                            mSocket.emit("token.register.processing","");
                            timer.schedule(timerTask,
                                    1 * 1000,//延迟1秒执行
                                    1000);
                        }
                    }
                }).start();

                qmuiDialog.dismiss();
            }
        });

        mPasswordConfirmCloseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qmuiDialog.dismiss();
            }
        });
    }

    private String getAccountBPData(){
        String data = sharedPreferencesHelper.getSharedPreference("BPData", "").toString();
        return data;
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
                        Bundle argz = new Bundle();
                        argz.putString("txStatus","timeout");
                        argz.putString("tokenName",tokenName);
                        argz.putString("tokenCode",tokenCode);
//                        argz.putString("issueType",issueType);
                        argz.putString("issueAmount",issueAmount);
                        argz.putString("tokenDecimals",decimals);
                        argz.putString("tokenDesc",desc);
                        argz.putString("issueAddress",issueAddress);
                        /*RegisterStatusInfo registerStatusInfo = new RegisterStatusInfo();
                        registerStatusInfo.setErrorCode(2);
                        registerStatusInfo.setErrorMsg(getString(R.string.register_token_timeout_txt));
                        mSocket.emit("token.register.timeout",JSON.toJSON(registerStatusInfo).toString());*/
                        BPRegisterTokenStatusFragment bpRegisterTokenStatusFragment = new BPRegisterTokenStatusFragment();
                        bpRegisterTokenStatusFragment.setArguments(argz);
                        startFragmentAndDestroyCurrent(bpRegisterTokenStatusFragment);
                        return;
                    }
                    timerTimes++;
                    System.out.println("timerTimes:" + timerTimes);
                    TxService txService = RetrofitFactory.getInstance().getRetrofit().create(TxService.class);
                    Map<String, Object> parmasMap = new HashMap<>();
                    parmasMap.put("hash",hash);
                    Call<ApiResult<TxDetailRespDto>> call = txService.getTxDetail(parmasMap);
                    call.enqueue(new retrofit2.Callback<ApiResult<TxDetailRespDto>>(){

                        @Override
                        public void onResponse(Call<ApiResult<TxDetailRespDto>> call, Response<ApiResult<TxDetailRespDto>> response) {
                            ApiResult<TxDetailRespDto> resp = response.body();
                            System.out.println(JSON.toJSONString(resp));
                            if(!TxStatusEnum.SUCCESS.getCode().toString().equals(resp.getErrCode())){
                                return;
                            }else{
                                txDeatilRespBoBean = resp.getData().getTxDeatilRespBo();
                                timerTask.cancel();
                                txSendingTipDialog.dismiss();
                                Bundle argz = new Bundle();
                                argz.putString("txStatus",txDeatilRespBoBean.getStatus().toString());
                                argz.putString("tokenName",tokenName);
                                argz.putString("tokenCode",tokenCode);
//                                argz.putString("issueType",issueType);
                                argz.putString("issueAmount",issueAmount);
                                argz.putString("tokenDecimals",decimals);
                                argz.putString("tokenDesc",desc);
                                argz.putString("issueAddress",issueAddress);
                                argz.putString("txHash",hash);
                                argz.putString("txFee",txDeatilRespBoBean.getFee());
                                argz.putString("errorMsg",txDeatilRespBoBean.getErrorMsg());
                                BPRegisterTokenStatusFragment bpRegisterTokenStatusFragment = new BPRegisterTokenStatusFragment();
                                bpRegisterTokenStatusFragment.setArguments(argz);
                                startFragmentAndDestroyCurrent(bpRegisterTokenStatusFragment);
                            }
                        }

                        @Override
                        public void onFailure(Call<ApiResult<TxDetailRespDto>> call, Throwable t) {
                            Bundle argz = new Bundle();
                            argz.putString("txStatus","timeout");
                            argz.putString("tokenName",tokenName);
                            argz.putString("tokenCode",tokenCode);
//                            argz.putString("issueType",issueType);
                            argz.putString("issueAmount",issueAmount);
                            argz.putString("tokenDecimals",decimals);
                            argz.putString("tokenDesc",desc);
                            argz.putString("issueAddress",issueAddress);
                            /*RegisterStatusInfo registerStatusInfo = new RegisterStatusInfo();
                            registerStatusInfo.setErrorCode(2);
                            registerStatusInfo.setErrorMsg(getString(R.string.register_token_timeout_txt));
                            mSocket.emit("token.register.timeout",JSON.toJSON(registerStatusInfo).toString());*/
                            BPRegisterTokenStatusFragment bpRegisterTokenStatusFragment = new BPRegisterTokenStatusFragment();
                            bpRegisterTokenStatusFragment.setArguments(argz);
                            startFragmentAndDestroyCurrent(bpRegisterTokenStatusFragment);
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
            if(hash != null && !hash.equals("")){
                mHanlder.sendEmptyMessage(1);
            }
        }
    };

    private void initTopbar() {
        mTopBar.addLeftImageButton(R.mipmap.icon_tobar_left_arrow, R.id.topbar_left_arrow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QMUIStatusBarHelper.setStatusBarDarkMode(getBaseFragmentActivity());
                popBackStack();
            }
        });
        mTopBar.setTitle(getString(R.string.register_token_page_title));
    }
}
