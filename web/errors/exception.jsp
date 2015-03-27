<%@ page isErrorPage="true" import="java.io.*"%>
<jsp:include page="../fragments/header.jsp">
	<jsp:param value="title" name="Eccezione" />
</jsp:include>
<div class="error">
	<%
StringWriter stringWriter = new StringWriter();
PrintWriter printWriter = new PrintWriter(stringWriter);
exception.printStackTrace(printWriter);
out.println(stringWriter);
printWriter.close();
stringWriter.close();
%>
</div>
<%@ include file="../fragments/footer.jsp"%>
