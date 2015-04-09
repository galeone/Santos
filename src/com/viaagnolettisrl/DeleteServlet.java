package com.viaagnolettisrl;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.hibernate.Session;

import com.viaagnolettisrl.hibernate.AssignedJobOrder;
import com.viaagnolettisrl.hibernate.Client;
import com.viaagnolettisrl.hibernate.HibernateUtil;
import com.viaagnolettisrl.hibernate.History;
import com.viaagnolettisrl.hibernate.JobOrder;
import com.viaagnolettisrl.hibernate.Machine;
import com.viaagnolettisrl.hibernate.NonWorkingDay;
import com.viaagnolettisrl.hibernate.User;

public class DeleteServlet extends HttpServlet {
    
    private static final long serialVersionUID = 743707157203911L;
    
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
        HttpSession session = request.getSession(true);
        ServletOutputStream out = response.getOutputStream();
        User user = (User) session.getAttribute(LoginServlet.USER);
        
        if (user == null) { // not logged in
            out.print("login");
            return;
        }
        
        Map<String, String> params = ServletUtils.getParameters(request, new String[] { "what", "id" });
        String what;
        Long id;
        if ((what = params.get("what")) == null || params.get("id") == null) {
            out.println("error, invalid parameters");
            return;
        }
        
        id = Long.parseLong(params.get("id"));
        
        Session hibSession = HibernateUtil.getSessionFactory().openSession();
        hibSession.beginTransaction();
        
        Object toDelete = null;
        String message = "Impossibile eliminare. Elemento già eliminato o non esistente";
        
        switch (what) {
            case "user":
                if (!user.getIsAdmin()) {
                    message = "Non sei amministratore";
                } else {
                    toDelete = hibSession.get(User.class, id);
                    if (toDelete != null) { // exists
                        if (((User) toDelete).getId() == user.getId()) {
                            message = "Non puoi eliminare l'amministratore";
                        } else {
                            hibSession.delete((User) toDelete);
                            message = "ok";
                        }
                    }
                }
            break;
            
            case "nonworkingday":
                if (!user.getIsAdmin()) {
                    message = "Non sei amministratore";
                } else {
                    toDelete = hibSession.get(NonWorkingDay.class, id);
                    if (toDelete != null) { // exists
                        hibSession.delete((NonWorkingDay) toDelete);
                        message = "ok";
                    }
                }
            break;
            
            case "client":
                if (!user.getCanAddClient()) {
                    message = "Non puoi eliminare i clienti";
                } else {
                    toDelete = hibSession.get(Client.class, id);
                    if (toDelete != null) { // exists
                        hibSession.delete((Client) toDelete);
                        message = "ok";
                    }
                }
            break;
            
            case "machine":
                if (!user.getCanAddMachine()) {
                    message = "Non puoi eliminare le macchine";
                } else {
                    toDelete = hibSession.get(Machine.class, id);
                    if (toDelete != null) { // exists
                        hibSession.delete((Machine) toDelete);
                        message = "ok";
                    }
                }
            break;
            
            case "joborder":
                if (!user.getCanAddJobOrder()) {
                    message = "Non puoi eliminare le commesse";
                } else {
                    toDelete = hibSession.get(JobOrder.class, id);
                    if (toDelete != null) { // exists
                        hibSession.delete((JobOrder) toDelete);
                        message = "ok";
                    }
                }
            break;
            
            case "assignedjoborder":
                if (!user.getCanAddJobOrder()) {
                    message = "Non puoi eliminare le commesse";
                } else {
                    toDelete = hibSession.get(AssignedJobOrder.class, id);
                    if (toDelete != null) { // exists
                        hibSession.delete((AssignedJobOrder) toDelete);
                        message = "ok";
                    }
                }
            break;
        }
        if ("ok".equals(message)) {
            History h = new History();
            h.setAction("DELETE");
            h.setTime(new Date());
            h.setUser(user);
            h.setWhat(toDelete.toString());
            hibSession.saveOrUpdate(h);
            hibSession.getTransaction().commit();
        } else {
            hibSession.getTransaction().rollback();
        }
        out.print(message);
        
        hibSession.close();
    }
    
}
