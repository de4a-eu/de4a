<!DOCTYPE HTML>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<head>
<title>DE4A Pseudo Evaluator</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
</head>
<body>
	<h1>Carpeta Ciudadana Europea</h1>
	<form:form action="goEvidenceForm" method="post"
		modelAttribute="evidenceForm">
		<form:select path="canonicalEvidenceTypeId">
			<form:option value="BirthCertificate">Birth Certificate</form:option>
			<form:option value="DoingBusinessAbroad">Doing Business Abroad</form:option>
		</form:select>
		<form:select path="countryCode">
			<form:option value="ES">ES</form:option>
		</form:select>
		<p>
			<input type="submit" value="Submit" /> <input type="reset"
				value="Reset" />
		</p>
	</form:form>
</body>
</html>
