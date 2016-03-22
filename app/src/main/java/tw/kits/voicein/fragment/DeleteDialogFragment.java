package tw.kits.voicein.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

/**
 * Created by Henry on 2016/3/22.
 */
public class DeleteDialogFragment extends DialogFragment{
    private DialogInterface.OnClickListener mConfirm;


    public static DeleteDialogFragment newInstance(DialogInterface.OnClickListener comfirmListener){

        DeleteDialogFragment fragment = new DeleteDialogFragment();
        fragment.mConfirm = comfirmListener;
        return fragment;

    }
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setTitle("刪除?")
                .setMessage("確認刪除嗎，該用戶將無法在撥打電話給您")
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                    }
                })
                .setPositiveButton("確定", mConfirm).create();
        return dialog;
    }
}
