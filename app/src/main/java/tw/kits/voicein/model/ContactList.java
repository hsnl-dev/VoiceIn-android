package tw.kits.voicein.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Henry on 2016/3/6.
 */
public class ContactList implements Serializable {
    private List<Contact> contactList;
    public List<Contact> getContactList(){
        return contactList;
    }

    public void setContactList(List<Contact> contactList) {
        this.contactList = contactList;
    }
}
