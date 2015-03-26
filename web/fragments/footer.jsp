<footer style="position:absolute; bottom:1px; font-size:9px">Santos &copy; 2014.</footer>
<script>
$("#error").dialog({
	autoOpen: true,
	title: "Errore",
	modal: true,
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