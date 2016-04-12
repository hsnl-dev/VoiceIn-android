package tw.kits.voicein.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;

import tw.kits.voicein.R;

/**
 * Created by Henry on 2016/4/12.
 */
public class IntroActivity extends AppIntro {

    // Please DO NOT override onCreate. Use init.
    @Override
    public void init(Bundle savedInstanceState) {

//        // Add your slide's fragments here.
//        // AppIntro will automatically generate the dots indicator and buttons.
//        addSlide(first_fragment);
//        addSlide(second_fragment);
//        addSlide(third_fragment);
//        addSlide(fourth_fragment);

        // Instead of fragments, you can also use our default slide
        // Just set a title, description, background and image. AppIntro will do the rest.
        addSlide(AppIntroFragment.newInstance(getString(R.string.app_intro_080_title), getString(R.string.app_intro_080_body), R.drawable.feature1,Color.parseColor("#009688")));
        addSlide(AppIntroFragment.newInstance(getString(R.string.app_intro_icon_title), getString(R.string.app_intro_icon_body), R.drawable.feature2,Color.parseColor("#00ACC1")));
        addSlide(AppIntroFragment.newInstance(getString(R.string.app_intro_contact_title), getString(R.string.app_intro_contact_body), R.drawable.feature3,Color.parseColor("#7CB342")));
        addSlide(AppIntroFragment.newInstance(getString(R.string.app_intro_time_title), getString(R.string.app_intro_time_body), R.drawable.feature4,Color.parseColor("#FFA000")));

        // OPTIONAL METHODS
        // Override bar/separator color.
//        setBarColor(Color.parseColor("#3F51B5"));
//        setSeparatorColor(Color.parseColor("#2196F3"));

        // Hide Skip/Done button.
        showSkipButton(true);
        setSkipText("略過");
        setDoneText("開始使用");
        setProgressButtonEnabled(true);

        setFlowAnimation();

        // Turn vibration on and set intensity.
        // NOTE: you will probably need to ask VIBRATE permisssion in Manifest.
        setVibrate(true);
        setVibrateIntensity(30);
    }

    @Override
    public void onSkipPressed() {
        Intent i = new Intent(this,LoginActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    public void onDonePressed() {
        Intent i = new Intent(this,LoginActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    public void onSlideChanged() {

    }

    @Override
    public void onNextPressed() {
        // Do something when users tap on Next button.
    }

}
