package com.jlee3688gatech;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class UtilMethods {

    public static String slash;
    public static String version = "1.0.0";
    public static int doubleClickSpeed = 300;

    public static void initialize() {
        setOSSlash();
    }

    public static Calendar CalendarMaker(String date){
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, Integer.parseInt(date.substring(0, 4)));
        cal.set(Calendar.MONTH, Integer.parseInt(date.substring(4, 6)) - 1);
        cal.set(Calendar.DATE, Integer.parseInt(date.substring(6, 8)));
        //cal.set(Calendar.HOUR, 23);
        //cal.set(Calendar.MINUTE, 59);
        //cal.set(Calendar.SECOND, 59);

        return cal;
    }

    public static String CalendarToTimeString(Calendar cal) {
        SimpleDateFormat formatter = new SimpleDateFormat("HH-mm-ss");
        return formatter.format(cal.getTime());
    }

    public static String CalendarToString(Calendar cal) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        return formatter.format(cal.getTime());
    }

    public static String calendarToSimpleString(Calendar cal) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        return formatter.format(cal.getTime());
    }

    /**
     * The Directory separator ("\", "/") depends on which OS user use.
     * this method will check user's OS and set Directory separator (slash variable)
     */
    public static void setOSSlash() {
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("win")) {
            slash = "\\";
        } else {
            slash = "/";
        }
    }
    
}
