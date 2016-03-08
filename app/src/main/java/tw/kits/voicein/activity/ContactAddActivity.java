package tw.kits.voicein.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import tw.kits.voicein.model.ContactAddEntity;
import tw.kits.voicein.model.Provider;
import tw.kits.voicein.util.ColoredSnackBar;
import tw.kits.voicein.util.ServiceManager;
import tw.kits.voicein.util.UserAccessStore;
import tw.kits.voicein.util.VoiceInService;

public class ContactAddActivity extends AppCompatActivity {
    private static final String TAG = ContactAddActivity.class.getName();
    public static final String ARG_QRCODE = "code";
    public static final String RETURN_CONTACT = "contact";
    TextView iCompany;
    TextView iLocation;
    TextView iName;
    TextView iProfile;
    ImageView iAvatar;
    View iLayout;
    VoiceInService service;
    Provider contact;
    LinearLayout layout;
    EditText iNickname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_add);
        service = ServiceManager.createService(UserAccessStore.getToken());
        layout = (LinearLayout)findViewById(R.id.contact_add_lo_main);
        iCompany = (TextView) findViewById(R.id.contact_add_tv_com);
        iLocation = (TextView) findViewById(R.id.contact_add_tv_loc);
        iName = (TextView) findViewById(R.id.contact_add_tv_name);
        iProfile = (TextView) findViewById(R.id.contact_add_tv_profile);
        iAvatar = (ImageView) findViewById(R.id.contact_add_img_avatar);
        iLayout = findViewById(R.id.contact_add_lo_main);
        iNickname = (EditText)findViewById(R.id.contact_add_et_nickname);
        String qrCodeId = getIntent().getStringExtra(ARG_QRCODE);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        service.getProvider(qrCodeId).enqueue(new Callback<Provider>() {
            @Override
            public void onResponse(Call<Provider> call, Response<Provider> response) {
                if (response.isSuccess()) {
                    contact = response.body();
                    iCompany.setText(contact.getCompany());
                    iLocation.setText(contact.getLocation());
                    iName.setText(contact.getName());
                    iProfile.setText(contact.getProfile());
                    Picasso picasso = ServiceManager.getPicassoDowloader(ContactAddActivity.this, UserAccessStore.getToken());
                    picasso.load(ServiceManager.API_BASE + "api/v1/avatars/" + contact.getAvatarId() + "?size=large")
                            .placeholder(R.drawable.ic_user_placeholder)
                            .error(R.drawable.ic_user_placeholder)
                            .into(iAvatar);
                } else {
                    Snackbar bar;
                    Log.w(TAG, response.code() + "");
                    switch (response.code()){

                        case 404:
                            bar = Snackbar.make(iLayout, getResources().getString(R.string.user_not_found), Snackbar.LENGTH_SHORT);
                            ColoredSnackBar.primary(bar).show();
                            break;
                        case 401:
                            bar = Snackbar.make(iLayout, getResources().getString(R.string.user_not_auth), Snackbar.LENGTH_SHORT);
                            ColoredSnackBar.primary(bar).show();
                            break;
                        default:
                            bar = Snackbar.make(iLayout, getResources().getString(R.string.server_err), Snackbar.LENGTH_SHORT);
                            ColoredSnackBar.primary(bar).show();

                    }


                }

            }

            @Override
            public void onFailure(Call<Provider> call, Throwable t) {
                Snackbar bar = Snackbar.make(iLayout, getResources().getString(R.string.network_err), Snackbar.LENGTH_SHORT);
                ColoredSnackBar.primary(bar).show();
            }
        });


    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.contact_edit_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.contact_edit_menu_confirm:
                addContact();
                break;
            case android.R.id.home:
                finish();
                break;

        }
        return true;
    }

    public void addContact() {
        ContactAddEntity entity = new ContactAddEntity();
        entity.setAvailableEndTime("00:00");
        entity.setAvailableStartTime("00:00");
        entity.setChargeType(ContactAddEntity.FREE);
        entity.setIsEnable(true);
        entity.setNickName(iNickname.getText().toString());
        if (contact != null) {
            service.addContactByQrcode(UserAccessStore.getUserUuid(), getIntent().getStringExtra(ARG_QRCODE),entity)
                    .enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            if (response.isSuccess()) {
                                Intent intent = new Intent();
                                intent.putExtra("provider", contact);
                                setResult(RESULT_OK, intent);
                                Log.i(TAG, "Contact set OK");
                                finish();
                            } else {
                                Log.w(TAG, response.code() + "");
                                switch (response.code()) {
                                    case 304:
                                        ColoredSnackBar
                                                .primary(Snackbar.make(layout, R.string.user_have_added, Snackbar.LENGTH_INDEFINITE))
                                                .show();
                                        break;
                                    case 401:
                                        ColoredSnackBar
                                                .primary(Snackbar.make(layout, R.string.user_not_auth, Snackbar.LENGTH_INDEFINITE))
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
                            ColoredSnackBar
                                    .primary(Snackbar.make(layout, R.string.network_err, Snackbar.LENGTH_INDEFINITE))
                                    .show();
                            Log.w(TAG, t.toString());
                        }
                    });

        }

    }
}