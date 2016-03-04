package tw.kits.voicein.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.IOException;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tw.kits.voicein.R;
import tw.kits.voicein.util.VoiceInService;
import tw.kits.voicein.model.UserLoginRes;
import tw.kits.voicein.util.ServiceManager;

public class LoginActivity extends AppCompatActivity {
    TextView phoneNum;
    private final String TAG = LoginActivity.class.getName();
    private final VoiceInService service = ServiceManager.createService(null);
    private RelativeLayout mlayout;
    private Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Button confirmBtn = (Button)findViewById(R.id.login_btn_confirm);
        phoneNum = (TextView)findViewById(R.id.login_et_phonenum);
        mlayout = (RelativeLayout)findViewById(R.id.login_lo_main);
        mContext = getBaseContext();
        confirmBtn.setOnClickListener(new ConfirmClickListener());
    }
    private class ConfirmClickListener implements View.OnClickListener{
            @Override
            public void onClick(View v) {
                final ProgressDialog dialog = ProgressDialog.show(
                        LoginActivity.this,
                        getBaseContext().getString(R.string.wait),
                        getBaseContext().getString(R.string.wait_notice),
                        true);

                HashMap<String,String> res = new HashMap<>();
                res.put("phoneNumber", phoneNum.getText().toString());
                Call<UserLoginRes> call = service.getvalidationCode(res);
                call.enqueue(new Callback<UserLoginRes>() {
                    @Override
                    public void onResponse(Call<UserLoginRes> call, Response<UserLoginRes> response) {
                        dialog.dismiss();
                        if (response.isSuccess()){
                            Bundle bundle = new Bundle();
                            bundle.putString("userUuid", response.body().getUserUuid());
                            bundle.putString("phoneNumber", phoneNum.getText().toString());
                            Intent intent = new Intent(LoginActivity.this, VerifyActivity.class);
                            intent.putExtras(bundle);
                            startActivity(intent);

                        }else{
                            try {
                                Log.w(TAG, response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                    }
                    @Override
                    public void onFailure(Call<UserLoginRes> call, Throwable t) {
                        dialog.dismiss();
                        Log.e(TAG,t.toString());
                        Snackbar snack =  Snackbar.make(mlayout, mContext.getString(R.string.network_err), Snackbar.LENGTH_LONG);
                        View view = snack.getView();
                        TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                        tv.setTextColor(Color.WHITE);
                        snack.show();
                    }
                });


            }

    }
}
