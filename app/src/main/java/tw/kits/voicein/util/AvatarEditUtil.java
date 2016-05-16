package tw.kits.voicein.util;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Henry on 2016/3/14.
 */
public class AvatarEditUtil {
    private final int INTENT_PICK = 7000;
    private final int INTENT_CROP = 8000;
    Activity activity;

    public AvatarEditUtil(Activity activity) {
        this.activity = activity;
    }
    @SuppressWarnings("ResourceType")
    private void doCrop(Intent data) {
        Uri imgUri = data.getData();
        Log.e("wewewq", imgUri.toString());
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.setData(imgUri);
        intent.putExtra("outputFormat", "JPEG");
        intent.putExtra("return-data", true);
        intent.putExtra("noFaceDetection", false);
        activity.startActivityForResult(intent, INTENT_CROP);

    }

    public void startEdit() {

        Intent intent;


        intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        activity.startActivityForResult(intent, INTENT_PICK);


//        Intent intent;
//
//        if (Build.VERSION.SDK_INT < 19) {
//            intent = new Intent();
//            intent.setAction(Intent.ACTION_GET_CONTENT);
//            intent.setType("image/*");
//        } else {
//            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
//            intent.addCategory(Intent.CATEGORY_OPENABLE);
//            intent.setType("image/*");
//        }
//        activity.startActivityForResult(intent, INTENT_PICK);
    }

    /***
     * This is helper that help you to parse result from crop intent
     *
     * @param requestCode
     * @param resultCode
     * @param data
     * @return if success return bitmap, else return null;
     */
    public Bitmap parseResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == activity.RESULT_OK) {
            switch (requestCode) {
                case INTENT_PICK:
                    doCrop(data);
                    break;
                case INTENT_CROP:
                    Bundle bundle = data.getExtras();
                    return bundle.getParcelable("data");
            }
        }
        return null;
    }

    public File prepareImg(Bitmap bitmap) throws IOException {
        File imageFileFolder = new File(activity.getCacheDir(), "Avatar");
        if (!imageFileFolder.exists()) {
            imageFileFolder.mkdir();
        }
        File image = new File(imageFileFolder, "avatar-" + System.currentTimeMillis() + ".jpg");
        FileOutputStream out = null;

        out = new FileOutputStream(image);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        out.flush();
        return image;
    }

}
