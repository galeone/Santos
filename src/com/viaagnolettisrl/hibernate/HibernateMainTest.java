package com.viaagnolettisrl.hibernate;

import java.util.Calendar;
import java.util.HashSet;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class HibernateMainTest {
	public static void main(String[] args) {
		// Insert entries
		Transaction tx = null;
		Session session = HibernateUtil.getSessionFactory().openSession();
		try {
			tx = session.beginTransaction();
			Calendar cal = null;

			User a0 = new User();
			a0.setName("Name9");
			a0.setCanAddJobOrder(true);
			a0.setCanAddClient(true);
			a0.setCanAddMachine(true);
			a0.setUsername("Username26");
			a0.setPassword("Password28");
			a0.setSurname("Surname22");
			a0.setIsAdmin(true);
			session.saveOrUpdate(a0);

			User a1 = new User();
			a1.setName("Name26");
			a1.setCanAddJobOrder(true);
			a1.setCanAddClient(true);
			a1.setCanAddMachine(true);
			a1.setUsername("Username34");
			a1.setPassword("Password36");
			a1.setSurname("Surname39");
			a1.setIsAdmin(false);
			session.saveOrUpdate(a1);

			Client b0 = new Client();
			b0.setName("Name2");
			b0.setCode("Code5");
			session.saveOrUpdate(b0);

			Client b1 = new Client();
			b1.setName("Name7");
			b1.setCode("Code10");
			session.saveOrUpdate(b1);

			Machine c0 = new Machine();
			c0.setName("Name5");
			c0.setType("Type7");
			c0.setNicety(9F);
			c0.setColor("Color12");
			session.saveOrUpdate(c0);

			Machine c1 = new Machine();
			c1.setName("Name14");
			c1.setType("Type16");
			c1.setNicety(18F);
			c1.setColor("Color21");
			session.saveOrUpdate(c1);

			JobOrder d0 = new JobOrder();
			d0.setIdClient(b0.getId());
			d0.setLeadTime(20L);
			session.saveOrUpdate(d0);

			JobOrder d1 = new JobOrder();
			d1.setIdClient(b1.getId());
			d1.setLeadTime(23L);
			session.saveOrUpdate(d1);

			History e0 = new History();
			cal = Calendar.getInstance();
			cal.set(2014,Calendar.JUNE,21);
			e0.setTime(cal.getTime());
			e0.setAction("Action5");
			e0.setWhat("What7");
			e0.setIdUser(a0.getId());
			session.saveOrUpdate(e0);

			History e1 = new History();
			cal = Calendar.getInstance();
			cal.set(2014,Calendar.JUNE,20);
			e1.setTime(cal.getTime());
			e1.setAction("Action12");
			e1.setWhat("What14");
			e1.setIdUser(a1.getId());
			session.saveOrUpdate(e1);

			//Relations

			a0.setHistory(new HashSet<History>());
			a0.getHistory().add(e0);
			a0.getHistory().add(e1);
			session.saveOrUpdate(a0);

			b0.setJobOrders(new HashSet<JobOrder>());
			b0.getJobOrders().add(d0);
			b0.getJobOrders().add(d1);
			session.saveOrUpdate(b0);


			//Set property (inverse="false") makes hibernate generate insert queries on the join table when saving the set
			d0.setMachines(new HashSet<Machine>());
			d0.getMachines().add(c0);
			d0.getMachines().add(c1);
			session.saveOrUpdate(d0);

			d1.setMachines(new HashSet<Machine>());
			d1.getMachines().add(c0);
			session.saveOrUpdate(d1);

			session.saveOrUpdate(c0);
			session.saveOrUpdate(c1);
			tx.commit();
		} catch(Exception e1) {
			if (tx != null) {
				try {
					tx.rollback();
				} catch(Exception e2){
					e2.printStackTrace();
				}
			}
			e1.printStackTrace();
		} finally {
			session.close();
		}
	}
}