package com.viaagnolettisrl.hibernate;

public interface MachineEvent extends Event {
    public Machine getMachine();
    public void setMachine(Machine machine);
}
