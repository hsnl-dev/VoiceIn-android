package tw.kits.voicein.model;



import java.util.ArrayList;
import java.util.List;

/**
 * Created by Henry on 2016/3/2.
 */
public class Contact {
    private String name;
    private String company;
    private static final int LIST_NUM = 10;
    public static List<Contact> genFakeLists(){
        List<Contact> contactList = new ArrayList<Contact>();
        for(int i = 0; i<LIST_NUM; i++){


            Contact c = new Contact();
            c.setCompany("Waa");
            c.setName(i + "");
            contactList.add(c);

        }

        return contactList;

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }
}
