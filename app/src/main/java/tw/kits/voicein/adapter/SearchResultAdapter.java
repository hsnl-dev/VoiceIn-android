package tw.kits.voicein.adapter;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
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
import tw.kits.voicein.G8penApplication;
import tw.kits.voicein.R;
import tw.kits.voicein.model.Contact;
import tw.kits.voicein.util.ChargeTypeConstant;
import tw.kits.voicein.util.ServiceManager;

/**
 * Created by Henry on 2016/3/2.
 */
public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.ViewHolder> implements Filterable {
    private static final String TAG = SearchResultAdapter.class.getName();
    List<Contact> mContacts;
    List<Contact> mFilteredList;
    AppCompatActivity mActivity;
    Filter mFilter;
    View mLayout;
    String mToken;
    String mUserUuid;
    AdapterListener listListener;

    public SearchResultAdapter(List<Contact> contacts, AppCompatActivity activity,
                               View layout) {
        mToken = ((G8penApplication) activity.getApplication()).getToken();
        mUserUuid = ((G8penApplication) activity.getApplication()).getUserUuid();
        mContacts = contacts;
        mActivity = activity;
        mLayout = layout;
        mFilter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                List<Contact> filteredList = new ArrayList<>();
                if (constraint != null && !"".equals(constraint)) {

                    for (Contact contact : mContacts) {
                        if (contact.getUserName().contains(constraint)) {
                            filteredList.add(contact);
                        }
                    }
                } else {
                    filteredList = mContacts;

                }
                results.count = filteredList.size();
                results.values = filteredList;
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults filteredList) {
                SearchResultAdapter.this.mFilteredList = (List<Contact>) filteredList.values;
                SearchResultAdapter.this.notifyDataSetChanged();
            }
        };
    }

    @Override
    public Filter getFilter() {
        return mFilter;
    }

    public void setContatctListListener(AdapterListener listListener) {
        this.listListener = listListener;
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
        if (contact.getNickName() != null && !"".equals(contact.getNickName())) {
            holder.mName.setText(contact.getNickName());

        } else if (contact.getUserName() != null && !"".equals(contact.getUserName())) {

            holder.mName.setText(contact.getUserName());

        } else {
            holder.mName.setText(mActivity.getString(R.string.no_name));
        }
        if (contact.getCompany() != null && !"".equals(contact.getCompany())) {
            holder.mCompany.setText(contact.getCompany());

        } else {

            holder.mCompany.setText(mActivity.getString(R.string.no_company));

        }


        Context context = holder.mCircleImageView.getContext();
        Picasso picasso = ServiceManager.getPicassoDowloader(context, mToken);
        picasso.load(ServiceManager.getAvatarById(contact.getProfilePhotoId(), ServiceManager.PIC_SIZE_MID))
                .noFade()
                .placeholder(R.drawable.ic_person_white_48dp)
                .into(holder.mCircleImageView);
        holder.mItemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listListener.onListClick(position, contact);
            }
        });


        if (contact.getProviderIsEnable()) {
            holder.bindPositive(listListener, position, contact);
        } else {
            holder.bindNegative(listListener, position, contact);
        }


    }

    @Override
    public int getItemCount() {
        return mFilteredList.size();
    }


    public interface AdapterListener {
        void onPhoneClick(int pos, Contact item);

        void onNoPhoneClick(int pos, Contact item);

        void onListClick(int pos, Contact item);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mName;
        public CircleImageView mCircleImageView;
        public TextView mCompany;
        public RelativeLayout mItemLayout;
        public TextView mStatus;
        public ImageView mImgCall;
        Drawable rejectIcon = ContextCompat.getDrawable(mActivity
                , R.drawable.ic_phone_locked_grey_600_36dp);
        Drawable phoneIcon = ContextCompat.getDrawable(mActivity
                , R.drawable.ic_call_blue_grey_900_36dp);

        public ViewHolder(View itemView) {
            super(itemView);
            mStatus = (TextView) itemView.findViewById(R.id.contacti_tv_status);
            mName = (TextView) itemView.findViewById(R.id.contacti_tv_name);
            mCircleImageView = (CircleImageView) itemView.findViewById(R.id.contacti_img_avatar);
            mCompany = (TextView) itemView.findViewById(R.id.contacti_tv_company);
            mItemLayout = (RelativeLayout) itemView.findViewById(R.id.contacti_lo_item);
            mImgCall = (ImageView) itemView.findViewById(R.id.contacti_img_call);
        }

        public void bindPositive(final AdapterListener listListener, final int position, final Contact contact) {
            mImgCall.setImageDrawable(phoneIcon);
            mImgCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listListener.onPhoneClick(position, contact);

                }
            });
            switch (contact.getChargeType()) {
                case ChargeTypeConstant.FREE:
                    mStatus.setText(mActivity.getString(R.string.free_charge));
                    break;
                case ChargeTypeConstant.ICON:
                    mStatus.setText(mActivity.getString(R.string.icon_charge));
                    break;
                case ChargeTypeConstant.CHARGE:
                    mStatus.setText(mActivity.getString(R.string.must_charge));
                    break;
                default:
                    mStatus.setText(mActivity.getString(R.string.must_charge));
                    break;
            }
        }

        public void bindNegative(final AdapterListener listListener, final int position, final Contact contact) {
            mImgCall.setImageDrawable(rejectIcon);
            mStatus.setText(mActivity.getString(R.string.forbidden_call));
            mImgCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listListener.onNoPhoneClick(position, contact);
                }
            });

        }

    }

}
