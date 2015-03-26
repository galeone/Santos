package com.viaagnolettisrl.hibernate;

import java.io.Serializable;

public class User implements Serializable {

	private static final long serialVersionUID = 1L;

	private Boolean canAddJobOrder;
	private String name;
	private Boolean canAddClient;
	private Boolean canAddMachine;
	private String password;
	private String username;
	private Long id;
	private String surname;
	private Boolean isAdmin;

	public Boolean getCanAddJobOrder(){
		return canAddJobOrder;
	}

	public void setCanAddJobOrder(Boolean CanAddJobOrder) {
		this.canAddJobOrder = CanAddJobOrder;
	}

	public String getName(){
		return name;
	}

	public void setName(String Name) {
		this.name = Name;
	}

	public Boolean getCanAddClient(){
		return canAddClient;
	}

	public void setCanAddClient(Boolean CanAddClient) {
		this.canAddClient = CanAddClient;
	}

	public Boolean getCanAddMachine(){
		return canAddMachine;
	}

	public void setCanAddMachine(Boolean CanAddMachine) {
		this.canAddMachine = CanAddMachine;
	}

	public String getPassword(){
		return password;
	}

	public void setPassword(String Password) {
		this.password = Password;
	}

	public String getUsername(){
		return username;
	}

	public void setUsername(String Username) {
		this.username = Username;
	}

	public Long getId(){
		return id;
	}

	public void setId(Long Id) {
		this.id = Id;
	}

	public String getSurname(){
		return surname;
	}

	public void setSurname(String Surname) {
		this.surname = Surname;
	}

	public Boolean getIsAdmin(){
		return isAdmin;
	}

	public void setIsAdmin(Boolean IsAdmin) {
		this.isAdmin = IsAdmin;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((canAddClient == null) ? 0 : canAddClient.hashCode());
		result = prime * result
				+ ((canAddJobOrder == null) ? 0 : canAddJobOrder.hashCode());
		result = prime * result
				+ ((canAddMachine == null) ? 0 : canAddMachine.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((isAdmin == null) ? 0 : isAdmin.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result
				+ ((password == null) ? 0 : password.hashCode());
		result = prime * result + ((surname == null) ? 0 : surname.hashCode());
		result = prime * result
				+ ((username == null) ? 0 : username.hashCode());
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
		User other = (User) obj;
		if (canAddClient == null) {
			if (other.canAddClient != null)
				return false;
		} else if (!canAddClient.equals(other.canAddClient))
			return false;
		if (canAddJobOrder == null) {
			if (other.canAddJobOrder != null)
				return false;
		} else if (!canAddJobOrder.equals(other.canAddJobOrder))
			return false;
		if (canAddMachine == null) {
			if (other.canAddMachine != null)
				return false;
		} else if (!canAddMachine.equals(other.canAddMachine))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (isAdmin == null) {
			if (other.isAdmin != null)
				return false;
		} else if (!isAdmin.equals(other.isAdmin))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (password == null) {
			if (other.password != null)
				return false;
		} else if (!password.equals(other.password))
			return false;
		if (surname == null) {
			if (other.surname != null)
				return false;
		} else if (!surname.equals(other.surname))
			return false;
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		return true;
	}


}