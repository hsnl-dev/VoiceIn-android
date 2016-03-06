package tw.kits.voicein.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Henry on 2016/3/6.
 */
public class CountryCode {
    private String name;
    @SerializedName("dial_code")
    private String dialCode;
    private String code;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDialCode() {
        return dialCode;
    }

    public void setDialCode(String dialCode) {
        this.dialCode = dialCode;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
