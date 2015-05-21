package it.galeone_dev.hibernate.abstractions;

import it.galeone_dev.hibernate.models.Machine;

public interface MachineEvent extends Event {
    public Machine getMachine();
    public void setMachine(Machine machine);
}
