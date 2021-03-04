<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<html>
    <head>
    	<title>Evaluator Redirection - USI Pattern</title>
    	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    </head>
    <body>
    	<form action="${returnUrl}" method="post" id="previewResponse">
        <h3>REQUEST #${requestId} DONE</h3>
        <br>
    	<h2>5 seconds to redirect to evaluator:</h2>
    	<h3>${returnUrl}</h3>
    	<progress value="0" max="5" id="progressBar"></progress>    	
	    	 <input type="hidden" name="requestId" value="${requestId}"/>
    	</form>
    </body>
     <script>
		window.onload = function(){		   
		   var timeleft = 10;
		   var downloadTimer = setInterval(function(){
		     if(timeleft <= 0){
		       clearInterval(downloadTimer);
		       document.getElementById("previewResponse").submit();
		     }
		     document.getElementById("progressBar").value = 10 - timeleft;
		     timeleft -= 1;
		   }, 500);
		}
	</script> 
</html>