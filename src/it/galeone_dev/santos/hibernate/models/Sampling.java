package it.galeone_dev.santos.hibernate.models;

import it.galeone_dev.santos.hibernate.abstractions.DroppableMachineEvent;
import it.galeone_dev.santos.hibernate.abstractions.EventUtils;
import it.galeone_dev.santos.hibernate.abstractions.MachineEvent;

import java.io.Serializable;
import java.sql.Timestamp;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;


public class Sampling extends DroppableMachineEvent implements Serializable, MachineEvent {

	private static final long serialVersionUID = 1L;

	private Long id;
	private Date start, end;
	private Machine machine;
	private Client client;
	private boolean overlap = true, editable = true, allDay;
	private String description;
	private String title;
	private String dateTime;
	private Long leadTime;
	
	private String color = "#00E";

   public String getDateTime() {
        return this.dateTime;
    }
    
    public void setDateTime() {
        Format f = new SimpleDateFormat("dd/MM/yyyy");
        //hack -> hidden span with timestamp for js sorting
        this.dateTime = "<span style='display:none'>" + new Long(new Timestamp(getStart().getTime()).getTime()).toString() + "</span>" + f.format(getStart());
    }

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
    
    @Override
    public boolean mergeableWith(MachineEvent e) {
        if(e instanceof Sampling) {
            Sampling s = (Sampling)e;
            return !s.equals(this) && s.getMachine().equals(getMachine()) &&
                   s.getDescription().equals(getDescription()) &&
                   s.getClient().equals(getClient());
        }
        return false;
    }

    public Long getLeadTime() {
        return leadTime;
    }

    public void setLeadTime() {
        this.leadTime = EventUtils.getLast(this);
    }

    @Override
    public String getColor() {
        return color;
    }

    @Override
    public void setColor(String color) {
        this.color = color;
    }

}
