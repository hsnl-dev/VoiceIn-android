package tw.kits.voicein.adapter;


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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import tw.kits.voicein.R;

import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
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
public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> {
    List<Contact> mContacts;
    Fragment mFragment;
    ProgressDialog progressDialog;
    CoordinatorLayout mLayout;
    private static final String TAG = ContactAdapter.class.getName();

    public ContactAdapter(List<Contact> contacts, Fragment fragment, CoordinatorLayout layout) {
        mContacts = contacts;
        mFragment = fragment;
        mLayout = layout;
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
        final Contact contact = mContacts.get(position);
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
                Intent intent = new Intent(mFragment.getActivity(), ContactEditActivity.class);
                intent.putExtra(ContactEditActivity.ARG_CONTACT, contact);
                mFragment.startActivityForResult(intent, ContactFragment.INTENT_EDIT_CONTACT);


            }
        });

        Drawable rejectIcon = ContextCompat.getDrawable(mFragment.getContext()
                , R.drawable.ic_phone_locked_grey_600_36dp);
        Drawable phoneIcon = ContextCompat.getDrawable(mFragment.getContext()
                , R.drawable.ic_call_blue_grey_900_36dp);
        if(contact.getProviderIsEnable()){
            holder.mImgCall.setImageDrawable(phoneIcon);
            holder.mImgCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CallForm form = new CallForm();
                    form.setCallee(contact.getPhoneNumber());
                    form.setCaller(UserAccessStore.getPhoneNum());
                    VoiceInService service = ServiceManager.createService(UserAccessStore.getToken());
                    progressDialog= ProgressDialog.show(
                            mFragment.getContext(),
                            mFragment.getString(R.string.wait),
                            mFragment.getString(R.string.wait_notice),
                            true);
                    service.createCall(UserAccessStore.getUserUuid(), contact.getQrCodeUuid(),form)
                            .enqueue(new CallCallBack());
                }
            });
            switch (contact.getChargeType()){
                case ChargeTypeConstant.FREE:
                    holder.mStatus.setText(mFragment.getString(R.string.free_charge));
                    break;
                case ChargeTypeConstant.ICON:
                    holder.mStatus.setText(mFragment.getString(R.string.icon_charge));
                    break;
                case ChargeTypeConstant.CHARGE:
                    holder.mStatus.setText(mFragment.getString(R.string.must_charge));
                    break;
                default:
                    holder.mStatus.setText(mFragment.getString(R.string.must_charge));
                    break;
            }
        }else {
            holder.mImgCall.setImageDrawable(rejectIcon);
            holder.mStatus.setText(mFragment.getString(R.string.forbidden_call));
            holder.mImgCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    ColoredSnackBar.primary(
                            Snackbar.make(mLayout, mFragment.getString(R.string.forbidden_call_hint), Snackbar.LENGTH_SHORT)
                    ).show();
                }
            });
        }



    }
    private class CallCallBack implements Callback<ResponseBody>{
        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
            progressDialog.dismiss();
            if(response.isSuccess()){

            }else{
                switch (response.code()){
                    case 403:
                        ColoredSnackBar.primary(
                                Snackbar.make(mLayout, mFragment.getString(R.string.forbidden_call_hint), Snackbar.LENGTH_SHORT)
                        ).show();
                        break;
                    case 401:
                        ColoredSnackBar.primary(
                                Snackbar.make(mLayout, mFragment.getString(R.string.user_not_auth), Snackbar.LENGTH_SHORT)
                        ).show();
                        break;
                    default:
                        ColoredSnackBar.primary(
                                Snackbar.make(mLayout, mFragment.getString(R.string.server_err), Snackbar.LENGTH_SHORT)
                        ).show();

                }
            }
        }

        @Override
        public void onFailure(Call<ResponseBody> call, Throwable t) {
            progressDialog.dismiss();
            ColoredSnackBar.primary(
                    Snackbar.make(mLayout, mFragment.getString(R.string.network_err), Snackbar.LENGTH_SHORT)
            ).show();
        }
    }

    @Override
    public int getItemCount() {
        return mContacts.size();
    }
    public void clear(){
        mContacts.clear();

    }
    public boolean addAll(List<Contact> list){
        boolean result = mContacts.addAll(list);

        return result;
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
