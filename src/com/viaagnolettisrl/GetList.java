package com.viaagnolettisrl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.hibernate.Query;
import org.hibernate.Session;

import com.viaagnolettisrl.hibernate.AssignedJobOrder;
import com.viaagnolettisrl.hibernate.Client;
import com.viaagnolettisrl.hibernate.HibernateUtil;
import com.viaagnolettisrl.hibernate.History;
import com.viaagnolettisrl.hibernate.JobOrder;
import com.viaagnolettisrl.hibernate.Machine;
import com.viaagnolettisrl.hibernate.User;

public class GetList {
	@SuppressWarnings("rawtypes")
	private static List Get(String entity) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Query q = session.createQuery("from " + entity);
		List ret = q.list();
		session.close();
		return ret;
	}
	
	public static List<Map.Entry<JobOrder, Long>> notCompletelyAssignedJobOrders() {
		List<JobOrder> joborders = JobOrders();
		Map<JobOrder, Long> map = new HashMap<JobOrder, Long>();
		
		for(JobOrder j : joborders) {
			map.put(j, j.getLeadTime());
		}
		
		List<AssignedJobOrder> ajo = AssignedJobOrder();
		
		for(AssignedJobOrder a : ajo) {
			JobOrder j = a.getJobOrder();
			map.put(j, map.get(j) - (a.getEnds().getTime() - a.getBegins().getTime())/(1000*60*60));
		}
		Iterator<Entry<JobOrder, Long>> it = map.entrySet().iterator();
		while(it.hasNext()) {
			Entry<JobOrder, Long> e = it.next();
			if(e.getValue() <= 0L) {
				it.remove();
			}
		}
		
		return new LinkedList<Entry<JobOrder, Long>>(map.entrySet());
	}

	@SuppressWarnings("unchecked")
	public static List<User> Users() {
		return (List<User>) Get("User");
	}

	@SuppressWarnings("unchecked")
	public static List<Client> Clients() {
		return (List<Client>) Get("Client");
	}

	@SuppressWarnings("unchecked")
	public static List<Machine> Machines() {
		return (List<Machine>) Get("Machine");
	}

	@SuppressWarnings("unchecked")
	public static List<JobOrder> JobOrders() {
		return (List<JobOrder>) Get("JobOrder");
	}
	
	@SuppressWarnings("unchecked")
	public static List<AssignedJobOrder> AssignedJobOrder() {
		return (List<AssignedJobOrder>) Get("AssignedJobOrder");
	}

	@SuppressWarnings("unchecked")
	public static List<History> Histories() {
		List<History> histories = (List<History>) Get("History");
		for(History h : histories) {
			h.setDateTime();
		}
		return histories;
	}

}
