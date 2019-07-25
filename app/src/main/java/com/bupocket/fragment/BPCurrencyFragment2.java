package com.bupocket.fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.bupocket.R;
import com.bupocket.adaptor.CurrencyAdapter;
import com.bupocket.base.AbsBaseFragment;
import com.bupocket.base.BaseFragment;
import com.bupocket.common.Constants;
import com.bupocket.fragment.home.HomeFragment;
import com.bupocket.model.NodeSettingModel;
import com.bupocket.utils.LocaleUtil;
import com.bupocket.utils.SharedPreferencesHelper;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView;
import com.qmuiteam.qmui.widget.grouplist.QMUIGroupListView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BPCurrencyFragment2 extends AbsBaseFragment {
    @BindView(R.id.topbar)
    QMUITopBarLayout mTopBar;
    @BindView(R.id.monetaryLv)
    ListView mMonetaryLv;


    public static String[] currencyArray = new String[]{"CNY", "USD", "JPY", "KRW"};


    @Override
    protected int getLayoutView() {
        return R.layout.fragment_currency2;
    }

    public void initData() {

    }

    @Override
    protected void setListeners() {

        mMonetaryLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                spHelper.put("currencyType", currencyArray[position]);
                LocaleUtil.restartApp(mContext);
            }
        });
    }

    public void initView() {
        QMUIStatusBarHelper.setStatusBarLightMode(getBaseFragmentActivity());
        initTopBar();
        initListView();

    }

    private void initListView() {
        CurrencyAdapter adapter = new CurrencyAdapter(getActivity());
        ArrayList<NodeSettingModel> newData = new ArrayList<>();

        for (int i = 0; i < currencyArray.length; i++) {
            NodeSettingModel nodeSettingModel = new NodeSettingModel();
            nodeSettingModel.setUrl(currencyArray[i]);
            newData.add(nodeSettingModel);
        }

        adapter.setNewData(newData);
        mMonetaryLv.setAdapter(adapter);
    }


    private void initTopBar() {
        mTopBar.setTitle(R.string.monetary_title_txt);
        mTopBar.addLeftImageButton(R.mipmap.icon_tobar_left_arrow, R.id.topbar_left_arrow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popBackStack();
            }
        });
    }
}
