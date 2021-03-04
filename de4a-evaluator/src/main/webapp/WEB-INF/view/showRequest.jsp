<!DOCTYPE HTML>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<head>
<title>Request Transfer Evidence</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
</head>
<body>
	<h1>Request to Requestor</h1>
	<form:form action="requestEvidence" method="post"
		modelAttribute="userForm">
		<p>XML Request:</p>
		<form:textarea path="request" rows="25" cols="200" />
		<p>
			<input type="submit" value="Submit >" />
		</p>
	</form:form>
</body>
</html>
