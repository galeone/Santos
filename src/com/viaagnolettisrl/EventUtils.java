package com.viaagnolettisrl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import com.viaagnolettisrl.hibernate.Event;

public class EventUtils {
	
	private static SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH);
	public final static TimeZone timezone = TimeZone.getTimeZone("GMT");
	private static Calendar cal = Calendar.getInstance(timezone);
	
    public static Long getLast(Event e) {
        return (e.getEnd().getTime() - e.getStart().getTime()) / (60000);
    }
    
    public static Date parseDate(String date) throws ParseException {
        sdf.setTimeZone(timezone);
        cal.setTime(sdf.parse(date));
        return cal.getTime();
    }
    
    
}
