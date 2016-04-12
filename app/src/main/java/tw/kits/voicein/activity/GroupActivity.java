package tw.kits.voicein.activity;

import android.content.Intent;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.squareup.picasso.Picasso;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tw.kits.voicein.G8penApplication;
import tw.kits.voicein.R;
import tw.kits.voicein.adapter.ContactAdapter;
import tw.kits.voicein.fragment.ProgressFragment;
import tw.kits.voicein.model.CallForm;
import tw.kits.voicein.model.Contact;
import tw.kits.voicein.model.ContactList;
import tw.kits.voicein.model.Group;
import tw.kits.voicein.model.GroupList;
import tw.kits.voicein.util.ColoredSnackBar;
import tw.kits.voicein.util.SnackBarHelper;
import tw.kits.voicein.util.VoiceInService;

public class GroupActivity extends AppCompatActivity {
    public static final String ARG_GROUP = "group";
 public static final String TAG = GroupActivity.class.getName();
    public static final int INTENT_EDIT_CONTACT = 8000;
    public static final int INTENT_EDIT_GROUP = 9000;

    FloatingActionButton editFab;

    RecyclerView mListView;
    View mMainView;
    VoiceInService mApiService;
    Picasso mImgLoader;
    String mUserUuid;
    String mGid;
    String mGName;
    String mToken;
    ContactAdapter mAdapter;
    ProgressFragment mProgressDialog;
    SnackBarHelper helper;
    Group mGroup;
    ContactAdapter.AdapterListener mlistListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        mApiService = ((G8penApplication) getApplication()).getAPIService();
        mToken = ((G8penApplication) getApplication()).getToken();
        mUserUuid = ((G8penApplication) getApplication()).getUserUuid();
        mImgLoader = ((G8penApplication) getApplication()).getImgLoader(this);
        mGroup = (Group)getIntent().getSerializableExtra(ARG_GROUP);
        mGid = mGroup.getGroupId();
        mGName = mGroup.getGroupName();
        editFab = (FloatingActionButton) findViewById(R.id.group_fab_edit);
        mListView = (RecyclerView) findViewById(R.id.group_rv_list);
        mMainView = findViewById(R.id.group_cl_main);
        mListView.setLayoutManager(new LinearLayoutManager(this));
        mProgressDialog = new ProgressFragment();
        helper = new SnackBarHelper(mMainView, this);
        helper.setErrorMessage(403, getString(R.string.forbidden_call_hint));
        getSupportActionBar().setTitle(mGName);
        refresh();
        mlistListener = new ContactAdapter.AdapterListener() {
            @Override
            public void onListClick(int pos, Contact item) {
                Intent intent = new Intent(GroupActivity.this, ContactEditActivity.class);
                intent.putExtra(ContactEditActivity.ARG_CONTACT, item);
                startActivityForResult(intent, INTENT_EDIT_CONTACT);
            }

            @Override
            public void onNoPhoneClick(int pos, Contact item) {
                helper.showSnackBar(403);
            }

            @Override
            public void onPhoneClick(int pos, Contact item) {
                CallForm form = new CallForm();
                form.setContactId(item.getId());
                mProgressDialog.show(getSupportFragmentManager(), "wait");
                mApiService.createCall(mUserUuid, form)
                        .enqueue(new CallCallBack());
            }

            @Override
            public void onFavoriteClick(int pos, Contact item) {
                Log.e(TAG, item.toString());
                mProgressDialog.show(getSupportFragmentManager(), "wait");
                mApiService.updateQRcodeILike(item.getId(), !item.getLike())
                        .enqueue(new FavoriteCallBack(item, mAdapter, pos));
            }

        };
        editFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(GroupActivity.this,GroupAddActivity.class);
                ContactList contactList = new ContactList();
                contactList.setContactList(mAdapter.getContacts());
                i.putExtra(GroupAddActivity.ARG_GROUP, mGroup);
                i.putExtra(GroupAddActivity.ARG_LIST, contactList);
                startActivityForResult(i, INTENT_EDIT_GROUP);
            }
        });

    }
    private class FavoriteCallBack implements Callback<ResponseBody>{
        Contact contact;
        ContactAdapter adapter;
        int pos;
        public FavoriteCallBack(Contact contact, ContactAdapter adapter, int pos){

            this.contact = contact;
            this.adapter = adapter;
            this.pos = pos;
        }
        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
            mProgressDialog.dismiss();
            if(response.isSuccess()){
                contact.setLike(!contact.getLike());
                adapter.notifyItemChanged(pos);
            }else{

                    helper.showSnackBar(response.code());


            }
        }

        @Override
        public void onFailure(Call<ResponseBody> call, Throwable t) {
            mProgressDialog.dismiss();
            helper.showSnackBar(SnackBarHelper.NETWORK_ERR);
        }
    }
    public void refresh(){
        final SnackBarHelper helper = new SnackBarHelper(mMainView,this);
        mApiService.getGroupContactList(mUserUuid,mGid).enqueue(new Callback<List<Contact>>() {
            @Override
            public void onResponse(Call<List<Contact>> call, Response<List<Contact>> response) {
                if(response.isSuccess()){
                    mAdapter = new ContactAdapter(response.body(),GroupActivity.this,mToken,mUserUuid,mImgLoader,mMainView);
                    mListView.setAdapter(mAdapter);
                    mAdapter.setContatctListListener(mlistListener);
                }else{
                    helper.showSnackBar(response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Contact>> call, Throwable t) {
                helper.showSnackBar(SnackBarHelper.NETWORK_ERR);
            }
        });


    }
    private class CallCallBack implements Callback<ResponseBody>{
        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
            mProgressDialog.dismiss();
            if(response.isSuccess()){

            }else{
                helper.showSnackBar(response.code());
            }
        }

        @Override
        public void onFailure(Call<ResponseBody> call, Throwable t) {
            mProgressDialog.dismiss();
            helper.showSnackBar(SnackBarHelper.NETWORK_ERR);
        }
    }
}
