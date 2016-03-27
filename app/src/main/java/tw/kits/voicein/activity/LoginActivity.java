package tw.kits.voicein.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.text.TextUtilsCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tw.kits.voicein.G8penApplication;
import tw.kits.voicein.R;
import tw.kits.voicein.adapter.CountryAdapter;
import tw.kits.voicein.fragment.ProgressFragment;
import tw.kits.voicein.model.CountryCode;
import tw.kits.voicein.model.UserLoginRes;
import tw.kits.voicein.util.ColoredSnackBar;
import tw.kits.voicein.util.PhoneNumberHelper;
import tw.kits.voicein.util.ServiceManager;
import tw.kits.voicein.util.VoiceInService;

public class LoginActivity extends AppCompatActivity {
    private final String TAG = LoginActivity.class.getName();
    private VoiceInService mService;
    TextView phoneNum;
    private View mlayout;
    private Context mContext;
    private ProgressFragment mDialog;

    private static final String WAIT_TAG ="wait";
    //private Spinner mSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Button confirmBtn = (Button) findViewById(R.id.login_btn_confirm);
        phoneNum = (TextView) findViewById(R.id.login_et_phonenum);
        mlayout = findViewById(R.id.login_lo_main);
        mDialog = new ProgressFragment();
        mContext = getBaseContext();
        mService = ((G8penApplication)getApplication()).getAPIService();
        //mSpinner = (Spinner) findViewById(R.id.login_sn_country);
//        byte[] countryRaw = Base64.decode(getResources().getString(R.string.countries_code), Base64.DEFAULT);
//        String countryString = null;
//        try {
//            countryString = new String(countryRaw, "UTF-8");
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//        Gson gson = new Gson();
//        Type listType = new TypeToken<ArrayList<CountryCode>>() {}.getType();
//        List<CountryCode> countryCodes = gson.fromJson(countryString, listType);
//        CountryAdapter countryAdapter = new CountryAdapter(countryCodes,this);
//        String devCountry = Locale.getDefault().getCountry();
//        mSpinner.setAdapter(countryAdapter);
//        int pos = countryAdapter.findByCode(devCountry);
//        Log.e(TAG,devCountry);
//        if(pos>=0)
//            mSpinner.setSelection(pos);
//        Log.e(TAG,""+pos);
        confirmBtn.setOnClickListener(new ConfirmClickListener());
    }

    private class ConfirmClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if(!PhoneNumberHelper.isValid(phoneNum.getText().toString())){
                phoneNum.setError(getString(R.string.ilegal_input));
                return;
            }
            mDialog.show(getSupportFragmentManager(),WAIT_TAG);
            HashMap<String, String> res = new HashMap<>();

            final String standardNum =PhoneNumberHelper.getStandardNumber(phoneNum.getText().toString());
            res.put("phoneNumber", standardNum);
            Call<UserLoginRes> call = mService.getvalidationCode(res);
            call.enqueue(new Callback<UserLoginRes>() {
                @Override
                public void onResponse(Call<UserLoginRes> call, Response<UserLoginRes> response) {
                    mDialog.dismiss();
                    if (response.isSuccess()) {
                        Bundle bundle = new Bundle();
                        bundle.putString(VerifyActivity.ARG_UUID, response.body().getUserUuid());
                        bundle.putString(VerifyActivity.ARG_PHONE_NUM, standardNum);
                        Intent intent = new Intent(LoginActivity.this, VerifyActivity.class);
                        intent.putExtras(bundle);
                        startActivity(intent);

                    } else {
                        try {
                            Log.w(TAG, response.errorBody().string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                }

                @Override
                public void onFailure(Call<UserLoginRes> call, Throwable t) {
                    mDialog.dismiss();
                    Log.e(TAG, t.toString());
                    ColoredSnackBar
                            .primary(Snackbar.make(mlayout, mContext.getString(R.string.network_err), Snackbar.LENGTH_LONG))
                            .show();
                }
            });


        }

    }
}
