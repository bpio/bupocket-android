package com.bupocket.utils;

import android.graphics.MaskFilter;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.orhanobut.logger.LogLevel;
import com.orhanobut.logger.Logger;

import org.json.JSONObject;


/***
 * log utils class
 *
 */
public class LogUtils {
    /**
     * log level none
     */
    public static final int LEVEL_NONE = 0;//上线级别

    /**
     */
    private static final int LEVEL_VERBOSE = 1;

    /**
     */
    private static final int LEVEL_DEBUG = 2;

    /**
     */
    private static final int LEVEL_INFO = 3;

    /**
     */
    private static final int LEVEL_WARN = 4;

    /**
     */
    public static final int LEVEL_ERROR = 5;

    /**
     */
    private static final String mTag = "mmmmbumo";

    /* 是否允许输出log LEVEL_NONE时候不输出日志 */
    public static int mDebuggable = 5;

    /**
     */
    private static long mTimestamp = 0;

    /**
     */
    public static void v(Object msg) {
        if (mDebuggable >= LEVEL_VERBOSE) {
            //Log.v(mTag, msg + "");
            Logger.t(mTag).v(msg + "");
        }
    }

    /**
     */
    public static void d(Object msg) { //(可以list,map等数据类型)
        if (mDebuggable >= LEVEL_DEBUG) {
            //Log.d(mTag, msg + "");
            Logger.t(mTag).d(msg + "");
            //Logger.d(msg);
        }
    }

    /**
     */
    public static void i(Object msg) {
        if (mDebuggable >= LEVEL_INFO) {
            //Log.i(mTag, msg + "");
            Logger.t(mTag).i(msg + "");
        }
    }

    /**
     */
    public static void w(Object msg) {
        if (mDebuggable >= LEVEL_WARN) {
            //Log.w(mTag, msg + "");
            Logger.t(mTag).w(msg + "");
        }
    }

    /**
     */
    public static void w(Throwable tr) {
        if (mDebuggable >= LEVEL_WARN) {
            Logger.t(mTag).w(mTag, "", tr);
        }
    }

    /**
     */
    public static void w(@Nullable String msg, Throwable tr) {
        if (mDebuggable >= LEVEL_WARN && null != msg) {
            //            Log.w(mTag, msg, tr);
            Logger.t(mTag).w(msg);
        }
    }

    /**
     */
    public static void e(Object msg) {
        if (mDebuggable >= LEVEL_ERROR) {
            //            Log.e(mTag, msg + "");
            Logger.t(mTag).e(msg + "");
        }
    }

    public static void e(String message, Object... args) {
        if (mDebuggable >= LEVEL_ERROR) {
            //            Log.e(mTag, msg + "");
            Logger.t(mTag).e(message,args);
        }
    }

    /**
     *
     * @param jsonObject
     */
    public static void json(@NonNull JSONObject jsonObject) {
        Logger.json(jsonObject.toString());
    }


    /**
     *
     * @param xmlObject
     */
    public static void Xml(@Nullable Object xmlObject) {
        if (xmlObject != null) {
            Logger.xml(xmlObject.toString());
        } else {
            LogUtils.e("xml()参数 xmlObject 参数为null!,请检查!");
        }
    }

    // 更改tag
    public static void init(String mTag) {
        Logger.init(mTag);// 测试环境
    }

    // 设置日志输出基本
    public static void logLevel(LogLevel logLevel) {
        Logger.init(mTag).logLevel(logLevel);//正式环境
    }

    /**
     *
     * @param exception
     * @param msg
     */
    public static void e(Exception exception, Object msg) {
        Logger.e(exception, msg + "");
        Logger.t(mTag).e("mmmm", msg);
    }

    /**
     *
     * @param msg
     */
    public static void dd(Object msg) {
        Logger.d(msg);
        Logger.t(mTag).d("mmmm", msg);
    }

    /**
     *
     * @param msg
     */
    public static void ee(Object msg) {
        Logger.t(mTag).e(msg + "");
    }

    /**
     *
     */
    public static void e(@NonNull Throwable tr) {
        if (mDebuggable >= LEVEL_ERROR) {
            //            Log.e(mTag, tr.getMessage(), tr);
            Logger.t(mTag).e(tr, tr.getMessage());
        }
    }

    /**
     */
    public static void e(@Nullable String msg, Throwable tr) {
        if (mDebuggable >= LEVEL_ERROR && null != msg) {
            //            Log.e(mTag, msg, tr);
            Logger.t(mTag).e(tr, msg);
        }
    }

    /**
     */
    public static void elapsed(String msg) {
        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - mTimestamp;
        mTimestamp = currentTime;
        e("[Elapsed：" + elapsedTime + "]" + msg);
    }


    public static void setmDebuggable(int mDebuggable) {
        LogUtils.mDebuggable = mDebuggable;
    }

    public static int getmDebuggable() {
        return mDebuggable;
    }
}
