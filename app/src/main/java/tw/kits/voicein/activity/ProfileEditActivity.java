package tw.kits.voicein.activity;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
import tw.kits.voicein.G8penApplication;
import tw.kits.voicein.R;
import tw.kits.voicein.fragment.PickerDialogFragment;
import tw.kits.voicein.fragment.TimePickerDialogFragment;
import tw.kits.voicein.model.UserInfo;
import tw.kits.voicein.model.UserUpdateForm;
import tw.kits.voicein.util.AvatarEditUtil;
import tw.kits.voicein.util.ColoredSnackBarUtil;
import tw.kits.voicein.util.PhoneNumberUtil;
import tw.kits.voicein.util.ServiceConstant;
import tw.kits.voicein.util.TimeHandler;
import tw.kits.voicein.util.TimeParser;
import tw.kits.voicein.util.VoiceInService;

public class ProfileEditActivity extends AppCompatActivity {
    public static final int PROFILE_RETURN_SUCCESS = 0x1;
    AvatarEditUtil helper;
    private String token;
    private String userUuid;
    private VoiceInService service;
    private EditText name;
    private EditText company;
    private EditText location;
    private EditText introduction;
    private EditText email;
    private EditText jobTitle;
    private TextView phone;
    private TextView availableStime;
    private TextView availableEtime;
    private LinearLayout layout;
    private CircleImageView avatar;
    private Bitmap uploadAvatar;
    ProgressDialog progressDialog;
    private Picasso mImgLoader;
    private UserInfo mUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);
        token = ((G8penApplication)getApplication()).getToken();
        userUuid =((G8penApplication)getApplication()).getUserUuid();
        service =((G8penApplication)getApplication()).getAPIService();
        mImgLoader = Picasso.with(this);
        name = (EditText) findViewById(R.id.profile_edit_et_name);
        email = (EditText) findViewById(R.id.profile_edit_et_mail);
        jobTitle = (EditText) findViewById(R.id.profile_edit_et_jt);
        company = (EditText) findViewById(R.id.profile_edit_et_com);
        location = (EditText) findViewById(R.id.profile_edit_et_loc);
        introduction = (EditText) findViewById(R.id.profile_edit_et_intro);
        phone = (TextView) findViewById(R.id.profile_edit_tv_phone);
        availableStime = (TextView) findViewById(R.id.profile_edit_tv_as);
        availableEtime = (TextView) findViewById(R.id.profile_edit_tv_ae);
        layout = (LinearLayout) findViewById(R.id.profile_edit_layout);
        avatar = (CircleImageView) findViewById(R.id.profile_edit_img_avatar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        helper = new AvatarEditUtil(this);
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
                    mImgLoader.load(ServiceConstant.getAvatarUri(userUuid, ServiceConstant.PIC_SIZE_MID))
                            .placeholder(R.drawable.ic_user_placeholder)
                            .error(R.drawable.ic_user_placeholder)
                            .into(avatar);
                    mUser = response.body();
                    renderUser(mUser);

                }
            }

            @Override
            public void onFailure(Call<UserInfo> call, Throwable t) {
                progressDialog.dismiss();
                ColoredSnackBarUtil.primary(
                        Snackbar.make(layout, R.string.network_err, Snackbar.LENGTH_INDEFINITE)
                ).show();
            }
        });
        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PickerDialogFragment.OnSelectListener listener = new PickerDialogFragment.OnSelectListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        switch (i){
                            case 0:
                                helper.goChoosePic();
                                break;
                            case 1:
                                helper.doTakePhoto();
                                break;
                            case 2:
                                dialog.dismiss();
                                break;
                        }
                    }
                };
                String[] actions={"從相簿中選取","拍照","取消"};
                PickerDialogFragment fragment = PickerDialogFragment.newInstance(listener,actions);
                fragment.show(getSupportFragmentManager(),"chooser");
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
        email.setText(user.getEmail());
        jobTitle.setText(user.getJobTitle());
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
        if(!setUser()){
            progressDialog.dismiss();
            return;
        }
        service.updateProfile(mUser, userUuid).enqueue(new Callback<ResponseBody>() {
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
                        ColoredSnackBarUtil.primary(
                                Snackbar.make(layout, R.string.network_err, Snackbar.LENGTH_INDEFINITE)
                        ).show();
                    }
                } else {
                    progressDialog.dismiss();
                    ColoredSnackBarUtil.primary(
                            Snackbar.make(layout, R.string.user_not_auth, Snackbar.LENGTH_INDEFINITE)
                    ).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progressDialog.dismiss();
                ColoredSnackBarUtil.primary(
                        Snackbar.make(layout, R.string.network_err, Snackbar.LENGTH_INDEFINITE)
                ).show();
            }
        });
    }
    private void startUploadAvatar() throws IOException {
        mImgLoader.invalidate(ServiceConstant.getAvatarUri(userUuid, ServiceConstant.PIC_SIZE_LARGE));
        mImgLoader.invalidate(ServiceConstant.getAvatarUri(userUuid, ServiceConstant.PIC_SIZE_MID));
        mImgLoader.invalidate(ServiceConstant.getAvatarUri(userUuid, ServiceConstant.PIC_SIZE_SMALL));
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
                    ColoredSnackBarUtil.primary(
                            Snackbar.make(layout, R.string.user_not_auth, Snackbar.LENGTH_INDEFINITE)
                    ).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progressDialog.dismiss();
                ColoredSnackBarUtil.primary(
                        Snackbar.make(layout, R.string.network_err, Snackbar.LENGTH_INDEFINITE)
                ).show();
            }
        });

    }
    private boolean setUser() {
        try {
            TimeParser end = new TimeParser(availableEtime.getText().toString());
            TimeParser start = new TimeParser(availableStime.getText().toString());
            if(start.compareTo(end)>=0){
                availableEtime.setError(getString(R.string.ilegal_input));
                ColoredSnackBarUtil.primary(
                        Snackbar.make(layout, R.string.ilegal_input, Snackbar.LENGTH_LONG)
                ).show();
                return false;
            }

        } catch (IOException e) {
            return false;
        }
        mUser.setAvailableStartTime(availableStime.getText().toString());
        mUser.setAvailableEndTime(availableEtime.getText().toString());
        mUser.setCompany(company.getText().toString());
        mUser.setLocation(location.getText().toString());
        mUser.setJobTitle(jobTitle.getText().toString());
        mUser.setEmail(email.getText().toString());
        mUser.setProfile(introduction.getText().toString());
        mUser.setUserName(name.getText().toString());
        mUser.setPhoneNumber(PhoneNumberUtil.getStandardNumber(mUser.getPhoneNumber()));
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
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                break;
            case R.id.contact_edit_menu_confirm:
                startUpdateInfo();
                break;
        }
        return true;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        helper.parsePermissonResult(requestCode,permissions,grantResults);
    }
}
