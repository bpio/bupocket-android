package com.bupocket.fragment;

import android.graphics.drawable.Drawable;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bupocket.R;
import com.bupocket.base.BaseFragment;
import com.bupocket.enums.CurrencyTypeEnum;
import com.bupocket.fragment.home.HomeFragment;
import com.bupocket.utils.SharedPreferencesHelper;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView;
import com.qmuiteam.qmui.widget.grouplist.QMUIGroupListView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BPCurrencyFragment extends BaseFragment {
    @BindView(R.id.topbar)
    QMUITopBarLayout mTopBar;
    @BindView(R.id.monetaryLv)
    QMUIGroupListView mMonetaryLv;

    protected SharedPreferencesHelper sharedPreferencesHelper;

    @Override
    protected View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_currency, null);
        ButterKnife.bind(this, root);
        init();
        return root;
    }

    private void init() {
        initData();
        initUI();
    }

    private void initData() {
        sharedPreferencesHelper = new SharedPreferencesHelper(getContext(), "buPocket");
    }

    private void initUI() {
        QMUIStatusBarHelper.setStatusBarLightMode(getBaseFragmentActivity());
        initTopBar();
        initGroupListView();
    }

    private void initGroupListView() {
//        mMonetaryLv.setDividerPadding(10);
//        mMonetaryLv.setShowDividers(LinearLayout.SHOW_DIVIDER_END);
//        mMonetaryLv.setDividerDrawable(getResources().getDrawable(R.drawable.shape_corner_green));
        mMonetaryLv.setDividerDrawable(getResources().getDrawable(R.drawable.bg_gray_line));
        final QMUICommonListItemView CNYItem = mMonetaryLv.createItemView(getString(R.string.currency_cny_txt));
        CNYItem.getTextView().setTextColor(getResources().getColor(R.color.app_txt_color_gray_2));
//        View lineView=LayoutInflater.from(mContext).inflate(R.layout.view_gray_line,null);
//        CNYItem.setAccessoryType(QMUICommonListItemView.VERTICAL);
//        CNYItem.addAccessoryCustomView(lineView);


        final QMUICommonListItemView USDItem = mMonetaryLv.createItemView(getString(R.string.currency_usd_txt));
        USDItem.getTextView().setTextColor(getResources().getColor(R.color.app_txt_color_gray_2));

        final QMUICommonListItemView JPYItem = mMonetaryLv.createItemView(getString(R.string.currency_jpy_txt));
        JPYItem.getTextView().setTextColor(getResources().getColor(R.color.app_txt_color_gray_2));

        final QMUICommonListItemView KRWItem = mMonetaryLv.createItemView(getString(R.string.currency_krw_txt));
        KRWItem.getTextView().setTextColor(getResources().getColor(R.color.app_txt_color_gray_2));

        ImageView selected = new ImageView(getContext());
        selected.setImageDrawable(getResources().getDrawable(R.mipmap.icon_language_selected));
        String currencyType = sharedPreferencesHelper.getSharedPreference("currencyType","CNY").toString();
        switch (currencyType){
            case "CNY":{
                CNYItem.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CUSTOM);
                CNYItem.addAccessoryCustomView(selected);
                break;
            }
            case "USD":{
                USDItem.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CUSTOM);
                USDItem.addAccessoryCustomView(selected);
                break;
            }
            case "JPY":{
                JPYItem.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CUSTOM);
                JPYItem.addAccessoryCustomView(selected);
                break;
            }
            case "KRW":{
                KRWItem.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CUSTOM);
                KRWItem.addAccessoryCustomView(selected);
                break;
            }
        }

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CNYItem.setAccessoryType(QMUICommonListItemView.VERTICAL);
                USDItem.setAccessoryType(QMUICommonListItemView.VERTICAL);
                JPYItem.setAccessoryType(QMUICommonListItemView.VERTICAL);
                KRWItem.setAccessoryType(QMUICommonListItemView.VERTICAL);

                if (v instanceof QMUICommonListItemView) {
                    ImageView selected = new ImageView(getContext());
                    selected.setImageDrawable(getResources().getDrawable(R.mipmap.icon_language_selected));
                    CharSequence currencyTypeText = ((QMUICommonListItemView) v).getText();
                    sharedPreferencesHelper.put("currencyType",currencyTypeText);
                    ((QMUICommonListItemView) v).setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CUSTOM);
                    ((QMUICommonListItemView) v).addAccessoryCustomView(selected);
                }
                startFragment(new HomeFragment());
            }
        };

        QMUIGroupListView.newSection(getContext())
                .setSeparatorDrawableRes(R.color.app_color_white)
                .setSeparatorDrawableRes(R.color.app_color_white,R.color.app_color_white,R.color.app_color_white,R.color.app_color_white)
                .addItemView(CNYItem,onClickListener)
                .addItemView(USDItem,onClickListener)
                .addItemView(JPYItem,onClickListener)
                .addItemView(KRWItem,onClickListener)
                .addTo(mMonetaryLv);

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
