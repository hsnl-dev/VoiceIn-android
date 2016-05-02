package tw.kits.voicein.fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tw.kits.voicein.G8penApplication;
import tw.kits.voicein.R;
import tw.kits.voicein.activity.GroupActivity;
import tw.kits.voicein.activity.GroupAddActivity;
import tw.kits.voicein.adapter.GroupAdapter;
import tw.kits.voicein.model.Group;
import tw.kits.voicein.model.GroupList;
import tw.kits.voicein.util.DividerItemDecoration;
import tw.kits.voicein.util.SnackBarUtil;
import tw.kits.voicein.util.VoiceInService;

public class GroupFragment extends Fragment implements GroupAdapter.OnClickListener {
    VoiceInService mApiService;
    String mUserUuid;
    RecyclerView mRview;
    View mMainView;
    FloatingActionButton mFab;
    GroupAdapter mGroupAdapter;
    SwipeRefreshLayout mSwipeContainer;
    SnackBarUtil mSnackBarHelper;
    static  final  int  ADD_GROUP = 689;
    public GroupFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_group, container, false);
        mApiService = ((G8penApplication)getActivity().getApplication()).getAPIService();
        mMainView = view.findViewById(R.id.group_frag_cl_main);
        mUserUuid =  ((G8penApplication)getActivity().getApplication()).getUserUuid();
        mRview = (RecyclerView)view.findViewById(R.id.group_frag_rv_items);
        mRview.setLayoutManager(new LinearLayoutManager(getContext()));
        mFab = (FloatingActionButton)view.findViewById(R.id.group_frag_fab_plus);
        mSwipeContainer = (SwipeRefreshLayout)view.findViewById(R.id.group_frag_sp_container);


        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(GroupFragment.this.getContext(), GroupAddActivity.class);
                startActivityForResult(i,ADD_GROUP);
            }
        });


        mSwipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        RecyclerView.ItemDecoration itemDecoration = new
                DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST);
        mRview.addItemDecoration(itemDecoration);

        mSnackBarHelper = new SnackBarUtil(mMainView,this.getContext());
        mSwipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });
        mGroupAdapter = new GroupAdapter(new ArrayList<Group>(),GroupFragment.this);
        mRview.setAdapter(mGroupAdapter);
        refresh();
        return view;
    }
    public void  refresh(){


        mApiService.getAccountGroupList(mUserUuid).enqueue(new Callback<GroupList>() {
            @Override
            public void onResponse(Call<GroupList> call, Response<GroupList> response) {
                mSwipeContainer.setRefreshing(false);
                if(response.isSuccess()){
                    mGroupAdapter.clear();
                    mGroupAdapter.addList(response.body().getGroups());
                    mGroupAdapter.notifyDataSetChanged();

                }else{
                    mSnackBarHelper.showSnackBar(response.code());


                }
            }

            @Override
            public void onFailure(Call<GroupList> call, Throwable t) {
                mSwipeContainer.setRefreshing(false);
                mSnackBarHelper.showSnackBar(SnackBarUtil.NETWORK_ERR);
            }
        });

    }

    @Override
    public void onClick(View view, Group group) {
        startGroup(group);


    }
    public void startGroup(Group group){
        Intent i = new Intent(getContext(), GroupActivity.class);
        i.putExtra(GroupActivity.ARG_GROUP,group);
        startActivity(i);
    }

    @Override
    public void onLongClick(View view, final Group group) {
        Log.e("TAG","sfuck");
        String[] actions = {"刪除群組","檢視群組","取消"};
        PickerDialogFragment fragment = PickerDialogFragment.newInstance(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case 0:
                        delGroup(group.getGroupId());
                        break;
                    case 1:
                        startGroup(group);
                        break;
                    default:
                        dialog.dismiss();


                }
            }
        },actions);
        fragment.show(getFragmentManager(),"show_options");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e("TAG",requestCode+"s"+resultCode);
        if(requestCode==ADD_GROUP &&resultCode== Activity.RESULT_OK){
            mSwipeContainer.setRefreshing(true);
            refresh();
            mSnackBarHelper.showSnackBar(200);
        }
    }
    public void delGroup(String gid){
        final ProgressFragment progressFragment = new ProgressFragment();
        final SnackBarUtil sh = new SnackBarUtil(mMainView,this.getContext());
        progressFragment.show(getFragmentManager(),"wait");
        mApiService.delGroup(mUserUuid,gid).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                progressFragment.dismiss();
                if(response.isSuccess()){
                   sh.showSnackBar(200);
                    refresh();
                }else{
                   sh.showSnackBar(response.code());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progressFragment.dismiss();
                sh.showSnackBar(SnackBarUtil.NETWORK_ERR);
            }
        });

    }

}
