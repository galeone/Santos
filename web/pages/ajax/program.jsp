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
	application.setAttribute("todojoborders",GetCollection.todoJobOrders(user.getCanAddJobOrder()));
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
						<option value="${loop.index}">
						[${entry.id}] Tempo mancante:
							<fmt:formatNumber value="${entry.missingTime / 60 -0.5}" maxFractionDigits="0"/> ore e
							${entry.missingTime % 60} minuti</option>
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
window.machines = <%= gson.toJson(application.getAttribute("machines")) %>;

var machineSelect = '<select name="machine">';
window.machines.forEach(function(machine, index, array) {
    machineSelect += '<option value="' + machine.id + '">'+machine.id+' - ' +machine.name +'</option>';
});
machineSelect += "</select>";

$("#jobordersummary").on('submit', '#autoassign', function(e) {
   e.preventDefault();
	if(window.user.canAddJobOrder) {
	    var start = $("#autostart").datepicker({altFormat: "dd/mm/yy"}).datepicker("getDate"),
	        end = $("#autoend").datepicker({altFormat: "dd/mm/yy"}).datepicker("getDate");
	    var machine =  $(this).find('select[name="machine"]').val(),
	    	jo = $(this).find('input[name="joborder"]').val();
	    if(!end) { end = new Date(2000000000); }
	    $.post("<%=request.getContextPath()%>/add?what=autoassignedjoborder",
	            {
	            	start: start.toUTCString(),
	            	end:   end.toUTCString(),
	            	machine: machine,
	            	joborder: jo
	            }, function(data){
	        	     var removedTime = parseInt(data);
	                if(event.type == "assignedjoborder") {
	                	var remain = $("#remainingTime"), minutes = parseInt(remain.data("minutes"));
	                	minutes -= removedTime;
	                	remain.data("minutes", minutes);
	                	// update event globalobject (avoid recration of assigned blocks on select reselection)
	                	window.todojoborders[remain.data("arrayindex")].missingTime = minutes;
						var lastHours = Math.floor(minutes / 60), lastMinutes = minutes % 60;
						remain.html("<b>" + lastHours + " ore e " + lastMinutes + " minuti</b>");
	                }

	                $("#m" + machine + "Calendar").fullCalendar( 'refetchEvents' );
					$("#m" + machine + "Calendar").fullCalendar( 'rerenderEvents' );
	    });
	}
});

$("#todoJobOrders").selectmenu({
	select: function(event, ui) {
	    var leadMinutes = parseInt(window.todojoborders[ui.item.element.val()].leadTime),
	    	leadHours = Math.floor(leadMinutes / 60),
	    	leadMinutes = leadMinutes % 60,
	    	dataRemainMinutes = parseInt(window.todojoborders[ui.item.element.val()].missingTime),
	    	remainMinutes = dataRemainMinutes,
	    	remainHours = Math.floor(remainMinutes / 60),
	    	remainMinutes = remainMinutes  % 60;
	    
		$("#jobordersummary").html(
				"Nome cliente: " + window.todojoborders[ui.item.element.val()].client.name + "<br />" +
				"Codice cliente: " + window.todojoborders[ui.item.element.val()].client.code + "<br /><br />" +
				"Tempo totale: <div><b>" + leadHours + " ore e " + leadMinutes + " minuti</b></div><br />" +
				"Tempo rimanente : <div id='remainingTime' data-minutes="+dataRemainMinutes+" data-arrayindex="+ui.item.element.val()+"><b>" + remainHours + " ore e " + remainMinutes + " minuti</div></b><br /><br />" +
				"<i>Puoi inserire in un unico colpo quante ore di produzione desideri scegliendo la data di inizio e fine e la macchina a cui assegnale<br />" +
				"Oppure fare drag and drop delle ore sul calendario della macchina alla quale si desidere assegnare il lavoro</i><br /><br />" +
				"<b>Inserimento automatico</b>" +
				'<form id="autoassign">' + '<input type="hidden" name="joborder" value="' + window.todojoborders[ui.item.element.val()].id + '" />' +
				'A partire da <sup>*</sup><input type="text" id="autostart" required /> <br />' +
				'Fino a <input type="text" id="autoend" /> <br />' +
				machineSelect + '<br /><input type="submit" value="Auto assegna" />' +
				'</form>' +
				"<br /><br /><b>Inserimento manuale (drag and drop)</b><br /><br />");
		
		$("#autostart").datepicker();
		$("#autoend").datepicker();
		
		var c = 0, aDay = 24*60;
		
		while(dataRemainMinutes > 0) {
			var $block = $(document.createElement("div")),
			last =  dataRemainMinutes >= aDay ? aDay : dataRemainMinutes,
			lastHours = Math.floor(last / 60), lastMinutes = last % 60,
			title = '[' + window.todojoborders[ui.item.element.val()].id + "] " +
					window.todojoborders[ui.item.element.val()].client.code +  " - " +
					window.todojoborders[ui.item.element.val()].client.name + 
					"<br>" + lastHours + " ore" + (
						lastMinutes > 0 ? " e " + lastMinutes + " minuti" : ""
							),
			color = window.todojoborders[ui.item.element.val()].color;
			$block.html(title);
			event = {
				joborder: window.todojoborders[ui.item.element.val()].id,
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
			                if(event.type == "assignedjoborder") {
			                	var remain = $("#remainingTime"), minutes = parseInt(remain.data("minutes"));
			                	minutes -= event.last;
			                	remain.data("minutes", minutes);
			                	// update event globalobject (avoid recration of assigned blocks on select reselection)
			                	window.todojoborders[remain.data("arrayindex")].missingTime = minutes;
								var lastHours = Math.floor(minutes / 60), lastMinutes = minutes % 60;
								remain.html("<b>" + lastHours + " ore e " + lastMinutes + " minuti</b>");
								event.me.remove();
			                }

			                $("#m${machine.id}Calendar").fullCalendar( 'refetchEvents' );
							$("#m${machine.id}Calendar").fullCalendar( 'rerenderEvents' );
			    });
			}
		},
		eventDrop: function(event, delta, revertFunc) {
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
			            	machine:  typeof(event.machine)  == 'undefined' ? '' : event.machine.id,
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