package tw.kits.voicein.util;

import android.content.Context;
import android.graphics.Bitmap;

import com.google.gson.Gson;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tw.kits.voicein.model.UserInfo;

/**
 * Created by Henry on 2016/5/21.
 */
public class UserInfoReaderImpl extends UserInfoReader{
    VoiceInService mApiService;
    UserInfoView mView;
    Context mContext;
    String mUserUuid;
    public UserInfoReaderImpl(VoiceInService service,UserInfoView view, Context context, String userUuid){
        mApiService = service;
        mView = view;
        mContext = context;
        mUserUuid = userUuid;
    }
    @Override
    public void readUser() {
        mApiService.getUser(mUserUuid).enqueue(new Callback<UserInfo>() {
            @Override
            public void onResponse(Call<UserInfo> call, Response<UserInfo> response) {
                if (response.isSuccess()) {
                    UserInfo userInfo = response.body();
                    saveUser(userInfo,mUserUuid);
                    mView.OnRenderUserSuccess(
                            userInfo);
                    mView.OnPicRender();
                }else{
                    mView.OnRenderUserServerFail(response.code());
                }
            }
            @Override
            public void onFailure(Call<UserInfo> call, Throwable t) {
                mView.OnRenderUserFail(500);
            }
        });
    }
    private void saveUser(UserInfo userInfo,String userUuid){
        Gson gson = new Gson();
        FileOutputStream fs;
        OutputStreamWriter os = null;
        try {
            fs = mContext.openFileOutput(userUuid+ POST_FIX,Context.MODE_PRIVATE);
            os = new OutputStreamWriter(fs);
            gson.toJson(userInfo,os);

        }catch (IOException ex){
            ex.printStackTrace();
        }finally {
            if(os!=null){
                try {
                    os.flush();
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void saveFile(Bitmap bitmap, String name){
        FileOutputStream fo = null;

        try {
            fo = mContext.openFileOutput(mUserUuid+name ,Context.MODE_PRIVATE);
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,fo);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if(fo!=null){
                try {
                    fo.close();
                }catch (IOException io){
                    io.printStackTrace();
                }
            }
        }
    }
}
