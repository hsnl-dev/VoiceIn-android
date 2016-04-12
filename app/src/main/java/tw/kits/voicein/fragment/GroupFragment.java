package tw.kits.voicein.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tw.kits.voicein.G8penApplication;
import tw.kits.voicein.R;
import tw.kits.voicein.adapter.GroupAdapter;
import tw.kits.voicein.model.Group;
import tw.kits.voicein.util.SnackBarHelper;
import tw.kits.voicein.util.VoiceInService;

public class GroupFragment extends Fragment implements GroupAdapter.OnClickListener{
    VoiceInService mApiService;
    String mUserUuid;
    RecyclerView mRview;
    View mMainView;
    FloatingActionButton mFab;
    GroupAdapter mGroupAdapter;
    public GroupFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_group, container, false);
        mApiService = ((G8penApplication)getActivity().getApplication()).getAPIService();
        mMainView = view.findViewById(R.id.contact_frag_cl_main);
        mUserUuid =  ((G8penApplication)getActivity().getApplication()).getUserUuid();
        mRview = (RecyclerView)view.findViewById(R.id.group_frag_rv_main);
        mRview.setLayoutManager(new LinearLayoutManager(getContext()));
        mFab = (FloatingActionButton)view.findViewById(R.id.group_frag_fab_add);
        onRefresh();
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
            }
        });

        return view;
    }
    public void  onRefresh(){
        final SnackBarHelper sh = new SnackBarHelper(mMainView,this.getContext());
        mApiService.getAccountGroupList(mUserUuid).enqueue(new Callback<List<Group>>() {
            @Override
            public void onResponse(Call<List<Group>> call, Response<List<Group>> response) {
                if(response.isSuccess()){
                    mGroupAdapter = new GroupAdapter(response.body(),GroupFragment.this);
                    mRview.setAdapter(mGroupAdapter);

                }else{
                    sh.showSnackBar(response.code());


                }
            }

            @Override
            public void onFailure(Call<List<Group>> call, Throwable t) {
                sh.showSnackBar(SnackBarHelper.NETWORK_ERR);
            }
        });

    }

    @Override
    public void onClick(View view, Group group) {


    }
}
