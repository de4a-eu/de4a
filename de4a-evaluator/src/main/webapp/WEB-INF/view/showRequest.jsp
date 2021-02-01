<!DOCTYPE HTML>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>    
<head> 
    <title>Ole response</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
</head>
<body>
	<h1>Request to Requestor</h1>
	<form:form action="requestEvidence"  method="post" modelAttribute="userForm">
			<p><input type="submit" value="Submit" /> </p> 
    	   <p>XML Request:  <form:textarea   path="request" rows="50" cols="200" />	</p>   
    	   
    </form:form> 
</body>
</html>
