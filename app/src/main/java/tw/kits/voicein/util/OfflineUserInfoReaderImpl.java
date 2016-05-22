package tw.kits.voicein.util;

import android.content.Context;
import android.graphics.Bitmap;

import com.google.gson.Gson;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import tw.kits.voicein.model.UserInfo;

/**
 * Created by Henry on 2016/5/21.
 */
public class OfflineUserInfoReaderImpl extends UserInfoReader {
    VoiceInService mApiService;
    Context mContext;
    UserInfoView mView;
    String mUserUuid;

    public OfflineUserInfoReaderImpl(VoiceInService service, UserInfoView view, Context context, String userUuid) {
        mApiService = service;
        mContext = context;
        mView = view;
        mUserUuid = userUuid;

    }

    @Override
    public void readUser() {
        Gson gson = new Gson();
        InputStreamReader ir = null;
        try {
            ir = new InputStreamReader(mContext.openFileInput(mUserUuid + POST_FIX));
            UserInfo userInfo =
                    gson.fromJson(ir, UserInfo.class);
            if (userInfo == null) {
                mView.OnRenderUserFail(500);
            } else {
                mView.OnRenderUserSuccess(userInfo);
                mView.OnOfflinePicRender(mContext.getFileStreamPath(mUserUuid + QR_POST_FIX),
                        mContext.getFileStreamPath(mUserUuid + AVATR_POST_FIX));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (ir != null) {
                try {
                    ir.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    public void saveFile(Bitmap bitmap, String name) {
    }

}
