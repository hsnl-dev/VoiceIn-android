package tw.kits.voicein.activity;

import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.widget.LinearLayout;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tw.kits.voicein.R;
import tw.kits.voicein.adapter.SearchResultAdapter;
import tw.kits.voicein.model.Contact;
import tw.kits.voicein.util.ServiceManager;
import tw.kits.voicein.util.UserAccessStore;
import tw.kits.voicein.util.VoiceInService;

public class SearchActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    private static final String TAG = SearchActivity.class.getSimpleName();
    SearchView mSearchView;
    List<Contact> mContactList;
    RecyclerView mListView;
    LinearLayout mLayout;
    SearchResultAdapter mSearchAdapter;
    public static String ARG_CONTACTS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        VoiceInService service = ServiceManager.createService(UserAccessStore.getToken());
        service.getContacts(UserAccessStore.getUserUuid())
                .enqueue(new Callback<List<Contact>>() {
                    @Override
                    public void onResponse(Call<List<Contact>> call, Response<List<Contact>> response) {
                        if (response.isSuccess()) {
                            mContactList = response.body();
                            mSearchAdapter = new SearchResultAdapter(mContactList, SearchActivity.this, mLayout);
                            mListView.setAdapter(mSearchAdapter);
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
}
