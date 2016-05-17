package tw.kits.voicein.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.Window;

import java.io.Serializable;

import tw.kits.voicein.model.Group;

/**
 * Created by Henry on 2016/3/22.
 */
public class PickerDialogFragment extends DialogFragment{
    private OnSelectListener mActions;
    private String[] options;


    public static PickerDialogFragment newInstance(PickerDialogFragment.OnSelectListener actions, String[] options){
        PickerDialogFragment fragment = new PickerDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putStringArray("options",options);
        bundle.putSerializable("actions",actions);
        fragment.setArguments(bundle);
        return fragment;

    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        options = getArguments().getStringArray("options");
        mActions = (OnSelectListener) getArguments().getSerializable("actions");
        AlertDialog dialog = new AlertDialog.Builder(getContext()).setItems(options,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mActions.onClick(dialog,which);
                    }
                }).create();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }
    public static class OnSelectListener implements Serializable{
        public void onClick(DialogInterface dialog, int i){
            return;
        }
    }

}
