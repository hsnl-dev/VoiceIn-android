package tw.kits.voicein.activity;

import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.widget.LinearLayout;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tw.kits.voicein.G8penApplication;
import tw.kits.voicein.R;
import tw.kits.voicein.adapter.SearchResultAdapter;
import tw.kits.voicein.fragment.ProgressFragment;
import tw.kits.voicein.model.CallForm;
import tw.kits.voicein.model.Contact;
import tw.kits.voicein.util.ColoredSnackBarUtil;
import tw.kits.voicein.util.VoiceInService;

public class SearchActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    private static final String TAG = SearchActivity.class.getSimpleName();
    SearchView mSearchView;
    List<Contact> mContactList;
    RecyclerView mListView;
    LinearLayout mLayout;
    SearchResultAdapter mSearchAdapter;
    VoiceInService mApiService;
    String mToken;
    String mUserUuid;
    ProgressFragment mProgressDialog;
    SearchResultAdapter.AdapterListener mListListener;
    public final static int INTENT_EDIT_CONTACT = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        mApiService = ((G8penApplication)getApplication()).getAPIService();
        mToken = ((G8penApplication)getApplication()).getToken();
        mUserUuid = ((G8penApplication)getApplication()).getUserUuid();
        mProgressDialog = new ProgressFragment();
        mApiService.getContacts(mUserUuid)
                .enqueue(new Callback<List<Contact>>() {
                    @Override
                    public void onResponse(Call<List<Contact>> call, Response<List<Contact>> response) {
                        if (response.isSuccess()) {
                            mContactList = response.body();
                            mSearchAdapter = new SearchResultAdapter(mContactList, SearchActivity.this, mLayout);
                            mListView.setAdapter(mSearchAdapter);
                            mSearchAdapter.setContatctListListener(mListListener);
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Contact>> call, Throwable t) {

                    }
                });


        //view
        mSearchView = (SearchView) findViewById(R.id.search_res_sv);
        mListView = (RecyclerView) findViewById(R.id.search_res_lv);
        mLayout = (LinearLayout) findViewById(R.id.search_res_lo_main);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchableInfo searchableInfo = searchManager.getSearchableInfo(getComponentName());
        mListView.setLayoutManager(new LinearLayoutManager(this));
        mSearchView.setSearchableInfo(searchableInfo);
        mSearchView.setOnQueryTextListener(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mListListener = new SearchResultAdapter.AdapterListener() {
            @Override
            public void onListClick(int pos, Contact item) {
                Intent intent = new Intent(SearchActivity.this, ContactEditActivity.class);
                intent.putExtra(ContactEditActivity.ARG_CONTACT, item);
                startActivityForResult(intent, INTENT_EDIT_CONTACT);
            }

            @Override
            public void onNoPhoneClick(int pos, Contact item) {
                ColoredSnackBarUtil.primary(
                        Snackbar.make(mLayout, getString(R.string.forbidden_call_hint), Snackbar.LENGTH_SHORT)
                ).show();
            }

            @Override
            public void onPhoneClick(int pos, Contact item) {
                CallForm form = new CallForm();
                form.setContactId(item.getId());
                mProgressDialog.show(getSupportFragmentManager(),"wait");
                mApiService.createCall(mUserUuid, form)
                        .enqueue(new CallCallBack());
            }
        };



    }


    @Override
    public boolean onQueryTextSubmit(String query) {

        return false;

    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if(mSearchAdapter!=null){
            mSearchAdapter.getFilter().filter(newText);
        }

        return true;
    }


    private class CallCallBack implements Callback<ResponseBody>{
        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
            mProgressDialog.dismiss();
            if(response.isSuccess()){

            }else{
                switch (response.code()){
                    case 403:
                        ColoredSnackBarUtil.primary(
                                Snackbar.make(mLayout, getString(R.string.forbidden_call_hint), Snackbar.LENGTH_SHORT)
                        ).show();
                        break;
                    case 401:
                        ColoredSnackBarUtil.primary(
                                Snackbar.make(mLayout, getString(R.string.user_not_auth), Snackbar.LENGTH_SHORT)
                        ).show();
                        break;
                    default:
                        ColoredSnackBarUtil.primary(
                                Snackbar.make(mLayout, getString(R.string.server_err), Snackbar.LENGTH_SHORT)
                        ).show();

                }
            }
        }

        @Override
        public void onFailure(Call<ResponseBody> call, Throwable t) {
            mProgressDialog.dismiss();
            ColoredSnackBarUtil.primary(
                    Snackbar.make(mLayout, getString(R.string.network_err), Snackbar.LENGTH_SHORT)
            ).show();
        }
    }
}
