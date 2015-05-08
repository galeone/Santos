package com.viaagnolettisrl;

import java.io.IOException;
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

import com.viaagnolettisrl.hibernate.AssignedJobOrder;
import com.viaagnolettisrl.hibernate.Client;
import com.viaagnolettisrl.hibernate.HibernateUtil;
import com.viaagnolettisrl.hibernate.History;
import com.viaagnolettisrl.hibernate.JobOrder;
import com.viaagnolettisrl.hibernate.Machine;
import com.viaagnolettisrl.hibernate.NonWorkingDay;
import com.viaagnolettisrl.hibernate.Sampling;
import com.viaagnolettisrl.hibernate.User;

public class EditServlet extends HttpServlet {
    
    private static final long serialVersionUID = 74377157203911L;
    private String message, outputResult = "";
    private User user;
    private Object toEdit, savedObject;
    private Session hibSession;
    
    @Override
    public void init() throws ServletException {
    }
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }
    
    private void sampling(Long id, HttpServletRequest request) {

        if (!user.getCanAddJobOrder()) {
            message = "Non puoi gestire le commesse e nemmeno i campionamenti";
        } else {
            
            toEdit = (Sampling) hibSession.get(Sampling.class, id);
            
            Sampling sd = new Sampling();
            
            if (toEdit != null) { // edit
                sd = (Sampling) toEdit;
                //DroppableMachineEvent switch next
                sd.setOldStart(sd.getStart());
                Map<String,String> params = ServletUtils.getParameters(request, new String[]{"start", "end"});
                String startS = params.get("start"), endS = params.get("end");

                if (startS == null || "".equals(startS.trim()) || endS == null || "".equals(endS.trim())) {
                    message = "Il campo non può essere vuoto";
                } else {
                    try {
                        Date start = EventUtils.parseDate(startS.trim()), end = EventUtils.parseDate(endS.trim());
                        sd.setStart(start);
                        sd.setEnd(end);
                        message = "ok";
                    } catch (ParseException e) {
                        message = "formato data non valido";
                    }
                }
            } else {
                message = "Giorno di campionamento da modificare non trovato";
            }
            
            if (message.equals("ok")) {
                hibSession.saveOrUpdate(sd);
                Sampling.switchOnNext(sd, hibSession);
            }
        }
    }
    
    private void nonWorkingDay(Long id, HttpServletRequest request) {
        if (!user.getIsAdmin()) {
            message = "Non sei admin";
        } else {
            toEdit = (NonWorkingDay) hibSession.get(NonWorkingDay.class, id);
            
            NonWorkingDay nw = new NonWorkingDay();
            
            if (toEdit != null) {
                nw = (NonWorkingDay) toEdit;
                String[] fields = new String[] { "start", "end" };
                Arrays.sort(fields);
                Map<String, String> params = ServletUtils.getParameters(request, fields);
                if (params.containsValue(null) || params.containsValue("")) {
                    message = "Completare tutti i campi";
                } else {
                    try {
                        nw.setStart(EventUtils.parseDate(params.get("start").trim()));
                        nw.setEnd(EventUtils.parseDate(params.get("end").trim()));
                        if(nw.getEnd().before(nw.getStart()) || nw.getEnd().equals(nw.getStart())) {
                            message = "Data di inizio e fine evento errate (insensate)";
                        } else {
                            message = "ok";
                        }
                    } catch (ParseException e) {
                        message = "formato data non valido";
                    }
                }
            } else {
                message = "Giorno non lavorativo da modificare non trovato";
            }
            
            if (message.equals("ok")) {
                hibSession.saveOrUpdate(nw);
                NonWorkingDay.shiftMachineEventsRight(nw, hibSession);
            }
        }
    }
    
    private void user_e(Long id, HttpServletRequest request) {
        String[] fields = new String[] { "id", "name", "surname", "username", "password", "canaddjoborder", "canaddclient", "canaddmachine" };
        Arrays.sort(fields);
        
        if (!user.getIsAdmin()) {
            message = "Non sei admin";
        } else {
            toEdit = (User) hibSession.get(User.class, id);
            
            User u = new User();
            
            if (toEdit != null) { // edit
                u = (User) toEdit;
                Map<String, String> params = ServletUtils.getParameters(request, new String[] { "columnName", "value" });
                String field = params.get("columnName");
                if (params.containsValue(null) || params.containsValue("")) {
                    message = "Richiesta di edit errata";
                } else if (Arrays.binarySearch(fields, field) == -1) {
                    message = "Nome colonna non valido";
                }
                
                String value = params.get("value");
                message = "ok";
                boolean result;
                if (message.equals("ok")) {
                    switch (field) {
                        case "id":
                            u.setId(Long.parseLong(value));
                        break;
                        case "name":
                            u.setName(value);
                        break;
                        case "surname":
                            u.setSurname(value);
                        break;
                        case "username":
                            u.setUsername(value);
                        break;
                        case "password":
                            u.setPassword(value);
                        break;
                        case "canaddjoborder":
                            result = value.equals("Si");
                            outputResult = Boolean.toString(result);
                            u.setCanAddJobOrder(result);
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
                            message = "Campo non riconosciuto";
                        break;
                    }// switch
                }
            } else {
                message = "Utente da modificare non trovato";
            }
            
            if (message.equals("ok")) {
                hibSession.saveOrUpdate(u);
                savedObject = u;
            }
        }// isadmin
    }
    
    private void client(Long id, HttpServletRequest request) {
        if (!user.getCanAddClient()) {
            message = "Non puoi aggiungere clienti";
        } else {
            String[] fields = new String[] { "name", "code" };
            Arrays.sort(fields);
            toEdit = (Client) hibSession.get(Client.class, id);
            Client c = new Client();
            if (toEdit != null) { // edit
                c = (Client) toEdit;
                Map<String, String>params = ServletUtils.getParameters(request, new String[] { "columnName", "value" });
                String value = params.get("value");
                String field = params.get("columnName");
                if (params.containsValue(null) || params.containsValue("")) {
                    message = "Richiesta di edit errata";
                } else if (Arrays.binarySearch(fields, field) == -1) {
                    message = "Nome colonna non valido";
                } else if (value == null || "".equals(value.trim())) {
                    message = "Il campo non può essere vuoto";
                } else {
                    message = "ok";
                    switch (field) {
                        case "name":
                            c.setName(value);
                        break;
                        case "code":
                            c.setCode(value);
                        break;
                        default:
                            message = "Campo non riconosciuto";
                        break;
                    }// switch
                }
            } else {
                message = "Cliente da modificare non trovato";
            }
            
            if (message.equals("ok")) {
                hibSession.saveOrUpdate(c);
            }
        }
    }
    
    private void machine(Long id, HttpServletRequest request) {
        if (!user.getCanAddMachine()) {
            message = "Non puoi aggiungere macchine";
        } else {
            String[] fields = new String[] { "name", "type", "nicety", "color" };
            Arrays.sort(fields);
            toEdit = (Machine) hibSession.get(Machine.class, id);
            
            Machine m = new Machine();
            if (toEdit != null) { // edit
                m = (Machine) toEdit;
                Map<String, String> params = ServletUtils.getParameters(request, new String[] { "columnName", "value" });
                String field = params.get("columnName");
                String value = params.get("value");
                if (params.containsValue(null) || params.containsValue("")) {
                    message = "Richiesta di edit errata";
                } else if (Arrays.binarySearch(fields, field) == -1) {
                    message = "Nome colonna non valido";
                } else if (value == null || "".equals(value.trim())) {
                    message = "Il campo non può essere vuoto";
                } else {
                    message = "ok";
                    switch (field) {
                        case "name":
                            m.setName(value);
                        break;
                        case "type":
                            m.setType(value);
                        break;
                        case "nicety":
                            try {
                                m.setNicety(Float.parseFloat(value));
                            } catch (NumberFormatException e) {
                                message = "Valore della finezza non valido";
                            }
                        break;
                        
                        default:
                            message = "Campo non riconosciuto";
                        break;
                    }// switch
                }
            } else {
                message = "Macchina da modificare non trovata";
            }
            
            if (message.equals("ok")) {
                hibSession.saveOrUpdate(m);
            }
        }
    }
    
    private void joborder(Long id, HttpServletRequest request) {
        if (!user.getCanAddJobOrder()) {
            message = "Non puoi aggiungere commesse";
        } else {
            String[] fields = new String[] { "client", "leadTime" };
            Arrays.sort(fields);
            toEdit = (JobOrder) hibSession.get(JobOrder.class, id);
            
            JobOrder j = new JobOrder();
            if (toEdit != null) { // edit
                j = (JobOrder) toEdit;
                Map<String, String> params = ServletUtils.getParameters(request, new String[] { "columnName", "value" });
                String field = params.get("columnName");
                String value = params.get("value");
                if (params.containsValue(null) || params.containsValue("")) {
                    message = "Richiesta di edit errata";
                } else if (Arrays.binarySearch(fields, field) == -1) {
                    message = "Nome colonna non valido";
                } else if (value == null || "".equals(value.trim())) {
                    message = "Il campo non può essere vuoto";
                } else {
                    message = "ok";
                    switch (field) {
                        case "client":
                            try {
                                Client c = (Client) hibSession.get(Client.class, Long.parseLong(value));
                                if (c == null) { throw new NumberFormatException(); }
                                j.setClient(c);
                            } catch (NumberFormatException e) {
                                message = "Cliente non trovato";
                            }
                        break;
                        case "numberOfItems":
                            try {
                                Long lt = Long.parseLong(value);
                                if (lt <= 0) { throw new NumberFormatException(); }
                                j.setNumberOfItems(lt);
                                j.setLeadTime(lt * j.getTimeForItem());
                                outputResult = value;
                            } catch (NumberFormatException e) {
                                message = "Numero di elementi <= 0";
                            }
                        break;
                        case "timeForItem":
                            try {
                                Long lt = Long.parseLong(value);
                                if (lt <= 0) { throw new NumberFormatException(); }
                                j.setTimeForItem(lt);
                                j.setLeadTime(lt * j.getNumberOfItems());
                                outputResult = value;
                            } catch (NumberFormatException e) {
                                message = "Tempo per capo <= 0";
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
                            message = "Campo non riconosciuto";
                        break;
                    }// switch
                }
            } else {
                message = "Commessa da modificare non trovata";
            }
            
            if (message.equals("ok")) {
                hibSession.saveOrUpdate(j);
            }
        }
    }
    
    private void assignedJobOrder(Long id, HttpServletRequest request) {
        if (!user.getCanAddJobOrder()) {
            message = "Non puoi assegnare commesse";
        } else {
            String[] fields = new String[] { "start", "end", "machine", "joborder" };
            Arrays.sort(fields);
            toEdit = (AssignedJobOrder) hibSession.get(AssignedJobOrder.class, id);
            
            AssignedJobOrder aj = new AssignedJobOrder();
            if (toEdit != null) { // edit
                aj = (AssignedJobOrder) toEdit;
                // DroppableMachineEvent -> switch next
                aj.setOldStart(aj.getStart());
                Map<String,String> params = ServletUtils.getParameters(request, fields);
                String startS = params.get("start"), endS = params.get("end");
                
                if (params.containsValue(null) || params.containsValue("")) {
                    message = "Richiesta di edit errata";
                } else if (startS == null || "".equals(startS.trim()) || endS == null || "".equals(endS.trim())) {
                    message = "Il campo non può essere vuoto";
                } else {
                    message = "ok";
                    try {
                        aj.setStart(EventUtils.parseDate(startS.trim()));
                        aj.setEnd(EventUtils.parseDate(endS.trim()));

                        if(aj.getEnd().before(aj.getStart()) || aj.getEnd().equals(aj.getStart())) {
                            message = "Data di inizio e fine evento errate (insensate)";
                            return;
                        }
                    } catch (ParseException e) {
                        message = "formato data non valido";
                    }
                    try {
                        JobOrder j = (JobOrder) hibSession.get(JobOrder.class, Long.parseLong(params.get("joborder")));
                        if (j == null) { throw new NumberFormatException("Commesssa non valida"); }
                        aj.setJobOrder(j);
                        
                        Machine m = (Machine) hibSession.get(Machine.class, Long.parseLong(params.get("machine")));
                        if (m == null) { throw new NumberFormatException("Macchina non valida"); }
                        aj.setMachine(m);
                    } catch (NumberFormatException e) {
                        message = e.getMessage() + " < ";
                    }
                }
            } else {
                message = "Commessa da modificare non trovata";
            }
            
            if (message.equals("ok")) {
                hibSession.saveOrUpdate(aj);
                AssignedJobOrder.switchOnNext(aj, hibSession);
            }
        }
    }
    
    private void log(Long id, String what, Map<String, String> params, HttpServletRequest request, HttpSession session) {
        if (message.equals("ok")) {
            History h = new History();
            h.setAction("EDIT");
            h.setTime(new Date());
            h.setUser(user);

            String updatedField = params.get("columnName");
            // not table but event dragging
            if(updatedField == null) {
                Enumeration<String> parameterNames = request.getParameterNames();
                updatedField = "";
                while (parameterNames.hasMoreElements()) {
                    String paramName =  parameterNames.nextElement();
                    updatedField += paramName + " = " + request.getParameterValues(paramName)[0] + "; ";
                }

            } else {
                updatedField = updatedField + " = " + params.get("value");
            }
            h.setWhat(what + "(" + id + "): " + updatedField);
            hibSession.saveOrUpdate(h);
            try {
                hibSession.getTransaction().commit();
                if (savedObject instanceof User) {
                    User o = (User) savedObject;
                    if (user.getId().equals(o.getId())) {
                        session.setAttribute(LoginServlet.USER, o);
                    }
                }
            } catch (ConstraintViolationException e) {
                message = "Identificativo duplicato";
                hibSession.getTransaction().rollback();
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
                user_e(id, request);
            break;
            
            case "nonworkingday":
                nonWorkingDay(id, request);
            break;
            
            case "sampling":
                sampling(id, request);
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
        
        out.print(outputResult.equals("") ? message : outputResult);
        
        hibSession.close();
    }
    
}
