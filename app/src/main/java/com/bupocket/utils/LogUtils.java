package com.bupocket.utils;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.orhanobut.logger.LogLevel;
import com.orhanobut.logger.Logger;

import org.json.JSONObject;


/***
 * 描述: log 工具类
 *
 * @author zhangg
 * @下午10:17:04
 */
public class LogUtils {
    /**
     * 日志输出级别NONE
     */
    public static final int LEVEL_NONE = 0;//上线级别

    /**
     * 日志输出级别V
     */
    private static final int LEVEL_VERBOSE = 1;

    /**
     * 日志输出级别D
     */
    private static final int LEVEL_DEBUG = 2;

    /**
     * 日志输出级别I
     */
    private static final int LEVEL_INFO = 3;

    /**
     * 日志输出级别W
     */
    private static final int LEVEL_WARN = 4;

    /**
     * 日志输出级别E
     */
    private static final int LEVEL_ERROR = 5;

    /**
     * 日志输出时的TAG
     */
    private static final String mTag = "mmmm";

    /* 是否允许输出log LEVEL_NONE时候不输出日志 */
    private static int mDebuggable = LEVEL_ERROR;

    /**
     * 用于记时的变量
     */
    private static long mTimestamp = 0;

    /**
     * 以级别为 d 的形式输出LOG
     */
    public static void v(Object msg) {
        if (mDebuggable >= LEVEL_VERBOSE) {
            //Log.v(mTag, msg + "");
            Logger.t(mTag).v(msg + "");
        }
    }

    /**
     * 以级别为 d 的形式输出LOG
     */
    public static void d(Object msg) { //(可以list,map等数据类型)
        if (mDebuggable >= LEVEL_DEBUG) {
            //Log.d(mTag, msg + "");
            Logger.t(mTag).d(msg + "");
            //Logger.d(msg);
        }
    }

    /**
     * 以级别为 i 的形式输出LOG
     */
    public static void i(Object msg) {
        if (mDebuggable >= LEVEL_INFO) {
            //Log.i(mTag, msg + "");
            Logger.t(mTag).i(msg + "");
        }
    }

    /**
     * 以级别为 w 的形式输出LOG
     */
    public static void w(Object msg) {
        if (mDebuggable >= LEVEL_WARN) {
            //Log.w(mTag, msg + "");
            Logger.t(mTag).w(msg + "");
        }
    }

    /**
     * 以级别为 w 的形式输出Throwable
     */
    public static void w(Throwable tr) {
        if (mDebuggable >= LEVEL_WARN) {
            Logger.t(mTag).w(mTag, "", tr);
        }
    }

    /**
     * 以级别为 w 的形式输出LOG信息和Throwable
     */
    public static void w(@Nullable String msg, Throwable tr) {
        if (mDebuggable >= LEVEL_WARN && null != msg) {
            //            Log.w(mTag, msg, tr);
            Logger.t(mTag).w(msg);
        }
    }

    /**
     * 以级别为 e 的形式输出LOG
     */
    public static void e(Object msg) {
        if (mDebuggable >= LEVEL_ERROR) {
            //            Log.e(mTag, msg + "");
            Logger.t(mTag).e(msg + "");
        }
    }

    /**
     * 格式化
     *
     * @param jsonObject
     */
    public static void json(@NonNull JSONObject jsonObject) {
        Logger.json(jsonObject.toString());
    }


    /**
     * 格式化
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
     * 格式化
     *
     * @param exception
     * @param msg
     */
    public static void e(Exception exception, Object msg) {
        Logger.e(exception, msg + "");
        Logger.t(mTag).e("mmmm", msg);
    }

    /**
     * 格式化
     *
     * @param msg
     */
    public static void dd(Object msg) {
        Logger.d(msg);
        Logger.t(mTag).d("mmmm", msg);
    }

    /**
     * 格式化
     *
     * @param msg
     */
    public static void ee(Object msg) {
        Logger.t(mTag).e(msg + "");
    }

    /**
     * 以级别为 e 的形式输出Throwable
     */
    public static void e(@NonNull Throwable tr) {
        if (mDebuggable >= LEVEL_ERROR) {
            //            Log.e(mTag, tr.getMessage(), tr);
            Logger.t(mTag).e(tr, tr.getMessage());
        }
    }

    /**
     * 以级别为 e 的形式输出LOG信息和Throwable
     */
    public static void e(@Nullable String msg, Throwable tr) {
        if (mDebuggable >= LEVEL_ERROR && null != msg) {
            //            Log.e(mTag, msg, tr);
            Logger.t(mTag).e(tr, msg);
        }
    }

    /**
     * 以级别为 e 的形式输出msg信息,附带时间戳，用于输出一个时间段结束点* @param msg 需要输出的msg
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
