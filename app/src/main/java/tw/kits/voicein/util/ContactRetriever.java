package tw.kits.voicein.util;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tw.kits.voicein.model.Contact;

/**
 * Created by Henry on 2016/5/17.
 */
public class ContactRetriever {
    List<Contact> contactList;
    VoiceInService service;

    public ContactRetriever(VoiceInService uService) {
        this.contactList = new ArrayList<>();
        this.service = uService;
    }
    public interface Callback{
        void onSuccess(List<Contact> contacts);
        void onError(Response<List<Contact>> response);
        void onFailure(Call<List<Contact>> call, Throwable t);
    }
    public void getContactList(String uuid,final Callback callback) {
        if(contactList.isEmpty()){
            service.getContacts(uuid).enqueue(new retrofit2.Callback<List<Contact>>() {
                @Override
                public void onResponse(Call<List<Contact>> call, Response<List<Contact>> response) {
                    if(response.isSuccess()){
                        contactList.addAll(response.body());
                        callback.onSuccess(contactList);
                    }else
                        callback.onError(response);
                }

                @Override
                public void onFailure(Call<List<Contact>> call, Throwable t) {
                    callback.onFailure(call, t);
                }
            });
        }else{
            service.getContactsConditional(uuid).enqueue(new retrofit2.Callback<List<Contact>>() {
                @Override
                public void onResponse(Call<List<Contact>> call, Response<List<Contact>> response) {
                    if(response.isSuccess()){
                        for(Contact contact: response.body()){
                            if(contact.getUserName()==null){
                                for(Contact target:contactList){
                                    if(contact.getId().equals(target.getId())){
                                        Log.e("123", "onResponse: error");
                                        contactList.remove(target);
                                        break;
                                    }

                                }
                            }else{
                                contactList.add(contact);
                            }
                        }
                        callback.onSuccess(contactList);
                    }else
                        callback.onError(response);
                }

                @Override
                public void onFailure(Call<List<Contact>> call, Throwable t) {
                    callback.onFailure(call, t);
                }
            });

        }

    }
}
