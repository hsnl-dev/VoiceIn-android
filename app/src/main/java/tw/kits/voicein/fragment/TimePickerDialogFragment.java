package tw.kits.voicein.fragment;


import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.widget.TextView;
import android.widget.TimePicker;


/**
 * Created by Henry on 2016/3/14.
 */
public class TimePickerDialogFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {
    public static String RETURN_HOUR = "return_hour";
    public static String RETURN_MIN = "return_min";
    public static String ARG_HOUR = "arg_hour";
    public static String ARG_MIN = "arg_min";
    private Handler handler;

    public static TimePickerDialogFragment createInstance(Handler handler, TextView textView) {
        TimePickerDialogFragment fragment = new TimePickerDialogFragment();
        Bundle data = new Bundle();
        String[] hourMin = textView.getText().toString().split(":");
        if (hourMin.length == 2) {
            data.putInt(ARG_HOUR, Integer.parseInt(hourMin[0]));
            data.putInt(ARG_MIN, Integer.parseInt(hourMin[1]));

            fragment.setArguments(data);
        }
        fragment.handler = handler;
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle args = getArguments();
        int min = args.getInt(ARG_MIN, 0);
        int hour = args.getInt(ARG_HOUR, 0);
        return new TimePickerDialog(getActivity(), this, hour, min, true);
    }


    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        Bundle bundle = new Bundle();
        bundle.putInt(RETURN_HOUR, hourOfDay);
        bundle.putInt(RETURN_MIN, minute);
        Message message = new Message();
        message.setData(bundle);
        handler.sendMessage(message);
    }
}
