package com.bupocket.adaptor;

import android.content.Context;
import android.database.DataSetObservable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bupocket.R;
import com.bupocket.http.api.AddressBookListBean;
import com.bupocket.http.api.dto.resp.GetAddressBookRespDto;
import com.bupocket.utils.AddressUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddressAdapter extends BaseAdapter {
    private Map<String, AddressBookListBean> addressMap = new HashMap<>();
    private List<AddressBookListBean> mData;
    private GetAddressBookRespDto.PageBean page;
    private Context mContext;

    public GetAddressBookRespDto.PageBean getPage() {
        return page;
    }

    public void setPage(GetAddressBookRespDto.PageBean page) {
        this.page = page;
    }
    private final DataSetObservable mDataSetObservable = new DataSetObservable();

    public AddressAdapter(List<AddressBookListBean> data,Context Context){
        mData = data;
        mContext = Context;
    }

    public void loadMore(List<AddressBookListBean> data){
        for(AddressBookListBean addressBookListBean : data){
            if(addressMap.containsKey(addressBookListBean.getLinkmanAddress())){
                addressMap.put(addressBookListBean.getLinkmanAddress(),addressBookListBean);
                mData.add(addressBookListBean);
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        AddressAdapter.ViewHolder holder;
        if(convertView == null){
            holder = new AddressAdapter.ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.view_address_list_item, null);
            holder.addressNameTv = convertView.findViewById(R.id.addressNameTv);
            holder.addressTv = convertView.findViewById(R.id.addressTv);
            holder.addressDescribeTv = convertView.findViewById(R.id.addressDescribeTv);
            convertView.setTag(holder);
        } else {
            holder = (AddressAdapter.ViewHolder) convertView.getTag();
        }

        if(mData.size() != 0){
            holder.addressNameTv.setText(mData.get(position).getNickName());
            holder.addressTv.setText(AddressUtil.anonymous(mData.get(position).getLinkmanAddress()));
            holder.addressDescribeTv.setText(mData.get(position).getRemark());
            if(mData.get(position).getRemark().length() == 0){
                holder.addressDescribeTv.setVisibility(View.GONE);
            }
        }

        return convertView;
    }

    class ViewHolder {
        private TextView addressNameTv;
        private TextView addressTv;
        private TextView addressDescribeTv;
    }
}
