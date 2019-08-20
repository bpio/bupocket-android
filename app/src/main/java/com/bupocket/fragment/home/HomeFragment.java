package com.bupocket.fragment.home;

import android.annotation.SuppressLint;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.bupocket.R;
import com.bupocket.base.BaseFragment;
import com.bupocket.common.ConstantsType;
import com.bupocket.fragment.BPAssetsHomeFragment;
import com.bupocket.fragment.BPProfileHomeFragment;
import com.bupocket.fragment.discover.BPDiscoverHomeFragment;
import com.bupocket.utils.CommonUtil;
import com.bupocket.utils.DialogUtils;
import com.bupocket.utils.LogUtils;
import com.bupocket.voucher.BPVoucherHomeFragment;
import com.qmuiteam.qmui.widget.QMUIPagerAdapter;
import com.qmuiteam.qmui.widget.QMUITabSegment;

import butterknife.BindView;
import butterknife.ButterKnife;


public class HomeFragment extends BaseFragment {

    @BindView(R.id.pager)
    ViewPager mViewPager;
    @BindView(R.id.tabs)
    public QMUITabSegment mTabSegment;

    @Override
    protected View onCreateView() {
        View layout =  LayoutInflater.from(getActivity()).inflate(R.layout.fragment_home, null);
        ButterKnife.bind(this, layout);
        initTabs();
        initPagers();
        return layout;
    }


    @Override
    public void onResume() {
        super.onResume();

        if (getArguments()!=null) {
            if (BPVoucherHomeFragment.class.getSimpleName().equals(getArguments().getString(ConstantsType.FRAGMENT_TAG))) {
                mTabSegment.selectTab(1);
                getArguments().clear();
            }
        }

    }

    private void initTabs() {


        QMUITabSegment.Tab assets = new QMUITabSegment.Tab(
                ContextCompat.getDrawable(getContext(), R.mipmap.icon_tabbar_asset),
                ContextCompat.getDrawable(getContext(), R.mipmap.icon_tabbar_asset_selected),
                getResources().getString(R.string.tabbar_assets_txt), false
        );

        int voucher_normal = R.mipmap.icon_tabbar_voucher;
        int voucher_selected = R.mipmap.icon_tabbar_voucher_selected;

        if (spHelper.getSharedPreference(ConstantsType.FIRST_OPEN_VOUCHER, "yes").equals("yes")) {

            if (CommonUtil.isEnglishLangage()) {
                voucher_normal = R.mipmap.icon_tabbar_voucher_new_en;
            } else {
                voucher_normal = R.mipmap.icon_tabbar_voucher_new_cn;
            }
        }


        QMUITabSegment.Tab voucher = new QMUITabSegment.Tab(
                ContextCompat.getDrawable(getContext(), voucher_normal),
                ContextCompat.getDrawable(getContext(), voucher_selected),
                getResources().getString(R.string.tabbar_toucher_txt), false
        );
        QMUITabSegment.Tab discover = new QMUITabSegment.Tab(
                ContextCompat.getDrawable(getContext(), R.mipmap.icon_tabbar_discover),
                ContextCompat.getDrawable(getContext(), R.mipmap.icon_tabbar_discover_selected),
                getResources().getString(R.string.tabbar_discover_txt), false
        );
        QMUITabSegment.Tab profile = new QMUITabSegment.Tab(
                ContextCompat.getDrawable(getContext(), R.mipmap.icon_tabbar_profile),
                ContextCompat.getDrawable(getContext(), R.mipmap.icon_tabbar_profile_selected),
                getResources().getString(R.string.tabbar_profile_txt), false
        );

        mTabSegment.addTab(assets);
        mTabSegment.addTab(voucher);
        mTabSegment.addTab(discover);
        mTabSegment.addTab(profile);
        mTabSegment.setDefaultSelectedColor(getContext().getResources().getColor(R.color.app_color_green));

        mTabSegment.addOnTabSelectedListener(new QMUITabSegment.OnTabSelectedListener() {
            @Override
            public void onTabSelected(int index) {

                if (index == 1) {
                    if (spHelper.getSharedPreference(ConstantsType.FIRST_OPEN_VOUCHER, "yes").equals("yes")) {
                        spHelper.put(ConstantsType.FIRST_OPEN_VOUCHER, "no");
                        int voucher_normal = R.mipmap.icon_tabbar_voucher;
                        int voucher_selected = R.mipmap.icon_tabbar_voucher_selected;
                        QMUITabSegment.Tab voucher = new QMUITabSegment.Tab(
                                ContextCompat.getDrawable(getContext(), voucher_normal),
                                ContextCompat.getDrawable(getContext(), voucher_selected),
                                getResources().getString(R.string.tabbar_toucher_txt), false
                        );
                        mTabSegment.replaceTab(1,voucher);

                        DialogUtils.showMessageScrollDialog(mContext,
                                getString(R.string.number_voucher_package_info),
                                getString(R.string.number_voucher_package_title), new DialogUtils.KnowListener() {
                                    @Override
                                    public void Know() {

                                    }
                                });
                    }

                }
            }

            @Override
            public void onTabUnselected(int index) {

            }

            @Override
            public void onTabReselected(int index) {

            }

            @Override
            public void onDoubleTap(int index) {

            }
        });
    }


    private void initPagers() {
        QMUIPagerAdapter pagerAdapter = new QMUIPagerAdapter() {
            private FragmentTransaction mCurrentTransaction;
            private Fragment mCurrentPrimaryItem = null;

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == ((Fragment) object).getView();
            }

            @Override
            public int getCount() {
                return 4;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                switch (position) {
                    case 0:
                        return getResources().getString(R.string.tabbar_assets_txt);
                    case 1:
                        return getResources().getString(R.string.tabbar_toucher_txt);
                    case 2:
                        return getResources().getString(R.string.tabbar_discover_txt);
                    case 3:
                    default:
                        return getResources().getString(R.string.tabbar_profile_txt);
                }
            }

            @SuppressLint("CommitTransaction")
            @Override
            protected Object hydrate(ViewGroup container, int position) {
                String name = makeFragmentName(container.getId(), position);
                if (mCurrentTransaction == null) {
                    mCurrentTransaction = getChildFragmentManager()
                            .beginTransaction();
                }
                Fragment fragment = getChildFragmentManager().findFragmentByTag(name);
                if (fragment != null) {
                    return fragment;
                }
                switch (position) {
                    case 0:
                        return new BPAssetsHomeFragment();
                    case 1:
                        return new BPVoucherHomeFragment();
                    case 2:
                        return new BPDiscoverHomeFragment();
                    case 3:
                    default:
                        return new BPProfileHomeFragment();
                }
            }

            @SuppressLint("CommitTransaction")
            @Override
            protected void populate(ViewGroup container, Object item, int position) {
                String name = makeFragmentName(container.getId(), position);
                if (mCurrentTransaction == null) {
                    mCurrentTransaction = getChildFragmentManager()
                            .beginTransaction();
                }
                Fragment fragment = getChildFragmentManager().findFragmentByTag(name);
                if (fragment != null) {
                    mCurrentTransaction.attach(fragment);
                    if (fragment.getView() != null && fragment.getView().getWidth() == 0) {
                        fragment.getView().requestLayout();
                    }
                } else {
                    fragment = (Fragment) item;
                    mCurrentTransaction.add(container.getId(), fragment, name);
                }
                if (fragment != mCurrentPrimaryItem) {
                    fragment.setMenuVisibility(false);
                    fragment.setUserVisibleHint(false);
                }


            }

            @SuppressLint("CommitTransaction")
            @Override
            protected void destroy(ViewGroup container, int position, Object object) {
                if (mCurrentTransaction == null) {
                    mCurrentTransaction = getChildFragmentManager()
                            .beginTransaction();
                }
                mCurrentTransaction.detach((Fragment) object);
            }

            @Override
            public void startUpdate(ViewGroup container) {
                if (container.getId() == View.NO_ID) {
                    throw new IllegalStateException("ViewPager with adapter " + this
                            + " requires a view id");
                }
            }

            @Override
            public void finishUpdate(ViewGroup container) {
                if (mCurrentTransaction != null) {
                    mCurrentTransaction.commitNowAllowingStateLoss();
                    mCurrentTransaction = null;
                }
            }

            @Override
            public void setPrimaryItem(ViewGroup container, int position, Object object) {
                Fragment fragment = (Fragment) object;
                if (fragment != mCurrentPrimaryItem) {
                    if (mCurrentPrimaryItem != null) {
                        mCurrentPrimaryItem.setMenuVisibility(false);
                        mCurrentPrimaryItem.setUserVisibleHint(false);
                    }
                    if (fragment != null) {
                        fragment.setMenuVisibility(true);
                        fragment.setUserVisibleHint(true);
                    }
                    mCurrentPrimaryItem = fragment;
                }
            }

            private String makeFragmentName(int viewId, long id) {
                return "HomeFragment:" + viewId + ":" + id;
            }
        };
        mViewPager.setAdapter(pagerAdapter);
        mTabSegment.setupWithViewPager(mViewPager, false, true);
    }

    @Override
    protected boolean canDragBack() {
        return false;
    }

    private long exitTime = 0;

    public void onBackPressed() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(getContext(), getResources().getText(R.string.next_key_down_err), Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            getActivity().finish();
        }

    }
}
