<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page errorPage="../errors/exception.jsp"%>
<%@ page session="true"%>
<%@ page import="it.galeone_dev.servlet.LoginServlet"%>
<%@ page import="it.galeone_dev.hibernate.models.User"%>
<%
	User user = (User)session.getAttribute(LoginServlet.USER);
	if( user != null ) {
		session.invalidate();
	}
	response.sendRedirect(request.getContextPath() + LoginServlet.LOGIN_FORM);
%>