package it.galeone_dev.hibernate.models;

import it.galeone_dev.GetCollection;
import it.galeone_dev.hibernate.abstractions.DroppableGlobalEvent;
import it.galeone_dev.hibernate.abstractions.EventUtils;
import it.galeone_dev.hibernate.abstractions.GlobalEvent;
import it.galeone_dev.hibernate.abstractions.MachineEvent;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

public class WorkingDay extends DroppableGlobalEvent implements Serializable, GlobalEvent {
    
    private static final long serialVersionUID = 1L;
    
    private Long id;
    
    private Date start, end;
    
    private String title;
    public String color = "#afafaf", type = "workinghours";
    private boolean overlap = true,  allDay = true, editable = false;
    
    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "WorkingHours [id=" + id + ", start=" + start + ", end=" + end + ", title=" + title + ", color=" + color + "]";
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        WorkingDay other = (WorkingDay) obj;
        if (id == null) {
            if (other.id != null) return false;
        } else if (!id.equals(other.id)) return false;
        return true;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Date getStart() {
        return start;
    }
    
    public void setStart(Date start) {
        this.start = start;
    }
    
    public Date getEnd() {
        return end;
    }
    
    public void setEnd(Date end) {
        this.end = end;
    }
    
    public static Long getAvaiableHoursBetween(MachineEvent e) {
        Date start = new Date(e.getStart().getTime()), end = new Date(e.getEnd().getTime());
        Long hours = 0L;
        while(start.before(end)) {
            hours += EventUtils.getLast(get(e.getStart()));
            for(MachineEvent conf : GetCollection.assignedJobOrdersTheSameDayOf(e)) {
                hours -= EventUtils.getLast(conf);
            }
            start = EventUtils.tomorrow(start);
        }
        return hours;
    }
    
    public static Long getWorkingHoursBetween(MachineEvent e) {
        Date start = new Date(e.getStart().getTime()), end = new Date(e.getEnd().getTime());
        AssignedJobOrder dummy = new AssignedJobOrder();
        dummy.setStart(start);
        dummy.setEnd(end);
        Long hours = 0L;
        while(dummy.getStart().before(dummy.getEnd())) {
            hours += EventUtils.getLast(get(dummy.getStart()));
            dummy.setStart(EventUtils.tomorrow(dummy.getStart()));
        }
        Collection<NonWorkingDay> nonWorks = GetCollection.nonWorkingDaysBetween(start, end);
        for(NonWorkingDay nw : nonWorks) {
        	hours -= EventUtils.getLast(getDefault(nw.getStart()));
        }
        return hours;
    }
    
    
	@SuppressWarnings("unchecked")
	public static WorkingDay get(Date day) {
        Collection<WorkingDay> tmp = GetCollection.get(WorkingDay.class, EventUtils.start(day), EventUtils.end(day));
        return tmp.size() == 0
        		? getDefault(day)
                : tmp.toArray(new WorkingDay[tmp.size()])[0];
    }
    
    private static WorkingDay getDefault(Date day) {
        WorkingDay ret = new WorkingDay();
        Date start = EventUtils.start(day);
        EventUtils.cal.setTime(start);
        int dow = EventUtils.cal.get(Calendar.DAY_OF_WEEK);
        ret.setStart(start);
        ret.setEnd(new Date(start.getTime() + (
                dow >= Calendar.MONDAY && dow <= Calendar.FRIDAY
                ? EventUtils.WEEK_WORKING_HOURS_IN_MINUTES
                : EventUtils.WEEKEND_WORKING_HOURS_IN_MINUTES) * 60000 ));
        return ret;
    }

	public boolean getOverlap() {
		return overlap;
	}

	public void setOverlap(boolean overlap) {
		this.overlap = overlap;
	}

	public boolean getAllDay() {
		return allDay;
	}

	public void setAllDay(boolean allDay) {
		this.allDay = allDay;
	}

	public boolean getEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}
    
}
