package tw.kits.voicein.fragment;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.google.zxing.qrcode.QRCodeReader;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tw.kits.voicein.G8penApplication;
import tw.kits.voicein.R;
import tw.kits.voicein.activity.ContactAddActivity;
import tw.kits.voicein.activity.ContactEditActivity;
import tw.kits.voicein.adapter.ContactAdapter;
import tw.kits.voicein.model.CallForm;
import tw.kits.voicein.model.Contact;
import tw.kits.voicein.util.CameraPermission;
import tw.kits.voicein.util.ColoredSnackBarUtil;
import tw.kits.voicein.util.ContactRetriever;
import tw.kits.voicein.util.DividerItemDecoration;
import tw.kits.voicein.util.InitCallCallBackImpl;
import tw.kits.voicein.util.PermissionHandler;
import tw.kits.voicein.util.QRCodeUtil;
import tw.kits.voicein.util.ScrollAwareFABBehavior;
import tw.kits.voicein.util.SnackBarUtil;
import tw.kits.voicein.util.VoiceInService;


/**
 * A simple {@link Fragment} subclass.
 */
public class ContactFragment extends Fragment implements View.OnClickListener {
    public static final int INTENT_ADD_CONTACT = 900;
    public static final int INTENT_EDIT_CONTACT = 800;
    public static final int INTENT_PICK = 7985;
    public static final String TAG = ContactFragment.class.getName();
    String mUserUuid;
    String mToken;
    Context mContext;
    RecyclerView mRvContact;
    CoordinatorLayout mMainLayout;
    FloatingActionButton mActionBtn;
    SwipeRefreshLayout mRefreshContainer;
    ContactAdapter mContactAdapter;
    VoiceInService mApiService;
    ProgressFragment mProgressDialog;
    ProgressCallFragment mProgressDialogCall;
    ContactRetriever mRetriever;
    TextView mState;
    CameraPermission mCameraPermission;

    public ContactFragment() {


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mContext = getContext();
        mApiService = ((G8penApplication) getActivity().getApplication()).getAPIService();
        mRetriever = new ContactRetriever(mApiService);
        mToken = ((G8penApplication) getActivity().getApplication()).getToken();
        mUserUuid = ((G8penApplication) getActivity().getApplication()).getUserUuid();

        View view = inflater.inflate(R.layout.fragment_contact, container, false);

        mProgressDialogCall = new ProgressCallFragment();
        mProgressDialog = new ProgressFragment();
        mRvContact = (RecyclerView) view.findViewById(R.id.contact_rv_items);
        mMainLayout = (CoordinatorLayout) view.findViewById(R.id.contact_frag_cl_main);
        mActionBtn = (FloatingActionButton) view.findViewById(R.id.contact_fab_plus);
        mRefreshContainer = (SwipeRefreshLayout) view.findViewById(R.id.contact_frag_sp_container);
        mState = (TextView) view.findViewById(R.id.contact_frag_tv_state);

        CoordinatorLayout.LayoutParams params =
                (CoordinatorLayout.LayoutParams) mActionBtn.getLayoutParams();
        params.setBehavior(new ScrollAwareFABBehavior());
        mActionBtn.requestLayout();
        //setting list view
        RecyclerView.ItemDecoration itemDecoration = new
                DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST);
        mRvContact.addItemDecoration(itemDecoration);
        mRvContact.setLayoutManager(new LinearLayoutManager(mContext));
        mContactAdapter = new ContactAdapter(new ArrayList<Contact>(), ContactFragment.this, mMainLayout);
        mRvContact.setAdapter(mContactAdapter);
        mRvContact.setLayoutManager(new LinearLayoutManager(this.getContext()));


        ContactAdapter.AdapterListener listListener = new ContactAdapter.AdapterListener() {
            @Override
            public void onListClick(int pos, Contact item) {
                Intent intent = new Intent(getContext(), ContactEditActivity.class);
                intent.putExtra(ContactEditActivity.ARG_CONTACT, item);
                startActivityForResult(intent, INTENT_EDIT_CONTACT);
            }

            @Override
            public void onListLongClick(final int pos, final Contact item) {
                final PickerDialogFragment.OnSelectListener listener = new PickerDialogFragment.OnSelectListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        switch (i){
                            case 0:
                                onListClick(pos, item);
                                break;
                            case 1:
                                DeleteDialogFragment.newInstance(new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        mApiService.delContact(item.getId()).enqueue(new Callback<ResponseBody>() {
                                            SnackBarUtil util = new SnackBarUtil(mMainLayout,getContext());
                                            @Override
                                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                                mContactAdapter.getContacts().remove(pos);
                                                mContactAdapter.notifyItemRemoved(pos);
                                                util.showSnackBar(response.code());

                                            }

                                            @Override
                                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                                util.showSnackBar(SnackBarUtil.NETWORK_ERR);
                                            }
                                        });

                                    }
                                }).show(getFragmentManager(),"ask");
                                break;
                            case 2:
                                dialog.dismiss();
                                break;
                        }
                    }

                };
                PickerDialogFragment pickerDialogFragment = PickerDialogFragment.newInstance(listener,new String[]{"檢視聯絡人","刪除聯絡人","取消"});
                pickerDialogFragment.show(getFragmentManager(),"options");
            }

            @Override
            public void onNoPhoneClick(int pos, Contact item) {
                    ColoredSnackBarUtil.primary(
                            Snackbar.make(mMainLayout, getString(R.string.forbidden_call_hint), Snackbar.LENGTH_SHORT)
                    ).show();
            }

            @Override
            public void onPhoneClick(int pos, Contact item) {
                CallForm form = new CallForm();
                form.setContactId(item.getId());
                mProgressDialogCall.show(getFragmentManager(),"wait");
                mApiService.createCall(mUserUuid, form)
                        .enqueue(new InitCallCallBackImpl(mProgressDialogCall,getContext(),mMainLayout));
            }
            @Override
            public void onFavoriteClick(int pos, Contact item){
                Log.e(TAG,item.toString());
                mProgressDialog.show(getFragmentManager(),"wait");
                mApiService.updateQRcodeILike(item.getId(),!item.getLike())
                        .enqueue(new FavoriteCallBack(item,mContactAdapter,pos));
            }
        };
        mContactAdapter.setContatctListListener(listListener);

        //setting action button
        mActionBtn.setOnClickListener(this);

        mRefreshContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshContact();
            }
        });
        mRefreshContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        refreshContact();
        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.e(TAG, "onRequestPermissionsResult: parse" );
        if(mCameraPermission!=null){
            Log.e(TAG, "onRequestPermissionsResult: parse" );
            mCameraPermission.parseResult(requestCode,permissions,grantResults);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.contact_fab_plus:
                PickerDialogFragment fragment = PickerDialogFragment.newInstance(
                        new PickerDialogFragment.OnSelectListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int i) {
                                super.onClick(dialog, i);
                                switch (i){
                                    case 0:
                                        mCameraPermission = new CameraPermission(new PermissionHandler.ViewHandler() {
                                            @Override
                                            public void onSuccessAsk() {
                                                IntentIntegrator scanIntegrator = IntentIntegrator.forSupportFragment(ContactFragment.this);
                                                scanIntegrator.initiateScan();
                                            }

                                            @Override
                                            public void onFailureAsk() {
                                                Log.e(TAG, "onFailureAsk: " );

                                                PermisionFailureFragment failureFragment = new PermisionFailureFragment();
                                                Log.e(TAG, "onFailureAsk:" +getFragmentManager());
                                                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                                                transaction.add(failureFragment,"warin");
                                                transaction.commitAllowingStateLoss();


                                            }
                                        });
                                        Log.e(TAG, "onClick: Test point 1");
                                        mCameraPermission.askPermissionForFrag(ContactFragment.this,getContext());


                                        break;
                                    case 1:
                                        Intent pickintent;
                                        pickintent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                        pickintent.setType("image/*");
                                        startActivityForResult(pickintent, INTENT_PICK);
                                        break;
                                    case 2:
                                        dialog.dismiss();
                                }
                            }
                        },new String [] {"從照相機掃描","從相簿選取","取消新增聯絡人"});

                fragment.show(getFragmentManager(),"show_picker");
                break;
        }

    }

    private void refreshContact() {
        mRefreshContainer.setRefreshing(true);
        if(mContactAdapter!=null){
            mContactAdapter.invalidateAllImg();
        }
        final SnackBarUtil snackBarUtil = new SnackBarUtil(mMainLayout,ContactFragment.this.getContext());
        mRetriever
                .getContactList(mUserUuid, new ContactRetriever.Callback() {
                    @Override
                    public void onSuccess(List<Contact> contacts) {
                        mRefreshContainer.setRefreshing(false);
                        mContactAdapter.clear();
                        mContactAdapter.addAll(contacts);
                        mContactAdapter.notifyDataSetChanged();
                        if(contacts.size()==0){
                            mState.setVisibility(View.VISIBLE);
                        }else{
                            mState.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onError(Response<List<Contact>> response) {
                        mRefreshContainer.setRefreshing(false);
                        snackBarUtil.showSnackBar(response.code());
                        Log.e(TAG, "Fial");

                    }

                    @Override
                    public void onFailure(Call<List<Contact>> call, Throwable t) {
                        mRefreshContainer.setRefreshing(false);
                        Log.e(TAG, t.toString());
                        snackBarUtil.showSnackBar(SnackBarUtil.NETWORK_ERR);
                    }
                });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if (scanningResult != null) {
                String scanContent = scanningResult.getContents();
                Log.e(TAG, scanContent.toString());
                Intent i = new Intent(this.getActivity(), ContactAddActivity.class);
                i.putExtra(ContactAddActivity.ARG_QRCODE, scanningResult.getContents());
                startActivityForResult(i, INTENT_ADD_CONTACT);
            } else {
                switch (requestCode) {
                    case INTENT_PICK:
                        String parsedUrl = QRCodeUtil.readQRCode(getContext(),data.getData());
                        if(parsedUrl==null){
                            ColoredSnackBarUtil.primary(Snackbar.make(mMainLayout, "無效的圖片", Snackbar.LENGTH_LONG)).show();
                            return;
                        }
                        Log.e(TAG, "onActivityResult: "+parsedUrl );
                        Intent i = new Intent(this.getActivity(), ContactAddActivity.class);
                        i.putExtra(ContactAddActivity.ARG_QRCODE, parsedUrl);
                        startActivityForResult(i, INTENT_ADD_CONTACT);

                    case INTENT_ADD_CONTACT:
                        Log.i(TAG, "Success");
                        ColoredSnackBarUtil.primary(Snackbar.make(mMainLayout, getString(R.string.success), Snackbar.LENGTH_LONG)).show();
                        mRefreshContainer.setRefreshing(true);
                        refreshContact();
                        break;
                    case INTENT_EDIT_CONTACT:
                        Log.i(TAG, "Success");
                        ColoredSnackBarUtil.primary(Snackbar.make(mMainLayout, getString(R.string.success), Snackbar.LENGTH_LONG)).show();
//                        mRefreshContainer.setRefreshing(true);
//                        refreshContact();
                        if(ContactEditActivity.DELETE_ACTION ==data.getIntExtra(ContactEditActivity.EXTRA_ACTION,-1)){
                            mContactAdapter.findAndDelete((Contact)data.getSerializableExtra(ContactEditActivity.EXTRA_CONTACT));
                        }else if(ContactEditActivity.UPDATE_ACTION ==data.getIntExtra(ContactEditActivity.EXTRA_ACTION,-1)){
                            Log.e(TAG, "onActivityResult: ");
                            mContactAdapter.findAndModify((Contact)data.getSerializableExtra(ContactEditActivity.EXTRA_CONTACT));

                        }
                        break;
                }


            }

        }
    }
    private class FavoriteCallBack implements Callback<ResponseBody>{
        Contact contact;
        ContactAdapter adapter;
        int pos;
        public FavoriteCallBack(Contact contact, ContactAdapter adapter, int pos){

            this.contact = contact;
            this.adapter = adapter;
            this.pos = pos;
        }
        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
            mProgressDialog.dismiss();
            if(response.isSuccess()){
                contact.setLike(!contact.getLike());
                adapter.notifyItemChanged(pos);
            }else{
                switch (response.code()){
                    case 403:
                        ColoredSnackBarUtil.primary(
                                Snackbar.make(mMainLayout, getString(R.string.forbidden_call_hint), Snackbar.LENGTH_SHORT)
                        ).show();
                        break;
                    case 401:
                        ColoredSnackBarUtil.primary(
                                Snackbar.make(mMainLayout, getString(R.string.user_not_auth), Snackbar.LENGTH_SHORT)
                        ).show();
                        break;
                    default:
                        ColoredSnackBarUtil.primary(
                                Snackbar.make(mMainLayout, getString(R.string.server_err), Snackbar.LENGTH_SHORT)
                        ).show();

                }
            }
        }

        @Override
        public void onFailure(Call<ResponseBody> call, Throwable t) {
            mProgressDialog.dismiss();
            ColoredSnackBarUtil.primary(
                    Snackbar.make(mMainLayout, getString(R.string.network_err), Snackbar.LENGTH_SHORT)
            ).show();
        }
    }

}
