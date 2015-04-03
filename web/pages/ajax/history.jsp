<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page errorPage="../../errors/exception.jsp"%>
<%@ page session="true"%>
<%@ page import="com.viaagnolettisrl.hibernate.*"%>
<%@ page import="com.viaagnolettisrl.*"%>
<%@ page import="com.google.gson.*"%>
<%@ page import="java.util.List"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%
	User user = (User) session.getAttribute(LoginServlet.USER);
	if (user == null) {
		response.sendRedirect(request.getContextPath()
				+ LoginServlet.LOGIN_FORM);
	}
%>
<table id="history-table" class="display" cellspacing="0" width="100%">
	<thead>
		<tr class="ui-widget-header ">
			<th>Data</th>
			<th>Utente</th>
			<th>Azione</th>
			<th>Contenuto</th>
		</tr>
	</thead>
	<tbody>
	</tbody>
</table>
<%Gson gson = new Gson();%>
<script>
$("#history-table").dataTable({
	"bJQueryUI": true,
	"bProcestrueng": true,
	"sPaginationType": "full_numbers",
	"order": [[ 0, "desc" ]],
	"language": {
		"url": "<%=request.getContextPath()%>/scripts/datatables/italian.js"
	},
	"data": <%=gson.toJson(GetList.Histories())%>,
    columns: [
              { data: 'dateTime', name: "dateTime" },
              { data: 'user.username', name: 'user.username' },
              { data: 'action', name: 'action' },
              { data: 'what', name: 'what' }
          ]
});
</script>