<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<spring:eval expression="@environment.getProperty('ssl.context.enabled') == 'true'" var="sslContextEnabled" />
<spring:eval expression="@environment.getProperty('http.proxy.enabled') == 'true'" var="httpProxyEnabled" />
<spring:eval expression="@environment.getProperty('de4a.kafka.enabled') == 'true'" var="kafkaEnabled" />
<spring:eval expression="@environment.getProperty('de4a.kafka.http.enabled') == 'true'" var="kafkaHttpEnabled" />
<spring:eval expression="@environment.getProperty('de4a.kafka.topic')" var="kafkaTopic" />
<spring:eval expression="@environment.getProperty('de4a.kafka.url')" var="kafkaUrl" />
<spring:eval expression="@environment.getProperty('smp.endpoint')" var="smpEndpoint" />
<spring:eval expression="@environment.getProperty('idk.endpoint')" var="idkEndpoint" />
<spring:eval expression="@environment.getProperty('phase4.send.toparty.id.type')" var="phase4SendToParty" />
<spring:eval expression="@environment.getProperty('phase4.send.fromparty.id.type')" var="phase4SendFromParty" />
<spring:eval expression="@environment.getProperty('toop.mem.implementation')" var="toopImpl" />
<spring:eval expression="@environment.getProperty('as4.gateway.implementation.bean')" var="as4GatewayBean" />


<!DOCTYPE HTML>
<html xmlns:th="https://www.thymeleaf.org" lang="en">
<head>
    <title>DE4A-Connector</title>
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
    <h1 style="color: #21739a;"><span>DE4A</span> - Connector</h1>
    <h3>Status: <span class="param-value value-true">Running</span></h3>
    <br>
    <h3 style="margin-bottom: 5px;">System Parameters</h3>
    <hr style="width: 40%;margin-left: 0;">
	<h4 class="param-name">ssl.context.enabled: <span class="param-value ${sslContextEnabled ? ' value-true' : ' value-false'}">${sslContextEnabled}</span></h4>
	<h4 class="param-name">http.proxy.enabled: <span class="param-value ${httpProxyEnabled ? ' value-true' : ' value-false'}">${httpProxyEnabled}</span></h4><br>
	<h4 class="param-name">de4a.kafka.enabled: <span class="param-value ${kafkaEnabled ? ' value-true' : ' value-false'}">${kafkaEnabled}</span></h4>
	<h4 class="param-name">de4a.kafka.http.enabled: <span class="param-value ${kafkaHttpEnabled ? ' value-true' : ' value-false'}">${kafkaHttpEnabled}</span></h4>
	<h4 class="param-name">de4a.kafka.url: <span class="param-value value">${empty kafkaUrl ? 'not-setted' : kafkaUrl}</span></h4>
	<h4 class="param-name">de4a.kafka.topic: <span class="param-value value">${empty kafkaTopic ? 'not-setted' : kafkaTopic}</span></h4><br>
	<h4 class="param-name">smp.endpoint: <span class="param-value value">${empty smpEndpoint ? 'not-setted' : smpEndpoint}</span></h4>
	<h4 class="param-name">idk.endpoint: <span class="param-value value">${empty idkEndpoint ? 'not-setted' : idkEndpoint}</span></h4><br>
	<h4 class="param-name">phase4.send.toparty.id.type: <span class="param-value value">${empty phase4SendToParty ? 'not-setted' : phase4SendToParty}</span></h4>
	<h4 class="param-name">phase4.send.fromparty.id.type: <span class="param-value value">${empty phase4SendFromParty ? 'not-setted' : phase4SendFromParty}</span></h4>
	<h4 class="param-name">toop.mem.implementation: <span class="param-value value">${empty toopImpl ? 'not-setted' : toopImpl}</span></h4>
	<h4 class="param-name">as4.gateway.implementation.bean: <span class="param-value value">${empty as4GatewayBean ? 'not-setted' : as4GatewayBean}</span></h4>
	<hr style="width: 40%;margin-left: 0; margin-top:25px;">
	
	<h4>Check out the system API on: <a href="./swagger-ui/">Connector Swagger API definition</a></h4>
</body>
</html>
