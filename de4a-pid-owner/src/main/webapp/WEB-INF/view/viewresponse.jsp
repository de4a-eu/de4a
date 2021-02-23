<!DOCTYPE HTML>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>    
<head> 
    <title>Previsualización evidencia</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
</head>
<body>
	<h1>Evidencia recuperada a ser reenviada al Transferor</h1>
	<form:form action="greetinggo"  method="post" modelAttribute="userForm"> 
    	   <p>XML Canonical:  <form:textarea   path="response"  rows="20" cols="200"/>	</p>    
    </form:form>
</body>
</html>
