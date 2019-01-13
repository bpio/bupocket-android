package com.bupocket.adaptor;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bupocket.R;
import com.bupocket.fragment.BPWalletManageFragment;
import com.bupocket.model.WalletInfo;
import com.bupocket.utils.AddressUtil;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;

import java.util.List;

public class ImportWalletAdapter extends BaseAdapter {
    private List<WalletInfo> datas;
    private Context mContext;
    private String mCurrentWalletAddress;

    public ImportWalletAdapter(List<WalletInfo> datas, Context mContext, String mCurrentWalletAddress){
        this.datas = datas;
        this.mContext = mContext;
        this.mCurrentWalletAddress = mCurrentWalletAddress;
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Object getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView == null){
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.view_wallet_list_item, null);
            holder.walletNameTv = convertView.findViewById(R.id.walletNameTv);
            holder.walletAddressTv = convertView.findViewById(R.id.walletAddressTv);
            holder.walletSignTv = convertView.findViewById(R.id.walletSignTv);
            holder.manageWalletBtn = convertView.findViewById(R.id.manageWalletBtn);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if(datas.size() != 0){
            final String address = datas.get(position).getWalletAddress();
            holder.walletNameTv.setText(datas.get(position).getWalletName());
            holder.walletAddressTv.setText(AddressUtil.anonymous(address));
            if(mCurrentWalletAddress.equals(address)){
                holder.walletSignTv.setVisibility(View.VISIBLE);
            }
            holder.manageWalletBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onManageWalletBtnListener.onClick(position);
                }
            });
        }
        return convertView;

    }

    public interface OnManageWalletBtnListener {
        void onClick(int i);
    }
    private OnManageWalletBtnListener onManageWalletBtnListener;

    public void setOnManageWalletBtnListener(OnManageWalletBtnListener onManageWalletBtnListener) {
        this.onManageWalletBtnListener = onManageWalletBtnListener;
    }

    class ViewHolder {
        private TextView walletNameTv;
        private TextView walletAddressTv;
        private TextView walletSignTv;
        private QMUIRoundButton manageWalletBtn;
    }
}
