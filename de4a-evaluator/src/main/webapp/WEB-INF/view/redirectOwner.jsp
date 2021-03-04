<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<html>
    <head>
    	<title>Owner Redirection - USI Pattern</title>
    	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    </head>
    <body>
    	<form:form action="${redirectUrl}" method="post" modelAttribute="previewRequest">
        <h3>REQUEST #${idRequest} DONE</h3>
        <br>
    	<h2>5 seconds to redirect to owner:</h2>
    	<h3>${redirectUrl}</h3>
    	<progress value="0" max="5" id="progressBar"></progress>    	
	    	 <form:input type="hidden" path="idRequest"/>  
	    	 <form:input type="hidden" path="returnUrl"/>
    	</form:form>
    </body>
     <script>
		window.onload = function(){		   
		   var timeleft = 10;
		   var downloadTimer = setInterval(function(){
		     if(timeleft <= 0){
		       clearInterval(downloadTimer);
		       document.getElementById("previewRequest").submit();
		     }
		     document.getElementById("progressBar").value = 10 - timeleft;
		     timeleft -= 1;
		   }, 500);
		}
	</script> 
</html>