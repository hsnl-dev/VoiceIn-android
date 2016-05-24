package tw.kits.voicein.util;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.view.View;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tw.kits.voicein.R;
import tw.kits.voicein.fragment.ProgressCallFragment;

/**
 * Created by Henry on 2016/5/25.
 */
public class InitCallCallBackImpl implements Callback<ResponseBody> {
    ProgressCallFragment mDialog;
    Context mContext;
    View mLayout;
    public InitCallCallBackImpl(ProgressCallFragment dialog, Context context, View mainLayout){
        this.mDialog = dialog;
        this.mContext = context;
        this.mLayout = mainLayout;
    }
    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

        if(response.isSuccess()){
            mDialog.setupTimer();
        }else{
            mDialog.dismiss();
            switch (response.code()){
                case 403:
                    ColoredSnackBarUtil.primary(
                            Snackbar.make(mLayout, mContext.getString(R.string.forbidden_call_hint), Snackbar.LENGTH_SHORT)
                    ).show();
                    break;
                case 401:
                    ColoredSnackBarUtil.primary(
                            Snackbar.make(mLayout,  mContext.getString(R.string.user_not_auth), Snackbar.LENGTH_SHORT)
                    ).show();
                    break;
                default:
                    ColoredSnackBarUtil.primary(
                            Snackbar.make(mLayout,  mContext.getString(R.string.server_err), Snackbar.LENGTH_SHORT)
                    ).show();

            }
        }
    }

    @Override
    public void onFailure(Call<ResponseBody> call, Throwable t) {
        mDialog.dismiss();
        ColoredSnackBarUtil.primary(
                Snackbar.make(mLayout, mContext.getString(R.string.network_err), Snackbar.LENGTH_SHORT)
        ).show();
    }
}
