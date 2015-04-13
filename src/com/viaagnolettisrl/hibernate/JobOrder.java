package com.viaagnolettisrl.hibernate;

import java.io.Serializable;
import java.util.Set;

public class JobOrder implements Serializable {
	private static final long serialVersionUID = 1L;

	private Client client;
	private Long id;
	private Long leadTime, missingTime;
	private String color;
	
	transient private Set<AssignedJobOrder> assignedJobOrders;
	
	public Set<AssignedJobOrder> getAssignedJobOrders() {
		return assignedJobOrders;
	}

	public Client getClient() {
		return client;
	}

	public Long getId() {
		return id;
	}

	public Long getLeadTime() {
		return leadTime;
	}

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((client == null) ? 0 : client.hashCode());
        result = prime * result + ((color == null) ? 0 : color.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((leadTime == null) ? 0 : leadTime.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        JobOrder other = (JobOrder) obj;
        if (client == null) {
            if (other.client != null) return false;
        } else if (!client.equals(other.client)) return false;
        if (color == null) {
            if (other.color != null) return false;
        } else if (!color.equals(other.color)) return false;
        if (id == null) {
            if (other.id != null) return false;
        } else if (!id.equals(other.id)) return false;
        if (leadTime == null) {
            if (other.leadTime != null) return false;
        } else if (!leadTime.equals(other.leadTime)) return false;
        return true;
    }

    public void setAssignedJobOrders(Set<AssignedJobOrder> assignedJobOrders) {
		this.assignedJobOrders = assignedJobOrders;
	}

	public void setClient(Client client) {
		this.client = client;
	}

	public void setId(Long Id) {
		this.id = Id;
	}

	public void setLeadTime(Long LeadTime) {
		this.leadTime = LeadTime;
	}

	@Override
	public String toString() {
		return "JobOrder [client=" + client + ", id=" + id + ", leadTime="
				+ leadTime + "]";
	}

    public Long getMissingTime() {
        return missingTime;
    }

    public void setMissingTime(Long missingTime) {
        this.missingTime = missingTime;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

}