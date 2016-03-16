package tw.kits.voicein.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;
import tw.kits.voicein.R;
import tw.kits.voicein.util.UserAccessStore;

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
                Intent i = new Intent(WelcomeActivity.this, LoginActivity.class);
                startActivity(i);

            }else{
                Intent i = new Intent(WelcomeActivity.this, MainActivity.class);
                UserAccessStore.setUserUuid(userUuid);
                UserAccessStore.setToken(token);
                UserAccessStore.setPhoneNum(phoneNum);
                startActivity(i);
            }
            finish();
        }

        @Override
        protected Void doInBackground(Integer[] params) {
            try {
                Thread.sleep(params[0]);
            } catch (InterruptedException e) {
                Log.w(TAG,e.toString());
            }
            SharedPreferences sp = getSharedPreferences(UserAccessStore.PREF_LOC, Context.MODE_PRIVATE);
            userUuid = sp.getString("userUuid", null);
            token = sp.getString("token",null);
            phoneNum = sp.getString("phoneNum",null);
            Log.i(TAG, "L"+userUuid+token+"L");

            return null;
        }
    }



}
