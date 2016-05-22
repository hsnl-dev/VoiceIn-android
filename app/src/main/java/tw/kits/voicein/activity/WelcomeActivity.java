package tw.kits.voicein.activity;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesUtil;

import io.fabric.sdk.android.Fabric;
import tw.kits.voicein.G8penApplication;
import tw.kits.voicein.R;
import tw.kits.voicein.RegistrationIntentService;

public class WelcomeActivity extends AppCompatActivity {
    private final String TAG = WelcomeActivity.class.getName();

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, 60000)
                        .show();

            } else {

                finish();
            }
            Log.i(TAG, "This device is not supported.");
            return false;
        }
        return true;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_welcome);

        new TimerAsync().execute(100);
    }
    private class TimerAsync extends AsyncTask<Integer, Void, Void>{
        String userUuid;
        String token;
        String phoneNum;
        @Override
        protected void onPreExecute() {

            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            super.onPostExecute(aVoid);
            if(userUuid==null || token == null) {
                Intent i = new Intent(WelcomeActivity.this, IntroActivity.class);
                startActivity(i);

            }else{
                if(!isNetworkAvaliable()){
                    Intent cardIntent = new Intent(WelcomeActivity.this, MyCardActivity.class);
                    cardIntent.putExtra(MyCardActivity.ARG_OFFLINE,true);
                    startActivity(cardIntent);

                }else if (checkPlayServices()) {
                    // Start IntentService to register this application with GCM.
                    Intent intent = new Intent(WelcomeActivity.this, RegistrationIntentService.class);
                    startService(intent);
                    Intent i = new Intent(WelcomeActivity.this, MainActivity.class);
                    startActivity(i);
                }

            }
            finish();
        }

        @Override
        protected Void doInBackground(Integer[] params) {
            try {
                Thread.sleep(params[0]);
                token = ((G8penApplication)getApplication()).getToken();
                userUuid = ((G8penApplication)getApplication()).getUserUuid();

            } catch (InterruptedException e) {
                Log.w(TAG,e.toString());
            }

            return null;
        }
    }
    public boolean isNetworkAvaliable(){
        boolean isConnected = false;
        ConnectivityManager cm = (ConnectivityManager)this.getSystemService(CONNECTIVITY_SERVICE);
        if(cm!=null){
            NetworkInfo info = cm.getActiveNetworkInfo();
            isConnected = info !=null && info.isConnectedOrConnecting();
            Log.e(TAG, "isNetworkAvaliable: "+isConnected+"");
        }
        return isConnected;
    }



}
