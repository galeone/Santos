package it.galeone_dev.santos.hibernate.models;

import java.util.Date;

import it.galeone_dev.santos.hibernate.abstractions.Event;

public class DummyEvent implements Event {
    
    private Date start, end;

    @Override
    public Long getId() {
        return null;
    }

    @Override
    public Date getEnd() {
        return end;
    }

    @Override
    public Date getStart() {
        return start;
    }

    @Override
    public void setStart(Date start) {
        this.start = start;
    }

    @Override
    public void setEnd(Date end) {
        this.end = end;
    }

}
