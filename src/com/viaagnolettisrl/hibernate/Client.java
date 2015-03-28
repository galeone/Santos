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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((code == null) ? 0 : code.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result
				+ ((joborders == null) ? 0 : joborders.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		Client other = (Client) obj;
		if (code == null) {
			if (other.code != null)
				return false;
		} else if (!code.equals(other.code))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (joborders == null) {
			if (other.joborders != null)
				return false;
		} else if (!joborders.equals(other.joborders))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
}