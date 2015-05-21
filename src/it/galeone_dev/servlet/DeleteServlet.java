package it.galeone_dev.servlet;

import it.galeone_dev.hibernate.HibernateUtils;
import it.galeone_dev.hibernate.abstractions.EventUtils;
import it.galeone_dev.hibernate.models.AssignedJobOrder;
import it.galeone_dev.hibernate.models.Client;
import it.galeone_dev.hibernate.models.History;
import it.galeone_dev.hibernate.models.JobOrder;
import it.galeone_dev.hibernate.models.Machine;
import it.galeone_dev.hibernate.models.Maintenance;
import it.galeone_dev.hibernate.models.NonWorkingDay;
import it.galeone_dev.hibernate.models.Sampling;
import it.galeone_dev.hibernate.models.User;
import it.galeone_dev.hibernate.models.WorkingHours;

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
    
    private void workingHours(Long id) {
        if (!user.getIsAdmin()) {
            message = "Non sei amministratore";
        } else {
            toDelete = hibSession.get(WorkingHours.class, id);
            if (toDelete != null) { // exists
                hibSession.delete((WorkingHours) toDelete);
                message = "ok";
            } else {
                message = "Giorno non lavorativo non esistente";
            }
        }
    }
    
    private void sampling(Long id) {
        if (!user.getCanAssignJobOrder()) {
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
    
    private void maintenance(Long id) {
        if (!user.getCanAssignJobOrder()) {
            message = "Non pui gestire le commesse e quindi la manutenzione";
        } else {
            toDelete = hibSession.get(Maintenance.class, id);
            if (toDelete != null) { // exists
                hibSession.delete((Maintenance) toDelete);
                message = "ok";
            } else {
                message = "Manutenzione non esistente";
            }
        }
    }
    
    private void client(Long id) {
        if (!user.getIsAdmin()) {
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
        if (!user.getIsAdmin()) {
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
            hibSession.merge(h);
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
        
        hibSession = HibernateUtils.getSessionFactory().openSession();
        hibSession.beginTransaction();
                
        switch (what) {
            case "user":
                user_e(id);
            break;
            
            case "nonworkingday":
                nonWorkingDay(id);
            break;
            
            case "workingHours":
                workingHours(id);
            break;
            
            case "sampling":
                sampling(id);
            break;
            
            case "maintenance":
                maintenance(id);
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
