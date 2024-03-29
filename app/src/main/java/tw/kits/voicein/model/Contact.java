package tw.kits.voicein.model;


import java.io.Serializable;

public class Contact implements Serializable, Comparable<Contact>{

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
    private boolean isEnable;
    private String providerAvailableStartTime;
    private String providerAvailableEndTime;
    private boolean providerIsEnable;
    private String qrCodeUuid;
    private Object customerIcon;
    private boolean isHigherPriorityThanGlobal;
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
    public boolean getIsEnable() {
        return isEnable;
    }

    /**
     *
     * @param isEnable
     * The isEnable
     */
    public void setIsEnable(boolean isEnable) {
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
    public boolean getProviderIsEnable() {
        return providerIsEnable;
    }

    /**
     *
     * @param providerIsEnable
     * The providerIsEnable
     */
    public void setProviderIsEnable(boolean providerIsEnable) {
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

    public boolean getIsHigherPriorityThanGlobal() {
        return isHigherPriorityThanGlobal;
    }

    public void setIsHigherPriorityThanGlobal(boolean isHigherPriorityThanGlobal) {
        this.isHigherPriorityThanGlobal = isHigherPriorityThanGlobal;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean getLike() {
        return isLike;
    }

    public void setLike(boolean like) {
        this.isLike = like;
    }

    @Override
    public int compareTo(Contact another) {

        return userName.compareTo(another.getUserName());
    }
    @Override
    public boolean equals(Object o) {
        if(o instanceof Contact){
            Contact another = (Contact) o;
            return another.getId() == this.getId();

        }
        return false;
    }
}