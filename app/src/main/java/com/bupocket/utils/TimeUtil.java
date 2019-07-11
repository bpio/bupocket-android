package com.bupocket.utils;

import android.content.Context;
import android.content.IntentSender;
import android.content.res.Resources;
import android.icu.text.TimeZoneFormat;
import android.text.TextUtils;
import android.view.TextureView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.bupocket.R;
import com.squareup.okhttp.internal.framed.Variant;

public class TimeUtil {


    private static long second = 1000;
    private static long minute = 1000 * 60;
    private static long hour = minute * 60;
    private static long day = hour * 24;
    private static long halfamonth = day * 15;
    private static long month = day * 30;
    public static final String TIME_TYPE = "yyyy-MM-dd HH:mm:ss";
    public static final String TIME_TYPE_YYYYY_MM_DD = "yyyy-MM-dd HH:mm:ss";
    public static final String TIME_TYPE_ONE = "yyyy-MM-dd";


    // string类型转换为long类型
    // strTime要转换的String类型的时间
    // formatType时间格式
    // strTime的时间格式和formatType的时间格式必须相同
    public static long stringToLong(String strTime, String formatType)
            throws ParseException {
        Date date = stringToDate(strTime, formatType); // String类型转成date类型
        if (date == null) {
            return 0;
        } else {
            long currentTime = dateToLong(date); // date类型转成long类型
            return currentTime;
        }
    }

    // date类型转换为long类型
    // date要转换的date类型的时间
    public static long dateToLong(Date date) {
        return date.getTime();
    }

    // string类型转换为date类型
    // strTime要转换的string类型的时间，formatType要转换的格式yyyy-MM-dd HH:mm:ss//yyyy年MM月dd日
    // HH时mm分ss秒，
    // strTime的时间格式必须要与formatType的时间格式相同
    public static Date stringToDate(String strTime, String formatType)
            throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat(formatType);
        Date date = null;
        date = formatter.parse(strTime);
        return date;
    }

    public static String getDateDiff(long dateTimeStamp, Context context) {
        Resources resources = context.getResources();
        dateTimeStamp = Long.parseLong((dateTimeStamp + "").substring(0, 13));
        String result;
        long now = new Date().getTime();
        long diffValue = now - dateTimeStamp;
        long monthC = diffValue / month;
        long weekC = diffValue / (7 * day);
        long dayC = diffValue / day;
        long hourC = diffValue / hour;
        long minC = diffValue / minute;
        long secC = diffValue / second;


        if (monthC >= 1 || weekC >= 1) {
            SimpleDateFormat sdf = new SimpleDateFormat(TIME_TYPE);
            sdf.setLenient(false);
            result = sdf.format(new Date(dateTimeStamp));
            return result;
        } else if (dayC > 1) {
            return Integer.parseInt(dayC + "") + " " + resources.getString(R.string.time_day_ago_s);
        } else if (dayC == 1) {
            return Integer.parseInt(dayC + "") + " " + resources.getString(R.string.time_day_ago);
        } else if (hourC >= 1) {
            return Integer.parseInt(hourC + "") + " " + resources.getString(R.string.time_hour_ago_s);
        } else if (hourC == 1) {
            return Integer.parseInt(hourC + "") + " " + resources.getString(R.string.time_hour_ago);
        } else if (minC > 1) {
            return Integer.parseInt(minC + "") + " " + resources.getString(R.string.time_minute_ago_s);
        } else if (minC == 1) {
            return Integer.parseInt(minC + "") + " " + resources.getString(R.string.time_minute_ago);
        } else if (secC > 1) {
            return Integer.parseInt(secC + "") + " " + resources.getString(R.string.time_sec_ago_s);
        } else if (secC == 1) {
            return Integer.parseInt(secC + "") + " " + resources.getString(R.string.time_sec_ago);
        } else {
            result = resources.getString(R.string.time_just_now);
            return result;
        }
    }

    public static String timeStamp2Date(String seconds) {
        return timeStamp2Date(seconds, TIME_TYPE);
    }

    public static String timeStamp2Date(String seconds, String format) {

        if (TextUtils.isEmpty(seconds)) {
            return "";
        }
        SimpleDateFormat sdf = null;
        Long date = null;
        try {
            if (seconds == null || seconds.isEmpty() || seconds.equals("null")) {
                return "";
            }
            seconds = seconds.substring(0, 10) + "000";
            if (format == null || format.isEmpty()) format = TIME_TYPE;
            sdf = new SimpleDateFormat(format);
            date = Long.valueOf(seconds);
        } catch (Exception e) {
            return "";
        }

        return sdf.format(new Date(date));
    }

    public static String[] time_mmss(long time) {

        String format = "mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        String[] split = sdf.format(new Date(time)).toString().split(":");
        split[0] = split[0].replace("0", "");
        split[1] = String.valueOf(Integer.parseInt(split[1]));
        return split;
    }


    public static long getCurrentTimeMillis() {
        return System.currentTimeMillis();
    }


    /**
     * @param inputTime
     * @return true inputTime
     */
    public static boolean judgeTime(long inputTime) {
        if (inputTime > getCurrentTimeMillis()) {
            return true;
        }
        return false;
    }

}