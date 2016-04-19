package learn.android.kangel.mycontacts.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Kangel on 2016/3/19.
 */
public class DateParseUtil {
    public static String getTimeString(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        Calendar today = Calendar.getInstance();
        Calendar yesterday = Calendar.getInstance();
        yesterday.add(Calendar.DATE, -1);
        DateFormat dateFormat1 = new SimpleDateFormat("MM月dd日 HH:mm");
        DateFormat dateFormat2 = new SimpleDateFormat("HH:mm");
        if (calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) && calendar.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)) {
            return dateFormat2.format(date);
        }
        if (calendar.get(Calendar.YEAR) == yesterday.get(Calendar.YEAR) && calendar.get(Calendar.DAY_OF_YEAR) == yesterday.get(Calendar.DAY_OF_YEAR)) {
            return dateFormat2.format(date);
        }
        return dateFormat1.format(date);
    }

    public static String getTimeString(Long date) {
        Date datetime = new Date(date);
        return getTimeString(datetime);
    }

    public static boolean isTodayDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        Calendar today = Calendar.getInstance();
        return calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) && calendar.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR);
    }

    public static boolean isTodayDate(long date) {
        Date datetime = new Date(date);
        return isTodayDate(datetime);
    }

    public static boolean isYesterdayDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        Calendar yesterday = Calendar.getInstance();
        yesterday.add(Calendar.DATE, -1);
        return calendar.get(Calendar.YEAR) == yesterday.get(Calendar.YEAR) && calendar.get(Calendar.DAY_OF_YEAR) == yesterday.get(Calendar.DAY_OF_YEAR);
    }

    public static boolean isYesterdayDate(long date) {
        Date datetime = new Date(date);
        return isYesterdayDate(datetime);
    }

    public static boolean isSameDay(Date date1, Date date2) {
        Calendar datetimeA = Calendar.getInstance();
        Calendar datetimeB = Calendar.getInstance();
        datetimeA.setTime(date1);
        datetimeB.setTime(date2);
        return (datetimeA.get(Calendar.YEAR) == datetimeB.get(Calendar.YEAR) && datetimeA.get(Calendar.DAY_OF_YEAR) == datetimeB.get(Calendar.DAY_OF_YEAR));
    }

    public static boolean isSameDay(Long date1, Long date2) {
        Date mDate1 = new Date(date1);
        Date mDate2 = new Date(date2);
        return isSameDay(mDate1, mDate2);
    }

    public static String getDateString(Date date) {
        if (isTodayDate(date)) {
            return "今天";
        }
        if (isYesterdayDate(date)) {
            return "昨天";
        }
        return "更早";
    }

    public static String getDateString(Long date) {
        if (isTodayDate(date)) {
            return "今天";
        }
        if (isYesterdayDate(date)) {
            return "昨天";
        }
        return "更早";
    }
}
