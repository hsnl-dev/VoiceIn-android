package tw.kits.voicein.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ImageReader;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import static android.graphics.Bitmap.Config.ARGB_8888;

/**
 * Created by Henry on 2016/5/30.
 */
public class QRCodeUtil {
    static final String TAG = QRCodeUtil.class.getSimpleName();
    @Nullable
    public static String readQRCode(Context context, Uri uri){
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(),uri);

            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            int[] pixels = new int[width * height];
            bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
            //recycle bitmap;
            bitmap.recycle();
            bitmap = null;
            RGBLuminanceSource source = new RGBLuminanceSource(width,height,pixels);
            BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(source));
            QRCodeReader reader = new QRCodeReader();
            Result result   = reader.decode(binaryBitmap);
            return result.getText();
        } catch (NotFoundException e) {
            e.printStackTrace();
            Log.w(TAG, e);
            return null;
        } catch (ChecksumException e) {
            e.printStackTrace();
            Log.w(TAG, e);
            return null;
        } catch (FormatException e) {
            e.printStackTrace();
            Log.w(TAG, e);
            return null;
        } catch (IOException e){
            e.printStackTrace();
            Log.w(TAG, e);
            return null;
        }

    }
}
