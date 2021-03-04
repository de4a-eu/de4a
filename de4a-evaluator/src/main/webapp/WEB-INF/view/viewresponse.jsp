<!DOCTYPE HTML>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<head>
<title>Response Transfer Evidence</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
</head>
<body>
	<h1>Owner response</h1>
	
	<%String isResponseReady = (String) request.getAttribute("isResponseReady");%>
	
	<% if("true".equals(isResponseReady)) {%>
		<form:form modelAttribute="userForm">
			<p>XML Canonical:</p>
			<form:textarea path="response" rows="20" cols="200" />
			<p>XML National:</p>
			<form:textarea path="nationalResponse" rows="20" cols="200" />
		</form:form>
	<%} else { %>
		<p>Request still processing... Wait for a bit</p>
		<form id="checkResponseForm" action="viewresponse">
			<input type="hidden" name="requestId" value="${requestId}"/>
			<input type="submit" value="Check again"/>			
		</form>
	<%}%>
</body>
</html>
