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
import java.util.Set;

public class HeadIconAdapter extends RecyclerView.Adapter<HeadIconAdapter.VH> {


    private List<HeadIconModel> data;
    private final Context mContext;
    private OnItemClickListener onItemClickListener;

    public HeadIconAdapter(Context mContext, List<HeadIconModel> data) {
        this.mContext = mContext;
        this.data = data;
    }


    public void setData( List<HeadIconModel> data){
        this.data=data;
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_head_icon_item, parent, false);
        return new VH(v);

    }

    @Override
    public void onBindViewHolder(@NonNull final VH holder, int position) {


        if (position == data.get(position).getSelectedPosition()) {
            holder.headSelectRIV.setVisibility(View.VISIBLE);
        } else {
            holder.headSelectRIV.setVisibility(View.INVISIBLE);
        }
        holder.headIconRV.setImageResource(data.get(position).getIconRes());

     holder.parentView.setOnClickListener(new View.OnClickListener() {
    @Override
      public void onClick(View v) {

        onItemClickListener.onItemClick(holder.itemView,holder.getLayoutPosition());

    }
});
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    //② 创建ViewHolder
    public static class VH extends RecyclerView.ViewHolder {
        public final ImageView headSelectRIV;
        public final ImageView headIconRV;
        public final View parentView;
        public VH(View v) {
            super(v);
            parentView=v;
            headSelectRIV = (ImageView) v.findViewById(R.id.headSelectedRIV);
            headIconRV = (ImageView) v.findViewById(R.id.headIconIV);
        }
    }



    public interface OnItemClickListener {
        void onItemClick(View v,int position);
    }


    public void setOnItemClickListener(OnItemClickListener listener){
        onItemClickListener= listener;
    }

}
