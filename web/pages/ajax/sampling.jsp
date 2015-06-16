<%@page import="java.util.HashMap"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page errorPage="../../errors/exception.jsp"%>
<%@ page session="true"%>
<%@ page import="it.galeone_dev.santos.*"%>
<%@ page import="it.galeone_dev.santos.servlet.*"%>
<%@ page import="it.galeone_dev.santos.hibernate.models.*"%>
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
<table id="sampling-table" class="display" width="100%">
	<thead>
		<tr class="ui-widget-header ">
			<th>Cliente</th>
			<th>Descrizione</th>
			<th>Macchina</th>
			<th>Durata</th>
			<th>Giorno di assegnamento</th>
		</tr>
	</thead>
	<tbody>
	</tbody>
	<tfoot>
		<tr>
			<td></td>
			<td></td>
			<td></td>
			<td></td>
			<td></td>
		</tr>
	</tfoot>
</table>
<br />
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
	"data": <%=gson.toJson(GetCollection.setSamplingAttr(GetCollection.sampling(),false))%>,
	"createdRow": function ( row, data, index ) {
		row.setAttribute('id', data.id);
	},
    columns: [
              { data: 'client.name', name: "client" },
              { data: 'description', name: "description"},
              { data: 'machine.name', name: "machine"},
              {
            	  data: 'leadTime',
            	  name: 'leadTime',
            	  render: dataTablesLeadTime
              },
              {
        	  	  data: 'dateTime',
        	  	  name: 'dateTime'
              }
          ],
     drawCallback: function () {
         var api = this.api();
         var hours = api.column( 3, {page:'current'} ).data().sum() / 60;
         $(api.table().footer()).find("tr td:eq(3)").html('<b>' + hours + '</b>');
       }

});
</script>