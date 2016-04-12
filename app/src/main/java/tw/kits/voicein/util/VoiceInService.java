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
import tw.kits.voicein.model.CallForm;
import tw.kits.voicein.model.Contact;
import tw.kits.voicein.model.ContactAddEntity;
import tw.kits.voicein.model.CustomerQRcodeForm;
import tw.kits.voicein.model.Group;
import tw.kits.voicein.model.Provider;
import tw.kits.voicein.model.QRcode;
import tw.kits.voicein.model.QRcodeContainer;
import tw.kits.voicein.model.Token;
import tw.kits.voicein.model.UserInfo;
import tw.kits.voicein.model.UserLoginRes;
import tw.kits.voicein.model.UserUpdateForm;
import tw.kits.voicein.model.VerifyForm;

/**
 * Created by Henry on 2016/3/3.
 */
public interface VoiceInService {
    @POST("api/v1/accounts/validations/")
    Call<UserLoginRes> getRealCode(@Body HashMap<String, String> user);

    @POST("api/v1/sandboxs/accounts/validations/")
    Call<UserLoginRes> getvalidationCode(@Body HashMap<String, String> user);

    @POST("api/v1/accounts/tokens/")
    Call<Token> getToken(@Body VerifyForm verifyInfo);

    @PUT("api/v1/accounts/{userUuid}")
    Call<ResponseBody> updateProfile(@Body UserUpdateForm profile, @Path("userUuid") String userUuid);

    @GET("api/v1/accounts/{userUuid}")
    Call<UserInfo> getUser(@Path("userUuid") String userUuid);

    @GET("api/v2/accounts/{userUuid}/contacts")
    Call<List<Contact>> getContacts(@Path("userUuid") String userUuid);

    @GET("api/v2/accounts/{userUuid}/contacts?filter=like")
    Call<List<Contact>> getFavoriteContacts(@Path("userUuid") String userUuid);

    @Multipart
    @POST("api/v1/accounts/{userUuid}/avatar")
    Call<ResponseBody> uploadAvatar(@Path("userUuid") String userUuid, @Part("photo") RequestBody file);

    @POST("api/v1/accounts/{userUuid}/qrcode")
    Call<ResponseBody> setQRcode(@Path("userUuid") String userUuid);

    @GET("api/v1/providers/{qrcodeId}")
    Call<Provider> getProvider(@Path("qrcodeId") String qrcodeId);

    @POST("api/v1/accounts/{userUuid}/contacts/{qrCodeUuid}")
    Call<ResponseBody> addContactByQrcode(@Path("userUuid") String userUuid, @Path("qrCodeUuid") String qrCodeuid, @Body ContactAddEntity entity);


    @DELETE("api/v2/accounts/{contactId}/contacts/")
    Call<ResponseBody> delContact(@Path("contactId") String contactId);

    @PUT("api/v2/accounts/{contactId}/contacts/")
    Call<ResponseBody> updateQRcodeInfo(@Path("contactId") String contactId,
                                        @Query("nickName") String nickName,
                                        @Query("isEnable") boolean isEnable,
                                        @Query("availableStartTime") String availableStartTime,
                                        @Query("availableEndTime") String availableEndTime,
                                        @Query("isHigherPriorityThanGlobal") boolean isHigherPriorityThanGlobal
                                       );
    @PUT("api/v2/accounts/{contactId}/contacts/")
    Call<ResponseBody> updateQRcodeILike(@Path("contactId") String contactId,
                                        @Query("like") boolean like);
    @POST("api/v2/accounts/{userUuid}/calls/")
    Call<ResponseBody> createCall(@Path("userUuid") String userUuid, @Body CallForm form);

    @POST("api/v1/accounts/{accountUuid}/customQrcodes/")
    Call<ResponseBody> createcustomQrcodes(@Path("accountUuid") String uuid, @Body CustomerQRcodeForm form);

    @GET("api/v1/accounts/{accountUuid}/customQrcodes/")
    Call<QRcodeContainer> getCustomQrcodes(@Path("accountUuid") String uuid);

    @DELETE("api/v1/accounts/{accountUuid}/customQrcodes/{qrCodeUuid}")
    Call<ResponseBody> delCustomQrcodes(@Path("accountUuid") String uuid, @Path("qrCodeUuid") String qrCodeUuid);

    @GET("api/v1/accounts/{userUuid}/groups")

    Call<List<Group>> getAccountGroupList(@Path("userUuid") String uuid);
}

