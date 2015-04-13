<footer style="position: fixed; bottom: 1px; font-size: 9px">Santos
	&copy; 2015.</footer>
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
</script>
</body>
</html>