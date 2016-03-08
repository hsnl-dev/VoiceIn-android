package tw.kits.voicein.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tw.kits.voicein.R;
import tw.kits.voicein.model.UserInfo;
import tw.kits.voicein.util.ServiceManager;
import tw.kits.voicein.util.UserAccessStore;

public class MyCardActivity extends AppCompatActivity {
    TextView mCom;
    TextView mLoc;
    TextView mName;
    TextView mProfile;
    ImageView mqrCode;
    CircleImageView mAvatar;

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
        ServiceManager.createService(UserAccessStore.getToken()).getUser(UserAccessStore.getUserUuid()).enqueue(new Callback<UserInfo>() {
            @Override
            public void onResponse(Call<UserInfo> call, Response<UserInfo> response) {
                if (response.isSuccess()) {
                    UserInfo usr = response.body();
                    mCom.setText(usr.getCompany());
                    mLoc.setText(usr.getLocation());
                    mName.setText(usr.getUserName());
                    mProfile.setText(usr.getProfile());
                    Picasso downloader = ServiceManager.getPicassoDowloader(MyCardActivity.this.getBaseContext(), UserAccessStore.getToken());
                    downloader.load(ServiceManager.API_BASE + "api/v1/accounts/" +
                            UserAccessStore.getUserUuid() + "/avatar?size=large")
                            .placeholder(R.drawable.ic_user_placeholder)
                            .error(R.drawable.ic_user_placeholder)
                            .into(mAvatar);
                    downloader.load(ServiceManager.API_BASE + "api/v1/accounts/" +
                            UserAccessStore.getUserUuid() + "/qrcode")
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
}
