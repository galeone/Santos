package it.galeone_dev.santos.hibernate.abstractions;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class EventUtils {
	
	private static SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH);
	public final static TimeZone timezone = TimeZone.getTimeZone("GMT");
	public static Calendar cal = Calendar.getInstance(timezone);
	
	public static final Long WEEK_WORKING_HOURS_IN_MINUTES    = 16L * 60;
	public static final Long WEEKEND_WORKING_HOURS_IN_MINUTES = 8L  * 60;
	
    public static Long getLast(Event e) {
        return (e.getEnd().getTime() - e.getStart().getTime()) / (60000);
    }
    
    public static Date parseDate(String date) throws ParseException {
        sdf.setTimeZone(timezone);
        cal.setTime(sdf.parse(date));
        return cal.getTime();
    }
    
    public static Date start(Date d) {
        Calendar cal = Calendar.getInstance(EventUtils.timezone);
        cal.setTime(d);
        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return new Date(cal.getTime().getTime());
    }
    
    public static Date end(Date d) {
        Calendar calendar = Calendar.getInstance(EventUtils.timezone);
        calendar.setTime(d);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime();
    }
    
    public static Date tomorrow(Date d) {
        Calendar cal = Calendar.getInstance(EventUtils.timezone);
        Date today = start(d);
        cal.setTime(today);
        cal.add(Calendar.DATE, 1);
        return cal.getTime();
    }
    
    public static boolean sameDay(Date a, Date b) {
        return sdf.format(a).startsWith(sdf.format(b).substring(0, 16));
    }
    
}
