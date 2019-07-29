package com.bupocket.utils;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.bupocket.R;
import com.bupocket.wallet.Wallet;
import com.bupocket.wallet.exception.WalletException;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.qmuiteam.qmui.widget.popup.QMUIPopup;

import io.bumo.model.response.AccountGetInfoResponse;

import static android.content.Context.PRINT_SERVICE;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static com.bupocket.BPApplication.getContext;

public class WalletUtils {

    private static boolean isOne;

    public static void checkToAddressValidateAndOpenAccount(final String password, final String bPData, final String fromAddr, final String toAddr, final String amount, final String fee, final ReqListener reqListener) {


        Runnable getInfoRunnable = new Runnable() {
            @Override
            public void run() {

                long nonce = 0;
                try {
                    AccountGetInfoResponse accountGetInfoResponse = Wallet.getInstance().GetInfo(toAddr);
                } catch (WalletException e) {
                    e.printStackTrace();
                    //address not exist
                    try {
                        nonce = Wallet.getInstance().getAccountNonce(fromAddr) + 1;
                        String hash = Wallet.getInstance().sendBuNoNonceVoucher(password, bPData, fromAddr, toAddr, amount, "", fee, nonce);
                        if (hash != null) {
                            isOne = true;
                            reqListener.success(0, nonce);
                        } else {
                            reqListener.failed();
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        reqListener.failed();
                    }

                }

                if (!isOne) {
                    reqListener.success(1, nonce);
                }


            }
        };
        ThreadManager.getInstance().execute(getInfoRunnable);
    }

    public static void setEditTextEyeHide(ImageView mPwdShow, EditText mSetPwdEt, boolean isHide) {
        if (!isHide) {
            mPwdShow.setImageDrawable(ContextCompat.getDrawable(getContext(), R.mipmap.icon_open_eye));
            mSetPwdEt.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_FLAG_MULTI_LINE);
            mSetPwdEt.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            mSetPwdEt.setSelection(mSetPwdEt.getText().length());
        } else {
            mPwdShow.setImageDrawable(ContextCompat.getDrawable(getContext(), R.mipmap.icon_close_eye));
            mSetPwdEt.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD|InputType.TYPE_TEXT_FLAG_MULTI_LINE);
            mSetPwdEt.setTransformationMethod(PasswordTransformationMethod.getInstance());
            mSetPwdEt.setSelection(mSetPwdEt.getText().length());
        }
    }


    public interface ReqListener {

        void success(final int status,final long nonce);


        void failed();

    }

}
