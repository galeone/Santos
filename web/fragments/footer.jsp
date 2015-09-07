<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<jsp:useBean id="date" class="java.util.Date" />
<footer style="position: fixed; bottom: 1px; font-size: 9px">Santos
	&copy; <fmt:formatDate value="${date}" pattern="yyyy" />.</footer>
<script>
$("#error").dialog({
	autoOpen: true,
	title: "Errore",
	modal: true,
	closeOnEscape: false,
	buttons: {
		"ok": function() {
			if($(this).data('next')) {
				eval($(this).data('next'));
			} else {
				$(this).dialog("close");
			}
		}
	}
});

$.datepicker.setDefaults($.datepicker.regional['it']);
</script>
</body>
</html>