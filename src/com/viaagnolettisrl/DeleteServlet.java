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
import org.hibernate.exception.GenericJDBCException;

import com.viaagnolettisrl.hibernate.AssignedJobOrder;
import com.viaagnolettisrl.hibernate.Client;
import com.viaagnolettisrl.hibernate.HibernateUtil;
import com.viaagnolettisrl.hibernate.History;
import com.viaagnolettisrl.hibernate.JobOrder;
import com.viaagnolettisrl.hibernate.Machine;
import com.viaagnolettisrl.hibernate.NonWorkingDay;
import com.viaagnolettisrl.hibernate.Sampling;
import com.viaagnolettisrl.hibernate.User;

public class DeleteServlet extends HttpServlet {
    
    private static final long serialVersionUID = 743707157203911L;
    private User user;
    private Session hibSession;
    private String message;
    private Object toDelete;
    
    @Override
    public void init() throws ServletException {
    }
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }
    
    private void user_e(Long id) {
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
            } else {
                message = "Utente con id " + id + " non esistente";
            }
        }
    }
    
    private void nonWorkingDay(Long id) {
        if (!user.getIsAdmin()) {
            message = "Non sei amministratore";
        } else {
            toDelete = hibSession.get(NonWorkingDay.class, id);
            if (toDelete != null) { // exists
                hibSession.delete((NonWorkingDay) toDelete);
                message = "ok";
            } else {
                message = "Giorno non lavorativo non esistente";
            }
        }
    }
    
    private void sampling(Long id) {
        if (!user.getCanAddJobOrder()) {
            message = "Non pui gestire le commesse e quindi i campionamenti";
        } else {
            toDelete = hibSession.get(Sampling.class, id);
            if (toDelete != null) { // exists
                hibSession.delete((Sampling) toDelete);
                message = "ok";
            } else {
                message = "Campionamento non esistente";
            }
        }
    }
    
    private void client(Long id) {
        if (!user.getCanAddClient()) {
            message = "Non puoi eliminare i clienti";
        } else {
            toDelete = hibSession.get(Client.class, id);
            if (toDelete != null) { // exists
                hibSession.delete((Client) toDelete);
                message = "ok";
            } else {
                message = "Cliente con id " + id + " non esistente";
            }
        }
    }
    
    private void machine(Long id) {
        if (!user.getCanAddMachine()) {
            message = "Non puoi eliminare le macchine";
        } else {
            toDelete = hibSession.get(Machine.class, id);
            if (toDelete != null) { // exists
                hibSession.delete((Machine) toDelete);
                message = "ok";
            } else {
                message = "Macchina con id " + id + " non esistente";
            }
        }
    }
    
    private void assignedJobOrder(Long id) {
        if (!user.getCanAddJobOrder()) {
            message = "Non puoi eliminare le commesse";
        } else {
            toDelete = hibSession.get(AssignedJobOrder.class, id);
            if (toDelete != null) { // exists
            	AssignedJobOrder aj = (AssignedJobOrder)toDelete;
            	JobOrder j = aj.getJobOrder();
            	j.setMissingTime(j.getMissingTime() + EventUtils.getLast(aj));
            	hibSession.saveOrUpdate(j);
                hibSession.delete((AssignedJobOrder) toDelete);
                message = "ok";
            } else {
                message = "Assegnamento blocchetto orario a macchina non esistente";
            }
        }
    }
    
    private void jobOrder(Long id) {
        if (!user.getCanAddJobOrder()) {
            message = "Non puoi eliminare le commesse";
        } else {
            toDelete = hibSession.get(JobOrder.class, id);
            if (toDelete != null) { // exists
                hibSession.delete((JobOrder) toDelete);
                message = "ok";
            } else {
                message = "Commessa con id " + id + " non esistente";
            }
        }
    }
    
    private void log() {
        if ("ok".equals(message)) {
            try {
            History h = new History();
            h.setAction("DELETE");
            h.setTime(new Date());
            h.setUser(user);
            h.setWhat(toDelete.toString());
            hibSession.saveOrUpdate(h);
            hibSession.getTransaction().commit();
            } catch(GenericJDBCException e) {
                hibSession.getTransaction().rollback();
                message = "Errore nella cancellazione del record";
            }
        } else {
            hibSession.getTransaction().rollback();
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(true);
        ServletOutputStream out = response.getOutputStream();
        user = (User) session.getAttribute(LoginServlet.USER);
        
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
        
        hibSession = HibernateUtil.getSessionFactory().openSession();
        hibSession.beginTransaction();
                
        switch (what) {
            case "user":
                user_e(id);
            break;
            
            case "nonworkingday":
                nonWorkingDay(id);
            break;
            
            case "sampling":
                sampling(id);
            break;
            
            case "client":
                client(id);
            break;
            
            case "machine":
                machine(id);
            break;
            
            case "joborder":
                jobOrder(id);
            break;
            
            case "assignedjoborder":
                assignedJobOrder(id);
            break;
        }
        
        log();
        
        out.print(message);
        
        hibSession.close();
    }
    
}
