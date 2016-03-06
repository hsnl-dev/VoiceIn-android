package tw.kits.voicein.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import tw.kits.voicein.R;
import tw.kits.voicein.model.Contact;
import tw.kits.voicein.util.ServiceManager;
import tw.kits.voicein.util.UserAccessStore;

/**
 * Created by Henry on 2016/3/2.
 */
public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> {
    List<Contact> mContacts;
    public ContactAdapter(List<Contact> contacts){
        mContacts = contacts;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View contactView  = LayoutInflater.from(context).inflate(R.layout.item_contact, parent, false);
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Contact contact = mContacts.get(position);
        TextView nameTextView = holder.textName;
        nameTextView.setText(contact.getUserName());
        holder.company.setText(contact.getCompany());
        Context context = holder.circleImageView.getContext();
        Picasso picasso = ServiceManager.getPicassoDowloader(context, UserAccessStore.getToken());
        picasso.load(ServiceManager.API_BASE + "api/v1/avatars/" + contact.getProfilePhotoId() + "?size=mid")
        .into(holder.circleImageView);

    }

    @Override
    public int getItemCount() {
        return mContacts.size();
    }

    public class ViewHolder  extends RecyclerView.ViewHolder{
        public TextView textName;
        public CircleImageView circleImageView;
        public TextView company;
        public ViewHolder(View itemView) {
            super(itemView);
            textName = (TextView) itemView.findViewById(R.id.contacti_tv_name);
            circleImageView = (CircleImageView) itemView.findViewById(R.id.contacti_img_avatar);
            company = (TextView)itemView.findViewById(R.id.contacti_tv_company);
        }

    }
}
