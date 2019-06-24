package com.bupocket.adaptor;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.TimeUtils;
import android.widget.TextView;

import com.bupocket.R;
import com.bupocket.base.AbsViewHolderAdapter;
import com.bupocket.base.BaseViewHolder;
import com.bupocket.fragment.VersionLogFragment;
import com.bupocket.model.VersionLogModel;
import com.bupocket.utils.LocaleUtil;
import com.bupocket.utils.TimeUtil;

public class VersionLogAdapter extends AbsViewHolderAdapter<VersionLogModel.LogListBean> {


    public VersionLogAdapter(@NonNull Context context) {
        super(context);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_version_log_item;
    }

    @Override
    protected void convert(BaseViewHolder holder, VersionLogModel.LogListBean itemData) {

//        TextView versionNumTv = (TextView) holder.getView(R.id.versionNumTV);
//        TextView versionInfoTv = (TextView) holder.getView(R.id.versionInfoTV);
//        if (!TextUtils.isEmpty(itemData.getVerContents())) {
//            versionNumTv.setText(itemData.getVerNumber() + "   " + TimeUtil.timeStamp2Date(itemData.getCreateTime(), TimeUtil.TIME_TYPE_ONE));
//        }
//
//        switch (LocaleUtil.getLanguageStatus()) {
//            case 0:
//                versionInfoTv.setText(itemData.getVerContents());
//                break;
//            case 1:
//                versionInfoTv.setText(itemData.getEnglishVerContents());
//                break;
//        }


    }
}
