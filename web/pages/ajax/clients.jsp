<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page errorPage="../../errors/exception.jsp"%>
<%@ page session="true"%>
<%@ page import="com.viaagnolettisrl.hibernate.*"%>
<%@ page import="com.viaagnolettisrl.*"%>
<%@ page import="com.google.gson.*"%>
<%@ page import="java.util.Collection"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%
	User user = (User) session.getAttribute(LoginServlet.USER);
	if (user == null) {
		response.sendRedirect(request.getContextPath()
				+ LoginServlet.LOGIN_FORM);
	}
%>
<form id="formAddNewRowClient" action="#" title="Aggiungi cliente">
	<p class="validateTips">Tutti i campi sono necessari.</p>
	<fieldset>
		<input type="hidden" name="fakeid" id="fakeid" rel="0" value="0" /> <label
			for="name">Nome (almeno 1 carattere)</label> <input type="text"
			name="name" minlength="1" required id="name" value=""
			class="text ui-widget-content ui-corner-all" rel="1"> <label
			for="code">Codice (almeno 1 carattere)</label> <input type="text"
			name="code" id="code" value="" minlength="1" required
			class="text ui-widget-content ui-corner-all" rel="2">
			<input type="submit" value="ok" class="inner">
	</fieldset>
</form>

<table id="clients-table" class="display" cellspacing="0" width="100%">
	<thead>
		<tr class="ui-widget-header">
			<th>ID</th>
			<th>Nome</th>
			<th>Codice</th>
		</tr>
	</thead>
	<tbody>
	</tbody>
</table><br />
<% String style = user.getCanAddClient() ? "" : "display:none"; %>
<button id="btnAddNewRowClient" style="<%=style%>">Aggiungi
	cliente</button>
<button id="btnDeleteRowClient" style="<%=style%>">Cancella
	cliente</button>
<script>
<%Gson gson = new Gson();%>
$("#clients-table").dataTable({
	"bJQueryUI": true,
	"bProcestrueng": true,
	"sPaginationType": "full_numbers",
	"language": {
		"url": "<%=request.getContextPath()%>/scripts/datatables/italian.js"
	},
	"data": <%=gson.toJson(GetCollection.Clients())%>,
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
              { data: 'name', name: 'name' },
              { data: 'code', name: 'code' }
          ]
}).makeEditable({
	sDeleteURL: "<%=request.getContextPath()%>/delete?what=client",
	sUpdateURL: "<%=request.getContextPath()%>/edit?what=client",
	sAddURL: "<%=request.getContextPath()%>/add?what=client",
	sReadOnlyCellClass : "read_only",
	sAddNewRowButtonId: "btnAddNewRowClient",
	sDeleteRowButtonId: "btnDeleteRowClient",
	sAddNewRowFormId: "formAddNewRowClient",
	fnOnDeleting: function() {
		return confirm("Vuoi davvero rimuovere questo cliente?\n" + 
			"Cancellandolo eliminerai tutte le sue commesse (e quindi tutte le ore di lavoro associate alle macchine)");
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
                  {},//name
                  {}//code
              ]
});
</script>