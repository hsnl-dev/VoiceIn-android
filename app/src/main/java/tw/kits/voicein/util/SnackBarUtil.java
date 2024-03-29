package tw.kits.voicein.util;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.view.View;

import java.util.HashMap;

import tw.kits.voicein.R;

/**
 * Created by Henry on 2016/4/12.
 */
public class SnackBarUtil {
    public final static int NETWORK_ERR = 1000;
    public final static int INTERNAL_ERR = 500;
    View attachView;
    HashMap<Integer, String> hashMap;
    public SnackBarUtil(View view, Context context){
        attachView = view;
        hashMap = new HashMap<>();
        hashMap.put(401,context.getString(R.string.user_auth_err));
        hashMap.put(INTERNAL_ERR,context.getString(R.string.server_err));
        hashMap.put(NETWORK_ERR,context.getString(R.string.network_err));
        hashMap.put(200,context.getString(R.string.success));
    }
    public void setErrorMessage(int status, String string){
        hashMap.put(status,string);
    }
    public Snackbar showSnackBar(int statusCode){

        String text = hashMap.get(statusCode)!=null? hashMap.get(statusCode):hashMap.get(INTERNAL_ERR);
        Snackbar bar =  ColoredSnackBarUtil.primary(Snackbar.make(attachView,text,Snackbar.LENGTH_LONG));
        bar.show();
        return bar;

    }
}
