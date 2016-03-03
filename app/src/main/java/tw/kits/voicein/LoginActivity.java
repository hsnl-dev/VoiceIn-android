package tw.kits.voicein;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Button phoneNum = (Button)findViewById(R.id.login_btn_confirm);
        phoneNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog dialog = ProgressDialog.show(LoginActivity.this,
                        getBaseContext().getString(R.string.wait), getBaseContext().getString(R.string.wait_notice), true);
                new Thread(new Runnable(){
                    @Override
                    public void run() {
                        try{
                            Thread.sleep(3000);
                            Intent intent = new Intent(LoginActivity.this, VerifyActivity.class);
                            startActivity(intent);
                        }
                        catch(Exception e){
                            e.printStackTrace();
                        }
                        finally{
                            dialog.dismiss();
                        }
                    }
                }).start();
            }
        });
    }
}
