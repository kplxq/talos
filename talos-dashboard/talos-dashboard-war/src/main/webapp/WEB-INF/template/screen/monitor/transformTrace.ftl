<#include "/layout/blankLayout.ftl" />

<#macro title>转换器轨迹</#macro>
<#macro body>

<div id="trace-echarts" style="width:600px;height:400px;"></div>

<@p.hidden id="ip" value="${thisIp}"/>
<@p.hidden id="appId" value="${thisAppId}" />

</#macro>
<#macro foot>
<@p.js src="third/echarts.js" />
<@p.js src="monitor/transformTrace.js"/>
</#macro>