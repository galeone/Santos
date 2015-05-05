package com.viaagnolettisrl.hibernate;

import java.io.Serializable;
import java.util.Date;


public class Sampling extends DraggableMachineEvent implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;

	private Date start, end;
	
	private Machine machine;
	private JobOrder jobOrder;
	
	public String title = "Campionamento",
			color = "#00E",
			type = "sampling";
	public boolean overlap = true,
			allDay = true,
			editable = true;

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
        Sampling other = (Sampling) obj;
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

    @Override
    public String toString() {
        return "Sampling [id=" + id + ", start=" + start + ", end=" + end + ", machine=" + machine + ", jobOrder="
                + jobOrder + ", title=" + title + ", color=" + color + ", type=" + type + ", overlap=" + overlap
                + ", allDay=" + allDay + ", editable=" + editable + "]";
    }

    public Machine getMachine() {
        return machine;
    }

    public void setMachine(Machine machine) {
        this.machine = machine;
    }

    public JobOrder getJobOrder() {
        return jobOrder;
    }

    public void setJobOrder(JobOrder jobOrder) {
        this.jobOrder = jobOrder;
    }

}
