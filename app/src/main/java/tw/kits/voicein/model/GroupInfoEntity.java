package tw.kits.voicein.model;

import java.util.List;

/**
 * Created by Henry on 2016/4/12.
 */
public class GroupInfoEntity {
    private List<String> contacts;
    private String groupName;

    public List<String> getContacts() {
        return contacts;
    }

    public void setContacts(List<String> contacts) {
        this.contacts = contacts;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
}
