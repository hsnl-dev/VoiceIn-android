package tw.kits.voicein.model;

import java.util.List;

/**
 * Created by Henry on 2016/4/11.
 */
public class Group {
    private String groupId;
    private String groupName;
    private int groupMemberCount;


    public int getGroupMemberCount() {
        return groupMemberCount;
    }

    public void setGroupMemberCount(int groupMemberCount) {
        this.groupMemberCount = groupMemberCount;
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
