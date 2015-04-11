<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page errorPage="../errors/exception.jsp"%>
<%@ page session="true"%>
<%@ page import="com.viaagnolettisrl.*"%>
<%@ page import="com.viaagnolettisrl.hibernate.*"%>
<%@ page import="com.google.gson.*"%>
<%
	User user = (User)session.getAttribute(LoginServlet.USER);
	if( user == null ) {
		response.sendRedirect(request.getContextPath() + LoginServlet.LOGIN_FORM);
	}
	Gson gson = new Gson();
%>
<jsp:include page="../fragments/header.jsp">
	<jsp:param name="title" value="Gestione macchine" />
</jsp:include>
<div id="menu">
	<!-- menu(main) -->
	<ol>
		<li class="ui-state-default ui-corner-all"><a
			href="<%= request.getContextPath() %>/pages/ajax/home.jsp">Home</a></li>
		<li class="ui-state-default ui-corner-all"><a
			href="<%= request.getContextPath() %>/pages/ajax/clients.jsp">Clienti</a>
		</li>
		<li class="ui-state-default ui-corner-all"><a
			href="<%= request.getContextPath() %>/pages/ajax/joborders.jsp">Commesse</a>
		</li>
		<li class="ui-state-default ui-corner-all"><a
			href="<%= request.getContextPath() %>/pages/ajax/machines.jsp">Macchine</a>
		</li>
		<li class="ui-state-default ui-corner-all"><a
			href="<%= request.getContextPath() %>/pages/ajax/program.jsp">Programma</a>
		</li>
		<li class="ui-state-default ui-corner-all" style="float: right">
			<a href='<%= request.getContextPath() %>/pages/ajax/logout.jsp'>Esci</a>
		</li>
		<%
	if(user.getIsAdmin()) {
%>
		<li class="ui-state-default ui-corner-all"><a
			href="<%= request.getContextPath() %>/pages/ajax/users.jsp">Utenti</a>
		</li>
		<li class="ui-state-default ui-corner-all"><a
			href="<%= request.getContextPath() %>/pages/ajax/history.jsp">Storico</a>
		</li>
		<%		
	}
%>
	</ol>
</div>
<script>
$(document).ready(function() {
	window.user = <%= gson.toJson(user) %>;
	$("#menu").tabs({
		activate: function( event, ui ) {
			// REMOVE DIALOGS created by datatables.editable
			// P.S: fuck you.
			$("div[aria-describedby^=formAddNew]").remove();
			ui.oldPanel.empty();
		}
	});
});
</script>
<%@ include file="../fragments/footer.jsp"%>