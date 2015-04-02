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
<form id="formAddNewRow" action="#" title="Aggiunti commessa">
	<p class="validateTips">Tutti i campi sono necessari.</p>
	<fieldset>
		<input type="hidden" name="fakeid" id="fakeid" rel="0" value="0" />
		<label for="client">Cliente</label>
		<select name="client" id="client" required rel="1">
			<%
			application.setAttribute("clients", GetList.Clients());
			%>
			<c:forEach var="client" items="${clients}">
				<option value="${client.id}">${client.name}</option>
			</c:forEach>
		</select>
		<!-- tempo in gg mesi ore -->
		<input type="hidden" name="fakeid" id="fakeid" rel="2" value="0" />
	</fieldset>
</form>

<table id="joborders-table" class="display" cellspacing="0" width="100%">
	<thead>
		<tr class="ui-widget-header ">
			<th>ID</th>
			<th>Cliente</th>
			<th>Tempo di produzione</th>
		</tr>
	</thead>
	<tbody>
	</tbody>
</table>
<% String style = user.getCanAddJobOrder() ? "" : "display:none"; %>
<button id="btnAddNewRowJobOrder" style="<%=style%>">Aggiungi commessa</button>
<button id="btnDeleteRowJobOrder" style="<%=style%>">Cancella commessa</button>
<script>
<%Gson gson = new Gson();%>
$(document).ready(function() {
	$("#btnAddNewRow").off(); // remove handlers (conflits with same id in other tabs)
	$("#btnDeleteRow").off();
	$("#joborders-table").dataTable({
		"bJQueryUI": true,
		"bProcestrueng": true,
		"sPaginationType": "full_numbers",
		"language": {
			"url": "<%=request.getContextPath()%>/scripts/datatables/italian.js"
		},
		"data": <%=gson.toJson(GetList.JobOrders())%>,
		"createdRow": function ( row, data, index ) {
			row.setAttribute('id', data.id);
		},
	    columns: [
	              {
	            	  data: 'id',
	            	  name: 'id',
	            	  createdCell: function (td, cellData, rowData, row, col) {
	            			td.setAttribute('class', 'read_only');
	            	  }
	              },
	              { data: 'client.name', name: "client.name" },
	              { data: 'leadTime', name: 'leadTime' }
	          ]
	}).makeEditable({
		sDeleteURL: "<%=request.getContextPath()%>/delete?what=joborder",
		sUpdateURL: "<%=request.getContextPath()%>/edit?what=joborder",
		sAddURL: "<%=request.getContextPath()%>/add?what=joborder",
		sReadOnlyCellClass : "read_only",
		sAddNewRowButtonId: "btnAddNewRowJobOrder",
		sDeleteRowButtonId: "btnDeleteRowJobOrder",
		fnOnDeleting: function() {
			return confirm("Vuoi davvero rimuovere questa commessa?");
		},
		"fnOnNewRowPosted": function(data) {
			try {
				JSON.parse(data);
				return true;
			}catch(e) {
				alert(data);
				return false;
			}
		},
	    "aoColumns": [
	                  {},
	                  {},
	                  {}
	              ]
	});
});
</script>