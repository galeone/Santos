<%@ page session="true"%>
<%@ page import="com.viaagnolettisrl.hibernate.*"%>

<script>
	$(function() {
	    var dialog, form,
	      user_username = $( "#user_username" ),
	      user_name = $( "#user_name" ),
	      user_surname = $( "#user_surname" ),
	      user_password = $( "#user_password" ),
	      
	      allFields = $( [] ).add( user_username ).add( user_name ).add( user_surname ).add( user_password ),
	      tips = $( "#add-user-dialog-form .validateTips" );

		function updateTips(t) {
			tips.text(t).addClass("ui-state-highlight");
			setTimeout(function() {
				tips.removeClass("ui-state-highlight", 1500);
			}, 500);
		}

		function checkLength(o, n, min, max) {
			if (o.val().length > max || o.val().length < min) {
				o.addClass("ui-state-error");
				updateTips("La lunghezza del campo " + n + " dev'essere tra " + min
						+ " e " + max + ".");
				return false;
			} else {
				return true;
			}
		}

		function checkRegexp(o, regexp, n) {
			if (!(regexp.test(o.val()))) {
				o.addClass("ui-state-error");
				updateTips(n);
				return false;
			} else {
				return true;
			}
		}

		function addUser() {
			var valid = true;
			allFields.removeClass("ui-state-error");

			valid = valid && checkLength(user_username, "Nome utente", 3, 16);
			valid = valid && checkLength(user_password, "Password", 5, 16);
			valid = valid && checkLength(user_name, "Nome", 2, 30);
			valid = valid && checkLength(user_surname, "Cognome", 2, 30);

			valid = valid
					&& checkRegexp(
							user_username,
							/^[a-z]([0-9a-z_\s])+$/i,
							"Il nome utente può contenere lettere, numeri, _ (underscore) e deve iniziare con una lettera.");

			valid = valid
					&& checkRegexp(user_password, /^([0-9a-zA-Z])+$/,
							"La password può contenere solo numeri e lettere : a-z 0-9");

			if (valid) {
				$("#users-table tbody").append(
						"<tr>" + "<td>" + user_username.val() + "</td>" + "<td>"
								+ user_name.val() + "</td>" + "<td>"
								+ user_surname.val() + "</td>" + "<td>"
								+ user_password.val() + "</td>" + "</tr>");
				dialog.dialog("close");
			}
			return valid;
		}

		dialog = $("#add-user-dialog-form").dialog({
			autoOpen : false,
			//height : 300,
			//width : 350,
			modal : true,
			buttons : {
				"Aggiungi utente" : addUser,
				Cancel : function() {
					dialog.dialog("close");
				}
			},
			chiudi : function() {
				form[0].reset();
				allFields.removeClass("ui-state-error");
			}
		});

		form = dialog.find("form").on("submit", function(event) {
			event.preventDefault();
			addUser();
		});

		$("#create-user").button().on("click", function() {
			dialog.dialog("open");
		});
	});
</script>

<div id="add-user-dialog-form" title="Crea un nuovo utente">
	<p class="validateTips">Tutti i campi sono necessari.</p>

	<form>
		<fieldset>
			<label for="user_username">Nome utente</label> <input type="text"
				name="user_username" id="user_username" value=""
				class="text ui-widget-content ui-corner-all"> <label
				for="user_name">Nome</label> <input type="text" name="user_name"
				id="user_name" value="" class="text ui-widget-content ui-corner-all">
			<label for="user_surname">Cognome</label> <input type="text"
				name="user_surname" id="user_surname" value=""
				class="text ui-widget-content ui-corner-all"> <label
				for="user_password">Password</label> <input type="password"
				name="user_password" id="user_password" value=""
				class="text ui-widget-content ui-corner-all">

			<!-- Allow form submission with keyboard without duplicating the dialog button -->
			<input type="submit" tabindex="-1"
				style="position: absolute; top: -1000px">
		</fieldset>
	</form>
</div>


<div class="contain ui-widget">
	<h1>Utenti esistenti:</h1>
	<table id="users-table" class="ui-widget ui-widget-content">
		<thead>
			<tr class="ui-widget-header ">
				<th>Nome utente</th>
				<th>Nome</th>
				<th>Cognome</th>
				<th>Password</th>
			</tr>
		</thead>
		<tbody>
			<tr>
				<td>John Doe</td>
				<td>John</td>
				<td>Doe</td>
				<td>johndoe1</td>
			</tr>
		</tbody>
	</table>
</div>
<button id="create-user">Crea un nuovo utente</button>
<script>
$("#users-table").dataTable();
</script>