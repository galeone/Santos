package com.viaagnolettisrl.hibernate;

import java.io.Serializable;
import java.util.Date;

public class NonWorkingDay implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;

	private Date start, end;
	
	@Override
    public String toString() {
        return "NonWorkingDay [id=" + id + ", start=" + start + ", end=" + end + ", title=" + title + ", color="
                + color + "]";
    }

    public String title = "Giorno non lavorativo",
			color = "#ff9f89",
			type = "nonworkingday";
	public boolean overlap = false, // can't drop events on a non working day
			allDay = true,
			editable = true;
			

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((start == null) ? 0 : start.hashCode());
		result = prime * result + ((end == null) ? 0 : end.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		NonWorkingDay other = (NonWorkingDay) obj;
		if (start == null) {
			if (other.start != null)
				return false;
		} else if (!start.equals(other.start))
			return false;
		if (end == null) {
			if (other.end != null)
				return false;
		} else if (!end.equals(other.end))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
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

}