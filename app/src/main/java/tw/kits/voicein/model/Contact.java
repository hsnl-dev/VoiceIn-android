package tw.kits.voicein.model;


import java.io.Serializable;

public class Contact implements Serializable{

    private String id;
    private String userName;
    private String phoneNumber;
    private String location;
    private String profile;
    private String company;
    private String profilePhotoId;
    private String nickName;
    private Integer chargeType;
    private String availableStartTime;
    private String availableEndTime;
    private Boolean isEnable;
    private String providerAvailableStartTime;
    private String providerAvailableEndTime;
    private Boolean providerIsEnable;
    private String qrCodeUuid;
    private Object customerIcon;
    private Boolean isHigherPriorityThanGlobal;
    private boolean isLike;

    /**
     *
     * @return
     * The userName
     */
    public String getUserName() {
        return userName;
    }

    /**
     *
     * @param userName
     * The userName
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     *
     * @return
     * The phoneNumber
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     *
     * @param phoneNumber
     * The phoneNumber
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     *
     * @return
     * The location
     */
    public String getLocation() {
        return location;
    }

    /**
     *
     * @param location
     * The location
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     *
     * @return
     * The profile
     */
    public String getProfile() {
        return profile;
    }

    /**
     *
     * @param profile
     * The profile
     */
    public void setProfile(String profile) {
        this.profile = profile;
    }

    /**
     *
     * @return
     * The company
     */
    public String getCompany() {
        return company;
    }

    /**
     *
     * @param company
     * The company
     */
    public void setCompany(String company) {
        this.company = company;
    }

    /**
     *
     * @return
     * The profilePhotoId
     */
    public String getProfilePhotoId() {
        return profilePhotoId;
    }

    /**
     *
     * @param profilePhotoId
     * The profilePhotoId
     */
    public void setProfilePhotoId(String profilePhotoId) {
        this.profilePhotoId = profilePhotoId;
    }

    /**
     *
     * @return
     * The nickName
     */
    public String getNickName() {
        return nickName;
    }

    /**
     *
     * @param nickName
     * The nickName
     */
    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    /**
     *
     * @return
     * The chargeType
     */
    public Integer getChargeType() {
        return chargeType;
    }

    /**
     *
     * @param chargeType
     * The chargeType
     */
    public void setChargeType(Integer chargeType) {
        this.chargeType = chargeType;
    }

    /**
     *
     * @return
     * The availableStartTime
     */
    public String getAvailableStartTime() {
        return availableStartTime;
    }

    /**
     *
     * @param availableStartTime
     * The availableStartTime
     */
    public void setAvailableStartTime(String availableStartTime) {
        this.availableStartTime = availableStartTime;
    }

    /**
     *
     * @return
     * The availableEndTime
     */
    public String getAvailableEndTime() {
        return availableEndTime;
    }

    /**
     *
     * @param availableEndTime
     * The availableEndTime
     */
    public void setAvailableEndTime(String availableEndTime) {
        this.availableEndTime = availableEndTime;
    }

    /**
     *
     * @return
     * The isEnable
     */
    public Boolean getIsEnable() {
        return isEnable;
    }

    /**
     *
     * @param isEnable
     * The isEnable
     */
    public void setIsEnable(Boolean isEnable) {
        this.isEnable = isEnable;
    }

    /**
     *
     * @return
     * The providerAvailableStartTime
     */
    public String getProviderAvailableStartTime() {
        return providerAvailableStartTime;
    }

    /**
     *
     * @param providerAvailableStartTime
     * The providerAvailableStartTime
     */
    public void setProviderAvailableStartTime(String providerAvailableStartTime) {
        this.providerAvailableStartTime = providerAvailableStartTime;
    }

    /**
     *
     * @return
     * The providerAvailableEndTime
     */
    public String getProviderAvailableEndTime() {
        return providerAvailableEndTime;
    }

    /**
     *
     * @param providerAvailableEndTime
     * The providerAvailableEndTime
     */
    public void setProviderAvailableEndTime(String providerAvailableEndTime) {
        this.providerAvailableEndTime = providerAvailableEndTime;
    }

    /**
     *
     * @return
     * The providerIsEnable
     */
    public Boolean getProviderIsEnable() {
        return providerIsEnable;
    }

    /**
     *
     * @param providerIsEnable
     * The providerIsEnable
     */
    public void setProviderIsEnable(Boolean providerIsEnable) {
        this.providerIsEnable = providerIsEnable;
    }

    /**
     *
     * @return
     * The qrCodeUuid
     */
    public String getQrCodeUuid() {
        return qrCodeUuid;
    }

    /**
     *
     * @param qrCodeUuid
     * The qrCodeUuid
     */
    public void setQrCodeUuid(String qrCodeUuid) {
        this.qrCodeUuid = qrCodeUuid;
    }

    /**
     *
     * @return
     * The customerIcon
     */
    public Object getCustomerIcon() {
        return customerIcon;
    }

    /**
     *
     * @param customerIcon
     * The customerIcon
     */
    public void setCustomerIcon(Object customerIcon) {
        this.customerIcon = customerIcon;
    }

    public Boolean getIsHigherPriorityThanGlobal() {
        return isHigherPriorityThanGlobal;
    }

    public void setIsHigherPriorityThanGlobal(Boolean isHigherPriorityThanGlobal) {
        this.isHigherPriorityThanGlobal = isHigherPriorityThanGlobal;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Boolean getLike() {
        return isLike;
    }

    public void setLike(Boolean like) {
        this.isLike = like;
    }
}