package com.bupocket.adaptor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bupocket.R;
import com.bupocket.base.AbsViewHolderAdapter;
import com.bupocket.base.BaseViewHolder;
import com.bupocket.common.Constants;
import com.bupocket.common.ConstantsType;
import com.bupocket.model.NodeSettingModel;
import com.bupocket.model.SuperNodeModel;
import com.bupocket.utils.CommonUtil;
import com.bupocket.utils.SharedPreferencesHelper;
import com.google.gson.Gson;
import com.qmuiteam.qmui.widget.dialog.QMUIBottomSheet;

import java.util.List;

import butterknife.BindView;

public class NodeSettingAdapter extends AbsViewHolderAdapter<NodeSettingModel> {


    private int currentPosition;

    public NodeSettingAdapter(@NonNull Context context) {
        super(context);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_node_setting_item;
    }

    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        currentPosition = position;
        return super.getView(position, convertView, parent);
    }

    @Override
    protected void convert(BaseViewHolder holder, final NodeSettingModel itemData) {
        View ivNodeSetSelected = holder.getView(R.id.ivNodeSetSelected);
        final View ivAddMore = holder.getView(R.id.ivAddMore);
        TextView tvNodeInfo = holder.getView(R.id.tvNodeInfo);

        if (itemData.isSelected()) ivNodeSetSelected.setVisibility(View.VISIBLE);
        else ivNodeSetSelected.setVisibility(View.INVISIBLE);

        if (itemData.isMore()) ivAddMore.setVisibility(View.VISIBLE);
        else ivAddMore.setVisibility(View.INVISIBLE);

        tvNodeInfo.setText(itemData.getUrl());
        ivAddMore.setTag(currentPosition);
        ivAddMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showSelectedView(ivAddMore);
            }
        });
    }

    @SuppressLint("ResourceAsColor")
    private void showSelectedView(final View ivAddMore) {
        final int position = (int) ivAddMore.getTag();
        final QMUIBottomSheet walletBottom = new QMUIBottomSheet(context);
        walletBottom.setContentView(R.layout.view_create_wallet);

        TextView editWalletTV = walletBottom.findViewById(R.id.tvCreateWallet);
        TextView deleteWalletTV = walletBottom.findViewById(R.id.tvRecoverWallet);
        TextView cancelTV = walletBottom.findViewById(R.id.tvDeleteWallet);
        editWalletTV.setText(R.string.edit_node_info);
        editWalletTV.setTextColor(context.getResources().getColor(R.color.app_color_green));
        deleteWalletTV.setText(R.string.delete_token_btn_txt);

        editWalletTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                walletBottom.dismiss();
                String url = getData().get(position).getUrl();
                CommonUtil.showEditMessageDialog(
                        context,
                        context.getString(R.string.add_node_address_title),
                        context.getString(R.string.add_node_address_title),
                        url,
                        new CommonUtil.ConfirmListener() {
                            @Override
                            public void confirm(String url) {
                                getData().get(position).setUrl(url);

//                                saveNodeData();
                                notifyDataSetChanged();
                            }
                        });


            }
        });

        deleteWalletTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                walletBottom.dismiss();
                getData().remove(position);
                notifyDataSetChanged();
            }
        });

        cancelTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                walletBottom.dismiss();
            }
        });
        walletBottom.show();
    }


    public void saveNodeData() {
        List<NodeSettingModel> data = getData();
        String json = new Gson().toJson(data);
        SharedPreferencesHelper spHelper = new SharedPreferencesHelper(context, ConstantsType.BU_POCKET);
        spHelper.put(Constants.BUMO_NODE_URL, json);
    }


}
