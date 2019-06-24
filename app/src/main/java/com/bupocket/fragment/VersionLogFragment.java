package com.bupocket.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

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
import com.github.ksoichiro.android.observablescrollview.ObservableListView;
import com.qmuiteam.qmui.widget.QMUIEmptyView;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VersionLogFragment extends AbsBaseFragment {

    @BindView(R.id.topbar)
    QMUITopBarLayout topbar;
    @BindView(R.id.lvRefresh)
    ObservableListView lvRefresh;
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
    Unbinder unbinder;
    private int pageStart;
    private final int normalPageSize = 10;
    private VersionLogAdapter adapter;

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
        lvRefresh.setAdapter(adapter);
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
        pageStart = 1;
        reqVersionLogData(pageStart);

    }

    private void reqVersionLogData(final int curPageStart) {

        HashMap<Object, Object> reqMap = new HashMap<>();
        reqMap.put(ConstantsType.APP_TYPE, 1);
        reqMap.put(ConstantsType.PAGE_START, curPageStart);
        reqMap.put(ConstantsType.PAGE_SIZE, normalPageSize);
        VersionService versionService = RetrofitFactory.getInstance().getRetrofit().create(VersionService.class);
        Call<ApiResult<VersionLogModel>> versionLog = versionService.getVersionLog(reqMap);
        versionLog.enqueue(new Callback<ApiResult<VersionLogModel>>() {
            @Override
            public void onResponse(Call<ApiResult<VersionLogModel>> call, Response<ApiResult<VersionLogModel>> response) {


                if (response.body() != null && ExceptionEnum.SUCCESS.getCode().equals(response.body().getErrCode()) && response.body().getData() != null) {
                    List<LogListModel> logList = response.body().getData().getLogList();
                    if (curPageStart == 1) {
                        adapter.setNewData(logList);
                    } else {
                        adapter.addMoreDataList(logList);
                    }
                }


            }

            @Override
            public void onFailure(Call<ApiResult<VersionLogModel>> call, Throwable t) {
                if (call.isCanceled()) {
                    return;
                }


            }
        });
    }

    @Override
    protected void setListeners() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
