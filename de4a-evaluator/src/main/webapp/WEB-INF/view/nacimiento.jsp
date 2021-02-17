<!DOCTYPE HTML>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>    
<head> 
    <title>BirthCertificate Form Submission</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
</head>
<body>
	<h1>Personal data</h1>	
	
	<form:form action="greetinggo"  method="post" modelAttribute="userForm">
    <!--  <form action="greetinggo" th:action="@{/greetinggo}" th:object="${greetinggo}" method="post">--> 
    	<form:label path="atuCode" >Administrative Territorial Unit: </form:label><form:select path="atuCode"> 
			<form:options items="${iaOrganisationalStructure}"/> 
		</form:select>
        <p>Eidas ID (eg:SI/ES/10000949C):  <form:input type="text" path="eidas"/>	</p>
   		<p>Name (eg: OLGA):  <form:input type="text" path="name"/>	</p>
   		<p>Surname (eg: SAN MIGUEL):  <form:input type="text" path="ap1"/>	</p>
   		<p>Surname 2(eg: CHAO):  <form:input type="text" path="ap2"/>	</p>
   		<p>BirthDate  (eg: 1940-06-03):  <form:input type="text" path="birthDate"/>	</p> 
   		<form:input path="country" type="hidden"/>
   		<form:input path="evidenceTypeId" type="hidden"/>
   		
        <p><input type="submit" value="Submit" /> <input type="reset" value="Reset" /></p>
    </form:form>
</body>
</html>

