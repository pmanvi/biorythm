package com.astro.bio;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class DateUtils {

    public static Date getDateCopy(Date theDate) {
        return new Date(theDate.getTime());
    }

    // this method shaves off the timestamp part, i.e., timet set to midnight
    public static Date getDateBeginning(TimeZone tz, Date theDate) {
        Calendar theCalendar = Calendar.getInstance(tz);
        theCalendar.setTime(theDate);
        theCalendar.set(Calendar.HOUR, 0);
        theCalendar.set(Calendar.MINUTE, 0);
        theCalendar.set(Calendar.SECOND, 0);
        theCalendar.set(Calendar.MILLISECOND, 0);
        theCalendar.set(Calendar.AM_PM, Calendar.AM);
        return theCalendar.getTime();
    }

    public static Date getLastDateOfPastTwelfthMonth(TimeZone tz, Date theDate) {
        Calendar aDate = Calendar.getInstance(tz);
        aDate.setTime(theDate);
        aDate.add(Calendar.MONTH, -11);
        return getLastDateInMonth(tz, aDate.getTime());
    }

    public static Date getLastDateInMonth(TimeZone tz, Date theDate) {
        Calendar originalDate = Calendar.getInstance(tz);
        originalDate.setTime(theDate);
        Calendar returnDate = Calendar.getInstance(tz);
        returnDate.setTime(theDate);
        while (originalDate.get(Calendar.MONTH) == returnDate.get(Calendar.MONTH)) {
            returnDate.add(Calendar.DATE, 1);
        }
        returnDate.add(Calendar.DATE, -1);
        return returnDate.getTime();
    }

    public static Date getPreviousSunday(TimeZone tz, Date theDate) {
        return getPreviousWeekDay(tz, theDate, Calendar.SUNDAY);
    }

    public static Date getPreviousMonday(TimeZone tz, Date theDate) {
        return getPreviousWeekDay(tz, theDate, Calendar.MONDAY);
    }

    public static Date getPreviousSundayWeeksAgo(TimeZone tz, Date theDate, int numOfWeeksAgo) {
        return getPreviousWeekDayWeeksAgo(tz, theDate, Calendar.SUNDAY, numOfWeeksAgo);
    }

    public static Date getPreviousWeekDay(TimeZone tz, Date theDate, int dayOfWeek) {
        if (!isValidDayOfWeek(dayOfWeek)) {
            return null;
        }
        Calendar returnDate = Calendar.getInstance(tz);
        returnDate.setTime(theDate);
        returnDate.add(Calendar.DATE, -1);
        while (returnDate.get(Calendar.DAY_OF_WEEK) != dayOfWeek) {
            returnDate.add(Calendar.DATE, -1);
        }
        return returnDate.getTime();
    }

    public static Date getPreviousWeekDayWeeksAgo(TimeZone tz, Date theDate, int dayOfWeek, int numOfWeeksAgo) {
        if (numOfWeeksAgo < 0 || !isValidDayOfWeek(dayOfWeek)) {
            return null;
        }
        Date previousWeekDay = getPreviousWeekDay(tz, theDate, dayOfWeek);
        Calendar returnDate = Calendar.getInstance(tz);
        returnDate.setTime(previousWeekDay);
        returnDate.add(Calendar.DATE, -(numOfWeeksAgo * 7));
        return returnDate.getTime();
    }

    public static Date getNextSunday(TimeZone tz, Date theDate) {
        return getNextWeekDay(tz, theDate, Calendar.SUNDAY);
    }

    public static Date getNextWeekDay(TimeZone tz, Date theDate, int dayOfWeek) {
        if (!isValidDayOfWeek(dayOfWeek)) {
            return null;
        }
        Calendar returnDate = Calendar.getInstance(tz);
        returnDate.setTime(theDate);
        returnDate.add(Calendar.DATE, 1);
        while (returnDate.get(Calendar.DAY_OF_WEEK) != dayOfWeek) {
            returnDate.add(Calendar.DATE, 1);
        }
        return returnDate.getTime();
    }

    public static boolean isValidDayOfWeek(int dayOfWeek) {
        if (dayOfWeek == Calendar.SUNDAY ||
                dayOfWeek == Calendar.MONDAY ||
                dayOfWeek == Calendar.TUESDAY ||
                dayOfWeek == Calendar.WEDNESDAY ||
                dayOfWeek == Calendar.THURSDAY ||
                dayOfWeek == Calendar.FRIDAY ||
                dayOfWeek == Calendar.SATURDAY) {
            return true;
        } else {
            return false;
        }
    }
    static final long ONE_HOUR = 60 * 60 * 1000L;

    public static long getDaysInBetweenDates(Date d1, Date d2) {
        if (d1 == null || d2 == null) {
            return -1;
        }
        return ((d2.getTime() - d1.getTime() + ONE_HOUR) /
                (ONE_HOUR * 24));

    }

    public static int getDays(Calendar from, Calendar to) {
        int cnt = 0;
        while (from.before(to)) {
            cnt++;
            if (from.getActualMaximum(from.DATE) == from.get(to.DATE)) {
                from.roll(to.MONTH, true);
            }
            if (from.getActualMaximum(from.MONTH) == from.get(to.MONTH)) {
                from.roll(to.YEAR, true);
            }
            from.roll(to.DATE, true);
        }
        return cnt;
    }

    public static void main(String[] args) {
        // Testing code
        Date d1 = new Date(1976, 7, 29);
        // d1=new Date(2008,7,1);
        Date d2 = new Date(2008, 7, 2);

        Calendar start = Calendar.getInstance();
        start.set(Calendar.YEAR, 1976);
        start.set(Calendar.MONTH, Calendar.JULY);
        start.set(Calendar.DATE, 29);
        start.set(Calendar.HOUR, 3);
        start.set(Calendar.MINUTE, 0);

        Calendar end = Calendar.getInstance();
        end.set(Calendar.YEAR, 2008);
        end.set(Calendar.MONTH, Calendar.JULY);
        end.set(Calendar.DATE, 3);
        end.set(Calendar.HOUR, 3);
        end.set(Calendar.MINUTE, 0);

        //System.out.println(""+diffDayPeriods(end, start));

        long days1 = diffDayPeriods(start, end);
        //System.out.println("Total Days" + days1);
        days1 = Math.abs(days1);
        long year = days1 / 365;
        //System.out.println("Year :" + year);
        long days = days1 - (year * 365);
        int months = (int) days / 30;
        //System.out.println("Months :" + months);
        //System.out.println("Days :" + (days - (months * 30)));
        //System.out.println("E :" + days1 % Cycle.Emotional.duration());
        //System.out.println("I :" + days1 % Cycle.Intellectual.duration());
        //System.out.println("P :" + days1 % Cycle.Physical.duration());

        long tDays = days1;
        System.out.println("Total Days" + tDays);
        System.out.println("Years :"+tDays/365+" Days: "+tDays%365);
        
        System.out.println("E :" + Cycle.Emotional.percent(tDays));
        System.out.println("I :" + Cycle.Intellectual.percent(tDays));
        System.out.println("P :" + Cycle.Physical.percent(tDays));
        System.out.println("O :" + Cycle.overAll(tDays));
        System.out.println("OverAll :" + State.state(Cycle.overAll(tDays)));
        System.out.println("Map : " + Cycle.Emotional.getDetails(d1, d2));

    }
   
    public static final long MILLISECS_PER_MINUTE = 60*1000;
    public static final long MILLISECS_PER_HOUR   = 60*MILLISECS_PER_MINUTE;
    protected static final long MILLISECS_PER_DAY = 24*MILLISECS_PER_HOUR;
    public enum Month{
        JAN,FEB,MARCH,APR,MAY,JUN,JUL,AUG,SEP,OCT,NOV,DEC
    };
    
    public static long diffDayPeriods(Calendar start,Calendar end) {
        if(start.after(end)){
            Calendar temp = start;
            start=end;
            end=temp;
        }
        long endL   =  end.getTimeInMillis() +  end.getTimeZone().getOffset(  end.getTimeInMillis() );
        long startL = start.getTimeInMillis() + start.getTimeZone().getOffset( start.getTimeInMillis() );
        return (endL - startL) / MILLISECS_PER_DAY;
    }

}
