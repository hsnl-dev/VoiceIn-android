package tw.kits.voicein.util;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import java.io.IOException;

/**
 * Created by Henry on 2016/3/4.
 */
public class UserAccessStore {
    public final static String PREF_LOC = "LOGIN_PREF";
    private static String TOKEN;
    private static String USER_UUID;
    private static String PHONE_NUM;

    public static String getPhoneNum() {
        return PHONE_NUM;
    }

    public static void setPhoneNum(String phoneNum) {
        PHONE_NUM = phoneNum;
    }

    public static String getToken() {
        return TOKEN;
    }

    public static void setToken(@NonNull String token) {
        UserAccessStore.TOKEN = token;
    }

    public static String getUserUuid() {
        return USER_UUID;
    }

    public static void setUserUuid(@NonNull String userUuid) {
        USER_UUID = userUuid;
    }



}
