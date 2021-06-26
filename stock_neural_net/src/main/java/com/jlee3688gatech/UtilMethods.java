package com.jlee3688gatech;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class UtilMethods {

    public static Calendar CalendarMaker(String date) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, Integer.parseInt(date.substring(0, 4)));
        cal.set(Calendar.MONTH, Integer.parseInt(date.substring(4, 6)) - 1);
        cal.set(Calendar.DATE, Integer.parseInt(date.substring(6, 8)));
        //cal.set(Calendar.HOUR, Integer.parseInt(date.substring(8, 10)));
        //cal.set(Calendar.MINUTE, Integer.parseInt(date.substring(10, 12)));
        //cal.set(Calendar.SECOND, Integer.parseInt(date.substring(12, 14)));

        return cal;
    }

    public static String CalendarToString(Calendar cal) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        return formatter.format(cal.getTime());
    }

    /**
     * The Directory separator ("\", "/") depends on which OS user use.
     * this method will check user's OS and set Directory separator (slash variable)
     */
    public static String getOSSlash() {
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("win")) {
            return "\\";
        } else {
            return "/";
        }
    }
    
}
