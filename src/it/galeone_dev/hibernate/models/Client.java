package it.galeone_dev.hibernate.models;

import java.io.Serializable;
import java.util.Set;

public class Client implements Serializable {
	private static final long serialVersionUID = 1L;

	private String name;
	private Long id;
	private String code;
	transient private Set<JobOrder> joborders;
	transient private Set<Sampling> sampling;

	public String getCode() {
		return code;
	}

	public Long getId() {
		return id;
	}

	public Set<JobOrder> getJobOrders() {
		return this.joborders;
	}

	public String getName() {
		return name;
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
        Client other = (Client) obj;
        if (id == null) {
            if (other.id != null) return false;
        } else if (!id.equals(other.id)) return false;
        return true;
    }

    public void setCode(String Code) {
		this.code = Code;
	}

	public void setId(Long Id) {
		this.id = Id;
	}

	public void setJobOrders(Set<JobOrder> joborders) {
		this.joborders = joborders;
	}

	public void setName(String Name) {
		this.name = Name;
	}

	@Override
	public String toString() {
		return "Client [name=" + name + ", id=" + id + ", code=" + code + "]";
	}

    public Set<Sampling> getSampling() {
        return sampling;
    }

    public void setSampling(Set<Sampling> sampling) {
        this.sampling = sampling;
    }
}