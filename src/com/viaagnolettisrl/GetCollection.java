package com.viaagnolettisrl;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
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
import com.viaagnolettisrl.hibernate.MachineEvent;
import com.viaagnolettisrl.hibernate.HibernateUtil;
import com.viaagnolettisrl.hibernate.History;
import com.viaagnolettisrl.hibernate.JobOrder;
import com.viaagnolettisrl.hibernate.Machine;
import com.viaagnolettisrl.hibernate.NonWorkingDay;
import com.viaagnolettisrl.hibernate.Sampling;
import com.viaagnolettisrl.hibernate.User;

public class GetCollection {
    @SuppressWarnings("rawtypes")
    private static Collection Get(String entity, Date start, Date end) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        boolean between = start != null && end != null;
        Query q = session.createQuery("from " + entity + (between ? " where starts between :start AND :end" : ""));
        if(between) {
            q.setDate("start", start).setDate("end", end);
        }
        
        List ret = q.list();
        session.close();
        return ret;
    }
    
    @SuppressWarnings("rawtypes")
    private static Collection Get(String entity) {
        return Get(entity, null, null);
    }
    
    @SuppressWarnings("unchecked")
    public static Collection<AssignedJobOrder> assignedJobOrdersInConflictWith(MachineEvent e) {
        Session session = HibernateUtil.getSessionFactory().openSession();

        Date start = new Date(), end = new Date();
        setStartEnd(e, start, end);
        
        Machine m = e.getMachine();
        Query q = session.createQuery("from AssignedJobOrder where starts between :start AND :end AND idmachine = :machine").
                setDate("start", start).setDate("end", end).setLong("machine", m.getId());
        
        List<AssignedJobOrder> ret = (List<AssignedJobOrder>)q.list();
        session.close();
        return ret;
    }
    
    private static void setStartEnd(Event e, Date start, Date end) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(e.getStart());
        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        
        start.setTime(cal.getTime().getTime());
        cal.add(Calendar.DATE, 1);
        end.setTime(cal.getTime().getTime());
    }
    
    @SuppressWarnings("unchecked")
    public static Collection<AssignedJobOrder> assignedJobOrdersInConflictWith(Event e) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        
        Date start = new Date(), end = new Date();
        setStartEnd(e, start, end);
        Query q = session.createQuery("from AssignedJobOrder where starts between :start AND :end").
                setDate("start", start).setDate("end", end);
        
        List<AssignedJobOrder> ret = (List<AssignedJobOrder>)q.list();
        session.close();
        return ret;
    }
    
    @SuppressWarnings("unchecked")
    public static Collection<AssignedJobOrder> assignedJobOrdersAfterEvent(Event e) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        
        Date start = new Date(), end = new Date();
        setStartEnd(e, start, end);
        Query q = session.createQuery("from AssignedJobOrder where starts > :start").
                setDate("start", start);
        List<AssignedJobOrder> ret = (List<AssignedJobOrder>)q.list();
        session.close();
        return ret;
    }
    
    @SuppressWarnings("unchecked")
    public static Collection<AssignedJobOrder> assignedJobOrdersAfterEvent(MachineEvent e) {
        Session session = HibernateUtil.getSessionFactory().openSession();

        Date start = new Date(), end = new Date();
        setStartEnd(e, start, end);
        
        Query q = session.createQuery("from AssignedJobOrder where starts > :start").
                setDate("start", start);
        List<AssignedJobOrder> ret = (List<AssignedJobOrder>)q.list();
        session.close();
        return ret;
    }
    
    @SuppressWarnings("unchecked")
    public static Collection<NonWorkingDay> nonWorkingDaysAfterEvent(Event e) {
        Session session = HibernateUtil.getSessionFactory().openSession();

        Date start = new Date(), end = new Date();
        setStartEnd(e, start, end);
        
        Query q = session.createQuery("from NonWorkingDay where starts > :start").setDate("start", start);
        List<NonWorkingDay> ret = (List<NonWorkingDay>)q.list();
        session.close();
        return ret;
    }
    
    @SuppressWarnings("unchecked")
    public static Collection<Sampling> samplingAfterEvent(MachineEvent e) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        
        Date start = new Date(), end = new Date();
        setStartEnd(e, start, end);

        Machine m = e.getMachine();
        Query q = session.createQuery("from Sampling where starts > :start AND idmachine = :machine").setDate("start", start).setLong("machine", m.getId());
        List<Sampling> ret = (List<Sampling>)q.list();
        session.close();
        return ret;
    }
    
    @SuppressWarnings("unchecked")
    public static Collection<Sampling> samplingAfterEvent(Event e) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        
        Date start = new Date(), end = new Date();
        setStartEnd(e, start, end);

        Query q = session.createQuery("from Sampling where starts > :start").setDate("start", start);
        List<Sampling> ret = (List<Sampling>)q.list();
        session.close();
        return ret;
    }
    
    public static Collection<Map.Entry<JobOrder, Long>> notCompletelyAssignedJobOrders(User user) {
        Collection<JobOrder> joborders = JobOrders();
        Map<JobOrder, Long> map = new HashMap<JobOrder, Long>();
        
        for (JobOrder j : joborders) {
            map.put(j, j.getLeadTime());
            System.out.println(j.equals(j));
        }
        
        Collection<AssignedJobOrder> ajo = AssignedJobOrders(user);
        
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
    
    @SuppressWarnings("unchecked")
    public static Collection<User> Users() {
        return (Collection<User>) Get("User");
    }
    
    @SuppressWarnings("unchecked")
    public static Collection<Client> Clients() {
        return (Collection<Client>) Get("Client");
    }
    
    @SuppressWarnings("unchecked")
    public static Collection<Machine> Machines() {
        return (Collection<Machine>) Get("Machine");
    }
    
    @SuppressWarnings("unchecked")
    public static Collection<JobOrder> JobOrders() {
        Collection<JobOrder> ret = (Collection<JobOrder>) Get("JobOrder");
        return setJobOrderAttr(ret);
    }
    
    public static Collection<AssignedJobOrder> setAssignedJobOrderAttr(Collection<AssignedJobOrder> l, User user) {
        for(AssignedJobOrder aj : l) {
            aj.setTitle("[" + aj.getJobOrder().getId() + "] " + aj.getJobOrder().getClient().getCode() + " - " + aj.getJobOrder().getClient().getName() + "\n" + aj.getLast() + " ore");
            aj.setOverlap(!aj.getLast().equals(24L));
            aj.setAllDay(aj.getLast().equals(24L));
            aj.setColor(aj.getJobOrder().getColor());
            aj.setEditable(user.getCanAddJobOrder());
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
    public static Collection<AssignedJobOrder> AssignedJobOrders(User user) {
        Collection<AssignedJobOrder> ret = (Collection<AssignedJobOrder>) Get("AssignedJobOrder");
        return setAssignedJobOrderAttr(ret, user);
    }
   
    @SuppressWarnings("unchecked")
    public static Collection<NonWorkingDay> NonWorkingDays() {
        return (Collection<NonWorkingDay>) Get("NonWorkingDay");
    }
    
    @SuppressWarnings("unchecked")
    public static Collection<NonWorkingDay> NonWorkingDays(Boolean editable, Date start, Date end) {
        Collection<NonWorkingDay> l = (Collection<NonWorkingDay>) Get("NonWorkingDay", start, end);
        for(NonWorkingDay nw : l) {
            nw.editable = editable;
        }
        return l;
    }
    
    
    public static Collection<NonWorkingDay> NonWorkingDays(Boolean editable) {
        Collection<NonWorkingDay> l = NonWorkingDays();
        for(NonWorkingDay nw : l) {
            nw.editable = editable;
        }
        return l;
    }
    
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Collection<Sampling> Sampling(Machine m) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Query q = session.createQuery("from Sampling where idmachine = :id");
        q.setLong("id", m.getId());
        List ret = q.list();
        session.close();
        return (Collection<Sampling>)ret;
    }
    
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Collection<Sampling> Sampling(Machine m, Date start, Date end) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Query q = session.createQuery("from Sampling where idmachine = :id AND starts between :start AND :end");
        q.setLong("id", m.getId()).setDate("start", start).setDate("end", end);
        
        List ret = q.list();
        session.close();
        return (Collection<Sampling>)ret;
    }
    
    @SuppressWarnings("unchecked")
    public static Collection<Sampling> Sampling() {
        return (Collection<Sampling>)Get("Sampling");
    }
    
    @SuppressWarnings("unchecked")
    public static Collection<History> Histories() {
        Collection<History> histories = (Collection<History>) Get("History");
        for (History h : histories) {
            h.setDateTime();
        }
        return histories;
    }
    
}
