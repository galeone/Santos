package it.galeone_dev.santos.servlet;

import it.galeone_dev.santos.hibernate.HibernateUtils;
import it.galeone_dev.santos.hibernate.models.User;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.hibernate.Query;
import org.hibernate.Session;

import java.security.InvalidParameterException;
import java.util.Map;

public class LoginServlet extends HttpServlet {
    
    private static final long serialVersionUID = 7437071575710203911L;
    public static final String USER = "user", LOGIN_NEXT = "/pages/home.jsp", LOGIN_FORM = "/pages/login.jsp";
    
    @Override
    public void init() throws ServletException {
    }
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        
        HttpSession session = req.getSession(true);
        User user = (User) session.getAttribute(USER);
        
        if (user != null) { // already logged in
            // change url
            resp.sendRedirect(req.getContextPath() + LOGIN_NEXT);
            return;
        } else {
            session.setAttribute(USER, null);
        }
        try {
            Map<String, String> params = ServletUtils.getParameters(req, new String[] { "username", "password" });
            session.setAttribute(USER, check(params.get("username"), params.get("password")));
            
            user = (User) session.getAttribute(USER);
            if (user == null) {
                resp.sendRedirect(req.getContextPath() + LOGIN_FORM + "?failed=1&next=" + LOGIN_NEXT);
            } else { // change url
                resp.sendRedirect(req.getContextPath() + LOGIN_NEXT);
            }
        } catch (InvalidParameterException e) {
            resp.getOutputStream().print(e.getMessage());
        }
    }
    
    private User check(String username, String password) {
        Session session = HibernateUtils.getSessionFactory().openSession();
        Query q = session.createQuery("from User where username = :username and password = :password")
                .setString("password", password).setString("username", username);
        User ret = (User) q.uniqueResult();
        session.close();
        return ret;
    }
}