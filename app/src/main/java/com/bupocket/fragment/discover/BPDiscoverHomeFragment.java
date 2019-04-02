package com.bupocket.fragment.discover;

import android.content.Intent;
import android.os.SystemClock;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bupocket.R;
import com.bupocket.activity.BumoNewsActivity;
import com.bupocket.adaptor.DisBannerAdapter;
import com.bupocket.base.BaseFragment;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.tencent.mm.opensdk.modelbiz.WXLaunchMiniProgram;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BPDiscoverHomeFragment extends BaseFragment {
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
    private ArrayList<ImageView> banListData;
    private DisBannerAdapter disBannerAdapter;
    private long PAGER_TIME = 3 * 1000;
    private boolean isStop;
    private boolean isDownStop;


    @Override
    protected View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_discover, null);
        ButterKnife.bind(this, root);
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
        ImageView iv;
        for (int i = 0; i < 4; i++) {
            iv = new ImageView(getContext());
            iv.setBackgroundResource(R.mipmap.upgrade_dialog_bg);
            iv.setId(i);
//            iv.setOnClickListener(new PageImageOnClick());
            iv.setOnTouchListener(new PageImageOnTouch());
            banListData.add(iv);
        }

        disBannerAdapter = new DisBannerAdapter(banListData, vpDisBanner);
        vpDisBanner.setAdapter(disBannerAdapter);

    }

    @Override
    public void onResume() {
        super.onResume();

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
                                vpDisBanner.setCurrentItem(vpDisBanner.getCurrentItem() + 1);
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
                String appId = "wxaecf7ac4085fd34a";
                IWXAPI api = WXAPIFactory.createWXAPI(getContext(), appId);
                WXLaunchMiniProgram.Req req = new WXLaunchMiniProgram.Req();
                req.userName = "gh_463781563a74";
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
                        Toast.makeText(getContext(), "图片"+v.getId()+"被点击", Toast.LENGTH_SHORT).show();
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
