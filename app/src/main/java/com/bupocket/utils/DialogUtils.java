package com.bupocket.utils;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.bupocket.R;
import com.bupocket.enums.ExceptionEnum;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;

public class DialogUtils {



    public static void showConfirmDialog(Context mContext, String msg, final DialogUtils.KnowListener knowListener){
        final QMUIDialog qmuiDialog = new QMUIDialog.CustomDialogBuilder(mContext).
                setLayout(R.layout.view_com_dialog_no_title).create();
        ((TextView) qmuiDialog.findViewById(R.id.dialogMsgTV)).setText(msg);
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



    public static void showUpdateAppDialog(Context mContext, String title,String msg, final DialogUtils.KnowListener knowListener){
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

    public static void showEditMessageDialog(Context mContext, String title, String msgHint, String msg, final DialogUtils.ConfirmListener confirmListener) {
        final QMUIDialog qmuiDialog = new QMUIDialog(mContext);
        qmuiDialog.setCanceledOnTouchOutside(false);
        qmuiDialog.setContentView(R.layout.view_change_wallet_name);
        TextView titleTV = qmuiDialog.findViewById(R.id.dialogEditTitle);
        TextView cancelTv = qmuiDialog.findViewById(R.id.changeNameCancel);
        TextView confirmTv = qmuiDialog.findViewById(R.id.changeNameConfirm);
        final EditText infoET = qmuiDialog.findViewById(R.id.walletNewNameEt);
        titleTV.setText(title);

        infoET.setHint(msgHint);
        if (!msg.isEmpty()) {
            infoET.setText(msg);
        }

        cancelTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qmuiDialog.dismiss();
            }
        });
        confirmTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qmuiDialog.dismiss();
                confirmListener.confirm(infoET.getText().toString().trim());
            }
        });

        qmuiDialog.show();
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


    public interface KnowListener {

        void Know();
    }

    public interface ConfirmListener {

        void confirm(String url);
    }
}
