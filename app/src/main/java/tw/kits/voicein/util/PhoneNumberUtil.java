package tw.kits.voicein.util;

/**
 * Created by Henry on 2016/3/27.
 */
public class PhoneNumberUtil {
    private static final String TAIWAN_NATION_CODE = "+886";
    public static boolean isValid(String phoneNumber){
        if(phoneNumber.startsWith("0") && phoneNumber.length()==10){
            return true;
        }else if(!phoneNumber.startsWith("0") && phoneNumber.length()==9){
            return true;
        }
        return false;
    }
    public static String getStandardNumber(String phoneNumber){
       return TAIWAN_NATION_CODE + phoneNumber.replaceFirst("0", "");
    }
}
