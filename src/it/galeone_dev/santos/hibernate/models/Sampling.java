package it.galeone_dev.santos.hibernate.models;

import it.galeone_dev.santos.GetCollection;
import it.galeone_dev.santos.hibernate.abstractions.DroppableMachineEvent;
import it.galeone_dev.santos.hibernate.abstractions.EventUtils;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;

import org.hibernate.Session;


public class Sampling extends DroppableMachineEvent implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;
	private Date start, end;
	private Machine machine;
	private Client client;
	private boolean overlap = true, editable = true, allDay;
	private String description;
	private String title;
	
	public String color = "#00E";
	

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public String type = "sampling";
	
	public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean getAllDay() {
        return allDay;
    }

    public void setAllDay(boolean allDay) {
        this.allDay = allDay;
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
        Sampling other = (Sampling) obj;
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

    @Override
    public String toString() {
        return "Sampling [id=" + id + ", start=" + start + ", end=" + end + ", machine=" + machine + ", client="
                + client + ", overlap=" + overlap + ", editable=" + editable + ", allDay=" + allDay + ", description="
                + description + ", title=" + title + ", color=" + color + ", type=" + type + "]";
    }

    public Machine getMachine() {
        return machine;
    }

    public void setMachine(Machine machine) {
        this.machine = machine;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }
    
    public static void merge(Sampling event, Session hibSession) {
        // After the shift, I have only the event not in conflict with event
        Collection<Sampling> sameDayEvents = GetCollection.samplingTheSameDayOf(event);
        Collection<Sampling> mergeable = new LinkedList<Sampling>();
        
        for(Sampling sd : sameDayEvents) {
            if(!sd.equals(event) && sd.getClient().equals(event.getClient())
                    && sd.getDescription().equals(event.getDescription())) {
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
            event = (Sampling) hibSession.merge(event);
            hibSession.getTransaction().commit();
            hibSession.getTransaction().begin();
        }
    }

}
