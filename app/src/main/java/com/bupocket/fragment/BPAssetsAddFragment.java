package com.bupocket.fragment;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.alibaba.fastjson.JSON;
import com.bupocket.R;
import com.bupocket.adaptor.SearchTokenAdapter;
import com.bupocket.base.BaseFragment;
import com.bupocket.http.api.RetrofitFactory;
import com.bupocket.http.api.TokenService;
import com.bupocket.http.api.dto.resp.ApiResult;
import com.bupocket.http.api.dto.resp.SearchTokenRespDto;
import com.bupocket.utils.CommonUtil;
import com.bupocket.utils.SharedPreferencesHelper;
import com.bupocket.view.DrawableEditText;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.QMUIEmptyView;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BPAssetsAddFragment extends BaseFragment {
    @BindView(R.id.topbar)
    QMUITopBarLayout mTopBar;
    @BindView(R.id.searchTokenListView)
    ListView mSearchTokenListView;
    @BindView(R.id.emptyView)
    QMUIEmptyView mEmptyView;
    @BindView(R.id.searchResultEmptyLL)
    LinearLayout mSearchResultEmptyLL;
    @BindView(R.id.searchTokenEt)
    DrawableEditText mSearchTokenEt;

    private SearchTokenAdapter searchTokenAdapter;
    private SharedPreferencesHelper sharedPreferencesHelper;
    private String currentWalletAddress;
    private List<SearchTokenRespDto.TokenListBean> respTokenList = new ArrayList<>();
    private boolean searchLoading = false;
    private boolean loadFailFlag = false;

    @Override
    protected View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_assets_add, null);
        ButterKnife.bind(this, root);
        QMUIStatusBarHelper.setStatusBarLightMode(getBaseFragmentActivity());
        initTopBar();
        sharedPreferencesHelper = new SharedPreferencesHelper(getContext(), "buPocket");
        currentWalletAddress = sharedPreferencesHelper.getSharedPreference("currentWalletAddress","").toString();
        if(CommonUtil.isNull(currentWalletAddress)){
            currentWalletAddress = sharedPreferencesHelper.getSharedPreference("currentAccAddr","").toString();
        }

        mSearchTokenEt.setOnDrawableClickListener(new DrawableEditText.OnDrawableClickListener() {
            @Override
            public void onDrawableClick() {
                if (searchLoading) {
                    return;
                }
                mSearchResultEmptyLL.setVisibility(View.GONE);
                String input = mSearchTokenEt.getText().toString().trim();
                if(CommonUtil.isNull(input)){
//                    mEmptyView.show(getResources().getString(R.string.search_result_not_found),null);
                    mEmptyView.show(false);
                    showOrHideNoResult(true);
                    return;
                }
                mSearchTokenEt.clearFocus();
                CommonUtil.hideInputMethod(getContext(), mSearchTokenEt);
                searchToken(input);
            }
        });

        mSearchTokenEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (searchLoading) {
                    return false;
                }
                mSearchResultEmptyLL.setVisibility(View.GONE);
                String input = mSearchTokenEt.getText().toString().trim();
                if(CommonUtil.isNull(input)){
                    mEmptyView.show(false);
                    showOrHideNoResult(true);
                    return false;
                }
                searchToken(input);
                mSearchTokenEt.clearFocus();
                CommonUtil.hideInputMethod(getContext(), mSearchTokenEt);
                return true;
            }
        });

        return root;
    }

    private void searchToken(String assetCode){
        searchLoading= true;
        respTokenList.clear();
        loadTokenAdapter();
        mEmptyView.show(true);
        showOrHideNoResult(false);
        mSearchResultEmptyLL.setVisibility(View.GONE);
        TokenService tokenService = RetrofitFactory.getInstance().getRetrofit().create(TokenService.class);
        Map<String, Object> parmasMap = new HashMap<>();
        parmasMap.put("address", currentWalletAddress);
        parmasMap.put("assetCode", assetCode);
        parmasMap.put("supportFuzzy", true);
        parmasMap.put("startPage", 1);
        parmasMap.put("pageSize", 100);
        Call<ApiResult<SearchTokenRespDto>> call = tokenService.queryTokens(parmasMap);
        call.enqueue(new Callback<ApiResult<SearchTokenRespDto>>() {
            @Override
            public void onResponse(Call<ApiResult<SearchTokenRespDto>> call, Response<ApiResult<SearchTokenRespDto>> response) {
                searchLoading= false;
                loadFailFlag = false;
                ApiResult<SearchTokenRespDto> respDto = response.body();
                if(respDto != null){
                    handleSearchTokenData(respDto.getData());
                }else {
                    respTokenList.clear();
                    loadTokenAdapter();
                    mEmptyView.show(false);
                    showOrHideNoResult(true);
                }
            }

            @Override
            public void onFailure(Call<ApiResult<SearchTokenRespDto>> call, Throwable t) {
                searchLoading= false;
                loadFailFlag = true;
                t.printStackTrace();
                respTokenList.clear();
                loadTokenAdapter();
                mEmptyView.show(getResources().getString(R.string.emptyView_mode_desc_fail_title), null);
            }
        });
    }

    private void handleSearchTokenData(SearchTokenRespDto searchTokenRespDto){

        if(searchTokenRespDto != null || null != searchTokenRespDto.getTokenList()){
            if(searchTokenRespDto.getTokenList() == null || searchTokenRespDto.getTokenList().size() == 0) {
                respTokenList.clear();
                mEmptyView.show(false);
                showOrHideNoResult(true);
            }else{
                respTokenList = searchTokenRespDto.getTokenList();
                mEmptyView.show(null, null);
                loadTokenAdapter();
            }

        }else{
            respTokenList.clear();
            loadFailFlag = true;
            mEmptyView.show(getResources().getString(R.string.emptyView_mode_desc_fail_title), null);
            loadTokenAdapter();
        }
    }

    private void loadTokenAdapter() {
        mSearchTokenListView.setVisibility(View.VISIBLE);
        searchTokenAdapter = new SearchTokenAdapter(respTokenList, getContext());
        mSearchTokenListView.setAdapter(searchTokenAdapter);
    }

    private void showOrHideNoResult(boolean showFlag) {
        if (showFlag) {
            mSearchResultEmptyLL.setVisibility(View.VISIBLE);
            mSearchTokenListView.setVisibility(View.GONE);
        } else {
            mSearchResultEmptyLL.setVisibility(View.GONE);
            mSearchTokenListView.setVisibility(View.VISIBLE);
        }
    }

    private void initTopBar() {
        mTopBar.setBackgroundDividerEnabled(true);
        mTopBar.setTitle(R.string.assets_add_txt);
        mTopBar.addLeftImageButton(R.mipmap.icon_tobar_left_arrow, R.id.topbar_left_arrow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QMUIStatusBarHelper.setStatusBarDarkMode(getBaseFragmentActivity());
                popBackStack();
            }
        });
    }
}
