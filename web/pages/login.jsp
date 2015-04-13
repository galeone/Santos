<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page errorPage="../errors/exception.jsp"%>
<%@ page session="true"%>
<%@ page import="com.viaagnolettisrl.*"%>
<%@ page import="com.viaagnolettisrl.hibernate.*"%>
<%
	User user = (User)session.getAttribute(LoginServlet.USER);
	if( user != null ) {
		response.sendRedirect(request.getContextPath() + LoginServlet.LOGIN_NEXT);
	}
%>
<jsp:include page="../fragments/header.jsp">
	<jsp:param name="title" value="Autenticazione" />
</jsp:include>
<%
	if(request.getParameter("failed") != null) {
%>
<div id="error" style="display: none" data-next="">
	<p>
		<span class="ui-icon ui-icon-alert"
			style="float: left; margin: 0 7px 20px 0;"></span> Coppia
		username/password non valida.
	</p>
</div>
<%
	}
%>
<div style="display: none">
	<form id="loginfrm" title="Autenticazione" method="post"
		action="<%= request.getContextPath() %>/login">
		<label for="username">Nome utente:</label> <input type="text"
			name="username" id="username"> <label for="password">Password:</label>
		<input type="password" name="password" id="password">
		<input type="submit" value="Login" class="inner">
	</form>
</div>
<script>
$( "#loginfrm" ).dialog({
   	autoOpen: true,
   	modal:true,
   	dialogClass: "no-close",
   	closeOnEscape: false,
    buttons: [
           {
               text: "Login",
               click: function() {$("form").submit();},
               type: "submit"
           }
    ]
});
</script>
<%@ include file="../fragments/footer.jsp"%>