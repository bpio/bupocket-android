package com.bupocket.adaptor;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.bupocket.R;
import com.bupocket.base.AbsBaseAdapter;
import com.bupocket.base.AbsViewHolderAdapter;
import com.bupocket.base.BaseViewHolder;
import com.bupocket.enums.SuperNodeTypeEnum;
import com.bupocket.model.SuperNodeModel;
import com.bupocket.utils.LogUtils;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.qmuiteam.qmui.widget.popup.QMUIListPopup;
import com.qmuiteam.qmui.widget.popup.QMUIPopup;

import java.util.ArrayList;
import java.util.List;

import static com.qmuiteam.qmui.widget.popup.QMUIPopup.DIRECTION_BOTTOM;

public class SuperNodeAdapter extends AbsViewHolderAdapter<SuperNodeModel> {

    QMUIListPopup morePop;
    MoreBtnAdapter moreBtnAdapter;

    public SuperNodeAdapter(@NonNull Context context) {
        super(context);

        initData();
    }

    private void initData() {
        if (moreBtnAdapter == null) {
            moreBtnAdapter = new MoreBtnAdapter(context);
            ArrayList<String> newData = new ArrayList<>();
            newData.add(context.getResources().getString(R.string.cancel_vote));
            newData.add(context.getResources().getString(R.string.vote_record));
            moreBtnAdapter.setNewData(newData);
        }

    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_node_plan_list_item;
    }

    @Override
    protected void convert(BaseViewHolder holder, SuperNodeModel itemData) {

        if (itemData == null) {
            return;
        }
        holder.setText(R.id.nodeNameTv, itemData.getNodeName());
        String identityType = itemData.getIdentityType();

        if(SuperNodeTypeEnum.VALIDATOR.getCode().equals(identityType)){
            holder.setText(R.id.nodeTypeTv, context.getResources().getString(R.string.common_node));
        }else if(SuperNodeTypeEnum.ECOLOGICAL.getCode().equals(identityType)){
            holder.setText(R.id.nodeTypeTv, context.getResources().getString(R.string.ecological_node));
        }

        holder.setText(R.id.haveVotesNumTv, itemData.getNodeVote());
        holder.setText(R.id.myVotesNumTv, itemData.getMyVoteCount());

        final View shareBtn = holder.getView(R.id.shareBtn);
        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        final View revokeVoteBtn = holder.getView(R.id.revokeVoteBtn);
        revokeVoteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        /*final View moreBtn = holder.getView(R.id.moreBtnIv);
        moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                initPop();
                if (morePop.isShowing()) {
                    morePop.dismiss();
                } else {
                    morePop.show(v);
                }


            }
        });*/


//        holder.setText(R.id.nodeIconBgIv,itemData.getNodeName());


    }

    /*private void initPop() {
        if (morePop != null) {
            return;
        }
        morePop = new QMUIListPopup(context, DIRECTION_BOTTOM, moreBtnAdapter);
        morePop.create(QMUIDisplayHelper.dp2px(context, 150), QMUIDisplayHelper.dp2px(context, 400), new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
          Toast.makeText(getActivity(), "Item " + (i + 1), Toast.LENGTH_SHORT).show();
                LogUtils.e("位置：" + i);
                morePop.dismiss();
            }
        });
        morePop.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                        mActionButton2.setText(getContext().getResources().getString(R.string.popup_list_action_button_text_show));
            }
        });
        morePop.setAnimStyle(QMUIPopup.ANIM_GROW_FROM_CENTER);
        morePop.setPreferredDirection(QMUIPopup.DIRECTION_BOTTOM);
    }*/
}
