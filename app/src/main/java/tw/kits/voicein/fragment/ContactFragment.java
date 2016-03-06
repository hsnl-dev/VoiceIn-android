package tw.kits.voicein.fragment;


import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tw.kits.voicein.R;
import tw.kits.voicein.adapter.ContactAdapter;
import tw.kits.voicein.model.Contact;
import tw.kits.voicein.model.ContactList;
import tw.kits.voicein.util.DividerItemDecoration;
import tw.kits.voicein.util.ServiceManager;
import tw.kits.voicein.util.UserAccessStore;


/**
 * A simple {@link Fragment} subclass.
 */
public class ContactFragment extends Fragment {
    String TAG = getClass().getName();
    String userUuid;
    String token;
    Context context;
    RecyclerView rvContact;
    public ContactFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact, container, false);
        context = getContext();
        rvContact = (RecyclerView) view.findViewById(R.id.contact_rv_items);

        RecyclerView.ItemDecoration itemDecoration = new
                DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST);
        rvContact.addItemDecoration(itemDecoration);
        rvContact.setLayoutManager(new LinearLayoutManager(context));
        userUuid = UserAccessStore.getUserUuid();
        token = UserAccessStore.getToken();
        ServiceManager.createService(token)
                .getContacts(userUuid)
                .enqueue(new Callback<List<Contact>>() {
                    @Override
                    public void onResponse(Call<List<Contact>> call, Response<List<Contact>> response) {
                        ContactAdapter contactAdapter = new ContactAdapter(response.body());
                        rvContact.setAdapter(contactAdapter);

                    }

                    @Override
                    public void onFailure(Call<List<Contact>> call, Throwable t) {
                        Log.e(TAG,t.toString());
                    }
                });
        return view;
    }

    private void showSnackBar(String msg, View parentView) {
        Snackbar snack = Snackbar.make(parentView, msg, Snackbar.LENGTH_LONG);
        View view = snack.getView();
        TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextColor(Color.WHITE);
        snack.show();
    }
}
