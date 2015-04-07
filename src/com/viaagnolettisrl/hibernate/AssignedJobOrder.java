package com.viaagnolettisrl.hibernate;

import java.io.Serializable;
import java.util.Date;

public class AssignedJobOrder implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;

	private Date begins, ends;
	private Machine machine;
	private JobOrder jobOrder;
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AssignedJobOrder other = (AssignedJobOrder) obj;
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
		if (jobOrder == null) {
			if (other.jobOrder != null)
				return false;
		} else if (!jobOrder.equals(other.jobOrder))
			return false;
		if (machine == null) {
			if (other.machine != null)
				return false;
		} else if (!machine.equals(other.machine))
			return false;
		return true;
	}

	public Date getBegins() {
		return begins;
	}

	public Date getEnds() {
		return ends;
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((begins == null) ? 0 : begins.hashCode());
		result = prime * result + ((ends == null) ? 0 : ends.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result
				+ ((jobOrder == null) ? 0 : jobOrder.hashCode());
		result = prime * result + ((machine == null) ? 0 : machine.hashCode());
		return result;
	}

	public void setBegins(Date begin) {
		this.begins = begin;
	}

	public void setEnds(Date end) {
		this.ends = end;
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
		return "AssignedJobOrder [id=" + id + ", begins=" + begins + ", ends="
				+ ends + ", machine=" + machine + ", jobOrder=" + jobOrder + "]";
	}

}
