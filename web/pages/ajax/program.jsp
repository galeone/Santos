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
	<div id="leftc" style="background: #eee">
		<h4>Cestino</h4>
		<div id="programTrash" class="calendar-trash">
			<img
				src="<%=request.getContextPath()%>/styles/fullcalendar/trash.png"></img>
		</div>
		<h4>
			<label for="todoJobOrders">Commesse non assegnate o
				parzialmente assegnate<br />
			</label>
		</h4>
		<select id="todoJobOrders">
			<c:forEach var="entry" items="${todojoborders}" varStatus="loop">
				<option data-arrayindex="${loop.index}" value="${entry.key.id}">${entry.key.id}
					- Tempo mancante: ${entry.value} ore</option>
			</c:forEach>
		</select>
		<div id="jobordersummary"></div>
	</div>
	<div id="rightc">
		<h1>Calendario per macchina</h1>
		<div id="accordion">
			<c:forEach var="machine" items="${machines}">
				<h3 style="color: ${machine.color}">ID: ${machine.id} - ${machine.name} - ${machine.type} - Finezza: ${machine.nicety}</h3>
				<div id="mcal${machine.id}"></div>
			</c:forEach>
		</div>
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
					"\n" + last + " ore";
			$block.html(title);
			event = {
				title: title,
				allDay: last === 24
			};
			$block.data('event', event);
			console.log(event);
			
			$block.draggable({
				zIndex: 999,
				revert: true,
				revertduration: 0
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
		editable: window.user.isAdmin,
		droppable: window.user.isAdmin,
	    eventOverlap: function(stillEvent, movingEvent) {
	        return !stillEvent.allDay;
	    },
		eventReceive: function(event) {
			if(window.user.isAdmin) {
			    $.post(
			            "<%=request.getContextPath()%>/add?what=assignedjoborder",
			            {
			            	date: event._start._d.toUTCString()
			            }, function(data){
			            	var id = jQuery.parseJSON(data).id;
			            	event.id = id;
			                $("#globalCalendar").fullCalendar('updateEvent',event , false);
			    });
			}
		},
		eventDrop: function(event, delta, revertFunc) {
			if(window.user.isAdmin) {
			    $.post(
			            "<%=request.getContextPath()%>/edit?what=assignedjoborder",
			            {
			            	id: event.id,
			            	date: event._start._d.toUTCString()
			            },
						function(data){
			            	if(data != 'ok') { alert(data); revertFunc(); } 
			    });
			}
		},
		header: {
			left: 'prev,next today',
			center: 'title',
			right: 'month,agendaWeek,agendaDay'
		},
		eventSources:[ {
		        events: $.merge(<%=gson.toJson(GetList.AssignedJobOrders((Machine)pageContext.findAttribute("machine")))%>,
		        	<%=gson.toJson(GetList.NonWorkingDays())%>)
		    }
		],
		eventDragStop: function(event,jsEvent) {
			if(window.user.isAdmin) {
			    var trashEl = $('#programTrash'), ofs = trashEl.offset(),
			    x1 = ofs.left, x2 = ofs.left + trashEl.outerWidth(true),
			    y1 = ofs.top, y2 = ofs.top + trashEl.outerHeight(true);

			    if (jsEvent.pageX >= x1 && jsEvent.pageX<= x2 &&
			        jsEvent.pageY>= y1 && jsEvent.pageY <= y2) {
				    $.post("<%=request.getContextPath()%>/delete?what=assignedjoborder", { id: event.id }, function(data) {
				    	if(data == 'ok') {
				    		$('#globalCalendar').fullCalendar('removeEvents', event.id);
				    	} else {
				    		alert(data);
				    	}
				    });
			    }
			}
		}
	});
</c:forEach>

$("#accordion").accordion({
    collapsible: true,
    heightStyle: "content"
});

</script>