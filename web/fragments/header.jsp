<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<meta name="author" content="Paolo Galeone">
<title>Santos -<%= request.getParameter("title") %></title>
<link type="text/css"
	href="<%= request.getContextPath() %>/styles/fullcalendar/fullcalendar.css"
	rel="stylesheet" />
<link type="text/css"
	href="<%= request.getContextPath() %>/styles/jquery-ui.css"
	rel="stylesheet">
<link type="text/css"
	href="<%= request.getContextPath() %>/styles/datatables/jquery.dataTables.min.css"
	rel="stylesheet">
<link type="text/css"
	href="<%= request.getContextPath() %>/styles/default.css"
	rel="stylesheet">
<script src="<%= request.getContextPath() %>/scripts/jquery/jquery.js"></script>
<script
	src="<%= request.getContextPath() %>/scripts/jquery/jquery-ui.js"></script>
<script
	src="<%= request.getContextPath() %>/scripts/fullcalendar/lib/moment.min.js"></script>
<script
	src="<%= request.getContextPath() %>/scripts/fullcalendar/fullcalendar.js"></script>
<script
	src="<%= request.getContextPath() %>/scripts/fullcalendar/lang-all.js"></script>
<script
	src="<%= request.getContextPath() %>/scripts/datatables/jquery.dataTables.min.js"></script>
<script
	src="<%= request.getContextPath() %>/scripts/datatables/jquery.dataTables.editable.js"></script>
<script
	src="<%= request.getContextPath() %>/scripts/datatables/jquery.jeditable.js"></script>
<script src="<%= request.getContextPath() %>/scripts/datatables/jquery.validate.js"></script>
</head>
<body>