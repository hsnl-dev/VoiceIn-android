package tw.kits.voicein.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;

/**
 * Created by Henry on 2016/6/6.
 */
public class CameraPermission extends PermissionHandler {
    private static final int REQUEST_CODE = 69;
    private ViewHandler mView;

    public CameraPermission(ViewHandler view) {
        mView = view;
    }

    @Override
    public void parseResult(int reqCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (reqCode == REQUEST_CODE) {
            if (grantResults.length == 2) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
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
        boolean storageGrant = PackageManager.PERMISSION_GRANTED  ==
                ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        boolean cameraGrant = PackageManager.PERMISSION_GRANTED  ==
                ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA);
        Log.e("ww", "askPermission: " + !cameraGrant +!storageGrant );
        if (!cameraGrant || !storageGrant) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                    REQUEST_CODE);
        }else{
            mView.onSuccessAsk();
        }
    }

    @Override
    public void askPermissionForFrag(Fragment fragment, Context context) {
        boolean storageGrant = PackageManager.PERMISSION_GRANTED  ==
                ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        boolean cameraGrant = PackageManager.PERMISSION_GRANTED  ==
                ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA);
        Log.e("ww", "askPermission: " + !cameraGrant +!storageGrant );
        if (!cameraGrant || !storageGrant) {
            fragment.requestPermissions(
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                    REQUEST_CODE);
        }else{
            mView.onSuccessAsk();
        }
    }
}
