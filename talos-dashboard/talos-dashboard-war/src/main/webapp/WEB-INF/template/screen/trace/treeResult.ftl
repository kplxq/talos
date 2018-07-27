<#include "/layout/tableDataLayout.ftl">

<div style="display:none;">
	<li><label>TraceId: </label><span><@p.out value="traceid" /></span></li>
	<li><label>调用链入口IP: </label><span><@p.out value="traceid" /></span></li>
	<li><label>开始时间: </label><span><@p.out value="traceid" /></span></li>
	<li><label>调用链总时长: </label><span><@p.out value="traceid" /></span></li>
</div>

<table id="trace-tree" width="100%">
	<thead>
		<tr>
			<th>应用名</th>
			<th>IP</th>
			<th>类型</th>
			<th>状态</th>
			<th>服务/方法</th>
			<th>错误编码</th>
			<th>耗时(毫秒)</th>
		</tr>
	</thead>
	<tbody>
		<#if dataList??>
			<#list dataList as single>
				<#if single??>
					<#if single.exType == "OK">
						<tr data-tt-id="<@p.out value="${single.spanId}" />" data-tt-parent-id="<@p.out value="${single.parentSpanId}" />">
							<td><#if (single.content)??><@p.a href="javascript:;" class="viewContent"><@p.out value="${single.appName}" /></@p.a><#else><@p.out value="${single.appName}" /></#if></td>
							<td><@p.out value="${single.hostIp}" /></td>
							<td><@p.out value="${single.type}" /></td>
							<td><@p.out value="${single.exType}" /></td>
							<td><@p.out value="${single.method}" /></td>
							<td><@p.out value="${single.errorCode}" /></td>
							<td><@p.out value="${single.duration}" /></td>
						</tr>
						<#else>
						<tr data-tt-id="<@p.out value="${single.spanId}" />" data-tt-parent-id="<@p.out value="${single.parentSpanId}" />">
							<td><#if (single.content)??><@p.a href="javascript:;" class="viewContent"><@p.out value="${single.appName}" /></@p.a><#else><@p.out value="${single.appName}" /></#if></td>
							<td style="color:red;"><@p.out value="${single.hostIp}" /></td>
							<td style="color:red;"><@p.out value="${single.type}" /></td>
							<td style="color:red;"><@p.out value="${single.exType}" /></td>
							<td style="color:red;"><@p.out value="${single.method}" /></td>
							<td style="color:red;"><#if single.errorCode !='success' ><@p.a href="javascript:;" class="viewErrorMsg"><@p.out value="${single.errorCode}" /></@p.a>
									<#else><@p.out value="${single.errorCode}" /></#if>
							</td>
							<td style="color:red;"><@p.out value="${single.duration}" /></td>
						</tr>
					</#if>
				</#if>
			</#list>
		</#if>
	</tbody>
</table>

<div id="span-param" class="span-param dis-no">
	<table>
		<thead>
			<tr>
				<th>属性编码</th>
				<th>属性值</th>
			<tr>
		</thead>
		<tbody>
		<#if dataList??>
			<#list dataList as single>
				<#if (single.content)??>
					<#list (single.content)?keys as mKey>
					    <tr class="param-for-<@p.out value="${single.spanId?replace('.','')}" />">
					    	<td>${mKey}</td>
					    	<td>${single.content[mKey]}</td>
					    </tr>
				 	</#list>
			 	</#if>
			</#list>
		</#if>
		</tbody>
	</table>
</div>

<div id="span-error-msg" class="dis-no">
</div>

<#if dataList??>
	<#list dataList as single>
		<#if single??>
			<div id="errorMsgContent-${single.spanId?replace('.','')}" class="dis-no" >${single.errorMessage?replace('@wrap#','<br/>&nbsp&nbsp&nbsp&nbsp')} </div>
		 	</#if>
	</#list>
</#if>