package com.bupocket.base;


import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.bupocket.utils.LocaleUtil;
import com.bupocket.utils.LogUtils;
import com.qmuiteam.qmui.arch.QMUIFragmentActivity;

import java.util.ArrayList;

public abstract class BaseFragmentActivity extends QMUIFragmentActivity {

    public static final int BASE_REQUEST_CODE = 1003;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleUtil.attachBaseContext(base));
    }


    public ArrayList<String> getNeedRequestPermissions(ArrayList<String> permissions) {

        if (null == permissions || permissions.size() == 0) {
            return null;
        }
        ArrayList<String> needRequestList = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                needRequestList.add(permission);
            }
        }
        return needRequestList;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == BASE_REQUEST_CODE) {
            LogUtils.e(" request permission success");
        }

    }

}

