package tw.kits.voicein.util;

import java.util.HashMap;

import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import tw.kits.voicein.model.Token;
import tw.kits.voicein.model.UserLoginRes;

/**
 * Created by Henry on 2016/3/3.
 */
public interface VoiceInService {
    @POST("api/v1/accounts/validations/")
    Call<UserLoginRes> getRealCode(@Body HashMap<String,String> user);
    @POST("api/v1/sandboxs/accounts/validations/")
    Call<UserLoginRes> getvalidationCode(@Body HashMap<String,String> user);
    @POST("api/v1/accounts/tokens/")
    Call<Token> getToken (@Body HashMap<String,String> user);
}
