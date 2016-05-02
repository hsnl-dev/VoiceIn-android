package tw.kits.voicein.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tw.kits.voicein.G8penApplication;
import tw.kits.voicein.R;
import tw.kits.voicein.adapter.QrcodeAdapter;
import tw.kits.voicein.fragment.ProgressFragment;
import tw.kits.voicein.model.QRcode;
import tw.kits.voicein.model.QRcodeContainer;
import tw.kits.voicein.util.ColoredSnackBarUtil;
import tw.kits.voicein.util.VoiceInService;

public class QRCodeActivity extends AppCompatActivity implements View.OnClickListener {
    RecyclerView mList;
    View mMainView;
    QrcodeAdapter mAdapter;
    VoiceInService mService;
    SwipeRefreshLayout mRefresh;
    ProgressFragment mfragment;
    FloatingActionButton mFab;
    String mToken;
    String mUserUuid;
    QrcodeAdapter.AdapterListener mAdapterListener;
    private final static int INTENT_ADD = 1;
    private static String TAG = QRCodeActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);
        mList = (RecyclerView) findViewById(R.id.qrcode_rv_main);
        mMainView = findViewById(R.id.qrcode_lo_main);
        mList.setLayoutManager(new LinearLayoutManager(this));
        mService = ((G8penApplication) getApplication()).getAPIService();
        mToken = ((G8penApplication) getApplication()).getToken();
        mUserUuid = ((G8penApplication) getApplication()).getUserUuid();
        refreshList();
        mRefresh = (SwipeRefreshLayout) findViewById(R.id.qrcode_rv_container);
        mFab = (FloatingActionButton) findViewById(R.id.qrcode_fab_plus);
        mfragment = new ProgressFragment();
        mRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshList();
            }
        });
        mAdapterListener = new QrcodeAdapter.AdapterListener() {
            @Override
            public void onLongClick(final int pos, final QRcode item) {
                new AlertDialog.Builder(QRCodeActivity.this)
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
                                mfragment = new ProgressFragment();
                                mfragment.show(getSupportFragmentManager(), "loading");
                                delCode(item.getId(), pos);
                            }
                        }).show();
            }

            @Override
            public void onShareClick(int pos, QRcode item, Bitmap bitmap) {
                ShareTask task = new ShareTask();
                Bitmap[] bitmaps = {bitmap};
                task.execute(bitmaps);
            }
        };

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mFab.setOnClickListener(this);

    }

    private void delCode(String qrUuid, final int pos) {
        mService.delCustomQrcodes(mUserUuid, qrUuid)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                        if (response.isSuccess()) {
                            ColoredSnackBarUtil.primary(
                                    Snackbar.make(mMainView, getString(R.string.success), Snackbar.LENGTH_LONG))
                                    .show();
                            mAdapter.removeItem(pos);

                            mfragment.dismiss();
                        } else {
                            mfragment.dismiss();
                            ColoredSnackBarUtil.primary(
                                    Snackbar.make(mMainView, getString(R.string.server_err), Snackbar.LENGTH_LONG))
                                    .show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        mfragment.dismiss();
                        ColoredSnackBarUtil.primary(
                                Snackbar.make(mMainView, getString(R.string.network_err), Snackbar.LENGTH_LONG))
                                .show();
                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.qrcode_fab_plus:
                Intent add = new Intent(QRCodeActivity.this, QrcodeCreateActivity.class);
                startActivityForResult(add, INTENT_ADD);
        }
    }

    private void refreshList() {

        mService.getCustomQrcodes(mUserUuid)
                .enqueue(new ApiCallBack());


    }


    private class ShareTask extends AsyncTask<Bitmap, Void, Void> {
        @Override
        protected Void doInBackground(Bitmap... params) {
            Intent i = null;
            if (params[0] != null) {
                File imageFileFolder = new File(getExternalCacheDir(), "Qrcodes");
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
                startActivity(i);
            }

            return null;
        }
    }

    private class ApiCallBack implements Callback<QRcodeContainer> {

        @Override
        public void onResponse(Call<QRcodeContainer> call, Response<QRcodeContainer> response) {
            mRefresh.setRefreshing(false);
            if (response.isSuccess()) {
                mAdapter = new QrcodeAdapter(response.body().getQrcodes(), QRCodeActivity.this, mMainView);
                mAdapter.setAdapterListener(mAdapterListener);
                mList.setAdapter(mAdapter);
                Log.e(TAG, response.body().getQrcodes().size() + "" + mAdapter.getItemCount());
                mAdapter.notifyDataSetChanged();

            }
        }

        @Override
        public void onFailure(Call<QRcodeContainer> call, Throwable t) {
            mRefresh.setRefreshing(false);
            Log.e(TAG, t.toString());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Log.e(TAG, "OK");
            switch (requestCode) {
                case INTENT_ADD:
                    ColoredSnackBarUtil.primary(Snackbar.make(mMainView, getString(R.string.success), Snackbar.LENGTH_SHORT)).show();
                    refreshList();
                    break;

            }

        }
    }
}
