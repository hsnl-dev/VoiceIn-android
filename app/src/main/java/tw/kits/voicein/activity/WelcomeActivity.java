package tw.kits.voicein.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;
import tw.kits.voicein.G8penApplication;
import tw.kits.voicein.R;

public class WelcomeActivity extends AppCompatActivity {
    private final String TAG = WelcomeActivity.class.getName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_welcome);
        new TimerAsync().execute(1000);
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
                Intent i = new Intent(WelcomeActivity.this, MainActivity.class);
                startActivity(i);
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



}
