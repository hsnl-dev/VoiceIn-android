package tw.kits.voicein.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tw.kits.voicein.G8penApplication;
import tw.kits.voicein.R;
import tw.kits.voicein.model.Contact;
import tw.kits.voicein.model.SmallMsgForm;
import tw.kits.voicein.util.VoiceInService;

/**
 * Created by Henry on 2016/3/22.
 */
public class SmallChatFragment extends DialogFragment{
    private Contact contact;
    private EditText editText;
    private String uuid;
    private TextView state;
    private static final String ARG_CONTACTS  = "contacts";
    private static final String ARG_UUID  = "uuid";

    public static SmallChatFragment newInstance(Contact contact, String uuid){

        SmallChatFragment fragment = new SmallChatFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(ARG_CONTACTS,contact);
        bundle.putString(ARG_UUID,uuid);
        fragment.setArguments(bundle);
        return fragment;

    }
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.fragment_edit_text,null);
        editText = (EditText) view.findViewById(R.id.edit_frag_et);
        state = (TextView) view.findViewById(R.id.edit_frag_tv_status);
        contact = (Contact) getArguments().get(ARG_CONTACTS);
        uuid = getArguments().getString(ARG_UUID);
        String contactName;
        if(contact.getNickName() == null){
            contactName = contact.getUserName();
        }else if (contact.getNickName().isEmpty()){
            contactName = contact.getUserName();
        }else{
            contactName = contact.getNickName();
        }
        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setTitle("傳訊息給聯絡人")
                .setMessage("傳訊息給"+contactName)
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                    }
                })
                .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SmallMsgForm form = new SmallMsgForm();
                        if(TextUtils.isEmpty(editText.getText())){
                           editText.setError("空白輸入");
                        }
                        form.setContent(editText.getText().toString());
                        VoiceInService apiService = ((G8penApplication)getActivity().getApplication()).getAPIService();
                        apiService.sendSmallMsg(uuid,contact.getId(),form).enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                if(response.isSuccess()){

                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                state.setText(getString(R.string.network_err));
                                state.setVisibility(View.VISIBLE);
                            }
                        });

                    }
                })
                .setView(view)
                .create();
        return dialog;
    }
}
