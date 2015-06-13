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

}
