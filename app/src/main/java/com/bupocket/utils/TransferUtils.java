package com.bupocket.utils;

import android.content.Context;
import android.support.annotation.FractionRes;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.bupocket.R;
import com.bupocket.common.Constants;
import com.qmuiteam.qmui.widget.dialog.QMUIBottomSheet;

import kotlin.collections.AbstractMutableMap;

public class TransferUtils {




    public static void confirmTxSheet(Context context, String fromAddress, String destAddress, String amount, Double fee, String metaData,String input, final TransferListener transferListener) {
        confirmTxSheet(context, fromAddress, destAddress, "", amount, fee, metaData, input, transferListener);
    }

    public static void confirmTxSheet(Context context, String fromAddress, String destAddress, String destTag, String amount, Double fee, String metaData, String input, final TransferListener transferListener) {

        String tx_fee = String.valueOf(fee);
        String destAddressTag = destAddress;
        if (!TextUtils.isEmpty(destTag)) {
            destAddressTag = destAddress.concat(destTag);
        }

        amount = CommonUtil.thousandSeparator(amount);

        final QMUIBottomSheet qmuiBottomSheet = new QMUIBottomSheet(context);
        qmuiBottomSheet.setContentView(qmuiBottomSheet.getLayoutInflater().inflate(R.layout.view_transfer_confirm, null));
        TextView mTransactionDetailTv = qmuiBottomSheet.findViewById(R.id.transactionDetailTv);
        TextView mDestAddressTv = qmuiBottomSheet.findViewById(R.id.destAddressTv);
        TextView mTxFeeTv = qmuiBottomSheet.findViewById(R.id.txFeeTv);
        TextView mSourceAddressTv = qmuiBottomSheet.findViewById(R.id.sourceAddressTv);
        TextView mDetailsDestAddressTv = qmuiBottomSheet.findViewById(R.id.detailsDestAddressTv);
        TextView mDetailsAmountTv = qmuiBottomSheet.findViewById(R.id.detailsAmountTv);
        TextView mDetailsTxFeeTv = qmuiBottomSheet.findViewById(R.id.detailsTxFeeTv);
        TextView mTransactionParamsTv = qmuiBottomSheet.findViewById(R.id.transactionParamsTv);
        TextView mDestAddressTvHint = qmuiBottomSheet.findViewById(R.id.destAddressTvHint);
        TextView mDetailsDestAddressTvHint = qmuiBottomSheet.findViewById(R.id.detailsDestAddressTvHint);


        mTransactionDetailTv.setText(metaData);
        mDestAddressTv.setText(destAddressTag);
        mSourceAddressTv.setText(fromAddress);
        mDetailsDestAddressTv.setText(destAddress);
        mTransactionParamsTv.setText(input);
        mDetailsAmountTv.setText(amount);
        mDetailsTxFeeTv.setText(tx_fee);
        mTxFeeTv.setText(tx_fee);


        if (TextUtils.isEmpty(destAddress)) {
            mDestAddressTv.setVisibility(View.GONE);
            mDestAddressTvHint.setVisibility(View.GONE);
            mDetailsDestAddressTv.setVisibility(View.GONE);
            mDetailsDestAddressTvHint.setVisibility(View.GONE);
        } else {
            mDestAddressTv.setVisibility(View.VISIBLE);
            mDestAddressTvHint.setVisibility(View.VISIBLE);
            mDetailsDestAddressTv.setVisibility(View.VISIBLE);
            mDetailsDestAddressTvHint.setVisibility(View.VISIBLE);
        }


        qmuiBottomSheet.findViewById(R.id.detailBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qmuiBottomSheet.findViewById(R.id.confirmLl).setVisibility(View.GONE);
                qmuiBottomSheet.findViewById(R.id.confirmDetailsLl).setVisibility(View.VISIBLE);
            }
        });

        qmuiBottomSheet.findViewById(R.id.goBackBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qmuiBottomSheet.findViewById(R.id.confirmLl).setVisibility(View.VISIBLE);
                qmuiBottomSheet.findViewById(R.id.confirmDetailsLl).setVisibility(View.GONE);
            }
        });


        qmuiBottomSheet.findViewById(R.id.sendConfirmBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qmuiBottomSheet.dismiss();
                transferListener.confirm();
            }
        });
        qmuiBottomSheet.findViewById(R.id.cancelBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qmuiBottomSheet.dismiss();
            }
        });
        qmuiBottomSheet.findViewById(R.id.tvConfirmCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qmuiBottomSheet.dismiss();
            }
        });
        qmuiBottomSheet.show();

    }

    public static void confirmSendVoucherDialog(Context context,
                                                String fromAddress,
                                                String destAddress,
                                                String destTag,
                                                String amount,
                                                String fee,
                                                String metaData,
                                                String input,String remark,
                                                final TransferListener transferListener) {

        String tx_fee = fee;
        String destAddressTag = destAddress;
        if (!TextUtils.isEmpty(destTag)) {
            destAddressTag = destAddress.concat(destTag);
        }

        amount = CommonUtil.thousandSeparator(amount);

        final QMUIBottomSheet qmuiBottomSheet = new QMUIBottomSheet(context);
        qmuiBottomSheet.setContentView(qmuiBottomSheet.getLayoutInflater().inflate(R.layout.view_voucher_transfer_confirm, null));
        TextView mTransactionDetailTv = qmuiBottomSheet.findViewById(R.id.transactionDetailTv);
        TextView mDestAddressTv = qmuiBottomSheet.findViewById(R.id.destAddressTv);
        TextView mTxFeeTv = qmuiBottomSheet.findViewById(R.id.txFeeTv);
        TextView mSourceAddressTv = qmuiBottomSheet.findViewById(R.id.sourceAddressTv);
        TextView mDetailsDestAddressTv = qmuiBottomSheet.findViewById(R.id.detailsDestAddressTv);
        TextView mDetailsAmountTv = qmuiBottomSheet.findViewById(R.id.detailsAmountTv);
        TextView mDetailsTxFeeTv = qmuiBottomSheet.findViewById(R.id.detailsTxFeeTv);
        TextView mTransactionParamsTv = qmuiBottomSheet.findViewById(R.id.transactionParamsTv);
        TextView mDestAddressTvHint = qmuiBottomSheet.findViewById(R.id.destAddressTvHint);
        TextView mDetailsDestAddressTvHint = qmuiBottomSheet.findViewById(R.id.detailsDestAddressTvHint);
        TextView sendNumTv=qmuiBottomSheet.findViewById(R.id.sendNumTv);
        TextView remarkTv=qmuiBottomSheet.findViewById(R.id.remarkTv);

        remarkTv.setText(remark);

        sendNumTv.setText(amount);
        mTransactionDetailTv.setText(metaData);
        mDestAddressTv.setText(destAddressTag);
        mSourceAddressTv.setText(fromAddress);
        mDetailsDestAddressTv.setText(destAddress);
        mTransactionParamsTv.setText(input);
        mDetailsAmountTv.setText(amount);
        mDetailsTxFeeTv.setText(tx_fee);
        mTxFeeTv.setText(tx_fee);


        if (TextUtils.isEmpty(destAddress)) {
            mDestAddressTv.setVisibility(View.GONE);
            mDestAddressTvHint.setVisibility(View.GONE);
            mDetailsDestAddressTv.setVisibility(View.GONE);
            mDetailsDestAddressTvHint.setVisibility(View.GONE);
        } else {
            mDestAddressTv.setVisibility(View.VISIBLE);
            mDestAddressTvHint.setVisibility(View.VISIBLE);
            mDetailsDestAddressTv.setVisibility(View.VISIBLE);
            mDetailsDestAddressTvHint.setVisibility(View.VISIBLE);
        }


        qmuiBottomSheet.findViewById(R.id.detailBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qmuiBottomSheet.findViewById(R.id.confirmLl).setVisibility(View.GONE);
                qmuiBottomSheet.findViewById(R.id.confirmDetailsLl).setVisibility(View.VISIBLE);
            }
        });

        qmuiBottomSheet.findViewById(R.id.goBackBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qmuiBottomSheet.findViewById(R.id.confirmLl).setVisibility(View.VISIBLE);
                qmuiBottomSheet.findViewById(R.id.confirmDetailsLl).setVisibility(View.GONE);
            }
        });


        qmuiBottomSheet.findViewById(R.id.sendConfirmBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qmuiBottomSheet.dismiss();
                transferListener.confirm();
            }
        });
        qmuiBottomSheet.findViewById(R.id.cancelBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qmuiBottomSheet.dismiss();
            }
        });
        qmuiBottomSheet.findViewById(R.id.tvConfirmCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qmuiBottomSheet.dismiss();
            }
        });
        qmuiBottomSheet.show();

    }



    public interface TransferListener {

        void confirm();
    }
}
