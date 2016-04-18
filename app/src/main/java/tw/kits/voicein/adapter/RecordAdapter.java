package tw.kits.voicein.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.TimedText;
import android.support.v4.content.ContextCompat;
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

    public RecordAdapter(List<Record> records, Picasso imgLoader, Context context){
        this.mRecordList = records;
        this.mImgLoader = imgLoader;
        this.mCurrent = new Date();
        this.mContext = context;
        received = ContextCompat.getDrawable(mContext,R.drawable.ic_call_received_blue_grey_700_18dp);
        made = ContextCompat.getDrawable(mContext,R.drawable.ic_call_made_blue_grey_700_18dp);
        madeMiss = ContextCompat.getDrawable(mContext,R.drawable.ic_call_missed_outgoing_red_500_18dp);
        receivedMiss = ContextCompat.getDrawable(mContext,R.drawable.ic_call_missed_red_500_18dp);
    }
    @Override
    public RecordAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_record,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecordAdapter.ViewHolder holder, int position) {
        Record record = mRecordList.get(position);


        String status;

        if(record.isAnswer()){
            CharSequence timeSpanString = DateUtils. getRelativeDateTimeString (mContext,
                    record.getStartTime(), DateUtils.SECOND_IN_MILLIS, DateUtils.DAY_IN_MILLIS,
                    DateUtils.FORMAT_ABBREV_ALL);
            holder.timeText.setText(timeSpanString);
            if("incoming".equals(record.getType())){
                status = "撥入";
                holder.statusImg.setImageDrawable(received);
            }else{
                status = "撥出";
                holder.statusImg.setImageDrawable(made);
            }
        }else{
            if("incoming".equals(record.getType())){
                status = "未接來電";
                holder.statusImg.setImageDrawable(receivedMiss);
                holder.statusText.setTextColor(Color.RED);
            }else{
                status = "未撥通";
                holder.statusImg.setImageDrawable(madeMiss);
                holder.statusText.setTextColor(Color.RED);
            }
        }
        mImgLoader.load(ServiceConstant.getAvatarById(record.getAnotherAvatarId(),ServiceConstant.PIC_SIZE_MID))
                .placeholder(R.drawable.ic_person_white_36dp)
                .into(holder.avatarImg);
        holder.statusText.setText(status);
        String displayName;
        if(record.getAnotherNickName()==null || TextUtils.isEmpty(record.getAnotherNickName())){
            displayName = record.getAnotherName();
        }else{
            displayName = record.getAnotherNickName();
        }

        holder.nameText.setText(displayName);
    }

    @Override
    public int getItemCount() {
        if(mRecordList!=null)
            return mRecordList.size();
        return 0;
    }
//    public interface OnClickListener{
//        public View.OnClickListener
//    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView nameText;
        TextView statusText;
        ImageView statusImg;
        ImageView avatarImg;
        TextView timeText;

        public ViewHolder(View itemView) {
            super(itemView);
            nameText = (TextView)itemView.findViewById(R.id.recordi_tv_name);
            statusText = (TextView)itemView.findViewById(R.id.recordi_tv_status);
            timeText = (TextView)itemView.findViewById(R.id.recordi_tv_time);
            statusImg = (ImageView) itemView.findViewById(R.id.recordi_img_status);
            avatarImg = (ImageView) itemView.findViewById(R.id.recordi_img_avatar);
        }
    }
}
