package com.viaagnolettisrl.hibernate;
import java.io.Serializable;
import java.util.Set;

public class Machine implements Serializable {
	private static final long serialVersionUID = 1L;
	private String name;
	private String type;
	private Float nicety;
	private Long id;
	private String color;
	transient private Set<JobOrder> joborders;

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
	public String getType(){
		return type;
	}
	public void setType(String Type) {
		this.type = Type;
	}
	public Float getNicety(){
		return nicety;
	}
	public void setNicety(Float Nicety) {
		this.nicety = Nicety;
	}
	public Long getId(){
		return id;
	}
	public void setId(Long Id) {
		this.id = Id;
	}
	public String getColor(){
		return color;
	}
	public void setColor(String Color) {
		this.color = Color;
	}
}