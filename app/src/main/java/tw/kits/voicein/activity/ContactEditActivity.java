package tw.kits.voicein.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tw.kits.voicein.R;
import tw.kits.voicein.model.Contact;
import tw.kits.voicein.util.ColoredSnackBar;
import tw.kits.voicein.util.ServiceManager;
import tw.kits.voicein.util.UserAccessStore;
import tw.kits.voicein.util.VoiceInService;

public class ContactEditActivity extends AppCompatActivity {
    public static final String ARG_CONTACT = "contact";
    private static final String TAG = ContactEditActivity.class.getName();

    Contact contact;
    LinearLayout layout;
    EditText uNickName;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_edit);
        uNickName = (EditText)findViewById(R.id.contact_edit_et_nickname);
        TextView iCompany = (TextView)findViewById(R.id.contact_edit_tv_com);
        TextView iLocation = (TextView)findViewById(R.id.contact_edit_tv_loc);
        TextView iName = (TextView)findViewById(R.id.contact_edit_tv_name);
        TextView iProfile = (TextView)findViewById(R.id.contact_edit_tv_profile);
        ImageView iAvatar = (ImageView)findViewById(R.id.contact_edit_img_avatar);
        layout = (LinearLayout)findViewById(R.id.contact_add_view);
        contact = (Contact)getIntent().getSerializableExtra(ARG_CONTACT);
        Button iDelButton = (Button)findViewById(R.id.contact_edit_btn_del);
        Picasso picasso = ServiceManager.getPicassoDowloader(this, UserAccessStore.getToken());
        picasso.load(ServiceManager.API_BASE + "api/v1/avatars/" + contact.getProfilePhotoId() + "?size=large")
                .placeholder(R.drawable.ic_user_placeholder)
                .error(R.drawable.ic_user_placeholder)
                .into(iAvatar);
        uNickName.setText(contact.getNickName());
        iCompany.setText(contact.getCompany());
        iLocation.setText(contact.getLocation());
        iName.setText(contact.getUserName());
        iProfile.setText(contact.getProfile());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        iDelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delContact();
            }
        });
    }
    private void updateContact(){
        progressDialog = ProgressDialog.show(
                ContactEditActivity.this,
                getBaseContext().getString(R.string.wait),
                getBaseContext().getString(R.string.wait_notice),
                true);
        VoiceInService service = ServiceManager.createService(UserAccessStore.getToken());

        service.updateQRcodeNickName(UserAccessStore.getUserUuid(),contact.getQrCodeUuid(),uNickName.getText().toString())
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        progressDialog.dismiss();
                        if (response.isSuccess()) {
                            Intent data = new Intent();
                            setResult(RESULT_OK, data);
                            finish();
                        } else {
                            switch (response.code()) {
                                case 304:
                                    Log.e(TAG, "layout=" + (layout == null));
                                    ColoredSnackBar
                                            .primary(Snackbar.make(layout, R.string.user_have_added, Snackbar.LENGTH_INDEFINITE))
                                            .show();
                                    break;
                                case 404:
                                    Log.e(TAG, "layout=" + (layout == null));
                                    ColoredSnackBar
                                            .primary(Snackbar.make(layout, R.string.user_not_found, Snackbar.LENGTH_INDEFINITE))
                                            .show();
                                    break;
                                default:
                                    Log.e(TAG, "layout=" + (layout == null));
                                    ColoredSnackBar
                                            .primary(Snackbar.make(layout, R.string.server_err, Snackbar.LENGTH_INDEFINITE))
                                            .show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        progressDialog.dismiss();
                        ColoredSnackBar
                                .primary(Snackbar.make(layout, R.string.server_err, Snackbar.LENGTH_INDEFINITE))
                                .show();
                    }
                });

    }
    private void delContact(){
        progressDialog = ProgressDialog.show(
                ContactEditActivity.this,
                getBaseContext().getString(R.string.wait),
                getBaseContext().getString(R.string.wait_notice),
                true);
        VoiceInService service = ServiceManager.createService(UserAccessStore.getToken());
        service.delContactByQrcode(UserAccessStore.getUserUuid(),contact.getQrCodeUuid()).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                progressDialog.dismiss();
                if(response.isSuccess()){
                    Intent data = new Intent();
                    setResult(RESULT_OK, data);
                    finish();
                }else{
                    switch (response.code()) {
                        case 304:
                            ColoredSnackBar
                                    .primary(Snackbar.make(layout, R.string.user_have_added, Snackbar.LENGTH_INDEFINITE))
                                    .show();
                            break;
                        case 404:
                            ColoredSnackBar
                                    .primary(Snackbar.make(layout, R.string.user_not_found, Snackbar.LENGTH_INDEFINITE))
                                    .show();
                            break;
                        default:
                            ColoredSnackBar
                                    .primary(Snackbar.make(layout, R.string.server_err, Snackbar.LENGTH_INDEFINITE))
                                    .show();

                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progressDialog.dismiss();
                ColoredSnackBar
                        .primary(Snackbar.make(layout, R.string.server_err, Snackbar.LENGTH_INDEFINITE))
                        .show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        Log.e(TAG,"Home");
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.contact_edit_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()){
            case R.id.contact_edit_menu_confirm:
                updateContact();
                break;
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }
}
