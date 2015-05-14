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
import com.viaagnolettisrl.hibernate.DroppableMachineEvent;
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
    private StringBuilder message = new StringBuilder();
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
    
    private void nonWorkingDay(HttpServletRequest request) {
        if (!user.getIsAdmin()) {
            message.replace(0,message.length(),"Non puoi aggiungere giorni non lavorativi");
        } else {
            String[] fields = new String[] { "start", "end" };
            Arrays.sort(fields);
            Map<String, String> params = ServletUtils.getParameters(request, fields);
            if (params.containsValue(null) || params.containsValue("")) {
                message.replace(0,message.length(),"Completare tutti i campi");
            } else {
                try {
                    NonWorkingDay nw = new NonWorkingDay();
                    nw.setStart(EventUtils.parseDate(params.get("start").trim()));
                    nw.setEnd(EventUtils.parseDate(params.get("end").trim()));
                    if(nw.getEnd().before(nw.getStart()) || nw.getEnd().equals(nw.getStart())) {
                        message.replace(0,message.length(),"Data di inizio e fine evento errate (insensate)");
                        return;
                    }
                    
                    hibSession.saveOrUpdate(nw);
                    message.replace(0,message.length(),g.toJson(nw));
                    savedObject = nw;
                    
                    NonWorkingDay.shiftMachineEventsRight(nw, hibSession);
                    
                } catch (ParseException e) {
                    message.replace(0,message.length(),"formato data non valido");
                }
            }
        }
    }
    
    private void user_a(HttpServletRequest request) {
        if (!user.getIsAdmin()) {
            message.replace(0,message.length(),"Non sei admin");
        } else {
            String[] fields = new String[] { "name", "surname", "username", "password", "canaddjoborder", "canaddclient", "canaddmachine" };
            Arrays.sort(fields);
            Map<String, String> params = ServletUtils.getParameters(request, fields);
            if (params.containsValue(null) || params.containsValue("")) {
                message.replace(0,message.length(),"Completare tutti i campi");
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
                
                message.replace(0,message.length(),g.toJson(u));
            }
        }
    }
    
    private void client(HttpServletRequest request) {
        if (!user.getCanAddClient()) {
            message.replace(0,message.length(),"Non puoi aggiungere clienti");
        } else {
            String[] fields = new String[] { "name", "code" };
            Arrays.sort(fields);
            Map<String, String> params = ServletUtils.getParameters(request, fields);
            if (params.containsValue(null) || params.containsValue("")) {
                message.replace(0,message.length(),"Completare tutti i campi");
            } else {
                Client c = new Client();
                c.setCode(params.get("code"));
                c.setName(params.get("name"));
                c.setJobOrders(new HashSet<JobOrder>());
                hibSession.saveOrUpdate(c);
                savedObject = c;
                
                message.replace(0,message.length(),g.toJson(c));
            }
        }
    }
    
    private void machine(HttpServletRequest request) {
        if (!user.getCanAddMachine()) {
            message.replace(0,message.length(),"Non puoi aggiungere macchine");
        } else {
            String[] fields = new String[] { "name", "type", "nicety" };
            Arrays.sort(fields);
            Map<String, String> params = ServletUtils.getParameters(request, fields);
            if (params.containsValue(null) || params.containsValue("")) {
                message.replace(0,message.length(),"Completare tutti i campi");
            } else {
                try {
                    Machine m = new Machine();
                    m.setName(params.get("name"));
                    m.setNicety(Float.parseFloat(params.get("nicety")));
                    m.setType(params.get("type"));
                    m.setAssignedJobOrders(new HashSet<AssignedJobOrder>());
                    hibSession.saveOrUpdate(m);
                    savedObject = m;
                    
                    message.replace(0,message.length(),g.toJson(m));
                } catch (NumberFormatException e) {
                    message.replace(0,message.length(),"Valore della finezza non valido");
                }
            }
        }
    }
    
    private void jobOrder(HttpServletRequest request) {
        if (!user.getCanAddJobOrder()) {
            message.replace(0,message.length(),"Non puoi aggiungere commesse");
        } else {
            String[] fields = new String[] { "leadtime", "client", "color", "numberofitems", "timeforitem", "description" };
            Arrays.sort(fields);
            Map<String, String> params = ServletUtils.getParameters(request, fields);
            if (params.containsValue(null) || params.containsValue("")) {
                message.replace(0,message.length(),"Completare tutti i campi");
            } else {
                try {
                    JobOrder j = new JobOrder();
                    Long id = null;
                    try {
                        id = Long.parseLong(params.get("client"));
                    } catch (NumberFormatException e) {
                        message.replace(0,message.length(),"Valore cliente non valido");
                        return;
                    }
                    Client c = (Client) hibSession.get(Client.class, id);
                    if (c == null) {
                        message.replace(0,message.length(),"Cliente non trovato");
                        return;
                    }
                    
                    j.setAssignedJobOrders(new HashSet<AssignedJobOrder>());
                    j.setClient(c);
                    Long leadTime = Long.parseLong(params.get("leadtime"));
                    if (leadTime <= 0) {
                        message.replace(0,message.length(),"Tempo di produzione <= 0");
                        return;
                    }
                    Long numberOfItems = Long.parseLong(params.get("numberofitems"));
                    if(numberOfItems <= 0) {
                        message.replace(0,message.length(),"Numero di capi <= 0");
                        return;
                    }
                    Long timeForItem = Long.parseLong(params.get("timeforitem"));
                    if(timeForItem <= 0) {
                        message.replace(0,message.length(),"Tempo per capo <= 0");
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
                    
                    message.replace(0,message.length(),g.toJson(j));
                    savedObject = j;
                } catch (NumberFormatException e) {
                    message.replace(0,message.length(),"Tempo di produzione non valido");
                }
            }
        }
    }
    
    private void addOneAssignedJobOrder(JobOrder j, Machine m, Date start, Date end) {
        DroppableMachineEvent event = addOneMacineEvent(AssignedJobOrder.class, j, m, start, end);
        j.setMissingTime(j.getMissingTime() - EventUtils.getLast(event));
        hibSession.saveOrUpdate(j);
    }
    
    private void addOneSampling(JobOrder j, Machine m, Date start, Date end) {
        addOneMacineEvent(Sampling.class, j, m, start, end);
    }
    
    private DroppableMachineEvent addOneMacineEvent(Class<? extends DroppableMachineEvent> droppableEvent, JobOrder j, Machine m, Date start, Date end ) {
        DroppableMachineEvent event = null;
        try {
            event = (DroppableMachineEvent) droppableEvent.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
            message.replace(0, message.length(), e.getMessage());
            return null;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            message.replace(0, message.length(), e.getMessage());
            return null;
        }
        event.setJobOrder(j);
        event.setMachine(m);
        
        event.setStart(start);
        event.setEnd(end);
        
        if(event.getStart().after(event.getEnd()) || EventUtils.getLast(event) > 1440L) {
            message.replace(0,message.length(),"Orario errato. O maggiore di 24h o fine precedente ad inizio");
            return null;
        }
                
        hibSession.saveOrUpdate(event);
        
        message.replace(0,message.length(),g.toJson(event));
        savedObject = event;
        
        DroppableMachineEvent.shiftRight(event, hibSession);
        return event;
    }
    
    private void autoAssignedJobOrder(HttpServletRequest request) {
        if (!user.getCanAddJobOrder()) {
            message.replace(0,message.length(),"Non puoi aggiungere commesse");
        } else {
            String[] fields = new String[] { "start", "end", "machine", "joborder" };
            Arrays.sort(fields);
            Map<String, String> params = ServletUtils.getParameters(request, fields);
            if (params.containsValue(null) || params.containsValue("")) {
                message.replace(0,message.length(),"Completare tutti i campi");
            } else {
                try {
                    Long id = null;
                    try {
                        id = Long.parseLong(params.get("machine"));
                    } catch (NumberFormatException e) {
                        message.replace(0,message.length(),"Valore macchine non valido");
                        return;
                    }
                    Machine m = (Machine) hibSession.get(Machine.class, id);
                    if (m == null) {
                        message.replace(0,message.length(),"Macchina non trovata");
                        return;
                    }
                                        
                    try {
                        id = Long.parseLong(params.get("joborder"));
                    } catch (NumberFormatException e) {
                        message.replace(0,message.length(),"Valore commessa non valido");
                        return;
                    }
                    JobOrder j = (JobOrder) hibSession.get(JobOrder.class, id);
                    if (j == null) {
                        message.replace(0,message.length(),"Commessa non trovata");
                        return;
                    }
                    

                    Date start = EventUtils.start(EventUtils.parseDate(params.get("start"))),
                         end   = EventUtils.parseDate(params.get("end"));

                    AssignedJobOrder dummy = new AssignedJobOrder();
                    dummy.setStart(start);
                    dummy.setEnd(end);
                    Long last = EventUtils.getLast(dummy), removedTime = 0L, missingTime = j.getMissingTime();
                    Date prev = new Date(dummy.getStart().getTime());
                    boolean fillAll = dummy.getEnd().before(dummy.getStart()) || last > missingTime;
                    if(last > 1440L || fillAll) {
                        if(fillAll) {
                            last = missingTime;
                        }
                        while(last > 0) {
                            dummy.setStart(prev);
                            Long howLong = last > 1440L ? 1440L : last;
                            
                            end = new Date(prev.getTime() + howLong * 60000);
                            dummy.setEnd(end);
                            
                            addOneAssignedJobOrder(j, m, prev, end);
                            last -= howLong;
                            removedTime += howLong;
                            prev = new Date(end.getTime());
                        }
                    } else if(last > 0L && last <= 1440L){
                        addOneAssignedJobOrder(j, m, start, end);
                        removedTime = last;
                    } else {
                        message.replace(0, message.length(), "Evento di durata nulla o negativa");
                        return;
                    }
                    
                    message.replace(0,message.length(),Long.toString(removedTime));
                    
                } catch (NumberFormatException e) {
                    message.replace(0,message.length(),"Macchina non valida");
                } catch (ParseException e) {
                    message.replace(0,message.length(),"Data inizio/fine non valida");
                }
            }
        }
    }
    
    private void autoSampling(HttpServletRequest request) {
        if (!user.getCanAddJobOrder()) {
            message.replace(0,message.length(),"Non puoi aggiungere commesse");
        } else {
            String[] fields = new String[] { "start", "end", "machine", "joborder" };
            Arrays.sort(fields);
            Map<String, String> params = ServletUtils.getParameters(request, fields);
            if (params.containsValue(null) || params.containsValue("")) {
                message.replace(0,message.length(),"Completare tutti i campi");
            } else {
                try {
                    Long id = null;
                    try {
                        id = Long.parseLong(params.get("machine"));
                    } catch (NumberFormatException e) {
                        message.replace(0,message.length(),"Valore macchine non valido");
                        return;
                    }
                    Machine m = (Machine) hibSession.get(Machine.class, id);
                    if (m == null) {
                        message.replace(0,message.length(),"Macchina non trovata");
                        return;
                    }
                                        
                    try {
                        id = Long.parseLong(params.get("joborder"));
                    } catch (NumberFormatException e) {
                        message.replace(0,message.length(),"Valore commessa non valido");
                        return;
                    }
                    JobOrder j = (JobOrder) hibSession.get(JobOrder.class, id);
                    if (j == null) {
                        message.replace(0,message.length(),"Commessa non trovata");
                        return;
                    }
                    

                    Date start = EventUtils.start(EventUtils.parseDate(params.get("start"))),
                         end   = EventUtils.start(EventUtils.parseDate(params.get("end")));;

                    AssignedJobOrder dummy = new AssignedJobOrder();
                    dummy.setStart(start);
                    dummy.setEnd(end);
                    Long last = EventUtils.getLast(dummy), removedTime = 0L;
                    Date prev = new Date(dummy.getStart().getTime());
                    if(last <= 0) {
                        message.replace(0, message.length(), "Evento di durata nulla o negativa");
                        return;
                    }

                    while(last > 0) {
                        dummy.setStart(prev);
                        Long howLong = last > 1440L ? 1440L : last;
                        
                        end = new Date(prev.getTime() + howLong * 60000);
                        dummy.setEnd(end);
                        
                        addOneSampling(j, m, prev, end);
                        last -= howLong;
                        removedTime += howLong;
                        prev = new Date(end.getTime());
                    }
                    
                    message.replace(0,message.length(),Long.toString(removedTime));
                    
                } catch (NumberFormatException e) {
                    message.replace(0,message.length(),"Macchina non valida");
                } catch (ParseException e) {
                    message.replace(0,message.length(),"Data inizio/fine non valida");
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
            message.replace(0,message.length(),"Esiste già un record con questo nome");
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
                autoAssignedJobOrder(request);
            break;
            
            case "sampling":
                autoSampling(request);
            break;
            
            case "nonworkingday":
                nonWorkingDay(request);
            break;
            
            default:
                out.print("Invalid parameter value for what: " + what);
                return;
        }
        
        log();
        
        out.print(message.toString());
        hibSession.close();
    }
    
}
