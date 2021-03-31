<!DOCTYPE HTML>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>    
<head> 
    <title>Error</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
</head>
<body>
	<h3>Error code: </h3>
	<p>${errorCode}</p>
	<hr>
	<h3>Error description:</h3>	
	<p>${errorDescription}</p>	
</body>
</html>