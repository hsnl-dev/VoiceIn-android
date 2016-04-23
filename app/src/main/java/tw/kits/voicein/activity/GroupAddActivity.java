package tw.kits.voicein.activity;

import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tw.kits.voicein.G8penApplication;
import tw.kits.voicein.R;
import tw.kits.voicein.adapter.MemberAdapter;
import tw.kits.voicein.fragment.ProgressFragment;
import tw.kits.voicein.model.Contact;
import tw.kits.voicein.model.ContactList;
import tw.kits.voicein.model.Group;
import tw.kits.voicein.model.GroupChangeEntity;
import tw.kits.voicein.model.GroupInfoEntity;
import tw.kits.voicein.util.SnackBarUtil;
import tw.kits.voicein.util.VoiceInService;

public class GroupAddActivity extends AppCompatActivity {
    EditText mNameText;
    public static final String ARG_GROUP = "group";
    public static final String ARG_LIST = "list";
    public static final String RETURN_NAME = "gname";
    ContactList mContacts;
    Group mGroup;
    View mMainView;
    RecyclerView mMemberListView;
    VoiceInService mAPIService;
    String mUserUuid;
    MemberAdapter mAdapter;
    SnackBarUtil mSnackBarHelper;
    ProgressFragment mProgressFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_add);
        mProgressFragment = new ProgressFragment();
        mProgressFragment.show(getSupportFragmentManager(),"wait");
        mGroup = (Group)getIntent().getSerializableExtra(ARG_GROUP);
        mContacts = (ContactList) getIntent().getSerializableExtra(ARG_LIST);
        mNameText = (EditText) findViewById(R.id.group_add_et_group_name);
        mAPIService = ((G8penApplication)getApplication()).getAPIService();
        mUserUuid = ((G8penApplication)getApplication()).getUserUuid();
        mMainView = findViewById(R.id.group_add_ll_main);
        mMemberListView = (RecyclerView) findViewById(R.id.group_add_rl_contact_list);
        mMemberListView.setLayoutManager(new LinearLayoutManager(this));
        mSnackBarHelper = new SnackBarUtil(mMainView,this);


        mAPIService.getContacts(mUserUuid).enqueue(new Callback<List<Contact>>() {
            @Override
            public void onResponse(Call<List<Contact>> call, Response<List<Contact>> response) {
                mProgressFragment.dismiss();
                if(response.isSuccess()){
                    List<Contact> res = response.body();

                    mAdapter = new MemberAdapter(res,GroupAddActivity.this);
                    if(mGroup!=null){
                        mAdapter.setInitState(mContacts.getContactList());
                    }

                    mMemberListView.setAdapter(mAdapter);
                }else{
                    mSnackBarHelper.showSnackBar(response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Contact>> call, Throwable t) {
                mProgressFragment.dismiss();
                mSnackBarHelper.showSnackBar(SnackBarUtil.NETWORK_ERR);
            }
        });
        if(mGroup!=null){
            mNameText.setText(mGroup.getGroupName());
            getSupportActionBar().setTitle(R.string.edit_group_bar);
        }else{
            getSupportActionBar().setTitle(R.string.add_group_bar);
        }

        getSupportActionBar().setHomeButtonEnabled(true);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.contact_edit_menu, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.contact_edit_menu_confirm:
                uploadGroup();
                break;
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(GroupAddActivity.this);
                break;

        }
        return true;
    }

    public void uploadGroup(){

        if(TextUtils.isEmpty(mNameText.getText())){
            mNameText.setError(getString(R.string.ilegal_input));
            return;
        }


        Callback<ResponseBody> callback = new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccess()) {

                    Intent intent = new Intent();
                    intent.putExtra(RETURN_NAME, mNameText.getText().toString());
                    setResult(RESULT_OK,intent);
                    finish();
                } else {
                    mSnackBarHelper.showSnackBar(response.code());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                mSnackBarHelper.showSnackBar(SnackBarUtil.NETWORK_ERR);

            }
        };


        if(mGroup!=null){
            GroupChangeEntity form = new GroupChangeEntity();
            form.setContacts(mAdapter.getSelectedContactId());
            mAPIService.changeGroup(mUserUuid, mGroup.getGroupId(),form,mNameText.getText().toString()).enqueue(callback);
        }else {
            GroupInfoEntity form = new GroupInfoEntity();
            form.setGroupName(mNameText.getText().toString());
            form.setContacts(mAdapter.getSelectedContactId());
            mAPIService.createGroup(mUserUuid, form).enqueue(callback);
        }
    }

}
