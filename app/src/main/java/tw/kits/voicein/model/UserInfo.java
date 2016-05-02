package tw.kits.voicein.model;


import java.io.Serializable;

public class UserInfo implements Serializable {


    private String userName;
    private String jobTitle;
    private String email;
    private String phoneNumber;
    private String profilePhotoId;
    private String profile;
    private String location;
    private String company;
    private String qrCodeUuid;
    private String availableStartTime;
    private String availableEndTime;

    /**
     * @return The userName
     */
    public String getUserName() {
        return userName;
    }

    /**
     * @param userName The userName
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * @return The profilePhotoId
     */
    public String getProfilePhotoId() {
        return profilePhotoId;
    }

    /**
     * @param profilePhotoId The profilePhotoId
     */
    public void setProfilePhotoId(String profilePhotoId) {
        this.profilePhotoId = profilePhotoId;
    }

    /**
     * @return The profile
     */
    public String getProfile() {
        return profile;
    }

    /**
     * @param profile The profile
     */
    public void setProfile(String profile) {
        this.profile = profile;
    }

    /**
     * @return The location
     */
    public String getLocation() {
        return location;
    }

    /**
     * @param location The location
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * @return The company
     */
    public String getCompany() {
        return company;
    }

    /**
     * @param company The company
     */
    public void setCompany(String company) {
        this.company = company;
    }

    /**
     * @return The qrCodeUuid
     */
    public String getQrCodeUuid() {
        return qrCodeUuid;
    }

    /**
     * @param qrCodeUuid The qrCodeUuid
     */
    public void setQrCodeUuid(String qrCodeUuid) {
        this.qrCodeUuid = qrCodeUuid;
    }

    /**
     * @return The availableStartTime
     */
    public String getAvailableStartTime() {
        return availableStartTime;
    }

    /**
     * @param availableStartTime The availableStartTime
     */
    public void setAvailableStartTime(String availableStartTime) {
        this.availableStartTime = availableStartTime;
    }

    /**
     * @return The availableEndTime
     */
    public String getAvailableEndTime() {
        return availableEndTime;
    }

    /**
     * @param availableEndTime The availableEndTime
     */
    public void setAvailableEndTime(String availableEndTime) {
        this.availableEndTime = availableEndTime;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
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
}