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
	<h3>Request ID: <%= request.getParameter("idRequest")%></h3>

	<%String isEvidenceReady = (String) request.getAttribute("isEvidenceReady");%>
	<% if("true".equals(isEvidenceReady)) {%>
		<p>Request is ready!</p>
		<p class="warn"> Here could be some additional input parameters mandatories to retrieve National evidence</p>		
	    <form:form action="viewResponse" method="post"
			modelAttribute="previewRequest">
			<p>
				<input type="submit" value="Authenticate and preview" />
			</p>
			<form:input type="hidden" path="idRequest" value="${param.idRequest}" />
			<form:input type="hidden" path="returnUrl" value="${param.returnUrl}" />
		</form:form>
	<%} else { %>
		<p>Request still processing... Wait for a bit</p>
	<form:form action="preview" method="post"
		modelAttribute="previewRequest">
		<p>
			<input type="submit" value="Authenticate and preview" />
		</p>
		<form:input type="hidden" path="idRequest" value="${param.idRequest}" />
		<form:input type="hidden" path="returnUrl" value="${param.returnUrl}" />
	</form:form>
	<%}%>

</body>
</html>
