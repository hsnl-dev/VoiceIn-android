package tw.kits.voicein.util;

import java.io.File;

import tw.kits.voicein.model.UserInfo;

/**
 * Created by Henry on 2016/5/21.
 */
public interface UserInfoView {
    void OnRenderUserSuccess(UserInfo userInfo);
    void OnOfflinePicRender(File qrcode, File avatar);
    void OnPicRender();
    void OnRenderUserFail(int code);
    void OnRenderUserServerFail(int code);
}
