package tw.kits.voicein.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.Window;

/**
 * Created by Henry on 2016/3/22.
 */
public class PickerDialogFragment extends DialogFragment{
    private DialogInterface.OnClickListener mActions;
    private String[] options;


    public static PickerDialogFragment newInstance(DialogInterface.OnClickListener actions, String[] options){
        PickerDialogFragment fragment = new PickerDialogFragment();
        fragment.mActions = actions;
        Bundle bundle = new Bundle();
        bundle.putStringArray("options",options);
        fragment.setArguments(bundle);
        return fragment;

    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        options = getArguments().getStringArray("options");
        AlertDialog dialog = new AlertDialog.Builder(getContext()).setItems(options,mActions).create();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }
}
