<%@ taglib uri = "http://java.sun.com/jsp/jstl/core" prefix = "c" %>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/functions" prefix = "fn" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<spring:eval expression="@environment.getProperty('ssl.context.enabled') == 'true'" var="sslContextEnabled" />
<spring:eval expression="@environment.getProperty('http.proxy.enabled') == 'true'" var="httpProxyEnabled" />
<spring:eval expression="@environment.getProperty('de4a.kafka.enabled') == 'true'" var="kafkaEnabled" />
<spring:eval expression="@environment.getProperty('de4a.kafka.http.enabled') == 'true'" var="kafkaHttpEnabled" />
<spring:eval expression="@environment.getProperty('de4a.kafka.logging.enabled') == 'true'" var="kafkaLogginEnabled" />
<spring:eval expression="@environment.getProperty('de4a.kafka.topic')" var="kafkaTopic" />
<spring:eval expression="@environment.getProperty('de4a.kafka.url')" var="kafkaUrl" />
<spring:eval expression="@environment.getProperty('smp.endpoint')" var="smpEndpoint" />
<spring:eval expression="@environment.getProperty('idk.endpoint')" var="idkEndpoint" />
<spring:eval expression="@environment.getProperty('smpclient.truststore.path')" var="smpTruststore" />
<spring:eval expression="@environment.getProperty('phase4.keystore.path')" var="phase4Keystore" />
<spring:eval expression="@environment.getProperty('phase4.truststore.path')" var="phase4Truststore" />
<spring:eval expression="@environment.getProperty('as4.gateway.implementation.bean')" var="as4GatewayBean" />


<!DOCTYPE HTML>
<html xmlns:th="https://www.thymeleaf.org" lang="en">
<head>
    <title>DE4A-Connector - Info</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <style type="text/css">
        body {
            font-family: 'Helvetica';
            color: #484848;
        }
        br {
            content: "";
            height: 5px;
            display: block;
        }
        h4 {
            margin: 15px auto;
        }
        .h1-title {
            color: #21739a;
		    display: inline-block;
		    border: 1px solid #ececec;
		    padding-right: 7px;
		    border-radius: 8px;
		    background: #f7f7f7;
		    margin-bottom: 0px;
		    color: #21739a;
        }
        .de4a {
            background: #ffd89121;
		    padding: 0px 5px;
		    border-radius: 5px;
		    border: 1px solid #ffd891;
		    color: #2483bf;
        }
        .param-value {
            font-weight: normal;		    
		    font-family: 'Consolas';
		    background: #eceaea;
		    padding: 2px 2px;
		    border: 1px solid #ccc;
		    font-size: 14px;
		    border-radius: 2px;
        }
        .value-true {
            color: #5aa20b;
        }
        .value-false {
            color: #e04747;
        }
        .value {
            color: #484848;
        }
        .param-name {
            font-family: 'Consolas';
            color: #6b6b6b;
            font-size: 13px;
        }
    </style>
</head>
<body>
    <h1 class="h1-title"><span class="de4a">DE4A</span> Connector</h1>
    <h3>Status: <span class="param-value value-true">Running</span></h3>
    <br>
    <h3 style="margin-bottom: 5px;">System Parameters</h3>
    <hr style="width:40%;min-width:600px;margin-left:0;">
	<h4 class="param-name">ssl.context.enabled: <span class="param-value ${sslContextEnabled ? ' value-true' : ' value-false'}">${sslContextEnabled}</span></h4>
	<h4 class="param-name">http.proxy.enabled: <span class="param-value ${httpProxyEnabled ? ' value-true' : ' value-false'}">${httpProxyEnabled}</span></h4><br>
	<h4 class="param-name">de4a.kafka.enabled: <span class="param-value ${kafkaEnabled ? ' value-true' : ' value-false'}">${kafkaEnabled}</span></h4>
	<h4 class="param-name">de4a.kafka.logging.enabled: <span class="param-value ${kafkaLogginEnabled ? ' value-true' : ' value-false'}">${kafkaLogginEnabled}</span></h4>
	<h4 class="param-name">de4a.kafka.http.enabled: <span class="param-value ${kafkaHttpEnabled ? ' value-true' : ' value-false'}">${kafkaHttpEnabled}</span></h4>
	<h4 class="param-name">de4a.kafka.url: <span class="param-value value">${empty kafkaUrl ? 'not-set' : kafkaUrl}</span></h4>
	<h4 class="param-name">de4a.kafka.topic: <span class="param-value value">${empty kafkaTopic ? 'not-set' : kafkaTopic}</span></h4><br>
	<h4 class="param-name">smp.endpoint: <span class="param-value value">${empty smpEndpoint ? 'not-set' : smpEndpoint}</span></h4>
	<h4 class="param-name">idk.endpoint: <span class="param-value value">${empty idkEndpoint ? 'not-set' : idkEndpoint}</span></h4><br>
	<c:set value="${fn:split(smpTruststore,'/')}" var="separatorPosition" />
	<h4 class="param-name">smpclient.truststore: <span class="param-value value">${empty smpTruststore ? 'not-set' : separatorPosition[fn:length(separatorPosition)-1]}</span></h4>
	<c:set value="${fn:split(phase4Keystore,'/')}" var="separatorPosition1" />
	<h4 class="param-name">phase4.keystore: <span class="param-value value">${empty phase4Keystore ? 'not-set' : separatorPosition1[fn:length(separatorPosition1)-1]}</span></h4>
	<c:set value="${fn:split(phase4Truststore,'/')}" var="separatorPosition2" />
	<h4 class="param-name">phase4.truststore: <span class="param-value value">${empty phase4Truststore ? 'not-set' : separatorPosition2[fn:length(separatorPosition2)-1]}</span></h4>
    <h4 class="param-name">as4.gateway.implementation.bean: <span class="param-value value">${empty as4GatewayBean ? 'not-set' : as4GatewayBean}</span></h4>
	<hr style="width: 40%;min-width:600px;margin-left:0; margin-top:25px;">
	
	<h4>Check out the system API on: <a href="./swagger-ui/">Connector Swagger API definition</a></h4>
</body>
</html>
