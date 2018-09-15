package com.bupocket.fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.bupocket.R;
import com.bupocket.base.BaseFragment;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;
import com.bupocket.fragment.BPSendStatusFragment;
import com.qmuiteam.qmui.widget.dialog.QMUIBottomSheet;

public class BPSendTokenFragment extends BaseFragment {
    @BindView(R.id.topbar)
    QMUITopBarLayout mTopBar;
    @Override
    protected View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_send, null);
        ButterKnife.bind(this, root);
        QMUIStatusBarHelper.setStatusBarLightMode(getBaseFragmentActivity());
        confirmSendInfo(root);
        initTopBar();

        return root;
    }

    private void initTopBar() {
        mTopBar.addLeftImageButton(R.mipmap.icon_tobar_left_arrow, R.id.topbar_left_arrow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popBackStack();
            }
        });
    }

    private void confirmSendInfo(View view){

        view.findViewById(R.id.completeMnemonicCodeBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText addressET = getView().findViewById(R.id.destAccountAddressEt);
                String address = addressET.getText().toString();

                EditText sendAmountET = getView().findViewById(R.id.sendAmountEt);
                String sendAmount = sendAmountET.getText().toString();

                EditText sendFormNoteEt = getView().findViewById(R.id.sendFormNoteEt);
                String note = sendFormNoteEt.getText().toString();

                EditText sendFormTxFeeEt = getView().findViewById(R.id.sendFormTxFeeEt);
                String txFee = sendFormTxFeeEt.getText().toString();

                QMUIBottomSheet sheet = new QMUIBottomSheet(getContext());
                sheet.setContentView(R.layout.fragment_send_confirm);

                TextView addressTxt = sheet.findViewById(R.id.sendTargetAddress);
                addressTxt.setText(address);

                TextView amountTxt = sheet.findViewById(R.id.sendAmount);
                amountTxt.setText(sendAmount);

                TextView estimateCostTxt = sheet.findViewById(R.id.sendEstimateCost);
                estimateCostTxt.setText(txFee);

                TextView remarkTxt = sheet.findViewById(R.id.sendRemark);
                remarkTxt.setText(note);

                sheet.show();
            }
        });
    }
}
