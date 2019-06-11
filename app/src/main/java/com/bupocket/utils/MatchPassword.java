package com.bupocket.utils;

import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MatchPassword {

    static final String  PASSWORD_PATTERN="^(?=.*[0-9])(?=.*[a-zA-Z])(?=\\S+$).{6,30}$";

    public static boolean isValidPW(final String pw) {
        if (TextUtils.isEmpty(pw)) {
            return false;
        }
        Pattern pattern;
        Matcher matcher;
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(pw);

        return matcher.matches();
    }
}
