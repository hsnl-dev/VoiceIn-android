package tw.kits.voicein.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import tw.kits.voicein.R;
import tw.kits.voicein.model.Contact;

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
        nameTextView.setText(contact.getName());
    }

    @Override
    public int getItemCount() {
        return mContacts.size();
    }

    public class ViewHolder  extends RecyclerView.ViewHolder{
        public TextView textName;
        public ViewHolder(View itemView) {
            super(itemView);
            textName = (TextView) itemView.findViewById(R.id.contacti_tv_name);
        }
    }
}
