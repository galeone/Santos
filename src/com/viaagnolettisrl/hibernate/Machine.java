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

	transient private Set<AssignedJobOrder> assignedJobOrders;

	public Set<AssignedJobOrder> getAssignedJobOrders() {
		return assignedJobOrders;
	}
	public void setAssignedJobOrders(Set<AssignedJobOrder> assignedJobOrders) {
		this.assignedJobOrders = assignedJobOrders;
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
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((color == null) ? 0 : color.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((nicety == null) ? 0 : nicety.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	public String getColor(){
		return color;
	}
	public void setColor(String Color) {
		this.color = Color;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Machine other = (Machine) obj;
		if (color == null) {
			if (other.color != null)
				return false;
		} else if (!color.equals(other.color))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (nicety == null) {
			if (other.nicety != null)
				return false;
		} else if (!nicety.equals(other.nicety))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}
}