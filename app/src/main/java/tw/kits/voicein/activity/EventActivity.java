package tw.kits.voicein.activity;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tw.kits.voicein.G8penApplication;
import tw.kits.voicein.R;
import tw.kits.voicein.adapter.EventAdapter;
import tw.kits.voicein.model.EventEntity;
import tw.kits.voicein.model.EventEntityList;
import tw.kits.voicein.util.DividerItemDecoration;
import tw.kits.voicein.util.SnackBarUtil;
import tw.kits.voicein.util.VoiceInService;

public class EventActivity extends BaseActivity {
    RecyclerView mMainList;
    VoiceInService mApiService;
    String mToken;
    String mUserUuid;
    EventAdapter mEventAdapter;
    Picasso mPicasso;
    ViewGroup mMainLayout;
    SnackBarUtil mSnackBarUtil;
    SwipeRefreshLayout mRefresh;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        mMainList  = (RecyclerView) findViewById(R.id.event_rv_main);
        mApiService = ((G8penApplication) getApplication()).getAPIService();
        mToken = ((G8penApplication) getApplication()).getToken();
        mUserUuid = ((G8penApplication) getApplication()).getUserUuid();
        mPicasso = ((G8penApplication) getApplication()).getImgLoader(this);
        mEventAdapter = new EventAdapter(new ArrayList<EventEntity>(),mPicasso,this);
        mMainLayout = (ViewGroup) findViewById(R.id.event_rl_main);
        mSnackBarUtil = new SnackBarUtil(mMainLayout,this);
        mMainList.setAdapter(mEventAdapter);
        mMainList.setLayoutManager(new LinearLayoutManager(this));
        mMainList.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL_LIST));
        mRefresh = (SwipeRefreshLayout) findViewById(R.id.event_srl_main);
        mRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });
        mRefresh.setRefreshing(true);
        setMainProgressBar(mMainLayout);
        showProgressBar(mMainList);
        refresh();

    }
    public void refresh(){

        mApiService.getInboxEvent(mUserUuid).enqueue(new Callback<EventEntityList>() {
            @Override
            public void onResponse(Call<EventEntityList> call, Response<EventEntityList> response) {
                mRefresh.setRefreshing(false);
                hideProgressBar(mMainList);
                if(response.isSuccess()){
                    mEventAdapter.clear();
                    mEventAdapter.addList(response.body().getNotifications());
                    mEventAdapter.notifyDataSetChanged();

                }else{
                    mSnackBarUtil.showSnackBar(response.code());
                }

            }

            @Override
            public void onFailure(Call<EventEntityList> call, Throwable t) {
                mRefresh.setRefreshing(false);
                hideProgressBar(mMainList);
                mSnackBarUtil.showSnackBar(SnackBarUtil.NETWORK_ERR);
            }
        });
    }
}
