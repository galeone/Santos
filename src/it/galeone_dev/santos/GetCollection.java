package it.galeone_dev.santos;

import it.galeone_dev.santos.hibernate.HibernateUtils;
import it.galeone_dev.santos.hibernate.abstractions.DroppableMachineEvent;
import it.galeone_dev.santos.hibernate.abstractions.Event;
import it.galeone_dev.santos.hibernate.abstractions.EventUtils;
import it.galeone_dev.santos.hibernate.abstractions.GlobalEvent;
import it.galeone_dev.santos.hibernate.abstractions.MachineEvent;
import it.galeone_dev.santos.hibernate.models.AssignedJobOrder;
import it.galeone_dev.santos.hibernate.models.Client;
import it.galeone_dev.santos.hibernate.models.History;
import it.galeone_dev.santos.hibernate.models.JobOrder;
import it.galeone_dev.santos.hibernate.models.Machine;
import it.galeone_dev.santos.hibernate.models.Maintenance;
import it.galeone_dev.santos.hibernate.models.NonWorkingDay;
import it.galeone_dev.santos.hibernate.models.Sampling;
import it.galeone_dev.santos.hibernate.models.User;
import it.galeone_dev.santos.hibernate.models.WorkingDay;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
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
                (conflict ? "(id <> :id)" : "1=1") +
                (after || between ? " ORDER BY starts ASC" : ""));
        
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
    public static Collection<WorkingDay> workingDay() {
        return (Collection<WorkingDay>) setWorkingDayAttr(get(WorkingDay.class), false);
    }
    
    public static Collection<WorkingDay> workingDay(Boolean editable) {
        Collection<WorkingDay> l = workingDay();
        return setWorkingDayAttr(l, editable);
    }
    
	public static Collection<WorkingDay> workingDaysBetween(Boolean editable, Date start, Date end) {
    	Collection<WorkingDay> ret = new LinkedList<WorkingDay>();
    	AssignedJobOrder dummy =  new AssignedJobOrder();
    	dummy.setStart(new Date(start.getTime()));
    	dummy.setEnd(new Date(end.getTime()));
    	
    	while(dummy.getStart().before(dummy.getEnd())) {
            ret.add(WorkingDay.get(dummy.getStart()));
    		dummy.setStart(EventUtils.tomorrow(dummy.getStart()));
    	}

    	return setWorkingDayAttr(ret, editable);
    }

    public static Collection<WorkingDay> workingDaysBetween(Date start, Date end) {
        return (Collection<WorkingDay>)workingDaysBetween(false, start, end);
    }
    
    @SuppressWarnings("unchecked")
    public static Collection<WorkingDay> workingDaysAfterEvent(Event e) {
        return (Collection<WorkingDay>)setWorkingDayAttr(get(WorkingDay.class, e.getStart()), false); 
    }
    
    public static Collection<WorkingDay> workingDaysAfterEvent(GlobalEvent e) {
        return workingDaysAfterEvent((Event)e);
    }
    
    public static Collection<WorkingDay> workingDaysAfterEvent(MachineEvent e) {
        return workingDaysAfterEvent((Event)e);
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
    public static Collection<Maintenance> maintenance(boolean editable) {
        return setMaintenanceAttr((Collection<Maintenance>) get(Maintenance.class), editable);
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
    public static Collection<Sampling> sampling(boolean editable) {
        return setSamplingAttr((Collection<Sampling>) get(Sampling.class), editable);
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
        List<Machine> sortedMach = new ArrayList<Machine>((Collection<Machine>)get(Machine.class));
        Collections.sort(sortedMach, new Comparator<Machine>() {
            @Override
            public int compare(Machine o1, Machine o2) {
                return o1.getName().compareTo(o2.getName());
            }});
        return sortedMach;
    }
    
    public static Collection<AssignedJobOrder> setAssignedJobOrderAttr(Collection<AssignedJobOrder> l, boolean editable) {
        for(AssignedJobOrder aj : l) {
            Long lastInMinutes = EventUtils.getLast(aj);
            Long missingHours = lastInMinutes / 60, missingMinutes = lastInMinutes % 60;
            
            aj.setTitle(aj.getJobOrder().getClient().getName() +  
                        " - " +  aj.getJobOrder().getId() + "\n" +
                        aj.getJobOrder().getDescription() + "\n" +
                        missingHours + " ore" + (
                                missingMinutes > 0
                                ? " e " + missingMinutes + " minuti"
                                : "")
                       );
            aj.setAllDay(false);
            aj.setColor(aj.getJobOrder().getColor());
            aj.setEditable(editable);
        }
        return l;
    }
    
    public static Collection<WorkingDay> setWorkingDayAttr(Collection<WorkingDay> l, boolean editable) {
    	Long i = 1L; // negative id for non persistente working hours
        for(WorkingDay wh : l) {
            Long lastInMinutes = EventUtils.getLast(wh);
            Long missingHours = lastInMinutes / 60, missingMinutes = lastInMinutes % 60;
            
            if(wh.getId() == null) {
                wh.setId(-i);
                ++i;
            }
            
            wh.setTitle(missingHours + " ore" +
            		(
            				missingMinutes > 0
                            ? " e " + missingMinutes + " minuti"
                            : ""
                    ) + (wh.getId() < 0L ? " [D]" : ""));

            wh.setAllDay(true);
            wh.setEditable(editable);
        }
        return l;
    }
    
    public static Collection<Sampling> setSamplingAttr(Collection<Sampling> l, boolean editable) {
        for(Sampling s : l) {
            Long lastInMinutes = EventUtils.getLast(s);
            Long missingHours = lastInMinutes / 60, missingMinutes = lastInMinutes % 60;
            
            s.setTitle("CAMPIONAMENTO\n" +  s.getClient().getName() + "\n" +
                        s.getDescription() + "\n" +
                        missingHours + " ore" + (
                                missingMinutes > 0
                                ? " e " + missingMinutes + " minuti"
                                : "")
                       );
            s.setAllDay(false);
            s.setEditable(editable);
            s.setDateTime();
            s.setLeadTime();
        }
        return l;
    }
    
    public static Collection<Maintenance> setMaintenanceAttr(Collection<Maintenance> l, boolean editable) {
        for(Maintenance s : l) {
            Long lastInMinutes = EventUtils.getLast(s);
            Long missingHours = lastInMinutes / 60, missingMinutes = lastInMinutes % 60;
            
            s.setTitle("MANUTENZIONE\n" +  s.getDescription() + "\n" +
                        missingHours + " ore" + (
                                missingMinutes > 0
                                ? " e " + missingMinutes + " minuti"
                                : "")
                       );
            s.setAllDay(lastInMinutes == EventUtils.getLast(WorkingDay.get((s.getStart()))));
            s.setEditable(editable);
        }
        return l;
    }
    
    @SuppressWarnings("unchecked")
    public static Collection<AssignedJobOrder> assignedJobOrders(boolean editable) {
        return setAssignedJobOrderAttr((Collection<AssignedJobOrder>) get(AssignedJobOrder.class), editable);
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

        Query q = session.createQuery("from JobOrder where missingTimeWithOffset <> 0");
        
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
        ret.add(WorkingDay.get(e.getStart()));
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
        
        Long last = EventUtils.getLast(e), hoursPerDay = EventUtils.getLast(WorkingDay.get(e.getStart()));
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
    
    public static Collection<AssignedJobOrder> assignedJobOrdersnConflictWith(MachineEvent e) {
        Collection<AssignedJobOrder> ret = new HashSet<AssignedJobOrder>();
        Collection<AssignedJobOrder> machineEventsTheSameDay = assignedJobOrdersTheSameDayOf(e);
        
        Long last = EventUtils.getLast(e), hoursPerDay = EventUtils.getLast(WorkingDay.get(e.getStart()));
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
    
    public static Collection<GlobalEvent> globalEventsBetween(Date start, Date end) {
    	Collection<GlobalEvent> ret = new HashSet<GlobalEvent>();
    	ret.addAll(nonWorkingDaysBetween(start, end));
    	ret.addAll(workingDaysBetween(start, end));
    	return ret;
    }
    
    public static Collection<GlobalEvent> globalEventsBetween(boolean editable, Date start, Date end) {
        Collection<GlobalEvent> ret = new HashSet<GlobalEvent>();
        ret.addAll(nonWorkingDaysBetween(editable, start, end));
        ret.addAll(workingDaysBetween(editable, start, end));
        return ret;
    }
    
    public static Collection<MachineEvent> machineEventsBetween(Date start, Date end) {
    	Collection<MachineEvent> ret = new HashSet<MachineEvent>();
    	ret.addAll(samplingBetween(start, end));
    	ret.addAll(assignedJobOrdersBetween(start, end));
    	ret.addAll(maintenanceBetween(start, end));
    	return ret;
    }
    
    public static Collection<GlobalEvent> globalEventsTheSameDayOf(MachineEvent e) {
        Collection<GlobalEvent> ret = new HashSet<GlobalEvent>();
        ret.addAll(nonWorkingDaysTheSameDayOf(e));
        ret.add(WorkingDay.get(e.getStart()));
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
    
    public static Collection<Event> eventsBetween(Date start, Date end) {
    	Collection<Event> ret = new HashSet<Event>();
    	ret.addAll(globalEventsBetween(start, end));
    	ret.addAll(machineEventsBetween(start, end));
    	return ret;
    }
    
}
