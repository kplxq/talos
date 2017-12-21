<#include "/layout/tableDataLayout.ftl">
<@tabledata>
<table width="100%">
	<tr>
		<th>TraceId</th>
		<th>入口方法</th>
		<th>创建时间</th>
		<th>操作</th>
	</tr>
	<#if dataList??>
		<#list dataList as single>
			<tr>
				<td class="traceId"><@p.out value="${single.traceId}" /></td>
				<td><@p.out value="${single.methodName}" /></td>
				<td><@p.out value="${single.startTime}" /></td>
				<td class="operate"><@p.a href="javascript:;" class="viewDetail">查看详情</@p.a></td>
			</tr>
		</#list>
	</#if>
</table>
</@tabledata>
