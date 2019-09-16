package com.bupocket.adaptor;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bupocket.BPApplication;
import com.bupocket.R;
import com.bupocket.base.AbsViewHolderAdapter;
import com.bupocket.base.BaseViewHolder;
import com.bupocket.common.Constants;
import com.bupocket.common.ConstantsType;
import com.bupocket.enums.BumoNodeEnum;
import com.bupocket.fragment.BPCurrencyFragment2;
import com.bupocket.fragment.BPNodeSettingFragment;
import com.bupocket.model.NodeAddressModel;
import com.bupocket.model.NodeSettingModel;
import com.bupocket.utils.DialogUtils;
import com.bupocket.utils.SharedPreferencesHelper;
import com.bupocket.utils.ToastUtil;
import com.google.gson.Gson;
import com.qmuiteam.qmui.widget.dialog.QMUIBottomSheet;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.bupocket.fragment.BPCurrencyFragment2.*;

public class CurrencyAdapter extends AbsViewHolderAdapter<NodeSettingModel> {


    private final Activity mActivity;
    private int currentPosition;

    public CurrencyAdapter(@NonNull Activity mActivity) {
        super(mActivity);
        this.mActivity = mActivity;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_currency_item;
    }

    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        currentPosition = position;
        return super.getView(position, convertView, parent);
    }

    @Override
    protected void convert(BaseViewHolder holder, final NodeSettingModel itemData) {
        View ivNodeSetSelected = holder.getView(R.id.ivNodeSetSelected);

        TextView tvNodeInfo = holder.getView(R.id.tvNodeInfo);

        if (itemData.isSelected()) ivNodeSetSelected.setVisibility(View.VISIBLE);
        else ivNodeSetSelected.setVisibility(View.INVISIBLE);

        tvNodeInfo.setText(itemData.getUrl());

        SharedPreferencesHelper spHelper = new SharedPreferencesHelper(context, "buPocket");
        if (((String) spHelper.getSharedPreference("currencyType", currencyArray[0])).equals(itemData.getUrl())) {
            ivNodeSetSelected.setVisibility(View.VISIBLE);
        } else {
            ivNodeSetSelected.setVisibility(View.GONE);
        }


    }

}
