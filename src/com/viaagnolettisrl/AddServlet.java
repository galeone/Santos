package com.viaagnolettisrl;

import java.io.IOException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.hibernate.Session;
import org.hibernate.exception.ConstraintViolationException;

import com.google.gson.Gson;
import com.viaagnolettisrl.hibernate.AssignedJobOrder;
import com.viaagnolettisrl.hibernate.Client;
import com.viaagnolettisrl.hibernate.HibernateUtil;
import com.viaagnolettisrl.hibernate.History;
import com.viaagnolettisrl.hibernate.JobOrder;
import com.viaagnolettisrl.hibernate.Machine;
import com.viaagnolettisrl.hibernate.NonWorkingDay;
import com.viaagnolettisrl.hibernate.Sampling;
import com.viaagnolettisrl.hibernate.User;

public class AddServlet extends HttpServlet {
    
    private static final long serialVersionUID = 74377157203911L;
    private User user;
    private String message;
    private Object savedObject = null;
    private Gson g = new Gson();
    private Session hibSession;
    
    @Override
    public void init() throws ServletException {
    }
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }
    
    private void sampling(HttpServletRequest request) {
        Map<String, String> params;
        
        if (!user.getCanAddJobOrder()) {
            message = "Non puoi gestire commesse";
        } else {
            String[] fields = new String[] { "start", "end", "machine", "joborder" };
            Arrays.sort(fields);
            params = ServletUtils.getParameters(request, fields);
            if (params.containsValue(null) || params.containsValue("")) {
                message = "Completare tutti i campi";
            } else {
                try {
                    Sampling s = new Sampling();
                    Long id = null;
                    try {
                        id = Long.parseLong(params.get("machine"));
                    } catch (NumberFormatException e) {
                        message = "Valore macchine non valido";
                        return;
                    }
                    Machine m = (Machine) hibSession.get(Machine.class, id);
                    if (m == null) {
                        message = "Macchina non trovata";
                        return;
                    }
                    
                    s.setMachine(m);
                    
                    try {
                        id = Long.parseLong(params.get("joborder"));
                    } catch (NumberFormatException e) {
                        message = "Valore commessa non valido";
                        return;
                    }
                    JobOrder j = (JobOrder) hibSession.get(JobOrder.class, id);
                    if (j == null) {
                        message = "Commessa non trovata";
                        return;
                    }
                    
                    s.setJobOrder(j);
                    
                    s.setEnd(EventUtils.parseDate(params.get("end")));
                    s.setStart(EventUtils.parseDate(params.get("start")));
                    
                    hibSession.saveOrUpdate(s);
                    
                    message = g.toJson(s);
                    savedObject = s;
                    
                    Sampling.shiftRight(s, hibSession);
                    
                } catch (NumberFormatException e) {
                    message = "Macchina non valida";
                } catch (ParseException e) {
                    message = "Data inizio/fine non valida";
                }
            }
        }
    }
    
    private void nonWorkingDay(HttpServletRequest request) {
        if (!user.getIsAdmin()) {
            message = "Non puoi aggiungere giorni non lavorativi";
        } else {
            String[] fields = new String[] { "start", "end" };
            Arrays.sort(fields);
            Map<String, String> params = ServletUtils.getParameters(request, fields);
            if (params.containsValue(null) || params.containsValue("")) {
                message = "Completare tutti i campi";
            } else {
                try {
                    NonWorkingDay nw = new NonWorkingDay();
                    nw.setStart(EventUtils.parseDate(params.get("start").trim()));
                    nw.setEnd(EventUtils.parseDate(params.get("end").trim()));
                    if(nw.getEnd().before(nw.getStart()) || nw.getEnd().equals(nw.getStart())) {
                        message = "Data di inizio e fine evento errate (insensate)";
                        return;
                    }
                    
                    hibSession.saveOrUpdate(nw);
                    message = g.toJson(nw);
                    savedObject = nw;
                    
                    NonWorkingDay.shiftMachineEventsRight(nw, hibSession);
                    
                } catch (ParseException e) {
                    message = "formato data non valido";
                }
            }
        }
    }
    
    private void user_a(HttpServletRequest request) {
        if (!user.getIsAdmin()) {
            message = "Non sei admin";
        } else {
            String[] fields = new String[] { "name", "surname", "username", "password", "canaddjoborder", "canaddclient", "canaddmachine" };
            Arrays.sort(fields);
            Map<String, String> params = ServletUtils.getParameters(request, fields);
            if (params.containsValue(null) || params.containsValue("")) {
                message = "Completare tutti i campi";
            } else {
                User u = new User();
                u.setCanAddClient(params.get("canaddclient").equals("Si"));
                u.setCanAddJobOrder(params.get("canaddjoborder").equals("Si"));
                u.setCanAddMachine(params.get("canaddmachine").equals("Si"));
                u.setIsAdmin(false);
                u.setName(params.get("name"));
                u.setPassword(params.get("password"));
                u.setSurname(params.get("surname"));
                u.setUsername(params.get("username"));
                u.setHistory(new HashSet<History>());
                hibSession.saveOrUpdate(u);
                savedObject = u;
                
                message = g.toJson(u);
            }
        }
    }
    
    private void client(HttpServletRequest request) {
        if (!user.getCanAddClient()) {
            message = "Non puoi aggiungere clienti";
        } else {
            String[] fields = new String[] { "name", "code" };
            Arrays.sort(fields);
            Map<String, String> params = ServletUtils.getParameters(request, fields);
            if (params.containsValue(null) || params.containsValue("")) {
                message = "Completare tutti i campi";
            } else {
                Client c = new Client();
                c.setCode(params.get("code"));
                c.setName(params.get("name"));
                c.setJobOrders(new HashSet<JobOrder>());
                hibSession.saveOrUpdate(c);
                savedObject = c;
                
                message = g.toJson(c);
            }
        }
    }
    
    private void machine(HttpServletRequest request) {
        if (!user.getCanAddMachine()) {
            message = "Non puoi aggiungere macchine";
        } else {
            String[] fields = new String[] { "name", "type", "nicety" };
            Arrays.sort(fields);
            Map<String, String> params = ServletUtils.getParameters(request, fields);
            if (params.containsValue(null) || params.containsValue("")) {
                message = "Completare tutti i campi";
            } else {
                try {
                    Machine m = new Machine();
                    m.setName(params.get("name"));
                    m.setNicety(Float.parseFloat(params.get("nicety")));
                    m.setType(params.get("type"));
                    m.setAssignedJobOrders(new HashSet<AssignedJobOrder>());
                    hibSession.saveOrUpdate(m);
                    savedObject = m;
                    
                    message = g.toJson(m);
                } catch (NumberFormatException e) {
                    message = "Valore della finezza non valido";
                }
            }
        }
    }
    
    private void jobOrder(HttpServletRequest request) {
        if (!user.getCanAddJobOrder()) {
            message = "Non puoi aggiungere commesse";
        } else {
            String[] fields = new String[] { "leadtime", "client", "color", "numberofitems", "timeforitem", "description" };
            Arrays.sort(fields);
            Map<String, String> params = ServletUtils.getParameters(request, fields);
            if (params.containsValue(null) || params.containsValue("")) {
                message = "Completare tutti i campi";
            } else {
                try {
                    JobOrder j = new JobOrder();
                    Long id = null;
                    try {
                        id = Long.parseLong(params.get("client"));
                    } catch (NumberFormatException e) {
                        message = "Valore cliente non valido";
                        return;
                    }
                    Client c = (Client) hibSession.get(Client.class, id);
                    if (c == null) {
                        message = "Cliente non trovato";
                        return;
                    }
                    
                    j.setAssignedJobOrders(new HashSet<AssignedJobOrder>());
                    j.setClient(c);
                    Long leadTime = Long.parseLong(params.get("leadtime"));
                    if (leadTime <= 0) {
                        message = "Tempo di produzione <= 0";
                        return;
                    }
                    Long numberOfItems = Long.parseLong(params.get("numberofitems"));
                    if(numberOfItems <= 0) {
                        message = "Numero di capi <= 0";
                        return;
                    }
                    Long timeForItem = Long.parseLong(params.get("timeforitem"));
                    if(timeForItem <= 0) {
                        message = "Tempo per capo <= 0";
                        return;
                    }
                    leadTime = timeForItem * numberOfItems;
                    j.setNumberOfItems(numberOfItems);
                    j.setTimeForItem(timeForItem);
                    j.setLeadTime(leadTime);
                    j.setMissingTime(leadTime);
                    j.setColor(params.get("color"));
                    j.setDescription(params.get("description"));
                    hibSession.saveOrUpdate(j);
                    
                    message = g.toJson(j);
                    savedObject = j;
                } catch (NumberFormatException e) {
                    message = "Tempo di produzione non valido";
                }
            }
        }
    }
    
    private void assignedJobOrder(HttpServletRequest request) {
        if (!user.getCanAddJobOrder()) {
            message = "Non puoi aggiungere commesse";
        } else {
            String[] fields = new String[] { "start", "end", "machine", "joborder" };
            Arrays.sort(fields);
            Map<String, String> params = ServletUtils.getParameters(request, fields);
            if (params.containsValue(null) || params.containsValue("")) {
                message = "Completare tutti i campi";
            } else {
                try {
                    AssignedJobOrder aj = new AssignedJobOrder();
                    Long id = null;
                    try {
                        id = Long.parseLong(params.get("machine"));
                    } catch (NumberFormatException e) {
                        message = "Valore macchine non valido";
                        return;
                    }
                    Machine m = (Machine) hibSession.get(Machine.class, id);
                    if (m == null) {
                        message = "Macchina non trovata";
                        return;
                    }
                    
                    aj.setMachine(m);
                    
                    try {
                        id = Long.parseLong(params.get("joborder"));
                    } catch (NumberFormatException e) {
                        message = "Valore commessa non valido";
                        return;
                    }
                    JobOrder j = (JobOrder) hibSession.get(JobOrder.class, id);
                    if (j == null) {
                        message = "Commessa non trovata";
                        return;
                    }
                    
                    aj.setJobOrder(j);

                    aj.setEnd(EventUtils.parseDate(params.get("end")));
                    aj.setStart(EventUtils.parseDate(params.get("start")));
                    
                    if(aj.getStart().after(aj.getEnd()) || EventUtils.getLast(aj) > 1440L) {
                        message = "Orario errato. O maggiore di 24h o fine precedente ad inizio";
                        return;
                    }
                    
                    j.setMissingTime(j.getMissingTime() - EventUtils.getLast(aj));
                    
                    hibSession.saveOrUpdate(aj);
                    hibSession.saveOrUpdate(j);
                    
                    message = g.toJson(aj);
                    savedObject = aj;
                    
                    AssignedJobOrder.shiftRight(aj, hibSession);
                    
                } catch (NumberFormatException e) {
                    message = "Macchina non valida";
                } catch (ParseException e) {
                    message = "Data inizio/fine non valida";
                }
            }
        }
    }
    
    private void autoAssignedJobOrder(HttpServletRequest request) {
        if (!user.getCanAddJobOrder()) {
            message = "Non puoi aggiungere commesse";
        } else {
            String[] fields = new String[] { "start", "end", "machine", "joborder" };
            Arrays.sort(fields);
            Map<String, String> params = ServletUtils.getParameters(request, fields);
            if (params.containsValue(null) || params.containsValue("")) {
                message = "Completare tutti i campi";
            } else {
                try {
                    AssignedJobOrder aj = new AssignedJobOrder();
                    Long id = null;
                    try {
                        id = Long.parseLong(params.get("machine"));
                    } catch (NumberFormatException e) {
                        message = "Valore macchine non valido";
                        return;
                    }
                    Machine m = (Machine) hibSession.get(Machine.class, id);
                    if (m == null) {
                        message = "Macchina non trovata";
                        return;
                    }
                    
                    aj.setMachine(m);
                    
                    try {
                        id = Long.parseLong(params.get("joborder"));
                    } catch (NumberFormatException e) {
                        message = "Valore commessa non valido";
                        return;
                    }
                    JobOrder j = (JobOrder) hibSession.get(JobOrder.class, id);
                    if (j == null) {
                        message = "Commessa non trovata";
                        return;
                    }
                    
                    aj.setJobOrder(j);

                    aj.setEnd(EventUtils.parseDate(params.get("end")));
                    aj.setStart(EventUtils.parseDate(params.get("start")));
                    Long last = EventUtils.getLast(aj), removedTime = 0L;
                    Date prev = new Date(aj.getStart().getTime());
                    boolean fillAll = aj.getEnd().before(aj.getStart());
                    if(last > 1440L || fillAll) {
                        if(fillAll)  {
                            //TODO: non considerare la durata ma rimepire finché ce ne è.
                        }
                        while(last > 0 || j.getMissingTime() > 0) {
                            AssignedJobOrder slice = new AssignedJobOrder();
                            slice.setJobOrder(j);
                            slice.setMachine(m);
                            slice.setStart(prev);
                            Long howLong = last > 1440L ? 1440L : last;
                            Date end = new Date(prev.getTime() + howLong);
                            slice.setEnd(end);
                            hibSession.saveOrUpdate(slice);
                            j.setMissingTime(j.getMissingTime() - EventUtils.getLast(slice));
                            last -= howLong;
                            prev = slice.getEnd();
                            removedTime += howLong;
                            
                            AssignedJobOrder.shiftRight(slice, hibSession);
                        }
                        message = Long.toString(removedTime);
                        savedObject = aj;
                    } else {
                        j.setMissingTime(j.getMissingTime() - EventUtils.getLast(aj));
                    
                        hibSession.saveOrUpdate(aj);
                        hibSession.saveOrUpdate(j);
                    
                        message = g.toJson(aj);
                        savedObject = aj;
                    
                        AssignedJobOrder.shiftRight(aj, hibSession);
                    }
                    
                } catch (NumberFormatException e) {
                    message = "Macchina non valida";
                } catch (ParseException e) {
                    message = "Data inizio/fine non valida";
                }
            }
        }
    }
    
    private void log() {
        try {
            if (savedObject != null) {
                History h = new History();
                h.setAction("CREATE");
                h.setTime(new Date());
                h.setUser(user);
                h.setWhat(savedObject.toString());
                hibSession.saveOrUpdate(h);
            }
            hibSession.getTransaction().commit();
        } catch (ConstraintViolationException e) {
            message = "Esiste già un record con questo nome";
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
        
        Map<String, String> params = ServletUtils.getParameters(request, new String[] { "what" });
        String what;
        if ((what = params.get("what")) == null) {
            out.println("error, invalid parameters");
            return;
        }
        
        hibSession = HibernateUtil.getSessionFactory().openSession();
        hibSession.beginTransaction();
        switch (what) {
            case "user":
                user_a(request);
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
            
            case "assignedjoborder":
                assignedJobOrder(request);
            break;
            
            case "autoassignedjoborder":
                autoAssignedJobOrder(request);
            break;
            
            case "nonworkingday":
                nonWorkingDay(request);
            break;
            
            case "sampling":
                sampling(request);
            break;
            
            default:
                out.print("Invalid parameter value for what: " + what);
                return;
        }
        
        log();
        
        out.print(message);
        hibSession.close();
    }
    
}
