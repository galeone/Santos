package it.galeone_dev.santos.hibernate.models;

import java.io.Serializable;
import java.sql.Timestamp;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;


public class History implements Serializable {

	private static final long serialVersionUID = 1L;

	private Date time;
	private String action;
	private String what;
	private String dateTime;
	private User user;
	private Long id;
	
	public String getDateTime() {
		return this.dateTime;
	}
	
	public void setDateTime() {
		Format f = new SimpleDateFormat("dd/MM/yyyy - HH:mm");
		//hack -> hidden span with timestamp for js sorting
		this.dateTime = "<span style='display:none'>" + new Long(new Timestamp(getTime().getTime()).getTime()).toString() + "</span>" + f.format(getTime());
	}

	public Date getTime(){
		return time;
	}

	public void setTime(Date Time) {
		this.time = Time;
	}

	public String getAction(){
		return action;
	}

	public void setAction(String Action) {
		this.action = Action;
	}

	public String getWhat(){
		return what;
	}

	public void setWhat(String What) {
		this.what = What;
	}

	public User getUser(){
		return user;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((action == null) ? 0 : action.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((time == null) ? 0 : time.hashCode());
		result = prime * result + ((user == null) ? 0 : user.hashCode());
		result = prime * result + ((what == null) ? 0 : what.hashCode());
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
		History other = (History) obj;
		if (action == null) {
			if (other.action != null)
				return false;
		} else if (!action.equals(other.action))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (time == null) {
			if (other.time != null)
				return false;
		} else if (!time.equals(other.time))
			return false;
		if (user == null) {
			if (other.user != null)
				return false;
		} else if (!user.equals(other.user))
			return false;
		if (what == null) {
			if (other.what != null)
				return false;
		} else if (!what.equals(other.what))
			return false;
		return true;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Long getId(){
		return id;
	}

	public void setId(Long Id) {
		this.id = Id;
	}

}