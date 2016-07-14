package tw.kits.voicein.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;
import tw.kits.voicein.G8penApplication;
import tw.kits.voicein.R;
import tw.kits.voicein.fragment.ProgressFragment;
import tw.kits.voicein.model.UserInfo;
import tw.kits.voicein.util.OfflineUserInfoReaderImpl;
import tw.kits.voicein.util.ServiceConstant;
import tw.kits.voicein.util.UserInfoReader;
import tw.kits.voicein.util.UserInfoReaderImpl;
import tw.kits.voicein.util.UserInfoView;
import tw.kits.voicein.util.VoiceInService;

public class MyCardActivity extends AppCompatActivity implements UserInfoView {
    private final static String TAG = MyCardActivity.class.getSimpleName();
    public final static String ARG_OFFLINE = "off";
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
//    Button mShareBtn;
    UserInfo mUsr;
    UserInfoReader mUserReader;
    ProgressFragment mProgressFragment;
    boolean mIsOffline;

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
        //mShareBtn = (Button) findViewById(R.id.my_card_btn_share);
        mMainLayout.setVisibility(View.INVISIBLE);
        mCustomQrcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent qrcode = new Intent(MyCardActivity.this, QRCodeActivity.class);
                startActivity(qrcode);
            }
        });
        //mShareBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                handleShare();
//            }
//        });
        mProfileLayout = findViewById(R.id.my_card_cv_profile_sec);
        mProfileLayout.setDrawingCacheEnabled(true);
        mApiService = ((G8penApplication) getApplication()).getAPIService();
        mUserUuid = ((G8penApplication) getApplication()).getUserUuid();
        mToken = ((G8penApplication) getApplication()).getToken();
        if (getIntent().getExtras() != null)
            mIsOffline = getIntent().getBooleanExtra(ARG_OFFLINE, false);
        else
            mIsOffline = false;
        //get data
        Log.e(TAG, "onCreate: " + mIsOffline);
        if (mIsOffline) {
            findViewById(R.id.my_card_cv_custom_sec).setVisibility(View.GONE);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            mUserReader = new OfflineUserInfoReaderImpl(mApiService, this, this, mUserUuid);
        } else {
            mUserReader = new UserInfoReaderImpl(mApiService, this, this, mUserUuid);
        }

        mProgressFragment = new ProgressFragment();
        mProgressFragment.show(getSupportFragmentManager(), "wait");
        mUserReader.readUser();


    }


    public void handleShare() {
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
            String qrcodeUrl = ServiceConstant.WEB_BASE_URL+"qrcode?id=" + mUsr.getQrCodeUuid();
            i.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(String.format(getString(R.string.default_qrcode_desc_for_customer), qrcodeUrl)));
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.profile_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.profile_menu_share:
                handleShare();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void OnRenderUserSuccess(UserInfo userInfo) {

        mUsr = userInfo;
        mEmail.setText(mUsr.getEmail());
        mJobTitle.setText(mUsr.getJobTitle());
        mCom.setText(mUsr.getCompany());
        mLoc.setText(mUsr.getLocation());
        mName.setText(mUsr.getUserName());
        mProfile.setText(mUsr.getProfile());
        mMainLayout.setVisibility(View.VISIBLE);
        mProgressFragment.dismissAllowingStateLoss();
    }

    @Override
    public void OnOfflinePicRender(File qrcode, File avatar) {
        Picasso downloader = ((G8penApplication) getApplication()).getImgLoader(MyCardActivity.this);
        downloader.load(avatar)
                .placeholder(R.drawable.ic_user_placeholder)
                .error(R.drawable.ic_user_placeholder)
                .into(mAvatar);
        downloader.load(qrcode)
                .placeholder(R.drawable.ic_user_placeholder)
                .error(R.drawable.ic_user_placeholder)
                .into(mqrCode);
    }

    @Override
    public void OnPicRender() {
        Picasso downloader = ((G8penApplication) getApplication()).getImgLoader(MyCardActivity.this);
        downloader.load(ServiceConstant.getAvatarUri(mUserUuid, ServiceConstant.PIC_SIZE_LARGE))
                .placeholder(R.drawable.ic_user_placeholder)
                .error(R.drawable.ic_user_placeholder)
                .into(mAvatar, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {
                        BitmapDrawable drawable = (BitmapDrawable) mAvatar.getDrawable();
                        mUserReader.saveFile(drawable.getBitmap(), UserInfoReader.AVATR_POST_FIX);
                    }

                    @Override
                    public void onError() {

                    }
                });
        downloader.load(ServiceConstant.getQRcodeUri(mUserUuid))
                .placeholder(R.drawable.ic_user_placeholder)
                .error(R.drawable.ic_user_placeholder)
                .into(mqrCode, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {
                        BitmapDrawable drawable = (BitmapDrawable) mqrCode.getDrawable();
                        mUserReader.saveFile(drawable.getBitmap(), UserInfoReader.QR_POST_FIX);
                    }

                    @Override
                    public void onError() {

                    }
                });
    }

    @Override
    public void OnRenderUserFail(int code) {
        mProgressFragment.dismiss();
    }

    @Override
    public void OnRenderUserServerFail(int code) {
        mProgressFragment.dismiss();
    }
}
