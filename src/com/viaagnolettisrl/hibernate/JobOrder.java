package com.viaagnolettisrl.hibernate;
import java.io.Serializable;
import java.util.Set;

public class JobOrder implements Serializable {
	private static final long serialVersionUID = 1L;
	private Long idClient;
	private Long id;
	private Long leadTime;
	transient private Set<Machine> machines;

	public void setMachines(Set<Machine> machines) {
		this.machines = machines;
	}

	public Set<Machine> getMachines() {
		return this.machines;
	}

	public Long getIdClient(){
		return idClient;
	}
	public void setIdClient(Long idClient) {
		this.idClient = idClient;
	}
	public Long getId(){
		return id;
	}
	public void setId(Long Id) {
		this.id = Id;
	}
	public Long getLeadTime(){
		return leadTime;
	}
	public void setLeadTime(Long LeadTime) {
		this.leadTime = LeadTime;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result
				+ ((idClient == null) ? 0 : idClient.hashCode());
		result = prime * result
				+ ((leadTime == null) ? 0 : leadTime.hashCode());
		result = prime * result
				+ ((machines == null) ? 0 : machines.hashCode());
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
		JobOrder other = (JobOrder) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (idClient == null) {
			if (other.idClient != null)
				return false;
		} else if (!idClient.equals(other.idClient))
			return false;
		if (leadTime == null) {
			if (other.leadTime != null)
				return false;
		} else if (!leadTime.equals(other.leadTime))
			return false;
		if (machines == null) {
			if (other.machines != null)
				return false;
		} else if (!machines.equals(other.machines))
			return false;
		return true;
	}
}