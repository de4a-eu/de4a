<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<html>
<head>
</head>
<body>
	<h3>JOB DONE! REQ:</h3>
	<h4>${idRequest}</h4>
</body>
<form id="form" action="viewresponse?requestId=${idRequest}"
	method="POST">
	<input type="hidden" id="id" value="${idRequest}" />
</form>
<!-- <form  id="form" action = "/download?id=< %=request.getParameter("id")% >" method = "GET">
     </form>-->
<script>
	window.onload = function() {
		document.forms['form'].submit();
	}
</script>
</html>
