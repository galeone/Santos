package com.viaagnolettisrl;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import com.viaagnolettisrl.hibernate.AssignedJobOrder;
import com.viaagnolettisrl.hibernate.Client;
import com.viaagnolettisrl.hibernate.Event;
import com.viaagnolettisrl.hibernate.GlobalEvent;
import com.viaagnolettisrl.hibernate.MachineEvent;
import com.viaagnolettisrl.hibernate.HibernateUtil;
import com.viaagnolettisrl.hibernate.History;
import com.viaagnolettisrl.hibernate.JobOrder;
import com.viaagnolettisrl.hibernate.Machine;
import com.viaagnolettisrl.hibernate.NonWorkingDay;
import com.viaagnolettisrl.hibernate.Sampling;
import com.viaagnolettisrl.hibernate.User;

public class GetCollection {
    
    public static Date reset(Date d) {
        Calendar cal = Calendar.getInstance(EventUtils.timezone);
        cal.setTime(d);
        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        return new Date(cal.getTime().getTime());
    }
    
    public static Date tomorrow(Date d) {
        Calendar cal = Calendar.getInstance(EventUtils.timezone);
        Date today = reset(d);
        cal.setTime(today);
        cal.add(Calendar.DATE, 1);
        return cal.getTime();
    }
    
    @SuppressWarnings("rawtypes")
    private static Collection get(Class entity, Date start, Date end, Machine m) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        boolean between = start != null && end != null,
                machine = m != null,
                after = start != null && end == null;
        
        Query q = session.createQuery("from " + entity.getSimpleName() + " where " +
                (between ? "(starts between :start AND :end)" : "1=1") + " AND " +
                (after   ? "(starts > :start)" : "1=1") + " AND " +
                (machine ? "(idmachine = :machine)" : "1=1"));
        
        if(between) {
            q.setDate("start", reset(start)).setDate("end", reset(end));
        }
        
        if(after) {
            q.setDate("start", reset(start));
        }
        
        if(machine) {
            q.setLong("machine", m.getId());
        }
        
        List ret = q.list();
        session.close();
        return ret;
    }
    
    @SuppressWarnings("rawtypes")
    private static Collection get(Class entity) {
        return get(entity, null, null,null);
    }
    
    // Machine
    @SuppressWarnings("rawtypes")
    private static Collection get(Class entity, Machine m) {
        return get(entity, null, null, m);
    }
    
    // After
    @SuppressWarnings("rawtypes")
    private static Collection get(Class entity, Date after) {
        return get(entity, after, null,null);
    }
    
    // After on machine
    @SuppressWarnings("rawtypes")
    private static Collection get(Class entity, Date after, Machine m) {
        return get(entity, after, null, m);
    }
    
    // Global conflict
    @SuppressWarnings("rawtypes")
    private static Collection get(Class entity, Date start, Date end) {
        return get(entity, start, end, null);
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
    public static Collection<AssignedJobOrder> assignedJobOrdersInConflictWith(MachineEvent e) {
        return (Collection<AssignedJobOrder>)get(AssignedJobOrder.class, e.getStart(), e.getEnd(), e.getMachine());
    }
    
    @SuppressWarnings("unchecked")
    public static Collection<AssignedJobOrder> assignedJobOrdersInConflictWith(GlobalEvent e) {
        return (Collection<AssignedJobOrder>)get(AssignedJobOrder.class, e.getStart(), e.getEnd());
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
    private static Collection<NonWorkingDay> nonWorkingDaysInConflictWith(Event e) {
        return (Collection<NonWorkingDay>) get(NonWorkingDay.class,e.getStart(), e.getEnd());
    }
    
    public static Collection<NonWorkingDay> nonWorkingDaysInConflictWith(GlobalEvent e) {
        return nonWorkingDaysInConflictWith((Event)e);
    }
    
    public static Collection<NonWorkingDay> nonWorkingDaysInConflictWith(MachineEvent e) {
        return nonWorkingDaysInConflictWith((Event)e); 
    }
    
    @SuppressWarnings("unchecked")
    private static Collection<NonWorkingDay> nonWorkingDaysAfterEvent(Event e) {
        return (Collection<NonWorkingDay>)get(NonWorkingDay.class, e.getStart()); 
    }
    
    public static Collection<NonWorkingDay> nonWorkingDaysAfterEvent(GlobalEvent e) {
        return nonWorkingDaysAfterEvent((Event)e);
    }
    
    public static Collection<NonWorkingDay> nonWorkingDaysAfterEvent(MachineEvent e) {
        return nonWorkingDaysAfterEvent((Event)e);
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
    public static Collection<Sampling> samplingInConflictWith(GlobalEvent e) {
        return (Collection<Sampling>) get(Sampling.class, e.getStart(), e.getEnd());
    }
    
    @SuppressWarnings("unchecked")
    public static Collection<Sampling> samplingInConflictWith(MachineEvent e) {
        return (Collection<Sampling>) get(Sampling.class, e.getStart(), e.getEnd(), e.getMachine());
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
            Long lastInHours = aj.getLastInHours(), remainMinutes =aj.getLastInMinutes() - lastInHours*60;
            aj.setTitle("[" + aj.getJobOrder().getId() + "] " + 
                        aj.getJobOrder().getClient().getCode() + " - " + 
                        aj.getJobOrder().getClient().getName() + "\n" +
                        lastInHours + " ore" + (
                                remainMinutes > 0
                                ? " e " + remainMinutes + " minuti"
                                : "")
                       );
            aj.setAllDay(aj.getLastInHours().equals(24L));
            aj.setColor(aj.getJobOrder().getColor());
            aj.setEditable(editable);
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
        Session session = HibernateUtil.getSessionFactory().openSession();

        Query q = session.createQuery("from JobOrder where missingTime <> 0");
        
        List<JobOrder> ret = q.list();
        session.close();
        return ret;
    }
    
    public static Collection<MachineEvent> machineEventsInConflictWith(GlobalEvent e) {
        Collection<MachineEvent> ret = new HashSet<MachineEvent>();
        ret.addAll(samplingInConflictWith(e));
        ret.addAll(assignedJobOrdersInConflictWith(e));
        return ret;
    }
    
    public static Collection<GlobalEvent> globalEventsInConflictWith(GlobalEvent e) {
        Collection<GlobalEvent> ret = new HashSet<GlobalEvent>();
        ret.addAll(nonWorkingDaysInConflictWith(e));
        return ret;
    }
    
    public static Collection<MachineEvent> machineEventsInConflictWith(MachineEvent e) {
        Collection<MachineEvent> ret = new HashSet<MachineEvent>();
        // The same day of
        Collection<Sampling> sampling = samplingInConflictWith(e);
        Collection<AssignedJobOrder> ajo = assignedJobOrdersInConflictWith(e);
        
        Long last = EventUtils.getLast(e);
        
        if(last >= 24) {
            ret.addAll(sampling);
            ret.addAll(ajo);
        } else {
            Long sumOfLast = 0L;
            for(MachineEvent ev : sampling) {
                sumOfLast += EventUtils.getLast(ev);
            }
            if(sumOfLast + last > 24) {
                ret.addAll(sampling);
                ret.addAll(ajo);
            } else {
                for(MachineEvent ev: ajo) {
                    sumOfLast += EventUtils.getLast(ev);
                }
                if(sumOfLast + last > 24) {
                    ret.addAll(sampling);
                    ret.addAll(ajo);
                }
            }
        }
        return ret;
    }
    
    public static Collection<GlobalEvent> globalEventsInConflictWith(MachineEvent e) {
        Collection<GlobalEvent> ret = new HashSet<GlobalEvent>();
        ret.addAll(nonWorkingDaysInConflictWith(e));
        return ret;
    }
    
    public static Collection<Event> eventsInConflictWith(GlobalEvent e) {
        Collection<Event> ret = new HashSet<Event>();
        ret.addAll(globalEventsInConflictWith(e));
        ret.addAll(machineEventsInConflictWith(e));
        return ret;
    }
    
    public static Collection<Event> eventsInConflictWith(MachineEvent e) {
        Collection<Event> ret = new HashSet<Event>();
        ret.addAll(globalEventsInConflictWith(e));
        ret.addAll(machineEventsInConflictWith(e));
        return ret;
    }
    
    public static Collection<MachineEvent> machineEventsAfter(GlobalEvent e) {
        Collection<MachineEvent> ret = new HashSet<MachineEvent>();
        ret.addAll(samplingAfterEvent(e));
        ret.addAll(assignedJobOrdersAfterEvent(e));
        return ret;
    }
    
    public static Collection<MachineEvent> machineEventsAfter(MachineEvent e) {
        Collection<MachineEvent> ret = new HashSet<MachineEvent>();
        ret.addAll(samplingAfterEvent(e));
        ret.addAll(assignedJobOrdersAfterEvent(e));
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
