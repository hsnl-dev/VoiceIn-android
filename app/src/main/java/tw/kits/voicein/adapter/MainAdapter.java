package tw.kits.voicein.adapter;

import android.content.Context;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import tw.kits.voicein.fragment.ContactFragment;
import tw.kits.voicein.R;
import tw.kits.voicein.fragment.UnderDevelopFragment;

/**
 * Created by Henry on 2016/3/1.
 */
public class MainAdapter extends FragmentPagerAdapter {
    final static Integer titles[] = {
            R.string.contact_text,
            R.string.favorite_text,
            R.string.history_text,
            R.string.group_text};
    final static Integer imageR[] = {
            R.drawable.ic_face_white_24dp,
            R.drawable.ic_favorite_white_24dp,
            R.drawable.ic_history_white_24dp,
            R.drawable.ic_people_white_24dp
        };

    final Context context;
    private final List<Fragment> fragments;

    public MainAdapter(FragmentManager manager, Context context){
        super(manager);
        fragments = new ArrayList<>();
        fragments.add(new ContactFragment());
        fragments.add(new UnderDevelopFragment());
        fragments.add(new UnderDevelopFragment());
        fragments.add(new UnderDevelopFragment());
        this.context = context;
    }
    @Override
    public Fragment getItem(int position) {

        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return titles.length;
    }
    @Override
    public CharSequence getPageTitle(int position){
        return context.getString(titles[position]);
    }
    public View getTabView(TabLayout tablayout, int position){
        View v = LayoutInflater.from(context).inflate(R.layout.tab_main, tablayout, false);
        ImageView uImageView = (ImageView) v.findViewById(R.id.img_tab);
        TextView uTextView = (TextView) v.findViewById(R.id.text_tab);
        uTextView.setText(titles[position]);
        uImageView.setImageResource(imageR[position]);
        return v;
    }
}
