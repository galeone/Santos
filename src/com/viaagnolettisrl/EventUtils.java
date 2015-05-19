package com.viaagnolettisrl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import com.viaagnolettisrl.hibernate.Event;
import com.viaagnolettisrl.hibernate.GlobalEvent;
import com.viaagnolettisrl.hibernate.MachineEvent;
import com.viaagnolettisrl.hibernate.WorkingHours;

public class EventUtils {
	
	private static SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH);
	public final static TimeZone timezone = TimeZone.getTimeZone("GMT");
	private static Calendar cal = Calendar.getInstance(timezone);
	
	public static final Long WEEK_WORKING_HOURS_IN_MINUTES    = 16L * 60;
	public static final Long WEEKEND_WORKING_HOURS_IN_MINUTES = 8L  * 60;
	
    public static Long getLast(Event e) {
        return (e.getEnd().getTime() - e.getStart().getTime()) / (60000);
    }
    
    public static WorkingHours getDefaultWorkingHours(Event e) {
        WorkingHours ret = new WorkingHours();
        sdf.setTimeZone(timezone);
        cal.setTime(e.getStart());
        Date start = start(e.getStart());
        int dow = cal.get(Calendar.DAY_OF_WEEK);
        ret.setStart(start);
        ret.setEnd(new Date(start.getTime() + (
                dow >= Calendar.MONDAY && dow <= Calendar.FRIDAY
                ? WEEK_WORKING_HOURS_IN_MINUTES
                : WEEKEND_WORKING_HOURS_IN_MINUTES) * 60000 ));
        return ret;
    }
    
    @SuppressWarnings("unchecked")
    private static WorkingHours workingHoursTheSameDayOf(Event e) {
        Collection<WorkingHours> ret = GetCollection.get(WorkingHours.class,EventUtils.start(e.getStart()), EventUtils.end(e.getEnd()));
        return ret.size() == 0
                ? getDefaultWorkingHours(e)
                : ret.toArray(new WorkingHours[0])[0];
    }
    
    public static WorkingHours workingHoursTheSameDayOf(GlobalEvent e) {
        return workingHoursTheSameDayOf((Event)e);
    }
    
    public static WorkingHours workingHoursTheSameDayOf(MachineEvent e) {
        return workingHoursTheSameDayOf((Event)e); 
    }
    
    public static Long getMaxLastForEventDay(Event e) {
        return getLast(getDefaultWorkingHours(e));
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
    
    
}
