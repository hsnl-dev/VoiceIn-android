package tw.kits.voicein.util;

import android.content.Context;

import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.LruCache;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * Created by Henry on 2016/3/28.
 */
public class ImageHelper {
    private final int MAX_AGE=300;
    private final int CACHE_SIZE = 10 * 1024 * 1024; // 10 MiB
    private Interceptor cacheInterceptor;
    private Cache cache;
    private OkHttp3Downloader downloader;
    private final  HttpLoggingInterceptor LOGGER = new HttpLoggingInterceptor();
    public ImageHelper(Context context,String token) {
        File httpCacheDirectory = new File(context.getCacheDir(), "pics");
        if(!httpCacheDirectory.exists())
            httpCacheDirectory.mkdir();

        cache = new Cache(httpCacheDirectory, CACHE_SIZE);
        LOGGER.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new ImgInterceptor(token))
                .addInterceptor(LOGGER)
                .cache(cache)
                .build();
        downloader = new OkHttp3Downloader(client);

    }
    public Picasso getDownloader(Context context){
        return new Picasso.Builder(context).downloader(downloader).loggingEnabled(true).indicatorsEnabled(true).build();
    }


}
class ImgInterceptor implements Interceptor {
    String vToken;

    ImgInterceptor(String token) {
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
                    .addHeader("Cache-Control", "public,max-age=900")
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