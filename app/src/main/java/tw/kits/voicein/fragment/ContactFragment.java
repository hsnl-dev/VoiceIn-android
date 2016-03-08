package tw.kits.voicein.fragment;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tw.kits.voicein.R;
import tw.kits.voicein.activity.ContactAddActivity;
import tw.kits.voicein.adapter.ContactAdapter;
import tw.kits.voicein.model.Contact;
import tw.kits.voicein.util.ColoredSnackBar;
import tw.kits.voicein.util.DividerItemDecoration;
import tw.kits.voicein.util.ServiceManager;
import tw.kits.voicein.util.UserAccessStore;


/**
 * A simple {@link Fragment} subclass.
 */
public class ContactFragment extends Fragment {
    public static final int INTENT_ADD_CONTACT = 900;
    public static final int INTENT_EDIT_CONTACT= 800;
    String TAG = getClass().getName();
    String userUuid;
    String token;
    Context context;
    RecyclerView rvContact;
    CoordinatorLayout mainLayout;
    FloatingActionButton action;
    SwipeRefreshLayout pullContainer;
    ContactAdapter contactAdapter;

    public ContactFragment() {
        // Required empty public constructor

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.e(TAG, "FUCKIN");
        View view = inflater.inflate(R.layout.fragment_contact, container, false);
        context = getContext();
        rvContact = (RecyclerView) view.findViewById(R.id.contact_rv_items);
        mainLayout = (CoordinatorLayout) view.findViewById(R.id.contact_frag_cl_main);
        RecyclerView.ItemDecoration itemDecoration = new
                DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST);
        rvContact.addItemDecoration(itemDecoration);
        rvContact.setLayoutManager(new LinearLayoutManager(context));
        userUuid = UserAccessStore.getUserUuid();
        token = UserAccessStore.getToken();
        action = (FloatingActionButton) view.findViewById(R.id.contact_fab_plus);

        action.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        IntentIntegrator scanIntegrator = IntentIntegrator.forSupportFragment(ContactFragment.this);
                        scanIntegrator.initiateScan();
                    }
                }


        );
        pullContainer = (SwipeRefreshLayout) view.findViewById(R.id.contact_frag_sp_container);
        pullContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshContact();

            }
        });
        pullContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        contactAdapter = new ContactAdapter(new ArrayList<Contact>(), ContactFragment.this);
        rvContact.setAdapter(contactAdapter);
        rvContact.setLayoutManager(new LinearLayoutManager(this.getContext()));
        refreshContact();
        return view;
    }

    private void refreshContact() {
        ServiceManager.createService(token)
                .getContacts(userUuid)
                .enqueue(new Callback<List<Contact>>() {
                    @Override
                    public void onResponse(Call<List<Contact>> call, Response<List<Contact>> response) {
                        pullContainer.setRefreshing(false);
                        if(response.isSuccess()){

                            contactAdapter.clear();
                            contactAdapter.addAll(response.body());
                            contactAdapter.notifyDataSetChanged();


                        } else {
                            Log.e(TAG, "Fial");
                            Snackbar snack = Snackbar.make(pullContainer, getResources().getString(R.string.user_auth_err), Snackbar.LENGTH_LONG);
                            snack.show();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Contact>> call, Throwable t) {
                        pullContainer.setRefreshing(false);
                        Log.e(TAG, t.toString());
                        Snackbar snack = Snackbar.make(pullContainer, getResources().getString(R.string.network_err), Snackbar.LENGTH_LONG);
                        ColoredSnackBar.primary(snack).show();
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if (scanningResult != null) {
                String scanContent = scanningResult.getContents();
                Log.e(TAG, scanContent.toString());
                Intent i = new Intent(this.getActivity(), ContactAddActivity.class);
                i.putExtra(ContactAddActivity.ARG_QRCODE, scanningResult.getContents());
                startActivityForResult(i, INTENT_ADD_CONTACT);
            } else {
                switch (requestCode){
                    case INTENT_ADD_CONTACT:
                        Log.i(TAG, "Success");
                        ColoredSnackBar.primary(Snackbar.make(mainLayout, getString(R.string.success), Snackbar.LENGTH_LONG)).show();
                        pullContainer.setRefreshing(true);
                        refreshContact();
                        break;
                    case INTENT_EDIT_CONTACT:
                        Log.i(TAG, "Success");
                        ColoredSnackBar.primary(Snackbar.make(mainLayout, getString(R.string.success), Snackbar.LENGTH_LONG)).show();
                        pullContainer.setRefreshing(true);
                        refreshContact();
                        break;
                }


            }

        }
    }
}
