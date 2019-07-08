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
import com.bupocket.model.NodeAddressModel;
import com.bupocket.model.NodeSettingModel;
import com.bupocket.utils.DialogUtils;
import com.bupocket.utils.LogUtils;
import com.bupocket.utils.SharedPreferencesHelper;
import com.bupocket.utils.ToastUtil;
import com.google.gson.Gson;
import com.qmuiteam.qmui.widget.dialog.QMUIBottomSheet;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;

import java.io.IOException;
import java.util.List;
import java.util.logging.Handler;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NodeSettingAdapter extends AbsViewHolderAdapter<NodeSettingModel> {


    private final Activity mActivity;
    private int currentPosition;

    public NodeSettingAdapter(Context context, @NonNull Activity mActivity) {
        super(context);
        this.mActivity = mActivity;
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
        String testTitle="";
        if (SharedPreferencesHelper.getInstance().getInt("bumoNode", Constants.DEFAULT_BUMO_NODE) == BumoNodeEnum.TEST.getCode()) {
            testTitle = "(" + context.getString(R.string.current_test_message_txt) + ")";
        }

        final String finalTestTitle = testTitle;
        editWalletTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                walletBottom.dismiss();
                String url = getData().get(position).getUrl();
                DialogUtils.showEditMessageDialog(
                        context,
                        context.getString(R.string.edit_node_address_title)+ finalTestTitle,
                        context.getString(R.string.add_node_address_title),
                        url,
                        new DialogUtils.ConfirmListener() {
                            @Override
                            public void confirm(final String url) {
                                invalidNodeAddress(url, position, new NodeAddressListener() {
                                    @Override
                                    public void success(String url) {

                                    }

                                    @Override
                                    public void failed() {

                                    }
                                });
                            }
                        });


            }
        });

        deleteWalletTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                walletBottom.dismiss();

                if (getData().get(position).isSelected()) {
                    DialogUtils.showConfirmDialog(context, context.getString(R.string.confirm_delete_node), context.getString(R.string.delete_node_info), new DialogUtils.KnowListener() {
                        @Override
                        public void Know() {
                            getData().remove(position);
                            saveNodeData(0);
                            BPApplication.switchNetConfig(null);
                            notifyDataSetChanged();

                        }
                    });

                } else {
                    DialogUtils.showConfirmDialog(context, context.getString(R.string.confirm_delete_node), "", new DialogUtils.KnowListener() {
                        @Override
                        public void Know() {
                            getData().remove(position);
                            notifyDataSetChanged();
                        }
                    });
                }


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

    public void invalidNodeAddress(final String url, final int position, final NodeAddressListener nodeListener) {


        if (TextUtils.isEmpty(url)) {
            ToastUtil.showToast(mActivity, R.string.add_node_address_title, Toast.LENGTH_SHORT);
            nodeListener.failed();
            return;
        }

        for (int i = 0; i < getData().size(); i++) {
            if (getData().get(i).getUrl().equals(url.trim())) {
                ToastUtil.showToast(mActivity, R.string.node_address_repeat, Toast.LENGTH_SHORT);
                nodeListener.failed();
                return;
            }
        }


        final QMUITipDialog txSendingTipDialog = new QMUITipDialog.Builder(context)
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                .setTipWord(context.getResources().getString(R.string.user_info_backup_loading))
                .create();
        txSendingTipDialog.show();


        try {
            //check url
            final OkHttpClient okHttpClient = new OkHttpClient();
            final Request request = new Request.Builder()
                    .url(url + Constants.BUMO_NODE_URL_PATH)
                    .build();

            okHttpClient.newCall(request).enqueue(new okhttp3.Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    ToastUtil.showToast(mActivity, R.string.invalid_node_address, Toast.LENGTH_SHORT);
                    nodeListener.failed();
                    txSendingTipDialog.dismiss();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {

                    try {
                        String json = response.body().string();
                        NodeAddressModel nodeAddressModel = new Gson().fromJson(json, NodeAddressModel.class);
                        if (nodeAddressModel.getError_code() == 0) {
                            final int newSeq = nodeAddressModel.getResult().getHeader().getSeq();

                            final Request request1 = new Request.Builder()
                                    .url(Constants.BUMO_NODE_URL + Constants.BUMO_NODE_URL_PATH)
                                    .build();
                            okHttpClient.newCall(request1).enqueue(new Callback() {
                                @Override
                                public void onFailure(Call call, IOException e) {
                                    ToastUtil.showToast(mActivity, R.string.invalid_node_address, Toast.LENGTH_SHORT);
                                    txSendingTipDialog.dismiss();
                                    nodeListener.failed();
                                }

                                @Override
                                public void onResponse(Call call, Response response) throws IOException {
                                    String json = response.body().string();
                                    NodeAddressModel nodeAddressModel = new Gson().fromJson(json, NodeAddressModel.class);
                                    if (nodeAddressModel.getError_code() == 0) {
                                        int oldSeq = nodeAddressModel.getResult().getHeader().getSeq();
                                        int resultSeq = oldSeq - newSeq;
                                        if (resultSeq >=0 && resultSeq < 10) {

                                            mActivity.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    txSendingTipDialog.dismiss();
                                                    nodeListener.success(url);
                                                    if (!(position == 0)) {
                                                        getData().get(position).setUrl(url);
                                                        notifyDataSetChanged();
                                                    }
                                                }
                                            });


                                        } else {
                                            ToastUtil.showToast(mActivity, R.string.invalid_node_address, Toast.LENGTH_SHORT);
                                            txSendingTipDialog.dismiss();
                                            nodeListener.failed();
                                        }
                                    } else {
                                        ToastUtil.showToast(mActivity, R.string.invalid_node_address, Toast.LENGTH_SHORT);
                                        txSendingTipDialog.dismiss();
                                        nodeListener.failed();
                                    }
                                }
                            });

                        } else {
                            ToastUtil.showToast(mActivity, R.string.invalid_node_address, Toast.LENGTH_SHORT);
                            txSendingTipDialog.dismiss();
                            nodeListener.failed();
                        }
                    } catch (Exception e) {
                        ToastUtil.showToast(mActivity, R.string.invalid_node_address, Toast.LENGTH_SHORT);
                        txSendingTipDialog.dismiss();
                        nodeListener.failed();
                    }


                }
            });


        } catch (Exception e) {
            ToastUtil.showToast(mActivity, R.string.invalid_node_address, Toast.LENGTH_SHORT);
            txSendingTipDialog.dismiss();
            nodeListener.failed();
        }


    }

    /**
     * save node  data
     *
     * @param oldPosition selection node
     */
    public void saveNodeData(int oldPosition) {
        List<NodeSettingModel> data = getData();
        for (int i = 0; i < data.size(); i++) {
            if (oldPosition == i) {
                data.get(i).setSelected(true);
            } else {
                data.get(i).setSelected(false);
            }
        }
        saveNodeData(data);
    }


    public void saveNodeData(List<NodeSettingModel> data) {
        String json = new Gson().toJson(data);
        SharedPreferencesHelper spHelper = new SharedPreferencesHelper(context, ConstantsType.BU_POCKET);
        spHelper.put(Constants.BUMO_NODE_URL, json);
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).isSelected()) {
                spHelper.put(Constants.BUMO_NODE_URL + "nodeUrl", data.get(i).getUrl());
            }
        }
    }


    public interface NodeAddressListener {

        void success(String url);

        void failed();
    }

}
