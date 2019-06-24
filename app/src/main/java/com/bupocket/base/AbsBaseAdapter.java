package com.bupocket.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * base  adapter
 */
public abstract class AbsBaseAdapter<T> extends BaseAdapter implements BaseAdapterDataListener<T> {

    List<T> data;

    public Context context;

    private LayoutInflater inflater;

    private Bundle bundle;

    public AbsBaseAdapter(@NonNull Context context) {
        this.data = new ArrayList<>();
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }


    //加到list后面
    void addToEnd(@Nullable List<T> datas) {
        if (datas != null && datas.size() > 0) {
            data.addAll(datas);
            notifyDataSetChanged();
        }
    }

    //加到list前面面
    public void addToFront(@Nullable List<T> datas) {
        if (datas != null && datas.size() > 0) {
            data.addAll(0, datas);
            notifyDataSetChanged();
        }
    }

    public Bundle getBundle() {
        return bundle;
    }

    public void setBundle(Bundle bundle) {
        this.bundle = bundle;
    }

    @Override
    public int getCount() {
        if (data == null)
            return 0;
        else
            return data.size();

    }


    @Nullable
    @Override
    public T getItem(int position) {
        if (data == null || data.size() == 0)
            return null;
        else
            return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public List<T> getData() {
        return data;
    }

    void setData(@NonNull List<T> data) {
        this.data = data;
    }

    /**
     * use setNewData
     */
    @Deprecated
    public void updateData(@NonNull List<T> data) {
        setNewData(data);
    }


    public void clear() {
        data.clear();
        notifyDataSetChanged();
    }

    /**
     * use setNewData
     */
    @Deprecated
    public void updateList(@NonNull List<T> data) {
        setNewData(data);
    }

    @Override
    public void setNewData(@NonNull List<T> newData) {
        setData(newData);
        notifyDataSetChanged();
    }

    @Override
    public void addMoreDataList(@NonNull List<T> moreData) {
        data.addAll(moreData);
        notifyDataSetChanged();
    }
}
