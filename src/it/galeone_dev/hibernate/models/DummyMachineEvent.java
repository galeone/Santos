package it.galeone_dev.hibernate.models;

import java.util.Date;

import it.galeone_dev.hibernate.abstractions.MachineEvent;

public class DummyMachineEvent implements MachineEvent {
    
    private Date start, end;
    private Machine machine;

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

    @Override
    public Machine getMachine() {
        return machine;
    }

    @Override
    public void setMachine(Machine machine) {
        this.machine = machine;
    }

}
