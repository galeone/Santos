<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page errorPage="../errors/exception.jsp"%>
<%@ page session="true"%>
<%@ page import="it.galeone_dev.santos.*"%>
<%@ page import="it.galeone_dev.santos.servlet.*"%>
<%@ page import="it.galeone_dev.santos.hibernate.models.*"%>
<%@ page import="it.galeone_dev.santos.hibernate.abstractions.*"%>
<%@ page import="com.google.gson.*"%>
<%@ page import="java.io.*"%>
<%@ page import="java.util.*"%>
<%
	User user = (User)session.getAttribute(LoginServlet.USER);
	if( user == null ) {
		response.sendRedirect(request.getContextPath() + LoginServlet.LOGIN_FORM);
	}
	Gson gson = new Gson();
%>
<jsp:include page="../fragments/header.jsp">
	<jsp:param name="title" value="Calendario generale" />
</jsp:include>
<%
// DO NOT RUN. HERE FOR FUTURE (OR FALLBACK) PURPOSE
Calendar calStart = Calendar.getInstance(), calEnd = Calendar.getInstance(),
	calEndOfTheStartMonth = Calendar.getInstance();

GetServlet getServlet = new GetServlet();

Date start = getServlet.getDate(request, "start");
Date end = getServlet.getDate(request, "end");

calStart.setTime(start);
calEnd.setTime(end);
calStart.set(Calendar.DAY_OF_MONTH, 1);
calEnd.set(Calendar.DAY_OF_MONTH, calEnd.getMaximum(Calendar.DAY_OF_MONTH));

Collection<String> dates = new LinkedList<String>();
HashMap<String, Date[]> dateDates = new HashMap<String, Date[]>();

int startMonth = calStart.get(Calendar.MONTH);
while(calStart.get(Calendar.YEAR) != calEnd.get(Calendar.YEAR) || startMonth != calEnd.get(Calendar.MONTH)) {
	String key = GetServlet.itMonth.get(startMonth) + "-" + calStart.get(Calendar.YEAR) % 100;
	dates.add(key);
	Date[] datePair = new Date[2];
	datePair[0] = new Date(calStart.getTime().getTime());
	calEndOfTheStartMonth.setTime(calStart.getTime());
	calEndOfTheStartMonth.set(Calendar.DAY_OF_MONTH, calEndOfTheStartMonth.getMaximum(Calendar.DAY_OF_MONTH));
	datePair[1] = new Date(calEndOfTheStartMonth.getTime().getTime());
	dateDates.put(key, datePair);
	
	if(startMonth == Calendar.DECEMBER) {
		startMonth = Calendar.JANUARY;
		calStart.set(Calendar.YEAR, calStart.get(Calendar.YEAR) + 1);
		calStart.set(Calendar.MONTH, startMonth);
	} else {
		++startMonth;
		calStart.set(Calendar.MONTH, startMonth);
	}
}

Collection<Machine> machines = GetCollection.machines();
Collection<MachineCalendar> calendars = new LinkedList<MachineCalendar>();

for(Machine m : machines) {
	MachineCalendar mc = new MachineCalendar();
	mc.setMachine(m);
	LinkedHashMap<String, Collection<MachineEvent>> monthsCalendars = new LinkedHashMap<String, Collection<MachineEvent>>();
	for(String date : dates) {
		Date[] dd = dateDates.get(date);
		Collection<MachineEvent> collectionEvents = getServlet.getMachineEvents(m, dd[0], dd[1]);
		monthsCalendars.put(date, collectionEvents);
	}
	mc.setCalendar(monthsCalendars);
	calendars.add(mc);
}
%>
<script>
$(document).ready(function() {
		window.user = <%= gson.toJson(user) %>;

		var todaydate = new Date();
		var curmonth = todaydate.getMonth() + 1; //get current month (1-12)
		var curyear = todaydate.getFullYear(); //get current year
		var selectedmonth = curmonth;
		var selectedyear = curyear;
		var calendar;
		
		<% for(MachineCalendar mc : calendars) { %>
			    calendar = $(document.createElement("div")).attr("id", "m" +<%=mc.getMachine().getId()%>);
			    <% for(String date : mc.getCalendar().keySet()) { %>
			    	calendar.html(calendar.html() + <%=date %>+ "<br />" +
			    		"<div id='calendar<%=mc.getMachine().getId() + date %>"</div>");
			    	$("body").append(calendar);
					document.getElementById('calendar<%=mc.getMachine().getId() + date %>').innerHTML = buildCal(curmonth,
						curyear, "main", "month", "daysofweek", "days", 1,
						<%= gson.toJson(mc.getCalendar().get(date)) %>);
			    <%
			    }
			}
		%>
		
		


		$(".days").each(function() {
		    var ch = $(this).find(".eventBar");
		    if (ch.length > 1) {
			ch.each(function(index) {
			    $(this).css('margin-top', index * 40);
			});
		    }
		});

});
</script>
<%@ include file="../fragments/footer.jsp"%>