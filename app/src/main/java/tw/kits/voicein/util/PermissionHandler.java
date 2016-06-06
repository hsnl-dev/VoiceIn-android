package tw.kits.voicein.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

/**
 * Created by Henry on 2016/6/6.
 */
public abstract class PermissionHandler {
    public interface ViewHandler{
        void onSuccessAsk();
        void onFailureAsk();


    }
    public abstract void parseResult(int reqCode, @NonNull String[] permissions, @NonNull int[] grantResults);
    public abstract void askPermission(Activity activity, Context context);
    public abstract void askPermissionForFrag(Fragment fragment, Context context);

}
