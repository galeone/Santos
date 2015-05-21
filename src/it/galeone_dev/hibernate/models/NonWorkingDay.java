package it.galeone_dev.hibernate.models;

import it.galeone_dev.hibernate.abstractions.DroppableGlobalEvent;
import it.galeone_dev.hibernate.abstractions.GlobalEvent;

import java.io.Serializable;
import java.util.Date;

public class NonWorkingDay extends DroppableGlobalEvent implements Serializable, GlobalEvent {
    
    private static final long serialVersionUID = 1L;
    
    private Long id;
    
    private Date start, end;
    
    public String title = "Giorno non lavorativo", color = "#ff9f89", type = "nonworkingday";
    public boolean overlap = true,  allDay = true, editable = false;
    
    @Override
    public String toString() {
        return "NonWorkingDay [id=" + id + ", start=" + start + ", end=" + end + ", title=" + title + ", color="
                + color + "]";
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
        NonWorkingDay other = (NonWorkingDay) obj;
        if (id == null) {
            if (other.id != null) return false;
        } else if (!id.equals(other.id)) return false;
        return true;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Date getStart() {
        return start;
    }
    
    public void setStart(Date start) {
        this.start = start;
    }
    
    public Date getEnd() {
        return end;
    }
    
    public void setEnd(Date end) {
        this.end = end;
    }
    
}
