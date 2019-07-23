package com.bupocket.fragment;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.bupocket.R;
import com.bupocket.base.BaseFragment;
import com.bupocket.http.api.RetrofitFactory;
import com.bupocket.http.api.UserService;
import com.bupocket.http.api.dto.resp.ApiResult;
import com.bupocket.utils.LogUtils;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BPHelpFeedbackFragment extends BaseFragment {
    @BindView(R.id.topbar)
    QMUITopBarLayout mTopBar;
    @BindView(R.id.feedbackContentET)
    EditText mFeedbackContentET;
    @BindView(R.id.contactET)
    EditText mContactET;
    @BindView(R.id.nextHelpFeedbackBtn)
    Button mNextHelpFeedbackBtn;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_help_feedback, null);
        ButterKnife.bind(this, root);
        QMUIStatusBarHelper.setStatusBarLightMode(getBaseFragmentActivity());
        initTopBar();

        mNextHelpFeedbackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SubmitFeedback();
            }
        });
        buildWatcher();
        return root;
    }



    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void buildWatcher() {
        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                mNextHelpFeedbackBtn.setEnabled(false);
                mNextHelpFeedbackBtn.setBackground(getResources().getDrawable(R.drawable.radius_button_disable_bg));
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mNextHelpFeedbackBtn.setEnabled(false);
                mNextHelpFeedbackBtn.setBackground(getResources().getDrawable(R.drawable.radius_button_disable_bg));
            }

            @Override
            public void afterTextChanged(Editable s) {
                boolean signContent = mFeedbackContentET.getText().toString().trim().length() > 0;
                boolean signContact = mContactET.getText().toString().trim().length() > 0;
                if (signContent && signContact) {
                    mNextHelpFeedbackBtn.setEnabled(true);
                    mNextHelpFeedbackBtn.setBackground(getResources().getDrawable(R.drawable.radius_button_able_bg));
                } else {
                    mNextHelpFeedbackBtn.setEnabled(false);
                    mNextHelpFeedbackBtn.setBackground(getResources().getDrawable(R.drawable.radius_button_disable_bg));
                }
            }
        };
        mFeedbackContentET.addTextChangedListener(watcher);
        mContactET.addTextChangedListener(watcher);
    }

    private void initTopBar() {
        mTopBar.setTitle(R.string.help_txt);
        mTopBar.addLeftImageButton(R.mipmap.icon_tobar_left_arrow, R.id.topbar_left_arrow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popBackStack();
            }
        });
    }

    private void SubmitFeedback() {

        UserService userService = RetrofitFactory.getInstance().getRetrofit().create(UserService.class);
        Map<String, Object> parmasMap = new HashMap<>();
        String content = mFeedbackContentET.getText().toString();
        String contact = mContactET.getText().toString();
        if (content.equals("") || content == null) {
            final QMUITipDialog tipDialog = new QMUITipDialog.Builder(getContext())
                    .setTipWord(getResources().getString(R.string.content_empty_msg))
                    .create();
            tipDialog.show();
            mNextHelpFeedbackBtn.postDelayed(new Runnable() {
                @Override
                public void run() {
                    tipDialog.dismiss();
                }
            }, 1500);
        } else if (contact.equals("") || contact == null) {
            final QMUITipDialog tipDialog = new QMUITipDialog.Builder(getContext())
                    .setTipWord(getResources().getString(R.string.contact_empty_msg))
                    .create();
            tipDialog.show();
            mNextHelpFeedbackBtn.postDelayed(new Runnable() {
                @Override
                public void run() {
                    tipDialog.dismiss();
                }
            }, 1500);
        } else if (content.length() > com.bupocket.common.Constants.HELP_FEEDBACK_CONTENT_LENGTH) {
            final QMUITipDialog tipDialog = new QMUITipDialog.Builder(getContext())
                    .setTipWord(getResources().getString(R.string.content_too_long_msg))
                    .create();
            tipDialog.show();
            mNextHelpFeedbackBtn.postDelayed(new Runnable() {
                @Override
                public void run() {
                    tipDialog.dismiss();
                }
            }, 1500);
        } else if (contact.length() > com.bupocket.common.Constants.HELP_FEEDBACK_CONTACT_LENGTH) {
            final QMUITipDialog tipDialog = new QMUITipDialog.Builder(getContext())
                    .setTipWord(getResources().getString(R.string.contact_too_long_msg))
                    .create();
            tipDialog.show();
            mNextHelpFeedbackBtn.postDelayed(new Runnable() {
                @Override
                public void run() {
                    tipDialog.dismiss();
                }
            }, 1500);
        } else {
            parmasMap.put("content", content);
            parmasMap.put("contact", contact);
            parmasMap.put("walletAddress", getWalletAddress());
            parmasMap.put("osVer", "android:"+Build.VERSION.SDK.concat("/").concat(Build.DISPLAY));
            parmasMap.put("brandName", Build.MODEL.concat("/").concat(Build.BRAND));


            try {
                //
                PackageManager pm = mContext.getPackageManager();// 获得包管理器
                PackageInfo pi = null;// 得到该应用的信息，即主Activity
                pi = pm.getPackageInfo(mContext.getPackageName(),
                        PackageManager.GET_ACTIVITIES);
                if (pi != null) {
                    String versionName = pi.versionName == null ? "null"
                            : pi.versionName;
                    parmasMap.put("walletVer", versionName);

                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }


            final QMUITipDialog txSendingTipDialog = new QMUITipDialog.Builder(getContext())
                    .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                    .setTipWord(getResources().getString(R.string.user_info_backup_loading))
                    .create();
            txSendingTipDialog.show();


            Call<ApiResult> call = userService.submitFeedback(parmasMap);
            call.enqueue(new Callback<ApiResult>() {

                @Override
                public void onResponse(Call<ApiResult> call, Response<ApiResult> response) {
                    ApiResult respDto = response.body();
                    txSendingTipDialog.dismiss();
                    final QMUITipDialog tipDialog = new QMUITipDialog.Builder(getContext())
                            .setTipWord(getResources().getString(R.string.help_feedback_submit_success_msg))
                            .create();
                    tipDialog.show();

                    mNextHelpFeedbackBtn.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            tipDialog.dismiss();
                        }
                    }, 1500);
                    mFeedbackContentET.setText("");
                    mContactET.setText("");
                }

                @Override
                public void onFailure(Call<ApiResult> call, Throwable t) {
                    txSendingTipDialog.dismiss();
                    final QMUITipDialog tipDialog = new QMUITipDialog.Builder(getContext())
                            .setTipWord(getResources().getString(R.string.help_feedback_submit_failed_msg))
                            .create();
                    tipDialog.show();
                    mNextHelpFeedbackBtn.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            tipDialog.dismiss();
                        }
                    }, 1500);
                }
            });
        }

    }
}
