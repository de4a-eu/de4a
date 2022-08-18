<!DOCTYPE HTML>
<html lang="en">
<head>
    <title>DE4A-Connector (Iteration 2) - Info</title>
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
    <h1 class="h1-title"><span class="de4a">DE4A</span> Connector (Iteration 2)</h1>
    <h3>Status: <span class="param-value value-true">Running</span></h3>
    <br>
    <h3 style="margin-bottom: 5px;">System Parameters</h3>
    <hr style="width:40%;min-width:600px;margin-left:0;">
    <h4 class="param-name">de4a.smp.sml.dnszone: <%= eu.de4a.connector.JSPHelper.formattedProp("de4a.smp.sml.dnszone") %></h4>
    <h4 class="param-name">de4a.smp.sml.serviceurl: <%= eu.de4a.connector.JSPHelper.formattedProp("de4a.smp.sml.serviceurl") %></h4>
    <br>
    <h4 class="param-name">de4a.kafka.enabled: <%= eu.de4a.connector.JSPHelper.formattedProp("de4a.kafka.enabled") %></h4>
    <h4 class="param-name">de4a.kafka.http.enabled: <%= eu.de4a.connector.JSPHelper.formattedProp("de4a.kafka.http.enabled") %></h4>
    <h4 class="param-name">de4a.kafka.url: <%= eu.de4a.connector.JSPHelper.formattedProp("de4a.kafka.url") %></h4>
    <h4 class="param-name">de4a.kafka.topic: <%= eu.de4a.connector.JSPHelper.formattedProp("de4a.kafka.topic") %></h4>
    <br>
    <h4 class="param-name">idk.endpoint: <%= eu.de4a.connector.JSPHelper.formattedProp("idk.endpoint") %></h4></h4>
    <br>
    <h4 class="param-name">phase4.send.fromparty.id: <%= eu.de4a.connector.JSPHelper.formattedProp("phase4.send.fromparty.id") %></h4></h4>
    <h4 class="param-name">de4a.me.implementation: <%= eu.de4a.connector.JSPHelper.formattedProp("de4a.me.implementation") %></h4></h4>
    <hr style="width: 40%;min-width:600px;margin-left:0; margin-top:25px;">
</body>
</html>
