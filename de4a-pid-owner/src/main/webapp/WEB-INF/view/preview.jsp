<!DOCTYPE HTML>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>    
<head> 
    <title>European PID Owner</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
</head>
<body>
	<h1>Welcome to European PID Owner</h1>
	<form:form action="preview?id=<%=request.getParameter("id")%>"  method="post" modelAttribute="evidenceForm">
   			<p>  <input type="submit" value="Authenticate and preview" />  </p> 
	    	 <input type="hidden" id="id" value="<%=request.getParameter("id")%>"/>  
	    	 <input type="hidden" id="urlreturn" value="<%=request.getParameter("urlreturn")%>"/>  
    </form:form>
</body>
</html>
