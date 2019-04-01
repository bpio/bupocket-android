package com.bupocket.adaptor;

import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

public class DisBannerAdapter extends PagerAdapter {



    private List<ImageView> images;
    private ViewPager viewPager;


    public DisBannerAdapter(List<ImageView> images, ViewPager viewPager){
        this.images  = images;
        this.viewPager = viewPager;
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
        ImageView iv = images.get(position % images.size());
        viewPager.addView(iv);
        // 把当前添加ImageView返回回去.
        return iv;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        // 把ImageView从ViewPager中移除掉
        viewPager.removeView(images.get(position % images.size()));

    }
}
