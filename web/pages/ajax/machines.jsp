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
<form id="formAddNewRowMachine" action="#" title="Aggiungi macchina">
	<p class="validateTips">Tutti i campi sono necessari.</p>
	<fieldset>
		<input type="hidden" name="fakeid" id="fakeid" rel="0" value="0" />
		<label
			for="name">Nome (almeno 1 carattere)</label>
		<input type="text" name="name" minlength="1" required
			id="name" value="" class="text ui-widget-content ui-corner-all" rel="1">
		<label for="type">Tipo (almeno 1 carattere)</label>
		<input type="text"
			name="type" id="type" value="" minlength="1" required
			class="text ui-widget-content ui-corner-all" rel="2">
			
		<label for="nicety">Finezza (decimale)</label>
		<input type="text"
			name="nicety" id="nicety" value="" required
			class="text ui-widget-content ui-corner-all" rel="3">
		
		<label for="color">Colore</label>
		<input type="text"
			name="color" value="" required
			class="color text ui-widget-content ui-corner-all" rel="4" readonly>
		<div class="colorpicker"></div>
	</fieldset>
</form>

<table id="machines-table" class="display" cellspacing="0" width="100%">
	<thead>
		<tr class="ui-widget-header">
			<th>ID</th>
			<th>Nome</th>
			<th>Tipo</th>
			<th>Finezza</th>
			<th>Colore</th>
		</tr>
	</thead>
	<tbody>
	</tbody>
</table>
<% String style = user.getCanAddMachine() ? "" : "display:none"; %>
<button id="btnAddNewRowMachine" style="<%=style%>">Aggiungi macchina</button>
<button id="btnDeleteRowMachine" style="<%=style%>">Cancella macchina</button>
<%Gson gson = new Gson();%>
<script>
picker = $.farbtastic("#formAddNewRowMachine .colorpicker");
picker.setColor("#FF0000");
picker.linkTo(function(color) {
	var $in = $("#formAddNewRowMachine .color");
	$in.prop('readonly', false);
	$in.attr('value',color);
	$in.css('border-color', color);
	$in.prop('readonly', true);
});
$("#machines-table").dataTable({
	"bJQueryUI": true,
	"bProcestrueng": true,
	"sPaginationType": "full_numbers",
	"language": {
		"url": "<%=request.getContextPath()%>/scripts/datatables/italian.js"
	},
	"data": <%=gson.toJson(GetList.Machines())%>,
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
              { data: 'nicety', name: 'nicety' },
              {
            	  data: 'color',
            	  name: 'color',
            	  render: dataTablesColor
			  }
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
		return confirm("Vuoi davvero rimuovere questa macchina?");
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
                  {},//nicety
                  {
                	  type: 'farbtastic',
                      submit    : 'Ok',
                      cancel    : 'Cancel',
                  } //color
              ]
});
</script>