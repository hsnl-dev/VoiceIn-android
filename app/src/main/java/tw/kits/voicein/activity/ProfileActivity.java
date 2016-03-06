package tw.kits.voicein.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tw.kits.voicein.R;
import tw.kits.voicein.fragment.ProgressFragment;
import tw.kits.voicein.model.UserInfo;
import tw.kits.voicein.model.UserProfile;
import tw.kits.voicein.util.ServiceManager;
import tw.kits.voicein.util.UserAccessStore;
import tw.kits.voicein.util.VoiceInService;

public class ProfileActivity extends AppCompatActivity {
    private Button mSelectAvatar;
    private CircleImageView mImg;
    private EditText mCom;
    private EditText mIntro;
    private EditText mLoc;
    private EditText mName;
    private Button mConfirm;
    private final int INTENT_PICK = 1;
    private final int INTENT_CROP = 2;
    private Bitmap mBitmap; // May be null
    private UserInfo user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        //get sufficient info
        user = (UserInfo)getIntent().getSerializableExtra("userInfo");
        mSelectAvatar = (Button) findViewById(R.id.profile_btn_upload);

        mImg = (CircleImageView) findViewById(R.id.profile_image);
        mCom = (EditText) findViewById(R.id.profile_et_com);
        mIntro = (EditText) findViewById(R.id.profile_et_intro);
        mLoc = (EditText) findViewById(R.id.profile_et_loc);
        mName = (EditText) findViewById(R.id.profile_et_name);
        //set default
        Picasso pDowloader = ServiceManager.getPicassoDowloader(getBaseContext(), UserAccessStore.getToken());
        pDowloader.load(ServiceManager.API_BASE + "api/v1/accounts/" +
                UserAccessStore.getUserUuid() +"/avatar?size=normal").
                into(mImg);
        mLoc.setText(user.getLocation());
        mIntro.setText(user.getProfile());
        mName.setText(user.getUserName());
        mCom.setText(user.getCompany());

        mSelectAvatar.setOnClickListener(new SelectBtnListener());
        mConfirm = (Button) findViewById(R.id.profile_btn_confirm);
        mConfirm.setOnClickListener(new ConfirmListener());
    }

    private class ConfirmListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            submitInfo();
        }
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
        UserProfile usrProfile = new UserProfile();
        usrProfile.setUserName(name);
        usrProfile.setCompany(comp);
        usrProfile.setLocation(loc);
        usrProfile.setProfile(intro);
        usrProfile.setAvailableStartTime("00:00");
        usrProfile.setAvailableEndTime("00:00");
        usrProfile.setPhoneNumber(getIntent().getStringExtra("phoneNumber"));
        VoiceInService vs = ServiceManager.createService(UserAccessStore.getToken());
        final ProgressFragment progressFragment = new ProgressFragment();
        progressFragment.show(getSupportFragmentManager(), "wait");
        Callback<ResponseBody> cb = new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                progressFragment.dismiss();
                if (response.isSuccess()) {
                    Intent i = new Intent(ProfileActivity.this, MainActivity.class);
                    startActivity(i);
                    finish();

                } else if (response.code() == 404) {

                    //TODO show snackbar
                } else {
                    //TODO show snackbar

                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progressFragment.dismiss();
                //TODO show snackbar
            }

        };
        if(mBitmap!=null){
            File imageFileFolder = new File(getCacheDir(),"Avatar");
            if( !imageFileFolder.exists() ){
                imageFileFolder.mkdir();
            }
            File image = new File(imageFileFolder, "avatar-" + System.currentTimeMillis() + ".jpg");
            FileOutputStream out = null;
            try {
                out = new FileOutputStream(image);
                mBitmap.compress(Bitmap.CompressFormat.JPEG,100,out);
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            vs.updateProfile(usrProfile, UserAccessStore.getUserUuid()).enqueue(cb);
        }



    }

    private class SelectBtnListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");

            startActivityForResult(intent, INTENT_PICK);

        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case INTENT_PICK:
                    doCrop(data.getData());
                    break;
                case INTENT_CROP:
                    setImgView(data);
                    break;
            }
        }
    }

    private void doCrop(Uri imgUri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.setData(imgUri);
        intent.putExtra("outputFormat", "JPEG");
        intent.putExtra("return-data", true);
        intent.putExtra("noFaceDetection", false);
        startActivityForResult(intent, INTENT_CROP);

    }

    private void setImgView(Intent data) {
        Bundle bundle = data.getExtras();
        if (bundle != null) {
            mBitmap = bundle.getParcelable("data");
            mImg.setImageBitmap(mBitmap);

        }


    }
}
