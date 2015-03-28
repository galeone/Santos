package com.viaagnolettisrl;

import java.util.List;
import java.util.Set;

import org.hibernate.Query;
import org.hibernate.Session;

import com.viaagnolettisrl.hibernate.Client;
import com.viaagnolettisrl.hibernate.HibernateUtil;
import com.viaagnolettisrl.hibernate.User;

public class GetList {
	private static List Get(String entity) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Query q = session.createQuery("from "+ entity);
		List ret = q.list();
		session.close();
		return ret;
	}

	@SuppressWarnings("unchecked")
	public static List<User> Users() {
		return (List<User>)Get("User");
	}
	
	@SuppressWarnings("unchecked")
	public static List<Client> Clients() {
		return (List<Client>)Get("Client");
	}

}
