package tw.kits.voicein.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
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
import tw.kits.voicein.model.Contact;
import tw.kits.voicein.model.GroupInfoEntity;
import tw.kits.voicein.util.SnackBarHelper;
import tw.kits.voicein.util.VoiceInService;

public class GroupAddActivity extends AppCompatActivity {
    EditText mNameText;
    View mMainView;
    RecyclerView mMemberListView;
    VoiceInService mAPIService;
    String mUserUuid;
    MemberAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_add);
        mNameText = (EditText) findViewById(R.id.group_add_et_group_name);
        mAPIService = ((G8penApplication)getApplication()).getAPIService();
        mUserUuid = ((G8penApplication)getApplication()).getUserUuid();
        mMainView = findViewById(R.id.group_add_ll_main);
        mMemberListView = (RecyclerView) findViewById(R.id.group_add_rl_contact_list);
        mMemberListView.setLayoutManager(new LinearLayoutManager(this));
        final SnackBarHelper snackBarHelper = new SnackBarHelper(mMainView,this);
        mAPIService.getContacts(mUserUuid).enqueue(new Callback<List<Contact>>() {
            @Override
            public void onResponse(Call<List<Contact>> call, Response<List<Contact>> response) {
                if(response.isSuccess()){

                    mAdapter = new MemberAdapter(response.body(),GroupAddActivity.this);
                    mMemberListView.setAdapter(mAdapter);
                }else{
                    snackBarHelper.showSnackBar(response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Contact>> call, Throwable t) {
                snackBarHelper.showSnackBar(SnackBarHelper.NETWORK_ERR);
            }
        });

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
        GroupInfoEntity form = new GroupInfoEntity();
        if(TextUtils.isEmpty(mNameText.getText())){
            mNameText.setError(getString(R.string.ilegal_input));
            return;
        }
        form.setGroupName(mNameText.getText().toString());
        form.setContacts(mAdapter.getSelectedContactId());


        final SnackBarHelper snackBarHelper = new SnackBarHelper(mMainView,this);
        mAPIService.createGroup(mUserUuid, form).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.isSuccess()){

                    Intent intent = new Intent();
                    intent.putExtra("provider", "");
                    setResult(RESULT_OK, intent);
                    Log.i("TAG", "Contact set OK");
                    GroupAddActivity.this.setResult(Activity.RESULT_OK);
                    finish();
                }else{
                    snackBarHelper.showSnackBar(response.code());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                snackBarHelper.showSnackBar(SnackBarHelper.NETWORK_ERR);

            }
        });

    }

}
