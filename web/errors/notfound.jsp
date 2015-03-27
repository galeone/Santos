<%@ page isErrorPage="true"%>
<jsp:include page="../fragments/header.jsp">
	<jsp:param name="title" value="404 - Risorsa non trovata" />
</jsp:include>
<div id="error" data-next="window.history.go(-1)">
	<p>Risorsa non trovata</p>
</div>
<%@ include file="../fragments/footer.jsp"%>
