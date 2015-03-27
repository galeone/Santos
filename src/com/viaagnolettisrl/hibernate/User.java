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
	private Set<History> history;

	public void setHistory(Set<History> history) {
		this.history = history;
	}

	public Set<History> getHistory() {
		return this.history;
	}

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
}