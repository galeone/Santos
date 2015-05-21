package it.galeone_dev.hibernate.models;

import java.io.Serializable;
import java.util.Set;

public class User implements Serializable {
    private static final long      serialVersionUID = 1L;
    
    private Boolean                canAddJobOrder;
    private Boolean                canAssignJobOrder;
    private String                 name;
    private Boolean                canAddClient;
    private Boolean                canAddMachine;
    private String                 password;
    private String                 username;
    private Long                   id;
    private String                 surname;
    private Boolean                isAdmin;
    transient private Set<History> history;
    
    public Boolean getCanAddClient() {
        return canAddClient;
    }
    
    public Boolean getCanAddJobOrder() {
        return canAddJobOrder;
    }
    
    public Boolean getCanAddMachine() {
        return canAddMachine;
    }
    
    public Set<History> getHistory() {
        return this.history;
    }
    
    public Long getId() {
        return id;
    }
    
    public Boolean getIsAdmin() {
        return isAdmin;
    }
    
    public String getName() {
        return name;
    }
    
    public String getPassword() {
        return password;
    }
    
    public String getSurname() {
        return surname;
    }
    
    public String getUsername() {
        return username;
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
        User other = (User) obj;
        if (id == null) {
            if (other.id != null) return false;
        } else if (!id.equals(other.id)) return false;
        return true;
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
        return "User [canAddJobOrder=" + canAddJobOrder + ", name=" + name + ", canAddClient=" + canAddClient
                + ", canAddMachine=" + canAddMachine + ", password=" + password + ", username=" + username + ", id="
                + id + ", surname=" + surname + ", isAdmin=" + isAdmin + "]";
    }

    public Boolean getCanAssignJobOrder() {
        return canAssignJobOrder;
    }

    public void setCanAssignJobOrder(Boolean canAssignJobOrder) {
        this.canAssignJobOrder = canAssignJobOrder;
    }
}