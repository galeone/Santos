<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<meta name="author" content="Paolo Galeone">
<title><%= request.getParameter("title") %> - Santos</title>
<link type="text/css"
	href="<%= request.getContextPath() %>/styles/fullcalendar/fullcalendar.css"
	rel="stylesheet" />
<link type="text/css"
	href="<%= request.getContextPath() %>/styles/jquery-ui.css"
	rel="stylesheet">
<link type="text/css"
	href="<%= request.getContextPath() %>/styles/datatables/dataTables.jqueryui.css"
	rel="stylesheet">
<link type="text/css"
	href="<%= request.getContextPath() %>/styles/theme.css"
	rel="stylesheet">
<link type="text/css"
	href="<%= request.getContextPath() %>/styles/farbtastic/farbtastic.css"
	rel="stylesheet">
<link type="text/css"
	href="<%= request.getContextPath() %>/styles/default.css"
	rel="stylesheet">
<link rel="shortcut icon"
	href="<%= request.getContextPath() %>/styles/favicon.ico" />
<script src="<%= request.getContextPath() %>/scripts/jquery/jquery.js"></script>
<script
	src="<%= request.getContextPath() %>/scripts/jquery/jquery-ui.js"></script>
<!-- <script
	src="<%= request.getContextPath() %>/scripts/jquery/jquery.hoverintent.js"></script> -->
<script
	src="<%= request.getContextPath() %>/scripts/fullcalendar/lib/moment.min.js"></script>
<script
	src="<%= request.getContextPath() %>/scripts/fullcalendar/fullcalendar.js"></script>
<script
	src="<%= request.getContextPath() %>/scripts/fullcalendar/lang-all.js"></script>
<script
	src="<%= request.getContextPath() %>/scripts/datatables/jquery.dataTables.min.js"></script>
<script
	src="<%= request.getContextPath() %>/scripts/datatables/jquery.jeditable.js"></script>
<script
	src="<%= request.getContextPath() %>/scripts/datatables/jquery.jeditable.checkbox.js"></script>
<script
	src="<%= request.getContextPath() %>/scripts/datatables/dataTables.jqueryui.js"></script>
<script
	src="<%= request.getContextPath() %>/scripts/datatables/jquery.validate.js"></script>
<script
	src="<%= request.getContextPath() %>/scripts/datatables/validate/it.js"></script>
<script
	src="<%= request.getContextPath() %>/scripts/datatables/jquery.dataTables.editable.js"></script>
<script
	src="<%= request.getContextPath() %>/scripts/farbtastic/farbtastic.js"></script>
<script
	src="<%= request.getContextPath() %>/scripts/datatables/jquery.jeditable.farbtastic.js"></script>
<script src="<%= request.getContextPath() %>/scripts/common.js"></script>
</head>

<body onselectstart="return false">