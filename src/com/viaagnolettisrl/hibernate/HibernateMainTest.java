package com.viaagnolettisrl.hibernate;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Calendar;
import java.util.HashSet;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

public class HibernateMainTest {
	private static final String NAME = "name";
	private static final String CANADDJOBORDER = "canaddjoborder";
	private static final String CANADDCLIENT = "canaddclient";
	private static final String CANADDMACHINE = "canaddmachine";
	private static final String USERNAME = "username";
	private static final String PASSWORD = "password";
	private static final String ID = "id";
	private static final String SURNAME = "surname";
	private static final String ISADMIN = "isadmin";
	private static final String CODE = "code";
	private static final String COLOR = "color";
	private static final String IDCLIENT = "idclient";
	private static final String LEADTIME = "leadtime";
	private static final String IDMACHINE = "idmachine";
	private static final String STARTINGFROM = "startingfrom";
	private static final String ASSIGNEDTIME = "assignedtime";
	private static final String IDJOBORDER = "idjoborder";
	private static final String TIME = "time";
	private static final String ACTION = "action";
	private static final String WHAT = "what";
	private static final String IDUSER = "iduser";
	private static final String TABLE_USERS =  "users";
	private static final String DROP_TABLE_USERS = "DROP TABLE " + TABLE_USERS;
	private static final String CREATE_TABLE_USERS = "CREATE TABLE " + TABLE_USERS + " ( " + 
			NAME+ "  VARCHAR(50) NOT NULL, " +
			CANADDJOBORDER+ " SMALLINT NOT NULL, " +
			CANADDCLIENT+ " SMALLINT NOT NULL, " +
			CANADDMACHINE+ " SMALLINT NOT NULL, " +
			USERNAME+ "  VARCHAR(50) NOT NULL, " +
			PASSWORD+ "  VARCHAR(50) NOT NULL, " +
			ID+ " BIGINT NOT NULL PRIMARY KEY, " +
			SURNAME+ "  VARCHAR(50) NOT NULL, " +
			ISADMIN+ " SMALLINT NOT NULL, " +
			" UNIQUE ( USERNAME ))";

	private static final String TABLE_CLIENTS =  "clients";
	private static final String DROP_TABLE_CLIENTS = "DROP TABLE " + TABLE_CLIENTS;
	private static final String CREATE_TABLE_CLIENTS = "CREATE TABLE " + TABLE_CLIENTS + " ( " + 
			NAME+ "  VARCHAR(50) NOT NULL, " +
			ID+ " BIGINT NOT NULL PRIMARY KEY, " +
			CODE+ "  VARCHAR(50) NOT NULL, " +
			" UNIQUE ( CODE ))";

	private static final String TABLE_MACHINES =  "machines";
	private static final String DROP_TABLE_MACHINES = "DROP TABLE " + TABLE_MACHINES;
	private static final String CREATE_TABLE_MACHINES = "CREATE TABLE " + TABLE_MACHINES + " ( " + 
			NAME+ "  VARCHAR(50) NOT NULL, " +
			ID+ " BIGINT NOT NULL PRIMARY KEY, " +
			COLOR+ "  VARCHAR(50) NOT NULL, " +
			" UNIQUE ( NAME ))";

	private static final String TABLE_JOBORDERS =  "joborders";
	private static final String DROP_TABLE_JOBORDERS = "DROP TABLE " + TABLE_JOBORDERS;
	private static final String CREATE_TABLE_JOBORDERS = "CREATE TABLE " + TABLE_JOBORDERS + " ( " + 
			IDCLIENT+ " BIGINT NOT NULL REFERENCES Clients, " +
			ID+ " BIGINT NOT NULL PRIMARY KEY, " +
			LEADTIME+ " BIGINT NOT NULL)";

	private static final String TABLE_ASSIGNEDJOBORDERS =  "assignedjoborders";
	private static final String DROP_TABLE_ASSIGNEDJOBORDERS = "DROP TABLE " + TABLE_ASSIGNEDJOBORDERS;
	private static final String CREATE_TABLE_ASSIGNEDJOBORDERS = "CREATE TABLE " + TABLE_ASSIGNEDJOBORDERS + " ( " + 
			IDMACHINE+ " BIGINT NOT NULL REFERENCES Machines, " +
			STARTINGFROM+ " DATE NOT NULL, " +
			ASSIGNEDTIME+ " BIGINT NOT NULL, " +
			IDJOBORDER+ " BIGINT NOT NULL REFERENCES JobOrders, " +
			" PRIMRY KEY ( IDJOBORDER, IDMACHINE ))";

	private static final String TABLE_HISTORY =  "history";
	private static final String DROP_TABLE_HISTORY = "DROP TABLE " + TABLE_HISTORY;
	private static final String CREATE_TABLE_HISTORY = "CREATE TABLE " + TABLE_HISTORY + " ( " + 
			TIME+ " DATE NOT NULL, " +
			ACTION+ "  VARCHAR(50) NOT NULL, " +
			WHAT+ "  VARCHAR(50) NOT NULL, " +
			IDUSER+ " BIGINT NOT NULL REFERENCES Users, " +
			ID+ " BIGINT NOT NULL PRIMARY KEY)";

	public static void main(String[] args) {

		SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
		Session session = sessionFactory.openSession();
		Transaction tx = null;

		try { // Table creation
			//Unibo Intranet DB2 tw_stud connection
			//Class.forName("COM.ibm.db2.jdbc.app.DB2Driver").newInstance();
			//String url = "jdbc:db2:tw_stud";

			//Remote DB2 tw_stud connection
			Class.forName("com.ibm.db2.jcc.DB2Driver");
			String url = "jdbc:db2://diva.deis.unibo.it:50000/tw_stud";

			String username = "user";
			String password = "pass";

			Connection conn = DriverManager.getConnection(url, username, password);
			Statement st = conn.createStatement();

			try { //Try to execute sql
				System.out.println("Executing: " +DROP_TABLE_USERS);
				st.executeUpdate(DROP_TABLE_USERS);
			} catch(Exception e) {} //Table doesn't exist

			try { //Try to execute sql
				System.out.println("Executing: " +DROP_TABLE_CLIENTS);
				st.executeUpdate(DROP_TABLE_CLIENTS);
			} catch(Exception e) {} //Table doesn't exist

			try { //Try to execute sql
				System.out.println("Executing: " +DROP_TABLE_MACHINES);
				st.executeUpdate(DROP_TABLE_MACHINES);
			} catch(Exception e) {} //Table doesn't exist

			try { //Try to execute sql
				System.out.println("Executing: " +DROP_TABLE_JOBORDERS);
				st.executeUpdate(DROP_TABLE_JOBORDERS);
			} catch(Exception e) {} //Table doesn't exist

			try { //Try to execute sql
				System.out.println("Executing: " +DROP_TABLE_ASSIGNEDJOBORDERS);
				st.executeUpdate(DROP_TABLE_ASSIGNEDJOBORDERS);
			} catch(Exception e) {} //Table doesn't exist

			try { //Try to execute sql
				System.out.println("Executing: " +DROP_TABLE_HISTORY);
				st.executeUpdate(DROP_TABLE_HISTORY);
			} catch(Exception e) {} //Table doesn't exist


			System.out.println("Executing: " +CREATE_TABLE_USERS);
			st.executeUpdate(CREATE_TABLE_USERS);
			System.out.println("Executing: " +CREATE_TABLE_CLIENTS);
			st.executeUpdate(CREATE_TABLE_CLIENTS);
			System.out.println("Executing: " +CREATE_TABLE_MACHINES);
			st.executeUpdate(CREATE_TABLE_MACHINES);
			System.out.println("Executing: " +CREATE_TABLE_JOBORDERS);
			st.executeUpdate(CREATE_TABLE_JOBORDERS);
			System.out.println("Executing: " +CREATE_TABLE_ASSIGNEDJOBORDERS);
			st.executeUpdate(CREATE_TABLE_ASSIGNEDJOBORDERS);
			System.out.println("Executing: " +CREATE_TABLE_HISTORY);
			st.executeUpdate(CREATE_TABLE_HISTORY);
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			session.close();
		}

		// Init session
		sessionFactory = new Configuration().configure().buildSessionFactory();
		session = sessionFactory.openSession();
		tx = null;

		// Insert entries
		try {
			session = sessionFactory.openSession();
			tx = session.beginTransaction();
			Calendar cal = null;

			User a0 = new User();
			a0.setName("Name17");
			a0.setCanAddJobOrder(false);
			a0.setCanAddClient(false);
			a0.setCanAddMachine(false);
			a0.setUsername("Username25");
			a0.setPassword("Password27");
			a0.setSurname("Surname30");
			a0.setIsAdmin(true);
			session.saveOrUpdate(a0);

			User a1 = new User();
			a1.setName("Name34");
			a1.setCanAddJobOrder(true);
			a1.setCanAddClient(true);
			a1.setCanAddMachine(true);
			a1.setUsername("Username42");
			a1.setPassword("Password44");
			a1.setSurname("Surname47");
			a1.setIsAdmin(false);
			session.saveOrUpdate(a1);

			Client b0 = new Client();
			b0.setName("Name17");
			b0.setCode("Code20");
			session.saveOrUpdate(b0);

			Client b1 = new Client();
			b1.setName("Name22");
			b1.setCode("Code25");
			session.saveOrUpdate(b1);

			Machine c0 = new Machine();
			c0.setName("Name5");
			c0.setColor("Color8");
			session.saveOrUpdate(c0);

			Machine c1 = new Machine();
			c1.setName("Name10");
			c1.setColor("Color13");
			session.saveOrUpdate(c1);

			JobOrder d0 = new JobOrder();
			d0.setIdClient(b0.getId());
			d0.setLeadTime(11L);
			session.saveOrUpdate(d0);

			JobOrder d1 = new JobOrder();
			d1.setIdClient(b1.getId());
			d1.setLeadTime(14L);
			session.saveOrUpdate(d1);

			History e0 = new History();
			cal = Calendar.getInstance();
			cal.set(2014,Calendar.JUNE,27);
			e0.setTime(cal.getTime());
			e0.setAction("Action20");
			e0.setWhat("What22");
			e0.setIdUser(a0.getId());
			session.saveOrUpdate(e0);

			History e1 = new History();
			cal = Calendar.getInstance();
			cal.set(2014,Calendar.JUNE,27);
			e1.setTime(cal.getTime());
			e1.setAction("Action27");
			e1.setWhat("What29");
			e1.setIdUser(a1.getId());
			session.saveOrUpdate(e1);

			//Relations


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
		session = sessionFactory.openSession();

		try {}
		catch (Exception e1) {
			e1.printStackTrace();
		} finally {
			session.close();
		}
	}
}