package it.galeone_dev.santos.hibernate.abstractions;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import it.galeone_dev.santos.hibernate.models.Machine;

public class MachineCalendar {
    private Machine machine;
    private LinkedHashMap<String, ArrayList<ArrayList<Event>>> calendar;
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((calendar == null) ? 0 : calendar.hashCode());
        result = prime * result + ((machine == null) ? 0 : machine.hashCode());
        return result;
    }
    public LinkedHashMap<String,ArrayList<ArrayList<Event>>> getCalendar() {
        return calendar;
    }
    public void setCalendar(LinkedHashMap<String, ArrayList<ArrayList<Event>>> calendar) {
        this.calendar = calendar;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        MachineCalendar other = (MachineCalendar) obj;
        if (calendar == null) {
            if (other.calendar != null) return false;
        } else if (!calendar.equals(other.calendar)) return false;
        if (machine == null) {
            if (other.machine != null) return false;
        } else if (!machine.equals(other.machine)) return false;
        return true;
    }

    public Machine getMachine() {
        return machine;
    }
    public void setMachine(Machine machine) {
        this.machine = machine;
    }
 
}
