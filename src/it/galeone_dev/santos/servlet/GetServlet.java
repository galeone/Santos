package it.galeone_dev.santos.servlet;

import it.galeone_dev.santos.GetCollection;
import it.galeone_dev.santos.hibernate.HibernateUtils;
import it.galeone_dev.santos.hibernate.abstractions.Event;
import it.galeone_dev.santos.hibernate.abstractions.MachineCalendar;
import it.galeone_dev.santos.hibernate.abstractions.MachineEvent;
import it.galeone_dev.santos.hibernate.models.AssignedJobOrder;
import it.galeone_dev.santos.hibernate.models.Machine;
import it.galeone_dev.santos.hibernate.models.Maintenance;
import it.galeone_dev.santos.hibernate.models.Sampling;
import it.galeone_dev.santos.hibernate.models.User;


import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.hibernate.Session;

import com.google.gson.Gson;

public class GetServlet extends HttpServlet {
    
    private static final long serialVersionUID = 74377157203911L;
    private Gson gson = new Gson();
    private User user;
    public static HashMap<Integer, String> itMonth = new HashMap<Integer, String>();
    
    @Override
    public void init() throws ServletException {
        itMonth.put(Calendar.JANUARY, "gen");
        itMonth.put(Calendar.FEBRUARY, "feb");
        itMonth.put(Calendar.MARCH, "mar");
        itMonth.put(Calendar.APRIL, "apr");
        itMonth.put(Calendar.MAY, "mag");
        itMonth.put(Calendar.JUNE, "giu");
        itMonth.put(Calendar.JULY, "lug");
        itMonth.put(Calendar.AUGUST, "ago");
        itMonth.put(Calendar.SEPTEMBER, "set");
        itMonth.put(Calendar.OCTOBER, "ott");
        itMonth.put(Calendar.NOVEMBER, "nov");
        itMonth.put(Calendar.DECEMBER, "dic");
    }
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }
    private Collection<AssignedJobOrder> assignedJobOrders(Machine m, Date start, Date end) {
        return GetCollection.setAssignedJobOrderAttr(
                GetCollection.assignedJobOrdersBetween(m, start, end),
                user.getCanAssignJobOrder());
    }
    private Collection<AssignedJobOrder> assignedJobOrders(HttpServletRequest request, Date start, Date end) throws Exception {
        String machine;
        if((machine = request.getParameter("machine")) == null || machine.isEmpty()) {
            throw new Exception("error, machine required");
        }
        Long machineId;
        Machine m;
        try {
            machineId = Long.parseLong(machine);
            Session hibSession = HibernateUtils.getSessionFactory().openSession();
            hibSession.beginTransaction();
            m = (Machine) hibSession.get(Machine.class, machineId);
            hibSession.close();
            if(m == null) {
                throw new NumberFormatException();
            }
            return assignedJobOrders(m, start, end);
            
        }catch(NumberFormatException e) {
            throw new Exception("Id macchina non valido");
        }        
    }
    
    private Collection<Sampling> sampling(Machine m, Date start, Date end) {
        return GetCollection.setSamplingAttr(
                GetCollection.samplingBetween(m, start, end),
                user.getCanAssignJobOrder());
    }
    private Collection<Sampling> sampling(HttpServletRequest request, Date start, Date end) throws Exception {
        String machine;
        if((machine = request.getParameter("machine")) == null || machine.isEmpty()) {
            throw new Exception("error, machine required");
        }
        Long machineId;
        Machine m;
        try {
            machineId = Long.parseLong(machine);
            Session hibSession = HibernateUtils.getSessionFactory().openSession();
            hibSession.beginTransaction();
            m = (Machine) hibSession.get(Machine.class, machineId);
            hibSession.close();
            if(m == null) {
                throw new NumberFormatException();
            }
            
        }catch(NumberFormatException e) {
            throw new Exception("Id macchina non valido");
        }
        return sampling(m, start, end);
    }
    
    private Collection<Maintenance> maintenance(Machine m, Date start, Date end) {
        return GetCollection.setMaintenanceAttr(
                GetCollection.maintenanceBetween(m, start, end),
                user.getCanAssignJobOrder());
    }
    private Collection<Maintenance> maintenance(HttpServletRequest request, Date start, Date end) throws Exception {
        String machine;
        if((machine = request.getParameter("machine")) == null || machine.isEmpty()) {
            throw new Exception("error, machine required");
        }
        Long machineId;
        Machine m;
        try {
            machineId = Long.parseLong(machine);
            Session hibSession = HibernateUtils.getSessionFactory().openSession();
            hibSession.beginTransaction();
            m = (Machine) hibSession.get(Machine.class, machineId);
            hibSession.close();
            if(m == null) {
                throw new NumberFormatException();
            }
            
        }catch(NumberFormatException e) {
            throw new Exception("Id macchina non valido");
        }
        return maintenance(m, start, end);
    }
    
    public Date getDate(HttpServletRequest request, String name) {
        String dateS = request.getParameter(name);
        if (dateS == null || "".equals(dateS)) {
            return null;
        } else {
            try {
            	// Do not replace with EventUtils.parseDate (different parse string)
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                return sdf.parse(dateS);
            } catch (ParseException e) {
                return null;
            }
        }
    }
    
    public Collection<MachineEvent> getMachineEvents(Machine m, Date start, Date end) throws Exception {
        Collection<MachineEvent> c = new HashSet<MachineEvent>();
        c.addAll(assignedJobOrders(m, start, end));
        c.addAll(sampling(m, start, end));
        c.addAll(maintenance(m, start, end));
        return c;
    }
    
    public Collection<MachineEvent> getMachineEvents(HttpServletRequest request, Date start, Date end) throws Exception {
        Collection<MachineEvent> c = new HashSet<MachineEvent>();
        c.addAll(assignedJobOrders(request, start, end));
        c.addAll(sampling(request, start, end));
        c.addAll(maintenance(request, start, end));
        return c;
    }
    
    private Collection<Event> getProgram(HttpServletRequest request, Date start, Date end) throws Exception {
        Collection<Event> c = new HashSet<Event>();
        c.addAll(GetCollection.globalEventsBetween(user.getIsAdmin(), start, end));
        c.addAll(getMachineEvents(request, start, end));
        return c;
    }
    
    private Collection<Event> getProgram(Machine m, Date start, Date end) throws Exception {
        Collection<Event> c = new HashSet<Event>();
        c.addAll(GetCollection.globalEventsBetween(user.getIsAdmin(), start, end));
        c.addAll(getMachineEvents(m, start, end));
        return c;
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
        
        String what = request.getParameter("what");
        if (what == null || what.isEmpty()) {
            out.println("error, invalid parameter what");
            return;
        }
        
        Date start = getDate(request, "start");
        Date end = getDate(request, "end");
        
        Collection<Object> c = new HashSet<Object>();
        switch (what) {
            case "nonworkingdays":
                out.print(gson.toJson(GetCollection.nonWorkingDaysBetween(user.getIsAdmin(), start, end)));
            break;
            
            case "workingdays":
                out.print(gson.toJson(GetCollection.workingDaysBetween(user.getIsAdmin(), start, end)));
                break;
            
            case "assignedjoborders":
                try {
                    out.print(gson.toJson(assignedJobOrders(request, start, end)));
                } catch (Exception e) {
                    out.print(e.getMessage());
                    e.printStackTrace();
                }
            break;
            
            case "sampling":
                try {
                    out.print(gson.toJson(sampling(request, start, end)));
                } catch (Exception e) {
                    out.print(e.getMessage());
                    e.printStackTrace();
                }
            break;
            
            case "maintenance":
                try {
                    out.print(gson.toJson(maintenance(request, start, end)));
                } catch (Exception e) {
                    out.print(e.getMessage());
                    e.printStackTrace();
                }
            break;
            
            case "globalevents":
                out.print(gson.toJson(GetCollection.globalEventsBetween(user.getIsAdmin(), start, end)));
                break;
            
            case "program":
                try {
                    c.addAll(getProgram(request,start, end));
                    out.print(gson.toJson(c));
                } catch (Exception e) {
                    out.print(e.getMessage());
                    e.printStackTrace();
                }
            break;
            
            case "xls":
                try {
                    Calendar calStart = Calendar.getInstance(), calEnd = Calendar.getInstance(),
                            calEndOfTheStartMonth = Calendar.getInstance();                   
                    calStart.setTime(start);
                    calStart.set(Calendar.DAY_OF_MONTH, 1);
                    //System.out.println(start + "\n" + end);
                    
                    calEnd.setTime(end);
                    calEnd.set(Calendar.DAY_OF_MONTH, calEnd.getActualMaximum(Calendar.DAY_OF_MONTH));
                    
                    //System.out.println(calStart.getTime() + "\n" + calEnd.getTime());
                    
                    
                    Collection<String> dates = new LinkedList<String>();
                    HashMap<String, Date[]> dateDates = new HashMap<String, Date[]>();
                                        
                    if(calStart.get(Calendar.YEAR) <= calEnd.get(Calendar.YEAR)) {
                        while(calStart.get(Calendar.YEAR) <= calEnd.get(Calendar.YEAR) ||
                                        calStart.get(Calendar.MONTH) <= calEnd.get(Calendar.MONTH)) {
                            //System.out.println(calStart.get(Calendar.YEAR) + " != " + calEnd.get(Calendar.YEAR) + " || " + calStart.get(Calendar.MONTH) + " != " + calEnd.get(Calendar.MONTH));
                            
                            String key = itMonth.get(calStart.get(Calendar.MONTH)) + "-" + calStart.get(Calendar.YEAR) % 100;
                            dates.add(key);
                            Date[] datePair = new Date[2];
                            datePair[0] = new Date(calStart.getTime().getTime());
                            calEndOfTheStartMonth.setTime(calStart.getTime());
                            calEndOfTheStartMonth.set(Calendar.DAY_OF_MONTH, calEndOfTheStartMonth.getActualMaximum(Calendar.DAY_OF_MONTH));
                            datePair[1] = new Date(calEndOfTheStartMonth.getTime().getTime());
                            dateDates.put(key, datePair);
                            
                            if(calStart.get(Calendar.YEAR) == calEnd.get(Calendar.YEAR) &&
                                            calStart.get(Calendar.MONTH) == calEnd.get(Calendar.MONTH)){
                                break;
                            }
                            
                            if(calStart.get(Calendar.MONTH) == Calendar.DECEMBER) {
                                calStart.add(Calendar.YEAR, 1);
                                calStart.set(Calendar.MONTH, Calendar.JANUARY);
                                // copied code
                                key = itMonth.get(calStart.get(Calendar.MONTH)) + "-" + calStart.get(Calendar.YEAR) % 100;
                                dates.add(key);
                                datePair = new Date[2];
                                datePair[0] = new Date(calStart.getTime().getTime());
                                calEndOfTheStartMonth.setTime(calStart.getTime());
                                calEndOfTheStartMonth.set(Calendar.DAY_OF_MONTH, calEndOfTheStartMonth.getActualMaximum(Calendar.DAY_OF_MONTH));
                                datePair[1] = new Date(calEndOfTheStartMonth.getTime().getTime());
                                dateDates.put(key, datePair);
                                // end
                                
                            } else {
                                calStart.add(Calendar.MONTH, 1);
                            }
                        }
                    }
                                        
                    Collection<Machine> machines = GetCollection.machines();
                    Collection<MachineCalendar> calendars = new LinkedList<MachineCalendar>();
                    
                    for(Machine m : machines) {
                        LinkedHashMap<String, ArrayList<ArrayList<Event>>> monthsCalendars = new LinkedHashMap<String, ArrayList<ArrayList<Event>>>();
                        for(String date : dates) {
                            Date[] dd = dateDates.get(date);
                            ArrayList<ArrayList<Event>> events = new ArrayList<ArrayList<Event>>(31);
                            for(int i=0;i<31;i++) {
                                events.add(i, new ArrayList<Event>());
                            }
                            Collection<Event> collectionEvents = getProgram(m, dd[0], dd[1]);
                            for(Event e : collectionEvents) {
                                calStart.setTime(e.getStart());
                                int day = calStart.get(Calendar.DAY_OF_MONTH) - 1;
                                events.get(day).add(e);
                            }
                            monthsCalendars.put(date, events);
                        }
                        MachineCalendar mc = new MachineCalendar();
                        mc.setMachine(m);
                        mc.setCalendar(monthsCalendars);
                        calendars.add(mc);
                    }
                    out.print(gson.toJson(calendars));

                } catch (Exception e) {
                    out.print(e.getMessage());
                    e.printStackTrace();
                }
                break;
            
            default:
                out.print("Error");
            break;
        
        }
    }
}
