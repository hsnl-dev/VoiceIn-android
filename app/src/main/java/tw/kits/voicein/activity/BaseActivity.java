package tw.kits.voicein.activity;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

/**
 * Created by Henry on 2016/7/4.
 */
public class BaseActivity extends AppCompatActivity{
    private ProgressBar mLoadBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLoadBar = new ProgressBar(this, null, android.R.attr.progressBarStyle);
    }

    /***
     * set the layout that where the progressbar located
     */
    public void setMainProgressBar(ViewGroup layout){


        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        params.setMargins(8,8,8,8);
        mLoadBar.setLayoutParams(params);
        layout.addView(mLoadBar,0);
    }
    public void showProgressBar(ViewGroup hideTarget){
        hideTarget.setVisibility(View.INVISIBLE);
        mLoadBar.setVisibility(View.VISIBLE);

    }
    public void hideProgressBar(ViewGroup showTarget){
        showTarget.setVisibility(View.VISIBLE);
        mLoadBar.setVisibility(View.GONE);

    }

}
