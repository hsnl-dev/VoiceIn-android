package tw.kits.voicein.util;

import tw.kits.voicein.BuildConfig;

/**
 * Created by Henry on 2016/3/3.
 */
public class ServiceConstant {

    public static final String WEB_BASE_URL = BuildConfig.WEB_BASE_URL;
    public final static String API_BASE = BuildConfig.API_BASE;
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
