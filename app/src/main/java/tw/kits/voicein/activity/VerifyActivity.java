package tw.kits.voicein.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.IOException;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tw.kits.voicein.R;
import tw.kits.voicein.fragment.ProgressFragment;
import tw.kits.voicein.model.Token;
import tw.kits.voicein.model.UserInfo;
import tw.kits.voicein.model.UserLoginRes;
import tw.kits.voicein.util.ServiceManager;
import tw.kits.voicein.util.UserAccessStore;
import tw.kits.voicein.util.VoiceInService;

public class VerifyActivity extends AppCompatActivity {
    private final String TAG = VerifyActivity.class.getName();
    private final int WAIT_NANO_SEC = 10000;
    Button mResend;
    Button mConfirm;
    Intent mIntent;
    RelativeLayout mlayout;
    VoiceInService service;
    EditText mCode;
    Context mContext;
    String userUuid;
    String token;
    final ProgressFragment pf = new ProgressFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_verify);
        service = ServiceManager.createService(null);
        mContext = getBaseContext();
        mResend = (Button) findViewById(R.id.verify_btn_resend);
        mConfirm = (Button) findViewById(R.id.verify_btn_confirm);
        mCode = (EditText) findViewById(R.id.verify_et_code);
        mlayout = (RelativeLayout) findViewById(R.id.verify_lo_main);
        mIntent = this.getIntent();
        userUuid = mIntent.getExtras().getString("userUuid");
        mResend.setOnClickListener(new ResendListener());
        mConfirm.setOnClickListener(new VerfiyListener());
        TimerAsync timer = new TimerAsync();
        timer.execute(WAIT_NANO_SEC);

    }

    private class ResendListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            HashMap<String, String> req = new HashMap<>();
            req.put("phoneNumber", mIntent.getExtras().getString("phoneNumber"));
            final ProgressDialog dialog = ProgressDialog.show(
                    VerifyActivity.this,
                    getBaseContext().getString(R.string.wait),
                    getBaseContext().getString(R.string.wait_notice),
                    true);
            service.getvalidationCode(req).enqueue(
                    new Callback<UserLoginRes>() {
                        @Override
                        public void onResponse(Call<UserLoginRes> call, Response<UserLoginRes> response) {
                            if (response.isSuccess()) {
                                dialog.dismiss();
                                new TimerAsync().execute(WAIT_NANO_SEC);
                            } else {
                                try {
                                    Log.w(TAG, response.errorBody().string());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                Snackbar snack = Snackbar.make(mlayout, mContext.getString(R.string.user_auth_err), Snackbar.LENGTH_LONG);
                                View view = snack.getView();
                                TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                                tv.setTextColor(Color.WHITE);
                                snack.show();
                            }
                        }

                        @Override
                        public void onFailure(Call<UserLoginRes> call, Throwable t) {
                            Log.e(TAG, t.toString());
                            Snackbar snack = Snackbar.make(mlayout, mContext.getString(R.string.network_err), Snackbar.LENGTH_LONG);
                            View view = snack.getView();
                            TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                            tv.setTextColor(Color.WHITE);
                            snack.show();

                        }
                    }
            );
        }
    }


    private class VerfiyListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            HashMap<String, String> req = new HashMap<>();
            req.put("userUuid", VerifyActivity.this.userUuid);
            req.put("code", mCode.getText().toString());

            pf.show(getSupportFragmentManager(), "WAIT");
            service.getToken(req).enqueue(tokenCallBack);
        }
    }

    private void showSnackBar(String msg) {
        Snackbar snack = Snackbar.make(mlayout, msg, Snackbar.LENGTH_LONG);
        View view = snack.getView();
        TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextColor(Color.WHITE);
        snack.show();
    }

    private class TimerAsync extends AsyncTask<Integer, Void, Void> {
        @Override
        protected void onPreExecute() {
            mResend.setEnabled(false);
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            mResend.setEnabled(true);
            super.onPostExecute(aVoid);
        }

        @Override
        protected Void doInBackground(Integer[] params) {
            try {
                Thread.sleep(params[0]);
            } catch (InterruptedException e) {
                Log.w(TAG, e.toString());
            }
            return null;
        }
    }

    Callback<Token> tokenCallBack = new Callback<Token>() {
        @Override
        public void onResponse(Call<Token> call, Response<Token> response) {
            if (response.isSuccess()) {
                //successful login
                VerifyActivity.this.token = response.body().getToken();
                service = ServiceManager.createService(VerifyActivity.this.token);
                service.getUser(VerifyActivity.this.userUuid).enqueue(userInfoCallBack);

            } else {
                //fail
                pf.dismiss();
                showSnackBar(getResources().getString(R.string.user_auth_err));
            }
        }

        @Override
        public void onFailure(Call<Token> call, Throwable t) {
            //fail
            pf.dismiss();
            showSnackBar(getResources().getString(R.string.network_err));
        }
    };
    Callback<UserInfo> userInfoCallBack = new Callback<UserInfo>() {
        @Override
        public void onResponse(Call<UserInfo> call, Response<UserInfo> response) {
            pf.dismiss();
            if (response.isSuccess()) {
                //save pref
                SharedPreferences.Editor editor = getSharedPreferences(UserAccessStore.PREF_LOC
                        , Context.MODE_PRIVATE).edit();
                editor.putString("token", VerifyActivity.this.token);
                editor.putString("userUuid", VerifyActivity.this.userUuid);
                editor.commit();
                //set basic info
                UserAccessStore.setToken(VerifyActivity.this.token);
                UserAccessStore.setUserUuid(VerifyActivity.this.userUuid);
                //start intent
                Intent intent = new Intent(VerifyActivity.this, ProfileActivity.class);
                intent.putExtra("userInfo", response.body());
                intent.putExtra("phoneNumber",getIntent().getStringExtra("phoneNumber"));
                startActivity(intent);
                finish();
            } else {
                showSnackBar(getResources().getString(R.string.network_err));
            }
        }

        @Override
        public void onFailure(Call<UserInfo> call, Throwable t) {
            pf.dismiss();
            Log.w(TAG, t.toString());
            t.printStackTrace();
            showSnackBar(getResources().getString(R.string.network_err));
        }
    };
}
