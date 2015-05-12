package com.viaagnolettisrl.hibernate;

public interface MachineEvent extends Event {
    public Machine getMachine();
    public void setMachine(Machine machine);
    public JobOrder getJobOrder();
    public void setJobOrder(JobOrder jobOrder);
}
