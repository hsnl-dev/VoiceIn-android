package tw.kits.voicein.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import tw.kits.voicein.R;
import tw.kits.voicein.util.ServiceManager;
import tw.kits.voicein.util.UserAccessStore;
import tw.kits.voicein.util.VoiceInService;

public class QrcodeCreateActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText mName;
    private EditText mPhone;
    private EditText mCom;
    private EditText mLoc;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode_create);

        findViewById(R.id.customer_qrcode_confirm).setOnClickListener(this);

        mName = (EditText) findViewById(R.id.qrcode_create_et_name);
        mPhone = (EditText) findViewById(R.id.qrcode_create_et_phone);
        mCom = (EditText) findViewById(R.id.qrcode_create_et_com);
        mLoc = (EditText) findViewById(R.id.qrcode_create_et_loc);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.customer_qrcode_confirm:
                VoiceInService service = ServiceManager.createService(UserAccessStore.getToken());
                service.
                break;
        }
    }

}
