<%@page import="java.util.HashMap"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page errorPage="../../errors/exception.jsp"%>
<%@ page session="true"%>
<%@ page import="com.viaagnolettisrl.hibernate.*"%>
<%@ page import="com.viaagnolettisrl.*"%>
<%@ page import="com.google.gson.*"%>
<%@ page import="java.util.Collection"%>
<%@ page import="java.util.Map"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%
	User user = (User) session.getAttribute(LoginServlet.USER);
	if (user == null) {
		response.sendRedirect(request.getContextPath()
				+ LoginServlet.LOGIN_FORM);
	}
%>
<table id="sampling-table" class="display" cellspacing="0" width="100%">
	<thead>
		<tr class="ui-widget-header ">
			<th>Cliente</th>
			<th>Commessa</th>
			<th>Macchina</th>
			<th>Durata</th>
			<th>Giorno di assegnamento</th>
		</tr>
	</thead>
	<tbody>
	</tbody>
</table><br />
<%
Gson gson = new Gson();
%>
<script>

$("#sampling-table").dataTable({
	"bJQueryUI": true,
	"bProcestrueng": true,
	"sPaginationType": "full_numbers",
	"language": {
		"url": "<%=request.getContextPath()%>/scripts/datatables/italian.js"
	},
	"data": <%=gson.toJson(GetCollection.Sampling())%>,
	"createdRow": function ( row, data, index ) {
		row.setAttribute('id', data.id);
	},
    columns: [
              { data: 'jobOrder.client.name', name: "client" },
              { data: 'jobOrder.id', name: "joborder"},
              { data: 'machine.name', name: "machine"},
              {
            	  data: 'leadTime',
            	  name: 'leadTime',
            	  render: dataTablesLeadTime
              },
              {
        	  	  data: 'start',
        	  	  name: 'start'
              }
          ]
});
</script>