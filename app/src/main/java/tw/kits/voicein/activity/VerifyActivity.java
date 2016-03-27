package tw.kits.voicein.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.IOException;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tw.kits.voicein.G8penApplication;
import tw.kits.voicein.R;
import tw.kits.voicein.fragment.ProgressFragment;
import tw.kits.voicein.model.Token;
import tw.kits.voicein.model.UserInfo;
import tw.kits.voicein.model.UserLoginRes;
import tw.kits.voicein.model.VerifyForm;
import tw.kits.voicein.util.ColoredSnackBar;
import tw.kits.voicein.util.VoiceInService;

public class VerifyActivity extends AppCompatActivity {
    private final String TAG = VerifyActivity.class.getName();
    private final int WAIT_NANO_SEC = 10000;
    VoiceInService mApiService;
    String mUserUuid;
    String token;
    String mPhoneNumber;
    Button mResend;
    Button mConfirm;
    Intent mIntent;
    View mLayout;
    EditText mCode;
    G8penApplication mApplication;
    ProgressFragment mDialog;
    public static final String ARG_UUID = "mUserUuid";
    public static final String ARG_PHONE_NUM = "phone_num";
    private static final String DIALOG_TAG = "dialog";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_verify);
        mApplication = (G8penApplication) getApplication();
        mApiService = mApplication.getAPIService();
        mDialog = new ProgressFragment();
        mResend = (Button) findViewById(R.id.verify_btn_resend);
        mConfirm = (Button) findViewById(R.id.verify_btn_confirm);
        mCode = (EditText) findViewById(R.id.verify_et_code);
        mLayout = findViewById(R.id.verify_lo_main);

        mIntent = getIntent();
        mUserUuid = mIntent.getExtras().getString(ARG_UUID);
        mPhoneNumber = mIntent.getExtras().getString(ARG_PHONE_NUM);
        mResend.setOnClickListener(new ResendListener());
        mConfirm.setOnClickListener(new VerifyListener());
        TimerAsync timer = new TimerAsync();
        timer.execute(WAIT_NANO_SEC);

    }

    private class ResendListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            HashMap<String, String> req = new HashMap<>();
            req.put("phoneNumber", mPhoneNumber);
            mDialog.show(getSupportFragmentManager(), DIALOG_TAG);
            mApiService.getvalidationCode(req).enqueue(
                    new Callback<UserLoginRes>() {
                        @Override
                        public void onResponse(Call<UserLoginRes> call, Response<UserLoginRes> response) {
                            if (response.isSuccess()) {
                                mDialog.dismiss();
                                new TimerAsync().execute(WAIT_NANO_SEC);
                            } else {
                                try {
                                    Log.w(TAG, response.errorBody().string());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                ColoredSnackBar
                                        .primary(Snackbar.make(mLayout, getString(R.string.user_auth_err), Snackbar.LENGTH_LONG))
                                        .show();

                            }
                        }

                        @Override
                        public void onFailure(Call<UserLoginRes> call, Throwable t) {
                            Log.e(TAG, t.toString());
                            ColoredSnackBar
                                    .primary(Snackbar.make(mLayout, getString(R.string.network_err), Snackbar.LENGTH_LONG))
                                    .show();

                        }
                    }
            );
        }
    }


    private class VerifyListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            VerifyForm form = new VerifyForm();
            form.setCode(mCode.getText().toString());
            form.setUserUuid(mUserUuid);
            mDialog.show(getSupportFragmentManager(), DIALOG_TAG);
            mApiService.getToken(form).enqueue(tokenCallBack);
        }
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
                mApplication.refreshAccessInfo(token, mUserUuid, mPhoneNumber);
                mApiService = mApplication.getAPIService();
                mApiService.getUser(VerifyActivity.this.mUserUuid).enqueue(userInfoCallBack);

            } else {
                //fail
                mDialog.dismiss();
                ColoredSnackBar
                        .primary(Snackbar.make(mLayout, getString(R.string.user_auth_err), Snackbar.LENGTH_LONG))
                        .show();
            }
        }

        @Override
        public void onFailure(Call<Token> call, Throwable t) {
            //fail
            mDialog.dismiss();
            ColoredSnackBar
                    .primary(Snackbar.make(mLayout, getString(R.string.network_err), Snackbar.LENGTH_LONG))
                    .show();
        }
    };
    Callback<UserInfo> userInfoCallBack = new Callback<UserInfo>() {
        @Override
        public void onResponse(Call<UserInfo> call, Response<UserInfo> response) {
            mDialog.dismiss();
            if (response.isSuccess()) {
                Intent intent = new Intent(VerifyActivity.this, ProfileActivity.class);
                intent.putExtra(ProfileActivity.ARG_USER, response.body());
                intent.putExtra(ProfileActivity.ARG_PHONE, mPhoneNumber);
                startActivity(intent);
                finish();
            } else {
                ColoredSnackBar
                        .primary(Snackbar.make(mLayout, getString(R.string.network_err), Snackbar.LENGTH_LONG))
                        .show();
            }
        }

        @Override
        public void onFailure(Call<UserInfo> call, Throwable t) {
            mDialog.dismiss();
            Log.w(TAG, t.toString());
            t.printStackTrace();
            ColoredSnackBar
                    .primary(Snackbar.make(mLayout, getString(R.string.network_err), Snackbar.LENGTH_LONG))
                    .show();
        }
    };
}
