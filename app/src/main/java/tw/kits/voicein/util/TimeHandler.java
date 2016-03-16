package tw.kits.voicein.util;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

import tw.kits.voicein.fragment.TimePickerDialogFragment;

/**
 * Created by Henry on 2016/3/15.
 */
public class TimeHandler extends Handler {
    TextView textView;
    public TimeHandler(TextView textView){
        super();
        this.textView = textView;

    }
    public TextView getTextView(){
        return textView;
    }
    @Override
    public void handleMessage (Message msg){
        Bundle bundle = msg.getData();
        int timeHour = bundle.getInt(TimePickerDialogFragment.RETURN_HOUR);
        int timeMinute = bundle.getInt(TimePickerDialogFragment.RETURN_MIN);
        textView.setText(String.format("%02d:%02d",timeHour,timeMinute));
    }
}