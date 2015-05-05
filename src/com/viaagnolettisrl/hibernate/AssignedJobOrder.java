package com.viaagnolettisrl.hibernate;

import java.io.Serializable;
import java.util.Date;

public class AssignedJobOrder extends DraggableMachineEvent implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;

	private Date start, end;
	private Machine machine;
	private JobOrder jobOrder;
	private String title, color;
	private boolean overlap, editable, allDay;
	
	public Long getLast() {
        return (getEnd().getTime() - getStart().getTime())/ (60 * 60 * 1000);
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
        AssignedJobOrder other = (AssignedJobOrder) obj;
        if (id == null) {
            if (other.id != null) return false;
        } else if (!id.equals(other.id)) return false;
        return true;
    }



    public Date getStart() {
		return start;
	}

	public Date getEnd() {
		return end;
	}

	public Long getId() {
		return id;
	}

	public JobOrder getJobOrder() {
		return jobOrder;
	}

	public Machine getMachine() {
		return machine;
	}

	public void setStart(Date begin) {
		this.start = begin;
	}

	public void setEnd(Date end) {
		this.end = end;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setJobOrder(JobOrder jobOrder) {
		this.jobOrder = jobOrder;
	}

	public void setMachine(Machine machine) {
		this.machine = machine;
	}

	@Override
	public String toString() {
		return "AssignedJobOrder [id=" + id + ", start=" + start + ", end="
				+ end + ", machine=" + machine + ", jobOrder=" + jobOrder + "]";
	}

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean getOverlap() {
        return this.overlap;
    }
    
    public void setOverlap(boolean overlap) {
        this.overlap = overlap;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public boolean getEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public boolean getAllDay() {
        return allDay;
    }

    public void setAllDay(boolean allDay) {
        this.allDay = allDay;
    }

}
