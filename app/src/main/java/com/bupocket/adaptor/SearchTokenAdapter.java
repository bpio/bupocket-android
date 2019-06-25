package com.bupocket.adaptor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.bupocket.R;
import com.bupocket.common.Constants;
import com.bupocket.enums.BumoNodeEnum;
import com.bupocket.http.api.dto.resp.GetTokensRespDto;
import com.bupocket.http.api.dto.resp.SearchTokenRespDto;
import com.bupocket.utils.CommonUtil;
import com.bupocket.utils.SharedPreferencesHelper;
import com.qmuiteam.qmui.widget.QMUIRadiusImageView;

import java.util.ArrayList;
import java.util.List;

import static com.bupocket.BPApplication.getContext;

public class SearchTokenAdapter extends BaseAdapter {
    private List<SearchTokenRespDto.TokenListBean> datas;
    private Context mContext;
    protected SharedPreferencesHelper sharedPreferencesHelper;
    private GetTokensRespDto getTokensRespDto = new GetTokensRespDto();
    private List<GetTokensRespDto.TokenListBean> tokenList = new ArrayList<>();

    public SearchTokenAdapter(List<SearchTokenRespDto.TokenListBean> datas, Context mContext) {
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
    public View getView(int i, View convertView, ViewGroup parent) {

        sharedPreferencesHelper = new SharedPreferencesHelper(getContext(), "buPocket");
        Integer bumoNodeType = sharedPreferencesHelper.getInt("bumoNode",Constants.DEFAULT_BUMO_NODE);
        final String localTokenListSharedPreferenceKey;
        if(BumoNodeEnum.TEST.getCode() == bumoNodeType){
            localTokenListSharedPreferenceKey = BumoNodeEnum.TEST.getLocalTokenListSharedPreferenceKey();
        }else {
            localTokenListSharedPreferenceKey = BumoNodeEnum.MAIN.getLocalTokenListSharedPreferenceKey();
        }
        getTokensRespDto = JSON.parseObject(sharedPreferencesHelper.getSharedPreference(localTokenListSharedPreferenceKey, "").toString(), GetTokensRespDto.class);
        if(getTokensRespDto != null) {
            tokenList = getTokensRespDto.getTokenList();
        }
        final SearchTokenAdapter.ViewHolder holder;
        /*if(convertView == null){
            holder = new SearchTokenAdapter.ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.view_search_token_result_item, null);
            holder.assetIconIv = convertView.findViewById(R.id.assetIconIv);
            holder.assetCodeTv = convertView.findViewById(R.id.assetCodeTv);
            holder.assetNameTv = convertView.findViewById(R.id.assetNameTv);
            holder.assetIssuerTv = convertView.findViewById(R.id.assetIssuerTv);
            holder.tokenOptAddBtn = convertView.findViewById(R.id.tokenOptAddBtn);
            holder.tokenOptCancelCBtn = convertView.findViewById(R.id.tokenOptCancelCBtn);
            convertView.setTag(holder);
        } else {
            holder = (SearchTokenAdapter.ViewHolder) convertView.getTag();
        }*/

        holder = new SearchTokenAdapter.ViewHolder();
        convertView = LayoutInflater.from(mContext).inflate(R.layout.view_search_token_result_item, null);
        holder.assetIconIv = convertView.findViewById(R.id.headIconIv);
        holder.assetCodeTv = convertView.findViewById(R.id.assetCodeTv);
        holder.assetNameTv = convertView.findViewById(R.id.assetNameTv);
        holder.assetIssuerTv = convertView.findViewById(R.id.assetIssuerTv);
        holder.tokenOptAddBtn = convertView.findViewById(R.id.tokenOptAddBtn);
        holder.tokenOptCancelCBtn = convertView.findViewById(R.id.tokenOptCancelCBtn);
        convertView.setTag(holder);

        if(datas.size() != 0){
            final String assetCode = datas.get(i).getAssetCode();
            final String issuer = datas.get(i).getIssuer();
            holder.assetCodeTv.setText(assetCode);
            holder.assetNameTv.setText(datas.get(i).getAssetName());
            holder.assetIssuerTv.setText(issuer);
            if(CommonUtil.isNull(datas.get(i).getIcon())){
                holder.assetIconIv.setBackgroundResource(R.mipmap.icon_token_default_icon);
            }else{
                try{
                    holder.assetIconIv.setImageBitmap(CommonUtil.base64ToBitmap(datas.get(i).getIcon()));
                }catch (Exception e){
                    holder.assetIconIv.setBackgroundResource(R.mipmap.icon_token_default_icon);
                }
            }
            String recommend = datas.get(i).getRecommend() + "";
            if("0".equals(recommend)){
                holder.tokenOptAddBtn.setVisibility(View.GONE);
            }else{
                final GetTokensRespDto.TokenListBean tokenListBean = new GetTokensRespDto.TokenListBean();
                tokenListBean.setAssetCode(assetCode);
                tokenListBean.setIssuer(issuer);

                if(tokenList.size() != 0){
                    for(int a = 0;a < tokenList.size(); a++){
                        if(tokenList.get(a).getAssetCode().equals(assetCode) && tokenList.get(a).getIssuer().equals(issuer)){
                            holder.tokenOptAddBtn.setVisibility(View.GONE);
                            holder.tokenOptCancelCBtn.setVisibility(View.VISIBLE);
                        }
                    }
                }


                holder.tokenOptAddBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        tokenList.add(tokenListBean);
                        getTokensRespDto = new GetTokensRespDto();
                        getTokensRespDto.setTokenList(tokenList);
                        sharedPreferencesHelper.put(localTokenListSharedPreferenceKey,JSON.toJSONString(getTokensRespDto));
                        holder.tokenOptAddBtn.setVisibility(View.GONE);
                        holder.tokenOptCancelCBtn.setVisibility(View.VISIBLE);
                    }
                });
                holder.tokenOptCancelCBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        for(int i = 0;i<tokenList.size();i++){
                            GetTokensRespDto.TokenListBean tokenListBean = tokenList.get(i);
                            if(tokenListBean.getIssuer().equals(issuer)&&tokenListBean.getAssetCode().equals(assetCode)){
                                tokenList.remove(i);
                                getTokensRespDto = new GetTokensRespDto();
                                getTokensRespDto.setTokenList(tokenList);
                                sharedPreferencesHelper.put(localTokenListSharedPreferenceKey,JSON.toJSONString(getTokensRespDto));
                            }

                        }
                        holder.tokenOptAddBtn.setVisibility(View.VISIBLE);
                        holder.tokenOptCancelCBtn.setVisibility(View.GONE);
                    }
                });
            }
        }

        return convertView;
    }
    class ViewHolder {
        private QMUIRadiusImageView assetIconIv;
        private TextView assetCodeTv;
        private TextView assetNameTv;
        private TextView assetIssuerTv;
        private ImageView tokenOptAddBtn;
        private ImageView tokenOptCancelCBtn;
    }
}
