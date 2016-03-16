package tw.kits.voicein.activity;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tw.kits.voicein.R;
import tw.kits.voicein.fragment.TimePickerDialogFragment;
import tw.kits.voicein.model.UserInfo;
import tw.kits.voicein.model.UserUpdateForm;
import tw.kits.voicein.util.AvatarEditHelper;
import tw.kits.voicein.util.ColoredSnackBar;
import tw.kits.voicein.util.ServiceManager;
import tw.kits.voicein.util.TimeHandler;
import tw.kits.voicein.util.TimeParser;
import tw.kits.voicein.util.UserAccessStore;
import tw.kits.voicein.util.VoiceInService;

public class ProfileEditActivity extends AppCompatActivity {
    public static final int PROFILE_RETURN_SUCCESS = 0x1;
    AvatarEditHelper helper;
    private String token;
    private String userUuid;
    private VoiceInService service;
    private EditText name;
    private EditText company;
    private EditText location;
    private EditText introduction;
    private TextView phone;
    private TextView availableStime;
    private TextView availableEtime;
    private LinearLayout layout;
    private CircleImageView avatar;
    private Bitmap uploadAvatar;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);
        token = UserAccessStore.getToken();
        userUuid = UserAccessStore.getUserUuid();
        service = ServiceManager.createService(token);
        name = (EditText) findViewById(R.id.profile_edit_et_name);
        company = (EditText) findViewById(R.id.profile_edit_et_com);
        location = (EditText) findViewById(R.id.profile_edit_et_loc);
        introduction = (EditText) findViewById(R.id.profile_edit_et_intro);
        phone = (TextView) findViewById(R.id.profile_edit_tv_phone);
        availableStime = (TextView) findViewById(R.id.profile_edit_tv_as);
        availableEtime = (TextView) findViewById(R.id.profile_edit_tv_ae);
        layout = (LinearLayout) findViewById(R.id.profile_edit_layout);
        avatar = (CircleImageView) findViewById(R.id.profile_edit_img_avatar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        helper = new AvatarEditHelper(this);
        progressDialog = ProgressDialog.show(
                this,
                getString(R.string.wait),
                getString(R.string.wait_notice),
                true);
        service.getUser(userUuid).enqueue(new Callback<UserInfo>() {
            @Override
            public void onResponse(Call<UserInfo> call, Response<UserInfo> response) {
                if (response.isSuccess()) {
                    progressDialog.dismiss();
                    Picasso picasso = ServiceManager.getPicassoDowloader(ProfileEditActivity.this, token);
                    picasso.load(ServiceManager.getAvatarUri(userUuid, ServiceManager.PIC_SIZE_MID))
                            .placeholder(R.drawable.ic_user_placeholder)
                            .error(R.drawable.ic_user_placeholder)
                            .into(avatar);
                    renderUser(response.body());

                }
            }

            @Override
            public void onFailure(Call<UserInfo> call, Throwable t) {
                progressDialog.dismiss();
                ColoredSnackBar.primary(
                        Snackbar.make(layout, R.string.user_not_auth, Snackbar.LENGTH_INDEFINITE)
                ).show();
            }
        });
        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                helper.startEdit();
            }
        });
        availableStime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimeHandler handler = new TimeHandler(availableStime);
                TimePickerDialogFragment fragment = TimePickerDialogFragment
                        .createInstance(handler,handler.getTextView());
                FragmentManager fm = getSupportFragmentManager();
                fm.beginTransaction().add(fragment,"timepicker").commit();

            }
        });
        availableEtime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimeHandler handler = new TimeHandler(availableEtime);
                TimePickerDialogFragment fragment = TimePickerDialogFragment
                        .createInstance(handler, handler.getTextView());
                FragmentManager fm = getSupportFragmentManager();
                fm.beginTransaction().add(fragment,"timepicker_end").commit();

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Bitmap bitmap = helper.parseResult(requestCode, resultCode, data);
        if (bitmap != null) {
            uploadAvatar = bitmap;
            avatar.setImageBitmap(bitmap);
        }
    }

    private void renderUser(UserInfo user) {
        name.setText(user.getUserName());
        location.setText(user.getLocation());
        company.setText(user.getCompany());
        introduction.setText(user.getProfile());

        phone.setText(user.getPhoneNumber());
        availableStime.setText(user.getAvailableStartTime());
        availableEtime.setText(user.getAvailableEndTime());
    }

    private void startUpdateInfo() {
        progressDialog = ProgressDialog.show(
                this,
                getString(R.string.wait),
                getString(R.string.wait_notice),
                true);
        UserUpdateForm form = getForm();
        if(form==null){
            progressDialog.dismiss();
            return;
        }
        service.updateProfile(getForm(), userUuid).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccess()) {
                    try {
                        if(uploadAvatar==null){
                            progressDialog.dismiss();
                            setResult(PROFILE_RETURN_SUCCESS);
                            NavUtils.navigateUpFromSameTask(ProfileEditActivity.this);
                            return;
                        }
                        startUploadAvatar();
                    } catch (IOException e) {
                        e.printStackTrace();
                        progressDialog.dismiss();
                        ColoredSnackBar.primary(
                                Snackbar.make(layout, R.string.network_err, Snackbar.LENGTH_INDEFINITE)
                        ).show();
                    }
                } else {
                    progressDialog.dismiss();
                    ColoredSnackBar.primary(
                            Snackbar.make(layout, R.string.user_not_auth, Snackbar.LENGTH_INDEFINITE)
                    ).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progressDialog.dismiss();
                ColoredSnackBar.primary(
                        Snackbar.make(layout, R.string.network_err, Snackbar.LENGTH_INDEFINITE)
                ).show();
            }
        });
    }
    private void startUploadAvatar() throws IOException {

        RequestBody body = RequestBody.create(
                MediaType.parse("multipart/form-data"),
                helper.prepareImg(uploadAvatar)
        );
        service.uploadAvatar(userUuid, body).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                progressDialog.dismiss();
                if(response.isSuccess()){
                    setResult(PROFILE_RETURN_SUCCESS);
                    NavUtils.navigateUpFromSameTask(ProfileEditActivity.this);
                } else{
                    ColoredSnackBar.primary(
                            Snackbar.make(layout, R.string.user_not_auth, Snackbar.LENGTH_INDEFINITE)
                    ).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progressDialog.dismiss();
                ColoredSnackBar.primary(
                        Snackbar.make(layout, R.string.network_err, Snackbar.LENGTH_INDEFINITE)
                ).show();
            }
        });

    }
    private UserUpdateForm getForm() {
        UserUpdateForm form = new UserUpdateForm();
        try {
            TimeParser end = new TimeParser(availableEtime.getText().toString());
            TimeParser start = new TimeParser(availableStime.getText().toString());
            if(start.compareTo(end)>=0){
                availableEtime.setError(getString(R.string.illgal_input));
                ColoredSnackBar.primary(
                        Snackbar.make(layout, R.string.illgal_input, Snackbar.LENGTH_LONG)
                ).show();
                return null;
            }

        } catch (IOException e) {
            return null;
        }
        form.setAvailableStartTime(availableStime.getText().toString());
        form.setAvailableEndTime(availableEtime.getText().toString());
        form.setCompany(company.getText().toString());
        form.setLocation(location.getText().toString());
        form.setProfile(introduction.getText().toString());
        form.setUserName(name.getText().toString());
        form.setPhoneNumber(phone.getText().toString());
        return form;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.contact_edit_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                break;
            case R.id.contact_edit_menu_confirm:
                startUpdateInfo();
                break;
        }
        return true;
    }
}
