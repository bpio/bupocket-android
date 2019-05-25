package com.bupocket.adaptor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.bupocket.R;

import java.util.List;

public class TxDetailSignatureAdapter extends BaseAdapter {
    private List<Signature> signatures;
    private Context mContext;

    public TxDetailSignatureAdapter(List<Signature> signatures, Context mContext) {
        this.signatures = signatures;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return signatures.size();
    }

    @Override
    public Object getItem(int position) {
        return signatures.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView == null){
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.view_tx_detail_signature_item, null);
            holder.mTxDetailTxInfoTxSignaturePkTv = convertView.findViewById(R.id.txDetailTxInfoTxSignaturePkTv);
            holder.mTxDetailTxInfoTxSignatureSdTv = convertView.findViewById(R.id.txDetailTxInfoTxSignatureSdTv);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.mTxDetailTxInfoTxSignaturePkTv.setText(signatures.get(i).getPublicKey());
        holder.mTxDetailTxInfoTxSignatureSdTv.setText(signatures.get(i).getSignData());
        return convertView;
    }

    class ViewHolder {
        private TextView mTxDetailTxInfoTxSignaturePkTv;
        private TextView mTxDetailTxInfoTxSignatureSdTv;
    }

    public static class Signature {
        private String publicKey;
        private String signData;

        public String getPublicKey() {
            return publicKey;
        }

        public void setPublicKey(String publicKey) {
            this.publicKey = publicKey;
        }

        public String getSignData() {
            return signData;
        }

        public void setSignData(String signData) {
            this.signData = signData;
        }
    }
}
