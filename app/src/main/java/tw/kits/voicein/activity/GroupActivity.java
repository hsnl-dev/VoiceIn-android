package tw.kits.voicein.activity;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
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
import tw.kits.voicein.fragment.ProgressCallFragment;
import tw.kits.voicein.fragment.ProgressFragment;
import tw.kits.voicein.model.CallForm;
import tw.kits.voicein.model.Contact;
import tw.kits.voicein.model.ContactList;
import tw.kits.voicein.model.Group;
import tw.kits.voicein.util.DividerItemDecoration;
import tw.kits.voicein.util.InitCallCallBackImpl;
import tw.kits.voicein.util.SnackBarUtil;
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
    ProgressCallFragment mProgressDialogCall;
    SnackBarUtil helper;
    Group mGroup;
    ContactAdapter.AdapterListener mlistListener;
    SwipeRefreshLayout mRefreshContainer;
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
        mProgressDialogCall = new ProgressCallFragment();
        helper = new SnackBarUtil(mMainView, this);
        helper.setErrorMessage(403, getString(R.string.forbidden_call_hint));
        getSupportActionBar().setTitle(mGName);
        RecyclerView.ItemDecoration itemDecoration = new
                DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST);
        mListView.addItemDecoration(itemDecoration);
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
                mProgressDialogCall.show(getSupportFragmentManager(), "wait");
                mApiService.createCall(mUserUuid, form)
                        .enqueue(new InitCallCallBackImpl(mProgressDialogCall,GroupActivity.this,mMainView));
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

        mRefreshContainer = (SwipeRefreshLayout) findViewById(R.id.group_container);
        mRefreshContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();

            }
        });
        mRefreshContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);


        refresh();

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
            helper.showSnackBar(SnackBarUtil.NETWORK_ERR);
        }
    }
    public void refresh(){

        final SnackBarUtil helper = new SnackBarUtil(mMainView,this);
        mApiService.getGroupContactList(mUserUuid,mGid).enqueue(new Callback<List<Contact>>() {
            @Override
            public void onResponse(Call<List<Contact>> call, Response<List<Contact>> response) {
                mRefreshContainer.setRefreshing(false);
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
                mRefreshContainer.setRefreshing(false);
                helper.showSnackBar(SnackBarUtil.NETWORK_ERR);
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode==RESULT_OK){
            refresh();
            if(requestCode==INTENT_EDIT_GROUP){
                getSupportActionBar().setTitle(data.getStringExtra(GroupAddActivity.RETURN_NAME));
            }
            mRefreshContainer.setRefreshing(true);
            helper.showSnackBar(200);
        }
    }
}
