<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page errorPage="../errors/exception.jsp"%>
<%@ page session="true"%>
<%@ page import="java.util.*"%>
<%@ page import="com.viaagnolettisrl.*"%>
<%@ page import="com.viaagnolettisrl.hibernate.*"%>
<%
	User user = (User)session.getAttribute(LoginServlet.USER);
	if( user == null ) {
		response.sendRedirect(request.getContextPath() + LoginServlet.LOGIN_FORM);
	}
%>
<jsp:include page="../fragments/header.jsp">
	<jsp:param name="title" value="Gestione macchine" />
</jsp:include>
<div id="menu">
	<!-- menu(main) -->
	<ol>
		<li class="ui-state-default ui-corner-all"><a href="#home">Home</a>
		</li>
		<li class="ui-state-default ui-corner-all"><a href="<%= request.getContextPath() %>/pages/ajax/clients.jsp">Clienti</a>
		</li>
		<li class="ui-state-default ui-corner-all"><a href="<%= request.getContextPath() %>/pages/ajax/joborders.jsp">Commesse</a>
		</li>
		<li class="ui-state-default ui-corner-all"><a href="<%= request.getContextPath() %>/pages/ajax/machines.jsp">Macchine</a>
		</li>
		<li class="ui-state-default ui-corner-all" style="float: right">
			<a href='<%= request.getContextPath() %>/pages/ajax/logout.jsp'>Esci</a>
		</li>
		<%
	if(user.getIsAdmin()) {
%>
		<li class="ui-state-default ui-corner-all"><a href="<%= request.getContextPath() %>/pages/ajax/users.jsp">Utenti</a>
		</li>
		<li class="ui-state-default ui-corner-all"><a href="<%= request.getContextPath() %>/pages/ajax/history.jsp">Storico</a>
		</li>
		<%		
	}
%>
	</ol>
	<div id="home">
		<div id="globalCalendar" style="max-width: 900px; margin: 0 auto"></div>
	</div>
</div>
<!-- /menu (main) -->

<script>
$(document).ready(function() {
	$("#menu").tabs();
	
	$("#globalCalendar").fullCalendar({
		lang: 'it'
	});
});
</script>
<%@ include file="../fragments/footer.jsp"%>