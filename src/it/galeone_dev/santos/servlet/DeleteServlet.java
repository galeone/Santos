package it.galeone_dev.santos.servlet;

import it.galeone_dev.santos.GetCollection;
import it.galeone_dev.santos.hibernate.HibernateUtils;
import it.galeone_dev.santos.hibernate.abstractions.EventUtils;
import it.galeone_dev.santos.hibernate.models.AssignedJobOrder;
import it.galeone_dev.santos.hibernate.models.Client;
import it.galeone_dev.santos.hibernate.models.History;
import it.galeone_dev.santos.hibernate.models.JobOrder;
import it.galeone_dev.santos.hibernate.models.Machine;
import it.galeone_dev.santos.hibernate.models.Maintenance;
import it.galeone_dev.santos.hibernate.models.NonWorkingDay;
import it.galeone_dev.santos.hibernate.models.Sampling;
import it.galeone_dev.santos.hibernate.models.User;
import it.galeone_dev.santos.hibernate.models.WorkingDay;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.text.ParseException;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.exception.GenericJDBCException;

public class DeleteServlet extends HttpServlet {

    private static final long serialVersionUID = 743707157203911L;
    private User user;
    private Session hibSession;
    private String message;
    private Object toDelete;

    @Override
    public void init() throws ServletException {}

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
                    IOException {
        doPost(req, resp);
    }
    
    private int deleteBetween(Class<?> entity, Date start, Date end) {
        return deleteBetween(entity, start, end, null, null, null);
    }
    
    private int deleteBetween(Class<?> entity, Date start, Date end, Machine m) {
        return deleteBetween(entity, start, end, m, null, null);
    }
    
    private int deleteBetween(Class<?> entity, Date start, Date end, Machine m, JobOrder j) {
        return deleteBetween(entity, start, end, m, j, null);
    }
    
    private int deleteBetween(Class<?> entity, Date start, Date end, Machine m, JobOrder j, Client c) {
        boolean between  = start != null && end != null,
                machine  = m != null,
                client   = c != null,
                joborder = j != null;
                
        Query q = hibSession.createQuery("delete " + entity.getSimpleName() + " where " +
                (between  ? "(starts BETWEEN :start AND :end)" : "1=1") + " AND " +
                (machine  ? "(idmachine = :machine)" : "1=1") + " AND " +
                (client   ? "(idclient = :client)" : "1=1") + " AND " +
                (joborder ? "(idjoborder = :joborder)" : "1=1"));
        
        if(between) {
            q.setDate("start", start).setDate("end", end);
        } else {
            message = "Data di inizio/fine non valida";
            return -1;
        }
        
        if(machine) {
            q.setLong("machine", m.getId());
        }
        
        if(client) {
            q.setLong("client", c.getId());
        }
        
        if(joborder) {
            q.setLong("joborder", j.getId());
        }
        
        return q.executeUpdate();
    }

    private void user_e(HttpServletRequest request) {
        if (!user.getIsAdmin()) {
            message = "Non sei amministratore";
            return;
        }
        
        try {
            Map<String, String> params = ServletUtils.getParameters(request, new String[] { "id" });
            Long id = Long.parseLong(params.get("id"));

            toDelete = hibSession.get(User.class, id);
            if (toDelete != null) { // exists
                if (((User) toDelete).getId() == user.getId()) {
                    message = "Non puoi eliminare l'amministratore";
                } else {
                    hibSession.delete((User) toDelete);
                }
            } else {
                message = "Utente con id " + id + " non esistente";
            }
        } catch (InvalidParameterException e) {
            message = e.getMessage();
        } catch (NumberFormatException e1) {
            message = "id non numerico";
        }
    }

    private void nonWorkingDay(HttpServletRequest request) {         
        if (!user.getIsAdmin()) {
            message = "Non sei amministratore";
            return;
        }
        try {
            Map<String, String> params = ServletUtils.getParameters(request, new String[]{}, new String[] { "id", "start", "end" });
            Long id = null;
            Date start = null, end = null;
            
            if (params.get("id") != null) {
                id = Long.parseLong(params.get("id"));
                toDelete = hibSession.get(NonWorkingDay.class, id);
                if (toDelete != null) { // exists
                    hibSession.delete((NonWorkingDay) toDelete);
                } else {
                    message = "Giorno non lavorativo non esistente";
                }
            } else if(params.get("start") != null && params.get("end") != null) {
                start = EventUtils.parseDate(params.get("start").trim());
                end = EventUtils.parseDate(params.get("end").trim());
                if (end.before(start) || end.equals(start)) {
                    message = "Data di inizio e fine evento errate (insensate)";
                    return;
                }
                
                if(deleteBetween(NonWorkingDay.class, start, end) < 0) {
                    message = "problema durante l'eliminazione";
                }
                
            } else {
                message = "richiesta errata, manca id|start|end";
                return;
            }
            

        } catch (InvalidParameterException e) {
            message = e.getMessage();
        } catch (NumberFormatException e1) {
            message = "id non numerico";
        } catch (ParseException e) {
            message = "data di inizio/fine non valida";
        }
    }

    private void workingDay(HttpServletRequest request) {
        if (!user.getIsAdmin()) {
            message = "Non sei amministratore";
            return;
        }
        try {
            Map<String, String> params = ServletUtils.getParameters(request, new String[]{}, new String[] { "id", "start", "end" });
            Long id = null;
            Date start = null, end = null;
            if (params.get("id") != null) {
                id = Long.parseLong(params.get("id"));
                toDelete = hibSession.get(WorkingDay.class, id);
                if (toDelete != null) { // exists
                    hibSession.delete((WorkingDay) toDelete);
                } else {
                    message = "Giorno lavorativo non esistente";
                }
            } else if(params.get("start") != null && params.get("end") != null) {
                start = EventUtils.parseDate(params.get("start").trim());
                end = EventUtils.parseDate(params.get("end").trim());
                if (end.before(start) || end.equals(start)) {
                    message = "Data di inizio e fine evento errate (insensate)";
                    return;
                }
                
                if(deleteBetween(WorkingDay.class, start, end) < 0) {
                    message = "problema durante l'eliminazione";
                }
                
            } else {
                message = "richiesta errata, manca id|start|end";
                return;
            }
            

        } catch (InvalidParameterException e) {
            message = e.getMessage();
        } catch (NumberFormatException e1) {
            message = "id non numerico";
        } catch (ParseException e) {
            message = "data di inizio/fine non valida";
        }
    }

    private void sampling(HttpServletRequest request) {
        if (!user.getCanAssignJobOrder()) {
            message = "Non pui gestire le commesse e quindi i campionamenti";
            return;
        }
        try {
            Map<String, String> params = ServletUtils.getParameters(request, new String[]{}, new String[] { "id", "start", "end", "client", "machine" });
            Long id = null;
            Date start = null, end = null;
            if (params.get("id") != null) {
                id = Long.parseLong(params.get("id"));
                toDelete = hibSession.get(Sampling.class, id);
                if (toDelete != null) { // exists
                    hibSession.delete((Sampling) toDelete);
                } else {
                    message = "Campionamento non esistente";
                }
            } else if(params.get("start") != null && params.get("end") != null && params.get("machine") != null && params.get("client") != null) {
                start = EventUtils.parseDate(params.get("start").trim());
                end = EventUtils.parseDate(params.get("end").trim());
                if (end.before(start) || end.equals(start)) {
                    message = "Data di inizio e fine evento errate (insensate)";
                    return;
                }
                
                id = Long.parseLong(params.get("machine"));
                Machine m = (Machine) hibSession.get(Machine.class, id);
                
                if(m == null) {
                    message = "Macchina non esistente";
                    return;
                }
                
                id = Long.parseLong(params.get("client"));
                Client c = (Client) hibSession.get(Client.class, id);
                
                if(c == null) {
                    message = "Cliente non esistente";
                    return;
                }
                
                if(deleteBetween(Sampling.class, start, end, m, null, c) < 0) {
                    message = "problema durante l'eliminazione";
                }
                
            } else {
                message = "richiesta errata, manca id|start|end";
                return;
            }
            

        } catch (InvalidParameterException e) {
            message = e.getMessage();
        } catch (NumberFormatException e1) {
            message = "id non numerico";
        } catch (ParseException e) {
            message = "data di inizio/fine non valida";
        }
        
    }

    private void maintenance(HttpServletRequest request) {
        if (!user.getCanAssignJobOrder()) {
            message = "Non pui gestire le commesse e quindi la manutenzione";
            return;
        }
        
        try {
            Map<String, String> params = ServletUtils.getParameters(request, new String[]{}, new String[] { "id", "start", "end", "machine" });
            Long id = null;
            Date start = null, end = null;
            if (params.get("id") != null) {
                id = Long.parseLong(params.get("id"));
                toDelete = hibSession.get(Maintenance.class, id);
                if (toDelete != null) { // exists
                    hibSession.delete((Maintenance) toDelete);
                } else {
                    message = "Manutenzione non esistente";
                }
            }
            else if(params.get("start") != null && params.get("end") != null && params.get("machine") != null) {
                start = EventUtils.parseDate(params.get("start").trim());
                end = EventUtils.parseDate(params.get("end").trim());
                if (end.before(start) || end.equals(start)) {
                    message = "Data di inizio e fine evento errate (insensate)";
                    return;
                }
                
                id = Long.parseLong(params.get("machine"));
                Machine m = (Machine) hibSession.get(Machine.class, id);
                
                if(m == null) {
                    message = "Macchina non esistente";
                    return;
                }
                
                if(deleteBetween(Maintenance.class, start, end, m) < 0) {
                    message = "problema durante l'eliminazione";
                }
                
            } else {
                message = "richiesta errata, manca id|start|end";
                return;
            }
            

        } catch (InvalidParameterException e) {
            message = e.getMessage();
        } catch (NumberFormatException e1) {
            message = "id non numerico";
        } catch (ParseException e) {
            message = "data di inizio/fine non valida";
        }
    }

    private void client(HttpServletRequest request) {
        if (!user.getIsAdmin()) {
            message = "Non puoi eliminare i clienti";
            return;
        }
        try {
            Map<String, String> params = ServletUtils.getParameters(request, new String[] { "id" });
            Long id = Long.parseLong(params.get("id"));

            toDelete = hibSession.get(Client.class, id);
            if (toDelete != null) { // exists
                hibSession.delete((Client) toDelete);
            } else {
                message = "Cliente con id " + id + " non esistente";
            }
        } catch (InvalidParameterException e) {
            message = e.getMessage();
        } catch (NumberFormatException e1) {
            message = "id non numerico";
        }
    }

    private void machine(HttpServletRequest request) {
        if (!user.getIsAdmin()) {
            message = "Non puoi eliminare le macchine";
            return;
        }
        
        try {
            Map<String, String> params = ServletUtils.getParameters(request, new String[] { "id" });
            Long id = Long.parseLong(params.get("id"));
            toDelete = hibSession.get(Machine.class, id);
            if (toDelete != null) { // exists
                hibSession.delete((Machine) toDelete);
            } else {
                message = "Macchina con id " + id + " non esistente";
            }
        } catch (InvalidParameterException e) {
            message = e.getMessage();
        } catch (NumberFormatException e1) {
            message = "id non numerico";
        }
    }

    private void assignedJobOrder(HttpServletRequest request) {
        if (!user.getCanAddJobOrder()) {
            message = "Non puoi eliminare le commesse";
            return;
        }
        try {
            Map<String, String> params = ServletUtils.getParameters(request, new String[]{}, new String[] { "id", "start", "end", "joborder", "machine" });
            Long id = null;
            Date start = null, end = null;
            if (params.get("id") != null) {
                id = Long.parseLong(params.get("id"));
                toDelete = hibSession.get(AssignedJobOrder.class, id);
                if (toDelete != null) { // exists
                    AssignedJobOrder aj = (AssignedJobOrder) toDelete;
                    JobOrder j = aj.getJobOrder();
                    Long deletedLast = EventUtils.getLast(aj);
                    j.setMissingTime(j.getMissingTime() + deletedLast);
                    j.setMissingTimeWithOffset(j.getMissingTimeWithOffset() + deletedLast);
                    hibSession.merge(j);
                    hibSession.delete((AssignedJobOrder) toDelete);
                } else {
                    message = "Assegnamento blocchetto orario a macchina non esistente";
                }
            } else if(params.get("start") != null && params.get("end") != null && params.get("machine") != null && params.get("joborder") != null) {
                start = EventUtils.parseDate(params.get("start").trim());
                end = EventUtils.parseDate(params.get("end").trim());
                if (end.before(start) || end.equals(start)) {
                    message = "Data di inizio e fine evento errate (insensate)";
                    return;
                }
                
                id = Long.parseLong(params.get("machine"));
                Machine m = (Machine) hibSession.get(Machine.class, id);
                
                if(m == null) {
                    message = "Macchina non esistente";
                    return;
                }
                
                id = Long.parseLong(params.get("joborder"));
                JobOrder j = (JobOrder) hibSession.get(JobOrder.class, id);
                
                if(j == null) {
                    message = "Commessa non esistente";
                    return;
                }
                
                Collection<AssignedJobOrder> willRemove = GetCollection.assignedJobOrdersBetween(m, start, end);
                Long removedTime = 0L;
                for(AssignedJobOrder a : willRemove) {
                    if(a.getJobOrder().equals(j)) {
                        removedTime += EventUtils.getLast(a);
                    }
                }
                j.setMissingTime(j.getMissingTime() + removedTime);
                j.setMissingTimeWithOffset(j.getMissingTimeWithOffset() + removedTime);
                if(deleteBetween(AssignedJobOrder.class, start, end, m, j) < 0) {
                    message = "problema durante l'eliminazione";
                } else {
                    hibSession.merge(j);
                }
                
                //Fake toDelete event
                AssignedJobOrder fake = new AssignedJobOrder();
                fake.setStart(start);
                fake.setEnd(end);
                fake.setJobOrder(j);
                fake.setMachine(m);
                toDelete = fake;
                
            } else {
                message = "richiesta errata, manca id|start|end";
                return;
            }
            

        } catch (InvalidParameterException e) {
            message = e.getMessage();
        } catch (NumberFormatException e1) {
            message = "id non numerico";
        } catch (ParseException e) {
            message = "data di inizio/fine non valida";
        }
    }

    private void jobOrder(HttpServletRequest request) {
        if (!user.getCanAddJobOrder()) {
            message = "Non puoi eliminare le commesse";
            return;
        }
        try {
            Map<String, String> params = ServletUtils.getParameters(request, new String[] { "id" });
            Long id = Long.parseLong(params.get("id"));

            toDelete = hibSession.get(JobOrder.class, id);
            if (toDelete != null) { // exists
                hibSession.delete((JobOrder) toDelete);
            } else {
                message = "Commessa con id " + id + " non esistente";
            }
        } catch (InvalidParameterException e) {
            message = e.getMessage();
        } catch (NumberFormatException e1) {
            message = "id non numerico";
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
            } catch (GenericJDBCException e) {
                hibSession.getTransaction().rollback();
                message = "Errore nella cancellazione del record";
            }
        } else {
            hibSession.getTransaction().rollback();
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
                    IOException {
        HttpSession session = request.getSession(true);
        ServletOutputStream out = response.getOutputStream();
        user = (User) session.getAttribute(LoginServlet.USER);

        if (user == null) { // not logged in
            out.print("login");
            return;
        }

        try {
            Map<String, String> params = ServletUtils.getParameters(request, new String[] { "what" });

            hibSession = HibernateUtils.getSessionFactory().openSession();
            hibSession.beginTransaction();
            
            message = "ok";
            switch (params.get("what")) {

            case "nonworkingday":
                nonWorkingDay(request);
                break;

            case "workingday":
                workingDay(request);
                break;

            case "sampling":
                sampling(request);
                break;

            case "maintenance":
                maintenance(request);
                break;
                
            case "assignedjoborder":
                assignedJobOrder(request);
                break;

            case "client":
                client(request);
                break;

            case "machine":
                machine(request);
                break;

            case "joborder":
                jobOrder(request);
                break;
                
            case "user":
                user_e(request);
                break;

            default:
                message = "Parametro " + params.get("what") + " non riconosciuto";
                break;
            }

            log();

        } catch (IllegalArgumentException e1) {
            message = e1.getMessage();
        }

        out.print(message);

        hibSession.close();
    }

}
