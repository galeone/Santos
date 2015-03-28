package com.viaagnolettisrl.hibernate;

import com.viaagnolettisrl.hibernate.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Calendar;
import java.util.HashSet;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class HibernateMainTest {

	public static void main(String[] args) {

		Session session = null;
		Transaction tx = null;

		// Insert entries
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			Calendar cal = null;

			User a0 = new User();
			a0.setName("Name18");
			a0.setCanAddJobOrder(true);
			a0.setCanAddClient(true);
			a0.setCanAddMachine(true);
			a0.setUsername("Username26");
			a0.setPassword("Password28");
			a0.setSurname("Surname31");
			a0.setIsAdmin(false);
			session.saveOrUpdate(a0);

			User a1 = new User();
			a1.setName("Name35");
			a1.setCanAddJobOrder(false);
			a1.setCanAddClient(false);
			a1.setCanAddMachine(false);
			a1.setUsername("Username43");
			a1.setPassword("Password45");
			a1.setSurname("Surname48");
			a1.setIsAdmin(true);
			session.saveOrUpdate(a1);

			Client b0 = new Client();
			b0.setName("GABIBBO");
			b0.setCode("Code18");
			session.saveOrUpdate(b0);

			Client b1 = new Client();
			b1.setName("Name20");
			b1.setCode("Code23");
			session.saveOrUpdate(b1);

			Machine c0 = new Machine();
			c0.setName("Name18");
			c0.setColor("Color21");
			session.saveOrUpdate(c0);

			Machine c1 = new Machine();
			c1.setName("Name23");
			c1.setColor("Color26");
			session.saveOrUpdate(c1);

			JobOrder d0 = new JobOrder();
			d0.setIdClient(b0.getId());
			d0.setLeadTime(5L);
			session.saveOrUpdate(d0);

			JobOrder d1 = new JobOrder();
			d1.setIdClient(b1.getId());
			d1.setLeadTime(8L);
			session.saveOrUpdate(d1);

			History e0 = new History();
			cal = Calendar.getInstance();
			cal.set(2014,Calendar.JUNE,12);
			e0.setTime(cal.getTime());
			e0.setAction("Action18");
			e0.setWhat("What20");
			e0.setIdUser(a0.getId());
			session.saveOrUpdate(e0);

			History e1 = new History();
			cal = Calendar.getInstance();
			cal.set(2014,Calendar.JUNE,10);
			e1.setTime(cal.getTime());
			e1.setAction("Action25");
			e1.setWhat("What27");
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

		//Queries

		//New session
		session = HibernateUtil.getSessionFactory().openSession();

		try {}
		catch (Exception e1) {
			e1.printStackTrace();
		} finally {
			session.close();
		}
	}
}