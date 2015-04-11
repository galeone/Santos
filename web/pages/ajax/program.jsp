<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page errorPage="../../errors/exception.jsp"%>
<%@ page session="true"%>
<%@ page import="com.viaagnolettisrl.hibernate.*"%>
<%@ page import="com.viaagnolettisrl.*"%>
<%@ page import="com.google.gson.*"%>
<%@ page import="java.util.Collection"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%
	User user = (User) session.getAttribute(LoginServlet.USER);
	if (user == null) {
		response.sendRedirect(request.getContextPath()
				+ LoginServlet.LOGIN_FORM);
	}
	application.setAttribute("todojoborders",GetCollection.notCompletelyAssignedJobOrders(user));
	application.setAttribute("machines", GetCollection.Machines());
%>
<div class="wrap">
	<div class="leftc" style="background: #eee">
		<h4>
			<label for="todoJobOrders">Commesse non assegnate o
				parzialmente assegnate<br />
			</label>
		</h4>
		<c:choose>
			<c:when test="${empty todojoborders}">
				Non esistono commesse non assegnate o parzilamente assegnate 
			</c:when>
			<c:otherwise>
				<select id="todoJobOrders">
					<c:forEach var="entry" items="${todojoborders}" varStatus="loop">
						<option data-arrayindex="${loop.index}" value="${entry.key.id}">${entry.key.id}
							- Tempo mancante: ${entry.value} ore</option>
					</c:forEach>
				</select>
			</c:otherwise>
		</c:choose>

		<div id="jobordersummary"></div>
	</div>
	<div class="rightc">
		<h1>Calendario per macchina</h1>
		<div id="accordion">
			<c:forEach var="machine" items="${machines}">
				<h3 style="color: ${machine.color}">ID: ${machine.id} -
					${machine.name} - ${machine.type} - Finezza: ${machine.nicety}</h3>
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
				"Tempo totale (ore): <div><b>" + window.todojoborders[ui.item.element.data('arrayindex')].key.leadTime + "</b></div><br />" +
				"Tempo rimanente (ore): <div id='remainingTime'><b>" + window.todojoborders[ui.item.element.data('arrayindex')].value + "</div></b><br /><br />" +
				"<b>Blocchetti orari (giornalieri):</b><br /><br />");
		
		var hours = parseInt(window.todojoborders[ui.item.element.data('arrayindex')].value), c = 0;
		
		while(hours > 0) {
			var $block = $(document.createElement("div")),
			last = hours >= 24 ? 24 : hours,
			title = 'Commessa ' + window.todojoborders[ui.item.element.data('arrayindex')].key.id +
					"\n" + last + " ore";
			$block.html(title);
			event = {
				joborder: window.todojoborders[ui.item.element.data('arrayindex')].key.id,
				title: title,
				allDay: last === 24,
				last: last,
				me: $block
			};
			$block.data('event', event);
			
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
		editable: window.user.canAddJobOrder,
		droppable: window.user.canAddJobOrder,
	    eventOverlap: function(stillEvent, movingEvent) {
	        return !stillEvent.allDay;
	    },
		eventReceive: function(event) {
		    var me = $(this);
			if(window.user.canAddJobOrder) {
			    var end = new Date(event._start._d);
			    end.setHours(end.getHours() +  event.last);
			    $.post(
			            "<%=request.getContextPath()%>/add?what=assignedjoborder",
			            {
			            	start: event._start._d.toUTCString(),
			            	end:   end.toUTCString(),
			            	machine: ${machine.id},
			            	joborder: event.joborder
			            }, function(data){
			            	var id = jQuery.parseJSON(data).id;
			            	event.id = id;
			                $("#mcal${machine.id}").fullCalendar('updateEvent',event , false);
			                $("#remainingTime").html(parseInt($("#remainingTime").html()) - event.last);
			                event.me.remove();
			    });
			}
		},
		eventDrop: function(event, delta, revertFunc) {
			if(window.user.canAddJobOrder) {
			    $.post(
			            "<%=request.getContextPath()%>/edit?what=assignedjoborder",
			            {
			            	id: event.id,
			            	start: event._start._d.toUTCString(),
			            	end:   event._end._d.toUTCString(),
			            	machine: event.machine.id,
			            	joborder: event.jobOrder.id
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
		        events: $.merge( $.merge(<%=gson.toJson(GetCollection.setAssignedJobOrderAttr(((Machine)pageContext.findAttribute("machine")).getAssignedJobOrders(),user)) %>,
		        	<%=gson.toJson(GetCollection.NonWorkingDays(false))%>),
			        <%=gson.toJson(GetCollection.SamplingDays(false))%> )
		    }
		],
		eventDragStop: function(event,jsEvent) {
			if(window.user.canAddJobOrder) {
			    var trashEl = $('#programTrashM${machine.id}'), ofs = trashEl.offset(),
			    x1 = ofs.left, x2 = ofs.left + trashEl.outerWidth(true),
			    y1 = ofs.top, y2 = ofs.top + trashEl.outerHeight(true);

			    if (jsEvent.pageX >= x1 && jsEvent.pageX<= x2 &&
			        jsEvent.pageY>= y1 && jsEvent.pageY <= y2) {
		            if (!trashEl.hasClass("to-trash")) {
		        		trashEl.addClass("to-trash");
		            }

				    $.post("<%=request.getContextPath()%>/delete?what=assignedjoborder", { id: event.id }, function(data) {
				    	if(data == 'ok') {
				    		$('#mcal${machine.id}').fullCalendar('removeEvents', event.id);
							trashEl.removeClass("to-trash");
				    	} else {
				    		alert(data);
				    	}
				    });
			    } else if (trashEl.hasClass("to-trash")) {
					trashEl.removeClass("to-trash");
	            }
			}
		}
	}).find(".fc-left").append('<div id="programTrashM${machine.id}" class="calendar-trash">' +
		'<img src="<%=request.getContextPath()%>/styles/fullcalendar/trash.png"></img></div>');
</c:forEach>

$("#accordion").accordion({
    collapsible: true,
    heightStyle: "content"
});

</script>