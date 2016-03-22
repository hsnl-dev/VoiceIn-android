package tw.kits.voicein.adapter;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tw.kits.voicein.R;
import tw.kits.voicein.activity.ContactEditActivity;
import tw.kits.voicein.fragment.ContactFragment;
import tw.kits.voicein.model.CallForm;
import tw.kits.voicein.model.Contact;
import tw.kits.voicein.util.ChargeTypeConstant;
import tw.kits.voicein.util.ColoredSnackBar;
import tw.kits.voicein.util.ServiceManager;
import tw.kits.voicein.util.UserAccessStore;
import tw.kits.voicein.util.VoiceInService;

/**
 * Created by Henry on 2016/3/2.
 */
public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.ViewHolder> implements Filterable{
    List<Contact> mContacts;
    List<Contact> mFilteredList;
    Activity mActivity;
    ProgressDialog progressDialog;
    View mLayout;
    Filter mFilter;
    private static final String TAG = SearchResultAdapter.class.getName();

    public SearchResultAdapter(List<Contact> contacts, Activity activity, View layout) {
        mContacts = contacts;
        mFilteredList = contacts;
        mActivity= activity;
        mLayout = layout;
        mFilter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                List<Contact> filteredList = null;
                if(constraint!=null && !"".equals(constraint)){
                    filteredList=new ArrayList<>();
                    for(Contact contact :mContacts){
                        if(contact.getUserName().contains(constraint)){
                            filteredList.add(contact);
                        }
                    }
                }else{
                    filteredList=mContacts;

                }
                results.count = filteredList.size();
                results.values = filteredList;
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults filteredList) {
                SearchResultAdapter.this.mFilteredList = (List<Contact>)filteredList.values;
                SearchResultAdapter.this.notifyDataSetChanged();
            }
        };
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View contactView = LayoutInflater.from(context).inflate(R.layout.item_contact, parent, false);
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Contact contact = mFilteredList.get(position);
        Log.e(TAG, contact.getNickName());
        if(contact.getNickName()!=null){
            if(contact.getNickName().equals("")==false){
                holder.mName.setText(contact.getNickName());
            }else{
                holder.mName.setText(contact.getUserName());
            }

        }else{
            holder.mName.setText(contact.getUserName());
        }

        holder.mCompany.setText(contact.getCompany());


        Context context = holder.mCircleImageView.getContext();
        Picasso picasso = ServiceManager.getPicassoDowloader(context, UserAccessStore.getToken());
        picasso.load(ServiceManager.API_BASE + "api/v1/avatars/" + contact.getProfilePhotoId() + "?size=mid")
                .into(holder.mCircleImageView);
        holder.mItemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, ContactEditActivity.class);
                intent.putExtra(ContactEditActivity.ARG_CONTACT, contact);
                mActivity.startActivityForResult(intent, ContactFragment.INTENT_EDIT_CONTACT);


            }
        });

        Drawable rejectIcon = ContextCompat.getDrawable(mActivity
                , R.drawable.ic_phone_locked_grey_600_36dp);
        Drawable phoneIcon = ContextCompat.getDrawable(mActivity
                , R.drawable.ic_call_blue_grey_900_36dp);
        if(contact.getProviderIsEnable()){
            holder.mImgCall.setImageDrawable(phoneIcon);
            holder.mImgCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CallForm form = new CallForm();
                    form.setContactId(contact.getId());
                    VoiceInService service = ServiceManager.createService(UserAccessStore.getToken());
                    progressDialog= ProgressDialog.show(
                            mActivity,
                            mActivity.getString(R.string.wait),
                            mActivity.getString(R.string.wait_notice),
                            true);
                    service.createCall(UserAccessStore.getUserUuid(),form)
                            .enqueue(new CallCallBack());
                }
            });
            switch (contact.getChargeType()){
                case ChargeTypeConstant.FREE:
                    holder.mStatus.setText(mActivity.getString(R.string.free_charge));
                    break;
                case ChargeTypeConstant.ICON:
                    holder.mStatus.setText(mActivity.getString(R.string.icon_charge));
                    break;
                case ChargeTypeConstant.CHARGE:
                    holder.mStatus.setText(mActivity.getString(R.string.must_charge));
                    break;
                default:
                    holder.mStatus.setText(mActivity.getString(R.string.must_charge));
                    break;
            }
        }else {
            holder.mImgCall.setImageDrawable(rejectIcon);
            holder.mStatus.setText(mActivity.getString(R.string.forbidden_call));
            holder.mImgCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    ColoredSnackBar.primary(
                            Snackbar.make(mLayout, mActivity.getString(R.string.forbidden_call_hint), Snackbar.LENGTH_SHORT)
                    ).show();
                }
            });
        }



    }



    @Override
    public Filter getFilter() {
        return  mFilter;
    }

    private class CallCallBack implements Callback<ResponseBody>{
        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
            progressDialog.dismiss();
            if(response.isSuccess()){

            }else{
                switch (response.code()){
                    case 403:
                        ColoredSnackBar.primary(
                                Snackbar.make(mLayout, mActivity.getString(R.string.forbidden_call_hint), Snackbar.LENGTH_SHORT)
                        ).show();
                        break;
                    case 401:
                        ColoredSnackBar.primary(
                                Snackbar.make(mLayout, mActivity.getString(R.string.user_not_auth), Snackbar.LENGTH_SHORT)
                        ).show();
                        break;
                    default:
                        ColoredSnackBar.primary(
                                Snackbar.make(mLayout, mActivity.getString(R.string.server_err), Snackbar.LENGTH_SHORT)
                        ).show();

                }
            }
        }

        @Override
        public void onFailure(Call<ResponseBody> call, Throwable t) {
            progressDialog.dismiss();
            ColoredSnackBar.primary(
                    Snackbar.make(mLayout, mActivity.getString(R.string.network_err), Snackbar.LENGTH_SHORT)
            ).show();
        }
    }

    @Override
    public int getItemCount() {
        return mFilteredList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mName;
        public CircleImageView mCircleImageView;
        public TextView mCompany;
        public RelativeLayout mItemLayout;
        public TextView mStatus;
        public ImageView mImgCall;

        public ViewHolder(View itemView) {
            super(itemView);
            mStatus = (TextView) itemView.findViewById(R.id.contacti_tv_status);
            mName = (TextView) itemView.findViewById(R.id.contacti_tv_name);
            mCircleImageView = (CircleImageView) itemView.findViewById(R.id.contacti_img_avatar);
            mCompany = (TextView) itemView.findViewById(R.id.contacti_tv_company);
            mItemLayout = (RelativeLayout) itemView.findViewById(R.id.contacti_lo_item);
            mImgCall = (ImageView) itemView.findViewById(R.id.contacti_img_call);
        }

    }

}
