package com.expensetracker.expensetracker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Krush on 14-Dec-15.
 */
public class DateUtili {
    private long longDate;
    private int intMonth;
    private String stringDate;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
    private Calendar calendar = Calendar.getInstance();

    public long StringtoLong(String date) {
        try {
            Date d = simpleDateFormat.parse(date);
            longDate = d.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return longDate;
    }

    public int StringtoMonth(String date) {
        try {
            Date d = simpleDateFormat.parse(date);
            intMonth = d.getMonth();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return intMonth;
    }

    public String LongtoString(long date) {
        Date d = new Date(date);
        stringDate = simpleDateFormat.format(d);
        return stringDate;
    }

    public int getDay() {
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    public int getMonth() {
        return calendar.get(Calendar.MONTH) + 1;
    }

    public int getYear() {
        return calendar.get(Calendar.YEAR);
    }

    public Calendar getCalendar() {
        return calendar;
    }
}
