package com.viaagnolettisrl;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        return new Date(cal.getTime().getTime());
    }
    
    public static Date tomorrow(Date d) {
        Calendar cal = Calendar.getInstance();
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
        Collection<JobOrder> ret = (Collection<JobOrder>) get(JobOrder.class);
        return setJobOrderAttr(ret);
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
            aj.setTitle("[" + aj.getJobOrder().getId() + "] " + aj.getJobOrder().getClient().getCode() + " - " + aj.getJobOrder().getClient().getName() + "\n" + aj.getLast() + " ore");
            aj.setAllDay(aj.getLast().equals(24L));
            aj.setColor(aj.getJobOrder().getColor());
            aj.setEditable(editable);
        }
        return l;
    }
    
    public static Collection<JobOrder> setJobOrderAttr(Collection<JobOrder> l) {
        for(JobOrder jo : l) {
            long sum = 0, missingTime;
            for(AssignedJobOrder aj : jo.getAssignedJobOrders()) {
                sum += aj.getLast();
            }
            missingTime = jo.getLeadTime() - sum;
            jo.setMissingTime(missingTime);
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
    
    public static Collection<Map.Entry<JobOrder, Long>> notCompletelyAssignedJobOrders(Boolean editable) {
        Collection<JobOrder> joborders = jobOrders();
        Map<JobOrder, Long> map = new HashMap<JobOrder, Long>();
        
        for (JobOrder j : joborders) {
            map.put(j, j.getLeadTime());
            System.out.println(j.equals(j));
        }
        
        Collection<AssignedJobOrder> ajo = assignedJobOrders(editable);
        
        for (AssignedJobOrder a : ajo) {
            JobOrder j = a.getJobOrder();
            System.out.println(map.get(j) + " " + j + " " + a.getEnd() + " " + a.getStart());
            map.put(j, map.get(j) - (a.getEnd().getTime() - a.getStart().getTime()) / (1000 * 60 * 60));
        }
        Iterator<Entry<JobOrder, Long>> it = map.entrySet().iterator();
        while (it.hasNext()) {
            Entry<JobOrder, Long> e = it.next();
            if (e.getValue() <= 0L) {
                it.remove();
            }
        }
        
        return new LinkedList<Entry<JobOrder, Long>>(map.entrySet());
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
        ret.addAll(samplingInConflictWith(e));
        ret.addAll(assignedJobOrdersInConflictWith(e));
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
