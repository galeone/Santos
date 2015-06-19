package it.galeone_dev.santos.hibernate.abstractions;

import it.galeone_dev.santos.hibernate.models.Machine;

public interface MachineEvent extends Event {
    public Machine getMachine();
    public void setMachine(Machine machine);
    public boolean mergeableWith(MachineEvent e);
    public String getColor();
    public void setColor(String color);
    public String getTitle();
    public void setTitle(String titile);
}
