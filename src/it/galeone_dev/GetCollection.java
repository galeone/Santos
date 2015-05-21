package it.galeone_dev;

import it.galeone_dev.hibernate.HibernateUtils;
import it.galeone_dev.hibernate.abstractions.DroppableMachineEvent;
import it.galeone_dev.hibernate.abstractions.Event;
import it.galeone_dev.hibernate.abstractions.EventUtils;
import it.galeone_dev.hibernate.abstractions.GlobalEvent;
import it.galeone_dev.hibernate.abstractions.MachineEvent;
import it.galeone_dev.hibernate.models.AssignedJobOrder;
import it.galeone_dev.hibernate.models.Client;
import it.galeone_dev.hibernate.models.History;
import it.galeone_dev.hibernate.models.JobOrder;
import it.galeone_dev.hibernate.models.Machine;
import it.galeone_dev.hibernate.models.Maintenance;
import it.galeone_dev.hibernate.models.NonWorkingDay;
import it.galeone_dev.hibernate.models.Sampling;
import it.galeone_dev.hibernate.models.User;
import it.galeone_dev.hibernate.models.WorkingHours;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

public class GetCollection {
        
    @SuppressWarnings("rawtypes")
    public static Collection get(Class entity, Date start, Date end, Machine m, Long eventId) {
        Session session = HibernateUtils.getSessionFactory().openSession();
        boolean between = start != null && end != null,
                machine = m != null,
                after = start != null && end == null,
                conflict = eventId != null;
        
        Query q = session.createQuery("from " + entity.getSimpleName() + " where " +
                (between  ? "(starts BETWEEN :start AND :end)" : "1=1") + " AND " +
                (after    ? "(starts > :start)" : "1=1") + " AND " +
                (machine  ? "(idmachine = :machine)" : "1=1") + " AND " +
                (conflict ? "(id <> :id)" : "1=1"));
        
        if(between) {
            q.setDate("start", start).setDate("end", end);
        }
        
        if(after) {
            q.setDate("start", start);
        }
        
        if(machine) {
            q.setLong("machine", m.getId());
        }
        
        if(conflict) {
            q.setLong("id", eventId);
        }
        
        List ret = q.list();
        session.close();
        return ret;
    }
    
    public static Date oldStart, oldEnd;
    
    public static void resetDate(Event e) {
        oldStart = new Date(e.getStart().getTime());
        oldEnd   = new Date(e.getEnd().getTime());
        
        e.setStart(EventUtils.start(oldStart));
        e.setEnd(EventUtils.end(oldStart));
    }
    
    public static void restoreDate(Event e) {
        e.setStart(oldStart);
        e.setEnd(oldEnd);
    }
    
    @SuppressWarnings("rawtypes")
    public static Collection get(Class entity) {
        return get(entity, null, null,null, null);
    }
    
    // Machine
    @SuppressWarnings("rawtypes")
    public static Collection get(Class entity, Machine m) {
        return get(entity, null, null, m, null);
    }
    
    // After
    @SuppressWarnings("rawtypes")
    public static Collection get(Class entity, Date after) {
        return get(entity, after, null,null, null);
    }
    
    // After on machine
    @SuppressWarnings("rawtypes")
    public static Collection get(Class entity, Date after, Machine m) {
        return get(entity, after, null, m, null);
    }
    
    // Between on machine
    @SuppressWarnings("rawtypes")
    public static Collection get(Class entity, Date start, Date end, Machine m) {
        return get(entity, start, end, m, null);
    }
    
    // Global conflict
    @SuppressWarnings("rawtypes")
    public static Collection get(Class entity, Date start, Date end) {
        return get(entity, start, end, null, null);
    }
    
    @SuppressWarnings("unchecked")
    public static Collection<AssignedJobOrder> assignedJobOrders() {
        return (Collection<AssignedJobOrder>)get(AssignedJobOrder.class);
    }

    @SuppressWarnings("unchecked")
    public static Collection<AssignedJobOrder> assignedJobOrders(Machine m) {
        return (Collection<AssignedJobOrder>)get(AssignedJobOrder.class, m);
    }
    
    @SuppressWarnings("unchecked")
    public static Collection<AssignedJobOrder> assignedJobOrdersBetween(Date start, Date end) {
        return (Collection<AssignedJobOrder>)get(AssignedJobOrder.class, start, end);
    }
    
    @SuppressWarnings("unchecked")
    public static Collection<AssignedJobOrder> assignedJobOrdersBetween(Machine m, Date start, Date end) {
        return (Collection<AssignedJobOrder>)get(AssignedJobOrder.class, start, end, m);
    }
    
    @SuppressWarnings("unchecked")
    public static Collection<AssignedJobOrder> assignedJobOrdersTheSameDayOf(MachineEvent e) {
        resetDate(e);
        Collection<AssignedJobOrder> ret = get(AssignedJobOrder.class, e.getStart(), e.getEnd(), e.getMachine(), e.getId());
        restoreDate(e);
        return ret;
    }
    
    @SuppressWarnings("unchecked")
    public static Collection<AssignedJobOrder> assignedJobOrdersTheSameDayOf(GlobalEvent e) {
        resetDate(e);
        Collection<AssignedJobOrder> ret = get(AssignedJobOrder.class, e.getStart(), e.getEnd());
        restoreDate(e);
        return ret;
    }
    
    @SuppressWarnings("unchecked")
    public static Collection<AssignedJobOrder> assignedJobOrdersAfterEvent(GlobalEvent e) {
        return (Collection<AssignedJobOrder>)get(AssignedJobOrder.class, e.getStart());
    }
    
    @SuppressWarnings("unchecked")
    public static Collection<AssignedJobOrder> assignedJobOrdersAfterEvent(MachineEvent e) {
        return (Collection<AssignedJobOrder>)get(AssignedJobOrder.class, e.getStart(), e.getMachine());
    }
    
    @SuppressWarnings("unchecked")
    public static Collection<WorkingHours> workingHours() {
        return (Collection<WorkingHours>) get(WorkingHours.class);
    }
    
    public static Collection<WorkingHours> workingHours(Boolean editable) {
        Collection<WorkingHours> l = workingHours();
        for(WorkingHours nw : l) {
            nw.editable = editable;
        }
        return l;
    }
    
    @SuppressWarnings("unchecked")
    public static Collection<WorkingHours> workingHoursBetween(Boolean editable, Date start, Date end) {
        Collection<WorkingHours> l = (Collection<WorkingHours>) get(WorkingHours.class, start, end);
        for(WorkingHours nw : l) {
            nw.editable = editable;
        }
        return l;
    }
    @SuppressWarnings("unchecked")
    public static Collection<WorkingHours> workingHoursBetween(Date start, Date end) {
        return (Collection<WorkingHours>) get(WorkingHours.class, start, end);
    }
    
    @SuppressWarnings("unchecked")
    public static Collection<WorkingHours> workingHoursAfterEvent(Event e) {
        return (Collection<WorkingHours>)get(WorkingHours.class, e.getStart()); 
    }
    
    public static Collection<WorkingHours> workingHoursAfterEvent(GlobalEvent e) {
        return workingHoursAfterEvent((Event)e);
    }
    
    public static Collection<WorkingHours> workingHoursAfterEvent(MachineEvent e) {
        return workingHoursAfterEvent((Event)e);
    }
    
    @SuppressWarnings("unchecked")
    public static Collection<NonWorkingDay> nonWorkingDays() {
        return (Collection<NonWorkingDay>) get(NonWorkingDay.class);
    }
    
    public static Collection<NonWorkingDay> nonWorkingDays(Boolean editable) {
        Collection<NonWorkingDay> l = nonWorkingDays();
        for(NonWorkingDay nw : l) {
            nw.editable = editable;
            nw.overlap = editable;
        }
        return l;
    }
    
    @SuppressWarnings("unchecked")
    public static Collection<NonWorkingDay> nonWorkingDaysBetween(Boolean editable, Date start, Date end) {
        Collection<NonWorkingDay> l = (Collection<NonWorkingDay>) get(NonWorkingDay.class, start, end);
        for(NonWorkingDay nw : l) {
            nw.editable = editable;
            nw.overlap = editable;
        }
        return l;
    }
    @SuppressWarnings("unchecked")
    public static Collection<NonWorkingDay> nonWorkingDaysBetween(Date start, Date end) {
        return (Collection<NonWorkingDay>) get(NonWorkingDay.class, start, end);
    }
    
    @SuppressWarnings("unchecked")
    public static Collection<NonWorkingDay> nonWorkingDaysTheSameDayOf(Event e) {
        resetDate(e);
        Collection<NonWorkingDay> ret = get(NonWorkingDay.class,e.getStart(), e.getEnd());
        restoreDate(e);
        return ret;
    }
    
    public static Collection<NonWorkingDay> nonWorkingDaysTheSameDayOf(GlobalEvent e) {
        return nonWorkingDaysTheSameDayOf((Event)e);
    }
    
    public static Collection<NonWorkingDay> nonWorkingDaysTheSameDayOf(MachineEvent e) {
        return nonWorkingDaysTheSameDayOf((Event)e); 
    }
    
    @SuppressWarnings("unchecked")
    public static Collection<NonWorkingDay> nonWorkingDaysAfterEvent(Event e) {
        return (Collection<NonWorkingDay>)get(NonWorkingDay.class, e.getStart()); 
    }
    
    public static Collection<NonWorkingDay> nonWorkingDaysAfterEvent(GlobalEvent e) {
        return nonWorkingDaysAfterEvent((Event)e);
    }
    
    public static Collection<NonWorkingDay> nonWorkingDaysAfterEvent(MachineEvent e) {
        return nonWorkingDaysAfterEvent((Event)e);
    }
    
    
    @SuppressWarnings("unchecked")
    public static Collection<Maintenance> maintenance() {
        return (Collection<Maintenance>)get(Maintenance.class);
    }
    
    @SuppressWarnings("unchecked")
    public static Collection<Maintenance> maintenance(Machine m) {
        return (Collection<Maintenance>)get(Maintenance.class, m);
    }    
    
    @SuppressWarnings("unchecked")
    public static Collection<Maintenance> maintenanceTheSameDayOf(GlobalEvent e) {
        resetDate(e);
        Collection<Maintenance> ret = get(Maintenance.class, e.getStart(), e.getEnd());
        restoreDate(e);
        return ret;
    }
    
    @SuppressWarnings("unchecked")
    public static Collection<Maintenance> maintenanceTheSameDayOf(MachineEvent e) {
        resetDate(e);
        Collection<Maintenance> ret =  get(Maintenance.class, e.getStart(), e.getEnd(), e.getMachine(), e.getId());
        restoreDate(e);
        return ret;
    }
    
    @SuppressWarnings("unchecked")
    public static Collection<Maintenance> maintenanceAfterEvent(GlobalEvent e) {
        return (Collection<Maintenance>) get(Maintenance.class, e.getStart());
    }
    
    @SuppressWarnings("unchecked")
    public static Collection<Maintenance> maintenanceAfterEvent(MachineEvent e) {
        return (Collection<Maintenance>) get(Maintenance.class, e.getStart(), e.getMachine());
    }
    
    @SuppressWarnings("unchecked")
    public static Collection<Maintenance> maintenanceBetween(Date start, Date end) {
        return (Collection<Maintenance>) get(Maintenance.class, start, end);
    }
    
    @SuppressWarnings("unchecked")
    public static Collection<Maintenance> maintenanceBetween(Machine m, Date start, Date end) {
        return (Collection<Maintenance>)get(Maintenance.class, start, end, m);
    }
    
    @SuppressWarnings("unchecked")
    public static Collection<Sampling> sampling() {
        return (Collection<Sampling>)get(Sampling.class);
    }
    
    @SuppressWarnings("unchecked")
    public static Collection<Sampling> sampling(Machine m) {
        return (Collection<Sampling>)get(Sampling.class, m);
    }    
    
    @SuppressWarnings("unchecked")
    public static Collection<Sampling> samplingTheSameDayOf(GlobalEvent e) {
        resetDate(e);
        Collection<Sampling> ret = get(Sampling.class, e.getStart(), e.getEnd());
        restoreDate(e);
        return ret;
    }
    
    @SuppressWarnings("unchecked")
    public static Collection<Sampling> samplingTheSameDayOf(MachineEvent e) {
        resetDate(e);
        Collection<Sampling> ret =  get(Sampling.class, e.getStart(), e.getEnd(), e.getMachine(), e.getId());
        restoreDate(e);
        return ret;
    }
    
    @SuppressWarnings("unchecked")
    public static Collection<Sampling> samplingAfterEvent(GlobalEvent e) {
        return (Collection<Sampling>) get(Sampling.class, e.getStart());
    }
    
    @SuppressWarnings("unchecked")
    public static Collection<Sampling> samplingAfterEvent(MachineEvent e) {
        return (Collection<Sampling>) get(Sampling.class, e.getStart(), e.getMachine());
    }
    
    @SuppressWarnings("unchecked")
    public static Collection<Sampling> samplingBetween(Date start, Date end) {
        return (Collection<Sampling>) get(Sampling.class, start, end);
    }
    
    @SuppressWarnings("unchecked")
    public static Collection<Sampling> samplingBetween(Machine m, Date start, Date end) {
        return (Collection<Sampling>)get(Sampling.class, start, end, m);
    }
    
    @SuppressWarnings("unchecked")
    public static Collection<JobOrder> jobOrders() {
        return (Collection<JobOrder>) get(JobOrder.class);
    }
    
    @SuppressWarnings("unchecked")
    public static Collection<User> users() {
        return (Collection<User>) get(User.class);
    }
    
    @SuppressWarnings("unchecked")
    public static Collection<Client> clients() {
        return (Collection<Client>) get(Client.class);
    }
    
    @SuppressWarnings("unchecked")
    public static Collection<Machine> machines() {
        return (Collection<Machine>) get(Machine.class);
    }
    
    public static Collection<AssignedJobOrder> setAssignedJobOrderAttr(Collection<AssignedJobOrder> l, boolean editable) {
        for(AssignedJobOrder aj : l) {
            Long lastInMinutes = EventUtils.getLast(aj);
            Long missingHours = lastInMinutes / 60, missingMinutes = lastInMinutes % 60;
            
            aj.setTitle(aj.getJobOrder().getClient().getCode() +  
                        " - " +  aj.getJobOrder().getId() + "\n" +
                        aj.getJobOrder().getDescription() + "\n" +
                        missingHours + " ore" + (
                                missingMinutes > 0
                                ? " e " + missingMinutes + " minuti"
                                : "")
                       );
            aj.setAllDay(lastInMinutes == EventUtils.getMaxLastForEventDay(aj));
            aj.setColor(aj.getJobOrder().getColor());
            aj.setEditable(editable);
        }
        return l;
    }
    
    public static Collection<Sampling> setSamplingAttr(Collection<Sampling> l, boolean editable) {
        for(Sampling s : l) {
            Long lastInMinutes = EventUtils.getLast(s);
            Long missingHours = lastInMinutes / 60, missingMinutes = lastInMinutes % 60;
            
            s.setTitle("CAMPIONAMENTO: " +  s.getClient().getCode() + "\n" +
                        s.getDescription() + "\n" +
                        missingHours + " ore" + (
                                missingMinutes > 0
                                ? " e " + missingMinutes + " minuti"
                                : "")
                       );
            s.setAllDay(lastInMinutes == EventUtils.getMaxLastForEventDay(s));
            s.setEditable(editable);
        }
        return l;
    }
    
    @SuppressWarnings("unchecked")
    public static Collection<AssignedJobOrder> assignedJobOrders(boolean editable) {
        return setAssignedJobOrderAttr((Collection<AssignedJobOrder>) get(AssignedJobOrder.class), editable);
    }
    
    @SuppressWarnings("unchecked")
    public static Collection<Sampling> sampling(boolean editable) {
        return setSamplingAttr((Collection<Sampling>) get(Sampling.class), editable);
    }
  
    @SuppressWarnings("unchecked")
    public static Collection<History> histories() {
        Collection<History> histories = (Collection<History>) get(History.class);
        for (History h : histories) {
            h.setDateTime();
        }
        return histories;
    }
    
    @SuppressWarnings("unchecked")
	public static Collection<JobOrder> todoJobOrders(Boolean editable) {
        Session session = HibernateUtils.getSessionFactory().openSession();

        Query q = session.createQuery("from JobOrder where missingTime <> 0");
        
        List<JobOrder> ret = q.list();
        session.close();
        return ret;
    }
    
    public static Collection<MachineEvent> machineEventsTheSameDayOf(GlobalEvent e) {
        Collection<MachineEvent> ret = new HashSet<MachineEvent>();
        ret.addAll(samplingTheSameDayOf(e));
        ret.addAll(assignedJobOrdersTheSameDayOf(e));
        ret.addAll(maintenanceTheSameDayOf(e));
        return ret;
    }
    
    public static Collection<GlobalEvent> globalEventsTheSameDayOf(GlobalEvent e) {
        Collection<GlobalEvent> ret = new HashSet<GlobalEvent>();
        ret.addAll(nonWorkingDaysTheSameDayOf(e));
        return ret;
    }
    
    
    public static Collection<DroppableMachineEvent> machineEventsTheSameDayOf(MachineEvent e) {
        Collection<DroppableMachineEvent> ret = new HashSet<DroppableMachineEvent>();
        // The same day of
        ret.addAll(samplingTheSameDayOf(e));
        ret.addAll(assignedJobOrdersTheSameDayOf(e));
        ret.addAll(maintenanceTheSameDayOf(e));
        return ret;
    }
    
    public static Collection<DroppableMachineEvent> machineEventsInConflictWith(MachineEvent e) {
        Collection<DroppableMachineEvent> ret = new HashSet<DroppableMachineEvent>();
        Collection<DroppableMachineEvent> machineEventsTheSameDay = machineEventsTheSameDayOf(e);
        
        Long last = EventUtils.getLast(e),
             hoursPerDay = EventUtils.getMaxLastForEventDay(e);
        if(last >= hoursPerDay) {
            ret =  machineEventsTheSameDay;
        } else {
            Long sumOfLast = 0L;
            for(MachineEvent ev : machineEventsTheSameDay) {
                sumOfLast += EventUtils.getLast(ev);
            }
            if(sumOfLast + last > hoursPerDay) {
                ret = machineEventsTheSameDay;
            }
        }
        return ret;
    }
    
    public static Collection<GlobalEvent> globalEventsTheSameDayOf(MachineEvent e) {
        Collection<GlobalEvent> ret = new HashSet<GlobalEvent>();
        ret.addAll(nonWorkingDaysTheSameDayOf(e));
        return ret;
    }
    
    public static Collection<Event> eventsTheSameDayOf(GlobalEvent e) {
        Collection<Event> ret = new HashSet<Event>();
        ret.addAll(globalEventsTheSameDayOf(e));
        ret.addAll(machineEventsTheSameDayOf(e));
        return ret;
    }
    
    public static Collection<Event> eventsTheSameDayOf(MachineEvent e) {
        Collection<Event> ret = new HashSet<Event>();
        ret.addAll(globalEventsTheSameDayOf(e));
        ret.addAll(machineEventsInConflictWith(e));
        return ret;
    }
    
    public static Collection<MachineEvent> machineEventsAfter(GlobalEvent e) {
        Collection<MachineEvent> ret = new HashSet<MachineEvent>();
        ret.addAll(samplingAfterEvent(e));
        ret.addAll(assignedJobOrdersAfterEvent(e));
        ret.addAll(maintenanceAfterEvent(e));
        return ret;
    }
    
    public static Collection<MachineEvent> machineEventsAfter(MachineEvent e) {
        Collection<MachineEvent> ret = new HashSet<MachineEvent>();
        ret.addAll(samplingAfterEvent(e));
        ret.addAll(assignedJobOrdersAfterEvent(e));
        ret.addAll(maintenanceAfterEvent(e));
        return ret;
    }
    
    public static Collection<GlobalEvent> globalEventsAfter(GlobalEvent e) {
        Collection<GlobalEvent> ret = new HashSet<GlobalEvent>();
        ret.addAll(nonWorkingDaysAfterEvent(e));
        return ret;
    }
    
    public static Collection<GlobalEvent> globalEventsAfter(MachineEvent e) {
        Collection<GlobalEvent> ret = new HashSet<GlobalEvent>();
        ret.addAll(nonWorkingDaysAfterEvent(e));
        return ret;
    }
    
    public static Collection<Event> eventsAfter(GlobalEvent e) {
        Collection<Event> ret = new HashSet<Event>();
        ret.addAll(globalEventsAfter(e));
        ret.addAll(machineEventsAfter(e));
        return ret;
    }
    
    public static Collection<Event> eventsAfter(MachineEvent e) {
        Collection<Event> ret = new HashSet<Event>();
        ret.addAll(globalEventsAfter(e));
        ret.addAll(machineEventsAfter(e));
        return ret;
    }
    
}