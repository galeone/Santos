package it.galeone_dev.hibernate;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtils {

      private static final SessionFactory sessionFactory;

      static {
          try {
              // Create the SessionFactory from hibernate.cfg.xml
              Configuration c = new Configuration().configure();
              if(c.getProperty("hibernate.connection.url") == null) {
                  System.out.println("Using h2 embedded connection string");
                  c.setProperty("hibernate.connection.url", "jdbc:h2:" + System.getProperty("user.home") + "/santos/db;MVCC=TRUE");
              } else {
                  System.out.println("Using hibernate.xml connection url");
              }
              sessionFactory = c.buildSessionFactory();
          } catch (Throwable ex) {
              // Make sure you log the exception, as it might be swallowed
              System.err.println("Initial SessionFactory creation failed." + ex);
              throw new ExceptionInInitializerError(ex);
          }
      }

      public static SessionFactory getSessionFactory() {
          return sessionFactory;
      }

}