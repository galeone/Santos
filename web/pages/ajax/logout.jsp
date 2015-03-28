<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page errorPage="../errors/exception.jsp"%>
<%@ page session="true"%>
<%@ page import="com.viaagnolettisrl.LoginServlet"%>
<%@ page import="com.viaagnolettisrl.hibernate.User"%>
<%
	User user = (User)session.getAttribute(LoginServlet.USER);
	if( user != null ) {
		session.invalidate();
	}
	response.sendRedirect(request.getContextPath() + LoginServlet.LOGIN_FORM);
%>