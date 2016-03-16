package tw.kits.voicein.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tw.kits.voicein.R;
import tw.kits.voicein.fragment.ProgressFragment;
import tw.kits.voicein.model.CustomerQRcodeForm;
import tw.kits.voicein.util.ColoredSnackBar;
import tw.kits.voicein.util.ServiceManager;
import tw.kits.voicein.util.UserAccessStore;
import tw.kits.voicein.util.VoiceInService;

public class QrcodeCreateActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText mName;
    private EditText mPhone;
    private EditText mCom;
    private EditText mLoc;
    DialogFragment mfragment;
    private LinearLayout mMainLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode_create);

        findViewById(R.id.customer_qrcode_confirm).setOnClickListener(this);

        mName = (EditText) findViewById(R.id.qrcode_create_et_name);
        mPhone = (EditText) findViewById(R.id.qrcode_create_et_phone);
        mCom = (EditText) findViewById(R.id.qrcode_create_et_com);
        mLoc = (EditText) findViewById(R.id.qrcode_create_et_loc);
        mMainLayout = (LinearLayout)findViewById(R.id.qrcode_create_lo_main);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.customer_qrcode_confirm:
                VoiceInService service = ServiceManager.createService(UserAccessStore.getToken());
                CustomerQRcodeForm form = new CustomerQRcodeForm();
                form.setName(mName.getText().toString());
                form.setPhoneNumber(mPhone.getText().toString());
                form.setLocation(mLoc.getText().toString());
                form.setCompany(mCom.getText().toString());
                mfragment = new ProgressFragment();
                mfragment.show(getSupportFragmentManager(),"load");

                service.createcustomQrcodes(UserAccessStore.getUserUuid(),form)
                        .enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                mfragment.dismiss();
                                if (response.isSuccess()) {

                                    finish();
                                } else {
                                    ColoredSnackBar
                                            .primary(Snackbar.make(mMainLayout, R.string.server_err, Snackbar.LENGTH_LONG))
                                            .show();
                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                mfragment.dismiss();
                                ColoredSnackBar
                                        .primary(Snackbar.make(mMainLayout,R.string.network_err,Snackbar.LENGTH_LONG))
                                        .show();
                            }
                        });
                break;
        }
    }

}
