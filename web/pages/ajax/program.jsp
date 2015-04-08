<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page errorPage="../../errors/exception.jsp"%>
<%@ page session="true"%>
<%@ page import="com.viaagnolettisrl.hibernate.*"%>
<%@ page import="com.viaagnolettisrl.*"%>
<%@ page import="com.google.gson.*"%>
<%@ page import="java.util.List"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%
	User user = (User) session.getAttribute(LoginServlet.USER);
	if (user == null) {
		response.sendRedirect(request.getContextPath()
				+ LoginServlet.LOGIN_FORM);
	}
	application.setAttribute("todojoborders",GetList.notCompletelyAssignedJobOrders());
	application.setAttribute("machines", GetList.Machines());
%>
<div id="wrap">
	<div id="leftc">
		<h1>
		<label for="todoJobOrders">Commesse non assegnate o parzialmente assegnate<br /></label>
		</h1>
		<select id="todoJobOrders">
			<c:forEach var="entry" items="${todojoborders}" varStatus="loop">
				<option data-arrayindex="${loop.index}" value="${entry.key.id}">${entry.key.id} - Tempo mancante: ${entry.value} ore</option>
			</c:forEach>
		</select>
		<div id="jobordersummary"></div>
	</div>
	<div id="rightc">
	<h1>Calendario per macchina</h1>
		<c:forEach var="machine" items="${machines}">
			<h1 style="color: ${machine.color}">ID: ${machine.id} - ${machine.name}</h1>
			<div id="mcal${machine.id}"></div>
		</c:forEach>
	</div>
</div>
<% Gson gson = new Gson(); %>
<script>
window.todojoborders = <%= gson.toJson(application.getAttribute("todojoborders")) %>;
$("#todoJobOrders").selectmenu({
	select: function(event, ui) {
		$("#jobordersummary").html(
				"Nome cliente: " + window.todojoborders[ui.item.element.data('arrayindex')].key.client.name + "<br />" +
				"Codice cliente: " + window.todojoborders[ui.item.element.data('arrayindex')].key.client.code + "<br /><br />" +
				"Tempo totale (ore): " + window.todojoborders[ui.item.element.data('arrayindex')].key.leadTime + "<br />" +
				"Tempo rimanente (ore): " + window.todojoborders[ui.item.element.data('arrayindex')].value + "<br /><br />" +
				"<b>Blocchetti orari (giornalieri):</b><br /><br />");
		
		var hours = parseInt(window.todojoborders[ui.item.element.data('arrayindex')].value), c = 0;
		
		while(hours > 0) {
			var $block = $(document.createElement("div")),
			last = hours >= 24 ? 24 : hours,
			title = 'Commessa ' + window.todojoborders[ui.item.element.data('arrayindex')].key.id +
			"<br />" +  last + " ore";
			$block.html(title)
			$block.data('event', {
				title: title,
				stick: true,
				last: last
			});
			$block.draggable({
				zIndex: 999
			});
			$block.addClass("fc-draggable-event");
			if(c == 3) {
				c = 0;
				$("#jobordersummary").append("<br />");
			}
			$("#jobordersummary").append($block);
			c++;
			hours -= 24;
		}
	}
});

<c:forEach var="machine" items="${machines}">
	$("#mcal${machine.id}").fullCalendar({
		lang: 'it',
		header: {
			left: 'prev,next today',
			center: 'title',
			right: 'month,agendaWeek,agendaDay'
		}
	});
</c:forEach>

</script>