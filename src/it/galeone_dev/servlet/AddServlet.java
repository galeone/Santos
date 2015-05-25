package it.galeone_dev.servlet;

import it.galeone_dev.hibernate.HibernateUtils;
import it.galeone_dev.hibernate.abstractions.DroppableMachineEvent;
import it.galeone_dev.hibernate.abstractions.EventUtils;
import it.galeone_dev.hibernate.abstractions.MachineEvent;
import it.galeone_dev.hibernate.models.AssignedJobOrder;
import it.galeone_dev.hibernate.models.Client;
import it.galeone_dev.hibernate.models.DummyMachineEvent;
import it.galeone_dev.hibernate.models.History;
import it.galeone_dev.hibernate.models.JobOrder;
import it.galeone_dev.hibernate.models.Machine;
import it.galeone_dev.hibernate.models.Maintenance;
import it.galeone_dev.hibernate.models.NonWorkingDay;
import it.galeone_dev.hibernate.models.Sampling;
import it.galeone_dev.hibernate.models.User;
import it.galeone_dev.hibernate.models.WorkingDay;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.text.ParseException;
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


public class AddServlet extends HttpServlet {

    private static final long serialVersionUID = 74377157203911L;
    private User user;
    private StringBuilder message = new StringBuilder();
    private Object savedObject = null;
    private Session hibSession;
    private Gson g  = new Gson();
    private boolean needsJson = false;

    @Override
    public void init() throws ServletException {}

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
                    IOException {
        doPost(req, resp);
    }

    private void nonWorkingDay(HttpServletRequest request) {
        if (!user.getIsAdmin()) {
            message.replace(0, message.length(), "Non puoi impostare i giorni non lavorativi");
            return;
        }

        try {
            Map<String, String> params = ServletUtils.getParameters(request, new String[] { "start", "end" });
            NonWorkingDay nw = new NonWorkingDay();
            nw.setStart(EventUtils.parseDate(params.get("start").trim()));
            nw.setEnd(EventUtils.parseDate(params.get("end").trim()));
            if (nw.getEnd().before(nw.getStart()) || nw.getEnd().equals(nw.getStart())) {
                message.replace(0, message.length(), "Data di inizio e fine evento errate (insensate)");
                return;
            }

            hibSession.saveOrUpdate(nw);
            savedObject = nw;

            NonWorkingDay.shiftMachineEventsRight(nw, hibSession);

        } catch (ParseException e) {
            message.replace(0, message.length(), "formato data non valido");
        } catch (InvalidParameterException e1) {
            message.replace(0, message.length(), e1.getMessage());
        }

    }

    private void workingDay(HttpServletRequest request) {
        if (!user.getIsAdmin()) {
            message.replace(0, message.length(), "Non puoi assegnare i giorni lavorativi");
            return;
        }

        try {
            Map<String, String> params = ServletUtils.getParameters(request, new String[] { "start", "end" });
            WorkingDay wh = new WorkingDay();
            wh.setStart(EventUtils.parseDate(params.get("start").trim()));
            wh.setEnd(EventUtils.parseDate(params.get("end").trim()));
            if (wh.getEnd().before(wh.getStart()) || wh.getEnd().equals(wh.getStart())) {
                message.replace(0, message.length(), "Data di inizio e fine evento errate (insensate)");
                return;
            }

            hibSession.saveOrUpdate(wh);
            savedObject = wh;

        } catch (ParseException e) {
            message.replace(0, message.length(), "formato data non valido");
        } catch (InvalidParameterException e1) {
            message.replace(0, message.length(), e1.getMessage());
        }

    }

    private void user_a(HttpServletRequest request) {
        if (!user.getIsAdmin()) {
            message.replace(0, message.length(), "Non sei admin");
            return;
        }

        try {
            Map<String, String> params = ServletUtils.getParameters(request, new String[] { "name",
                            "surname", "username", "password", "canaddjoborder", "canassignjoborder",
                            "canaddclient", "canaddmachine" });
            User u = new User();
            u.setCanAddClient(params.get("canaddclient").equals("Si"));
            u.setCanAddJobOrder(params.get("canaddjoborder").equals("Si"));
            u.setCanAssignJobOrder(params.get("canassignjoborder").equals("Si"));
            u.setCanAddMachine(params.get("canaddmachine").equals("Si"));
            u.setIsAdmin(false);
            u.setName(params.get("name"));
            u.setPassword(params.get("password"));
            u.setSurname(params.get("surname"));
            u.setUsername(params.get("username"));
            u.setHistory(new HashSet<History>());
            hibSession.saveOrUpdate(u);
            savedObject = u;
            needsJson = true;
        } catch (InvalidParameterException e1) {
            message.replace(0, message.length(), e1.getMessage());
        }

    }

    private void client(HttpServletRequest request) {
        if (!user.getCanAddClient()) {
            message.replace(0, message.length(), "Non puoi aggiungere clienti");
            return;
        }

        try {
            Map<String, String> params = ServletUtils.getParameters(request, new String[] { "name", "code" });
            Client c = new Client();
            c.setCode(params.get("code"));
            c.setName(params.get("name"));
            c.setJobOrders(new HashSet<JobOrder>());
            hibSession.saveOrUpdate(c);
            savedObject = c;
            needsJson = true;
        } catch (InvalidParameterException e1) {
            message.replace(0, message.length(), e1.getMessage());
        }

    }

    private void machine(HttpServletRequest request) {
        if (!user.getCanAddMachine()) {
            message.replace(0, message.length(), "Non puoi aggiungere macchine");
            return;
        }

        try {
            Map<String, String> params = ServletUtils.getParameters(request, new String[] { "name", "type",
                            "nicety" });
            Machine m = new Machine();
            m.setName(params.get("name"));
            m.setNicety(Float.parseFloat(params.get("nicety")));
            m.setType(params.get("type"));
            m.setAssignedJobOrders(new HashSet<AssignedJobOrder>());
            hibSession.saveOrUpdate(m);
            savedObject = m;
            needsJson = true;
        } catch (NumberFormatException e) {
            message.replace(0, message.length(), "Valore della finezza non valido");
        } catch (InvalidParameterException e1) {
            message.replace(0, message.length(), e1.getMessage());
        }

    }

    private void jobOrder(HttpServletRequest request) {
        if (!user.getCanAddJobOrder()) {
            message.replace(0, message.length(), "Non puoi aggiungere commesse");
            return;
        }

        try {
            Map<String, String> params = ServletUtils.getParameters(request, new String[] { "leadtime",
                            "client", "color", "numberofitems", "timeforitem", "description", "offset" });

            JobOrder j = new JobOrder();
            Long id = null;
            try {
                id = Long.parseLong(params.get("client"));
            } catch (NumberFormatException e) {
                message.replace(0, message.length(), "Valore cliente non valido");
                return;
            }
            Client c = (Client) hibSession.get(Client.class, id);
            if (c == null) {
                message.replace(0, message.length(), "Cliente non trovato");
                return;
            }

            j.setAssignedJobOrders(new HashSet<AssignedJobOrder>());
            j.setClient(c);
            Long leadTime = Long.parseLong(params.get("leadtime"));
            if (leadTime <= 0) {
                message.replace(0, message.length(), "Tempo di produzione <= 0");
                return;
            }
            Long numberOfItems = Long.parseLong(params.get("numberofitems"));
            if (numberOfItems <= 0) {
                message.replace(0, message.length(), "Numero di capi <= 0");
                return;
            }
            Long timeForItem = Long.parseLong(params.get("timeforitem"));
            if (timeForItem <= 0) {
                message.replace(0, message.length(), "Tempo per capo <= 0");
                return;
            }

            j.setOffset(Long.parseLong(params.get("offset")));

            leadTime = timeForItem * numberOfItems;
            j.setNumberOfItems(numberOfItems);
            j.setTimeForItem(timeForItem);
            j.setLeadTime(leadTime);
            j.setMissingTime(leadTime);
            j.setColor(params.get("color"));
            j.setDescription(params.get("description"));

            hibSession.saveOrUpdate(j);
            savedObject = j;
            needsJson = true;
        } catch (NumberFormatException e) {
            message.replace(0, message.length(), "Tempo di produzione non valido");
        } catch (InvalidParameterException e1) {
            message.replace(0, message.length(), e1.getMessage());
        }

    }

    private AssignedJobOrder addOneAssignedJobOrder(JobOrder j, Machine m, Date start, Date end) {
        AssignedJobOrder aj = new AssignedJobOrder();
        aj.setJobOrder(j);
        MachineEvent addedEvent = addOneMachineEvent(aj, m, start, end);
        j.setMissingTime(j.getMissingTime() - EventUtils.getLast(addedEvent));
        hibSession.saveOrUpdate(j);
        return (AssignedJobOrder) addedEvent;
    }

    private void addOneSampling(String description, Client c, Machine m, Date start, Date end) {
        Sampling s = new Sampling();
        s.setClient(c);
        s.setDescription(description);
        addOneMachineEvent(s, m, start, end);
    }

    private void addOneMaintenance(String description, Machine m, Date start, Date end) {
        Maintenance maintenance = new Maintenance();
        maintenance.setDescription(description);
        addOneMachineEvent(maintenance, m, start, end);
    }

    public MachineEvent addOneMachineEvent(DroppableMachineEvent event, Machine m, Date start, Date end) {
        event.setMachine(m);
        event.setStart(start);
        event.setEnd(end);

        if (event.getStart().after(event.getEnd())
                        || EventUtils.getLast(event) > EventUtils.getLast(WorkingDay.get(event.getStart()))) {
            message.replace(0, message.length(),
                            "Orario errato. O maggiore delle ore lavorative previste o fine precedente ad inizio");
            return null;
        }

        hibSession.saveOrUpdate(event);

        savedObject = event;

        DroppableMachineEvent.shiftRight(event, hibSession);
        return event;
    }
    
    private void autoAddMachineEvent(HttpServletRequest request) {
        try {
            Map<String, String> params = ServletUtils.getParameters(request,
                            new String[] { "start", "machine" },
                            new String[] { "end", "joborder", "client", "description" });
            
            Long id = null;
            
            try {
                id = Long.parseLong(params.get("machine"));
            } catch (NumberFormatException e) {
                message.replace(0, message.length(), "Valore macchine non valido");
                return;
            }
            Machine m = (Machine) hibSession.get(Machine.class, id);
            if (m == null) {
                message.replace(0, message.length(), "Macchina non trovata");
                return;
            }
            
            
            Date start = EventUtils.start(EventUtils.parseDate(params.get("start"))), end = null;
            
            if(params.get("end") != null) {
                end = EventUtils.parseDate(params.get("end"));
            }
            
            JobOrder j = null; 
            if(params.get("joborder") != null) {
                try {
                    id = Long.parseLong(params.get("joborder"));
                } catch (NumberFormatException e) {
                    message.replace(0, message.length(), "Valore commessa non valido");
                    return;
                }
                j = (JobOrder) hibSession.get(JobOrder.class, id);
                if (j == null) {
                    message.replace(0, message.length(), "Commessa non trovata");
                    return;
                }
            }
            
            Client c = null;
            if(params.get("client") != null) {
                try {
                    id = Long.parseLong(params.get("client"));
                } catch (NumberFormatException e) {
                    message.replace(0, message.length(), "Valore commessa non valido");
                    return;
                }
                c = (Client) hibSession.get(Client.class, id);
                if (c == null) {
                    message.replace(0, message.length(), "Cliente non trovato");
                    return;
                }
            }
            
            String description = params.get("description");
            
            MachineEvent dummy = new DummyMachineEvent();
            dummy.setStart(start);
            if(end == null) {
                end = new Date(start.getTime() + EventUtils.getLast( WorkingDay.get(start) ) * 60000);
            }
            dummy.setEnd(end);
            dummy.setMachine(m);
                        
            Long myLast = EventUtils.getLast(dummy);
            Long last = WorkingDay.getWorkingHoursBetween(dummy), removedTime = 0L;
            
            Class<? extends MachineEvent> eventType;
            if(c != null && description != null) {
                eventType = Sampling.class;
            } else if(description != null) {
                eventType = Maintenance.class;
            } else if(j != null) {
                eventType = AssignedJobOrder.class;
            } else {
                message.replace(0, message.length(), "Evento non riconosciuto");
                return;
            }
            
            if(myLast < last) {
                last = myLast;
            }
            
            if(eventType.equals(AssignedJobOrder.class)) {
                Long missingTime = j.getMissingTime();
                if(end.before(start) || missingTime < last) {
                    last = missingTime;
                }
            }
            
            Date prev = new Date(dummy.getStart().getTime());
            while (last > 0) {
                dummy.setStart(prev);
                Long hoursPerDay = EventUtils.getLast(WorkingDay.get(dummy.getStart()));
                Long howLong = last > hoursPerDay ? hoursPerDay : last;

                end = new Date(prev.getTime() + howLong * 60000);
                dummy.setEnd(end);
                
                if(eventType.equals(Sampling.class)) {
                    addOneSampling(description, c, m, prev, end);
                } else if(eventType.equals(Maintenance.class)) {
                    addOneMaintenance(description, m, prev, end);
                } else if(eventType.equals(AssignedJobOrder.class)) {
                    AssignedJobOrder added = addOneAssignedJobOrder(j, m, prev, end);
                    AssignedJobOrder.merge(added, hibSession);
                }

                last -= howLong;
                removedTime += howLong;
                prev = EventUtils.tomorrow(end);
            }

            message.replace(0, message.length(), Long.toString(removedTime));

        } catch (NumberFormatException e) {
            message.replace(0, message.length(), "Macchina non valida");
        } catch (ParseException e) {
            message.replace(0, message.length(), "Data inizio/fine non valida");
        } catch (InvalidParameterException e1) {
            message.replace(0, message.length(), e1.getMessage());
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
            message.replace(0, message.length(), "Esiste già un record con questo nome");
            hibSession.getTransaction().rollback();
        }
    }
    
    private void buildMessage() {
        if(savedObject != null && needsJson) {
            message.replace(0, message.length(), g.toJson(savedObject));
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
        
        try {
            Map<String, String> params = ServletUtils.getParameters(request, new String[] { "what" });
            
            message.replace(0, message.length(), "ok");
            needsJson = false;

            hibSession = HibernateUtils.getSessionFactory().openSession();
            hibSession.beginTransaction();

            switch (params.get("what")) {
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
            case "sampling":
            case "maintenance":
                autoAddMachineEvent(request);
                break;

            case "nonworkingday":
                nonWorkingDay(request);
                break;

            case "workingday":
                workingDay(request);
                break;

            default:
                out.print("Invalid parameter value for what: " + params.get("what"));
                return;
            }

            log();
            
            buildMessage();

            out.print(message.toString());
            hibSession.close();

        } catch (InvalidParameterException e1) {
            out.print("Parametro GET what necessario");
        }
    }

}
