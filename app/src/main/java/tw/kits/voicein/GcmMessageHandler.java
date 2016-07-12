package tw.kits.voicein;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.NotificationCompat;
import android.support.v7.view.ContextThemeWrapper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.android.gms.gcm.GcmListenerService;

import tw.kits.voicein.activity.MainActivity;


public class GcmMessageHandler extends GcmListenerService {
    public static final int MESSAGE_NOTIFICATION_ID = 435345;
    public static final String NEW_CONTACT_NOTIFY = "new_contact";

    @Override
    public void onMessageReceived(String from, Bundle data) {
        String message = data.getString("message");
        Log.e("message", "onMessageReceived() returned: " + message);
        if(message!=null){
            if(message.startsWith("#call#"))
                createNotification(from, message.replace("#call#",""));
            else{
//                Intent intent = new Intent();
//                intent.setAction(NEW_CONTACT_NOTIFY);
//                intent.setPackage(GcmMessageHandler.class.getPackage().getName());
//                intent.putExtra("message",message);
//                LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
                NotificationCompat.Builder builder =
                        new NotificationCompat.Builder(this);
                final int flags = PendingIntent.FLAG_CANCEL_CURRENT;
                Intent intent1 = new Intent(this, MainActivity.class);
                final PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0x1, intent1, flags); // 取得PendingIntent

                builder.setSmallIcon(R.drawable.ic_stat_voice)
                        .setWhen(System.currentTimeMillis())
                        .setContentTitle(getString(R.string.app_name))
                        .setContentText(message)
                        .setContentIntent(pendingIntent);


                NotificationManager notificationManager = (NotificationManager)
                        getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(0x001,builder.build());
            }
        }


    }

    // Creates notification based on title and body received
    private void createNotification(String title, String body) {

        Context context = getBaseContext();

        showDialog(title, body);
    }

    public void showDialog(String title, final String message) {



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
                    TextView text = (TextView) view.findViewById(R.id.alert_tv_status);
                    text.setText(message);
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