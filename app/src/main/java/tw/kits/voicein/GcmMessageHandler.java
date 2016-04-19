package tw.kits.voicein;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import com.google.android.gms.gcm.GcmListenerService;


public class GcmMessageHandler extends GcmListenerService {
    public static final int MESSAGE_NOTIFICATION_ID = 435345;


    @Override
    public void onMessageReceived(String from, Bundle data) {
        String message = data.getString("message");
        if(message.startsWith("#call#"))
            createNotification(from, message);
        else{

        }
    }

    // Creates notification based on title and body received
    private void createNotification(String title, String body) {

        Context context = getBaseContext();

        showDialog(title, body);
    }

    public void showDialog(String title, String message) {



        try {
            new Thread() {
                @Override
                public void run() {
                    Looper.prepare();

                    WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                            WindowManager.LayoutParams.WRAP_CONTENT,
                            WindowManager.LayoutParams.WRAP_CONTENT,
                            WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                            WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH|WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                            PixelFormat.RGBA_8888);
                    params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
                    params.setTitle("Load Average");
                    final WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
                    final View view = ((LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE)).inflate(R.layout.alert_layout,null);

                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ((WindowManager) getSystemService(WINDOW_SERVICE)).removeViewImmediate(v);
                            Log.w("WTF", "onClick: " );
                        }
                    });
                    wm.addView(view, params);
                    Looper.loop();

                }
            }.start();
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

}