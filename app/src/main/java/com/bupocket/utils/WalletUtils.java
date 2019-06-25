package com.bupocket.utils;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import com.bupocket.R;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.qmuiteam.qmui.widget.popup.QMUIPopup;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static com.bupocket.BPApplication.getContext;

public class WalletUtils {


    public static void showWalletPopup(Context context, String info, View v) {
        showWalletPopup(context, info, v, 200);
    }

    public static void showWalletPopup(Context context, String info, View v, int width) {
        QMUIPopup myNodeExplainPopup = new QMUIPopup(context,QMUIPopup.DIRECTION_NONE);
        TextView textView = new TextView(context);
        textView.setLayoutParams(myNodeExplainPopup.generateLayoutParam(
                QMUIDisplayHelper.dp2px(context, 200),
                WRAP_CONTENT
        ));
        textView.setLineSpacing(QMUIDisplayHelper.dp2px(context, 4), 1.0f);
        int padding = QMUIDisplayHelper.dp2px(context, 10);
        textView.setPadding(padding, padding, padding, padding);
        textView.setText(info);
        textView.setTextColor(context.getResources().getColor(R.color.app_color_white));
        textView.setBackgroundColor(context.getResources().getColor(R.color.popup_background_color));
        myNodeExplainPopup.setContentView(textView);
        myNodeExplainPopup.setAnimStyle(QMUIPopup.ANIM_GROW_FROM_CENTER);
        myNodeExplainPopup.setPreferredDirection(QMUIPopup.DIRECTION_BOTTOM);
        myNodeExplainPopup.show(v);
        ImageView arrowUp = myNodeExplainPopup.getDecorView().findViewById(R.id.arrow_up);
        arrowUp.setImageDrawable(context.getResources().getDrawable(R.mipmap.triangle));
    }
}
