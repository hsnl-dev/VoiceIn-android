package tw.kits.voicein.adapter;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tw.kits.voicein.R;
import tw.kits.voicein.fragment.ProgressFragment;
import tw.kits.voicein.model.QRcode;
import tw.kits.voicein.util.ColoredSnackBar;
import tw.kits.voicein.util.ServiceManager;
import tw.kits.voicein.util.UserAccessStore;

/**
 * Created by Henry on 2016/3/2.
 */
public class QrcodeAdapter extends RecyclerView.Adapter<QrcodeAdapter.ViewHolder> {
    List<QRcode> mQrcodes;
    ActionMode mActionMode;
    AppCompatActivity mActivity;
    DialogFragment fragment;
    View mLayout;
    ShareTarget mShareTarget;
    private static final String TAG = QrcodeAdapter.class.getName();

    public QrcodeAdapter(List<QRcode> contacts, AppCompatActivity activity, View layout) {
        mQrcodes = contacts;
        mActivity= activity;
        mLayout = layout;
        mShareTarget = new ShareTarget();
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
        holder.mName.setText(code.getUserName().equals("") ? mActivity.getString(R.string.unknown_name) : code.getUserName());
        holder.mCompany.setText(code.getCompany().equals("") ? mActivity.getString(R.string.unknown_com) : code.getCompany());
        ServiceManager
                .getPicassoDowloader(mActivity,UserAccessStore.getToken())
                .load(ServiceManager.getQRcodeById(code.getId()))
                .into(holder.mImageView);
        holder.mItemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    ServiceManager
                            .getPicassoDowloader(mActivity, UserAccessStore.getToken())
                            .load(ServiceManager.getQRcodeById(code.getId()))
                            .into(mShareTarget);


            }
        });
        holder.mItemLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                new AlertDialog.Builder(mActivity)
                        .setTitle("刪除?")
                        .setMessage("確認刪除嗎，該用戶將無法再使用該QRcode")
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();

                            }
                        })
                        .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                fragment = new ProgressFragment();
                                fragment.show(mActivity.getSupportFragmentManager(),"loading");
                                delCode(code.getId(), position);
                            }
                        }).show();
                return true;
            }
        });
    }
    private void delCode(String qrUuid, final int pos){
        ServiceManager
                .createService(UserAccessStore.getToken())
                .delCustomQrcodes(UserAccessStore.getUserUuid(),qrUuid)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                        if(response.isSuccess()) {
                            ColoredSnackBar.primary(
                                    Snackbar.make(mLayout, mActivity.getString(R.string.success), Snackbar.LENGTH_LONG))
                                    .show();
                            try {
                                mQrcodes.remove(pos);
                                notifyItemRemoved(pos);
                            }catch (IndexOutOfBoundsException e){
                                Log.e("Error",e.toString());
                            }

                            fragment.dismiss();
                        }else{
                            fragment.dismiss();
                            ColoredSnackBar.primary(
                                    Snackbar.make(mLayout, mActivity.getString(R.string.server_err), Snackbar.LENGTH_LONG))
                                    .show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        fragment.dismiss();
                        ColoredSnackBar.primary(
                                Snackbar.make(mLayout, mActivity.getString(R.string.network_err), Snackbar.LENGTH_LONG))
                                .show();
                    }
                });
    }
    private class ShareTarget implements Target{
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            ShareTask task = new ShareTask();
            Bitmap[] bitmaps = {bitmap};
            task.execute(bitmaps);
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
        }
    }
    private class ShareTask extends AsyncTask<Bitmap,Void,Void>{
        @Override
        protected Void doInBackground(Bitmap... params) {
            Intent i = null;
            if(params[0] != null){
                File imageFileFolder = new File(mActivity.getExternalCacheDir(), "Qrcodes");
                if (!imageFileFolder.exists()) {
                    imageFileFolder.mkdir();
                }
                File file = new File(imageFileFolder, "Qrcodes-" + System.currentTimeMillis() + ".jpg");
                FileOutputStream out = null;
                try {
                    out = new FileOutputStream(file);
                    params[0].compress(Bitmap.CompressFormat.JPEG, 80, out);
                    i = new Intent();
                    i.setAction(Intent.ACTION_SEND);
                    i.setType("image/jpeg");
                    i.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } finally {
                    if (out != null)
                        try {
                            out.close();
                        } catch (IOException e) {

                        }
                }
                mActivity.startActivity(i);
            }

            return null;
        }
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
