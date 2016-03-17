package tw.kits.voicein.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ShareActionProvider;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tw.kits.voicein.R;
import tw.kits.voicein.fragment.ProgressFragment;
import tw.kits.voicein.model.UserInfo;
import tw.kits.voicein.model.UserUpdateForm;
import tw.kits.voicein.util.AvatarEditHelper;
import tw.kits.voicein.util.ColoredSnackBar;
import tw.kits.voicein.util.ServiceManager;
import tw.kits.voicein.util.UserAccessStore;
import tw.kits.voicein.util.VoiceInService;

public class ProfileActivity extends AppCompatActivity {
    private final int INTENT_PICK = 1;
    private final int INTENT_CROP = 2;
    private final String TAG = ProfileActivity.class.getName();
    private Button mSelectAvatar;
    private CircleImageView mImg;
    private EditText mCom;
    private EditText mIntro;
    private EditText mLoc;
    private EditText mName;
    private LinearLayout mlayout;
    private Button mConfirm;
    private Bitmap mBitmap; // May be null
    private UserInfo user;
    private VoiceInService service;
    private ProgressFragment progressFragment;
    private AvatarEditHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        //get sufficient info
        user = (UserInfo) getIntent().getSerializableExtra("userInfo");
        mSelectAvatar = (Button) findViewById(R.id.profile_btn_upload);
        mlayout = (LinearLayout) findViewById(R.id.profile_layout_main);
        mImg = (CircleImageView) findViewById(R.id.profile_img_avatar);
        mCom = (EditText) findViewById(R.id.profile_et_com);
        mIntro = (EditText) findViewById(R.id.profile_et_intro);
        mLoc = (EditText) findViewById(R.id.profile_et_loc);
        mName = (EditText) findViewById(R.id.profile_et_name);
        progressFragment = new ProgressFragment();
        helper = new AvatarEditHelper(this);
        //set default
        Picasso pDowloader = ServiceManager.getPicassoDowloader(getBaseContext(), UserAccessStore.getToken());
        pDowloader.load(ServiceManager.getAvatarUri(UserAccessStore.getUserUuid(), ServiceManager.PIC_SIZE_LARGE))
                .placeholder(R.drawable.ic_user_placeholder)
                .error(R.drawable.ic_user_placeholder)
                .into(mImg);
        mLoc.setText(user.getLocation());
        mIntro.setText(user.getProfile());
        mName.setText(user.getUserName());
        mCom.setText(user.getCompany());

        mSelectAvatar.setOnClickListener(new SelectBtnListener());
        mConfirm = (Button) findViewById(R.id.profile_btn_confirm);
        mConfirm.setOnClickListener(new ConfirmListener());
    }




    private void submitInfo() {
        String name = mName.getText().toString();
        if ("".equals(name)) {
            mName.setError(getResources().getString(R.string.user_name_hint));
            return;
        }
        String comp = mCom.getText().toString();
        String loc = mLoc.getText().toString();
        String intro = mIntro.getText().toString();
        UserUpdateForm usrProfile = new UserUpdateForm();
        usrProfile.setUserName(name);
        usrProfile.setCompany(comp);
        usrProfile.setLocation(loc);
        usrProfile.setProfile(intro);
        usrProfile.setAvailableStartTime("00:00");
        usrProfile.setAvailableEndTime("00:00");
        usrProfile.setPhoneNumber(getIntent().getStringExtra("phoneNumber"));
        service = ServiceManager.createService(UserAccessStore.getToken());

        //start
        progressFragment.show(getSupportFragmentManager(), "wait");


        Callback<ResponseBody> cb = new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccess()) {
                    if (mBitmap != null) {
                        startUploadAvatar(mBitmap);

                    } else {
                        genQRcodeWhenNotExisted();
                    }


                } else if (response.code() == 404) {
                    progressFragment.dismiss();

                    ColoredSnackBar.primary(Snackbar.make(mlayout, getResources().getString(R.string.user_not_found), Snackbar.LENGTH_SHORT)).show();
                } else {
                    progressFragment.dismiss();
                    ColoredSnackBar.primary(Snackbar.make(mlayout, getResources().getString(R.string.server_err), Snackbar.LENGTH_SHORT)).show();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progressFragment.dismiss();
                ColoredSnackBar.primary(Snackbar.make(mlayout, getResources().getString(R.string.server_err), Snackbar.LENGTH_SHORT)).show();
            }

        };
        service.updateProfile(usrProfile, UserAccessStore.getUserUuid()).enqueue(cb);

    }

    private void startUploadAvatar(@NonNull Bitmap uBitmap) {
        try {
            File image = helper.prepareImg(uBitmap);
            RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), image);
            Call<ResponseBody> res = service.uploadAvatar(UserAccessStore.getUserUuid(), requestBody);
            res.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccess()) {
                        genQRcodeWhenNotExisted();
                    } else {
                        progressFragment.dismiss();
                        //// TODO: 2016/3/9  handle more suituation
                        ColoredSnackBar.primary(Snackbar.make(mlayout, getResources().getString(R.string.server_err), Snackbar.LENGTH_SHORT)).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    progressFragment.dismiss();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void genQRcodeWhenNotExisted() {
        Log.e("123", "HiHI");
        if (user.getQrCodeUuid() == null) {
            service.setQRcode(UserAccessStore.getUserUuid()).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    progressFragment.dismiss();
                    if (response.isSuccess()) {
                        Log.e(TAG, "success");
                        Intent i = new Intent(ProfileActivity.this, MainActivity.class);
                        startActivity(i);
                        finish();
                    } else {
                        Log.e(TAG, Integer.toString(response.code()));
                        //// TODO: 2016/3/7 error more
                        ColoredSnackBar.primary(Snackbar.make(mlayout, getResources().getString(R.string.server_err), Snackbar.LENGTH_SHORT)).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    progressFragment.dismiss();
                    t.printStackTrace();
                    Log.e(TAG, t.toString());
                    //TODO handle error
                    ColoredSnackBar.primary(Snackbar.make(mlayout, getResources().getString(R.string.server_err), Snackbar.LENGTH_SHORT)).show();
                }
            });
        } else {
            Intent i = new Intent(ProfileActivity.this, MainActivity.class);
            startActivity(i);
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Bitmap bitmap = helper.parseResult(requestCode, resultCode, data);
        if (bitmap != null) {
            mBitmap = bitmap;
            mImg.setImageBitmap(mBitmap);
        }
    }

    private class ConfirmListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            submitInfo();
        }
    }

    private class SelectBtnListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            helper.startEdit();
        }
    }
}
