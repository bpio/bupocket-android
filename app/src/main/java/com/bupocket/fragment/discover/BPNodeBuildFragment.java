package com.bupocket.fragment.discover;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bupocket.R;
import com.bupocket.adaptor.NodeBuildAdapter;
import com.bupocket.base.AbsBaseFragment;
import com.bupocket.http.api.NodeBuildService;
import com.bupocket.http.api.RetrofitFactory;
import com.bupocket.http.api.dto.resp.ApiResult;
import com.bupocket.model.CoBuildListModel;
import com.bupocket.model.NodeBuildModel;
import com.bupocket.wallet.enums.ExceptionEnum;
import com.github.ksoichiro.android.observablescrollview.ObservableListView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.nineoldandroids.view.ViewPropertyAnimator;
import com.qmuiteam.qmui.widget.QMUIEmptyView;
import com.qmuiteam.qmui.widget.QMUITopBar;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class BPNodeBuildFragment extends AbsBaseFragment {

    @BindView(R.id.topbar)
    QMUITopBar mTopBar;
    @BindView(R.id.lvRefresh)
    ObservableListView lvNodeBuild;
    @BindView(R.id.copyCommandBtn)
    QMUIRoundButton copyCommandBtn;
    @BindView(R.id.llLoadFailed)
    LinearLayout llLoadFailed;
    @BindView(R.id.addressRecordEmptyLL)
    LinearLayout addressRecordEmptyLL;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.qmuiEmptyView)
    QMUIEmptyView qmuiEmptyView;



    private NodeBuildAdapter nodeBuildAdapter;
    private ArrayList<NodeBuildModel> nodeList;
    private View headerView;
    private TextView tvTitle;
    private Call<ApiResult<CoBuildListModel>> serviceCoBuild;


    @Override
    protected int getLayoutView() {
        return R.layout.fragment_bpnode_build;
    }

    @Override
    protected void initView() {
        initTopBar();
        if (nodeBuildAdapter == null) {
            nodeBuildAdapter = new NodeBuildAdapter(getContext());
        }

        if (nodeList == null) {
            nodeList = new ArrayList<>();
        }
        lvNodeBuild.setAdapter(nodeBuildAdapter);
        headerView = LayoutInflater.from(mContext).inflate(R.layout.view_com_title, null);
        lvNodeBuild.addHeaderView(headerView);

        refreshLayout.setEnableLoadMore(false);
        qmuiEmptyView.show(true);
    }

    @Override
    protected void initData() {

        getBuildData();
    }

    @Override
    protected void setListeners(){
        lvNodeBuild.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                NodeBuildModel nodeBuildModel = nodeList.get(position - 1);
                if (nodeBuildModel == null) {
                    return;
                }
                BPNodeBuildDetailFragment fragment = new BPNodeBuildDetailFragment();
                Bundle args = new Bundle();
                args.putString("nodeId", nodeBuildModel.getNodeId());
                fragment.setArguments(args);
                startFragment(fragment);
            }
        });
        copyCommandBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshLayout.autoRefresh(0,200,1,false);
            }
        });
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(final RefreshLayout refreshlayout) {

                getBuildData();
            }
        });
        refreshLayout.setEnableLoadMore(false);

        lvNodeBuild.setScrollViewCallbacks(new ObservableScrollViewCallbacks() {
            @Override
            public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
//                LogUtils.e("scrollY:" + scrollY + "\tfirstScroll:" + firstScroll + "\tdragging:" + dragging);
                if (!dragging) {

                    int toolbarHeight = headerView.getHeight();
                    if (scrollY < toolbarHeight * 0.8) {
                        showToolbar();
                    }
                }
            }

            @Override
            public void onDownMotionEvent() {

            }

            @Override
            public void onUpOrCancelMotionEvent(ScrollState scrollState) {
//                LogUtils.e("scrollState" + scrollState);
                int toolbarHeight = headerView.getHeight();
                int scrollY = lvNodeBuild.getCurrentScrollY();
                if (scrollState == ScrollState.DOWN) {
                    if (toolbarHeight > scrollY * 0.8) {
                        showToolbar();
                    }
                } else if (scrollState == ScrollState.UP) {
                    if (toolbarHeight <= scrollY * 0.8) {
                        hideToolbar();
                    } else {
                        showToolbar();
                    }
                }
            }
        });
    }


    private void showToolbar() {
        ViewPropertyAnimator.animate(tvTitle).alpha(0).setDuration(200).start();
    }

    private void hideToolbar() {
        tvTitle.setVisibility(View.VISIBLE);
        ViewPropertyAnimator.animate(tvTitle).alpha(100).setDuration(200).start();
    }





    private void getBuildData() {


        HashMap<String, Object> map = new HashMap<>();

        final NodeBuildService nodeBuildService = RetrofitFactory.getInstance().getRetrofit().create(NodeBuildService.class);
        serviceCoBuild = nodeBuildService.getNodeBuildList(map);
        serviceCoBuild.enqueue(new Callback<ApiResult<CoBuildListModel>>() {
            @Override
            public void onResponse(Call<ApiResult<CoBuildListModel>> call, Response<ApiResult<CoBuildListModel>> response) {

                ApiResult<CoBuildListModel> body = response.body();
                llLoadFailed.setVisibility(View.GONE);
                if (body != null && ExceptionEnum.SUCCESS.getCode().equals(body.getErrCode())
                        && body.getData() != null && body.getData().getNodeList() != null
                        && body.getData().getNodeList().size() > 0) {
                    nodeList = body.getData().getNodeList();
                    if (nodeList != null) {
                        nodeBuildAdapter.setNewData(nodeList);
                        nodeBuildAdapter.notifyDataSetChanged();
                    }

                } else {
                    addressRecordEmptyLL.setVisibility(View.VISIBLE);
                }

                qmuiEmptyView.show(null,null);
                refreshLayout.finishRefresh();
            }

            @Override
            public void onFailure(Call<ApiResult<CoBuildListModel>> call, Throwable t) {

                if (llLoadFailed!=null) {
                    llLoadFailed.setVisibility(View.VISIBLE);
                }
                nodeBuildAdapter.setNewData(new ArrayList<NodeBuildModel>());
                nodeBuildAdapter.notifyDataSetChanged();

                qmuiEmptyView.show(null,null);
                refreshLayout.finishRefresh();
            }


        });



    }


    private void initTopBar() {
        mTopBar.setBackgroundDividerEnabled(false);
        mTopBar.addLeftImageButton(R.mipmap.icon_tobar_left_arrow, R.id.topbar_left_arrow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popBackStack();
//                getChildFragmentManager().beginTransaction().attach(BPNodeBuildFragment.this);

            }
        });
        tvTitle = mTopBar.setTitle(getResources().getString(R.string.build_node_txt));
        tvTitle.setVisibility(View.INVISIBLE);

    }

    @Override
    public void onDestroy() {
        serviceCoBuild.cancel();
        super.onDestroy();
    }
}
