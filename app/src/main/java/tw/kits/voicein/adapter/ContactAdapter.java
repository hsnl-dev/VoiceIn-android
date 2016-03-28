package tw.kits.voicein.adapter;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import tw.kits.voicein.G8penApplication;
import tw.kits.voicein.R;
import tw.kits.voicein.model.Contact;
import tw.kits.voicein.util.ChargeTypeConstant;
import tw.kits.voicein.util.ServiceConstant;

/**
 * Created by Henry on 2016/3/2.
 */
public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> {
    List<Contact> mContacts;
    Fragment mFragment;
    CoordinatorLayout mLayout;
    String mToken;
    String mUserUuid;
    AdapterListener listListener;
    Picasso mImgLoader;
    private static final String TAG = ContactAdapter.class.getName();
    public interface AdapterListener {
        public void onPhoneClick(int pos, Contact item);
        public void onNoPhoneClick(int pos, Contact item);
        public void onListClick(int pos, Contact item);
    }

    public void setContatctListListener(AdapterListener listListener){
        this.listListener = listListener;
    }

    public ContactAdapter(List<Contact> contacts, Fragment fragment,
                          CoordinatorLayout layout) {
        mToken =((G8penApplication)fragment.getActivity().getApplication()).getToken();
        mUserUuid = ((G8penApplication)fragment.getActivity().getApplication()).getUserUuid();
        mImgLoader = ((G8penApplication)fragment.getActivity().getApplication()).getImgLoader(fragment.getContext());
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
        if(contact.getNickName()!=null && !"".equals(contact.getNickName())){
            holder.mName.setText(contact.getNickName());

        }else if(contact.getUserName()!=null && !"".equals(contact.getUserName())){

            holder.mName.setText(contact.getUserName());

        }else{
            holder.mName.setText(mFragment.getString(R.string.no_name));
        }
        if(contact.getCompany()!=null && !"".equals(contact.getCompany())){
            holder.mCompany.setText(contact.getCompany());

        }else{

            holder.mCompany.setText(mFragment.getString(R.string.no_company));

        }



        Context context = holder.mCircleImageView.getContext();
        Picasso picasso = mImgLoader;
        picasso.load(ServiceConstant.getAvatarById(contact.getProfilePhotoId(), ServiceConstant.PIC_SIZE_MID))
                .noFade()
                .placeholder(R.drawable.ic_person_white_48dp)
                .into(holder.mCircleImageView);
        holder.mItemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listListener.onListClick(position, contact);
            }
        });


        if(contact.getProviderIsEnable()){
            holder.bindPositive(listListener,position,contact);
        }else {
            holder.bindNegative(listListener,position,contact);
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
        Drawable rejectIcon = ContextCompat.getDrawable(mFragment.getContext()
                , R.drawable.ic_phone_locked_grey_600_36dp);
        Drawable phoneIcon = ContextCompat.getDrawable(mFragment.getContext()
                , R.drawable.ic_call_blue_grey_900_36dp);
        public TextView mName;
        public CircleImageView mCircleImageView;
        public TextView mCompany;
        public RelativeLayout mItemLayout;
        public TextView mStatus;
        public ImageView mImgCall;
        public void  bindPositive(final AdapterListener listListener, final int position, final Contact contact){
            mImgCall.setImageDrawable(phoneIcon);
            mImgCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listListener.onPhoneClick(position,contact);

                }
            });
            switch (contact.getChargeType()){
                case ChargeTypeConstant.FREE:
                    mStatus.setText(mFragment.getString(R.string.free_charge));
                    break;
                case ChargeTypeConstant.ICON:
                    mStatus.setText(mFragment.getString(R.string.icon_charge));
                    break;
                case ChargeTypeConstant.CHARGE:
                    mStatus.setText(mFragment.getString(R.string.must_charge));
                    break;
                default:
                    mStatus.setText(mFragment.getString(R.string.must_charge));
                    break;
            }
        }
        public void bindNegative(final AdapterListener listListener, final int position, final Contact contact){
            mImgCall.setImageDrawable(rejectIcon);
            mStatus.setText(mFragment.getString(R.string.forbidden_call));
            mImgCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listListener.onNoPhoneClick(position,contact);
                }
            });

        }
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
