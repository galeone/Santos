package com.viaagnolettisrl.hibernate;

import java.io.Serializable;
import java.util.Date;

import com.viaagnolettisrl.EventUtils;

public class WorkingHours extends DroppableGlobalEvent implements Serializable, GlobalEvent {
    
    private static final long serialVersionUID = 1L;
    
    private Long id;
    
    private Date start, end;
    
    private String title;
    public String color = "#afafaf", type = "workinghours";
    public boolean overlap = true,  allDay = true, editable = false;
    
    public String getTitle() {
        return "Ore lavorative: " + EventUtils.getLast(this);
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "WorkingHours [id=" + id + ", start=" + start + ", end=" + end + ", title=" + title + ", color="
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
        WorkingHours other = (WorkingHours) obj;
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
