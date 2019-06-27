package com.bupocket.adaptor;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bupocket.R;
import com.bupocket.base.AbsViewHolderAdapter;
import com.bupocket.base.BaseViewHolder;
import com.bupocket.model.HeadIconModel;

import java.util.List;

public class HeadAdapter extends AbsViewHolderAdapter<HeadIconModel> {



    public HeadAdapter(@NonNull Context context) {
        super(context);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_head_icon_item;
    }

    @Override
    protected void convert(BaseViewHolder holder, HeadIconModel itemData) {


//        if (itemData.getSelectedPosition()) {
//            holder.headSelectRIV.setVisibility(View.VISIBLE);
//        } else {
//            holder.headSelectRIV.setVisibility(View.INVISIBLE);
//        }

//        holder.headIconRV.setImageDrawable(mContext.getResources().getDrawable(data.get(position).getIconRes()));
//        holder.headIconRV.setImageResource(data.get(position).getIconRes());


    }
}
