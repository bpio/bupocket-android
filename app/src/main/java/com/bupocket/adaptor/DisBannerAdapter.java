package com.bupocket.adaptor;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bupocket.R;
import com.bupocket.common.Constants;
import com.bupocket.fragment.discover.BPBannerFragment;
import com.bupocket.model.SlideModel;
import com.qmuiteam.qmui.arch.QMUIFragment;

import java.util.List;

public class DisBannerAdapter extends PagerAdapter {


    private final Context mContext;
    private final QMUIFragment fragment;
    private List<SlideModel.ImageInfo> images;
    private ViewPager viewPager;
    private ImageView ivSlide;

    public void setData(List<SlideModel.ImageInfo> images){
        if (images==null) {
            return;
        }
        this.images=images;
    }

    public DisBannerAdapter(QMUIFragment fragment,List<SlideModel.ImageInfo> images, ViewPager viewPager){
        this.fragment = fragment;
        this.images  = images;
        this.viewPager = viewPager;
        mContext = viewPager.getContext();
    }


    @Override
    public int getCount() {
        return Integer.MAX_VALUE;//返回一个无限大的值，可以 无限循环
    }


    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }


    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        // 把position对应位置的ImageView添加到ViewPager中
        View inflate = LayoutInflater.from(this.viewPager.getContext()).inflate(R.layout.view_discover_slide, null);
        if (images.size()!=0) {
            final int index = position % images.size();
            SlideModel.ImageInfo info = images.get(index);
            ivSlide = inflate.findViewById(R.id.ivSlide);
            ivSlide.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String url = images.get(index).getUrl();
                    BPBannerFragment fragment = new BPBannerFragment();
                    Bundle args = new Bundle();
                    args.putString("url",url);
                    fragment.setArguments(args);
                    DisBannerAdapter.this.fragment.startFragmentForResult(fragment,1001);

                }
            });
            Glide.with(mContext)
                    .load(info.getImageUrl())
                    .into(ivSlide);

            viewPager.addView(inflate);
        }


        // 把当前添加ImageView返回回去.
        return inflate;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        // 把ImageView从ViewPager中移除掉
//        viewPager.removeView(images.get(position % images.size()));

    }
}
