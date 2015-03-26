<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page errorPage="../errors/exception.jsp"%>
<%@ page session="true"%>
<%@ page import="java.util.*"%>
<%@ page import="com.viaagnolettisrl.*"%>
<%@ page import="com.viaagnolettisrl.hibernate.*"%>
<%
	Boolean loggedIn = (Boolean)session.getAttribute(LoginServlet.LOGIN_OK);
	if( loggedIn != null && !loggedIn ) {
		response.sendRedirect(request.getContextPath() + LoginServlet.LOGIN_FORM);
	}
	//User user = (User)session.getAttribute("user");
	User user = new User();
	user.setCanAddClient(true);
	user.setCanAddJobOrder(true);
	user.setCanAddMachine(true);
%>
<jsp:include page="../fragments/header.jsp">
	<jsp:param name="title" value="Gestione macchine"/>
</jsp:include> 

<ol id="menu">
	<li class="ui-state-default ui-corner-all">
		<a href="#home">Home</a>
	</li>
<%

	if(user.getCanAddClient()) {
%>
	<li class="ui-state-default ui-corner-all">
		<span class="ui-icon ui-icon-plusthick"></span>
		<a href="#add-client">Cliente</a>
	</li>
<%
	}
	if(user.getCanAddJobOrder()) {
%>
	<li class="ui-state-default ui-corner-all">
		<span class="ui-icon ui-icon-plusthick"></span>
		<a href="#add-joborder">Commessa</a>
	</li>
<%
	}
	if(user.getCanAddMachine()) {
%>
	<li class="ui-state-default ui-corner-all">
		<span class="ui-icon ui-icon-plusthick"></span>
		<a href="#add-machine">Macchina</a>
	</li>
<%
	}
%>
</ol>
<div id="home">
</div>
<script>
$("#menu").tabs();
</script>
<%@ include file="../fragments/footer.jsp" %>