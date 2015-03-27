package com.viaagnolettisrl.hibernate;

import java.io.Serializable;
import java.util.Date;


public class History implements Serializable {

	private static final long serialVersionUID = 1L;

	private Date time;
	private String action;
	private String what;
	private Long idUser;
	private Long id;

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

	public Long getIdUser(){
		return idUser;
	}

	public void setIdUser(Long idUser) {
		this.idUser = idUser;
	}

	public Long getId(){
		return id;
	}

	public void setId(Long Id) {
		this.id = Id;
	}


}