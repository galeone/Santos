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
		<li class="ui-state-default ui-corner-all"><a
			href="<%= request.getContextPath() %>/pages/ajax/sampling.jsp">Campionature</a>
		</li>
		<li class="ui-state-default ui-corner-all" style="float: right">
			<a href='<%= request.getContextPath() %>/pages/ajax/logout.jsp'>Esci</a>
		</li>
		<div id="reload-current-tab" class="ui-state-default ui-corner-all" style="float:right; margin-right:8px; margin-top:-2px; cursor:pointer">
			<img src="<%= request.getContextPath() %>/styles/images/reload.png" />
		</div>
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
	
	$("#reload-current-tab").on('click', function(e) {
	    e.preventDefault();
	    var current_index = $("#menu").tabs("option","active");
		$("#menu").tabs('load',current_index);
	});
	
});
</script>
<%@ include file="../fragments/footer.jsp"%>