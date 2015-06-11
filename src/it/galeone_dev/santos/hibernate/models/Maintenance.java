package it.galeone_dev.santos.hibernate.models;

import it.galeone_dev.santos.GetCollection;
import it.galeone_dev.santos.hibernate.abstractions.DroppableMachineEvent;
import it.galeone_dev.santos.hibernate.abstractions.EventUtils;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;

import org.hibernate.Session;

public class Maintenance extends DroppableMachineEvent implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;

	private Date start, end;
	private Machine machine;
	private String title, description;
	private boolean overlap = true, editable = true, allDay = true;
	public String type = "maintenance", color = "#808080";
	
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
        Maintenance other = (Maintenance) obj;
        if (id == null) {
            if (other.id != null) return false;
        } else if (!id.equals(other.id)) return false;
        return true;
    }



    public Date getStart() {
		return start;
	}

	public Date getEnd() {
		return end;
	}

	public Long getId() {
		return id;
	}

	public Machine getMachine() {
		return machine;
	}

	public void setStart(Date begin) {
		this.start = begin;
	}

	public void setEnd(Date end) {
		this.end = end;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setMachine(Machine machine) {
		this.machine = machine;
	}

    @Override
    public String toString() {
        return "Maintenance [id=" + id + ", start=" + start + ", end=" + end + ", machine=" + machine + ", title="
                + title + ", color=" + color + ", description=" + description + ", overlap=" + overlap + ", editable="
                + editable + ", allDay=" + allDay + ", type=" + type + "]";
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean getOverlap() {
        return this.overlap;
    }
    
    public void setOverlap(boolean overlap) {
        this.overlap = overlap;
    }

    public boolean getEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public boolean getAllDay() {
        return allDay;
    }

    public void setAllDay(boolean allDay) {
        this.allDay = allDay;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
    public static void merge(Maintenance event, Session hibSession) {
        // After the shift, I have only the event not in conflict with event
        Collection<Maintenance> sameDayEvents = GetCollection.maintenanceTheSameDayOf(event);
        Collection<Maintenance> mergeable = new LinkedList<Maintenance>();
        
        for(Maintenance sd : sameDayEvents) {
            if(!sd.equals(event) && sd.getDescription().equals(event.getDescription())) {
                mergeable.add(sd);
            }
        }
        
        if(mergeable.size() != 0) {
            Long sumOfLast = EventUtils.getLast(event);
            hibSession.clear();
            for(DroppableMachineEvent sc : mergeable) {
                sumOfLast += EventUtils.getLast(sc);
                hibSession.delete(sc);
            }
            event.setEnd(new Date(event.getStart().getTime() + sumOfLast * 60000));
            event = (Maintenance) hibSession.merge(event);
            hibSession.getTransaction().commit();
            hibSession.getTransaction().begin();
        }
    }

}
