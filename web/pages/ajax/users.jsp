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
<form id="formAddNewRow" action="#" title="Crea un nuovo utente">
	<p class="validateTips">Tutti i campi sono necessari.</p>
	<fieldset>
		<input type="hidden" name="fakeid" id="fakeid" rel="0" value="0" />
		<label for="username">Nome utente (almeno 2 caratteri)</label>
		<input type="text"
			name="username" id="username" value="" minlength="2" required
			class="text ui-widget-content ui-corner-all" rel="1">
		<label
			for="name">Nome (almeno 2 caratteri)</label>
		<input type="text" name="name" minlength="2" required
			id="name" value="" class="text ui-widget-content ui-corner-all" rel="2">
		<label for="surname">Cognome (almeno 2 caratteri)</label>
		<input type="text"
			name="surname" id="surname" value="" minlength="2" required
			class="text ui-widget-content ui-corner-all" rel="3">
		<label
			for="password">Password (almeno 5 caratteri)</label>
		<input type="password"
			name="password" id="password" value="" minlength="5" required
			class="text ui-widget-content ui-corner-all" rel="4">
			
		<label for="canaddjoborder">Può aggiungere commesse</label>
		<input type="checkbox" name="canaddjoborder" id="canaddjoborder" value="Si"
			class="text ui-widget-content ui-corner-all" rel="5">
			
		<label for="canaddclient">Può aggiungere macchine</label>
		<input type="checkbox" name="canaddmachine" id="canaddmachine" value="Si"
			class="text ui-widget-content ui-corner-all" rel="6">
			
		<label for="canaddclient">Può aggiungere clienti</label>
		<input type="checkbox" name="canaddclient" id="canaddclient" value="Si"
			class="text ui-widget-content ui-corner-all" rel="7">
	</fieldset>
</form>

<table id="users-table" class="display" cellspacing="0" width="100%">
	<thead>
		<tr class="ui-widget-header ">
			<th>ID</th>
			<th>Nome utente</th>
			<th>Nome</th>
			<th>Cognome</th>
			<th>Password</th>
			<th>Aggiunge Commesse</th>
			<th>Aggiunge Macchine</th>
			<th>Aggiunge Clienti</th>
		</tr>
	</thead>
	<tbody>
	</tbody>
</table>

<button id="btnAddNewRow">Crea un nuovo utente</button>
<button id="btnDeleteRow">Cancella utente</button>
<script>
<%Gson gson = new Gson();%>
var users = <%=gson.toJson(GetList.Users())%>;
$("#users-table").dataTable({
	"bJQueryUI": true,
	"bProcestrueng": true,
	"sPaginationType": "full_numbers",
	"language": {
		"url": "<%=request.getContextPath()%>/scripts/datatables/italian.js"
	},
	"data": users,
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
              { data: 'username', name: "username" },
              { data: 'name', name: 'name' },
              { data: 'surname', name: 'surname' },
              { data: 'password', name: 'password' },
              {
            	  data: 'canAddJobOrder',
            	  name: 'canaddjoborder',
            	  render: dataTablesCheckbox
			  },
              {
				  data: 'canAddMachine',
				  name: 'canaddmachine',
				  render: dataTablesCheckbox
		      },
              {
		    	  data: 'canAddClient',
		    	  name: 'canaddclient',
		    	  render: dataTablesCheckbox
		    }
          ]
}).makeEditable({
	sDeleteURL: "<%=request.getContextPath()%>/delete?what=user",
	sUpdateURL: "<%=request.getContextPath()%>/edit?what=user",
	sAddURL: "<%=request.getContextPath()%>/add?what=user",
	sReadOnlyCellClass : "read_only",
	fnOnDeleting: function() {
		return confirm("Vuoi davvero rimuovere questo utente?");
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
                  {},
                  {},
                  {},
                  {
                	  type: 'checkbox',
                      submit    : 'Ok',
                      cancel    : 'Cancel',
                      checkbox: { trueValue: 'Si', falseValue: 'No' }
                  },
                  {
                	  type: 'checkbox',
                      submit    : 'Ok',
                      cancel    : 'Cancel',
                      checkbox: { trueValue: 'Si', falseValue: 'No' }
    		      },
                  {
                	  type: 'checkbox',
                      submit    : 'Ok',
                      cancel    : 'Cancel',
                      checkbox: { trueValue: 'Si', falseValue: 'No' }
    		      }
              ]
});
</script>