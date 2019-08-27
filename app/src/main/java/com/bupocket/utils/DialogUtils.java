package com.bupocket.utils;

import android.app.Activity;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bupocket.R;
import com.bupocket.enums.ExceptionEnum;
import com.bupocket.interfaces.SignatureListener;
import com.bupocket.wallet.Wallet;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.qmuiteam.qmui.widget.popup.QMUIPopup;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class DialogUtils {

    public static QMUIDialog showConfirmNoTitleDialog(Context mContext, String msg,String confirmStr, final DialogUtils.KnowListener knowListener) {
        final QMUIDialog qmuiDialog = new QMUIDialog.CustomDialogBuilder(mContext).
                setLayout(R.layout.view_com_dialog_confirm_no_title).create();



        if (TextUtils.isEmpty(msg)) {
            qmuiDialog.findViewById(R.id.dialogMessageTv).setVisibility(View.GONE);
        } else {
            ((TextView) qmuiDialog.findViewById(R.id.dialogMessageTv)).setText(msg);
        }

        qmuiDialog.findViewById(R.id.dialogCancelTV).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qmuiDialog.dismiss();
            }
        });
        TextView confirmTv = qmuiDialog.findViewById(R.id.dialogConfirmTV);

        if (!TextUtils.isEmpty(confirmStr)) {
            confirmTv.setText(confirmStr);
        }

        confirmTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qmuiDialog.dismiss();
                knowListener.Know();
            }
        });
        qmuiDialog.show();

        return qmuiDialog;
    }

    public static QMUIDialog showConfirmNoTitleDialog(Context mContext, String msg, final DialogUtils.KnowListener knowListener) {
        final QMUIDialog qmuiDialog = new QMUIDialog.CustomDialogBuilder(mContext).
                setLayout(R.layout.view_com_dialog_confirm_no_title).create();


        if (TextUtils.isEmpty(msg)) {
            qmuiDialog.findViewById(R.id.dialogMessageTv).setVisibility(View.GONE);
        } else {
            ((TextView) qmuiDialog.findViewById(R.id.dialogMessageTv)).setText(msg);
        }

        qmuiDialog.findViewById(R.id.dialogCancelTV).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qmuiDialog.dismiss();
            }
        });
        qmuiDialog.findViewById(R.id.dialogConfirmTV).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qmuiDialog.dismiss();
                knowListener.Know();
            }
        });
        qmuiDialog.show();

        return qmuiDialog;
    }


    public static void showConfirmDialog(Context mContext, String title, String msg, final DialogUtils.KnowListener knowListener) {
        final QMUIDialog qmuiDialog = new QMUIDialog.CustomDialogBuilder(mContext).
                setLayout(R.layout.view_com_dialog_confirm).create();

        if (TextUtils.isEmpty(title)) {
            qmuiDialog.findViewById(R.id.dialogTitleTv).setVisibility(View.GONE);
        } else {
            ((TextView) qmuiDialog.findViewById(R.id.dialogTitleTv)).setText(title);
        }

        if (TextUtils.isEmpty(msg)) {
            qmuiDialog.findViewById(R.id.dialogMessageTv).setVisibility(View.GONE);
        } else {
            ((TextView) qmuiDialog.findViewById(R.id.dialogMessageTv)).setText(msg);
        }

        qmuiDialog.findViewById(R.id.dialogCancelTV).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qmuiDialog.dismiss();
            }
        });
        qmuiDialog.findViewById(R.id.dialogConfirmTV).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qmuiDialog.dismiss();
                knowListener.Know();
            }
        });
        qmuiDialog.show();
    }


    public static void showUpdateAppDialog(Context mContext, String title, String msg, final DialogUtils.KnowListener knowListener) {
        final QMUIDialog qmuiDialog = new QMUIDialog.CustomDialogBuilder(mContext).
                setLayout(R.layout.view_com_dialog_update_app).create();
        ((TextView) qmuiDialog.findViewById(R.id.updateTitleTV)).setText(title);
        ((TextView) qmuiDialog.findViewById(R.id.updateInfoTV)).setText(msg);
        qmuiDialog.findViewById(R.id.cancelTV).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qmuiDialog.dismiss();
            }
        });
        qmuiDialog.findViewById(R.id.confirmTV).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qmuiDialog.dismiss();
                knowListener.Know();
            }
        });
        qmuiDialog.show();
    }


    public static void showEditMessageDialog(Context mContext, String title, String msg, final DialogUtils.ConfirmListener confirmListener) {
        showEditMessageDialog(mContext, title, msg, "", confirmListener);

    }

    public static void showEditMessageDialog(Context mContext, final String title, String msgHint, String msg, final DialogUtils.ConfirmListener confirmListener) {

        final QMUIDialog qmuiDialog = new QMUIDialog(mContext);
        qmuiDialog.setCanceledOnTouchOutside(false);
        qmuiDialog.setContentView(R.layout.view_change_wallet_name);
        qmuiDialog.show();

        TextView cancelTv = qmuiDialog.findViewById(R.id.cancelNameTv);
        final TextView confirmTv = qmuiDialog.findViewById(R.id.confirmNameTv);
        final EditText walletNewNameEt = qmuiDialog.findViewById(R.id.walletNewNameEt);
        TextView titleTV = qmuiDialog.findViewById(R.id.dialogEditTitle);


        if (!TextUtils.isEmpty(title)) {
            titleTV.setText(title);
        }

        if (!TextUtils.isEmpty(msgHint)) {
            walletNewNameEt.setHint(msgHint);
        }


        if (!msg.isEmpty()) {
            walletNewNameEt.setText(msg);
        }

        cancelTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qmuiDialog.dismiss();
            }
        });

        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                confirmTv.setClickable(false);
                confirmTv.setEnabled(false);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                confirmTv.setClickable(false);
                confirmTv.setEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (walletNewNameEt.getText().toString().trim().length() > 0) {
                    confirmTv.setClickable(true);
                    confirmTv.setEnabled(true);
                }
            }
        };
        walletNewNameEt.addTextChangedListener(watcher);

        confirmTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmListener.confirm(walletNewNameEt.getText().toString().trim());
                qmuiDialog.dismiss();
            }
        });

    }


    public static void showTitleDialog(Context mContext, String msg, String title) {
        final QMUIDialog qmuiDialog = new QMUIDialog.CustomDialogBuilder(mContext).
                setLayout(R.layout.qmui_com_dialog_green).create();
        qmuiDialog.findViewById(R.id.tvComKnow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qmuiDialog.dismiss();
            }
        });
        TextView tvTitle = qmuiDialog.findViewById(R.id.tvComTitle);
        tvTitle.setVisibility(View.VISIBLE);
        tvTitle.setText(title);
        ((TextView) qmuiDialog.findViewById(R.id.tvComMassage)).setText(msg);
        qmuiDialog.show();

    }

    public static void showTitleDialog(Context mContext, int msg, int title) {
        final QMUIDialog qmuiDialog = new QMUIDialog.CustomDialogBuilder(mContext).
                setLayout(R.layout.qmui_com_dialog_green).create();
        qmuiDialog.findViewById(R.id.tvComKnow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qmuiDialog.dismiss();
            }
        });
        TextView tvTitle = qmuiDialog.findViewById(R.id.tvComTitle);
        tvTitle.setVisibility(View.VISIBLE);
        tvTitle.setText(title);
        ((TextView) qmuiDialog.findViewById(R.id.tvComMassage)).setText(msg);
        qmuiDialog.show();

    }


    /**
     * @param mContext
     * @param notice   error massage
     * @param code     error code
     */
    public static void showMessageNoTitleDialog(Context mContext, String notice, String code) {
        String errMsg = byCodeToMsg(mContext, code);
        if (!errMsg.isEmpty()) {
            notice = errMsg;
        }

        showMessageNoTitleDialog(mContext, notice);

    }


    public static void showMessageNoTitleDialog(Context mContext, int notice) {
        showMessageNoTitleDialog(mContext, mContext.getResources().getString(notice));
    }

    /**
     * get  error massage
     *
     * @param mContext
     * @param code     error code
     * @return error massage
     */
    public static String byCodeToMsg(Context mContext, String code) {
        ExceptionEnum byValue = ExceptionEnum.getByValue(code);
        if (byValue == null) {
            return "";
        }
        return mContext.getResources().getString(byValue.getMsg());
    }


    public static void showMessageNoTitleDialog(Context mContext, String msg) {
        final QMUIDialog qmuiDialog = new QMUIDialog.CustomDialogBuilder(mContext).
                setLayout(R.layout.qmui_com_dialog_green_no_title).create();
        qmuiDialog.findViewById(R.id.tvComKnow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qmuiDialog.dismiss();
            }
        });
        ((TextView) qmuiDialog.findViewById(R.id.tvComMassage)).setText(msg);
        qmuiDialog.show();

    }

    public static void showMessageNoTitleDialog(Context mContext, String msg, final DialogUtils.KnowListener knowListener) {
        final QMUIDialog qmuiDialog = new QMUIDialog.CustomDialogBuilder(mContext).
                setLayout(R.layout.qmui_com_dialog_green_no_title).create();
        qmuiDialog.findViewById(R.id.tvComKnow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qmuiDialog.dismiss();
                knowListener.Know();
            }
        });
        ((TextView) qmuiDialog.findViewById(R.id.tvComMassage)).setText(msg);
        qmuiDialog.show();

    }

    public static void showMessageDialog(Context mContext, String msg, String title, final DialogUtils.KnowListener knowListener) {
        final QMUIDialog qmuiDialog = new QMUIDialog.CustomDialogBuilder(mContext).
                setLayout(R.layout.qmui_com_dialog_green).create();
        qmuiDialog.findViewById(R.id.tvComKnow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qmuiDialog.dismiss();
                knowListener.Know();
            }
        });
        TextView tvTitle = qmuiDialog.findViewById(R.id.tvComTitle);
        if (!TextUtils.isEmpty(title)) {
            tvTitle.setVisibility(View.VISIBLE);
            tvTitle.setText(title);
        }

        ((TextView) qmuiDialog.findViewById(R.id.tvComMassage)).setText(msg);
        qmuiDialog.show();

    }


    public static void showMessageScrollDialog(Context mContext, String msg, String title, final DialogUtils.KnowListener knowListener) {
        final QMUIDialog qmuiDialog = new QMUIDialog.CustomDialogBuilder(mContext).
                setLayout(R.layout.qmui_com_dialog_green_scroll).create();
        qmuiDialog.findViewById(R.id.tvComKnow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qmuiDialog.dismiss();
                knowListener.Know();
            }
        });

        TextView tvTitle = qmuiDialog.findViewById(R.id.tvComTitle);
        if (!TextUtils.isEmpty(title)) {
            tvTitle.setVisibility(View.VISIBLE);
            tvTitle.setText(title);
        }

        ((TextView) qmuiDialog.findViewById(R.id.tvComMassage)).setText(msg);
        qmuiDialog.show();

    }

    /**
     * @param mContext
     * @param confirmListener
     */
    public static void showPassWordInputDialog(final Activity mContext,
                                               final ConfirmListener confirmListener) {
        showPassWordInputDialog(mContext, "", "", "", 0, confirmListener);
    }


    /**
     *
     * use
     * @param mContext
     * @param input
     * @param confirmListener
     */
    public static void showPassWordInputDialog(final Activity mContext,
                                               String input,
                                               final ConfirmListener confirmListener) {
        showPassWordInputDialog(mContext, "", input, "", 0, confirmListener);
    }


    /**
     * @param mContext
     * @param input
     * @param etInput
     * @param confirmListener
     */
    public static void showPassWordInputDialog(final Activity mContext,
                                               String input,
                                               String etInput,
                                               final ConfirmListener confirmListener) {
        showPassWordInputDialog(mContext, "", input, etInput, 0, confirmListener);
    }

    /**
     * @param mContext
     * @param title
     * @param input
     * @param etInput
     * @param confirmListener
     */
    public static void showPassWordInputDialog(final Activity mContext,
                                               String title, String input,
                                               String etInput, int inputColor, final ConfirmListener confirmListener) {

        final QMUIDialog qmuiDialog = new QMUIDialog(mContext);
        qmuiDialog.setCanceledOnTouchOutside(false);
        qmuiDialog.setContentView(R.layout.view_password_comfirm_common);
        qmuiDialog.show();
        final TextView mPasswordConfirmBtn = qmuiDialog.findViewById(R.id.dialogConfirmTV);
        TextView titleTv = (TextView) qmuiDialog.findViewById(R.id.passwordConfirmTitle);
        TextView inputTv = (TextView) qmuiDialog.findViewById(R.id.passwordConfirmNotice);
        final EditText passwordEt = qmuiDialog.findViewById(R.id.passwordConfirmEt);
        if (!TextUtils.isEmpty(title)) {
            titleTv.setText(title);
        }
        if (!TextUtils.isEmpty(input)) {
            inputTv.setText(input);
        }
        if (!TextUtils.isEmpty(etInput)) {
            passwordEt.setHint(etInput);
        }

        if (!(inputColor == 0)) {
            inputTv.setTextColor(inputColor);
        }

        TextView mPasswordConfirmCloseTv = qmuiDialog.findViewById(R.id.dialogCancelTV);
        final InputMethodManager inputManager = (InputMethodManager) passwordEt.getContext().
                getSystemService(Context.INPUT_METHOD_SERVICE);

        mPasswordConfirmCloseTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputManager.hideSoftInputFromWindow(passwordEt.getWindowToken(),0);
                qmuiDialog.dismiss();
            }
        });


        mPasswordConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputManager.hideSoftInputFromWindow(passwordEt.getWindowToken(),0);
                confirmListener.confirm(passwordEt.getText().toString().trim());
                qmuiDialog.dismiss();
            }
        });

        passwordEt.postDelayed(new Runnable() {
            @Override
            public void run() {
                passwordEt.setFocusable(true);
                passwordEt.setFocusableInTouchMode(true);
                passwordEt.requestFocus();
                passwordEt.findFocus();

                inputManager.showSoftInput(passwordEt, InputMethodManager.SHOW_FORCED);
            }
        }, 10);

        mPasswordConfirmBtn.setEnabled(false);
        passwordEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String trim = passwordEt.getText().toString().trim();
                if (TextUtils.isEmpty(trim)) {
                    mPasswordConfirmBtn.setEnabled(false);
                } else {
                    mPasswordConfirmBtn.setEnabled(true);
                }
            }
        });

    }

    public static void showPassWordInputDialog(final Activity mContext, String title, String input, String etInpput, ConfirmListener confirmListener) {
        showPassWordInputDialog(mContext,title,input,etInpput,0,confirmListener);
    }


    public interface KnowListener {

        void Know();
    }

    public interface ConfirmListener {

        void confirm(final String password);
    }


    public static void getSignatureInfo(final Activity mActivity, final Context mContext, final String accountData, final String walletAddress, final SignatureListener listener) {

        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                DialogUtils.showPassWordInputDialog(mActivity, mContext.getString(R.string.voucher_pwd_confirm_hint), new ConfirmListener() {
                    @Override
                    public void confirm(final String msg) {

                        final QMUITipDialog txSendingTipDialog = new QMUITipDialog.Builder(mContext)
                                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                                .setTipWord(mContext.getResources().getString(R.string.send_tx_sign_txt))
                                .create();
                        txSendingTipDialog.show();

//
                                String pkbyAccountPassword = null;
                                try {
                                    pkbyAccountPassword = Wallet.getInstance().getPKBYAccountPassword(msg, accountData, walletAddress);
                                    txSendingTipDialog.dismiss();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    ToastUtil.showToast(mActivity, R.string.checking_password_error, Toast.LENGTH_LONG);
                                    txSendingTipDialog.dismiss();
                                }

                                if (pkbyAccountPassword == null || pkbyAccountPassword.isEmpty()) {
                                    return;
                                }

                                final String finalPkbyAccountPassword = pkbyAccountPassword;
                                mActivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        listener.success(finalPkbyAccountPassword);
                                    }
                                });


//                            }
//                        }).start();

                    }
                });
            }
        });
    }



    public static void showInformationDialog(String msg,Context mContext,View v){

       QMUIPopup  qmuiPopup = new QMUIPopup(mContext, QMUIPopup.DIRECTION_NONE);
        TextView textView = new TextView(mContext);
        textView.setLayoutParams(qmuiPopup.generateLayoutParam(
                QMUIDisplayHelper.dp2px(mContext, 280),
                WRAP_CONTENT
        ));
        textView.setLineSpacing(QMUIDisplayHelper.dp2px(mContext, 4), 1.0f);
        int padding = QMUIDisplayHelper.dp2px(mContext, 20);
        textView.setPadding(padding, padding, padding, padding);
        textView.setText(msg);
        textView.setTextColor(ContextCompat.getColor(mContext, R.color.app_color_white));
        textView.setBackgroundColor(mContext.getResources().getColor(R.color.popup_background_color));
        qmuiPopup.setContentView(textView);
        qmuiPopup.setAnimStyle(QMUIPopup.ANIM_GROW_FROM_CENTER);
        qmuiPopup.setPreferredDirection(QMUIPopup.DIRECTION_BOTTOM);
        qmuiPopup.show(v);
        ImageView arrowUp = qmuiPopup.getDecorView().findViewById(R.id.arrow_up);
        arrowUp.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.triangle));

    }

}
