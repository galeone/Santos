package com.viaagnolettisrl.hibernate;
import java.io.Serializable;
import java.util.Set;

public class Machine implements Serializable {
	private static final long serialVersionUID = 1L;

	private String name;
	private String type;
	private Float nicety;
	private Long id;
	transient private Set<AssignedJobOrder> assignedJobOrders;
	transient private Set<Sampling> sampling;

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
        Machine other = (Machine) obj;
        if (id == null) {
            if (other.id != null) return false;
        } else if (!id.equals(other.id)) return false;
        return true;
    }

    public Set<AssignedJobOrder> getAssignedJobOrders() {
		return assignedJobOrders;
	}

	public Long getId(){
		return id;
	}
	public String getName(){
		return name;
	}
	public Float getNicety(){
		return nicety;
	}
	public String getType(){
		return type;
	}

	public void setAssignedJobOrders(Set<AssignedJobOrder> assignedJobOrders) {
		this.assignedJobOrders = assignedJobOrders;
	}

	public void setId(Long Id) {
		this.id = Id;
	}
	public void setName(String Name) {
		this.name = Name;
	}

	public void setNicety(Float Nicety) {
		this.nicety = Nicety;
	}
	public void setType(String Type) {
		this.type = Type;
	}
	
	@Override
	public String toString() {
		return "Machine [name=" + name + ", type=" + type + ", nicety="
				+ nicety + ", id=" + id + "]";
	}

    public Set<Sampling> getSampling() {
        return sampling;
    }

    public void setSampling(Set<Sampling> sampling) {
        this.sampling = sampling;
    }
}