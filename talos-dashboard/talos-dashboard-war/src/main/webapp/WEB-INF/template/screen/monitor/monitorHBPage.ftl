<#include "layout/tableDataLayout.ftl" />
<@tabledata page=false>
	<table width="100%">
		<thead>
			<tr>
				<th>主机IP</th>
				<th>进程ID</th>
				<th>数据最后一次采集时间</th>
				<th>累计启动时间(单位:秒)</th>
				<th>当前状态</th>
			</tr>
		</thead>
		<tbody>
			<#if data??>
				<#list data as single>
					<tr>
						<td><@p.out value="${single.host}" /></td>
						<td><@p.out value="${single.processId}" /></td>
						<td><@p.out value="${single.lastReportTimeStr}" /></td>
						<td><@p.out value="${single.totalRunningTimeInmills/1000}" /></td>
						<td <#if single.status=="offline"> style="color:red;" </#if>><@p.out value="${single.status}" /></td>
					</tr>
				</#list>
			</#if>
		</tbody>
	</table>
</@tabledata>