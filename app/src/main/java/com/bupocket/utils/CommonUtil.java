package com.bupocket.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
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
import android.widget.ImageView;
import android.widget.Toast;

import com.bupocket.R;
import com.bupocket.adaptor.NodeSettingAdapter;
import com.bupocket.common.Constants;
import com.bupocket.common.ConstantsType;
import com.bupocket.enums.CurrencyTypeEnum;
import com.bupocket.model.NodeAddressModel;
import com.google.gson.Gson;
import com.tencent.mm.opensdk.modelbiz.WXLaunchMiniProgram;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.io.IOException;
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

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.bupocket.common.Constants.WeChat_APPID;
import static com.bupocket.common.Constants.XB_YOUPING_USERNAME;

/**
 * common util class
 */
public class CommonUtil {
    private static SecureRandom random = new SecureRandom();

    public static final Pattern MAIL_PATTERN = Pattern.compile("^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$");

    public static final Pattern MOBILE_PATTERN = Pattern.compile("^1[3|4|5|7|8][0-9]\\d{8}$");

    public static final Pattern NAME_PATTERN = Pattern.compile("^[\\u4E00-\\u9FBF][\\u4E00-\\u9FBF(.|·)]{0,13}[\\u4E00-\\u9FBF]$");

    public static final Pattern NICKNAME_PATTERN = Pattern.compile("^((?!\\d{21})[\\u4E00-\\u9FBF(.|·)|0-9A-Za-z_]){1,20}$");
//    public static final Pattern PASSWORD_PATTERN = Pattern.compile("[^ \\f\\n\\r\\t\\v]{6,30}$");

    public static final Pattern ADDRESS_DESCRIBE_PATTERN = Pattern.compile(".{0,30}$");

    //    public static final Pattern PASSWORD_PATTERN = Pattern.compile(".{6,30}$");
    public static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[0-9])(?=.*[a-zA-Z])(?=\\S+$).{6,30}$");
    public static final Pattern OLD_PASSWORD_PATTERN = Pattern.compile(".{6,30}$");


    public static final Pattern CODE_PATTERN = Pattern.compile("^0\\d{2,4}$");

    public static final Pattern POSTCODE_PATTERN = Pattern.compile("^\\d{6}$");

    public static final Pattern ID_PATTERN = Pattern.compile("^\\d{6}(\\d{8}|\\d{11})[0-9a-zA-Z]$");

    public static final Pattern BANK_CARD_PATTERN = Pattern.compile("^\\d{16,30}$");


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


    public static boolean validateEmail(String email) {
        if (isEmpty(email)) {
            return false;
        }
        Matcher m = MAIL_PATTERN.matcher(email);
        return m.matches();
    }


    public static boolean validateMobile(String mobile) {
        if (isEmpty(mobile)) {
            return false;
        }
        Matcher m = MOBILE_PATTERN.matcher(mobile);
        return m.matches();
    }


    public static boolean validateName(String name) {
        if (isEmpty(name) || name.replaceAll("[^.·]", "").length() > 1) {
            return false;
        }
        Matcher m = NAME_PATTERN.matcher(name);
        return m.matches();
    }


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


    public static boolean validatePassword(String password) {
        if (isEmpty(password)) {
            return false;
        }
        Matcher m = PASSWORD_PATTERN.matcher(password);
        return m.matches();
    }

    public static boolean validateOldPassword(String password) {
        if (isEmpty(password)) {
            return false;
        }
        Matcher m = OLD_PASSWORD_PATTERN.matcher(password);
        return m.matches();
    }


    public static boolean validateCode(String code) {
        if (isEmpty(code)) {
            return false;
        }
        Matcher m = CODE_PATTERN.matcher(code);
        return m.matches();
    }


    public static boolean validatePostcode(String postcode) {
        if (isEmpty(postcode)) {
            return false;
        }
        Matcher m = POSTCODE_PATTERN.matcher(postcode);
        return m.matches();
    }


    public static boolean validateBankCardNumber(String bankCardNumber) {
        if (isEmpty(bankCardNumber)) {
            return false;
        }
        Matcher m = BANK_CARD_PATTERN.matcher(bankCardNumber);
        return m.matches();
    }


    public static void invalidService(final String url, final Activity mActivity, final NodeSettingAdapter.NodeAddressListener nodeListener) {
        if (TextUtils.isEmpty(url)) {
            ToastUtil.showToast(mActivity, R.string.add_node_address_title, Toast.LENGTH_SHORT);
            return;
        }


        try {
            //check url
            OkHttpClient okHttpClient = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(url + Constants.BUMO_NODE_URL_PATH)
                    .build();
            okHttpClient.newCall(request).enqueue(new okhttp3.Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    ToastUtil.showToast(mActivity, R.string.invalid_node_address, Toast.LENGTH_SHORT);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {

                    try {
                        String json = response.body().string();
                        NodeAddressModel nodeAddressModel = new Gson().fromJson(json, NodeAddressModel.class);
                        if (nodeAddressModel.getError_code() == 0) {
                            mActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    nodeListener.success(url);
                                }
                            });
                        } else {
                            ToastUtil.showToast(mActivity, R.string.invalid_node_address, Toast.LENGTH_SHORT);
                        }
                    } catch (Exception e) {
                        ToastUtil.showToast(mActivity, R.string.invalid_node_address, Toast.LENGTH_SHORT);
                    }


                }
            });

        } catch (Exception e) {
            ToastUtil.showToast(mActivity, R.string.invalid_node_address, Toast.LENGTH_SHORT);
        }

    }


    public static Timestamp getTimestamp() {
        Timestamp d = new Timestamp(System.currentTimeMillis());
        return d;
    }


    public static String getUUID() {
        String uuid = UUID.randomUUID().toString().trim().replaceAll("-", "");
        return uuid.toUpperCase();
    }


    public static Integer getGenderByIdNumber(String idNumber) {

        int gender = 0;

        if (idNumber.length() == 15) {
            gender = Integer.parseInt(String.valueOf(idNumber.charAt(14))) % 2 == 0 ? 2 : 1;
        } else if (idNumber.length() == 18) {
            gender = Integer.parseInt(String.valueOf(idNumber.charAt(16))) % 2 == 0 ? 2 : 1;
        }

        return gender;

    }


    public static String getBirthdayByIdNumber(String idNumber) {

        String birthday = "";

        if (idNumber.length() == 15) {
            birthday = "19" + idNumber.substring(6, 8) + "-" + idNumber.substring(8, 10) + "-" + idNumber.substring(10, 12);
        } else if (idNumber.length() == 18) {
            birthday = idNumber.substring(6, 10) + "-" + idNumber.substring(10, 12) + "-" + idNumber.substring(12, 14);
        }

        return birthday;

    }


    public static Integer getAgeByIdNumber(String idNumber) {

        String birthString = getBirthdayByIdNumber(idNumber);
        if (isEmpty(birthString)) {
            return 0;
        }

        return getAgeByBirthString(birthString);

    }


    public static Integer getAgeByIdNumber(String idNumber, boolean isNominalAge) {

        String birthString = getBirthdayByIdNumber(idNumber);
        if (isEmpty(birthString)) {
            return 0;
        }

        return getAgeByBirthString(birthString, isNominalAge);

    }


    public static Integer getAgeByBirthDate(Date birthDate) {

        return getAgeByBirthString(new SimpleDateFormat("yyyy-MM-dd").format(birthDate));

    }


    public static Integer getAgeByBirthString(String birthString) {

        return getAgeByBirthString(birthString, "yyyy-MM-dd");

    }


    public static Integer getAgeByBirthString(String birthString, boolean isNominalAge) {

        return getAgeByBirthString(birthString, "yyyy-MM-dd", isNominalAge);

    }


    public static Integer getAgeByBirthString(String birthString, String format) {
        return getAgeByBirthString(birthString, "yyyy-MM-dd", false);
    }


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


    public static String maskMobile(String mobile) {
        if (validateMobile(mobile)) {
            return mobile.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
        }
        return mobile;
    }


    public static String maskMobile(String mobile, String transCode) {
        if (validateMobile(mobile)) {
            transCode = isEmpty(transCode) ? "****" : transCode;
            return mobile.replaceAll("(\\d{3})\\d{4}(\\d{4})", String.format("$1%s$2", transCode));
        }
        return mobile;
    }


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


    public static String maskTradeAccount(String account) {
        return account.replaceAll("(\\d{7})\\d*(\\d{4})", "$1****$2");
    }


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
        String name = "";
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
     * show input
     *
     * @param context
     * @param view
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
        boolean isSingle;
        try {
            isSingle = isSingle(Integer.parseInt(num));
        } catch (Exception e) {
            return false;
        }

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
        if (!isSend) {
            DialogUtils.showMessageNoTitleDialog(context, R.string.wechat_down_load_info);
        }
        return isSend;
    }


    public static String setRatio(int support, int total) {
        String strRatio = "0";
        double ratio = ((double) support / total) * 100;
//        if (support == total) {
//            strRatio = "100";
//        } else {
        DecimalFormat df = new DecimalFormat("#0.00");
        strRatio = df.format(ratio);
//        }
        return strRatio + "%";
    }

    public static String setRatio(String total) {

        String strRatio = "0.00";
        if (total.equals("100")) {
            strRatio = "100";
        } else {

            DecimalFormat df = new DecimalFormat("#0.00");
            strRatio = df.format(Double.parseDouble(total));
        }
        return strRatio + "%";
    }


    public static void setExpiryTime(String expiryTime, Context context) {
        if (!TextUtils.isEmpty(expiryTime)) {
            String[] strings = TimeUtil.time_mmss(Long.parseLong(expiryTime) - System.currentTimeMillis());
            if (strings[0].isEmpty()) {
                @SuppressLint("StringFormatMatches") String format = String.format(context.getString(R.string.error_1011_s, strings[1] + ""));
                DialogUtils.showMessageNoTitleDialog(context, format);
            } else {
                @SuppressLint("StringFormatMatches") String format = String.format(context.getString(R.string.error_1011_m, strings[0] + "", strings[1] + ""));
                DialogUtils.showMessageNoTitleDialog(context, format);
            }
        }
    }

    public static boolean validatePasswordEquals(String pw, String pwConfirm) {
        if (pw.equals(pwConfirm)) {
            return true;
        }

        return false;
    }


    public static int getWalletHeadRes(int position) {


        int[] walletHeadResList = new int[]{
                R.mipmap.ic_wallet_head_0,
                R.mipmap.ic_wallet_head_1,
                R.mipmap.ic_wallet_head_2,
                R.mipmap.ic_wallet_head_3,
                R.mipmap.ic_wallet_head_4,
                R.mipmap.ic_wallet_head_5,
                R.mipmap.ic_wallet_head_6,
                R.mipmap.ic_wallet_head_7,
                R.mipmap.ic_wallet_head_8,
                R.mipmap.ic_wallet_head_9
        };

        return walletHeadResList[position];
    }

    ;


    public static void setHeadIvRes(String walletAddress, ImageView walletHeadRiv, SharedPreferencesHelper spHelper) {
        int imageRes = (int) spHelper.getSharedPreference(walletAddress + ConstantsType.WALLET_HEAD_ICON, -1);
        if (imageRes != -1) {
            walletHeadRiv.setImageResource(CommonUtil.getWalletHeadRes(imageRes));
        }
    }

}