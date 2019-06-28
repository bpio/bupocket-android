package com.bupocket.fragment;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.bupocket.R;
import com.bupocket.adaptor.VersionLogAdapter;
import com.bupocket.base.AbsBaseFragment;
import com.bupocket.common.ConstantsType;
import com.bupocket.enums.ExceptionEnum;
import com.bupocket.http.api.RetrofitFactory;
import com.bupocket.http.api.VersionService;
import com.bupocket.http.api.dto.resp.ApiResult;
import com.bupocket.model.LogListModel;
import com.bupocket.model.VersionLogModel;
import com.qmuiteam.qmui.widget.QMUIEmptyView;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VersionLogFragment extends AbsBaseFragment {

    @BindView(R.id.topbar)
    QMUITopBarLayout topbar;
    @BindView(R.id.refreshComLv)
    ListView refreshComLv;
    @BindView(R.id.reloadBtn)
    QMUIRoundButton reloadBtn;
    @BindView(R.id.loadFailedLL)
    LinearLayout loadFailedLL;
    @BindView(R.id.recordEmptyLL)
    LinearLayout recordEmptyLL;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.qmuiEmptyView)
    QMUIEmptyView qmuiEmptyView;


    private int pageStart;
    private final int normalPageSize = 10;
    private VersionLogAdapter adapter;
    private VersionLogModel.PageBean page;
    private Call<ApiResult<VersionLogModel>> versionLogService;

    @Override
    protected int getLayoutView() {
        return R.layout.fragment_version_log;
    }

    @Override
    protected void initView() {
        initTopBar();
        initListView();
    }

    private void initListView() {
        adapter = new VersionLogAdapter(mContext);
        refreshComLv.setAdapter(adapter);

    }

    private void initTopBar() {
        topbar.setTitle(R.string.version_info);
        topbar.addLeftImageButton(R.mipmap.icon_tobar_left_arrow, R.id.topbar_left_arrow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popBackStack();
            }
        });
    }

    @Override
    protected void initData() {

        refreshLayout.setEnableLoadMore(false);
        reqVersionLogData(1);

    }

    private void reqVersionLogData(final int curPageStart) {
        pageStart = curPageStart;
        HashMap<Object, Object> reqMap = new HashMap<>();
        reqMap.put(ConstantsType.APP_TYPE, 1);
        reqMap.put(ConstantsType.PAGE_START, curPageStart);
        reqMap.put(ConstantsType.PAGE_SIZE, normalPageSize);
        VersionService versionService = RetrofitFactory.getInstance().getRetrofit().create(VersionService.class);
        versionLogService = versionService.getVersionLog(reqMap);
        versionLogService.enqueue(new Callback<ApiResult<VersionLogModel>>() {
            @Override
            public void onResponse(Call<ApiResult<VersionLogModel>> call, Response<ApiResult<VersionLogModel>> response) {


                if (response.body() != null && ExceptionEnum.SUCCESS.getCode().equals(response.body().getErrCode()) && response.body().getData() != null) {
                    List<LogListModel> logList = response.body().getData().getLogList();
                    page = response.body().getData().getPage();
                    if (curPageStart == 1) {
                        adapter.setNewData(logList);
                        refreshLayout.setEnableLoadMore(true);
                    } else {
                        adapter.addMoreDataList(logList);
                    }

                    if (recordEmptyLL != null) {
                        recordEmptyLL.setVisibility(View.GONE);
                    }

                    if (loadFailedLL != null) {
                        loadFailedLL.setVisibility(View.GONE);
                    }

                } else {
                    recordEmptyLL.setVisibility(View.VISIBLE);
                    adapter.clear();
                }


            }

            @Override
            public void onFailure(Call<ApiResult<VersionLogModel>> call, Throwable t) {
                if (call.isCanceled()) {
                    return;
                }
                if (loadFailedLL != null) {
                    loadFailedLL.setVisibility(View.VISIBLE);
                    refreshLayout.setEnableLoadMore(false);
                    adapter.clear();
                }

            }
        });
    }

    @Override
    protected void setListeners() {

        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull final RefreshLayout refreshLayout) {
                refreshData();
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull final RefreshLayout refreshLayout) {

                LoadMoreData();
            }
        });

        reloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshLayout.autoRefresh(0, 200, 1, false);
            }
        });
    }

    private void refreshData() {
        refreshLayout.getLayout().postDelayed(new Runnable() {
            @Override
            public void run() {
                reqVersionLogData(1);
                refreshLayout.finishRefresh();
            }
        }, 500);

    }

    private void LoadMoreData() {
        refreshLayout.getLayout().postDelayed(new Runnable() {


            @Override
            public void run() {
                int curPageStart = pageStart + 1;
                if (page.getEndOfGroup() < curPageStart) {
                    refreshLayout.finishLoadMore();
                    refreshLayout.finishLoadMoreWithNoMoreData();
                    return;
                }
                reqVersionLogData(curPageStart);
                refreshLayout.finishLoadMore();

                initData();
            }
        }, 500);

    }

    @Override
    public void onPause() {
        super.onPause();
        versionLogService.cancel();
    }
}
