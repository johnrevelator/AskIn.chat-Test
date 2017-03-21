package com.unated.askincht_beta.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Class for help formatting date in needed format.
 * Created by Qhash on 30.10.2014.
 */
public class DateFormater {

    /**
     * Get formatted date for current time
     *
     * @param outFormat format for output string(using standart regex e.t. dd,MM,hh,yy...)
     * @return formatted date
     */
    public static String getFormatedDateNow(String outFormat) {
        if (outFormat != null) {
            String str = null;
            SimpleDateFormat simpleDateFormatOut = new SimpleDateFormat(outFormat);

            Date date = GregorianCalendar.getInstance().getTime();
            str = simpleDateFormatOut.format(date);
            return str;
        }
        return null;
    }

    /**
     * Get formated date in format, which set user
     *
     * @param dateString input string
     * @param inFormat   input string format(using standart regex e.t. dd,MM,hh,yy...)
     * @param outFormat  format for output string(using standart regex e.t. dd,MM,hh,yy...)
     * @return formatted date
     */
    public static String getFormatedDate(String dateString, String inFormat, String outFormat) {
        if (dateString != null) {
            String str = null;
            SimpleDateFormat simpleDateFormatIn = new SimpleDateFormat(inFormat);
            SimpleDateFormat simpleDateFormatOut = new SimpleDateFormat(outFormat);

            try {
                Date date = simpleDateFormatIn.parse(dateString);
                str = simpleDateFormatOut.format(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return str;
        }
        return null;
    }

    /**
     * Get formatted date in format, which set user + TODAY and YESTERDAY
     *
     * @param dateString input string
     * @param inFormat   input string format(using standard regex e.t. dd,MM,hh,yy...)
     * @param outFormat  format for output string(using standard regex e.t. dd,MM,hh,yy...)
     * @return - formatted date
     */
    public static String getFormatedDateWithDays(String dateString, String inFormat, String outFormat, String today, String yesterday) {
        String str = null;
        SimpleDateFormat simpleDateFormatIn = new SimpleDateFormat(inFormat);
        SimpleDateFormat simpleDateFormatOut = new SimpleDateFormat(outFormat);

        try {
            Date date = simpleDateFormatIn.parse(dateString);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            int dayOfYear = calendar.get(Calendar.DAY_OF_YEAR);
            int currentDayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
            if (dayOfYear == currentDayOfYear) {
                str = today;
            } else if (currentDayOfYear - dayOfYear == 1) {
                str = yesterday;
            } else {
                str = simpleDateFormatOut.format(date);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }

    /**
     * Get formated date informat
     *
     * @param calendar  input calendar
     * @param outFormat format for output string(using standart regex e.t. dd,MM,hh,yy...)
     * @return
     */
    public static String getFormatedDate(Calendar calendar, String outFormat) {
        String str = null;

        SimpleDateFormat simpleDateFormatOut = new SimpleDateFormat(outFormat);
        str = simpleDateFormatOut.format(calendar.getTime());

        return str;
    }

    /**
     * Get formated date informat
     *
     * @param timeInMillis input time in millis
     * @param outFormat    format for output string(using standart regex e.t. dd,MM,hh,yy...)
     * @return
     */
    public static String getFormatedDate(Long timeInMillis, String outFormat) {
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTimeInMillis(timeInMillis);

        String str = null;

        SimpleDateFormat simpleDateFormatOut = new SimpleDateFormat(outFormat);
        //simpleDateFormatOut.setTimeZone(TimeZone.getTimeZone("UTC"));
        str = simpleDateFormatOut.format(calendar.getTime());

        return str;
    }
}
