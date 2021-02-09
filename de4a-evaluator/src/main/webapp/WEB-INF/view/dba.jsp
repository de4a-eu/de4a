<!DOCTYPE HTML>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>    
<head> 
    <title>DBA Form Submission</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
</head>
<body>
	<h1>Legal Entity Data</h1>
	<form:form action="greetinggo"  method="post" modelAttribute="userForm">
    <!--  <form action="greetinggo" th:action="@{/greetinggo}" th:object="${greetinggo}" method="post">--> 
        <p>Legal Entity ID (eg:NLNHR.90000471, NLOSS.70000777, NLXXX.10000111):  <form:input type="text" path="eidas"/>	</p> 
        <form:input type="hidden" path="evidenceServiceURI" value="dba" />
        <p><input type="submit" value="Submit" /> <input type="reset" value="Reset" />
        </p>
    </form:form>
</body>
</html>  