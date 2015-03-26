<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page errorPage="../errors/exception.jsp"%>
<%@ page session="true"%>
<%@ page import="java.util.*"%>
<%@ page import="com.viaagnolettisrl.*"%>
<%
	Boolean loggedIn = (Boolean)session.getAttribute(LoginServlet.LOGIN_OK);
	if( loggedIn != null && loggedIn ) {
		session.setAttribute(LoginServlet.LOGIN_OK, false);
		response.sendRedirect(request.getContextPath() + LoginServlet.LOGIN_FORM);
	}
%>
<jsp:include page="../fragments/header.jsp">
	<jsp:param name="title" value="Autenticazione"/>
</jsp:include> 
<%
	if(request.getParameter("failed") != null) {
%>
	<div id="error" style="display:none" data-next="">
		<p>
			<span class="ui-icon ui-icon-alert" style="float:left; margin:0 7px 20px 0;"></span>
			Coppia username/password non valida.
		</p>
	</div>
<%
	}
%>
<div style="display:none">
	<form id="loginfrm" title="Autenticazione" method="post" action="<%= request.getContextPath() %>/login">
		<label for="username">Nome utente:</label> <input type="text" name="username" id="username">
		<label for="password">Password:</label> <input type="password" name="password" id="password">
		<input type="submit" value="Login">
	</form>
</div>
<script>
$( "#loginfrm" ).dialog({
   	autoOpen: true,
   	modal:true,
   	dialogClass: "no-close",
       open: function() {
           // On open, hide the original submit button
           $( this ).find( "[type=submit]" ).hide();
       },
       buttons: [
           {
               text: "Login",
               click: $.noop,
               type: "submit",
               form: "loginfrm" // <-- Make the association
           }
       ]
   });
</script>
<%@ include file="../fragments/footer.jsp" %>