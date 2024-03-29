package it.galeone_dev.santos.servlet;

import it.galeone_dev.santos.hibernate.HibernateUtils;
import it.galeone_dev.santos.hibernate.abstractions.DroppableMachineEvent;
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
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.hibernate.Session;
import org.hibernate.exception.ConstraintViolationException;

public class EditServlet extends HttpServlet {
    
    private static final long serialVersionUID = 74377157203911L;
    private String            outputResult     = "";
    private StringBuilder     message          = new StringBuilder();
    private User              user;
    private Object            toEdit, savedObject;
    private Session           hibSession;
    
    @Override
    public void init() throws ServletException {
    }
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }
    
    private void sampling(Long id, HttpServletRequest request) {
        if (!user.getCanAssignJobOrder()) {
            message.replace(0, message.length(), "Non puoi gestire le commesse e nemmeno i campionamenti");
            return;
        } else {
            toEdit = (Sampling) hibSession.get(Sampling.class, id);
            
            Sampling sd = new Sampling();
            
            if (toEdit != null) { // edit
                sd = (Sampling) toEdit;
                // DroppableMachineEvent switch next
                sd.setOldStart(sd.getStart());
                try {
                    Map<String, String> params = ServletUtils.getParameters(request, new String[] { "start", "end" });
                    Date start = EventUtils.parseDate(params.get("start")), end = EventUtils.parseDate(params
                            .get("end"));
                    sd.setStart(start);
                    sd.setEnd(end);
                } catch (ParseException e) {
                    message.replace(0, message.length(), "formato data non valido");
                    return;
                } catch (InvalidParameterException e1) {
                    message.replace(0, message.length(), e1.getMessage());
                    return;
                }
            } else {
                message.replace(0, message.length(), "Giorno di campionamento da modificare non trovato");
                return;
            }
            
            hibSession.merge(sd);
            Sampling.switchOn(sd, hibSession, message);
            if("ok".equals(message.toString())) {
                DroppableMachineEvent.merge(sd, hibSession);
            }
        }
    }
    
    private void maintenance(Long id, HttpServletRequest request) {
        if (!user.getCanAssignJobOrder()) {
            message.replace(0, message.length(), "Non puoi gestire le commesse e nemmeno la manutenzione");
            return;
        } else {
            toEdit = (Maintenance) hibSession.get(Maintenance.class, id);
            
            Maintenance maintenance = new Maintenance();
            
            if (toEdit != null) { // edit
                maintenance = (Maintenance) toEdit;
                // DroppableMachineEvent switch next
                maintenance.setOldStart(maintenance.getStart());
                try {
                    Map<String, String> params = ServletUtils.getParameters(request, new String[] { "start", "end" });
                    Date start = EventUtils.parseDate(params.get("start")), end = EventUtils.parseDate(params
                            .get("end"));
                    maintenance.setStart(start);
                    maintenance.setEnd(end);
                } catch (ParseException e) {
                    message.replace(0, message.length(), "formato data non valido");
                    return;
                } catch (InvalidParameterException e1) {
                    message.replace(0, message.length(), e1.getMessage());
                    return;
                }
            } else {
                message.replace(0, message.length(), "Giorno di manutenzione da modificare non trovato");
                return;
            }
            
            hibSession.merge(maintenance);
            Sampling.switchOn(maintenance, hibSession, message);
            if("ok".equals(message.toString())) {
                DroppableMachineEvent.merge(maintenance, hibSession);
            }
        }
    }
    
    private void nonWorkingDay(Long id, HttpServletRequest request) {
        if (!user.getIsAdmin()) {
            message.replace(0, message.length(), "Non sei admin");
            return;
        } else {
            toEdit = (NonWorkingDay) hibSession.get(NonWorkingDay.class, id);
            
            NonWorkingDay nw = new NonWorkingDay();
            
            if (toEdit != null) {
                nw = (NonWorkingDay) toEdit;
                try {
                    Map<String, String> params = ServletUtils.getParameters(request, new String[] { "start", "end" });
                    nw.setStart(EventUtils.parseDate(params.get("start")));
                    nw.setEnd(EventUtils.parseDate(params.get("end")));
                    if (nw.getEnd().before(nw.getStart()) || nw.getEnd().equals(nw.getStart())) {
                        message.replace(0, message.length(), "Data di inizio e fine evento errate (insensate)");
                        return;
                    }
                } catch (ParseException e) {
                    message.replace(0, message.length(), "formato data non valido");
                    return;
                } catch (InvalidParameterException e1) {
                    message.replace(0, message.length(), e1.getMessage());
                    return;
                }
                
            } else {
                message.replace(0, message.length(), "Giorno non lavorativo da modificare non trovato");
                return;
            }
            
            hibSession.merge(nw);
            NonWorkingDay.shiftMachineEventsRight(nw, hibSession);
            
        }
    }
    
    private void workingDay(Long id, HttpServletRequest request) {
        if (!user.getIsAdmin()) {
            message.replace(0, message.length(), "Non sei admin");
            return;
        } else {
            toEdit = (WorkingDay) hibSession.get(WorkingDay.class, id);
            
            WorkingDay wh = new WorkingDay();
            
            if (toEdit != null) {
                wh = (WorkingDay) toEdit;
                try {
                    Map<String, String> params = ServletUtils.getParameters(request, new String[] { "start", "end" });
                    wh.setStart(EventUtils.parseDate(params.get("start").trim()));
                    wh.setEnd(EventUtils.parseDate(params.get("end").trim()));
                    if (wh.getEnd().before(wh.getStart()) || wh.getEnd().equals(wh.getStart())) {
                        message.replace(0, message.length(), "Data di inizio e fine evento errate (insensate)");
                        return;
                    }
                } catch (ParseException e) {
                    message.replace(0, message.length(), "formato data non valido");
                    return;
                } catch (InvalidParameterException e1) {
                    message.replace(0, message.length(), e1.getMessage());
                    return;
                }
            } else {
                message.replace(0, message.length(), "Ore di lavoro da modificare non trovate");
                return;
            }
            
            hibSession.merge(wh);
        }
    }
    
    private void user_e(Long id, HttpServletRequest request) {
        String[] fields = new String[] { "id", "name", "surname", "username", "password", "canaddjoborder",
                "canassignjoborder", "canaddclient", "canaddmachine" };
        Arrays.sort(fields);
        
        if (!user.getIsAdmin()) {
            message.replace(0, message.length(), "Non sei admin");
            return;
        } else {
            toEdit = (User) hibSession.get(User.class, id);
            
            User u = new User();
            
            if (toEdit != null) { // edit
                try {
                    u = (User) toEdit;
                    Map<String, String> params = ServletUtils.getParameters(request, new String[] { "columnName",
                            "value" });
                    String field = params.get("columnName");
                    if (Arrays.binarySearch(fields, field) == -1) {
                        message.replace(0, message.length(), "Nome colonna non valido");
                        return;
                    }
                    
                    String value = params.get("value");
                    boolean result;
                    
                        switch (field) {
                            case "id":
                                u.setId(Long.parseLong(value));
                                outputResult = value;
                            break;
                            case "name":
                                u.setName(value);
                                outputResult = value;
                            break;
                            case "surname":
                                u.setSurname(value);
                                outputResult = value;
                            break;
                            case "username":
                                u.setUsername(value);
                                outputResult = value;
                            break;
                            case "password":
                                u.setPassword(value);
                                outputResult = value;
                            break;
                            case "canaddjoborder":
                                result = value.equals("Si");
                                outputResult = Boolean.toString(result);
                                u.setCanAddJobOrder(result);
                            break;
                            case "canassignjoborder":
                                result = value.equals("Si");
                                outputResult = Boolean.toString(result);
                                u.setCanAssignJobOrder(result);
                            break;
                            case "canaddmachine":
                                result = value.equals("Si");
                                outputResult = Boolean.toString(result);
                                u.setCanAddMachine(result);
                            break;
                            case "canaddclient":
                                result = value.equals("Si");
                                outputResult = Boolean.toString(result);
                                u.setCanAddClient(result);
                            break;
                            
                            default:
                                message.replace(0, message.length(), "Campo non riconosciuto");
                                return;
                        }// switch
                    
                } catch (InvalidParameterException e1) {
                    message.replace(0, message.length(), e1.getMessage());
                    return;
                }
            } else {
                message.replace(0, message.length(), "Utente da modificare non trovato");
                return;
            }
            
            hibSession.merge(u);
            savedObject = u;
            
        }// isadmin
    }
    
    private void client(Long id, HttpServletRequest request) {
        if (!user.getCanAddClient()) {
            message.replace(0, message.length(), "Non puoi aggiungere clienti");
            return;
        } else {
            String[] fields = new String[] { "name", "code" };
            Arrays.sort(fields);
            toEdit = (Client) hibSession.get(Client.class, id);
            Client c = new Client();
            if (toEdit != null) { // edit
                try {
                    c = (Client) toEdit;
                    Map<String, String> params = ServletUtils.getParameters(request, new String[] { "columnName",
                            "value" });
                    String value = params.get("value");
                    String field = params.get("columnName");
                    
                    if (Arrays.binarySearch(fields, field) == -1) {
                        message.replace(0, message.length(), "Nome colonna non valido");
                        return;
                    } else {
                        switch (field) {
                            case "name":
                                c.setName(value);
                                outputResult = value;
                            break;
                            case "code":
                                c.setCode(value);
                                outputResult = value;
                            break;
                            default:
                                message.replace(0, message.length(), "Campo non riconosciuto");
                                return;
                        }// switch
                    }
                } catch (InvalidParameterException e1) {
                    message.replace(0, message.length(), e1.getMessage());
                    return;
                }
            } else {
                message.replace(0, message.length(), "Cliente da modificare non trovato");
                return;
            }
            
            hibSession.merge(c);
        }
    }
    
    private void machine(Long id, HttpServletRequest request) {
        if (!user.getCanAddMachine()) {
            message.replace(0, message.length(), "Non puoi aggiungere macchine");
            return;
        } else {
            String[] fields = new String[] { "name", "type", "nicety", "color" };
            Arrays.sort(fields);
            toEdit = (Machine) hibSession.get(Machine.class, id);
            
            Machine m = new Machine();
            if (toEdit != null) { // edit
                try {
                    m = (Machine) toEdit;
                    Map<String, String> params = ServletUtils.getParameters(request, new String[] { "columnName",
                            "value" });
                    String field = params.get("columnName");
                    String value = params.get("value");
                    if (Arrays.binarySearch(fields, field) == -1) {
                        message.replace(0, message.length(), "Nome colonna non valido");
                        return;
                    } else {
                        switch (field) {
                            case "name":
                                m.setName(value);
                                outputResult = value;
                            break;
                            case "type":
                                m.setType(value);
                                outputResult = value;
                            break;
                            case "nicety":
                                try {
                                    m.setNicety(Float.parseFloat(value));
                                    outputResult = value;
                                } catch (NumberFormatException e) {
                                    message.replace(0, message.length(), "Valore della finezza non valido");
                                    return;
                                }
                            break;
                            
                            default:
                                message.replace(0, message.length(), "Campo non riconosciuto");
                                return;
                        }// switch
                    }
                } catch (InvalidParameterException e1) {
                    message.replace(0, message.length(), e1.getMessage());
                    return;
                }
            } else {
                message.replace(0, message.length(), "Macchina da modificare non trovata");
                return;
            }
            
            hibSession.merge(m);
        }
    }
    
    private void joborder(Long id, HttpServletRequest request) {
        if (!user.getCanAddJobOrder()) {
            message.replace(0, message.length(), "Non puoi aggiungere commesse");
            return;
        } else {
            String[] fields = new String[] { "client", "leadTime" };
            Arrays.sort(fields);
            toEdit = (JobOrder) hibSession.get(JobOrder.class, id);
            
            JobOrder j = new JobOrder();
            if (toEdit != null) { // edit
                try {
                    j = (JobOrder) toEdit;
                    Map<String, String> params = ServletUtils.getParameters(request, new String[] { "columnName",
                            "value" });
                    String field = params.get("columnName");
                    String value = params.get("value");
                    if (Arrays.binarySearch(fields, field) == -1) {
                        message.replace(0, message.length(), "Nome colonna non valido");
                        return;
                    } else {
                        switch (field) {
                            case "client":
                                try {
                                    Client c = (Client) hibSession.get(Client.class, Long.parseLong(value));
                                    if (c == null) { throw new NumberFormatException(); }
                                    j.setClient(c);
                                } catch (NumberFormatException e) {
                                    message.replace(0, message.length(), "Cliente non trovato");
                                    return;
                                }
                            break;
                            case "numberOfItems":
                                try {
                                    Long lt = Long.parseLong(value);
                                    if (lt <= 0) { throw new NumberFormatException(); }
                                    Long actualAssignedTime = j.getLeadTime() - j.getMissingTime();
                                    if (lt * j.getTimeForItem() < actualAssignedTime) {
                                        message.replace(
                                                0,
                                                message.length(),
                                                "Non puoi modificare il numero di elementi se questa interferisce con le ore già assegnate per la produzione di questa\n"
                                                        + "Per farlo devi prima eliminare le ore in più assegnate.");
                                        return;
                                    }
                                    j.setNumberOfItems(lt);
                                    j.setLeadTime(lt * j.getTimeForItem());
                                    j.setMissingTime(j.getLeadTime() - actualAssignedTime);
                                    j.setMissingTimeWithOffset(j.getLeadTime() + j.getOffset() - actualAssignedTime);
                                    outputResult = value;
                                } catch (NumberFormatException e) {
                                    message.replace(0, message.length(), "Numero di elementi <= 0");
                                    return;
                                }
                            break;
                            case "timeForItem":
                                try {
                                    Long lt = Long.parseLong(value);
                                    if (lt <= 0) { throw new NumberFormatException(); }
                                    Long actualAssignedTime = j.getLeadTime() - j.getMissingTime();
                                    if (lt * j.getNumberOfItems() < actualAssignedTime) {
                                        message.replace(
                                                0,
                                                message.length(),
                                                "Non puoi modificare il tempo per elemento se questa interferisce con le ore già assegnate per la produzione di questa\n"
                                                        + "Per farlo devi prima eliminare le ore in più assegnate.");
                                        return;
                                    }
                                    j.setTimeForItem(lt);
                                    j.setLeadTime(lt * j.getNumberOfItems());
                                    j.setMissingTime(j.getLeadTime() - actualAssignedTime);
                                    j.setMissingTimeWithOffset(j.getLeadTime() + j.getOffset() - actualAssignedTime);
                                    outputResult = value;
                                } catch (NumberFormatException e) {
                                    message.replace(0, message.length(), "Tempo per capo <= 0");
                                    return;
                                }
                            break;
                            case "offset":
                                try {
                                    Long offset = Long.parseLong(value);
                                    Long newMissingTime = j.getMissingTime() + offset;
                                    if (newMissingTime < 0) {
                                        message.replace(0, message.length(),
                                                "Il valore della variazione nterferisce con le ore già assegnate per la produzione di questa\n"
                                                        + "Per farlo devi prima eliminare le ore in più assegnate.");
                                        return;
                                    }
                                    j.setOffset(offset);
                                    j.setMissingTimeWithOffset(newMissingTime);
                                    outputResult = value;
                                } catch (NumberFormatException e) {
                                    message.replace(0, message.length(), "Valore variazione non valido");
                                    return;
                                }
                            break;
                            case "color":
                                outputResult = value;
                                j.setColor(value);
                            break;
                            case "description":
                                outputResult = value;
                                j.setDescription(value);
                            break;
                            default:
                                message.replace(0, message.length(), "Campo non riconosciuto");
                                return;
                        }// switch
                    }
                } catch (InvalidParameterException e1) {
                    message.replace(0, message.length(), e1.getMessage());
                    return;
                }
            } else {
                message.replace(0, message.length(), "Commessa da modificare non trovata");
                return;
            }
            
            hibSession.merge(j);
        }
    }
    
    private void assignedJobOrder(Long id, HttpServletRequest request) {
        if (!user.getCanAddJobOrder()) {
            message.replace(0, message.length(), "Non puoi assegnare commesse");
            return;
        } else {
            String[] fields = new String[] { "start", "end", "machine", "joborder" };
            Arrays.sort(fields);
            toEdit = (AssignedJobOrder) hibSession.get(AssignedJobOrder.class, id);
            
            AssignedJobOrder aj = new AssignedJobOrder();
            if (toEdit != null) { // edit
                try {
                    aj = (AssignedJobOrder) toEdit;
                    // DroppableMachineEvent -> switch next
                    aj.setOldStart(aj.getStart());
                    Map<String, String> params = ServletUtils.getParameters(request, fields);
                    
                    try {
                        aj.setStart(EventUtils.parseDate(params.get("start")));
                        aj.setEnd(EventUtils.parseDate(params.get("end")));
                        
                        if (aj.getEnd().before(aj.getStart()) || aj.getEnd().equals(aj.getStart())) {
                            message.replace(0, message.length(), "Data di inizio e fine evento errate (insensate)");
                            return;
                        }
                    } catch (ParseException e) {
                        message.replace(0, message.length(), "formato data non valido");
                        return;
                    }
                    try {
                        JobOrder j = (JobOrder) hibSession.get(JobOrder.class, Long.parseLong(params.get("joborder")));
                        if (j == null) { throw new NumberFormatException("Commesssa non valida"); }
                        aj.setJobOrder(j);
                        
                        Machine m = (Machine) hibSession.get(Machine.class, Long.parseLong(params.get("machine")));
                        if (m == null) { throw new NumberFormatException("Macchina non valida"); }
                        aj.setMachine(m);
                    } catch (NumberFormatException e) {
                        message.replace(0, message.length(), e.getMessage() + " < ");
                        return;
                    }
                    
                } catch (InvalidParameterException e1) {
                    message.replace(0, message.length(), e1.getMessage());
                    return;
                }
            } else {
                message.replace(0, message.length(), "Commessa da modificare non trovata");
                return;
            }
            
            hibSession.merge(aj);
            AssignedJobOrder.switchOn(aj, hibSession, message);
            if (message.toString().equals("ok")) {
                AssignedJobOrder.merge(aj, hibSession);
            }
            
        }
    }
    
    private void log(Long id, String what, Map<String, String> params, HttpServletRequest request, HttpSession session) {
        if (message.toString().equals("ok")) {
            History h = new History();
            h.setAction("EDIT");
            h.setTime(new Date());
            h.setUser(user);
            
            String updatedField = params.get("columnName");
            // not table but event dragging
            if (updatedField == null) {
                Enumeration<String> parameterNames = request.getParameterNames();
                updatedField = "";
                while (parameterNames.hasMoreElements()) {
                    String paramName = parameterNames.nextElement();
                    updatedField += paramName + " = " + request.getParameterValues(paramName)[0] + "; ";
                }
                
            } else {
                updatedField = updatedField + " = " + params.get("value");
            }
            h.setWhat(what + "(" + id + "): " + updatedField);
            hibSession.merge(h);
            try {
                hibSession.getTransaction().commit();
                if (savedObject instanceof User) {
                    User o = (User) savedObject;
                    if (user.getId().equals(o.getId())) {
                        session.setAttribute(LoginServlet.USER, o);
                    }
                }
            } catch (ConstraintViolationException e) {
                message.replace(0, message.length(), "Identificativo duplicato");
                hibSession.getTransaction().rollback();
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
            out.print("Esegui l'autenticazione");
            return;
        }
        try {
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
            
            outputResult = "";
            message.replace(0, message.length(), "ok");
            switch (what) {
                case "user":
                    user_e(id, request);
                break;
                
                case "nonworkingday":
                    nonWorkingDay(id, request);
                break;
                
                case "workingDay":
                    workingDay(id, request);
                break;
                
                case "sampling":
                    sampling(id, request);
                break;
                
                case "maintenance":
                    maintenance(id, request);
                break;
                
                case "client":
                    client(id, request);
                break;
                
                case "machine":
                    machine(id, request);
                break;
                
                case "joborder":
                    joborder(id, request);
                break;
                
                case "assignedjoborder":
                    assignedJobOrder(id, request);
                break;
                default:
                    out.print("Invalid parameter value for what: " + what);
                    return;
            }
            
            log(id, what, params, request, session);
            
            out.print(outputResult.equals("") ? message.toString() : outputResult);
            
            hibSession.close();
        } catch (IllegalArgumentException e1) {
            out.print(e1.getMessage());
        }
    }
    
}
