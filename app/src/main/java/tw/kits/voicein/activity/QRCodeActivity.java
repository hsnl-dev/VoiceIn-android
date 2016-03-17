package tw.kits.voicein.activity;

import android.app.Service;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tw.kits.voicein.R;
import tw.kits.voicein.adapter.QrcodeAdapter;
import tw.kits.voicein.model.QRcode;
import tw.kits.voicein.model.QRcodeContainer;
import tw.kits.voicein.util.ServiceManager;
import tw.kits.voicein.util.UserAccessStore;
import tw.kits.voicein.util.VoiceInService;

public class QRCodeActivity extends AppCompatActivity implements View.OnClickListener {
    RecyclerView mList;
    View mMainView;
    QrcodeAdapter mAdapter;
    VoiceInService mService;
    SwipeRefreshLayout mRefresh;
    FloatingActionButton mFab;
    private static String TAG = QRCodeActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);
        mList = (RecyclerView)findViewById(R.id.qrcode_rv_main);
        mMainView = findViewById(R.id.qrcode_lo_main);
        mList.setLayoutManager(new LinearLayoutManager(this));
        mService = ServiceManager.createService(UserAccessStore.getToken());
        refresh();
        mRefresh = (SwipeRefreshLayout)findViewById(R.id.qrcode_rv_container);
        mRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });
        mFab = (FloatingActionButton)findViewById(R.id.qrcode_fab_plus);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mFab.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.qrcode_fab_plus:
                Intent add = new Intent(QRCodeActivity.this, QrcodeCreateActivity.class);
                startActivity(add);
        }
    }

    private void refresh() {

        mService.getCustomQrcodes(UserAccessStore.getUserUuid())
                .enqueue(new ApiCallBack());


    }
    private class ApiCallBack implements Callback<QRcodeContainer>{

        @Override
        public void onResponse(Call<QRcodeContainer> call, Response<QRcodeContainer> response) {
            mRefresh.setRefreshing(false);
            if(response.isSuccess()){
                mAdapter = new QrcodeAdapter(response.body().getQrcodes(),QRCodeActivity.this,mMainView);

                mList.setAdapter(mAdapter);
                Log.e(TAG, response.body().getQrcodes().size() + "" +mAdapter.getItemCount());
                mAdapter.notifyDataSetChanged();

            }
        }

        @Override
        public void onFailure(Call<QRcodeContainer> call, Throwable t) {
            mRefresh.setRefreshing(false);
            Log.e(TAG,t.toString());
        }
    }

}
