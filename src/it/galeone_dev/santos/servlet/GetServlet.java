package it.galeone_dev.santos.servlet;

import it.galeone_dev.santos.GetCollection;
import it.galeone_dev.santos.hibernate.HibernateUtils;
import it.galeone_dev.santos.hibernate.models.AssignedJobOrder;
import it.galeone_dev.santos.hibernate.models.Machine;
import it.galeone_dev.santos.hibernate.models.Maintenance;
import it.galeone_dev.santos.hibernate.models.Sampling;
import it.galeone_dev.santos.hibernate.models.User;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
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
    
    @Override
    public void init() throws ServletException {
    }
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
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
            return GetCollection.setAssignedJobOrderAttr(
                    GetCollection.assignedJobOrdersBetween(m, start, end),
                    user.getCanAssignJobOrder());
            
        }catch(NumberFormatException e) {
            throw new Exception("Id macchina non valido");
        }        
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
        return GetCollection.setSamplingAttr(
                GetCollection.samplingBetween(m, start, end),
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
        return GetCollection.setMaintenanceAttr(
                GetCollection.maintenanceBetween(m, start, end),
                user.getCanAssignJobOrder());
    }
    
    private Date getDate(HttpServletRequest request, String name) {
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
                    c.addAll(GetCollection.globalEventsBetween(user.getIsAdmin(), start, end));
                    c.addAll(assignedJobOrders(request, start, end));
                    c.addAll(sampling(request, start, end));
                    c.addAll(maintenance(request, start, end));
                    out.print(gson.toJson(c));
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
