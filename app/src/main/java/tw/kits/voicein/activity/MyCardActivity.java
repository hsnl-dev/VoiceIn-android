package tw.kits.voicein.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
    View mLayout;
    String mUserUuid;
    String mToken;
    int mLoadCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_card);

        mCom = (TextView) findViewById(R.id.my_card_tv_com);
        mLoc = (TextView) findViewById(R.id.my_card_tv_loc);
        mName = (TextView) findViewById(R.id.my_card_tv_name);
        mProfile = (TextView) findViewById(R.id.my_card_tv_profile);
        mqrCode = (ImageView) findViewById(R.id.my_card_img_qrcode);
        mAvatar = (CircleImageView) findViewById(R.id.my_card_img_avatar);
        mLayout = findViewById(R.id.contact_add_lo_main).getRootView();
        mLayout.setDrawingCacheEnabled(true);
        mApiService = ((G8penApplication)getApplication()).getAPIService();
        mUserUuid = ((G8penApplication)getApplication()).getUserUuid();
        mToken = ((G8penApplication)getApplication()).getToken();
        mApiService.getUser(mUserUuid).enqueue(new Callback<UserInfo>() {
            @Override
            public void onResponse(Call<UserInfo> call, Response<UserInfo> response) {
                if (response.isSuccess()) {
                    UserInfo usr = response.body();
                    mCom.setText(usr.getCompany());
                    mLoc.setText(usr.getLocation());
                    mName.setText(usr.getUserName());
                    mProfile.setText(usr.getProfile());
                    Picasso downloader = ((G8penApplication)getApplication()).getImgLoader(MyCardActivity.this);
                    downloader.load(ServiceConstant.getAvatarUri(mUserUuid, ServiceConstant.PIC_SIZE_LARGE))
                            .placeholder(R.drawable.ic_user_placeholder)
                            .error(R.drawable.ic_user_placeholder)
                            .into(mAvatar);
                    downloader.load(ServiceConstant.getQRcodeUri(mUserUuid))
                            .placeholder(R.drawable.ic_user_placeholder)
                            .error(R.drawable.ic_user_placeholder)
                            .into(mqrCode);

                }
            }

            @Override
            public void onFailure(Call<UserInfo> call, Throwable t) {

            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile_menu, menu);
        MenuItem item = menu.findItem(R.id.profile_menu_share);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.profile_menu_qrcode:
                Intent qrcode = new Intent(this, QRCodeActivity.class);
                startActivity(qrcode);
                break;
            case R.id.profile_menu_share:
                Intent i = new Intent();
                i.setAction(Intent.ACTION_SEND);
                i.setType("image/jpeg");
                mLayout.setDrawingCacheEnabled(true);
                mLayout.destroyDrawingCache();
                mLayout.buildDrawingCache();
                File imageFileFolder = new File(MyCardActivity.this.getExternalCacheDir(), "Card");
                if (!imageFileFolder.exists()) {
                    imageFileFolder.mkdir();
                }
                File file = new File(imageFileFolder, "card-" + System.currentTimeMillis() + ".jpg");
                FileOutputStream out = null;
                try {
                    out = new FileOutputStream(file);
                    mLayout.getDrawingCache().compress(Bitmap.CompressFormat.JPEG, 80, out);
                    i.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } finally {
                    if (out != null)
                        try {
                            out.close();
                        } catch (IOException e) {

                        }
                }
                startActivity(i);

        }
        return true;
    }



}
