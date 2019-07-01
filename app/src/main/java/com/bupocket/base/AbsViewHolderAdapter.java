package com.bupocket.base;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

/**
 *base  AbsViewHolderAdapter
 */
public abstract class AbsViewHolderAdapter<ItemDataType> extends AbsBaseAdapter<ItemDataType> {
    protected abstract int getLayoutId();

    public AbsViewHolderAdapter(@NonNull Context context) {
        super(context);
    }

    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        ItemDataType itemData = getItem(position);
        BaseViewHolder viewHolder = BaseViewHolder.get(convertView, parent, getLayoutId(), position);
        convert(viewHolder, itemData);
        return viewHolder.getConvertView();
    }

    protected abstract void convert(BaseViewHolder holder, ItemDataType itemData);
}
