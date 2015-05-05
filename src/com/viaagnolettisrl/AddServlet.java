package com.viaagnolettisrl;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
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
                    
                    SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH);
                    s.setEnd(sdf.parse(params.get("end")));
                    s.setStart(sdf.parse(params.get("start")));
                    
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
        
        message = "ok";
        hibSession = HibernateUtil.getSessionFactory().openSession();
        hibSession.beginTransaction();
        switch (what) {
            case "user":
                if (!user.getIsAdmin()) {
                    message = "Non sei admin";
                } else {
                    String[] fields = new String[] { "name", "surname", "username", "password", "canaddjoborder",
                            "canaddclient", "canaddmachine" };
                    Arrays.sort(fields);
                    params = ServletUtils.getParameters(request, fields);
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
                    
                }// isadmin
            break;
            
            case "client":
                if (!user.getCanAddClient()) {
                    message = "Non puoi aggiungere clienti";
                } else {
                    String[] fields = new String[] { "name", "code" };
                    Arrays.sort(fields);
                    params = ServletUtils.getParameters(request, fields);
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
            break;
            
            case "machine":
                if (!user.getCanAddMachine()) {
                    message = "Non puoi aggiungere macchine";
                } else {
                    String[] fields = new String[] { "name", "type", "nicety" };
                    Arrays.sort(fields);
                    params = ServletUtils.getParameters(request, fields);
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
            break;
            
            case "joborder":
                if (!user.getCanAddJobOrder()) {
                    message = "Non puoi aggiungere commesse";
                } else {
                    String[] fields = new String[] { "leadtime", "client", "color", "numberofitems", "timeforitem", "description" };
                    Arrays.sort(fields);
                    params = ServletUtils.getParameters(request, fields);
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
                                break;
                            }
                            Client c = (Client) hibSession.get(Client.class, id);
                            if (c == null) {
                                message = "Cliente non trovato";
                                break;
                            }
                            
                            j.setAssignedJobOrders(new HashSet<AssignedJobOrder>());
                            j.setClient(c);
                            Long leadTime = Long.parseLong(params.get("leadtime"));
                            if (leadTime <= 0) {
                                message = "Tempo di produzione <= 0";
                                break;
                            }
                            Long numberOfItems = Long.parseLong(params.get("numberofitems"));
                            if(numberOfItems <= 0) {
                                message = "Numero di capi <= 0";
                                break;
                            }
                            Long timeForItem = Long.parseLong(params.get("timeforitem"));
                            if(timeForItem <= 0) {
                                message = "Tempo per capo <= 0";
                                break;
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
            break;
            
            case "assignedjoborder":
                if (!user.getCanAddJobOrder()) {
                    message = "Non puoi aggiungere commesse";
                } else {
                    String[] fields = new String[] { "start", "end", "machine", "joborder" };
                    Arrays.sort(fields);
                    params = ServletUtils.getParameters(request, fields);
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
                                break;
                            }
                            Machine m = (Machine) hibSession.get(Machine.class, id);
                            if (m == null) {
                                message = "Macchina non trovata";
                                break;
                            }
                            
                            aj.setMachine(m);
                            
                            try {
                                id = Long.parseLong(params.get("joborder"));
                            } catch (NumberFormatException e) {
                                message = "Valore commessa non valido";
                                break;
                            }
                            JobOrder j = (JobOrder) hibSession.get(JobOrder.class, id);
                            if (j == null) {
                                message = "Commessa non trovata";
                                break;
                            }
                            
                            aj.setJobOrder(j);
                            
                            SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH);
                            aj.setEnd(sdf.parse(params.get("end")));
                            aj.setStart(sdf.parse(params.get("start")));
                            
                            hibSession.saveOrUpdate(aj);
                            
                            message = g.toJson(aj);
                            savedObject = aj;
                            
                        } catch (NumberFormatException e) {
                            message = "Macchina non valida";
                        } catch (ParseException e) {
                            message = "Data inizio/fine non valida";
                        }
                    }
                }
            break;
            
            case "nonworkingday":
                if (!user.getIsAdmin()) {
                    message = "Non puoi aggiungere giorni non lavorativi";
                } else {
                    String dateS = request.getParameter("date");
                    if (dateS == null || "".equals(dateS)) {
                        message = "Completare tutti i campi";
                    } else {
                        try {
                            NonWorkingDay nw = new NonWorkingDay();
                            SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH);
                            Date d = sdf.parse(dateS);
                            nw.setStart(d);
                            nw.setEnd(d);
                            
                            hibSession.saveOrUpdate(nw);
                            message = g.toJson(nw);
                            savedObject = nw;
                            
                            NonWorkingDay.shiftRight(nw, hibSession);
                            
                        } catch (ParseException e) {
                            message = "formato data non valido";
                        }
                    }
                }
            break;
            
            case "sampling":
                sampling(request);
            break;
        }
        
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
        
        out.print(message);
        hibSession.close();
    }
    
}
