package tw.kits.voicein.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import tw.kits.voicein.GcmMessageHandler;
import tw.kits.voicein.R;
import tw.kits.voicein.adapter.MainAdapter;

public class MainActivity extends AppCompatActivity {
    private static int INTENT_PROFILE_EDIT = 0x01;
    private static String TAG = "MainActivity";
    private BroadcastReceiver broadcastReceiver;
    private Menu mMenu;
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
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(mMenu!=null) {
                    Log.e(TAG, "onReceive: ");
                    View v = mMenu.findItem(R.id.main_menu_inbox).getActionView();
                    TextView t = (TextView) v.findViewById(R.id.notify_tv_count);
                    t.setVisibility(View.VISIBLE);
                    String countStr = t.getText().toString();
                    if (!"9+".equals(countStr)) {
                        int count = Integer.parseInt(countStr);
                        if (count == 9) {
                            t.setText("9+");
                        } else {
                            t.setText(Integer.toString(count + 1));
                        }
                    }
                }
            }
        };

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_main);

        tabLayout.setupWithViewPager(viewPager);
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            tab.setCustomView(mainAdapter.getTabView(tabLayout, i));
        }

        viewPager.setCurrentItem(0);
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver,new IntentFilter(GcmMessageHandler.NEW_CONTACT_NOTIFY));

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        mMenu = menu;
        View v = mMenu.findItem(R.id.main_menu_inbox).getActionView();
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "onOptionsItemSelected: " );
                Intent event = new Intent(MainActivity.this, EventActivity.class);
                startActivity(event);
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.e(TAG, "onOptionsItemSelected: " );
        switch (item.getItemId()) {
            case R.id.main_menu_inbox:
                Log.e(TAG, "onOptionsItemSelected: " );
                Intent event = new Intent(this, EventActivity.class);
                startActivity(event);
                break;
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

