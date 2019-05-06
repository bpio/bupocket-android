package com.bupocket.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.bupocket.R;
import com.bupocket.enums.CurrencyTypeEnum;
import com.bupocket.enums.ExceptionEnum;
import com.qmuiteam.qmui.widget.dialog.QMUIBottomSheet;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.tencent.mm.opensdk.modelbiz.WXLaunchMiniProgram;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.bupocket.common.Constants.WeChat_APPID;
import static com.bupocket.common.Constants.XB_YOUPING_USERNAME;

/**
 * 通用工具类.
 */
public class CommonUtil {
    private static SecureRandom random = new SecureRandom();

    public static final Pattern MAIL_PATTERN = Pattern.compile("^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$");

    public static final Pattern MOBILE_PATTERN = Pattern.compile("^1[3|4|5|7|8][0-9]\\d{8}$");

    public static final Pattern NAME_PATTERN = Pattern.compile("^[\\u4E00-\\u9FBF][\\u4E00-\\u9FBF(.|·)]{0,13}[\\u4E00-\\u9FBF]$");

    public static final Pattern NICKNAME_PATTERN = Pattern.compile("^((?!\\d{21})[\\u4E00-\\u9FBF(.|·)|0-9A-Za-z_]){1,20}$");
//    public static final Pattern PASSWORD_PATTERN = Pattern.compile("[^ \\f\\n\\r\\t\\v]{6,30}$");

    public static final Pattern ADDRESS_DESCRIBE_PATTERN = Pattern.compile(".{0,30}$");

    public static final Pattern PASSWORD_PATTERN = Pattern.compile(".{6,30}$");

    public static final Pattern CODE_PATTERN = Pattern.compile("^0\\d{2,4}$");

    public static final Pattern POSTCODE_PATTERN = Pattern.compile("^\\d{6}$");

    public static final Pattern ID_PATTERN = Pattern.compile("^\\d{6}(\\d{8}|\\d{11})[0-9a-zA-Z]$");

    public static final Pattern BANK_CARD_PATTERN = Pattern.compile("^\\d{16,30}$");

    /**
     * 生成6位随机数字, 用于手机短信验证码.
     *
     * @return 6位随机数字
     */
    public static int random() {
        int x = Math.abs(random.nextInt(899999));

        return x + 100000;
    }

    public static boolean isBU(String str) {
        Pattern pattern = Pattern.compile("^(([0-9]+\\.[0-9]*[1-9][0-9]*)|([0-9]*[1-9][0-9]*\\.[0-9]+)|([0-9]*[1-9][0-9]*))$");
        Matcher match = pattern.matcher(str);
        if (match.matches() == false) {
            return false;
        } else {
            return true;
        }
    }


    /**
     * 对url字符串进行编码.
     *
     * @param url 要编码的url字符串
     * @return 编码后的字符串
     */
    public static String urlEncoder(String url) {
        if (isEmpty(url)) {
            return null;
        }
        try {
            return java.net.URLEncoder.encode(url, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 对url字符串进行解码.
     *
     * @param url 要解码的url字符串
     * @return 解码后的字符串
     */
    public static String urlDecoder(String url) {
        if (isEmpty(url)) {
            return null;
        }
        try {
            return URLDecoder.decode(url, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 验证字符串是不是邮箱.
     *
     * @param email 要验证的邮箱
     * @return 是否正确邮箱
     */
    public static boolean validateEmail(String email) {
        if (isEmpty(email)) {
            return false;
        }
        Matcher m = MAIL_PATTERN.matcher(email);
        return m.matches();
    }

    /**
     * 验证字符串是不是手机号.
     *
     * @param mobile 要验证的手机号
     * @return 是否正确手机号
     */
    public static boolean validateMobile(String mobile) {
        if (isEmpty(mobile)) {
            return false;
        }
        Matcher m = MOBILE_PATTERN.matcher(mobile);
        return m.matches();
    }


    /**
     * 验证姓名是否有效.
     *
     * @param name 要验证的姓名
     * @return 是否正确姓名
     */
    public static boolean validateName(String name) {
        if (isEmpty(name) || name.replaceAll("[^.·]", "").length() > 1) {
            return false;
        }
        Matcher m = NAME_PATTERN.matcher(name);
        return m.matches();
    }

    /**
     * 验证昵称是否有效.
     *
     * @param nickname 要验证的昵称
     * @return 是否正确昵称
     */
    public static boolean validateNickname(String nickname) {

        //规则
        if (isEmpty(nickname)) {
            return false;
        }
        Matcher m = NICKNAME_PATTERN.matcher(nickname);
        boolean flag = m.matches();
        return m.matches();
    }


    public static boolean validateAddressDescribe(String describe) {
        Matcher m = ADDRESS_DESCRIBE_PATTERN.matcher(describe);
        boolean flag = m.matches();
        return m.matches();
    }

    /**
     * 验证密码格式是否有效.
     *
     * @param password 要验证的密码
     * @return 是否正确密码格式
     */
    public static boolean validatePassword(String password) {
        if (isEmpty(password)) {
            return false;
        }
        Matcher m = PASSWORD_PATTERN.matcher(password);
        return m.matches();
    }

    /**
     * 验证区号是否有效.
     *
     * @param code 要验证的区号
     * @return 是否正确身份证
     */
    public static boolean validateCode(String code) {
        if (isEmpty(code)) {
            return false;
        }
        Matcher m = CODE_PATTERN.matcher(code);
        return m.matches();
    }

    /**
     * 验证邮政编码是否有效.
     *
     * @param postcode 要验证的邮政编码
     * @return 是否正确邮政编码
     */
    public static boolean validatePostcode(String postcode) {
        if (isEmpty(postcode)) {
            return false;
        }
        Matcher m = POSTCODE_PATTERN.matcher(postcode);
        return m.matches();
    }

    /**
     * 验证银行卡是否有效.
     *
     * @param bankCardNumber 要验证的银行卡号
     * @return 是否正确银行卡号
     */
    public static boolean validateBankCardNumber(String bankCardNumber) {
        if (isEmpty(bankCardNumber)) {
            return false;
        }
        Matcher m = BANK_CARD_PATTERN.matcher(bankCardNumber);
        return m.matches();
    }


    /**
     * 获取当前系统时间,以java.sql.Timestamp类型返回.
     *
     * @return 当前时间
     */
    public static Timestamp getTimestamp() {
        Timestamp d = new Timestamp(System.currentTimeMillis());
        return d;
    }

    /**
     * 生成32位编码,不含横线
     *
     * @return uuid串
     */
    public static String getUUID() {
        String uuid = UUID.randomUUID().toString().trim().replaceAll("-", "");
        return uuid.toUpperCase();
    }


    /**
     * 通过身份证获取性别
     *
     * @param idNumber 身份证号
     * @return 返回性别, 0 保密 , 1 男 2 女
     */
    public static Integer getGenderByIdNumber(String idNumber) {

        int gender = 0;

        if (idNumber.length() == 15) {
            gender = Integer.parseInt(String.valueOf(idNumber.charAt(14))) % 2 == 0 ? 2 : 1;
        } else if (idNumber.length() == 18) {
            gender = Integer.parseInt(String.valueOf(idNumber.charAt(16))) % 2 == 0 ? 2 : 1;
        }

        return gender;

    }

    /**
     * 通过身份证获取生日
     *
     * @param idNumber 身份证号
     * @return 返回生日, 格式为 yyyy-MM-dd 的字符串
     */
    public static String getBirthdayByIdNumber(String idNumber) {

        String birthday = "";

        if (idNumber.length() == 15) {
            birthday = "19" + idNumber.substring(6, 8) + "-" + idNumber.substring(8, 10) + "-" + idNumber.substring(10, 12);
        } else if (idNumber.length() == 18) {
            birthday = idNumber.substring(6, 10) + "-" + idNumber.substring(10, 12) + "-" + idNumber.substring(12, 14);
        }

        return birthday;

    }


    /**
     * 通过身份证获取年龄
     *
     * @param idNumber 身份证号
     * @return 返回年龄
     */
    public static Integer getAgeByIdNumber(String idNumber) {

        String birthString = getBirthdayByIdNumber(idNumber);
        if (isEmpty(birthString)) {
            return 0;
        }

        return getAgeByBirthString(birthString);

    }

    /**
     * 通过身份证获取年龄
     *
     * @param idNumber     身份证号
     * @param isNominalAge 是否按元旦算年龄，过了1月1日加一岁 true : 是 false : 否
     * @return 返回年龄
     */
    public static Integer getAgeByIdNumber(String idNumber, boolean isNominalAge) {

        String birthString = getBirthdayByIdNumber(idNumber);
        if (isEmpty(birthString)) {
            return 0;
        }

        return getAgeByBirthString(birthString, isNominalAge);

    }

    /**
     * 通过生日日期获取年龄
     *
     * @param birthDate 生日日期
     * @return 返回年龄
     */
    public static Integer getAgeByBirthDate(Date birthDate) {

        return getAgeByBirthString(new SimpleDateFormat("yyyy-MM-dd").format(birthDate));

    }


    /**
     * 通过生日字符串获取年龄
     *
     * @param birthString 生日字符串
     * @return 返回年龄
     */
    public static Integer getAgeByBirthString(String birthString) {

        return getAgeByBirthString(birthString, "yyyy-MM-dd");

    }

    /**
     * 通过生日字符串获取年龄
     *
     * @param birthString  生日字符串
     * @param isNominalAge 是否按元旦算年龄，过了1月1日加一岁 true : 是 false : 否
     * @return 返回年龄
     */
    public static Integer getAgeByBirthString(String birthString, boolean isNominalAge) {

        return getAgeByBirthString(birthString, "yyyy-MM-dd", isNominalAge);

    }

    /**
     * 通过生日字符串获取年龄
     *
     * @param birthString 生日字符串
     * @param format      日期字符串格式,为空则默认"yyyy-MM-dd"
     * @return 返回年龄
     */
    public static Integer getAgeByBirthString(String birthString, String format) {
        return getAgeByBirthString(birthString, "yyyy-MM-dd", false);
    }


    /**
     * 通过生日字符串获取年龄
     *
     * @param birthString  生日字符串
     * @param format       日期字符串格式,为空则默认"yyyy-MM-dd"
     * @param isNominalAge 是否按元旦算年龄，过了1月1日加一岁 true : 是 false : 否
     * @return 返回年龄
     */
    public static Integer getAgeByBirthString(String birthString, String format, boolean isNominalAge) {

        int age = 0;

        if (isEmpty(birthString)) {
            return age;
        }

        if (isEmpty(format)) {
            format = "yyyy-MM-dd";
        }

        try {

            Calendar birthday = Calendar.getInstance();
            Calendar today = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            birthday.setTime(sdf.parse(birthString));
            age = today.get(Calendar.YEAR) - birthday.get(Calendar.YEAR);
            if (!isNominalAge) {
                if (today.get(Calendar.MONTH) < birthday.get(Calendar.MONTH) ||
                        (today.get(Calendar.MONTH) == birthday.get(Calendar.MONTH) &&
                                today.get(Calendar.DAY_OF_MONTH) < birthday.get(Calendar.DAY_OF_MONTH))) {
                    age = age - 1;
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return age;

    }

    /**
     * 手机号中间四位替换成星号
     *
     * @param mobile
     * @return
     */
    public static String maskMobile(String mobile) {
        if (validateMobile(mobile)) {
            return mobile.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
        }
        return mobile;
    }

    /**
     * 手机号中间四位自定义替换
     *
     * @param mobile
     * @param transCode 中间四位目标值 如GXJF 将136GXJF1111
     * @return
     */
    public static String maskMobile(String mobile, String transCode) {
        if (validateMobile(mobile)) {
            transCode = isEmpty(transCode) ? "****" : transCode;
            return mobile.replaceAll("(\\d{3})\\d{4}(\\d{4})", String.format("$1%s$2", transCode));
        }
        return mobile;
    }

    /**
     * 邮箱地址加星号
     *
     * @param email
     * @return
     */
    public static String maskEmail(String email) {
        if (validateEmail(email)) {
            String userName = email.substring(0, email.indexOf("@"));
            int len = userName.length();
            if (len >= 5) {
                int total = len - 3;
                int half = total / 2;
                int start = half;
                int end = len - half;
                if (total % 2 != 0) {
                    end = end - 1;
                }
                StringBuilder sb = new StringBuilder(email);
                for (int i = start; i < end; i++) {
                    sb.setCharAt(i, '*');
                }
                return sb.toString();
            }
        }
        return email;
    }

    /**
     * 账号中间四位自定义替换
     *
     * @param account
     * @return
     */
    public static String maskTradeAccount(String account) {
        return account.replaceAll("(\\d{7})\\d*(\\d{4})", "$1****$2");
    }


    /**
     * 验证是否为日期
     *
     * @param date
     * @return
     */
    public static boolean validateDate(String date) {
        boolean convertSuccess = true;
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
        try {
            format.setLenient(false);
            format.parse(date);
        } catch (ParseException e) {
            convertSuccess = false;
        }
        return convertSuccess;
    }

    /**
     * 获取时间戳,作为递增的ID
     */
    private static final Lock lock = new ReentrantLock();   //锁对象

    public static long getUniqueLong() {
        long l;
        lock.lock();
        try {
            l = System.currentTimeMillis();
        } finally {
            lock.unlock();
        }
        return l;
    }

    /**
     * 解析出url参数中的键值对
     * 如 "index.jsp?Action=del&id=123"，解析出Action:del,id:123存入map中
     *
     * @param URL url地址
     * @return url请求参数部分
     */
    public static String getUrlParams(String URL, String key) {
        Map<String, String> mapRequest = new HashMap<String, String>();
        String[] arrSplit = null;
        String strUrlParam = null;
        java.net.URL aURL = null;
        try {
            aURL = new URL(URL);
            strUrlParam = aURL.getQuery();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        if (strUrlParam == null) {
            return "";
        }
        arrSplit = strUrlParam.split("[&]");
        for (String strSplit : arrSplit) {
            String[] arrSplitEqual = null;
            arrSplitEqual = strSplit.split("[=]");
            if (arrSplitEqual.length > 1) {
                mapRequest.put(arrSplitEqual[0], arrSplitEqual[1]);
            } else {
                if (!isEmpty(arrSplitEqual[0])) {
                    mapRequest.put(arrSplitEqual[0], "");
                }
            }
        }
        if (mapRequest.containsKey(key)) {
            try {
                return URLDecoder.decode(mapRequest.get(key), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (Exception e) {

            }
        }
        return "";
    }


    /**
     * 生成随机密码
     *
     * @param pwd_len 生成的密码的总长度
     * @return 密码的字符串
     */
    public static String genRandomNum(int pwd_len) {
        // 35是因为数组是从0开始的，26个字母+10个数字
        final int maxNum = 36;
        int i; // 生成的随机数
        int count = 0; // 生成的密码的长度
        char[] str = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k',
                'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w',
                'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
        StringBuffer pwd = new StringBuffer("");
        Random r = new Random();
        while (count < pwd_len) {
            // 生成随机数，取绝对值，防止生成负数，
            i = Math.abs(r.nextInt(maxNum)); // 生成的数最大为36-1
            if (i >= 0 && i < str.length) {
                pwd.append(str[i]);
                count++;
            }
        }
        return pwd.toString();
    }

    public static int packageCode(Context context) {
        PackageManager manager = context.getPackageManager();
        int code = 0;
        try {
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            code = info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return code;
    }

    public static String packageName(Context context) {
        PackageManager manager = context.getPackageManager();
        String name = null;
        try {
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            name = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return name;
    }

    private static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }

    public static boolean isNull(String str) {
        if (isEmpty(str) || str == "") {
            return true;
        }
        return false;
    }

    public static String getUniqueId(Context context) {
        String androidID = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        String id = androidID + Build.SERIAL;
        try {
            return toMD5(id);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return id;
        }
    }


    private static String toMD5(String text) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        byte[] digest = messageDigest.digest(text.getBytes());

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < digest.length; i++) {
            int digestInt = digest[i] & 0xff;
            String hexString = Integer.toHexString(digestInt);
            if (hexString.length() < 2) {
                sb.append(0);
            }
            sb.append(hexString);
        }
        return sb.toString();
    }

    public static Bitmap base64ToBitmap(String base64Data) throws IllegalArgumentException {
        base64Data = base64Data.split(",")[1];
        byte[] bytes = Base64.decode(base64Data, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    public static String formatDouble(String value) {
        return new BigDecimal(value).setScale(2, BigDecimal.ROUND_HALF_UP).toString();
    }

    /*
     * add asset suffix
     */
    public static String addSuffix(String originalStr, String suffix) {
        return originalStr + " " + suffix;
    }

    /*
     *check number format
     */
    public static Boolean checkSendAmountDecimals(String srcAmount, String decimals) {
        BigDecimal value = BigDecimal.valueOf(DecimalCalculate.mul(Double.parseDouble(srcAmount), Math.pow(10, Double.parseDouble(decimals))));
//        System.out.println(value.toPlainString());
//        System.out.println(value.remainder(BigDecimal.ONE));
        if (value.remainder(BigDecimal.ONE).compareTo(BigDecimal.ZERO) == 0) {
            return false;
        } else {
            return true;
        }
    }

    public static String calculateMinSendAmount(String decimalsStr) {
        BigDecimal one = new BigDecimal(1);
        BigDecimal ten = new BigDecimal(10);
        return one.divide(ten.pow(Integer.valueOf(decimalsStr))).toPlainString();
    }

    public static String rvZeroAndDot(String s) {
        if (s.isEmpty()) {
            return null;
        }
        if (s.indexOf(".") > 0) {
            //去掉多余的0
            s = s.replaceAll("0+?$", "");
            //如最后一位是.则去掉
            s = s.replaceAll("[.]$", "");
        }
        return s;
    }

    public static boolean checkIsBase64(String str) {
        String base64Pattern = "^([A-Za-z0-9+/]{4})*([A-Za-z0-9+/]{4}|[A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{2}==)$";
        return Pattern.matches(base64Pattern, str);
    }

    /**
     * 数字添加千分符
     *
     * @param str
     * @return
     */
    public static String thousandSeparator(String str) {
        DecimalFormat df = new DecimalFormat("###,###.########");
        return df.format(new BigDecimal(str));
    }

    public static Boolean checkAmount(String issueAmount, String decimals) {
        try {
            Long.parseLong(new BigDecimal(issueAmount).multiply(new BigDecimal(Math.pow(10, Double.parseDouble(decimals)))).setScale(0).toPlainString());
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    public static String addCurrencySymbol(String assetAmount, String currencyType) {
        for (CurrencyTypeEnum currencyTypeEnum : CurrencyTypeEnum.values()) {
            if (currencyTypeEnum.getName().equals(currencyType)) {
                return "≈" + currencyTypeEnum.getSymbol() + " " + assetAmount;
            }
        }
        return null;
    }

    /**
     * 显示软键盘
     */
    public static void showInputMethod(Context context, View view) {
        if (context == null || view == null) {
            return;
        }

        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * 关闭软键盘
     */
    public static boolean hideInputMethod(Context context, View view) {
        if (context == null || view == null) {
            return false;
        }

        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null && imm.isActive()) {
            return imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        return false;
    }


    private static String getAccountBPData(Context context, boolean isWhetherIdentityWallet, String currentWalletAddress) {

        SharedPreferencesHelper sharedPreferencesHelper = new SharedPreferencesHelper(context, "buPocket");
        String accountBPData = null;
        if (isWhetherIdentityWallet) {
            accountBPData = sharedPreferencesHelper.getSharedPreference("BPData", "").toString();
        } else {
            accountBPData = sharedPreferencesHelper.getSharedPreference(currentWalletAddress + "-BPdata", "").toString();
        }
        return accountBPData;
    }

    public static void showMessageDialog(Context mContext, String notice) {
        int mCurrentDialogStyle = com.qmuiteam.qmui.R.style.QMUI_Dialog;
        new QMUIDialog.MessageDialogBuilder(mContext)
                .setMessage(notice)
                .addAction(R.string.i_knew_btn_txt, new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        dialog.dismiss();
                    }
                })
                .create(mCurrentDialogStyle).show();

    }

    /**
     * @param mContext
     * @param notice   error massage
     * @param code     error code
     */
    public static void showMessageDialog(Context mContext, String notice, String code) {
        String errMsg = byCodeToMsg(mContext, code);
        if (!errMsg.isEmpty()) {
            notice = errMsg;
        }

        showMessageDialog(mContext, notice);

    }


    public static void showMessageDialog(Context mContext, int notice) {
        showMessageDialog(mContext, mContext.getResources().getString(notice));
    }

    /**
     * get  error massage
     *
     * @param mContext
     * @param code     error code
     * @return error massage
     */
    public static String byCodeToMsg(Context mContext, String code) {
        ExceptionEnum byValue = ExceptionEnum.getByValue(code);
        if (byValue == null) {
            return "";
        }
        return mContext.getResources().getString(byValue.getMsg());
    }


    /**
     * @param
     * @return true==single
     */
    public static boolean isSingle(int num) {
        if (num == 0 || num == 1) {
            return true;
        }
        return false;
    }

    public static boolean isSingle(String num) {
        return isSingle(Integer.parseInt(num));
    }


    public static String format(String num) {
        String format = "";
        try {
            int num1 = Integer.parseInt(num);
            format = DecimalFormat.getNumberInstance().format(num1);
            if (TextUtils.isEmpty(format)) {
                return "0";
            }
        } catch (Exception e) {
            return "0";
        }
        return format;
    }

    public static boolean goWeChat(Context context, String appid, String username) {

        IWXAPI api = WXAPIFactory.createWXAPI(context, WeChat_APPID);
        WXLaunchMiniProgram.Req req = new WXLaunchMiniProgram.Req();
        req.userName = XB_YOUPING_USERNAME;
        req.miniprogramType = WXLaunchMiniProgram.Req.MINIPTOGRAM_TYPE_RELEASE;
        boolean isSend = api.sendReq(req);
        if (!isSend){
            showMessageDialog(context,"请下载微信后使用");
        }
        return isSend;
    }


    public static String setRatio(int support, int total) {
        String strRatio = "0";
        double ratio = ((double) support / total) * 100;
        if (support == 0) {
            strRatio = "0";
        } else if (support == total) {
            strRatio = "100";
        } else {
            DecimalFormat df = new DecimalFormat("#0.00");
            strRatio = df.format(ratio);
        }
        return strRatio + "%";
    }


    public static void TransactionConfirmSheet(Context context, String transactionDetail, String sourceAddress, String destAddress, String amount, String fee) {
//        final QMUIBottomSheet qmuiBottomSheet = new QMUIBottomSheet(context);
//        qmuiBottomSheet.setContentView(qmuiBottomSheet.getLayoutInflater().inflate(R.layout.view_transfer_confirm, null));
//
//        TextView mTransactionDetailTv = qmuiBottomSheet.findViewById(R.id.transactionDetailTv);
//        mTransactionDetailTv.setText(transactionDetail);
//        TextView mDestAddressTv = qmuiBottomSheet.findViewById(R.id.destAddressTv);
//        TextView mDestAddressTvHint = qmuiBottomSheet.findViewById(R.id.destAddressTvHint);
//        if (TextUtils.isEmpty(destAddress)) {
//            mDestAddressTv.setVisibility(View.GONE);
//            mDestAddressTvHint.setVisibility(View.GONE);
//        } else {
//            mDestAddressTv.setVisibility(View.VISIBLE);
//            mDestAddressTvHint.setVisibility(View.VISIBLE);
//            if (TextUtils.isEmpty(accountTag)) {
//                mDestAddressTv.setText(destAddress);
//            } else {
//                mDestAddressTv.setText(destAddress.concat(accountTag));
//            }
//
//        }
//
//        TextView mTxFeeTv = qmuiBottomSheet.findViewById(R.id.txFeeTv);
//        mTxFeeTv.setText(String.valueOf(scanTxFee));
//
//
//        qmuiBottomSheet.findViewById(R.id.detailBtn).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                qmuiBottomSheet.findViewById(R.id.confirmLl).setVisibility(View.GONE);
//                qmuiBottomSheet.findViewById(R.id.confirmDetailsLl).setVisibility(View.VISIBLE);
//            }
//        });
//
//
//        TextView mSourceAddressTv = qmuiBottomSheet.findViewById(R.id.sourceAddressTv);
//        mSourceAddressTv.setText(sourceAddress);
//        TextView mDetailsDestAddressTv = qmuiBottomSheet.findViewById(R.id.detailsDestAddressTv);
//        TextView mDetailsDestAddressTvHint = qmuiBottomSheet.findViewById(R.id.detailsDestAddressTvHint);
//        if (TextUtils.isEmpty(destAddress)) {
//            mDetailsDestAddressTv.setVisibility(View.GONE);
//            mDetailsDestAddressTvHint.setVisibility(View.GONE);
//        } else {
//            mDetailsDestAddressTv.setVisibility(View.VISIBLE);
//            mDetailsDestAddressTvHint.setVisibility(View.VISIBLE);
//            mDetailsDestAddressTv.setText(destAddress);
//        }
//
//
//        TextView mDetailsAmountTv = qmuiBottomSheet.findViewById(R.id.detailsAmountTv);
//        mDetailsAmountTv.setText(CommonUtil.thousandSeparator(transactionAmount));
//        TextView mDetailsTxFeeTv = qmuiBottomSheet.findViewById(R.id.detailsTxFeeTv);
//        mDetailsTxFeeTv.setText(String.valueOf(scanTxFee));
//        TextView mTransactionParamsTv = qmuiBottomSheet.findViewById(R.id.transactionParamsTv);
//        mTransactionParamsTv.setText(transactionParams);
//
//        // title view listener
//        qmuiBottomSheet.findViewById(R.id.goBackBtn).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                qmuiBottomSheet.findViewById(R.id.confirmLl).setVisibility(View.VISIBLE);
//                qmuiBottomSheet.findViewById(R.id.confirmDetailsLl).setVisibility(View.GONE);
//            }
//        });
//        qmuiBottomSheet.findViewById(R.id.cancelBtn).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                qmuiBottomSheet.dismiss();
//            }
//        });
//        qmuiBottomSheet.findViewById(R.id.detailsCancelBtn).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                qmuiBottomSheet.dismiss();
//            }
//        });
//        qmuiBottomSheet.findViewById(R.id.sendConfirmBtn).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                qmuiBottomSheet.dismiss();
//                confirmTransaction(contentDto);
//            }
//        });
//        qmuiBottomSheet.show();
    }


    public static void setExpiryTime(String expiryTime, Context context) {
        if (!TextUtils.isEmpty(expiryTime)) {
            String[] strings = TimeUtil.time_mmss(Long.parseLong(expiryTime) - System.currentTimeMillis());
            if (strings[0].isEmpty()) {
                @SuppressLint("StringFormatMatches") String format = String.format(context.getString(R.string.error_1011_s, strings[1] + ""));
                CommonUtil.showMessageDialog(context, format);
            } else {
                @SuppressLint("StringFormatMatches") String format = String.format(context.getString(R.string.error_1011_m, strings[0] + "", strings[1] + ""));
                CommonUtil.showMessageDialog(context, format);
            }
        }
    }

}