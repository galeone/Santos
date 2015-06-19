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
<form id="formAddNewRowJobOrder" action="#" title="Aggiunti commessa">
	<p class="validateTips">Tutti i campi sono necessari.</p>
	<fieldset>
		<input type="hidden" name="fakeid" id="fakeid" rel="0" value="0" /> <label
			for="client">Cliente</label> <select name="client" id="client"
			required rel="1">
			<%
			application.setAttribute("clients", GetCollection.clients());
			%>
			<c:forEach var="client" items="${clients}">
				<option value="${client.id}">${client.name}</option>
			</c:forEach>
		</select> <label for="description">Descrizione</label> <input type="text"
			required id="description" name="description" rel="2" value="" /> <label>Tempo
			per capo:</label> <label for="giorni">Ore</label> <input type="number"
			name="ore" id="ore" /> <label for="minuti">Minuti</label> <input
			type="number" name="minuti" id="minuti" /> <input type="hidden"
			name="timeforitem" id="timeforitem" rel="3" value="0" />
		<!-- tempo in gg e ore -->
		<label for="numberofitems">Numero di capi</label> <input type="number"
			name="numberofitems" id="numberofitems" rel="4" /> <input
			type="hidden" name="leadtime" id="leadtime" rel="5" value="0" /> <input
			type="hidden" name="missingtimewithoffset" id="missingtimewithoffset" rel="6" value="0" />
		<input type="hidden" name="offset" id="offset" rel="7" value="0" /> <label
			for="color">Colore</label> <input type="text" name="color" value=""
			required class="color text ui-widget-content ui-corner-all" rel="8"
			readonly> <input type="submit" value="ok" class="inner">
		<div class="colorpicker"></div>
	</fieldset>
</form>

<table id="joborders-table" class="display" cellspacing="0" width="100%">
	<thead>
		<tr class="ui-widget-header ">
			<th>ID</th>
			<th>Cliente</th>
			<th>Descrizione</th>
			<th>Tempo per capo capo</th>
			<th>Numero di capi</th>
			<th>Tempo totale di produzione</th>
			<th>Ore non assegnate</th>
			<th>Variazione</th>
			<th>Colore</th>
		</tr>
	</thead>
	<tbody>
	</tbody>
</table>
<br />
<% String style = user.getCanAddJobOrder() ? "" : "display:none"; %>
<button id="btnAddNewRowJobOrder" style="<%=style%>">Aggiungi
	commessa</button>
<button id="btnDeleteRowJobOrder" style="<%=style%>">Cancella
	commessa</button>
<%
Gson gson = new Gson();
Collection<Client> clients = (Collection<Client>) application.getAttribute("clients");
Map<Long, String> mapClient = new HashMap<Long, String>();
for(Client c : clients) {
	   mapClient.put(c.getId(), c.getName());
}
%>
<script>
picker = $.farbtastic("#formAddNewRowJobOrder .colorpicker");
picker.setColor("#FF0000");
picker.linkTo(function(color) {
	var $in = $("#formAddNewRowJobOrder .color");
	$in.prop('readonly', false);
	$in.attr('value',color);
	$in.css('border-color', color);
	$in.prop('readonly', true);
});

$("#ore").on('input mousewheel', function() {
	var minutes = parseInt($("#minuti").val()),
		hours =  parseInt($(this).val());
	
	minutes  = isNaN(minutes) ? 0 : minutes;
	hours 	 = isNaN(hours)   ? 0 : hours;
	$("#timeforitem").val(hours * 60 + minutes);
});

$("#minuti").on('input mousewheel', function() {
	var hours  	= parseInt($("#ore").val()),
		minutes =  parseInt($(this).val());

	minutes  = isNaN(minutes) ? 0 : minutes;
	hours 	 = isNaN(hours)   ? 0 : hours;
	$("#timeforitem").val(hours * 60 + minutes);
});

$("#numberofitems").on('input mousewheel', function() {
    $("#leadtime").val($("#timeforitem").val() * parseInt($(this).val()));
	$("#missingtimewithoffset").val($("#leadtime").val());
});

$("#joborders-table").dataTable({
	"bJQueryUI": true,
	"bProcestrueng": true,
	"sPaginationType": "full_numbers",
	"order": [[ 0, "desc" ]],
	"language": {
		"url": "<%=request.getContextPath()%>/scripts/datatables/italian.js"
	},
	"data": <%=gson.toJson(GetCollection.jobOrders())%>,
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
              { data: 'description', name: "description" },
              { data: 'timeForItem', name: "timeForItem", render: dataTablesLeadTime },
              { data: 'numberOfItems', name: "numberOfItems" },
              {
            	  data: 'leadTime',
            	  name: 'leadTime',
            	  render: dataTablesLeadTime,
            	  createdCell: function (td, cellData, rowData, row, col) {
  					    td.setAttribute('class', 'read_only');
  	  		      }
              },
              {
        	  	  data: 'missingTimeWithOffset',
        	  	  name: 'missingTimeWithOffset',
        	  	  render: dataTablesLeadTime,
                  createdCell: function (td, cellData, rowData, row, col) {
                      td.setAttribute('class', 'read_only');
          	  	  }
              },
              { data: 'offset', name: "offset", render: dataTablesOffset },
              {
            	  data: 'color',
            	  name: 'color',
            	  render: dataTablesColor
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
                  {},//id
                  {
                	  type: 'select',
                	  submit: 'Ok',
                	  cancel: 'Cancel',
                	  data: '<%=gson.toJson(mapClient) %>'
                  }, // client
                  {}, //description
                  {
            	      type: 'leadtime',
                      submit    : 'Ok',
                      cancel    : 'Cancel',
                  },
                  {}, //numberOfItems
                  {
            	      type: 'leadtime',
                      submit    : 'Ok',
                      cancel    : 'Cancel',
                  },
                  {}, //missing
                  {
            	      type: 'leadtime',
                      submit    : 'Ok',
                      cancel    : 'Cancel',
                  },
                  {
            	  	type: 'farbtastic',
                  	submit    : 'Ok',
                  	cancel    : 'Cancel',
              	  } //color
              ]
});
</script>