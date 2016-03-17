package tw.kits.voicein.adapter;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tw.kits.voicein.R;
import tw.kits.voicein.activity.ContactEditActivity;
import tw.kits.voicein.fragment.ContactFragment;
import tw.kits.voicein.model.CallForm;
import tw.kits.voicein.model.QRcode;
import tw.kits.voicein.util.ChargeTypeConstant;
import tw.kits.voicein.util.ColoredSnackBar;
import tw.kits.voicein.util.ServiceManager;
import tw.kits.voicein.util.UserAccessStore;
import tw.kits.voicein.util.VoiceInService;

/**
 * Created by Henry on 2016/3/2.
 */
public class QrcodeAdapter extends RecyclerView.Adapter<QrcodeAdapter.ViewHolder> {
    List<QRcode> mQrcodes;
    Activity mActivity;
    ProgressDialog progressDialog;
    View mLayout;
    private static final String TAG = QrcodeAdapter.class.getName();

    public QrcodeAdapter(List<QRcode> contacts, Activity activity, View layout) {
        mQrcodes = contacts;
        mActivity= activity;
        mLayout = layout;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View contactView = LayoutInflater.from(context).inflate(R.layout.item_qrcode, parent, false);
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final QRcode code = mQrcodes.get(position);
        holder.mPhone.setText(code.getPhoneNumber());
        holder.mNameCompany.setText(code.getUserName() + "," + code.getCompany());
        holder.mItemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });
    }

    @Override
    public int getItemCount() {
        return mQrcodes.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mPhone;
        public CircleImageView mCircleImageView;
        public TextView mNameCompany;
        public RelativeLayout mItemLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            mCircleImageView = (CircleImageView) itemView.findViewById(R.id.qrcodei_img_avatar);
            mPhone = (TextView) itemView.findViewById(R.id.qrcodei_tv_phone);
            mNameCompany = (TextView) itemView.findViewById(R.id.qrcodei_tv_name_company);
            mItemLayout = (RelativeLayout) itemView.findViewById(R.id.qrcodei_lo_item);

        }

    }

}
