package tw.kits.voicein.util;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Henry on 2016/3/3.
 */
public class ServiceManager {
    public final static String API_BASE = "https://voicein-web-service.us-west-2.elasticbeanstalk.com/";
    public final static String API_KEY = "784a48e7-a15f-4623-916a-1bd304dc9f56";


    /***
     * Create service with token or without token
     * @param token user accesstoken
     * @return
     */
    public static VoiceInService createService(String token){
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new VoiceInterceptor(token))
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_BASE)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        return retrofit.create(VoiceInService.class);
    }
}
class VoiceInterceptor implements Interceptor{
    String vToken;
    VoiceInterceptor(String token){
        super();
        vToken = token;
    }
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request req;
        if(vToken!=null) {
            req = chain.request().newBuilder()
                    .addHeader("apiKey", ServiceManager.API_KEY)
                    .addHeader("token", this.vToken)
                    .build();
        }else{
            req = chain.request().newBuilder()
                    .addHeader("apiKey", ServiceManager.API_KEY)
                    .build();
        }
        return chain.proceed(req);
    }
}