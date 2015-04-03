package com.viaagnolettisrl.hibernate;
import java.io.Serializable;
import java.util.Set;

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
	transient private Set<History> history;
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

	public Boolean getCanAddClient(){
		return canAddClient;
	}

	public Boolean getCanAddJobOrder(){
		return canAddJobOrder;
	}

	public Boolean getCanAddMachine(){
		return canAddMachine;
	}
	public Set<History> getHistory() {
		return this.history;
	}
	public Long getId(){
		return id;
	}
	public Boolean getIsAdmin(){
		return isAdmin;
	}
	public String getName(){
		return name;
	}
	public String getPassword(){
		return password;
	}
	public String getSurname(){
		return surname;
	}
	public String getUsername(){
		return username;
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
	public void setCanAddClient(Boolean CanAddClient) {
		this.canAddClient = CanAddClient;
	}
	public void setCanAddJobOrder(Boolean CanAddJobOrder) {
		this.canAddJobOrder = CanAddJobOrder;
	}
	public void setCanAddMachine(Boolean CanAddMachine) {
		this.canAddMachine = CanAddMachine;
	}
	public void setHistory(Set<History> history) {
		this.history = history;
	}
	public void setId(Long Id) {
		this.id = Id;
	}
	public void setIsAdmin(Boolean IsAdmin) {
		this.isAdmin = IsAdmin;
	}
	public void setName(String Name) {
		this.name = Name;
	}
	public void setPassword(String Password) {
		this.password = Password;
	}
	public void setSurname(String Surname) {
		this.surname = Surname;
	}

	public void setUsername(String Username) {
		this.username = Username;
	}

	@Override
	public String toString() {
		return "User [canAddJobOrder=" + canAddJobOrder + ", name=" + name
				+ ", canAddClient=" + canAddClient + ", canAddMachine="
				+ canAddMachine + ", password=" + password + ", username="
				+ username + ", id=" + id + ", surname=" + surname
				+ ", isAdmin=" + isAdmin + "]";
	}
}