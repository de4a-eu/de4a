<!DOCTYPE HTML>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>    
<head> 
    <title>Response Transfer Evidence</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
</head>
<body>
	<h1>Respuesta del owner</h1>
	<form:form action="greetinggo"  method="post" modelAttribute="userForm">
    <!--  <form action="greetinggo" th:action="@{/greetinggo}" th:object="${greetinggo}" method="post">-->
    	   <p>XML:  <form:textarea   path="response" />	</p>  
    </form:form>
</body>
</html>
