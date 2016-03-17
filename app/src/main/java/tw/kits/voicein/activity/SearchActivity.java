package tw.kits.voicein.activity;

import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.widget.LinearLayout;

import java.util.List;

import tw.kits.voicein.R;
import tw.kits.voicein.adapter.SearchResultAdapter;
import tw.kits.voicein.model.Contact;

public class SearchActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    private static final String TAG = SearchActivity.class.getSimpleName();
    SearchView mSearchView;
    List<Contact> mContactList;
    RecyclerView mListView;
    LinearLayout mLayout;
    SearchResultAdapter mContactAdapter;
    public static String ARG_CONTACTS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        mContactList = (List<Contact>) getIntent().getSerializableExtra(ARG_CONTACTS);
        //view
        mSearchView = (SearchView) findViewById(R.id.search_res_sv);
        mListView = (RecyclerView) findViewById(R.id.search_res_lv);
        mLayout = (LinearLayout) findViewById(R.id.search_res_lo_main);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchableInfo searchableInfo = searchManager.getSearchableInfo(getComponentName());
        mSearchView.setSearchableInfo(searchableInfo);
        mSearchView.setOnQueryTextListener(this);
        mContactAdapter = new SearchResultAdapter(mContactList, this, mLayout);
        mListView.setAdapter(mContactAdapter);

    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }
}
