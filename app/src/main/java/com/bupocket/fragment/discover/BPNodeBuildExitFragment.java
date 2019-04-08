package com.bupocket.fragment.discover;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bupocket.R;
import com.qmuiteam.qmui.arch.QMUIFragment;
import com.qmuiteam.qmui.widget.QMUITopBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class BPNodeBuildExitFragment extends QMUIFragment {


    @BindView(R.id.topbar)
    QMUITopBar mTopBar;
    @BindView(R.id.tvBuildExitName)
    TextView tvBuildExitName;
    @BindView(R.id.tvBuildExitAmount)
    TextView tvBuildExitAmount;
    @BindView(R.id.btnBuildExit)
    Button btnBuildExit;
    Unbinder unbinder;

    @Override
    protected View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_bpnode_build_exit, null);
        unbinder = ButterKnife.bind(this, root);
        init();
        return root;
    }


    private void init(){

        initTopBar();

    }



    private void initTopBar() {
        mTopBar.setBackgroundDividerEnabled(false);
        mTopBar.addLeftImageButton(R.mipmap.icon_tobar_left_arrow, R.id.topbar_left_arrow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popBackStack();
            }
        });
        TextView title = mTopBar.setTitle(getResources().getString(R.string.build_exit_title));


    }


    @OnClick(R.id.btnBuildExit)
    public void onViewClicked() {

        getBaseFragmentActivity().popBackStack(BPNodeBuildFragment.class);
    }
}
