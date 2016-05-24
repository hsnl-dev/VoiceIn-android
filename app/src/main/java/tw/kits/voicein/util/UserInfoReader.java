package tw.kits.voicein.util;

import android.graphics.Bitmap;

/**
 * Created by Henry on 2016/5/21.
 */
public abstract class UserInfoReader {
    public abstract void readUser();

    public abstract void saveFile(Bitmap bitmap, String postfix);

    public final static String POST_FIX = "userInfo.json";
    public final static String QR_POST_FIX = "qr.jpg";
    public final static String AVATR_POST_FIX = "av.jpg";
}
