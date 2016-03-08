package tw.kits.voicein.model;

import com.google.gson.annotations.SerializedName;

public class ContactAddEntity {

    public transient static final int ICON = 0;
    public transient static final int FREE = 1;
    public transient static final int CHARGE = 2;
    @SerializedName("nickName")
    private String nickName;
    @SerializedName("chargeType")
    private Integer chargeType;
    @SerializedName("availableStartTime")
    private String availableStartTime;
    @SerializedName("availableEndTime")
    private String availableEndTime;
    @SerializedName("isEnable")
    private Boolean isEnable;

    /**
     * @return The nickName
     */
    public String getNickName() {
        return nickName;
    }

    /**
     * @param nickName The nickName
     */
    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    /**
     * @return The chargeType
     */
    public Integer getChargeType() {
        return chargeType;
    }

    /**
     * @param chargeType The chargeType
     */
    public void setChargeType(Integer chargeType) {
        this.chargeType = chargeType;
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

    /**
     * @return The isEnable
     */
    public Boolean getIsEnable() {
        return isEnable;
    }

    /**
     * @param isEnable The isEnable
     */
    public void setIsEnable(Boolean isEnable) {
        this.isEnable = isEnable;
    }

}