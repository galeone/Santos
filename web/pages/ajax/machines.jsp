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
<form id="formAddNewRowMachine" action="#" title="Aggiungi macchina">
	<p class="validateTips">Tutti i campi sono necessari.</p>
	<fieldset>
		<input type="hidden" name="fakeid" id="fakeid" rel="0" value="0" /> <label
			for="name">Nome (almeno 1 carattere)</label> <input type="text"
			name="name" minlength="1" required id="name" value=""
			class="text ui-widget-content ui-corner-all" rel="1"> <label
			for="type">Tipo (almeno 1 carattere)</label> <input type="text"
			name="type" id="type" value="" minlength="1" required
			class="text ui-widget-content ui-corner-all" rel="2"> <label
			for="nicety">Finezza (decimale)</label> <input type="text"
			name="nicety" id="nicety" value="" required
			class="text ui-widget-content ui-corner-all" rel="3">
			<input type="submit" value="ok" class="inner">
	</fieldset>
</form>

<table id="machines-table" class="display" cellspacing="0" width="100%">
	<thead>
		<tr class="ui-widget-header">
			<th>ID</th>
			<th>Nome</th>
			<th>Tipo</th>
			<th>Finezza</th>
		</tr>
	</thead>
	<tbody>
	</tbody>
</table><br />
<button id="btnAddNewRowMachine" style="<%=user.getCanAddMachine() ? "" : "display:none"%>">Aggiungi
	macchina</button>
<button id="btnDeleteRowMachine" style="<%=user.getIsAdmin() ? "" : "display:none"%>">Cancella
	macchina</button>
<%Gson gson = new Gson();%>
<script>
$("#machines-table").dataTable({
	"bJQueryUI": true,
	"bProcestrueng": true,
	"sPaginationType": "full_numbers",
	"language": {
		"url": "<%=request.getContextPath()%>/scripts/datatables/italian.js"
	},
	"data": <%=gson.toJson(GetCollection.machines())%>,
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
              { data: 'type', name: 'type' },
              { data: 'nicety', name: 'nicety' }
          ]
}).makeEditable({
	sDeleteURL: "<%=request.getContextPath()%>/delete?what=machine",
	sUpdateURL: "<%=request.getContextPath()%>/edit?what=machine",
	sAddURL: "<%=request.getContextPath()%>/add?what=machine",
	sReadOnlyCellClass : "read_only",
	sAddNewRowButtonId: "btnAddNewRowMachine",
	sDeleteRowButtonId: "btnDeleteRowMachine",
	sAddNewRowFormId: "formAddNewRowMachine",
	fnOnDeleting: function() {
		return confirm("Vuoi davvero rimuovere questa macchina?\n" +
			"Cancellandola cancellerai tutte le assegnazioni delle commesse a questa macchina.");
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
                  {},//type
                  {}//nicety
              ]
});
</script>