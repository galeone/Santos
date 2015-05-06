<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page errorPage="../errors/exception.jsp"%>
<%@ page session="true"%>
<%@ page import="java.util.Collection"%>
<%@ page import="com.google.gson.*"%>
<%@ page import="com.viaagnolettisrl.*"%>
<%@ page import="com.viaagnolettisrl.hibernate.*"%>
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
		<h4>Giorni non lavorativi</h4>
		<div id="nonworkingevent"></div>
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
	revertduration: 0
});
$block.addClass("fc-draggable-event nonworkingday");


$("#globalCalendar").fullCalendar({
	lang: 'it',
	editable: window.user.isAdmin,
	droppable: window.user.isAdmin,
    eventOverlap: function(stillEvent, movingEvent) {
        return !stillEvent.allDay;
    },
	eventReceive: function(event) {
		if(window.user.isAdmin) {
		    $.post("<%=request.getContextPath()%>/add?what=" + event.type,
		            {
		            	start: event._start._d.toUTCString()
		            }, function(data) {
		        		var id = jQuery.parseJSON(data).id;
		                event.id = id;
		                $("#globalCalendar").fullCalendar('updateEvent',event);
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
	        url: "<%=request.getContextPath()%>/get?what=nonworkingdays"
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
</script>