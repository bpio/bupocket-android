package com.bupocket.adaptor;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bupocket.R;
import com.bupocket.model.MnemonicCodeInfo;

import java.util.List;

public class MyMnemonicCodeAdapter extends BaseAdapter {
    private List<MnemonicCodeInfo> datas;
    private Context mContext;

    public MyMnemonicCodeAdapter(List<MnemonicCodeInfo> datas, Context mContext) {
        this.datas = datas;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return 0;
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
    public View getView(int i, View convertView, ViewGroup parent) {
        MyMnemonicCodeAdapter.ViewHolder holder;
        if(convertView == null){
            holder = new MyMnemonicCodeAdapter.ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.view_mnemonic_code_list_item, null);
            holder.codeTV = convertView.findViewById(R.id.mnemonic_code);
            convertView.setTag(holder);
        } else {
            holder = (MyMnemonicCodeAdapter.ViewHolder) convertView.getTag();
        }

        holder.codeTV.setText(datas.get(i).getCode());
        return convertView;
    }

    class ViewHolder {
        private TextView codeTV;
    }

    @Nullable
    @Override
    public CharSequence[] getAutofillOptions() {
        return new CharSequence[0];
    }
}
