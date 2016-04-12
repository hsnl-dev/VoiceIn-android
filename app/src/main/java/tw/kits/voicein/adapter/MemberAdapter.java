package tw.kits.voicein.adapter;


import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
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
public class MemberAdapter extends RecyclerView.Adapter<MemberAdapter.ViewHolder> {
    List<Contact> mContacts;
    Activity mFragment;
    SparseBooleanArray mChecked;
    String mToken;
    String mUserUuid;
    Picasso mImgLoader;
    private static final String TAG = MemberAdapter.class.getName();



    public MemberAdapter(List<Contact> contacts, Activity fragment) {
        mToken =((G8penApplication)fragment.getApplication()).getToken();
        mUserUuid = ((G8penApplication)fragment.getApplication()).getUserUuid();
        mImgLoader = ((G8penApplication)fragment.getApplication()).getImgLoader(fragment);
        mContacts = contacts;

        mFragment = fragment;

        mChecked = new SparseBooleanArray(contacts.size());
    }
    public void setInitState(List<Contact> members){
        int curIdx = 0;

        for(int i = 0 ; i<members.size() ; i++){
            if(curIdx >= members.size())
                break;
            mChecked.put(i,true);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View contactView = LayoutInflater.from(context).inflate(R.layout.item_member, parent, false);
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
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
        Picasso picasso = mImgLoader;
        picasso.load(ServiceConstant.getAvatarById(contact.getProfilePhotoId(), ServiceConstant.PIC_SIZE_MID))
                .noFade()
                .placeholder(R.drawable.ic_person_white_48dp)
                .into(holder.mCircleImageView);

        if(contact.getLike()){
            holder.mImgFavorite.setColorFilter(ContextCompat.getColor(mFragment,R.color.colorAccent));
        }else{
            holder.mImgFavorite.setColorFilter(ContextCompat.getColor(mFragment,R.color.divider));
        }
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mChecked.get(position)){
                    mChecked.put(position,true);
                    holder.mCheck.setChecked(true);

                }else{
                    mChecked.put(position,false);
                    holder.mCheck.setChecked(false);
                }
            }
        };
        holder.mItemLayout.setOnClickListener(listener);

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



    }
    public List<String> getSelectedContactId(){
        List<String> contacts = new ArrayList<>();

        for(int i = 0;i<mContacts.size();i++){
            if(mChecked.get(i)){
                contacts.add(mContacts.get(i).getId());
            }
        }
        return contacts;
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

        public CheckBox mCheck;
        public View.OnClickListener itemListener;
        public TextView mName;
        public CircleImageView mCircleImageView;
        public TextView mCompany;
        public TextView mStatus;
        public RelativeLayout mItemLayout;
        public ImageView mImgFavorite;
        public ViewHolder(View itemView) {
            super(itemView);

            mName = (TextView) itemView.findViewById(R.id.memberi_tv_name);
            mCircleImageView = (CircleImageView) itemView.findViewById(R.id.memberi_img_avatar);
            mCompany = (TextView) itemView.findViewById(R.id.memberi_tv_company);
            mItemLayout = (RelativeLayout) itemView.findViewById(R.id.memberi_lo_item);
            mImgFavorite = (ImageView) itemView.findViewById(R.id.memberi_img_favorite);
            mCheck = (CheckBox)itemView.findViewById(R.id.memberi_cb_check);
            mStatus = (TextView)itemView.findViewById(R.id.memberi_tv_status);
        }


    }

}
