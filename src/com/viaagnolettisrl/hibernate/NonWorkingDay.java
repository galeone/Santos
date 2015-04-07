package com.viaagnolettisrl.hibernate;

import java.io.Serializable;
import java.util.Date;

public class NonWorkingDay implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;

	private Date begins, ends;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((begins == null) ? 0 : begins.hashCode());
		result = prime * result + ((ends == null) ? 0 : ends.hashCode());
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
		if (begins == null) {
			if (other.begins != null)
				return false;
		} else if (!begins.equals(other.begins))
			return false;
		if (ends == null) {
			if (other.ends != null)
				return false;
		} else if (!ends.equals(other.ends))
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

	public Date getBegins() {
		return begins;
	}

	public void setBegins(Date begins) {
		this.begins = begins;
	}

	public Date getEnds() {
		return ends;
	}

	public void setEnds(Date ends) {
		this.ends = ends;
	}

}
