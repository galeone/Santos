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
<table id="table" border="1" cellspacing="0" cellpadding="0">
	<tr><td colspan="33">Attendere prego...</td></tr>
</table>
<script>
var t = $("#table");
function content(event) {
	switch(event.type) {
	case "assignedjoborder":
		return event.jobOrder.client.name + "<br>" + event.jobOrder.description;
	case "maintenance":
		return "MANUTENZIONE: " + event.description;
	case "sampling":
		return "CAMPIONAMENTO: " + event.client.name + "<br>" + event.description;
	case "nonworkingday":
	case "workingday":
	    return event.title;
	default:
		return event.description;
	}
};
var start = "<%= request.getParameter("start") %>",
	end   = "<%= request.getParameter("end") %>";
start = start.split("/");
end = end.split("/");
var url = "<%=request.getContextPath()%>/get?what=xls&start=";
	url += start[2] + "-" + start[1] + "-" + start[0] + "&end=";
	url += end[2] + "-" + end[1] + "-" + end[0];
	
$.getJSON(url, function(data) {
	t.find("tr td")[0].innerHTML = "<b>Calendario generale</b>";
	$.each(data, function(i, mc) {
		var row = "<td rowspan='"+(Object.keys(mc.calendar).length + 1)+"'>" + mc.machine.name + "<br>Tipo:" + mc.machine.type + "<br>Finezza: " + mc.machine.nicety + "</td><td></td>";
		for(var i=1;i<=31;i++) {
			row += "<td style='font-weight:bold; text-align:center'>" + i + "</td>";
		}
		t.append("<tr>" + row + "</tr>");
		$.each(mc.calendar, function(date, days) {
			row = "<td>" + date + "</td>";
			for(var i=0;i<31;i++) {
				row += "<td>";
				row += "<table>"
				$.each(days[i], function(i, event) {
				    if(event.id > 0) { /* skip default values (workind day values) */
						row += "<tr><td style='background-color:" + event.color + "'>"+content(event)+"</td></tr>";
				    }
				});
				row += "</table>"
				row += "</td>";
			}
			t.append("<tr>" + row + "</tr>")
		});
	});
});

</script>
<%@ include file="../fragments/footer.jsp"%>