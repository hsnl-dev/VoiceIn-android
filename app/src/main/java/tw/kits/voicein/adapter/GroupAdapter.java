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
        public void onLongClick(View view, Group group);
    }

    public GroupAdapter(List<Group> list, OnClickListener clickListener) {
        this.groupList = list;
        this.mClickListener = clickListener;

    }
    public void clear(){
        groupList.clear();
    }
    public void addList(List<Group> group){
        groupList.addAll(group);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_group, parent, false);
        ViewHolder holder = new ViewHolder(itemView);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Group group = groupList.get(position);
        holder.setContent(group);
        if(!holder.isListenerSet()){
            holder.setListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    mClickListener.onClick(v,group);
                }
            },new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    mClickListener.onLongClick(v,group);
                    return true;
                }
            });
        }else{
            holder.setListener(null,null);

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
        public View.OnClickListener mClickListener;
        public View.OnLongClickListener mLongClickListener;


        public ViewHolder(View itemView) {
            super(itemView);
            mTitleText = (TextView) itemView.findViewById(R.id.groupi_tv_name);
            mCountText = (TextView) itemView.findViewById(R.id.groupi_tv_count);
            mItem = itemView.findViewById(R.id.groupi_lo_item);
        }
        public boolean isListenerSet(){
            return  mClickListener != null && mLongClickListener != null;
        }
        public void setListener(View.OnClickListener click, View.OnLongClickListener longClick){
            if(click!=null){
                mClickListener = click;
            }
            if(longClick!=null){
                mLongClickListener = longClick;
            }
            mItem.setOnClickListener(mClickListener);
            mItem.setOnLongClickListener(mLongClickListener);

        }

        public void setContent(Group group) {
            mTitleText.setText(group.getGroupName());
            mCountText.setText("共有 "+Integer.toString(group.getContactCount())+" 位聯絡人");

        }
    }

}

