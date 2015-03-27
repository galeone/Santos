package com.viaagnolettisrl.hibernate;
import java.io.Serializable;
import java.util.Set;

public class Client implements Serializable {
	private static final long serialVersionUID = 1L;
	private String name;
	private Long id;
	private String code;
	private Set<JobOrder> joborders;

	public void setJobOrders(Set<JobOrder> joborders) {
		this.joborders = joborders;
	}

	public Set<JobOrder> getJobOrders() {
		return this.joborders;
	}

	public String getName(){
		return name;
	}
	public void setName(String Name) {
		this.name = Name;
	}
	public Long getId(){
		return id;
	}
	public void setId(Long Id) {
		this.id = Id;
	}
	public String getCode(){
		return code;
	}
	public void setCode(String Code) {
		this.code = Code;
	}
}