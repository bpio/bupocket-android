package com.bupocket.fragment;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.bupocket.BPApplication;
import com.bupocket.R;
import com.bupocket.base.BaseFragment;
import com.bupocket.utils.KillSelfService;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView;
import com.qmuiteam.qmui.widget.grouplist.QMUIGroupListView;

public class BPSettingFragment extends BaseFragment {
    @BindView(R.id.topbar)
    QMUITopBarLayout mTopBar;
    @BindView(R.id.settingLv)
    QMUIGroupListView mSettingLv;


    @Override
    protected View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_setting, null);
        ButterKnife.bind(this, root);
        init();
        return root;
    }

    private void init() {
        initData();
        initUI();
    }

    private void initData() {

    }

    private void initUI() {
        QMUIStatusBarHelper.setStatusBarLightMode(getBaseFragmentActivity());
        initTopBar();
        initGroupListView();
    }

    private void initGroupListView() {


        // switch node item
        QMUICommonListItemView switchNode = mSettingLv.createItemView(getString(R.string.switch_node_title_txt));
        switchNode.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_SWITCH);
        switchNode.setImageDrawable(getResources().getDrawable(R.mipmap.icon_scan_blue));
        // get bumoNode and set checked
//        if(SharedPreferencesHelper.getInstance().getInt("bumoNode",Constants.DEFAULT_BUMO_NODE) == BumoNodeEnum.TEST.getCode()){
//            switchNode.getSwitch().setChecked(true);
//        }else {
//            switchNode.getSwitch().setChecked(false);
//        }
        switchNode.getSwitch().setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    BPApplication.switchNetConfig("mainnet");
//                    SharedPreferencesHelper.getInstance().save("bumoNode", BumoNodeEnum.TEST.getCode());
//                    restartApp(getContext());
                }else {
//                    SharedPreferencesHelper.getInstance().save("bumoNode", BumoNodeEnum.MAIN.getCode());
//                    restartApp(getContext());
                    BPApplication.switchNetConfig("mainnet");
                }
            }
        });

        QMUIGroupListView.newSection(getContext())
                .setSeparatorDrawableRes(R.color.app_color_white)
                .setSeparatorDrawableRes(R.color.app_color_white,R.color.app_color_white,R.color.app_color_white,R.color.app_color_white)
                .addItemView(switchNode,null)
                .addTo(mSettingLv);
    }

    public void restartApp(Context context) {
//        Intent intent = new Intent(context, BPMainActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(intent);
//        android.os.Process.killProcess(android.os.Process.myPid());
        Intent intent1=new Intent(context,KillSelfService.class);
        intent1.putExtra("PackageName",context.getPackageName());
        intent1.putExtra("Delayed",200);
        context.startService(intent1);

        android.os.Process.killProcess(android.os.Process.myPid());
    }

    private void initTopBar() {
        mTopBar.setBackgroundDividerEnabled(false);
        mTopBar.addLeftImageButton(R.mipmap.icon_tobar_left_arrow, R.id.topbar_left_arrow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popBackStack();
            }
        });
    }
}
