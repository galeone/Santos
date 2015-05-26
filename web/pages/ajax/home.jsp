<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page errorPage="../errors/exception.jsp"%>
<%@ page session="true"%>
<%@ page import="java.util.Collection"%>
<%@ page import="com.google.gson.*"%>
<%@ page import="it.galeone_dev.santos.servlet.*"%>
<%@ page import="it.galeone_dev.santos.hibernate.models.*"%>
<%
	User user = (User)session.getAttribute(LoginServlet.USER);
	if( user == null ) {
		response.sendRedirect(request.getContextPath() + LoginServlet.LOGIN_FORM);
	}
	Gson gson = new Gson();
%>
<div class="wrap">
	<%
if(user.getIsAdmin()) { %>
	<div class="leftc" style="background: #eee">
	<h1>Gestione giorni/ore lavorative</h1>
	<div id="accordion">
		<h4>Giorni non lavorativi</h4>
		<div>
			<i>Trascina il blocchetto sul giorno che vuoi rendere non lavorativo.<br />
			La produzione delle macchine slitterà in avanti automaticamente.
			</i>
			<div id="nonworkingevent"></div><br />
		</div>
		<h4>Giorni lavorativi</h4>
		<div>
			<i>Puoi modificare le ore lavorative solo per date successive o uguali ad oggi.<br />
			La produzione si adeguerà di conseguenza.
			</i><br />
			<h1>Inserimento manuale (drag and drop)</h1>
			<i>Inserisci il numero di ore e trascina il blocchetto sul calendario</i>
			<form id="whform">
				Ore: <input style="display: inline" type="number" min="1" max="24" id="wdhours" value="0" /><br />
			</form><br />
			<div id="workingday-event"></div>
			<hr />
			<h1>Inserimento automatico</h1>
			<p><i>Scegli la data di inzio, fine e le ore. Poi assegna</i></p>
			<form id="autoassignworkingdays">
				A partire da <sup>*</sup><input type="text" class="autostart" required /> <br />
				Fino a <sup>*</sup><input type="text" class="autoend" required /> <br />
				<input type="number" min="1" max="24" id="wdhours" value="0" /><br />
				<br /><br /><input type="submit" value="Auto assegna" />
			</form>
		</div>
		</div>
	</div>
	<div class="rightc">
		<div id='globalCalendar'></div>
	</div>
	<% } else { %>
	<div id='globalCalendar'></div>
	<% } %>

	<div style='clear: both'></div>
</div>
<script>
var $block = $("#nonworkingevent"), title = 'Giorno non lavorativo',
event = {
	title: title,
	allDay: true,
	color: '#ff9f89',
	type: 'nonworkingday'
};
$block.html(title);
$block.data('event', event);

$block.draggable({
	zIndex: 999,
	revert: true,
	revertduration: 0,
	appendTo: "body",
	helper: "clone"
});
$block.addClass("fc-draggable-event nonworkingday");

$block = $("#workingday-event"), title = 'Ore lavorative',
event = {
	title: title,
	allDay: true,
	color: '#afafaf',
	type: 'workingday'
};
$block.html(title);
$block.data('event', event);

$block.draggable({
	zIndex: 999,
	revert: true,
	revertduration: 0,
	start: function() {
	    var whform = $("#whform");
	    if(!whform[0].checkValidity()) {
			alert("numero di ore non valido");
	    }
	},
	appendTo: "body",
	helper: "clone"
});
$block.addClass("fc-draggable-event workingday");


$("#globalCalendar").fullCalendar({
	lang: 'it',
	weekNumbers: true,
	editable: window.user.isAdmin,
	droppable: window.user.isAdmin,
    eventOverlap: window.user.isAdmin,
	eventReceive: function(event) {
		if(window.user.isAdmin) {
		    var end = new Date(event._start._d);
		    if(event.type == "nonworkingday") {
		    	end.setHours(end.getHours() + 24);
		    } else if(event.type == "workingday") {
				end.setHours(end.getHours() + parseInt($("#wdhours").val()));
		    }
		    $.post("<%=request.getContextPath()%>/add?what=" + event.type,
		            {
		            	start: event._start._d.toUTCString(),
		            	end: end.toUTCString()
		            }, function(data) {
		        		if(data != 'ok') { alert(data); }
	 		   			$("#globalCalendar").fullCalendar( 'refetchEvents' );
						$("#globalCalendar").fullCalendar( 'rerenderEvents' );
		    });
		}
	},
	eventDrop: function(event, delta, revertFunc) {
		if(window.user.isAdmin) {
		    $.post("<%=request.getContextPath()%>/edit?what=" + event.type,
		            {
		            	id: event.id,
		            	start: event._start._d.toUTCString()
		            },
					function(data){
		            	if(data != 'ok') { alert(data); revertFunc(); } 
		    });
		}
	},
	header: {
		left: '',
		center: 'title'
	},
	eventSources:[ {
	        url: "<%=request.getContextPath()%>/get?what=globalevents"
	    }
	],
	eventDragStop: function(event,jsEvent) {
		if(window.user.isAdmin) {
		    var trashEl = $('#homeTrash'), ofs = trashEl.offset(),
		    x1 = ofs.left, x2 = ofs.left + trashEl.outerWidth(true),
		    y1 = ofs.top, y2 = ofs.top + trashEl.outerHeight(true);

		    if (jsEvent.pageX >= x1 && jsEvent.pageX<= x2 &&
		        jsEvent.pageY>= y1 && jsEvent.pageY <= y2) {
			    $.post("<%=request.getContextPath()%>/delete?what=" + event.type, { id: event.id }, function(data) {
			    	if(data == 'ok') {
						$("#globalCalendar").fullCalendar( 'refetchEvents' );
						$("#globalCalendar").fullCalendar( 'rerenderEvents' );
			    	} else {
			    		alert(data);
			    	}
			    });
		    }
		}
	}
}).find(".fc-left").append('<div id="homeTrash" class="calendar-trash">' +
'<img src="<%=request.getContextPath()%>/styles/fullcalendar/trash.png"></img></div>');

$("#accordion").accordion({
    collapsible: true,
    heightStyle: "content"
});
</script>