package com.viaagnolettisrl.hibernate;
import java.io.Serializable;
import java.util.Set;

public class JobOrder implements Serializable {
	private static final long serialVersionUID = 1L;
	private Long idClient;
	private Long id;
	private Long leadTime;
	private Set<Machine> machines;

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
}