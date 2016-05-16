package tw.kits.voicein.util;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

/**
 * Created by Henry on 2016/3/14.
 */
public class AvatarEditUtil {
    private final int INTENT_PICK = 7000;
    private final int INTENT_TAKE = 9000;
    private final int INTENT_CROP = 8000;
    Activity activity;
    File file;

    public AvatarEditUtil(Activity activity) {
        this.activity = activity;
    }
    @SuppressWarnings("ResourceType")
    private void doCrop(Uri imgUri) {
        Log.e("wewewq", imgUri.toString());
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.setDataAndType(imgUri,"image/*");
        intent.putExtra("outputFormat", "JPEG");
        intent.putExtra("return-data", true);
        intent.putExtra("noFaceDetection", false);
        activity.startActivityForResult(intent, INTENT_CROP);

    }

    public void goChoosePic() {


        Intent intent;


        intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");

        activity.startActivityForResult(intent, INTENT_PICK);
    }

    public void doTakePhoto(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        File mFolder = new File(activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                "photo");
        mFolder.mkdirs();
        file = new File(mFolder,
                "my-photo"+new Date().getTime()+".jpg");
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,Uri.fromFile(file));
        Log.e("GGGG", "doTakePhoto: ");
        activity.startActivityForResult(takePictureIntent,INTENT_TAKE);
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
                case INTENT_TAKE:
                    doCrop(Uri.fromFile(file));
                    break;
                case INTENT_PICK:
                    doCrop(data.getData());
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
