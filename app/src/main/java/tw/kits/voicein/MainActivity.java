package tw.kits.voicein;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ViewPager viewPager = (ViewPager)findViewById(R.id.pager_main);
        MainAdapter mainAdapter = new MainAdapter(getSupportFragmentManager(), this);
        viewPager.setAdapter(mainAdapter);
        TabLayout tabLayout = (TabLayout)findViewById(R.id.tab_main);
        tabLayout.setupWithViewPager(viewPager);
        for(int i=0 ; i<tabLayout.getTabCount(); i++){
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            tab.setCustomView(mainAdapter.getTabView(tabLayout,i));
        }

    }
}
