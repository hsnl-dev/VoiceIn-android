package tw.kits.voicein.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import tw.kits.voicein.R;
import tw.kits.voicein.adapter.MainAdapter;
import tw.kits.voicein.util.UserAccessStore;

public class MainActivity extends AppCompatActivity {
    private String userUuid;
    private String token;
    private static int INTENT_PROFILE_EDIT = 0x01;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ViewPager viewPager = (ViewPager) findViewById(R.id.pager_main);
        MainAdapter mainAdapter = new MainAdapter(getSupportFragmentManager(), this);
        viewPager.setAdapter(mainAdapter);
        viewPager.setOffscreenPageLimit(3);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_main);
        tabLayout.setupWithViewPager(viewPager);
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            tab.setCustomView(mainAdapter.getTabView(tabLayout, i));
        }
        userUuid = UserAccessStore.getUserUuid();
        token = UserAccessStore.getToken();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.main_menu_card:
                Intent i = new Intent(this,MyCardActivity.class);
                startActivity(i);
                break;
            case R.id.main_menu_setting:
                Intent edit =  new Intent(this, ProfileEditActivity.class);
                startActivityForResult(edit,INTENT_PROFILE_EDIT);
                break;
            case R.id.main_menu_search:
                Intent search = new Intent(this, SearchActivity.class);
                startActivity(search);
                break;

        }
        return true;
    }
}

