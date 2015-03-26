<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page errorPage="../errors/exception.jsp"%>
<%@ page session="true"%>
<%@ page import="java.util.*"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd"> 
<html>
<head>
	<meta name="author" content="Paolo Galeone">
   	<title>Sample JSP page</title>
    <link type="text/css" href="<%= request.getContextPath() %>/styles/default.css" rel="stylesheet">
</head>

<body>
	<%@ include file="../fragments/header.jsp" %>
	<%@ include file="../fragments/menu.jsp" %>
	<%@ include file="../fragments/footer.jsp" %>
</body>
</html>