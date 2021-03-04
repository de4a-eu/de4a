<!DOCTYPE HTML>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<head>
<title>Evidence preview</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
</head>
<body>
	<h1>National Evidence to be forwarded</h1>
	<p>Req. ID: ${requestId}</p>
	<p>XML National Evidence:</p>
	<textarea rows="20" cols="200">${responseFormat}</textarea>
	<p> Here could be some additional input parameters to complete evidence </p>
	<form:form action="goResponse" modelAttribute="previewResponse">
		<input type="submit" value="Ok. Continue"></input>
		<form:input type="hidden" path="requestId" value="${requestId}" />
		<form:input type="hidden" path="returnUrl" value="${returnUrl}" />
	</form:form>
</body>
</html>
