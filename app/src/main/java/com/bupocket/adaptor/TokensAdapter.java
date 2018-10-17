package com.bupocket.adaptor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.bupocket.R;
import com.bupocket.http.api.dto.resp.GetTokensRespDto;
import com.bupocket.utils.CommonUtil;

import java.util.List;

public class TokensAdapter extends BaseAdapter {
    private List<GetTokensRespDto.TokenListBean> datas;
    private Context mContext;
    String mPrefixTokenAmount = "≈￥";

    public TokensAdapter(List<GetTokensRespDto.TokenListBean> datas, Context mContext) {
        this.datas = datas;
        this.mContext = mContext;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView == null){
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.my_token_item_layout, null);
            holder.assetCodeTv = convertView.findViewById(R.id.assetCodeTv);
            holder.amountTv = convertView.findViewById(R.id.amountTv);
            holder.assetIconIv = convertView.findViewById(R.id.assetIconIv);
            holder.assetAmount = convertView.findViewById(R.id.assetAmountTv);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.assetCodeTv.setText(datas.get(position).getAssetCode());
        if (datas.get(position).getAssetAmount() == null){
            holder.assetAmount.setText("~");
        }else{
            if(!datas.get(position).getAssetAmount().equals("~")){
                holder.assetAmount.setText(mPrefixTokenAmount + CommonUtil.formatDouble(datas.get(position).getAssetAmount()));
            }else{
                holder.assetAmount.setText(datas.get(position).getAssetAmount());
            }
        }
        holder.amountTv.setText(datas.get(position).getAmount());
//        System.out.println("TokensAdapter.getView.assetCode: " + datas.get(position).getAssetCode());

        if(CommonUtil.isNull(datas.get(position).getIcon())){
            holder.assetIconIv.setBackgroundResource(R.mipmap.icon_token_default_icon);
        }else{
            try{
                holder.assetIconIv.setImageBitmap(CommonUtil.base64ToBitmap(datas.get(position).getIcon()));
            }catch (Exception e){
                holder.assetIconIv.setBackgroundResource(R.mipmap.icon_token_default_icon);
            }
        }
        return convertView;
    }

    class ViewHolder{
        private TextView assetCodeTv;
        private ImageView assetIconIv;
        private TextView amountTv;
        private TextView assetAmount;
    }
}
