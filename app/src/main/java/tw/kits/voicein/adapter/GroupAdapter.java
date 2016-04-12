package tw.kits.voicein.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import tw.kits.voicein.R;
import tw.kits.voicein.model.Group;

/**
 * Created by Henry on 2016/4/11.
 */
public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.ViewHolder> {
    private List<Group> groupList;
    private OnClickListener mClickListener;

    public interface OnClickListener {
        public void onClick(View view, Group group);
    }

    public GroupAdapter(List<Group> list, OnClickListener clickListener) {
        this.groupList = list;
        this.mClickListener = clickListener;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_qrcode, parent, false);
        ViewHolder holder = new ViewHolder(itemView);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Group group = groupList.get(position);
        holder.setContent(group);
        if (!holder.isListenerSet()) {
            holder.setItemListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mClickListener.onClick(v, group);
                }
            });

        }

    }

    @Override
    public int getItemCount() {
        return groupList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mTitleText;
        public TextView mCountText;
        public View mItem;
        public View.OnClickListener onClickListener;

        public ViewHolder(View itemView) {
            super(itemView);
            mTitleText = (TextView) itemView.findViewById(R.id.groupi_tv_name);
            mCountText = (TextView) itemView.findViewById(R.id.groupi_tv_count);
            mItem = itemView.findViewById(R.id.groupi_lo_item);
        }

        public boolean isListenerSet() {
            return onClickListener != null;
        }

        public void setItemListener(View.OnClickListener listener) {
            onClickListener = listener;
            mItem.setOnClickListener(onClickListener);
        }

        public void setContent(Group group) {
            mTitleText.setText(group.getName());
            mCountText.setText(group.getContacts().size());

        }
    }

}

