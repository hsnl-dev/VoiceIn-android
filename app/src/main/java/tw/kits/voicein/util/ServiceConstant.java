package tw.kits.voicein.util;

import android.content.Context;

import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Henry on 2016/3/3.
 */
public class ServiceConstant {
    public static final String WEB_BASE_URL = "https://voicein.kits.tw/";
//    public final static String API_BASE = "https://voicein-api.kits.tw/";
    public final static String API_BASE = "https://voicein.herokuapp.com/";
    public final static String API_KEY = "784a48e7-a15f-4623-916a-1bd304dc9f56";
    public final static String PIC_SIZE_MID = "mid";
    public final static String PIC_SIZE_LARGE = "large";
    public final static String PIC_SIZE_SMALL = "small";

    public static String getQRcodeUri(String uuid) {
        return String.format("%sapi/v2/accounts/%s/qrcode", API_BASE, uuid);
    }
    public static String getAvatarById(String avatarId, String size) {
        return String.format("%sapi/v2/avatars/%s?size=%s", API_BASE, avatarId, size);
    }
    public static String getQRcodeById(String uuid) {
        return String.format("%sapi/v2/qrcodes/%s/image", API_BASE, uuid);
    }
    public static String getAvatarUri(String uuid, String size) {
        return String.format("%sapi/v2/accounts/%s/avatar?size=%s", API_BASE, uuid, size);
    }
}
