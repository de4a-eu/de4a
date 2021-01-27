<!DOCTYPE HTML>
<html xmlns:th="https://www.thymeleaf.org">
<head> 
    <title>Getting Started: Handling Form Submission</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
</head>
<body>
	<h1>Result del requestor</h1> 
	<form id="form"  action="goevaluator" th:action="@{/goevaluator}"   method="post"> 
    </form>
    <script>
    	window.onload = function(){
    	   document.forms['form'].submit();
    	}
    </script>
    
</body>
</html> 