package com.bupocket.base;

import android.graphics.Bitmap;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.bupocket.utils.ImageUtils;



/**
 * edited by yanyi on 16/1/14.
 */
public class BaseViewHolder {
    private SparseArray<View> viewMap;
    private int position;
    private View convertView;
    private int type;

    public BaseViewHolder(@NonNull ViewGroup parent, int layoutId, int position) {
        this(parent, 0, layoutId, position);
    }

    private BaseViewHolder(@NonNull ViewGroup parent, int type, int layoutId, int position) {
        this.position = position;
        this.type = type;
        this.convertView = View.inflate(parent.getContext(), layoutId, null);
        viewMap = new SparseArray<>();
        this.convertView.setTag(this);
    }

    @NonNull
    public static BaseViewHolder get(View convertView, @NonNull ViewGroup parent, int layoutId, int position) {
        return get(convertView, parent, 0, layoutId, position);
    }

    @NonNull
    public static BaseViewHolder get(@Nullable View convertView, @NonNull ViewGroup parent, int type, int layoutId,
                                     int position) {
        if (convertView == null) {
            return new BaseViewHolder(parent, type, layoutId, position);
        } else {
            BaseViewHolder holder = (BaseViewHolder) convertView.getTag();
            if (holder.type != type) {
                return new BaseViewHolder(parent, type, layoutId, position);
            } else {
                holder.position = position;
                return holder;
            }
        }
    }

    @NonNull
    public <T extends View> T getView(int viewId) {
        View view = viewMap.get(viewId);
        if (view == null) {
            view = convertView.findViewById(viewId);
            viewMap.put(viewId, view);
        }
        return (T) view;
    }

    public int getPosition() {
        return position;
    }

    public View getConvertView() {
        return convertView;
    }

    @NonNull
    public BaseViewHolder setText(int viewId, String text) {
        TextView textView = getView(viewId);
        textView.setText(text);
        return this;
    }

    @NonNull
    public BaseViewHolder setImageResource(int viewId, int imageId) {
        ImageView imageView = getView(viewId);
        imageView.setImageResource(imageId);
        return this;
    }

    @NonNull
    public BaseViewHolder setImageBitmap(int viewId, Bitmap bitmap) {
        ImageView imageView = getView(viewId);
        imageView.setImageBitmap(bitmap);
        return this;
    }

    @NonNull
    public BaseViewHolder setBackgroundColor(int viewId, int color) {
        ImageView imageView = getView(viewId);
        imageView.setBackgroundColor(color);
        return this;
    }

    @NonNull
    public BaseViewHolder setVisible(int viewId, boolean show) {
        getView(viewId).setVisibility(show ? View.VISIBLE : View.GONE);
        return this;
    }

    @NonNull
    public BaseViewHolder setInVisible(int viewId, boolean show) {
        getView(viewId).setVisibility(show ? View.INVISIBLE : View.GONE);
        return this;
    }

    @NonNull
    public BaseViewHolder setOnclick(int viewId, View.OnClickListener listener) {
        getView(viewId).setOnClickListener(listener);
        return this;
    }

    @NonNull
    public BaseViewHolder setImageUrl(int viewId, String url) {
        ImageView imageView = getView(viewId);
        ImageUtils.showImage(imageView, url);
        return this;
    }

    @NonNull
    public BaseViewHolder setChecked(int viewId, boolean checked) {
        CheckBox checkBox = getView(viewId);
        checkBox.setChecked(checked);
        return this;
    }

    @NonNull
    public BaseViewHolder setBackgroundResource(int viewId, @DrawableRes int resId) {
        View view = getView(viewId);
        view.setBackgroundResource(resId);
        return this;
    }
}
