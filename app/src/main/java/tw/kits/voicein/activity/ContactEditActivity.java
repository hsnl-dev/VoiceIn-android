package tw.kits.voicein.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tw.kits.voicein.R;
import tw.kits.voicein.fragment.DeleteDialogFragment;
import tw.kits.voicein.fragment.TimePickerDialogFragment;
import tw.kits.voicein.model.Contact;
import tw.kits.voicein.util.ColoredSnackBar;
import tw.kits.voicein.util.ServiceManager;
import tw.kits.voicein.util.TimeHandler;
import tw.kits.voicein.util.UserAccessStore;
import tw.kits.voicein.util.VoiceInService;

public class ContactEditActivity extends AppCompatActivity {
    public static final String ARG_CONTACT = "Contact";
    private static final String TAG = ContactEditActivity.class.getName();

    Contact mContact;
    LinearLayout mLayout;
    EditText mNickName;
    ProgressDialog progressDialog;
    TextView mCompany;
    TextView mLocation;
    TextView mName;
    TextView mProfile;
    ImageView mAvatar;
    Button mDelButton;
    Picasso mPicasso;
    TextView mDisturbText;
    TextView mEnhanceText;
    TextView mAStartText;
    TextView mAEndText;
    Switch mDisturbSw;
    Switch mEnhanceSw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_edit);
        mNickName = (EditText) findViewById(R.id.contact_edit_et_nickname);
        mCompany = (TextView) findViewById(R.id.contact_edit_tv_com);
        mLocation = (TextView) findViewById(R.id.contact_edit_tv_loc);
        mName = (TextView) findViewById(R.id.contact_edit_tv_name);
        mProfile = (TextView) findViewById(R.id.contact_edit_tv_profile);
        mDisturbText = (TextView) findViewById(R.id.contact_edit_tv_disturb);
        mEnhanceText = (TextView) findViewById(R.id.contact_edit_tv_enhance);
        mAStartText = (TextView) findViewById(R.id.contact_edit_tv_start_time);
        mAEndText = (TextView) findViewById(R.id.contact_edit_tv_end_time);
        mDisturbSw = (Switch) findViewById(R.id.contact_edit_sw_disturb);
        mEnhanceSw = (Switch) findViewById(R.id.contact_edit_sw_enhance);

        mAvatar = (ImageView) findViewById(R.id.contact_edit_img_avatar);
        mLayout = (LinearLayout) findViewById(R.id.contact_add_view);
        mContact = (Contact) getIntent().getSerializableExtra(ARG_CONTACT);
        mDelButton = (Button) findViewById(R.id.contact_edit_btn_del);
        mPicasso = ServiceManager.getPicassoDowloader(this, UserAccessStore.getToken());
        mPicasso.load(ServiceManager.API_BASE + "api/v1/avatars/" + mContact.getProfilePhotoId() + "?size=large")
                .placeholder(R.drawable.ic_user_placeholder)
                .error(R.drawable.ic_user_placeholder)
                .into(mAvatar);

        mNickName.setText(mContact.getNickName());
        mCompany.setText(mContact.getCompany());
        mLocation.setText(mContact.getLocation());
        mName.setText(mContact.getUserName());
        mProfile.setText(mContact.getProfile());

        mDisturbSw.setChecked(!mContact.getIsEnable());
        mEnhanceSw.setChecked(mContact.getIsHigherPriorityThanGlobal());
        mDisturbText.setText(!mContact.getIsEnable() ? getString(R.string.enabled)
                : getString(R.string.disabled));
        mEnhanceText.setText(mContact.getIsHigherPriorityThanGlobal() ? getString(R.string.enabled)
                : getString(R.string.disabled));
        mAStartText.setText(mContact.getAvailableStartTime());
        mAEndText.setText(mContact.getAvailableEndTime());


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDisturbSw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mDisturbText.setText(isChecked ? getString(R.string.enabled)
                        : getString(R.string.disabled));
            }
        });
        mEnhanceSw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mEnhanceText.setText(isChecked ? getString(R.string.enabled)
                        : getString(R.string.disabled));
            }
        });
        mDelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeleteDialogFragment fragment = DeleteDialogFragment.newInstance(
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                delContact();
                            }
                        }
                );
                fragment.show(getSupportFragmentManager(),"asking");
            }
        });
        mAStartText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimeHandler handler = new TimeHandler(mAStartText);
                Fragment fragment = TimePickerDialogFragment
                        .createInstance(handler, handler.getTextView());
                FragmentManager fm = getSupportFragmentManager();
                fm.beginTransaction().add(fragment, "timepicker_start").commit();

            }
        });
        mAEndText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimeHandler handler = new TimeHandler(mAEndText);
                Fragment fragment = TimePickerDialogFragment
                        .createInstance(handler, handler.getTextView());
                FragmentManager fm = getSupportFragmentManager();
                fm.beginTransaction().add(fragment, "timepicker_end").commit();

            }
        });
    }

    private void updateContact() {
        progressDialog = ProgressDialog.show(
                ContactEditActivity.this,
                getString(R.string.wait),
                getString(R.string.wait_notice),
                true);
        VoiceInService service = ServiceManager.createService(UserAccessStore.getToken());

        service.updateQRcodeInfo(
                mContact.getId(),
                mNickName.getText().toString(),
                !mDisturbSw.isChecked(),
                mAStartText.getText().toString(),
                mAEndText.getText().toString(),
                mEnhanceSw.isChecked())
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
                                    Log.e(TAG, "mLayout=" + (mLayout == null));
                                    ColoredSnackBar
                                            .primary(Snackbar.make(mLayout, R.string.user_have_added, Snackbar.LENGTH_INDEFINITE))
                                            .show();
                                    break;
                                case 404:
                                    Log.e(TAG, "mLayout=" + (mLayout == null));
                                    ColoredSnackBar
                                            .primary(Snackbar.make(mLayout, R.string.user_not_found, Snackbar.LENGTH_INDEFINITE))
                                            .show();
                                    break;
                                default:
                                    Log.e(TAG, "mLayout=" + (mLayout == null));
                                    ColoredSnackBar
                                            .primary(Snackbar.make(mLayout, R.string.server_err, Snackbar.LENGTH_INDEFINITE))
                                            .show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        progressDialog.dismiss();
                        ColoredSnackBar
                                .primary(Snackbar.make(mLayout, R.string.server_err, Snackbar.LENGTH_INDEFINITE))
                                .show();
                    }
                });

    }

    private void delContact() {
        progressDialog = ProgressDialog.show(
                ContactEditActivity.this,
                getBaseContext().getString(R.string.wait),
                getBaseContext().getString(R.string.wait_notice),
                true);
        VoiceInService service = ServiceManager.createService(UserAccessStore.getToken());
        service.delContact(mContact.getId()).enqueue(new Callback<ResponseBody>() {
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
                            ColoredSnackBar
                                    .primary(Snackbar.make(mLayout, R.string.user_have_added, Snackbar.LENGTH_INDEFINITE))
                                    .show();
                            break;
                        case 404:
                            ColoredSnackBar
                                    .primary(Snackbar.make(mLayout, R.string.user_not_found, Snackbar.LENGTH_INDEFINITE))
                                    .show();
                            break;
                        default:
                            ColoredSnackBar
                                    .primary(Snackbar.make(mLayout, R.string.server_err, Snackbar.LENGTH_INDEFINITE))
                                    .show();

                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progressDialog.dismiss();
                ColoredSnackBar
                        .primary(Snackbar.make(mLayout, R.string.server_err, Snackbar.LENGTH_INDEFINITE))
                        .show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        Log.e(TAG, "Home");
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.contact_edit_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
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
