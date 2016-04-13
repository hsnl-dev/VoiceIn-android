package tw.kits.voicein.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Henry on 2016/4/11.
 */
public class Group implements Serializable{
    private String groupId;
    private String groupName;
    private int contactCount;


    public int getContactCount() {
        return contactCount;
    }

    public void setContactCount(int contactCount) {
        this.contactCount = contactCount;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
}
