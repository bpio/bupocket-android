package com.bupocket.fragment.discover;

import android.content.Intent;
import android.os.SystemClock;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bupocket.R;
import com.bupocket.activity.BumoNewsActivity;
import com.bupocket.adaptor.DisBannerAdapter;
import com.bupocket.base.BaseFragment;
import com.bupocket.http.api.DiscoverService;
import com.bupocket.http.api.RetrofitFactory;
import com.bupocket.http.api.dto.resp.ApiResult;
import com.bupocket.model.SlideModel;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.tencent.mm.opensdk.modelbiz.WXLaunchMiniProgram;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BPDiscoverHomeFragment extends BaseFragment {

    private static final String WeChat_APPID = "wxaecf7ac4085fd34a";
    private static final String XB_YOUPING_USERNAME = "gh_463781563a74";

    @BindView(R.id.cardPackageRl)
    RelativeLayout mCardPackageRl;
    @BindView(R.id.nodePlanRl)
    RelativeLayout mNodePlanRl;
    @BindView(R.id.nodeBuildRl)
    RelativeLayout mNodeBuildRl;
    @BindView(R.id.informationRl)
    RelativeLayout mInformationRl;
    @BindView(R.id.xiaobuYoupinRl)
    RelativeLayout mXiaobuYoupinRl;

    @BindView(R.id.vpDisBanner)
    ViewPager vpDisBanner;
    private ArrayList<SlideModel.ImageInfo> banListData;
    private DisBannerAdapter disBannerAdapter;
    private long PAGER_TIME = 3 * 1000;
    private boolean isStop;
    private boolean isDownStop;
    private Unbinder bind;


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
        setListener();
        initData();
        autoPlayView();
    }

    private void initData() {
        banListData = new ArrayList<>();
        disBannerAdapter = new DisBannerAdapter(banListData, vpDisBanner);
        vpDisBanner.setAdapter(disBannerAdapter);

        DiscoverService discoverService = RetrofitFactory.getInstance().getRetrofit().create(DiscoverService.class);
        discoverService.slideShow().enqueue(new Callback<ApiResult<SlideModel>>() {
            @Override
            public void onResponse(Call<ApiResult<SlideModel>> call, Response<ApiResult<SlideModel>> response) {
                ApiResult<SlideModel> body = response.body();
                if (body == null) {
                    return;
                }

                SlideModel imageList = body.getData();
                if (imageList != null) {
                    disBannerAdapter.setData(imageList.getSlideshow());
                    disBannerAdapter.notifyDataSetChanged();
                }


            }

            @Override
            public void onFailure(Call<ApiResult<SlideModel>> call, Throwable t) {

            }
        });


    }

    @Override
    public void onResume() {
        super.onResume();
        isDownStop = false;
    }

    @Override
    public void onPause() {
        super.onPause();
        isDownStop = true;
    }

    private void autoPlayView() {
        //自动播放图片
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!isStop) {

                    if (!isDownStop) {

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (vpDisBanner != null) {
                                    vpDisBanner.setCurrentItem(vpDisBanner.getCurrentItem() + 1);
                                }
                            }
                        });
                        SystemClock.sleep(PAGER_TIME);
                    }
                }
            }
        }).start();
    }

    private void initUI() {
        QMUIStatusBarHelper.setStatusBarLightMode(getBaseFragmentActivity());
    }

    private void setListener() {
        mNodePlanRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startFragment(new BPNodePlanFragment());
            }
        });

        mNodeBuildRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startFragment(new BPNodeBuildFragment());
            }
        });

        // open https://m-news.bumo.io/
        mInformationRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getContext(), BumoNewsActivity.class);
                startActivity(intent);
            }
        });

        // open weixin mini program
        mXiaobuYoupinRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                IWXAPI api = WXAPIFactory.createWXAPI(getContext(), WeChat_APPID);
                WXLaunchMiniProgram.Req req = new WXLaunchMiniProgram.Req();
                req.userName = XB_YOUPING_USERNAME;
                req.miniprogramType = WXLaunchMiniProgram.Req.MINIPTOGRAM_TYPE_RELEASE;
                api.sendReq(req);
            }
        });
    }


    private class PageImageOnClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case 0:
                    Toast.makeText(getContext(), "图片1被点击", Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    Toast.makeText(getContext(), "图片3被点击", Toast.LENGTH_SHORT).show();
                    break;
            }

        }
    }

    private class PageImageOnTouch implements View.OnTouchListener {

        private long downTime;

        @Override
        public boolean onTouch(View v, MotionEvent event) {


            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    isDownStop = true;
                    downTime = System.currentTimeMillis();
                    break;
                case MotionEvent.ACTION_MOVE:
                    isDownStop = true;
                    break;
                case MotionEvent.ACTION_UP:
                    isDownStop = false;
                    if (System.currentTimeMillis() - downTime < 500) {
                        Toast.makeText(getContext(), "图片" + v.getId() + "被点击", Toast.LENGTH_SHORT).show();
                    }

                    break;
                case MotionEvent.ACTION_CANCEL:
                    isDownStop = false;
                    break;


            }

            return true;
        }
    }


}
