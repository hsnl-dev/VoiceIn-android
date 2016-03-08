package tw.kits.voicein.adapter;


import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import tw.kits.voicein.R;
import tw.kits.voicein.activity.ContactEditActivity;
import tw.kits.voicein.fragment.ContactFragment;
import tw.kits.voicein.model.Contact;
import tw.kits.voicein.util.ServiceManager;
import tw.kits.voicein.util.UserAccessStore;

/**
 * Created by Henry on 2016/3/2.
 */
public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> {
    List<Contact> mContacts;
    Fragment mFragment;
    private static final String TAG = ContactAdapter.class.getName();

    public ContactAdapter(List<Contact> contacts, Fragment fragment) {
        mContacts = contacts;
        mFragment = fragment;
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
                holder.textName.setText(contact.getNickName());
            }else{
                holder.textName.setText(contact.getUserName());
            }

        }else{
            holder.textName.setText(contact.getUserName());
        }

        holder.company.setText(contact.getCompany());
        Context context = holder.circleImageView.getContext();
        Picasso picasso = ServiceManager.getPicassoDowloader(context, UserAccessStore.getToken());
        picasso.load(ServiceManager.API_BASE + "api/v1/avatars/" + contact.getProfilePhotoId() + "?size=mid")
                .into(holder.circleImageView);
        holder.circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mFragment.getActivity(),ContactEditActivity.class);
                intent.putExtra(ContactEditActivity.ARG_CONTACT,contact);
                mFragment.startActivityForResult(intent,ContactFragment.INTENT_EDIT_CONTACT);


            }
        });

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
        public TextView textName;
        public CircleImageView circleImageView;
        public TextView company;

        public ViewHolder(View itemView) {
            super(itemView);
            textName = (TextView) itemView.findViewById(R.id.contacti_tv_name);
            circleImageView = (CircleImageView) itemView.findViewById(R.id.contacti_img_avatar);
            company = (TextView) itemView.findViewById(R.id.contacti_tv_company);
        }

    }

}
