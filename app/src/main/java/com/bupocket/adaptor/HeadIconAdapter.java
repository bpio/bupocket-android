package com.bupocket.adaptor;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bupocket.R;
import com.bupocket.model.HeadIconModel;

import java.util.List;

public class HeadIconAdapter extends RecyclerView.Adapter<HeadIconAdapter.VH> {


    private final List<HeadIconModel> data;
    private final Context mContext;

    public HeadIconAdapter(Context mContext, List<HeadIconModel> data) {
        this.mContext = mContext;
        this.data = data;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //LayoutInflater.from指定写法
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_head_icon_item, parent, false);
        return new VH(v);

    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {

        if (position == data.get(position).getSelectedPosition()) {
            holder.headSelectRIV.setVisibility(View.VISIBLE);
        } else {
            holder.headSelectRIV.setVisibility(View.INVISIBLE);
        }

//        holder.headIconRV.setImageDrawable(mContext.getResources().getDrawable(data.get(position).getIconRes()));
        holder.headIconRV.setImageResource(data.get(position).getIconRes());

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    //② 创建ViewHolder
    public static class VH extends RecyclerView.ViewHolder {
        public final ImageView headSelectRIV;
        public final ImageView headIconRV;

        public VH(View v) {
            super(v);
            headSelectRIV = (ImageView) v.findViewById(R.id.headSelectedRIV);
            headIconRV = (ImageView) v.findViewById(R.id.headIconIV);
        }
    }
}
