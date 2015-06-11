package it.galeone_dev.santos.servlet;

import it.galeone_dev.santos.hibernate.HibernateUtils;
import it.galeone_dev.santos.hibernate.models.User;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;
import org.hibernate.Transaction;

public class InstallServlet extends HttpServlet {
    
    private static final long serialVersionUID = 7433911L;
    
    @Override
    public void init() throws ServletException {
    }
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
        Transaction tx = null;
        Session session = HibernateUtils.getSessionFactory().openSession();
        String message = "Username: admin\nPassoword: admin.\n Now Go to change it.";
        try {
            tx = session.beginTransaction();
            User exists = (User) session.get(User.class, 1L);
            if (exists == null) {
                User a0 = new User();
                a0.setName("Santos");
                a0.setCanAddJobOrder(true);
                a0.setCanAddClient(true);
                a0.setCanAddMachine(true);
                a0.setCanAssignJobOrder(true);
                a0.setUsername("admin");
                a0.setPassword("admin");
                a0.setSurname("Santos");
                a0.setIsAdmin(true);
                session.save(a0);
            } else {
                message = "Santos already installed. Admin exists";
            }
            tx.commit();
        } catch (Exception e1) {
            message = e1.getMessage();
            if (tx != null) {
                try {
                    tx.rollback();
                } catch (Exception e2) {
                    message = e2.getMessage();
                }
            }
            e1.printStackTrace();
        } finally {
            session.close();
        }
        response.getOutputStream().println(message);
    }
    
}
