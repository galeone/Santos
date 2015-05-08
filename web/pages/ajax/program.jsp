<%@page import="javax.swing.text.AbstractDocument.Content"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page errorPage="../../errors/exception.jsp"%>
<%@ page session="true"%>
<%@ page import="com.viaagnolettisrl.hibernate.*"%>
<%@ page import="com.viaagnolettisrl.*"%>
<%@ page import="com.google.gson.*"%>
<%@ page import="java.util.Collection"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%
	User user = (User) session.getAttribute(LoginServlet.USER);
	if (user == null) {
		response.sendRedirect(request.getContextPath()
				+ LoginServlet.LOGIN_FORM);
	}
	application.setAttribute("todojoborders",GetCollection.notCompletelyAssignedJobOrders(user.getCanAddJobOrder()));
	application.setAttribute("machines", GetCollection.machines());
	application.setAttribute("joborders", GetCollection.jobOrders());
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
				Non esistono commesse non assegnate o parzilamente assegnate<br /> 
			</c:when>
			<c:otherwise>
				<select id="todoJobOrders">
					<c:forEach var="entry" items="${todojoborders}" varStatus="loop">
						<option data-arrayindex="${loop.index}" value="${entry.key.id}">
						[${entry.key.id}] Tempo mancante:
							<fmt:formatNumber value="${entry.value / 60}" maxFractionDigits="0"/> ore e
							${entry.value % 60} minuti</option>
					</c:forEach>
				</select>
			</c:otherwise>
		</c:choose>
		<c:choose>
			<c:when test="${!empty todojoborders}">
				<div id="jobordersummary"></div>
				<h4>Campionamento</h4>
				<p>
					Seleziona la commessa ed imposta la durata. <br /> Dopo trascina il
					blocchetto sul calendario.
				</p>
				<div id="sampling">
					<div id="sampling-event"></div>
					<select id="joborder">
						<c:forEach var="jo" items="${joborders}">
							<option value="${jo.id}">Commessa: ${jo.id} - Cliente: ${jo.client.name}</option>
						</c:forEach>
					</select>
				</div>
			</c:when>
		</c:choose>
	</div>
	<div class="rightc">
		<h1>Calendario per macchina</h1>
		<div id="accordion">
			<c:forEach var="machine" items="${machines}">
				<h3>ID: ${machine.id} - ${machine.name} - ${machine.type} -
					Finezza: ${machine.nicety}</h3>
				<div id="m${machine.id}Calendar" data-calendarid="m${machine.id}Calendar"></div>
			</c:forEach>
		</div>
	</div>
</div>
<% Gson gson = new Gson(); %>
<script>
var $block = $("#sampling-event"), title = 'Campionamento',
event = {
		title: title,
		allDay: true,
		color: '#00E',
		type: 'sampling',
		last: 24
		// dinamically add machine & joborder reference
};
$block.html(title);
$block.data('event', event);

$block.draggable({
	zIndex: 999,
	revert: true,
	revertduration: 0
});
$block.addClass("fc-draggable-event sampling");

window.todojoborders = <%= gson.toJson(application.getAttribute("todojoborders")) %>;
$("#todoJobOrders").selectmenu({
	select: function(event, ui) {
	    var leadMinutes = parseInt(window.todojoborders[ui.item.element.data('arrayindex')].key.leadTime),
	    	leadHours = Math.floor(leadMinutes / 60),
	    	leadMinutes = leadMinutes % 60,
	    	dataRemainMinutes = parseInt(window.todojoborders[ui.item.element.data('arrayindex')].value),
	    	remainMinutes = dataRemainMinutes,
	    	remainHours = Math.floor(remainMinutes / 60),
	    	remainMinutes = remainMinutes  % 60;
	    
		$("#jobordersummary").html(
				"Nome cliente: " + window.todojoborders[ui.item.element.data('arrayindex')].key.client.name + "<br />" +
				"Codice cliente: " + window.todojoborders[ui.item.element.data('arrayindex')].key.client.code + "<br /><br />" +
				"Tempo totale: <div><b>" + leadHours + " ore e " + leadMinutes + " minuti</b></div><br />" +
				"Tempo rimanente : <div id='remainingTime' data-minutes="+dataRemainMinutes+"><b>" + remainHours + " ore e " + remainMinutes + " minuti</div></b><br /><br />" +
				"<b>Blocchetti orari (giornalieri):</b><br /><br />");
		
		var c = 0, aDay = 24*60;
		
		while(dataRemainMinutes > 0) {
			var $block = $(document.createElement("div")),
			last =  dataRemainMinutes >= aDay ? aDay : dataRemainMinutes,
			lastHours = Math.floor(last / 60), lastMinutes = last % 60,
			title = '[' + window.todojoborders[ui.item.element.data('arrayindex')].key.id + "] " +
					window.todojoborders[ui.item.element.data('arrayindex')].key.client.code +  " - " +
					window.todojoborders[ui.item.element.data('arrayindex')].key.client.name + 
					"<br>" + lastHours + " ore" + (
						lastMinutes > 0 ? " e " + lastMinutes + " minuti" : ""
							),
			color = window.todojoborders[ui.item.element.data('arrayindex')].key.color;
			$block.html(title);
			event = {
				joborder: window.todojoborders[ui.item.element.data('arrayindex')].key.id,
				title: title,
				allDay: last === aDay,
				last: last,
				me: $block,
				color: color,
				type: "assignedjoborder"
			};
			$block.data('event', event);
			
			$block.draggable({
				zIndex: 999
			});
			$block.addClass("fc-draggable-event");
			$block.css('background-color', color);
			
			if(c == 3) {
				c = 0;
				$("#jobordersummary").append("<br />");
			}
			$("#jobordersummary").append($block);
			c++;
			dataRemainMinutes -= aDay;
		}
	}
});

<c:forEach var="machine" items="${machines}">
	$("#m${machine.id}Calendar").fullCalendar({
		lang: 'it',
		editable: window.user.canAddJobOrder,
		droppable: window.user.canAddJobOrder,
	    eventOverlap: window.user.canAddJobOrder,
	    disableResizing: true,
		eventReceive: function(event) {
		    console.log(event);
			if(window.user.canAddJobOrder) {
			    var end = new Date(event._start._d.getTime() + event.last * 60000);
			    $.post("<%=request.getContextPath()%>/add?what=" + event.type,
			            {
			            	start: event._start._d.toUTCString(),
			            	end:   end.toUTCString(),
			            	machine: ${machine.id},
			            	joborder: event.type == 'sampling' ? $("#joborder").val() : event.joborder
			            }, function(data){
			        		//handle only machine events, will never add global event from here
			        		var ret = jQuery.parseJSON(data);
			            	event.id = ret.id;
			            	event.machine = ret.machine;
			            	event.jobOrder = ret.jobOrder;
			                $("#m${machine.id}Calendar").fullCalendar('updateEvent',event , false);
			                var remain = $("#remainingTime");
			                remain.data(parseInt(remain.data("minutes")) - event.last);
			                var minutes= parseInt(remain.data("minutes"));
							var lastHours = Math.floor(minutes / 60), lastMinutes = minutes % 60;
							remain.html(lastHours + " ore e " + lastMinutes + " minuti");

			                if(typeof(event.me) !== 'undefined') {event.me.remove();}
			                $("#m${machine.id}Calendar").fullCalendar( 'refetchEvents' );
							$("#m${machine.id}Calendar").fullCalendar( 'rerenderEvents' );
			    });
			}
		},
		eventDrop: function(event, delta, revertFunc) {
		    console.log(event);
			if(window.user.canAddJobOrder) {
			    var end = null;
			    if(!event._end) {
					end =  new Date(event._start._d.getTime() + event.last * 60000);
					event._end = moment(end);
			    }
			    $.post("<%=request.getContextPath()%>/edit?what=" + event.type,
			            {
			            	id: event.id,
			            	start: event._start._d.toUTCString(),
			            	end:   event._end._d.toUTCString(),
			            	// global events (does not have machine/joborder)
			            	machine: typeof(event.machine)  == 'undefined' ? '' : event.machine.id,
				            joborder: typeof(event.jobOrder) == 'undefined' ? '' : event.jobOrder.id
			            },
						function(data){
			            	if(data != 'ok') { alert(data); revertFunc(); }
			            	else {
			            	    // handle moving of events server side
			            		$("#m${machine.id}Calendar").fullCalendar( 'refetchEvents' );
								$("#m${machine.id}Calendar").fullCalendar( 'rerenderEvents' );
			            	}
			    });
			}
		},
		header: {
			left: '',
			center: 'title'
		},
		eventSources:[
		        "<%=request.getContextPath()%>/get?what=program&machine=${machine.id}"
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

				    $.post("<%=request.getContextPath()%>/delete?what=" + event.type, { id: event.id }, function(data) {
				    	if(data == 'ok') {
				    		$("#m${machine.id}Calendar").fullCalendar( 'refetchEvents' );
							$("#m${machine.id}Calendar").fullCalendar( 'rerenderEvents' );
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
    heightStyle: "content",
    activate: function( event, ui ) {
		var visibleCalendar = ui.newPanel.data('calendarid');
		if(visibleCalendar !== null) {
		    visibleCalendar = $("#"+visibleCalendar);
		    visibleCalendar.fullCalendar( 'refetchEvents' );
		    visibleCalendar.fullCalendar( 'rerenderEvents' );
		}		
    }
});

</script>