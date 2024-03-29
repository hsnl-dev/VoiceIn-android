package tw.kits.voicein.fragment;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.test.suitebuilder.TestMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tw.kits.voicein.G8penApplication;
import tw.kits.voicein.R;
import tw.kits.voicein.adapter.RecordAdapter;
import tw.kits.voicein.model.CallForm;
import tw.kits.voicein.model.Record;
import tw.kits.voicein.model.RecordList;
import tw.kits.voicein.util.ColoredSnackBarUtil;
import tw.kits.voicein.util.DividerItemDecoration;
import tw.kits.voicein.util.InitCallCallBackImpl;
import tw.kits.voicein.util.SnackBarUtil;
import tw.kits.voicein.util.VoiceInService;

public class RecordFragment extends Fragment {
    VoiceInService mApiService;
    String mUserUuid;
    RecyclerView mRview;
    View mMainView;
    FloatingActionButton mFab;
    Picasso mImgLoader;
    RecordAdapter mRecordAdapter;
    SwipeRefreshLayout mSwipeContainer;
    SnackBarUtil mSnackBarHelper;
    TextView mState;
    ProgressFragment mProgressDialog;
    ProgressCallFragment mProgressDialogCall;
    public RecordFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_record, container, false);
        mApiService = ((G8penApplication) getActivity().getApplication()).getAPIService();

        mImgLoader = ((G8penApplication) getActivity().getApplication()).getImgLoader(this.getContext());
        mMainView = view.findViewById(R.id.record_frag_cl_main);
        mUserUuid = ((G8penApplication) getActivity().getApplication()).getUserUuid();
        mRview = (RecyclerView) view.findViewById(R.id.record_frag_rv_main);
        mState = (TextView) view.findViewById(R.id.record_frag_tv_state);
        mRview.setLayoutManager(new LinearLayoutManager(getContext()));
//        mFab = (FloatingActionButton)view.findViewById(R.id.group_frag_fab_add);
        mSwipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.record_frag_sp_container);
        mSwipeContainer.setRefreshing(true);
        refresh();
//        mFab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent i = new Intent(RecordFragment.this.getContext(), GroupAddActivity.class);
//                startActivityForResult(i,ADD_GROUP);
//            }
//        });
        mSwipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });

        mSwipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        RecyclerView.ItemDecoration itemDecoration = new
                DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST);
        mRview.addItemDecoration(itemDecoration);
        mRecordAdapter = new RecordAdapter(new ArrayList<Record>(), mImgLoader, RecordFragment.this.getContext());
        mProgressDialog = new ProgressFragment();
        mProgressDialogCall = new ProgressCallFragment();
        mRecordAdapter.setOnAdaterListener(new RecordAdapter.AdapterListener() {
            @Override
            public void onPhoneClick(int pos, Record item) {
                CallForm form = new CallForm();
                form.setContactId(item.getContactId());
                mProgressDialogCall.show(getFragmentManager(),"wait");
                mApiService.createCall(mUserUuid, form)
                        .enqueue(new InitCallCallBackImpl(mProgressDialogCall,getContext(),mMainView));
            }

            @Override
            public void onListClick(int pos, Record item) {

            }
        });
        mRview.setAdapter(mRecordAdapter);
        mSnackBarHelper = new SnackBarUtil(mMainView, this.getContext());
        return view;
    }

    public void refresh() {


        mApiService.getRecords(mUserUuid, null).enqueue(new Callback<RecordList>() {
            @Override
            public void onResponse(Call<RecordList> call, Response<RecordList> response) {
                mSwipeContainer.setRefreshing(false);
                if (response.isSuccess()) {
                    List<Record> recordList = response.body().getRecords();
                    mRecordAdapter.clear();
                    mRecordAdapter.addList(response.body().getRecords());

                    mRecordAdapter.notifyDataSetChanged();

                    if(recordList.size()==0){
                        mState.setVisibility(View.VISIBLE);
                    }else{
                        mState.setVisibility(View.GONE);
                    }


                } else {
                    mSnackBarHelper.showSnackBar(response.code());


                }
            }

            @Override
            public void onFailure(Call<RecordList> call, Throwable t) {
                mSwipeContainer.setRefreshing(false);
                mSnackBarHelper.showSnackBar(SnackBarUtil.NETWORK_ERR);
            }
        });

    }


}
