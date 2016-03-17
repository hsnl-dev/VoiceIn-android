package tw.kits.voicein.activity;

import android.app.Service;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tw.kits.voicein.R;
import tw.kits.voicein.adapter.QrcodeAdapter;
import tw.kits.voicein.model.QRcode;
import tw.kits.voicein.util.ServiceManager;
import tw.kits.voicein.util.UserAccessStore;

public class QRCodeActivity extends AppCompatActivity {
    RecyclerView mList;
    View mMainView;
    QrcodeAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);
        mList = (RecyclerView)findViewById(R.id.qrcode_rv_main);
        mMainView = findViewById(R.id.qrcode_lo_main);
        mList.setLayoutManager(new LinearLayoutManager(this));
        ServiceManager.createService(UserAccessStore.getToken())
                .getCustomQrcodes(UserAccessStore.getUserUuid())
                .enqueue(new ApiCallBack());

    }
    private class ApiCallBack implements Callback<List<QRcode>>{

        @Override
        public void onResponse(Call<List<QRcode>> call, Response<List<QRcode>> response) {
            if(response.isSuccess()){
                mAdapter = new QrcodeAdapter(response.body(),QRCodeActivity.this,mMainView);
                mList.setAdapter(mAdapter);

            }
        }

        @Override
        public void onFailure(Call<List<QRcode>> call, Throwable t) {

        }
    }

}
