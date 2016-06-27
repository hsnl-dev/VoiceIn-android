package tw.kits.voicein.model;


import java.io.Serializable;

public class UserInfo implements Serializable {


    private String uuid;
    private String userName;
    private String phoneNumber;
    private String location;
    private String profile;
    private String company;
    private String jobTitle;
    private String email;
    private String profilePhotoId;
    private String qrCodeUuid;
    private String availableStartTime;
    private String availableEndTime;
    private String password;
    private String deviceOS;
    private String deviceKey;
    private boolean enableMVPNChecker;
    private float credit;


    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfilePhotoId() {
        return profilePhotoId;
    }

    public void setProfilePhotoId(String profilePhotoId) {
        this.profilePhotoId = profilePhotoId;
    }

    public String getQrCodeUuid() {
        return qrCodeUuid;
    }

    public void setQrCodeUuid(String qrCodeUuid) {
        this.qrCodeUuid = qrCodeUuid;
    }

    public String getAvailableStartTime() {
        return availableStartTime;
    }

    public void setAvailableStartTime(String availableStartTime) {
        this.availableStartTime = availableStartTime;
    }

    public String getAvailableEndTime() {
        return availableEndTime;
    }

    public void setAvailableEndTime(String availableEndTime) {
        this.availableEndTime = availableEndTime;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

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

    public float getCredit() {
        return credit;
    }

    public void setCredit(float credit) {
        this.credit = credit;
    }

    public boolean isEnableMVPNChecker() {
        return enableMVPNChecker;
    }

    public void setEnableMVPNChecker(boolean enableMVPNChecker) {
        this.enableMVPNChecker = enableMVPNChecker;
    }
}