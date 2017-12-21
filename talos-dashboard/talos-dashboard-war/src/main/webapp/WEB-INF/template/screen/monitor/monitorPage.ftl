<#include "/layout/contentLayout.ftl" />

<#macro title>转换器监控</#macro>
<#macro body>

<div class="heartbeat-data data">
	<div class="transformer-title">转换器活跃度监测</div>
	<div id="heartbeat-data">
		
	</div>

</div>

<div class="data">
	<div class="transformer-title">转换器处理效率监测</div>
	<div id="performance-data">

	</div>

</div>

<div id="transform-trace"  class="dis-no">
</div>

</#macro>
<#macro foot>
<@p.style>
	.transformer-title { font-size:14px; font-weight:bold; margin: 5px;}
	.heartbeat-data {margin-top: 50px;}
</@p.style>
<@p.js src="monitor/monitorPage.js"/>
</#macro>