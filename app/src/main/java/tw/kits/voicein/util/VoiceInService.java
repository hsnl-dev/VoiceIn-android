package tw.kits.voicein.util;

import java.util.HashMap;
import java.util.List;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import tw.kits.voicein.model.Contact;
import tw.kits.voicein.model.ContactAddEntity;
import tw.kits.voicein.model.Provider;
import tw.kits.voicein.model.Token;
import tw.kits.voicein.model.UserInfo;
import tw.kits.voicein.model.UserLoginRes;
import tw.kits.voicein.model.UserUpdateForm;

/**
 * Created by Henry on 2016/3/3.
 */
public interface VoiceInService {
    @POST("api/v1/accounts/validations/")
    Call<UserLoginRes> getvalidationCode(@Body HashMap<String,String> user);

    @POST("api/v1/sandboxs/accounts/validations/")
    Call<UserLoginRes> getRealCode(@Body HashMap<String,String> user);

    @POST("api/v1/accounts/tokens/")
    Call<Token> getToken (@Body HashMap<String,String> user);

    @PUT("api/v1/accounts/{userUuid}")
    Call<ResponseBody> updateProfile(@Body UserUpdateForm profile, @Path("userUuid") String userUuid);

    @GET("api/v1/accounts/{userUuid}")
    Call<UserInfo> getUser(@Path("userUuid")String userUuid);

    @GET("api/v1/accounts/{userUuid}/contacts")
    Call<List<Contact>> getContacts(@Path("userUuid") String userUuid);

    @Multipart
    @POST("api/v1/accounts/{userUuid}/avatar")
    Call<ResponseBody> uploadAvatar(@Path("userUuid") String userUuid, @Part("photo") RequestBody file);

    @POST("api/v1/accounts/{userUuid}/qrcode")
    Call<ResponseBody>setQRcode(@Path("userUuid") String userUuid);

    @GET("api/v1/providers/{qrcodeId}")
    Call<Provider>getProvider(@Path("qrcodeId") String qrcodeId);

    @POST("api/v1/accounts/{userUuid}/contacts/{qrCodeUuid}")
    Call<ResponseBody> addContactByQrcode(@Path("userUuid") String userUuid, @Path("qrCodeUuid") String qrCodeuid, @Body ContactAddEntity entity);

    @DELETE("api/v1/accounts/{userUuid}/contacts/{qrCodeUuid}")
    Call<ResponseBody> delContactByQrcode(@Path("userUuid") String userUuid, @Path("qrCodeUuid") String qrCodeuid);

    @PUT("api/v1/accounts/{userUuid}/contacts/{qrCodeUuid}")
    Call<ResponseBody> updateQRcodeNickName(@Path("userUuid") String userUuid,@Path("qrCodeUuid") String qrCodeuid ,@Query("nickName") String nickname);

}
