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
import com.viaagnolettisrl.hibernate.HibernateUtil;
import com.viaagnolettisrl.hibernate.History;
import com.viaagnolettisrl.hibernate.JobOrder;
import com.viaagnolettisrl.hibernate.Machine;
import com.viaagnolettisrl.hibernate.NonWorkingDay;
import com.viaagnolettisrl.hibernate.SamplingDay;
import com.viaagnolettisrl.hibernate.User;

public class GetCollection {
    @SuppressWarnings("rawtypes")
    private static Collection Get(String entity) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Query q = session.createQuery("from " + entity);
        List ret = q.list();
        session.close();
        return ret;
    }
    
    @SuppressWarnings("unchecked")
    public static Collection<AssignedJobOrder> assignedJobOrdersInConflictWith(SamplingDay s) {
        Session session = HibernateUtil.getSessionFactory().openSession();

        Calendar cal = Calendar.getInstance();
        cal.setTime(s.getStart());
        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        
        Date begin = cal.getTime();
        cal.add(Calendar.DATE, 1);
        Date end = cal.getTime();
        
        Query q = session.createQuery("from AssignedJobOrder where starts between :begin AND :end").
                setDate("begin", begin).setDate("end", end);
        List<AssignedJobOrder> ret = (List<AssignedJobOrder>)q.list();
        session.close();
        return ret;
    }
    
    @SuppressWarnings("unchecked")
    public static Collection<AssignedJobOrder> assignedJobOrdersAfterSampling(SamplingDay s) {
        Session session = HibernateUtil.getSessionFactory().openSession();

        Calendar cal = Calendar.getInstance();
        cal.setTime(s.getStart());
        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        
        Date begin = cal.getTime();
        
        Query q = session.createQuery("from AssignedJobOrder where starts > :begin").
                setDate("begin", begin);
        List<AssignedJobOrder> ret = (List<AssignedJobOrder>)q.list();
        session.close();
        return ret;
    }
    
    @SuppressWarnings("unchecked")
    public static Collection<NonWorkingDay> nonWorkingDaysAfterSampling(SamplingDay s) {
        Session session = HibernateUtil.getSessionFactory().openSession();

        Calendar cal = Calendar.getInstance();
        cal.setTime(s.getStart());
        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        
        Date begin = cal.getTime();
        
        Query q = session.createQuery("from NonWorkingDay where starts > :begin").setDate("begin", begin);
        List<NonWorkingDay> ret = (List<NonWorkingDay>)q.list();
        session.close();
        return ret;
    }
    
    @SuppressWarnings("unchecked")
    public static Collection<SamplingDay> samplingDaysAfterSampling(SamplingDay s) {
        Session session = HibernateUtil.getSessionFactory().openSession();

        Calendar cal = Calendar.getInstance();
        cal.setTime(s.getStart());
        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        
        Date begin = cal.getTime();
        
        Query q = session.createQuery("from SamplingDay where starts > :begin").setDate("begin", begin);
        List<SamplingDay> ret = (List<SamplingDay>)q.list();
        session.close();
        return ret;
    }
    
    public static Collection<Map.Entry<JobOrder, Long>> notCompletelyAssignedJobOrders(User user) {
        Collection<JobOrder> joborders = JobOrders();
        Map<JobOrder, Long> map = new HashMap<JobOrder, Long>();
        
        for (JobOrder j : joborders) {
            map.put(j, j.getLeadTime());
        }
        
        Collection<AssignedJobOrder> ajo = AssignedJobOrders(user);
        
        for (AssignedJobOrder a : ajo) {
            JobOrder j = a.getJobOrder();
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
            aj.setTitle("Commessa " + aj.getJobOrder().getId() + "\n" + aj.getLast() + " ore");
            aj.setOverlap(!aj.getLast().equals(24L));
            aj.setAllDay(aj.getLast().equals(24L));
            aj.setColor(aj.getMachine().getColor());
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
    
    
    public static Collection<NonWorkingDay> NonWorkingDays(Boolean editable) {
        Collection<NonWorkingDay> l = NonWorkingDays();
        for(NonWorkingDay nw : l) {
            nw.editable = editable;
        }
        return l;
    }
    
    @SuppressWarnings("unchecked")
    public static Collection<SamplingDay> SamplingDays() {
        return (Collection<SamplingDay>) Get("SamplingDay");
    }
    
    
    public static Collection<SamplingDay> SamplingDays(Boolean editable) {
        Collection<SamplingDay> l = SamplingDays();
        for(SamplingDay sd : l) {
            sd.editable = editable;
        }
        return l;
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
