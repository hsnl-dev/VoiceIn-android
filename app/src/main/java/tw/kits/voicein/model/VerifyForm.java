package tw.kits.voicein.model;

/**
 * Created by Henry on 2016/3/27.
 */
public class VerifyForm {
    private String userUuid;
    private String code;

    public String getUserUuid() {
        return userUuid;
    }

    public void setUserUuid(String userUuid) {
        this.userUuid = userUuid;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
