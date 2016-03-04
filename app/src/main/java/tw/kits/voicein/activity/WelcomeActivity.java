package tw.kits.voicein.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import tw.kits.voicein.R;
import tw.kits.voicein.util.UserAccessStore;

public class WelcomeActivity extends AppCompatActivity {
    private final String TAG = getLocalClassName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        new TimerAsync().execute(1000);
    }
    private class TimerAsync extends AsyncTask<Integer, Void, Void>{
        String userUuid;
        String token;
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
            Log.i(TAG, "L"+userUuid+token+"L");

            return null;
        }
    }



}
