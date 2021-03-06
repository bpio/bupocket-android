package com.bupocket.fragment.discover;

import android.content.Intent;
import android.os.SystemClock;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.bupocket.R;
import com.bupocket.activity.BumoNewsActivity;
import com.bupocket.adaptor.DisBannerAdapter;
import com.bupocket.base.BaseFragment;
import com.bupocket.common.Constants;
import com.bupocket.database.greendao.ImageInfoDao;
import com.bupocket.fragment.home.HomeFragment;
import com.bupocket.http.api.DiscoverService;
import com.bupocket.http.api.RetrofitFactory;
import com.bupocket.http.api.dto.resp.ApiResult;
import com.bupocket.model.ImageInfo;
import com.bupocket.model.SlideModel;
import com.bupocket.utils.CommonUtil;
import com.bupocket.utils.DialogUtils;
import com.bupocket.utils.LogUtils;
import com.bupocket.utils.NetworkUtils;
import com.bupocket.utils.ThreadManager;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.bupocket.common.Constants.WeChat_APPID;
import static com.bupocket.common.Constants.XB_YOUPING_USERNAME;

public class BPDiscoverHomeFragment extends BaseFragment {


    @BindView(R.id.nodePlanLl)
    LinearLayout mNodePlanLl;
    @BindView(R.id.nodeBuildLl)
    LinearLayout mNodeBuildRl;
    @BindView(R.id.informationLl)
    LinearLayout mInformationLl;
    @BindView(R.id.xiaobuYoupinLl)
    LinearLayout mXiaobuYoupinLl;
    @BindView(R.id.vpDisBanner)
    ViewPager vpDisBanner;
    @BindView(R.id.topbar)
    QMUITopBarLayout topBar;

    private ArrayList<ImageInfo> banListData;
    private DisBannerAdapter disBannerAdapter;
    private long PAGER_TIME = 4 * 1000;
    private boolean isStop;
    private boolean isDownStop;
    private Unbinder bind;
    private ArrayList<ImageInfo> slideshow;

    private static Runnable bannerRunnable;
    private ImageInfoDao imageInfoDao;


    @Override
    protected View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_discover, null);
        bind = ButterKnife.bind(this, root);
        init();
        return root;
    }

    @Override
    protected boolean canDragBack() {
        return false;
    }

    private void init() {
        initUI();
        initData();
        setListener();
        autoPlayView();
    }

    private void initData() {
        imageInfoDao = mApplication.getDaoSession().getImageInfoDao();

        banListData = new ArrayList<>();
        List<ImageInfo> imageInfos = imageInfoDao.loadAll();
        if (imageInfos!=null&&imageInfos.size()>0) {
            banListData.addAll(imageInfos);
        }
        disBannerAdapter = new DisBannerAdapter(this, banListData, vpDisBanner);
        vpDisBanner.setAdapter(disBannerAdapter);
        String youpin = (String) spHelper.getSharedPreference("youpin", "");
        if (TextUtils.isEmpty(youpin)) {
            spHelper.put("youpin", "0");
        }


        requestData();

    }


    private void requestData() {

        if (!NetworkUtils.isNetWorkAvailable(mContext)) {
            return;
        }

        DiscoverService discoverService = RetrofitFactory.getInstance().getRetrofit().create(DiscoverService.class);

        HashMap<String, Object> map = new HashMap<>();
        map.put("target", 1 + "");
        discoverService.slideShowNew(map).enqueue(new Callback<ApiResult<SlideModel>>() {
            @Override
            public void onResponse(Call<ApiResult<SlideModel>> call, Response<ApiResult<SlideModel>> response) {
                ApiResult<SlideModel> body = response.body();
                if (body == null) {
                    vpDisBanner.setVisibility(View.GONE);
                    return;
                }

                SlideModel imageList = body.getData();
                if (imageList != null && imageList.getSlideshow() != null && imageList.getSlideshow().size() > 0) {
                    slideshow = imageList.getSlideshow();
                    disBannerAdapter.setData(slideshow);
                    vpDisBanner.setCurrentItem(vpDisBanner.getCurrentItem() + 100);
                    vpDisBanner.setVisibility(View.VISIBLE);

                    imageInfoDao.deleteAll();
                    imageInfoDao.insertInTx(slideshow);

                } else {

                    if (disBannerAdapter.getCount()>0) {
                        return;
                    }

                    vpDisBanner.setVisibility(View.GONE);
                }


            }

            @Override
            public void onFailure(Call<ApiResult<SlideModel>> call, Throwable t) {
                vpDisBanner.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        isDownStop = false;

        if (slideshow == null || slideshow.size() == 0) {
            requestData();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        isDownStop = true;
    }

    private void autoPlayView() {

        if (bannerRunnable!=null) {
            return;
        }
        bannerRunnable = new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if (!isDownStop) {

                        try {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (vpDisBanner != null) {
                                        vpDisBanner.setCurrentItem(vpDisBanner.getCurrentItem() + 1);
                                        disBannerAdapter.notifyDataSetChanged();
                                    }
                                }

                            });
                        } catch (Exception e) {

                        }

                    }
                    SystemClock.sleep(PAGER_TIME);

                }
            }
        };

        ThreadManager.getInstance().execute(bannerRunnable);
    }

    private void initUI() {
        QMUIStatusBarHelper.setStatusBarLightMode(getBaseFragmentActivity());
        topBar.setTitle(R.string.tabbar_discover_txt);
        topBar.setBackgroundDividerEnabled(false);
    }

    private void setListener() {
        mNodePlanLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startFragment(new BPNodeCampaignFragment());
            }
        });

        mNodeBuildRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startFragment(new BPNodeBuildFragment());
            }
        });

        // open https://m-news.bumo.io/
        mInformationLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getContext(), BumoNewsActivity.class);
                startActivity(intent);
            }
        });

        // open weixin mini program
        mXiaobuYoupinLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String youpin = (String) spHelper.getSharedPreference("youpin", "0");
                if (youpin.equals("1")) {
                    CommonUtil.goWeChat(mContext, WeChat_APPID, XB_YOUPING_USERNAME);
                } else {

                    DialogUtils.showMessageDialog(mContext, getString(R.string.open_youpin), getString(R.string.open_youpin_title), new DialogUtils.KnowListener() {
                        @Override
                        public void Know() {
                            spHelper.put("youpin", "1");
                            CommonUtil.goWeChat(mContext, WeChat_APPID, XB_YOUPING_USERNAME);
                        }
                    });
                }
            }
        });

    }
}
