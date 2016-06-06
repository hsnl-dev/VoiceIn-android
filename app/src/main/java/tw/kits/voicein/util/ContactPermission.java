package tw.kits.voicein.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

/**
 * Created by Henry on 2016/6/6.
 */
public class ContactPermission extends PermissionHandler {
    private static final int CONTACT_REQUEST_CODE = 48;
    private ViewHandler mView;

    public ContactPermission(ViewHandler view) {
        mView = view;
    }

    @Override
    public void parseResult(int reqCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (reqCode == CONTACT_REQUEST_CODE) {
            if (grantResults.length == 1) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mView.onSuccessAsk();

                }else{
                    mView.onFailureAsk();
                }
            }

        }else{
            mView.onFailureAsk();
        }

    }

    @Override
    public void askPermission(Activity activity, Context context) {

        boolean contactGrant = PackageManager.PERMISSION_GRANTED  ==
                ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS);
        if (!contactGrant) {

            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                    CONTACT_REQUEST_CODE);
        }else{
            mView.onSuccessAsk();
        }
    }

    @Override
    public void askPermissionForFrag(Fragment fragment, Context context) {
        boolean contactGrant = PackageManager.PERMISSION_GRANTED  ==
                ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS);
        if (!contactGrant) {

            fragment.requestPermissions(
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                    CONTACT_REQUEST_CODE);
        }else{
            mView.onSuccessAsk();
        }
    }
}
