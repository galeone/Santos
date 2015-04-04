<%@page import="java.util.HashMap"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page errorPage="../../errors/exception.jsp"%>
<%@ page session="true"%>
<%@ page import="com.viaagnolettisrl.hibernate.*"%>
<%@ page import="com.viaagnolettisrl.*"%>
<%@ page import="com.google.gson.*"%>
<%@ page import="java.util.List"%>
<%@ page import="java.util.Map"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%
	User user = (User) session.getAttribute(LoginServlet.USER);
	if (user == null) {
		response.sendRedirect(request.getContextPath()
				+ LoginServlet.LOGIN_FORM);
	}
%>
<form id="formAddNewRowJobOrder" action="#" title="Aggiunti commessa">
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
		<label for="giorni">Giorni</label>
		<input type="number" name="giorni" id="giorni" />
		<label for="ore">Ore</label>
		<input type="number" name="ore" id="ore" />
		<!-- tempo in gg e ore -->
		<input type="hidden" name="leadtime" id="leadtime" rel="2" value="0" />
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
<%
Gson gson = new Gson();
List<Client> clients = GetList.Clients();
Map<Long, String> mapClient = new HashMap<Long, String>();
for(Client c : clients) {
	   mapClient.put(c.getId(), c.getName());
}
%>
<script>
$("#ore").on('keyup mouseup', function() {
	var days = parseInt($("#giorni").val()),
		hours =  parseInt($(this).val());
	
	days  = isNaN(days) ? 0 : days;
	hours = isNaN(hours) ? 0 : hours;
	$("#leadtime").val(days * 24 + hours);
});
$("#giorni").on('keyup mouseup', function() {
	var days = parseInt($(this).val()),
		hours =  parseInt($("#ore").val());

	days  = isNaN(days) ? 0 : days;
	hours = isNaN(hours) ? 0 : hours;
	$("#leadtime").val(days * 24 + hours);
});

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
              { data: 'client.name', name: "client" },
              {
            	  data: 'leadTime',
            	  name: 'leadTime',
            	  render: dataTablesLeadTime
              }
          ]
}).makeEditable({
	sDeleteURL: "<%=request.getContextPath()%>/delete?what=joborder",
	sUpdateURL: "<%=request.getContextPath()%>/edit?what=joborder",
	sAddURL: "<%=request.getContextPath()%>/add?what=joborder",
	sReadOnlyCellClass : "read_only",
	sAddNewRowButtonId: "btnAddNewRowJobOrder",
	sDeleteRowButtonId: "btnDeleteRowJobOrder",
	sAddNewRowFormId: "formAddNewRowJobOrder",
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
                  {
                	  type: 'select',
                	  submit: 'Ok',
                	  cancel: 'Cancel',
                	  data: '<%=gson.toJson(mapClient) %>'
                  }, // client
                  {
                	  type: 'leadtime',
                      submit    : 'Ok',
                      cancel    : 'Cancel',
                  } //leadtime
              ]
});
</script>