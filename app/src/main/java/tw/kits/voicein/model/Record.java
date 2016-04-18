
package tw.kits.voicein.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class Record {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("startTime")
    @Expose
    private long startTime;
    @SerializedName("endTime")
    @Expose
    private long endTime;
    @SerializedName("answer")
    @Expose
    private boolean answer;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("durationMills")
    @Expose
    private long durationMills;
    @SerializedName("anotherUserId")
    @Expose
    private String anotherUserId;
    @SerializedName("anotherIsIcon")
    @Expose
    private boolean anotherIsIcon;
    @SerializedName("anotherIconId")
    @Expose
    private String anotherIconId;
    @SerializedName("anotherNickName")
    @Expose
    private String anotherNickName;
    @SerializedName("anotherName")
    @Expose
    private String anotherName;
    @SerializedName("anotherNum")
    @Expose
    private String anotherNum;
    @SerializedName("anotherAvatarId")
    @Expose
    private String anotherAvatarId;
    @SerializedName("contactId")
    @Expose
    private String contactId;
    private Long reqTime;

    /**
     * @return The id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id The id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return The startTime
     */
    public long getStartTime() {
        return startTime;
    }

    /**
     * @param startTime The startTime
     */
    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    /**
     * @return The endTime
     */
    public long getEndTime() {
        return endTime;
    }

    /**
     * @param endTime The endTime
     */
    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    /**
     * @return The answer
     */
    public boolean isAnswer() {
        return answer;
    }

    /**
     * @param answer The answer
     */
    public void setAnswer(boolean answer) {
        this.answer = answer;
    }

    /**
     * @return The type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type The type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return The durationMills
     */
    public long getDurationMills() {
        return durationMills;
    }

    /**
     * @param durationMills The durationMills
     */
    public void setDurationMills(long durationMills) {
        this.durationMills = durationMills;
    }

    /**
     * @return The anotherUserId
     */
    public String getAnotherUserId() {
        return anotherUserId;
    }

    /**
     * @param anotherUserId The anotherUserId
     */
    public void setAnotherUserId(String anotherUserId) {
        this.anotherUserId = anotherUserId;
    }

    /**
     * @return The anotherIsIcon
     */
    public boolean isAnotherIsIcon() {
        return anotherIsIcon;
    }

    /**
     * @param anotherIsIcon The anotherIsIcon
     */
    public void setAnotherIsIcon(boolean anotherIsIcon) {
        this.anotherIsIcon = anotherIsIcon;
    }

    /**
     * @return The anotherIconId
     */
    public String getAnotherIconId() {
        return anotherIconId;
    }

    /**
     * @param anotherIconId The anotherIconId
     */
    public void setAnotherIconId(String anotherIconId) {
        this.anotherIconId = anotherIconId;
    }

    /**
     * @return The anotherNickName
     */
    public String getAnotherNickName() {
        return anotherNickName;
    }

    /**
     * @param anotherNickName The anotherNickName
     */
    public void setAnotherNickName(String anotherNickName) {
        this.anotherNickName = anotherNickName;
    }

    /**
     * @return The anotherName
     */
    public String getAnotherName() {
        return anotherName;
    }

    /**
     * @param anotherName The anotherName
     */
    public void setAnotherName(String anotherName) {
        this.anotherName = anotherName;
    }

    /**
     * @return The anotherNum
     */
    public String getAnotherNum() {
        return anotherNum;
    }

    /**
     * @param anotherNum The anotherNum
     */
    public void setAnotherNum(String anotherNum) {
        this.anotherNum = anotherNum;
    }

    /**
     * @return The anotherAvatarId
     */
    public String getAnotherAvatarId() {
        return anotherAvatarId;
    }

    /**
     * @param anotherAvatarId The anotherAvatarId
     */
    public void setAnotherAvatarId(String anotherAvatarId) {
        this.anotherAvatarId = anotherAvatarId;
    }

    /**
     * @return The contactId
     */
    public String getContactId() {
        return contactId;
    }

    /**
     * @param contactId The contactId
     */
    public void setContactId(String contactId) {
        this.contactId = contactId;
    }

    public Long getReqTime() {
        return reqTime;
    }

    public void setReqTime(Long reqTime) {
        this.reqTime = reqTime;
    }
}