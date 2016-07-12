package tw.kits.voicein.adapter;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.List;

import tw.kits.voicein.G8penApplication;
import tw.kits.voicein.R;
import tw.kits.voicein.model.QRcode;
import tw.kits.voicein.util.ServiceConstant;

/**
 * Created by Henry on 2016/3/2.
 */
public class QrcodeAdapter extends RecyclerView.Adapter<QrcodeAdapter.ViewHolder> {
    List<QRcode> mQrcodes;
    AppCompatActivity mActivity;
    String mToken;
    View mLayout;
    AdapterListener mAdapterListener;
    Picasso mImgLoader;
    private static final String TAG = QrcodeAdapter.class.getName();

    public QrcodeAdapter(List<QRcode> contacts, AppCompatActivity activity, View layout) {
        mQrcodes = contacts;
        mActivity = activity;
        mLayout = layout;
        mToken = ((G8penApplication) activity.getApplication()).getToken();
        mImgLoader = ((G8penApplication) activity.getApplication()).getImgLoader(activity);
    }

    public interface AdapterListener {
        public void onLongClick(int pos, QRcode item);

        public void onShareClick(int pos, QRcode item, Bitmap img);
    }

    public void removeItem(int pos) {
        mQrcodes.remove(pos);
        notifyItemRemoved(pos);
    }

    public void setAdapterListener(AdapterListener listener) {
        this.mAdapterListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View contactView = LayoutInflater.from(context).inflate(R.layout.item_qrcode, parent, false);
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final QRcode code = mQrcodes.get(position);
        holder.mPhone.setText(code.getPhoneNumber());
        holder.mName.setText(code.getUserName().equals("") ? mActivity.getString(R.string.unknown_name) : code.getUserName());
        holder.mCompany.setText(code.getCompany().equals("") ? mActivity.getString(R.string.unknown_com) : code.getCompany());
        mImgLoader
                .load(ServiceConstant.getQRcodeById(code.getId()))
                .into(holder.mImageView);
        holder.mItemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mImgLoader
                        .load(ServiceConstant.getQRcodeById(code.getId()))
                        .into(new Target() {
                            @Override
                            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                mAdapterListener.onShareClick(position, code, bitmap);
                            }

                            @Override
                            public void onBitmapFailed(Drawable errorDrawable) {

                            }

                            @Override
                            public void onPrepareLoad(Drawable placeHolderDrawable) {

                            }
                        });
            }
        });
        holder.mItemLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mAdapterListener.onLongClick(position, code);
                return true;
            }
        });
    }


    @Override
    public int getItemCount() {
        return mQrcodes.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mPhone;
        public ImageView mImageView;
        public TextView mName;
        public TextView mCompany;
        public RelativeLayout mItemLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            mImageView = (ImageView) itemView.findViewById(R.id.qrcodei_img_avatar);
            mPhone = (TextView) itemView.findViewById(R.id.qrcodei_tv_phone);
            mName = (TextView) itemView.findViewById(R.id.qrcodei_tv_name);
            mCompany = (TextView) itemView.findViewById(R.id.qrcodei_tv_company);
            mItemLayout = (RelativeLayout) itemView.findViewById(R.id.qrcodei_lo_item);

        }

    }

}
