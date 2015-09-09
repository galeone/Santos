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
								<option value="${loop.index}">[${entry.description}] TR:
									<c:choose>
										<c:when test="${ entry.missingTimeWithOffset > 60 }">
											<fmt:formatNumber value="${entry.missingTimeWithOffset % 60 == 0 ? entry.missingTimeWithOffset / 60 : entry.missingTimeWithOffset / 60  -0.5}"
												maxFractionDigits="0" /> ore e ${entry.missingTimeWithOffset % 60}
											minuti
										</c:when>
										<c:otherwise>
											<fmt:formatNumber value="${entry.missingTimeWithOffset / 60}"
												maxFractionDigits="0" /> ore e ${entry.missingTimeWithOffset % 60}
											minuti
										</c:otherwise>
									</c:choose>
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
								Ore: <input style="display: inline" type="number" min="0" max="24" value="0" name="hours" required /><br />
								Minuti: <input style="display: inline" type="number" min="0" max="59" value="0" name="minutes" required /><br />
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
								Ore: <input style="display: inline" type="number" min="0" max="24" value="0" name="hours" required/><br />
								Minuti: <input style="display: inline" type="number" min="0" max="59" value="0" name="minutes" required /><br />
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
			<h3>Calendario generale</h3>
			<div>
			<i>Scegli un mese di inzio e fine (il giorno non è importante)</i>
			<form method="get" target="_blank" action="<%=request.getContextPath()%>/pages/global.jsp">
				A partire da <sup>*</sup><input type="text" class="autostart" name="start" required /><br />
				Fino a <sup>*</sup><input type="text" class="autoend" name="end" required /> <br />
				<input type="submit" value="Genera calendario" /><br />
			</form>
			</div>
		</div>
		<!-- accorion -->
	</div>
	<!-- leftc -->
	<div class="rightc">
		<h1>Calendario per macchina</h1> <div id="message"></div>
		<div id="accordionCalendars">
			<c:forEach var="machine" items="${machines}">
				<h3>
					<c:out value="${machine.name}" />
					-
					<c:out value="${machine.type}" />
					- Finezza: ${machine.nicety} -
					ID: ${machine.id}
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
			
			var hours = parseInt(whform.find('input[name="hours"]').val(), 10),
			minutes = parseInt(whform.find('input[name="minutes"]').val(), 10);
			hours = isNaN(hours) ? 0 : hours;
			minutes = isNaN(minutes) ? 0 : minutes;
			
			event.last =(hours * 60 + minutes)*60*1000;
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
			var hours = parseInt(whform.find('input[name="hours"]').val(), 10),
			minutes = parseInt(whform.find('input[name="minutes"]').val(), 10);
			hours = isNaN(hours) ? 0 : hours;
			minutes = isNaN(minutes) ? 0 : minutes;
			
			event.last = (hours * 60 + minutes)*60*1000;
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
 	    $("#message").html("Attendere prego...");
 	    $.post("<%=request.getContextPath()%>/add?what=sampling",
 	            {
 	            	start: start.toUTCString(),
 	            	end:   end.toUTCString(),
 	            	client: client,
 	            	machine: machine,
 	            	description: description
 	            }, function(data){
 	            	$("#message").html("");
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
 	    $("#message").html("Attendere prego...");
 	    $.post("<%=request.getContextPath()%>/add?what=maintenance",
 	            {
 	            	start: start.toUTCString(),
 	            	end:   end.toUTCString(),
 	            	machine: machine,
 	            	description: description
 	            }, function(data){
 	            	$("#message").html("");
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
		window.todojoborders[remain.data("arrayindex")].missingTimeWithOffset = minutes;
		var lastHours = Math.floor(minutes / 60), lastMinutes = minutes % 60;
		out = lastHours + " ore e " + lastMinutes + " minuti";
		remain.html("<b>" + out +"</b>");
		var selectVal = $("#selectvalue");
		$('#todoJobOrders option[value="'+selectVal.data('value')+'"]').html("[" + selectVal.data('joborderdescr') + "] TR:" + out);
		$("#todoJobOrders").selectmenu("refresh");
		if(minutes <= 0) {
		    $("#jobordersforms").remove();
		}
		$("#autoaddstatus").html('ok...');
		setTimeout(function() {
		    $("#autoaddstatus").html('');
		},3000);
    }
}

function toSend(event, machine) {
    console.log(event);
    if(event.type == 'assignedjoborder') {
    	var ret = {
    		start: event._start._d.toUTCString(),
    		machine: machine,
	    	joborder: event.joborder
    	};
    	
    	if(typeof event.last !== 'undefined') {
    		var end = new Date(event._start._d);
    		end.setTime(end.getTime() + event.last);
    		ret.end = end.toUTCString();
    	}
		return ret;
    }
			
	if(event.type == 'sampling') {
	    var end = new Date(event._start._d);
	    end.setTime(end.getTime() + event.last);
	    return {
			start: event._start._d.toUTCString(),
			end: end.toUTCString(),
			machine: machine,
			client: event.client,
			description: event.description };
	}
			
	if(event.type == 'maintenance') {
	    var end = new Date(event._start._d);
	    end.setTime(end.getTime() + event.last);	    
	    return {
				start: event._start._d.toUTCString(),
				end: end.toUTCString(),
            	machine: machine,
            	description: event.description };
	}
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
	    $("#autoaddstatus").html('Inserimento...');
	    $("#message").html("Attendere prego...");
	    $.post("<%=request.getContextPath()%>/add?what=assignedjoborder",
	            {
	            	start: start.toUTCString(),
	            	end:   end.toUTCString(),
	            	machine: machine,
	            	joborder: jo
	            }, function(data) {
	        		newAssignedJobOrder(data, machine);
	        		$("#message").html("");
	        		$("#m" + machine + "Calendar").fullCalendar( 'refetchEvents' );
					$("#m" + machine + "Calendar").fullCalendar( 'rerenderEvents' );
	   });
	}
});

$("#todoJobOrders").selectmenu({
	select: function(event, ui) {
	    var index = parseInt(ui.item.element.val(), 10);
	    if(!isNaN(index)) {
		    var leadMinutes = parseInt(window.todojoborders[index].leadTime),
		    	leadHours = Math.floor(leadMinutes / 60),
		    	leadMinutes = leadMinutes % 60,
		    	dataRemainMinutes = parseInt(window.todojoborders[index].missingTimeWithOffset),
		    	remainMinutes = dataRemainMinutes,
		    	remainHours = Math.floor(remainMinutes / 60),
		    	remainMinutes = remainMinutes  % 60;
		    
			$("#jobordersummary").html( '<div id="selectvalue" data-value="'+index+'" data-joborderdescr="'+window.todojoborders[index].description+'"></div>' +
					"Nome cliente: " + window.todojoborders[index].client.name + "<br />" +
					"Codice cliente: " + window.todojoborders[index].client.code + "<br /><br />" +
					"Tempo totale: <div><b>" + leadHours + " ore e " + leadMinutes + " minuti</b></div><br />" +
					"Tempo rimanente (con variazioni): <div id='remainingTime' data-minutes="+dataRemainMinutes+" data-arrayindex="+index+"><b>" + remainHours + " ore e " + remainMinutes + " minuti</div></b><br /><br />" +
					"<div id=\"jobordersforms\"><i>Puoi inserire in un unico colpo quante ore di produzione desideri scegliendo la data di inizio e fine e la macchina a cui assegnale<br />" +
					"Oppure fare drag and drop del blocchetto sul calendario della macchina alla quale si desidere assegnare il lavoro</i><br /><br />" +
					"<b>Inserimento automatico</b>" +
					'<form id="autoassignjoborders">' + '<input type="hidden" name="joborder" value="' + window.todojoborders[index].id + '" />' +
					'A partire da <sup>*</sup><input type="text" class="autostart" required /> <br />' +
					'Fino a <input type="text" class="autoend" /> <br />' +
					machineSelect + '<br /><br /><input type="submit" value="Auto assegna" /><div id="autoaddstatus"></div>' +
					'</form>' +
					"<br /><br /><b>Inserimento manuale (drag and drop)</b><br /><br /></div><i>0 ore e 0 minuti equivalngono all'intera giornata lavorativa.<br /> Un numero di ore maggiore di quello della giornata lavorativa vengono troncate alla durata della giornata lavorativa.</i><br/>" +
					'<form id="ajeventform">Ore: <input style="display: inline" type="number" min="0" max="24" value="0" name="hours"/><br />' +
					'Minuti: <input style="display: inline" type="number" min="0" max="59" value="0" name="minutes" required /><br />' +
					'</form><br /><div id="joborderevent"></div>');
			
			$("#jobordersummary .autoend").datepicker();
			$("#jobordersummary .autostart").datepicker({
			    onSelect: function(dateText, inst) {
					$("#jobordersummary .autoend").datepicker("option", "defaultDate", dateText);
			    }
			});
			
			var $block = $("#joborderevent"),
				title = '[' + window.todojoborders[index].id + "] " +
					window.todojoborders[index].client.code +  " - " +
					window.todojoborders[index].client.name,
				color = window.todojoborders[index].color;
			
			$block.html(title);
			$block.data('event', {
				joborder: window.todojoborders[index].id,
				title: title,
				color: color,
				type: "assignedjoborder"
			});
			
			$block.draggable({
				zIndex: 999,
			    revert: true,
			    revertduration: 0,
				appendTo: "body",
				helper: function() {
				    var whform = $("#ajeventform");
				    if(!whform[0].checkValidity()) {
						alert("completa correttamente i campi");
				    } else {
						var hours = parseInt(whform.find('input[name="hours"]').val(), 10),
							minutes = parseInt(whform.find('input[name="minutes"]').val(), 10);
						hours = isNaN(hours) ? 0 : hours;
						minutes = isNaN(minutes) ? 0 : minutes;
						var last = (hours * 60 + minutes)*60*1000;
						var event = $block.data('event');
						if(last > 0) {
							event.last = last;
							$block.data('event', event);
						} else {
							if(typeof event.last !== 'undefined') {
								delete event.last;
							}
						}
				    }
				    return $block.clone(true);
				}
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
		timeFormat: ' ', /* remove starting hours from block */
		weekNumbers: true,
		editable: window.user.canAssignJobOrder,
		droppable: window.user.canAssignJobOrder,
	    eventOverlap: window.user.canAssignJobOrder,
	    disableResizing: true,
		eventReceive: function(event) {
			if(window.user.canAssignJobOrder) {
				$("#message").html("Attendere prego...");
			    $.post("<%=request.getContextPath()%>/add?what=" + event.type, toSend(event, ${machine.id}), function(data) {
			            	if(event.type == "assignedjoborder") {
			            		newAssignedJobOrder(data, ${machine.id});
			            	} else {
			            	    console.log(data);
			            	}
			            	$("#message").html("");
							$("#m${machine.id}Calendar").fullCalendar( 'refetchEvents' );
							$("#m${machine.id}Calendar").fullCalendar( 'rerenderEvents' );
				    });
			}
		},
		eventDrop: function(event, delta, revertFunc) {
			if(window.user.canAssignJobOrder) {
			    if(event.allDay) {
					alert("Non puoi muovere questo tipo di evento");
					revertFunc();
					return;
			    }
			    $("#message").html("Attendere prego...");
			    $.post("<%=request.getContextPath()%>/edit?what=" + event.type,
			            {
			            	id: event.id,
			            	start: event._start._d.toUTCString(),
			            	end: event._end._d.toUTCString(),
			            	// global events (does not have machine/joborder)
			            	machine:  typeof(event.machine)  == 'undefined' ? '' : event.machine.id,
				            joborder: typeof(event.jobOrder) == 'undefined' ? '' : event.jobOrder.id
			            },
						function(data){
			            	$("#message").html("");
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
		            $("#message").html("Attendere prego...");
				    $.post("<%=request.getContextPath()%>/delete?what=" + event.type, { id: event.id }, function(data) {
				    	if(data == 'ok') {
				    		$("#m${machine.id}Calendar").fullCalendar( 'refetchEvents' );
							$("#m${machine.id}Calendar").fullCalendar( 'rerenderEvents' );
							trashEl.removeClass("to-trash");
				    	} else {
				    		alert(data);
				    	}
				    	$("#message").html("");
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
		    $("#message").html("");
		    visibleCalendar.fullCalendar( 'refetchEvents' );
		    visibleCalendar.fullCalendar( 'rerenderEvents' );
		}		
    }
});

$("#accordionActions").accordion({
    collapsible: true,
    heightStyle: "content"    
});

$(".autoend").datepicker();
$(".autostart").datepicker({
    onSelect: function(dateText, inst) {
		$(".autoend").datepicker("option", "defaultDate", dateText);
    }
});

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
	    $("#message").html("Attendere prego...");
	    $.post("<%=request.getContextPath()%>/delete?what=assignedjoborder",
	            {
	            	start: start.toUTCString(),
	            	end:   end.toUTCString(),
	            	machine: machine,
	            	joborder: joborder
	            }, function(data){
	            	$("#message").html("");
	        		if(data != 'ok') { alert(data); }
	        		$("#m" + machine + "Calendar").fullCalendar( 'refetchEvents' );
					$("#m" + machine + "Calendar").fullCalendar( 'rerenderEvents' );
	            });
	}
   
});

var lc = $(".leftc"), goodTop = lc.offset().top, roundedTop = Math.round(goodTop);
lc.css({"top": goodTop,
    "bottom": "0",
    "position": "fixed",
    "overflow-y": "auto",
    "overflow-x": "hidden"
});
$(document).scroll(function(){
    if ( $(this).scrollTop() >= roundedTop ){
		lc.css('top', "0");
    } else {
		lc.css('top', goodTop);
    }
});
</script>