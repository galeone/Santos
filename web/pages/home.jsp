<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page errorPage="../errors/exception.jsp"%>
<%@ page session="true"%>
<%@ page import="java.util.*"%>
<%@ page import="com.viaagnolettisrl.*"%>
<%@ page import="com.viaagnolettisrl.hibernate.*"%>
<%
	User user = (User)session.getAttribute(LoginServlet.USER);
	if( user != null ) {
		response.sendRedirect(request.getContextPath() + LoginServlet.LOGIN_FORM);
	}
	/*
	
	User user = new User();
	user.setCanAddClient(true);
	user.setCanAddJobOrder(true);
	user.setCanAddMachine(true);
	user.setIsAdmin(true);
	session.setAttribute("user", user);
	*/
%>
<jsp:include page="../fragments/header.jsp">
	<jsp:param name="title" value="Gestione macchine" />
</jsp:include>
<div id="menu">
	<!-- menu(main) -->
	<ol>
		<li class="ui-state-default ui-corner-all"><a href="#home">Home</a>
		</li>
		<li class="ui-state-default ui-corner-all"><a href="#clients">Clienti</a>
		</li>
		<li class="ui-state-default ui-corner-all"><a href="#joborders">Commesse</a>
		</li>
		<li class="ui-state-default ui-corner-all"><a href="#machines">Macchine</a>
		</li>
		<li class="ui-state-default ui-corner-all" style="float: right">
			<a href='<%= request.getContextPath() %>/pages/logout.jsp'>Esci</a>
		</li>
		<%
	if(user.getIsAdmin()) {
%>
		<li class="ui-state-default ui-corner-all"><a href="#users">Utenti</a>
		</li>
		<li class="ui-state-default ui-corner-all"><a href="#history">Storico</a>
		</li>
		<%		
	}
%>
	</ol>
	<div id="home">
		<div id="globalCalendar" style="max-width: 900px; margin: 0 auto"></div>
	</div>

	<div id="clients">
		<jsp:include page="../fragments/clients.jsp" />
	</div>

	<div id="joborders">
		<%
	// mostra commesse
	if(user.getCanAddJobOrder()) {
		//aggiungi commesse
	}
%>
	</div>
	<!-- job orders -->

	<div id="machines">
		<%
	// mostra macchine 
	if(user.getCanAddMachine()) {
		// aggiungi macchine
	}
%>
	</div>
	<!-- machines -->
	<%
	if(user.getIsAdmin()) {
%>
	<div id="history"></div>
	<div id="users">
		<jsp:include page="../fragments/users.jsp" />
	</div>
	<%
	} // isAdmin
%>
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