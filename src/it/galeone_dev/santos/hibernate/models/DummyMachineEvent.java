package it.galeone_dev.santos.hibernate.models;

import it.galeone_dev.santos.hibernate.abstractions.MachineEvent;

public class DummyMachineEvent extends DummyEvent implements MachineEvent {
    
    private Machine machine;

    @Override
    public Machine getMachine() {
        return machine;
    }

    @Override
    public void setMachine(Machine machine) {
        this.machine = machine;
    }

    @Override
    public boolean mergeableWith(MachineEvent e) {
        return false;
    }

    @Override
    public String getColor() {
        return null;
    }

    @Override
    public void setColor(String color) {        
    }

    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public void setTitle(String titile) {       
    }

}
