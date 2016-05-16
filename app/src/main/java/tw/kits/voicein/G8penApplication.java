package tw.kits.voicein;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.squareup.picasso.Picasso;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import tw.kits.voicein.util.ImageUtil;
import tw.kits.voicein.util.ServiceConstant;
import tw.kits.voicein.util.VoiceInService;

/**
 * Created by Henry on 2016/3/19.
 */
public class G8penApplication extends Application{
    public static final String LOGIN_PREF = "LOGIN_PREF";
    public static final String USER_UUID_KEY = "userUuid";
    public static final String PHONE_NUM_KEY = "phoneNum";
    public static final String TOKEN_KEY="token";
    private String mUserUuid;
    private String mPhoneNum;
    private String mToken;
    private VoiceInService mService;
    private HttpLoggingInterceptor mLogger = new HttpLoggingInterceptor();
    private ImageUtil mImageUtil;

    @Override
    public void onCreate() {
        super.onCreate();
        mLogger.setLevel(HttpLoggingInterceptor.Level.BODY);
        readUserInfoAndSet();
        setService();
    }
    public Picasso getImgLoader(Context context){
        if(mImageUtil ==null)
            mImageUtil = new ImageUtil(this, mToken);
        return mImageUtil.getDownloader(this);
    }
    public void refreshAccessInfo(String token,String userUuid, String phoneNum){
        this.mUserUuid = userUuid;
        this.mPhoneNum = phoneNum;
        this.mToken = token;
        saveLoginPref(token, userUuid, phoneNum);
        setService();
        mImageUtil =null;

    }



    /***
     * get Retrofit API object
     * @return service object
     */
    public VoiceInService getAPIService(){
        return this.mService;


    }

    /***
     * refresh token and info
     * if you have new token change, call this method to set token to HTTP-Header,and this method
     * will save these info to your SharedPreference
     * @param token
     * @param userUuid
     * @param phoneNum
     */
    public void refreshToken(String token,String userUuid, String phoneNum){
        mUserUuid = userUuid;
        mPhoneNum = phoneNum;
        mToken = token;
        saveLoginPref(token, userUuid, phoneNum);
        setService();

    }

    private void readUserInfoAndSet (){
        SharedPreferences sp = getSharedPreferences(LOGIN_PREF,MODE_PRIVATE);
        mToken = sp.getString(TOKEN_KEY, null);
        mUserUuid = sp.getString(USER_UUID_KEY,null);
        mPhoneNum = sp.getString(PHONE_NUM_KEY, null);
    }
    /***
     * This method is to get token by local var
     */
    private void setService(){

        mLogger.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new VoiceInterceptor(getToken()))
                .addInterceptor(mLogger)
                .build();

        mService = new Retrofit.Builder()
                .baseUrl(ServiceConstant.API_BASE)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
                .create(VoiceInService.class);

    }
    private void saveLoginPref(String token,String userUuid, String phoneNum){
        SharedPreferences.Editor editor = getSharedPreferences(LOGIN_PREF,MODE_PRIVATE).edit();
        editor.putString(TOKEN_KEY,token);
        editor.putString(USER_UUID_KEY, userUuid);
        editor.putString(PHONE_NUM_KEY, phoneNum);
        editor.apply();

    }


    public String getUserUuid() {
        return mUserUuid;
    }

    public String getPhoneNum() {
        return mPhoneNum;
    }

    public String getToken() {
        return mToken;
    }
    class VoiceInterceptor implements Interceptor {
        String vToken;

        VoiceInterceptor(String token) {
            super();
            vToken = token;
        }

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request req;
            if (vToken != null) {
                req = chain.request().newBuilder()
                        .addHeader("apiKey", ServiceConstant.API_KEY)
                        .addHeader("token", this.vToken)
                        .addHeader("Cache-Control", "public,max-age=300")
                        .removeHeader("Pragma")
                        .build();
            } else {
                req = chain.request().newBuilder()
                        .addHeader("apiKey", ServiceConstant.API_KEY)
                        .build();
            }
            return chain.proceed(req);
        }

    }
}
