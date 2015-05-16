package com.viaagnolettisrl.hibernate;

import java.io.Serializable;
import java.util.Set;

public class JobOrder implements Serializable {
	private static final long serialVersionUID = 1L;

	private Client client;
	private Long id;
	private Long leadTime, missingTime, numberOfItems, timeForItem, offset;
	private String color, description;
	
	transient private Set<AssignedJobOrder> assignedJobOrders;
	transient private Set<Sampling> sampling;
	
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
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        JobOrder other = (JobOrder) obj;
        if (id == null) {
            if (other.id != null) return false;
        } else if (!id.equals(other.id)) return false;
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
        return "JobOrder [client=" + client + ", id=" + id + ", leadTime=" + leadTime + ", missingTime=" + missingTime
                + ", numberOfItems=" + numberOfItems + ", timeForItem=" + timeForItem + ", color=" + color + "]";
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

    public Long getNumberOfItems() {
        return numberOfItems;
    }

    public void setNumberOfItems(Long numberOfItems) {
        this.numberOfItems = numberOfItems;
    }

    public Long getTimeForItem() {
        return timeForItem;
    }

    public void setTimeForItem(Long timeForItem) {
        this.timeForItem = timeForItem;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<Sampling> getSampling() {
        return sampling;
    }

    public void setSampling(Set<Sampling> sampling) {
        this.sampling = sampling;
    }

    public Long getOffset() {
        return offset;
    }

    public void setOffset(Long offset) {
        this.offset = offset;
    }

}