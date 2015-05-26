package it.galeone_dev.santos.hibernate.abstractions;

import java.util.Date;

public interface Event {
    public Long getId();
    public Date getEnd();
    public Date getStart();   
    public void setStart(Date start);
    public void setEnd(Date end);
}