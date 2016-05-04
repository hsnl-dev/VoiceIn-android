package tw.kits.voicein.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tw.kits.voicein.G8penApplication;
import tw.kits.voicein.R;
import tw.kits.voicein.fragment.ProgressFragment;
import tw.kits.voicein.model.UserInfo;
import tw.kits.voicein.util.ServiceConstant;
import tw.kits.voicein.util.VoiceInService;

public class MyCardActivity extends AppCompatActivity {
    private final static String TAG = MyCardActivity.class.getSimpleName();
    TextView mCom;
    TextView mLoc;
    TextView mName;
    TextView mProfile;
    ImageView mqrCode;
    CircleImageView mAvatar;
    VoiceInService mApiService;
    View mProfileLayout;
    View mCustomQrcode;
    View mMainLayout;
    String mUserUuid;
    String mToken;
    TextView mEmail;
    TextView mJobTitle;
    Button mShareBtn;
    UserInfo mUsr;
    int mLoadCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_my_card);

        mCom = (TextView) findViewById(R.id.my_card_tv_com);
        mMainLayout = findViewById(R.id.contact_add_lo_main);
        mLoc = (TextView) findViewById(R.id.my_card_tv_loc);
        mEmail = (TextView) findViewById(R.id.my_card_tv_email);
        mJobTitle = (TextView) findViewById(R.id.my_card_tv_job_title);
        mName = (TextView) findViewById(R.id.my_card_tv_name);
        mProfile = (TextView) findViewById(R.id.my_card_tv_profile);
        mqrCode = (ImageView) findViewById(R.id.my_card_img_qrcode);
        mAvatar = (CircleImageView) findViewById(R.id.my_card_img_avatar);
        mCustomQrcode = findViewById(R.id.my_card_cv_custom_sec);
        mShareBtn = (Button)findViewById(R.id.my_card_btn_share);
        mMainLayout.setVisibility(View.INVISIBLE);
        mCustomQrcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent qrcode = new Intent(MyCardActivity.this, QRCodeActivity.class);
                startActivity(qrcode);
            }
        });
        mShareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleShare();
            }
        });
        mProfileLayout = findViewById(R.id.my_card_cv_profile_sec);
        mProfileLayout.setDrawingCacheEnabled(true);
        mApiService = ((G8penApplication)getApplication()).getAPIService();
        mUserUuid = ((G8penApplication)getApplication()).getUserUuid();
        mToken = ((G8penApplication)getApplication()).getToken();
        final ProgressFragment progressFragment = new ProgressFragment();
        progressFragment.show(getSupportFragmentManager(),"wait");
        mApiService.getUser(mUserUuid).enqueue(new Callback<UserInfo>() {
            @Override
            public void onResponse(Call<UserInfo> call, Response<UserInfo> response) {
                if (response.isSuccess()) {
                    mUsr  = response.body();
                    mEmail.setText(mUsr.getEmail());
                    mJobTitle.setText(mUsr.getJobTitle());
                    mCom.setText(mUsr.getCompany());
                    mLoc.setText(mUsr.getLocation());
                    mName.setText(mUsr.getUserName());
                    mProfile.setText(mUsr.getProfile());
                    Picasso downloader = ((G8penApplication)getApplication()).getImgLoader(MyCardActivity.this);
                    downloader.load(ServiceConstant.getAvatarUri(mUserUuid, ServiceConstant.PIC_SIZE_LARGE))
                            .placeholder(R.drawable.ic_user_placeholder)
                            .error(R.drawable.ic_user_placeholder)
                            .into(mAvatar);
                    downloader.load(ServiceConstant.getQRcodeUri(mUserUuid))
                            .placeholder(R.drawable.ic_user_placeholder)
                            .error(R.drawable.ic_user_placeholder)
                            .into(mqrCode);

                    mMainLayout.setVisibility(View.VISIBLE);
                    progressFragment.dismiss();
                }
            }

            @Override
            public void onFailure(Call<UserInfo> call, Throwable t) {
                progressFragment.dismiss();
            }
        });


    }




    public void handleShare(){
        Intent i = new Intent();
        i.setAction(Intent.ACTION_SEND);
        i.setType("image/*");
        mProfileLayout.setDrawingCacheEnabled(true);

        mProfileLayout.buildDrawingCache();
        File imageFileFolder = new File(MyCardActivity.this.getExternalCacheDir(), "Card");
        if (!imageFileFolder.exists()) {
            imageFileFolder.mkdir();
        }
        File file = new File(imageFileFolder, "card-" + System.currentTimeMillis() + ".jpg");
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
            mProfileLayout.getDrawingCache().compress(Bitmap.CompressFormat.JPEG, 80, out);
            i.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
            String qrcodeUrl = "https://voice-in.herokuapp.com/qrcode?id="+mUsr.getProfilePhotoId();
            i.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(String.format(getString(R.string.default_qrcode_desc_for_customer),qrcodeUrl)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (out != null)
                try {
                    out.close();
                } catch (IOException e) {

                }
        }
        mProfileLayout.destroyDrawingCache();
        startActivity(i);

    }

}
