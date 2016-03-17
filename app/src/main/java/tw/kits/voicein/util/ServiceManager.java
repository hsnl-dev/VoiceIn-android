package tw.kits.voicein.util;

import android.content.Context;

import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Henry on 2016/3/3.
 */
public class ServiceManager {
    public final static String API_BASE = "https://voicein.herokuapp.com/";
    public final static String API_KEY = "784a48e7-a15f-4623-916a-1bd304dc9f56";
    public final static String PIC_SIZE_MID = "mid";
    public final static String PIC_SIZE_LARGE = "large";
    public final static String PIC_SIZE_SMALL = "small";
    public static Cache cache;
    static HttpLoggingInterceptor logging = new HttpLoggingInterceptor();

    /***
     * Create service with token or without token
     *
     * @param token user accesstoken
     * @return
     */
    public static VoiceInService createService(String token) {
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_BASE)
                .addConverterFactory(GsonConverterFactory.create())
                .client(getClient(token))
                .build();
        return retrofit.create(VoiceInService.class);
    }

    private static OkHttpClient getClient(String token) {
        return new OkHttpClient.Builder()
                .addInterceptor(new VoiceInterceptor(token))
                .addInterceptor(logging)
                .build();
    }

    public static Picasso getPicassoDowloader(Context context, String token) {
        if(cache!=null)
            cache = new Cache(context.getCacheDir(), 10*1024*1024);
        OkHttpClient client = new OkHttpClient.Builder()
                .cache(cache)
                .addInterceptor(new VoiceInterceptor(token))
                .addInterceptor(logging)
                .build();

        OkHttp3Downloader okDownloader = new OkHttp3Downloader(client);
        return new Picasso.Builder(context).downloader(okDownloader).build();
    }

    public static String getQRcodeUri(String uuid) {
        return String.format("%sapi/v1/accounts/%s/qrcode", API_BASE, uuid);
    }
    public static String getQRcodeById(String uuid) {
        return String.format("%sapi/v1/qrcodes/%s/image", API_BASE, uuid);
    }
    public static String getAvatarUri(String uuid, String size) {
        return String.format("%sapi/v1/accounts/%s/avatar?size=%s", API_BASE, uuid, size);
    }
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
                    .addHeader("apiKey", ServiceManager.API_KEY)
                    .addHeader("token", this.vToken)
                    .addHeader("Cache-Control", "public,max-age=300")
                    .removeHeader("Pragma")
                    .build();
        } else {
            req = chain.request().newBuilder()
                    .addHeader("apiKey", ServiceManager.API_KEY)
                    .build();
        }
        return chain.proceed(req);
    }

}