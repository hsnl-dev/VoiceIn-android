package tw.kits.voicein.model;

/**
 * Created by Henry on 2016/4/20.
 */
public class DeviceInfoEntity {
    private String deviceKey;
    private String deviceOS;



    public String getDeviceOS() {
        return deviceOS;
    }

    public void setDeviceOS(String deviceOS) {
        this.deviceOS = deviceOS;
    }

    public String getDeviceKey() {
        return deviceKey;
    }

    public void setDeviceKey(String deviceKey) {
        this.deviceKey = deviceKey;
    }
}
