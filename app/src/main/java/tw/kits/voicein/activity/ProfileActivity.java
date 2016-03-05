package tw.kits.voicein.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import tw.kits.voicein.R;

public class ProfileActivity extends AppCompatActivity {
    Button mSelectAvatar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        mSelectAvatar = (Button)findViewById(R.id.profile_btn_upload);
        mSelectAvatar.setOnClickListener(new SelectBtnListener());

    }
    private class SelectBtnListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.putExtra("crop", true);
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            intent.putExtra("outputX", 256);
            intent.putExtra("outputY", 256);
            intent.putExtra("outputFormat", "JPEG");
            intent.putExtra("return-data", true);
            intent.putExtra("noFaceDetection", false);
            startActivityForResult(intent, 1);

        }
    }
}
