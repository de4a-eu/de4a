<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<html>
    <head>
    </head>
    <body>
        <h3>JOB DONE REQ: </h3> <h4><%=request.getParameter("id")%><%=request.getParameter("id")%></h4>
    </body>
     <form id="form" action="viewresponse?requestId=<%=request.getParameter("id")%>" th:action="@{/download?id=<%=request.getParameter("id")%>}" th:object="${download}" method="POST">
    	 <input type="hidden" id="id" value="<%=request.getParameter("id")%>"/> 
    </form> 
    <!-- <form  id="form" action = "/download?id=< %=request.getParameter("id")% >" method = "GET">
     </form>-->
     <script>
		window.onload = function(){
		   document.forms['form'].submit();
		}
	</script> 
</html>
 