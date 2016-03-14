package tw.kits.voicein.util;

import java.io.IOException;

/**
 * Created by Henry on 2016/3/14.
 */
public class TimeParser implements Comparable<TimeParser>{
    protected int hourOfDay;
    private int minutes;
    public TimeParser(String timeStr) throws IOException {
        String[] timesStrs =  timeStr.split(":");
        if (timesStrs.length!=2){
            throw new IOException("readErr");
        }
        hourOfDay = Integer.parseInt(timesStrs[0]);
        minutes = Integer.parseInt(timesStrs[1]);
    }

    public int getHourOfDay() {
        return hourOfDay;
    }

    public int getMinutes() {
        return minutes;

    }

    @Override
    public int compareTo(TimeParser another) {
        if(this.getHourOfDay() > another.getHourOfDay()){
            return 1;
        }else{
            if(this.getHourOfDay()==another.getHourOfDay()){
                if(this.getMinutes() > another.getMinutes()){
                    return 1;
                }else if (this.getMinutes() < another.getMinutes()){
                    return -1;
                }else {
                    return 0;
                }
            }else{
                return -1;
            }
        }
    }
}
