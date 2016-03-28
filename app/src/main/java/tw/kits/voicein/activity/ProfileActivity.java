package tw.kits.voicein.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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
import tw.kits.voicein.G8penApplication;
import tw.kits.voicein.R;
import tw.kits.voicein.fragment.ProgressFragment;
import tw.kits.voicein.model.UserInfo;
import tw.kits.voicein.model.UserUpdateForm;
import tw.kits.voicein.util.AvatarEditHelper;
import tw.kits.voicein.util.ColoredSnackBar;
import tw.kits.voicein.util.ServiceConstant;
import tw.kits.voicein.util.VoiceInService;

public class ProfileActivity extends AppCompatActivity {
    private final static int INTENT_PICK = 1;
    private final static int INTENT_CROP = 2;
    public final static String ARG_USER = "user";
    public final static String ARG_PHONE = "phone";
    public final static String WAIT_TAG = "wait";
    private final String TAG = ProfileActivity.class.getName();
    private Button mSelectAvatar;
    private CircleImageView mImg;
    private EditText mComText;
    private EditText mIntroText;
    private EditText mLocText;
    private EditText mNameText;
    private View mLayout;
    private Button mConfirm;
    private Bitmap mBitmap; // May be null
    private UserInfo mUser;
    private VoiceInService mApiService;
    private ProgressFragment mProgressDialog;
    private AvatarEditHelper helper;
    private String mToken;
    private String mUserUuid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        //get sufficient info
        mUser = (UserInfo) getIntent().getSerializableExtra(ARG_USER);
        mSelectAvatar = (Button) findViewById(R.id.profile_btn_upload);
        mLayout = findViewById(R.id.profile_layout_main);
        mImg = (CircleImageView) findViewById(R.id.profile_img_avatar);
        mComText = (EditText) findViewById(R.id.profile_et_com);
        mIntroText = (EditText) findViewById(R.id.profile_et_intro);
        mLocText = (EditText) findViewById(R.id.profile_et_loc);
        mNameText = (EditText) findViewById(R.id.profile_et_name);
        mProgressDialog = new ProgressFragment();
        helper = new AvatarEditHelper(this);

        mToken = ((G8penApplication)getApplication()).getToken();
        mUserUuid = ((G8penApplication)getApplication()).getUserUuid();
        mApiService = ((G8penApplication)getApplication()).getAPIService();

        //set default
        Picasso pDownloader =  ((G8penApplication)getApplication()).getImgLoader(this);
        pDownloader.load(ServiceConstant.getAvatarUri(mUserUuid, ServiceConstant.PIC_SIZE_LARGE))
                .placeholder(R.drawable.ic_user_placeholder)
                .error(R.drawable.ic_user_placeholder)
                .into(mImg);
        mLocText.setText(mUser.getLocation());
        mIntroText.setText(mUser.getProfile());
        mNameText.setText(mUser.getUserName());
        mComText.setText(mUser.getCompany());

        mSelectAvatar.setOnClickListener(new SelectBtnListener());
        mConfirm = (Button) findViewById(R.id.profile_btn_confirm);
        mConfirm.setOnClickListener(new ConfirmListener());

    }




    private void submitInfo() {
        String name = mNameText.getText().toString();
        if(TextUtils.isEmpty(name)){
            mNameText.setError(getString(R.string.ilegal_input));
            return;
        }
        String comp = mComText.getText().toString();
        String loc = mLocText.getText().toString();
        String intro = mIntroText.getText().toString();
        UserUpdateForm usrProfile = new UserUpdateForm();
        usrProfile.setUserName(name);
        usrProfile.setCompany(comp);
        usrProfile.setLocation(loc);
        usrProfile.setProfile(intro);
        usrProfile.setAvailableStartTime("00:00");
        usrProfile.setAvailableEndTime("23:59");
        usrProfile.setPhoneNumber(getIntent().getStringExtra(ARG_PHONE));

        //start
        mProgressDialog.show(getSupportFragmentManager(), WAIT_TAG);


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
                    mProgressDialog.dismiss();

                    ColoredSnackBar.primary(Snackbar.make(mLayout, getResources().getString(R.string.user_not_found), Snackbar.LENGTH_SHORT)).show();
                } else {
                    mProgressDialog.dismiss();
                    ColoredSnackBar.primary(Snackbar.make(mLayout, getResources().getString(R.string.server_err), Snackbar.LENGTH_SHORT)).show();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                mProgressDialog.dismiss();
                ColoredSnackBar.primary(Snackbar.make(mLayout, getResources().getString(R.string.server_err), Snackbar.LENGTH_SHORT)).show();
            }

        };
        mApiService.updateProfile(usrProfile, mUserUuid).enqueue(cb);

    }

    private void startUploadAvatar(@NonNull Bitmap uBitmap) {
        try {
            File image = helper.prepareImg(uBitmap);
            RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), image);
            Call<ResponseBody> res = mApiService.uploadAvatar(mUserUuid, requestBody);
            res.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccess()) {
                        genQRcodeWhenNotExisted();
                    } else {
                        mProgressDialog.dismiss();
                        //// TODO: 2016/3/9  handle more suituation
                        ColoredSnackBar
                                .primary(Snackbar.make(mLayout, getResources().getString(R.string.server_err), Snackbar.LENGTH_SHORT))
                                .show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    mProgressDialog.dismiss();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void genQRcodeWhenNotExisted() {
        if (mUser.getQrCodeUuid() == null) {
            mApiService.setQRcode(mUserUuid).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    mProgressDialog.dismiss();
                    if (response.isSuccess()) {
                        Log.e(TAG, "success");
                        Intent i = new Intent(ProfileActivity.this, MainActivity.class);
                        startActivity(i);
                        finish();
                    } else {
                        Log.e(TAG, Integer.toString(response.code()));
                        //// TODO: 2016/3/7 error more
                        ColoredSnackBar
                                .primary(Snackbar.make(mLayout, getResources().getString(R.string.server_err), Snackbar.LENGTH_SHORT))
                                .show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    mProgressDialog.dismiss();
                    t.printStackTrace();
                    Log.e(TAG, t.toString());
                    //TODO handle error
                    ColoredSnackBar
                            .primary(Snackbar.make(mLayout, getResources().getString(R.string.server_err), Snackbar.LENGTH_SHORT))
                            .show();
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
