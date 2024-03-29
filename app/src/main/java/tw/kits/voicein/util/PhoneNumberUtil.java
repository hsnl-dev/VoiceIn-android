package tw.kits.voicein.util;

/**
 * Created by Henry on 2016/3/27.
 */
public class PhoneNumberUtil {
    private static final String TAIWAN_NATION_CODE = "+886";
    public static boolean isValid(String phoneNumber){
        if(phoneNumber.startsWith("09") && phoneNumber.length()==10){
            return true;
        }else if(phoneNumber.startsWith("9") && phoneNumber.length()==9){
            return true;
        }
        return false;
    }
    public static String getStandardNumber(String phoneNumber){
       return TAIWAN_NATION_CODE + phoneNumber.replaceFirst("0", "");
    }
    public static boolean isTaiwan(String standPhonenumber){
        if(standPhonenumber==null)
            return false;
        else
            return standPhonenumber.startsWith("+8869");

    }
    public static String getTwFormat(String standPhonenumber){
        return standPhonenumber.replace("+8869","09");

    }
}
