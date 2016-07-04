package tw.kits.voicein.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.List;

import tw.kits.voicein.R;
import tw.kits.voicein.model.Contact;
import tw.kits.voicein.model.Group;
import tw.kits.voicein.model.Record;
import tw.kits.voicein.util.ServiceConstant;

/**
 * Created by Henry on 2016/4/18.
 */
public class RecordAdapter extends RecyclerView.Adapter<RecordAdapter.ViewHolder> {
    List<Record> mRecordList;
    Picasso mImgLoader;
    Date mCurrent;
    Context mContext;
    Drawable received;
    Drawable made;
    Drawable madeMiss;
    Drawable receivedMiss;
    AdapterListener adapterListener;
    public interface AdapterListener {
        public void onPhoneClick(int pos, Record item);

        public void onListClick(int pos, Record item);
    }

    public void clear() {
        mRecordList.clear();
    }

    public void addList(List<Record> group) {
        mRecordList.addAll(group);
    }

    public RecordAdapter(List<Record> records, Picasso imgLoader, Context context) {
        this.mRecordList = records;
        this.mImgLoader = imgLoader;
        this.mCurrent = new Date();
        this.mContext = context;
        received = ContextCompat.getDrawable(mContext, R.drawable.ic_call_received_blue_grey_700_18dp);
        made = ContextCompat.getDrawable(mContext, R.drawable.ic_call_made_blue_grey_700_18dp);
        madeMiss = ContextCompat.getDrawable(mContext, R.drawable.ic_call_missed_outgoing_red_500_18dp);
        receivedMiss = ContextCompat.getDrawable(mContext, R.drawable.ic_call_missed_red_500_18dp);
    }
    public void setOnAdaterListener(AdapterListener listener){
        this.adapterListener = listener;
    }

    @Override
    public RecordAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_record, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecordAdapter.ViewHolder holder, int position) {
        Record record = mRecordList.get(position);


        String status;
        CharSequence timeSpanString = DateUtils.getRelativeDateTimeString(mContext,
                record.getReqTime(), DateUtils.SECOND_IN_MILLIS, DateUtils.DAY_IN_MILLIS,
                DateUtils.FORMAT_ABBREV_ALL);
        holder.timeText.setText(timeSpanString);
        if (record.isAnswer()) {


            if ("incoming".equals(record.getType())) {
                status = "撥入";
                holder.statusImg.setImageDrawable(received);
                holder.statusText.setTextColor(ContextCompat.getColor(mContext,R.color.secondary_text));
            } else {
                status = "撥出";
                holder.statusImg.setImageDrawable(made);
                holder.statusText.setTextColor(ContextCompat.getColor(mContext,R.color.secondary_text));
            }
        } else {
            if ("incoming".equals(record.getType())) {
                status = "未接來電";
                holder.statusImg.setImageDrawable(receivedMiss);
                holder.statusText.setTextColor(Color.RED);
            } else {
                status = "未撥通";
                holder.statusImg.setImageDrawable(madeMiss);
                holder.statusText.setTextColor(Color.RED);
            }
        }
        if (record.getAnotherAvatarId() != null) {
            mImgLoader.load(ServiceConstant.getAvatarById(record.getAnotherAvatarId(), ServiceConstant.PIC_SIZE_MID))
                    .placeholder(R.drawable.ic_person_white_36dp)
                    .into(holder.avatarImg);
        }else{
            holder.avatarImg.setImageDrawable(ContextCompat.getDrawable(mContext,R.drawable.ic_person_white_36dp));

        }
        holder.statusText.setText(status);
        String displayName;
        if (record.getAnotherNickName() != null && !TextUtils.isEmpty(record.getAnotherNickName())) {
            displayName = record.getAnotherNickName();
        } else if (record.getAnotherName() != null && !TextUtils.isEmpty(record.getAnotherName())) {
            displayName = record.getAnotherName();
        } else {
            displayName = record.getAnotherNum();
        }

        holder.nameText.setText(displayName);
        holder.bindListener(adapterListener,position,record);
    }

    @Override
    public int getItemCount() {
        if (mRecordList != null)
            return mRecordList.size();
        return 0;
    }


    //    public interface OnClickListener{
//        public View.OnClickListener
//    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameText;
        TextView statusText;
        ImageView statusImg;
        ImageView avatarImg;
        TextView timeText;
        ImageView callImg;

        public void bindListener(final AdapterListener listener, final int pos, final Record record){
            callImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onPhoneClick(pos,record);
                }
            });

        }
        public ViewHolder(View itemView) {
            super(itemView);
            nameText = (TextView) itemView.findViewById(R.id.recordi_tv_name);
            statusText = (TextView) itemView.findViewById(R.id.recordi_tv_status);
            timeText = (TextView) itemView.findViewById(R.id.recordi_tv_time);
            statusImg = (ImageView) itemView.findViewById(R.id.recordi_img_status);
            avatarImg = (ImageView) itemView.findViewById(R.id.recordi_img_avatar);
            callImg = (ImageView) itemView.findViewById(R.id.recordi_img_call);
        }
    }
}
