package tw.kits.voicein.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import tw.kits.voicein.R;
import tw.kits.voicein.model.EventEntity;

/**
 * Created by Henry on 2016/4/23.
 */
public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {

    List<EventEntity> mEventEntities;
    Picasso mImageLoader;
    Context mContext;
    public void addList(List<EventEntity> entities){
        mEventEntities.addAll(entities);
    }
    public void clear(){
        mEventEntities.clear();
    }
    public EventAdapter(List<EventEntity> events, Picasso picasso, Context context) {
        mEventEntities = events;
        mImageLoader = picasso;
        mContext = context;
    }

    @Override
    public EventAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(EventAdapter.ViewHolder holder, int position) {
        holder.setText(mEventEntities.get(position), mContext);
    }

    @Override
    public int getItemCount() {
        return mEventEntities.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView mTimeText;
        TextView mDescText;
        ImageView mImg;

        public ViewHolder(View itemView) {
            super(itemView);
            mTimeText = (TextView) itemView.findViewById(R.id.eventi_tv_time);
            mDescText = (TextView) itemView.findViewById(R.id.eventi_tv_desc);
        }

        public void setText(EventEntity eventEntity, Context context) {
            CharSequence timeSpan = DateUtils.getRelativeDateTimeString(context,
                    eventEntity.getCreatedAt(), DateUtils.SECOND_IN_MILLIS, DateUtils.DAY_IN_MILLIS,
                    DateUtils.FORMAT_ABBREV_ALL);

            mTimeText.setText(timeSpan);
            mDescText.setText(eventEntity.getNotificationContent());
        }
    }
}
