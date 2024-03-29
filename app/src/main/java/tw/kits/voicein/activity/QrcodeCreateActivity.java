package tw.kits.voicein.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tw.kits.voicein.G8penApplication;
import tw.kits.voicein.R;
import tw.kits.voicein.fragment.ProgressFragment;
import tw.kits.voicein.model.CustomerQRcodeForm;
import tw.kits.voicein.util.ColoredSnackBarUtil;
import tw.kits.voicein.util.PhoneNumberUtil;
import tw.kits.voicein.util.VoiceInService;

public class QrcodeCreateActivity extends AppCompatActivity implements View.OnClickListener {

    DialogFragment mFragment;
    int INTENT_PHONE_BOOK = 0x01;
    int PEMISSON_PHONE_BOOK = 0x02;
    private EditText mName;
    private EditText mPhone;
    private EditText mCom;
    private EditText mLoc;
    private LinearLayout mMainLayout;
    VoiceInService mApiService;
    String mToken;
    String mUserUuid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode_create);

        findViewById(R.id.customer_qrcode_confirm).setOnClickListener(this);
        findViewById(R.id.qrcode_create_btn_read).setOnClickListener(this);
        mName = (EditText) findViewById(R.id.qrcode_create_et_name);
        mPhone = (EditText) findViewById(R.id.qrcode_create_et_phone);
        mCom = (EditText) findViewById(R.id.qrcode_create_et_com);
        mLoc = (EditText) findViewById(R.id.qrcode_create_et_loc);
        mMainLayout = (LinearLayout) findViewById(R.id.qrcode_create_lo_main);

        mApiService = ((G8penApplication)getApplication()).getAPIService();
        mToken = ((G8penApplication)getApplication()).getToken();
        mUserUuid = ((G8penApplication)getApplication()).getUserUuid();

    }
    public void readContactIntent(){
        Intent i = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(i, INTENT_PHONE_BOOK);

    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.qrcode_create_btn_read:
                if( PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)){
                    ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_CONTACTS},PEMISSON_PHONE_BOOK);
                }else{
                    readContactIntent();
                }

                break;
            case R.id.customer_qrcode_confirm:
                CustomerQRcodeForm form = new CustomerQRcodeForm();
                form.setName(mName.getText().toString());
                String number = mPhone.getText().toString().replace(" ", "").replace("-","");
                if(!PhoneNumberUtil.isValid(number)){
                    mPhone.setError("非正確電話格式，必須是09開頭");
                    mPhone.requestFocus();
                    return;
                }
                form.setPhoneNumber(PhoneNumberUtil.getStandardNumber(number));
                form.setLocation(mLoc.getText().toString());
                form.setCompany(mCom.getText().toString());
                mFragment = new ProgressFragment();
                mFragment.show(getSupportFragmentManager(), "load");

                mApiService.createcustomQrcodes(mUserUuid, form)
                        .enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                mFragment.dismiss();
                                if (response.isSuccess()) {
                                    Log.e("eeeeeeee","success");
                                    setResult(RESULT_OK);
                                    finish();
                                } else {
                                    ColoredSnackBarUtil
                                            .primary(Snackbar.make(mMainLayout, R.string.server_err, Snackbar.LENGTH_LONG))
                                            .show();
                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                mFragment.dismiss();
                                ColoredSnackBarUtil
                                        .primary(Snackbar.make(mMainLayout, R.string.network_err, Snackbar.LENGTH_LONG))
                                        .show();
                            }
                        });
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==PEMISSON_PHONE_BOOK){
            if(grantResults.length==1){
                if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    readContactIntent();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
            if (requestCode == INTENT_PHONE_BOOK) {
                String id = data.getData().getLastPathSegment();

                Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?", new String[] { id },
                        null);

                if (cursor.getCount()==0){
                    ColoredSnackBarUtil.primary(Snackbar.make(mMainLayout, "無電話資訊", Snackbar.LENGTH_LONG)).show();
                    cursor.close();
                    return;
                }
                cursor.moveToFirst();
                String number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER));
                if(!PhoneNumberUtil.isTaiwan(number)){
                    ColoredSnackBarUtil.primary(Snackbar.make(mMainLayout, "非台灣號碼", Snackbar.LENGTH_LONG)).show();
                    cursor.close();
                    return;
                }
                mPhone.setText(PhoneNumberUtil.getTwFormat(number));
                cursor.close();
                cursor = getContentResolver().query(ContactsContract.Data.CONTENT_URI,
                        null, ContactsContract.Data.CONTACT_ID + "=?", new String[] { id },
                        null);
                cursor.moveToFirst();
                mName.setText( cursor.getString(cursor.getColumnIndex(ContactsContract.Data.DISPLAY_NAME)));
                cursor.close();

            }
        }
    }
}
