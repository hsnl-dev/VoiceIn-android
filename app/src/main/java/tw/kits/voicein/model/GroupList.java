package tw.kits.voicein.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Henry on 2016/4/12.
 */
public class GroupList implements Serializable {
    private List<Group> groups;

    public List<Group> getGroups() {
        return groups;
    }

    public void setGroups(List<Group> groups) {
        this.groups = groups;
    }
}
