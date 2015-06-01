<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page errorPage="../../errors/exception.jsp"%>
<%@ page session="true"%>
<%@ page import="it.galeone_dev.santos.*"%>
<%@ page import="it.galeone_dev.santos.servlet.*"%>
<%@ page import="it.galeone_dev.santos.hibernate.models.*"%>
<%@ page import="it.galeone_dev.santos.hibernate.abstractions.EventUtils"%>
<%@ page import="com.google.gson.*"%>
<%@ page import="java.util.Collection"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%
	User user = (User) session.getAttribute(LoginServlet.USER);
	if (user == null) {
		response.sendRedirect(request.getContextPath()
				+ LoginServlet.LOGIN_FORM);
	}
	application.setAttribute("todojoborders",GetCollection.todoJobOrders(user.getCanAssignJobOrder()));
	application.setAttribute("machines", GetCollection.machines());
	application.setAttribute("joborders", GetCollection.jobOrders());
	application.setAttribute("clients", GetCollection.clients());
%>
<div class="wrap">
	<div class="leftc">
		<h1>Gestione commesse</h1>
		<div id="accordionActions">
			<h3>Assegnamento</h3>
			<div>
				<c:choose>
					<c:when test="${empty todojoborders}">
						Non esistono commesse non assegnate o parzilamente assegnate<br />
					</c:when>
					<c:otherwise>
						<select id="todoJobOrders">
							<option selected disabled>Scegli una commessa</option>
							<c:forEach var="entry" items="${todojoborders}" varStatus="loop">
								<option value="${loop.index}">[${entry.id}] Tempo
									rimanente:
									<fmt:formatNumber value="${entry.missingTime / 60 -0.5}"
										maxFractionDigits="0" /> ore e ${entry.missingTime % 60}
									minuti
								</option>
							</c:forEach>
						</select>
					</c:otherwise>
				</c:choose>
				<div id="jobordersummary"></div>
			</div>
			<h3>Cancellazione</h3>
			<div>
				<c:choose>
					<c:when test="${empty joborders}">
						Non esistono commesse<br />
					</c:when>
					<c:otherwise>
					<p>Puoi cancellare delle ore lavorative assegnate ad una macchina e ad una commessa</p>
					<p>La cancellazione manuale è fatta tramite trascinamento delle ore assegnate sul cestino associato
					al calendario della macchina</p>
					<p>La cancellazione automatica può essere fatta completando il form sottostante</p><br />
					<form id="deleteJobOrder">
						Commessa<br />
						<select name="joborder">
							<option selected disabled>Scegli una commessa</option>
							<c:forEach var="entry" items="${joborders}" varStatus="loop">
								<option value="${entry.id}">
									[${entry.id}] ${entry.description}
								</option>
							</c:forEach>
						</select>
						<br />
						Macchina<br />
						<select name="machine">
							<option selected disabled>Scegli una macchina</option>
							<c:forEach var="machine" items="${machines}">
								<option value="${machine.id}">${machine.id}-
									<c:out value="${machine.name}" /></option>
							</c:forEach>
						</select><br />
						A partire da <sup>*</sup><input type="text" class="autostart"required /><br />
						Fino a <sup>*</sup> <input type="text" class="autoend" required /> <br />
						<input type="submit" value="Auto cancella" /><br />
					</form>
					</c:otherwise>
				</c:choose>
				<div id="jobordersummary"></div>
			</div>
			<!-- accordion -->
			<h3>Campionamento</h3>
			<div>
				<c:choose>
					<c:when test="${empty clients}">
						Non esistono clienti
					</c:when>
					<c:otherwise>
						<div id="samplingsummary">
							<b>Inserimento automatico</b>
							<form id="autosampling">
								Cliente<br /> <select name="client">
									<option selected disabled>Scegli il cliente</option>
									<c:forEach var="c" items="${clients}">
										<option value="${c.id}" title="<c:out value="${c.name}" />"><c:out
												value="${c.name} [${c.code}]" /></option>
									</c:forEach>
								</select><br /> Macchina<br /> <select name="machine">
									<option selected disabled>Scegli una macchina</option>
									<c:forEach var="machine" items="${machines}">
										<option value="${machine.id}">${machine.id}-
											<c:out value="${machine.name}" /></option>
									</c:forEach>
								</select><br /> Descrizione<br /> <input type="text" name="description" required /><br />
								A partire da <sup>*</sup><input type="text" class="autostart"
									required /><br /> Fino a <sup>*</sup> <input type="text"
									class="autoend" required /> <br />
								<input type="submit" value="Auto assegna" /><br />
							</form>
							<b>Inserimento manuale (drag-and-drop)</b><br /> <i>Puoi
								inserire campionamenti manualmente per la durata massima di 24
								ore.</i> <br />
							<i>Per inserire più giorni di campionamento, usa
								l'inserimento automatico</i>
							<p>
								Seleziona il cliente ed imposta la durata. <br />Dopo trascina
								il blocchetto sul calendario.
							</p>
							Cliente<br />
							<form id="sform">
								<select name="client" required>
									<option selected disabled>Scegli il cliente</option>
									<c:forEach var="c" items="${clients}">
										<option value="${c.id}" title="<c:out value="${c.name}" />"><c:out
												value="${c.name} [${c.code}]" /></option>
									</c:forEach>
								</select><br /> Descrizione<br /> <input type="text" name="description" required /><br />
								Ore: <input style="display: inline" type="number" min="1" max="24" value="0" name="hours" required/><br />
							</form>
							<br />
							<div id="sampling-event"></div>
							<br />
							<br />
						</div>
					</c:otherwise>
				</c:choose>
			</div>
			<!-- accordion div -->
			<h3>Manutenzione</h3>
			<div>
				<c:choose>
					<c:when test="${empty machines}">
						Non esistono macchine
					</c:when>
					<c:otherwise>
						<div id="maintenancesummary">
							<b>Inserimento automatico</b>
							<form id="automaintenance">
								Macchina<br /> <select name="machine">
									<option selected disabled>Scegli una macchina</option>
									<c:forEach var="machine" items="${machines}">
										<option value="${machine.id}">${machine.id}-
											<c:out value="${machine.name}" /></option>
									</c:forEach>
								</select><br /> Descrizione<br /> <input type="text" name="description" /><br />
								A partire da <sup>*</sup><input type="text" class="autostart"
									required /><br /> Fino a <sup>*</sup> <input type="text"
									class="autoend" required /> <br />
								<input type="submit" value="Auto assegna" /><br />
							</form>
							<b>Inserimento manuale (drag-and-drop)</b><br /> <i>Puoi
								inserire manutenzione manualmente per la durata massima di 24
								ore.</i> <br />
							<i>Per inserire più giorni di manutenzione, usa l'inserimento
								automatico</i>
							<p>
								Inserisci la descrizione, <br />dopo trascina il blocchetto sul
								calendario delle macchina
							</p>
							<form id="mform">
								Descrizione<br /> <input type="text" name="description" required /><br />
								Ore: <input style="display: inline" type="number" min="1" max="24" value="0" name="hours" required/><br />
							</form>
							<br />
							<div id="maintenance-event"></div>
							<br />
							<br />
						</div>
					</c:otherwise>
				</c:choose>
			</div>
			<!-- accordion div -->
		</div>
		<!-- accorion -->
	</div>
	<!-- leftc -->
	<div class="rightc">
		<h1>Calendario per macchina</h1>
		<div id="accordionCalendars">
			<c:forEach var="machine" items="${machines}">
				<h3>
					ID: ${machine.id} -
					<c:out value="${machine.name}" />
					-
					<c:out value="${machine.type}" />
					- Finezza: ${machine.nicety}
				</h3>
				<div id="m${machine.id}Calendar"
					data-calendarid="m${machine.id}Calendar"></div>
			</c:forEach>
		</div>
	</div>
</div>
<% Gson gson = new Gson(); %>
<script>
var $sBlock = $("#sampling-event"), title = 'CAMPIONAMENTO',
sevent = {
		title: title,
		allDay: true,
		type: 'sampling'
};
$sBlock.html(title);
$sBlock.data('event', sevent);

$sBlock.draggable({
	zIndex: 999,
	revert: true,
	revertduration: 0,
	appendTo: "body",
	helper: function() {
	    var whform = $("#sform");
	    if(!whform[0].checkValidity()) {
			alert("completa tutti i campi");
	    } else {
			var event = $sBlock.data('event');
			event.description = whform.find('input[name="description"]').val();
			event.client = whform.find('select[name="client"]').val();
			event.last   = whform.find('input[name="hours"]').val();
			$sBlock.data('event', event);
	    }
	    return $sBlock.clone(true);
	}
});
$sBlock.addClass("fc-draggable-event sampling");

var $mBlock = $("#maintenance-event");
title = 'MANUTENZIONE';
var mevent = {
		title: title,
		allDay: true,
		type: 'maintenance'
};
$mBlock.html(title);
$mBlock.data('event', mevent);

$mBlock.draggable({
	zIndex: 999,
	revert: true,
	revertduration: 0,
	appendTo: "body",
	helper: function() {
	    var whform = $("#mform");
	    if(!whform[0].checkValidity()) {
			alert("completa tutti i campi");
	    } else {
			var event = $mBlock.data('event');
			event.description = whform.find('input[name="description"]').val();
			$mBlock.data('event', event);
	    }
		return $mBlock.clone(true);
	}
});
$mBlock.addClass("fc-draggable-event maintenance");

window.todojoborders = <%= gson.toJson(application.getAttribute("todojoborders")) %>;
window.machines = <%= gson.toJson(application.getAttribute("machines")) %>;

var machineSelect = '<select name="machine"><option selected disabled>Scegli una macchina</option>';
window.machines.forEach(function(machine, index, array) {
    machineSelect += '<option value="' + machine.id + '">'+machine.id+' - ' +machine.name +'</option>';
});
machineSelect += "</select>";

var aDay = <%=EventUtils. WEEK_WORKING_HOURS_IN_MINUTES %>;

$("#samplingsummary").on('submit', 'form', function(e) {
    e.preventDefault();
 	if(window.user.canAddJobOrder) {
 	    var start = $(this).find(".autostart").datepicker("getDate"),
 	        end = $(this).find(".autoend").datepicker("getDate");
 	    // datepicker start from the day before the selection (wtf)
 	    start.setDate(start.getDate() + 1); //always defined (required field)
 	    end.setDate(end.getDate() + 1); // idem for sampling ^
 	    
 	    var client = $(this).find('select[name="client"]').val(),
 	    	description = $(this).find('input[name="description"]').val(),
 	    	machine =  $(this).find('select[name="machine"]').val();
 	    
 	    $.post("<%=request.getContextPath()%>/add?what=sampling",
 	            {
 	            	start: start.toUTCString(),
 	            	end:   end.toUTCString(),
 	            	client: client,
 	            	machine: machine,
 	            	description: description
 	            }, function(data){
 	        		if(isNaN(parseInt(data))) { alert(data); }
 	        		$("#m" + machine + "Calendar").fullCalendar( 'refetchEvents' );
					$("#m" + machine + "Calendar").fullCalendar( 'rerenderEvents' );
 	            });
 	}
});

$("#maintenancesummary").on('submit', 'form', function(e) {
    e.preventDefault();
 	if(window.user.canAddJobOrder) {
 	    var start = $(this).find(".autostart").datepicker("getDate"),
 	        end = $(this).find(".autoend").datepicker("getDate");
 	    // datepicker start from the day before the selection (wtf)
 	    start.setDate(start.getDate() + 1); //always defined (required field)
 	    end.setDate(end.getDate() + 1); // ^
 	    
 	    var machine =  $(this).find('select[name="machine"]').val(),
 	    	description = $(this).find('input[name="description"]').val(),
 	    	machine =  $(this).find('select[name="machine"]').val();
 	    
 	    $.post("<%=request.getContextPath()%>/add?what=maintenance",
 	            {
 	            	start: start.toUTCString(),
 	            	end:   end.toUTCString(),
 	            	machine: machine,
 	            	description: description
 	            }, function(data){
 	        		if(isNaN(parseInt(data))) { alert(data); }
 	        		$("#m" + machine + "Calendar").fullCalendar( 'refetchEvents' );
					$("#m" + machine + "Calendar").fullCalendar( 'rerenderEvents' );
 	            });
 	}
});

function newAssignedJobOrder(data, machine) {
    var removedTime = parseInt(data);
    if(isNaN(removedTime)) {
	 	alert(data);
    } else {
		var remain = $("#remainingTime"),  minutes = parseInt(remain.data("minutes"));
		minutes -= removedTime;
		remain.data("minutes", minutes);
		// update event globalobject (avoid recration of assigned blocks on select reselection)
		window.todojoborders[remain.data("arrayindex")].missingTime = minutes;
		var lastHours = Math.floor(minutes / 60), lastMinutes = minutes % 60;
		out = lastHours + " ore e " + lastMinutes + " minuti";
		remain.html("<b>" + out +"</b>");
		var selectVal = $("#selectvalue");
		$('#todoJobOrders option[value="'+selectVal.data('value')+'"]').html("[" + selectVal.data('joborderid') + "] Tempo rimanente:" + out);
		$("#todoJobOrders").selectmenu("refresh");
		if(minutes <= 0) {
		    $("#jobordersforms").remove();
		}
    }
}

function toSend(event, machine) {
    console.log("ToSend: ", event);
    if(event.type == 'assignedjoborder') return {
			start: event._start._d.toUTCString(),
			machine: machine,
			joborder: event.joborder };
			
	if(event.type == 'sampling') return {
			start: event._start._d.toUTCString(),
			machine: machine,
			client: event.client,
			description: event.description };
			
	if(event.type == 'maintenance') return {
            	start: event._start._d.toUTCString(),
            	machine: machine,
            	description: event.description };
    return {};
};


$("#jobordersummary").on('submit', '#autoassignjoborders', function(e) {
   e.preventDefault();
	if(window.user.canAssignJobOrder) {
	    var start = $(this).find(".autostart").datepicker("getDate"),
	        end = $(this).find(".autoend").datepicker("getDate");
	    // datepicker start from the day before the selection (wtf)
	    start.setDate(start.getDate() + 1); //always defined (required field)
	    if(!end) { end = new Date(null); } else { end.setDate(end.getDate() + 1); }
	    
	    var machine =  $(this).find('select[name="machine"]').val(),
	    	jo = $(this).find('input[name="joborder"]').val();
	    $.post("<%=request.getContextPath()%>/add?what=assignedjoborder",
	            {
	            	start: start.toUTCString(),
	            	end:   end.toUTCString(),
	            	machine: machine,
	            	joborder: jo
	            }, function(data) {
	        		newAssignedJobOrder(data, machine);
	        		$("#m" + machine + "Calendar").fullCalendar( 'refetchEvents' );
					$("#m" + machine + "Calendar").fullCalendar( 'rerenderEvents' );
	   });
	}
});

$("#todoJobOrders").selectmenu({
	select: function(event, ui) {
	    var index = parseInt(ui.item.element.val());
	    if(!isNaN(index)) {
		    var leadMinutes = parseInt(window.todojoborders[index].leadTime),
		    	leadHours = Math.floor(leadMinutes / 60),
		    	leadMinutes = leadMinutes % 60,
		    	dataRemainMinutes = parseInt(window.todojoborders[index].missingTime),
		    	remainMinutes = dataRemainMinutes,
		    	remainHours = Math.floor(remainMinutes / 60),
		    	remainMinutes = remainMinutes  % 60;
		    
			$("#jobordersummary").html( '<div id="selectvalue" data-value="'+index+'" data-joborderid="'+window.todojoborders[index].id+'"></div>' +
					"Nome cliente: " + window.todojoborders[index].client.name + "<br />" +
					"Codice cliente: " + window.todojoborders[index].client.code + "<br /><br />" +
					"Tempo totale: <div><b>" + leadHours + " ore e " + leadMinutes + " minuti</b></div><br />" +
					"Tempo rimanente : <div id='remainingTime' data-minutes="+dataRemainMinutes+" data-arrayindex="+index+"><b>" + remainHours + " ore e " + remainMinutes + " minuti</div></b><br /><br />" +
					"<div id=\"jobordersforms\"><i>Puoi inserire in un unico colpo quante ore di produzione desideri scegliendo la data di inizio e fine e la macchina a cui assegnale<br />" +
					"Oppure fare drag and drop del blocchetto sul calendario della macchina alla quale si desidere assegnare il lavoro</i><br /><br />" +
					"<b>Inserimento automatico</b>" +
					'<form id="autoassignjoborders">' + '<input type="hidden" name="joborder" value="' + window.todojoborders[index].id + '" />' +
					'A partire da <sup>*</sup><input type="text" class="autostart" required /> <br />' +
					'Fino a <input type="text" class="autoend" /> <br />' +
					machineSelect + '<br /><br /><input type="submit" value="Auto assegna" />' +
					'</form>' +
					"<br /><br /><b>Inserimento manuale (drag and drop)</b><br /><br /></div>" +
					"<div id='joborderevent'></div>");
			
			$("#jobordersummary .autostart").datepicker( { dateFormat: "dd/mm/yy" } );
			$("#jobordersummary .autoend").datepicker( { dateFormat: "dd/mm/yy" } );
			
			var $block = $("#joborderevent"),
				title = '[' + window.todojoborders[index].id + "] " +
					window.todojoborders[index].client.code +  " - " +
					window.todojoborders[index].client.name,
				color = window.todojoborders[index].color;
			
			$block.html(title);
			event = {
				joborder: window.todojoborders[index].id,
				title: title,
				color: color,
				type: "assignedjoborder"
			};
			$block.data('event', event);
			
			$block.draggable({
				zIndex: 999,
				appendTo: "body",
			    helper: "clone",
			    revert: true,
			    revertduration: 0,
			});
			$block.addClass("fc-draggable-event");
			$block.addClass("block");
			$block.css('background-color', color);
	    }
	}
});

<c:forEach var="machine" items="${machines}">
	$("#m${machine.id}Calendar").fullCalendar({
		lang: 'it',
		weekNumbers: true,
		editable: window.user.canAssignJobOrder,
		droppable: window.user.canAssignJobOrder,
	    eventOverlap: window.user.canAssignJobOrder,
	    disableResizing: true,
		eventReceive: function(event) {
			if(window.user.canAssignJobOrder) {
			    $.post("<%=request.getContextPath()%>/add?what=" + event.type, toSend(event, ${machine.id}), function(data) {
			            	if(event.type == "assignedjoborder") {
			            		newAssignedJobOrder(data, ${machine.id});
			            	} else {
			            	    console.log(data);
			            	}
							$("#m${machine.id}Calendar").fullCalendar( 'refetchEvents' );
							$("#m${machine.id}Calendar").fullCalendar( 'rerenderEvents' );
				    });
			}
		},
		eventDrop: function(event, delta, revertFunc) {
			if(window.user.canAssignJobOrder) {
			    $.post("<%=request.getContextPath()%>/edit?what=" + event.type,
			            {
			            	id: event.id,
			            	start: event._start._d.toUTCString(),
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
			if(window.user.canAssignJobOrder) {
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

$("#accordionCalendars").accordion({
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

$("#accordionActions").accordion({
    collapsible: true,
    heightStyle: "content"    
});

$(".autostart").datepicker( { dateFormat: "dd/mm/yy" } );
$(".autoend").datepicker( { dateFormat: "dd/mm/yy" } );

$("#deleteJobOrder").on('submit', function(e) {
    e.preventDefault();
	if(window.user.canAddJobOrder) {
	    var start = $(this).find(".autostart").datepicker("getDate"),
	        end = $(this).find(".autoend").datepicker("getDate");
	    // datepicker start from the day before the selection (wtf)
	    start.setDate(start.getDate() + 1); //always defined (required field)
	    end.setDate(end.getDate() + 1); // idem for sampling ^
	    
	    var joborder = $(this).find('select[name="joborder"]').val(),
	    	machine =  $(this).find('select[name="machine"]').val();
	    
	    $.post("<%=request.getContextPath()%>/delete?what=assignedjoborder",
	            {
	            	start: start.toUTCString(),
	            	end:   end.toUTCString(),
	            	machine: machine,
	            	joborder: joborder
	            }, function(data){
	        		if(data != 'ok') { alert(data); }
	        		$("#m" + machine + "Calendar").fullCalendar( 'refetchEvents' );
					$("#m" + machine + "Calendar").fullCalendar( 'rerenderEvents' );
	            });
	}
   
});

</script>