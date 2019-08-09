package com.bupocket.fragment.discover;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.bupocket.R;
import com.bupocket.adaptor.NodeBuildAdapter;
import com.bupocket.base.AbsBaseFragment;
import com.bupocket.database.greendao.NodeBuildModelDao;
import com.bupocket.http.api.NodeBuildService;
import com.bupocket.http.api.RetrofitFactory;
import com.bupocket.http.api.dto.resp.ApiResult;
import com.bupocket.model.CoBuildListModel;
import com.bupocket.model.NodeBuildModel;
import com.bupocket.wallet.enums.ExceptionEnum;
import com.qmuiteam.qmui.widget.QMUIEmptyView;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class BPNodeBuildFragment extends AbsBaseFragment {

    @BindView(R.id.topbar)
    QMUITopBarLayout mTopBar;
    @BindView(R.id.refreshComLv)
    ListView nodeBuildLv;
    @BindView(R.id.reloadBtn)
    QMUIRoundButton copyCommandBtn;
    @BindView(R.id.loadFailedLL)
    LinearLayout loadFailedLL;
    @BindView(R.id.recordEmptyLL)
    LinearLayout addressRecordEmptyLL;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.qmuiEmptyView)
    QMUIEmptyView qmuiEmptyView;


    private NodeBuildAdapter nodeBuildAdapter;
    private List<NodeBuildModel> nodeList;

    private Call<ApiResult<CoBuildListModel>> serviceCoBuild;
    private NodeBuildModelDao nodeBuildModelDao;


    @Override
    protected int getLayoutView() {
        return R.layout.fragment_bpnode_build;
    }

    @Override
    protected void initView() {
        initTopBar();
        initListView();

    }

    private void initListView() {
        if (nodeBuildAdapter == null) {
            nodeBuildAdapter = new NodeBuildAdapter(getContext());
        }

        if (nodeList == null) {
            nodeList = new ArrayList<>();
        }

        nodeBuildLv.setAdapter(nodeBuildAdapter);

        refreshLayout.setEnableLoadMore(false);
        qmuiEmptyView.show(true);
    }

    private void querySetNewData() {
        nodeBuildModelDao = mApplication.getDaoSession().getNodeBuildModelDao();
        if (nodeBuildModelDao==null) {
            return;
        }
        List<NodeBuildModel> nodeBuildModels = nodeBuildModelDao.loadAll();
        if (nodeBuildModels.size() > 0) {
            nodeBuildAdapter.setNewData(nodeBuildModels);
            nodeList=nodeBuildModels;
        }
    }

    @Override
    protected void initData() {
        querySetNewData();

        nodeBuildLv.postDelayed(new Runnable() {
            @Override
            public void run() {
                getBuildData();
            }
        },500);

    }

    @Override
    protected void setListeners() {
        nodeBuildLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                NodeBuildModel nodeBuildModel = nodeList.get(position);
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
                refreshLayout.autoRefresh(0, 200, 1, false);
            }
        });
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(final RefreshLayout refreshlayout) {

                getBuildData();
            }
        });
        refreshLayout.setEnableLoadMore(false);

    }


    private void getBuildData() {
        HashMap<String, Object> map = new HashMap<>();

        final NodeBuildService nodeBuildService = RetrofitFactory.getInstance().getRetrofit().create(NodeBuildService.class);
        serviceCoBuild = nodeBuildService.getNodeBuildList(map);
        serviceCoBuild.enqueue(new Callback<ApiResult<CoBuildListModel>>() {
            @Override
            public void onResponse(Call<ApiResult<CoBuildListModel>> call, Response<ApiResult<CoBuildListModel>> response) {

                ApiResult<CoBuildListModel> body = response.body();
                loadFailedLL.setVisibility(View.GONE);
                if (body != null && ExceptionEnum.SUCCESS.getCode().equals(body.getErrCode())
                        && body.getData() != null && body.getData().getNodeList() != null
                        && body.getData().getNodeList().size() > 0) {
                    nodeList = body.getData().getNodeList();
                    if (nodeList != null) {
                        nodeBuildAdapter.setNewData(nodeList);
                        insertDao(nodeList);
                        nodeBuildAdapter.notifyDataSetChanged();
                    }

                } else {
                    addressRecordEmptyLL.setVisibility(View.VISIBLE);
                }

                qmuiEmptyView.show(null, null);
                refreshLayout.finishRefresh();
            }

            @Override
            public void onFailure(Call<ApiResult<CoBuildListModel>> call, Throwable t) {
                if (call.isCanceled()) {
                    return;
                }

                refreshLayout.finishRefresh();
                if (nodeList!=null&&nodeList.size()>0){
                    return;
                }


                if (loadFailedLL != null) {
                    loadFailedLL.setVisibility(View.VISIBLE);
                }
                nodeBuildAdapter.setNewData(new ArrayList<NodeBuildModel>());
                qmuiEmptyView.show(null, null);

            }


        });


    }

    private void insertDao(List<NodeBuildModel> nodeList) {
        nodeBuildModelDao.deleteAll();
        nodeBuildModelDao.insertInTx(nodeList);
    }


    private void initTopBar() {
        mTopBar.setBackgroundDividerEnabled(false);
        mTopBar.setTitle(R.string.build_node_txt);
        mTopBar.addLeftImageButton(R.mipmap.icon_tobar_left_arrow, R.id.topbar_left_arrow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popBackStack();
            }
        });

    }

    @Override
    public void onPause() {
        super.onPause();
        serviceCoBuild.cancel();
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
    }
}
