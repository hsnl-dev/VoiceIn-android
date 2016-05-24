package tw.kits.voicein.fragment;




import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import tw.kits.voicein.R;


public class ProgressCallFragment extends DialogFragment {
    private final int WAIT_MILLS=10000;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(false);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setTitle(getString(R.string.wait));
        dialog.setMessage(getString(R.string.wait_notice));
        dialog.setIndeterminate(true);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        return dialog;
    }
    public void setupTimer(){
        ((ProgressDialog)getDialog()).setMessage(getString(R.string.wait_call_notice));
        new TimerAsync().execute(WAIT_MILLS);
    }
    private class TimerAsync extends AsyncTask<Integer, Void, Void> {


        @Override
        protected void onPostExecute(Void aVoid) {
            dismiss();
            super.onPostExecute(aVoid);
        }

        @Override
        protected Void doInBackground(Integer[] params) {
            try {
                Thread.sleep(params[0]);
            } catch (InterruptedException e) {

            }
            return null;
        }
    }

}
