package tw.kits.voicein.activity;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
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
import tw.kits.voicein.util.ColoredSnackBar;
import tw.kits.voicein.util.VoiceInService;

public class QrcodeCreateActivity extends AppCompatActivity implements View.OnClickListener {

    DialogFragment mFragment;
    int INTENT_PHONE_BOOK = 0x01;
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.qrcode_create_btn_read:
                Intent i = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(i, INTENT_PHONE_BOOK);
                break;
            case R.id.customer_qrcode_confirm:
                CustomerQRcodeForm form = new CustomerQRcodeForm();
                form.setName(mName.getText().toString());
                form.setPhoneNumber(mPhone.getText().toString().replace(" ", "").replace("-",""));
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
                                    ColoredSnackBar
                                            .primary(Snackbar.make(mMainLayout, R.string.server_err, Snackbar.LENGTH_LONG))
                                            .show();
                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                mFragment.dismiss();
                                ColoredSnackBar
                                        .primary(Snackbar.make(mMainLayout, R.string.network_err, Snackbar.LENGTH_LONG))
                                        .show();
                            }
                        });
                break;
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
                    ColoredSnackBar.primary(Snackbar.make(mMainLayout, "無電話資訊", Snackbar.LENGTH_LONG)).show();
                    cursor.close();
                    return;
                }
                cursor.moveToFirst();


                mPhone.setText( cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER)));
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
