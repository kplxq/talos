<#include "layout/tableDataLayout.ftl" />
<@tabledata page=false>
	<table width="100%">
		<thead>
			<tr>
				<th>主机IP</th>
				<th>进程ID</th>
				<th>数据最后一次采集时间</th>
				<th>最近60秒TPS</th>
				<th>总TPS</th>
				<th>轨迹</th>
			</tr>
		</thead>
		<tbody>
			<#if data??>
				<#list data as single>
					<tr>
						<td><@p.out value="${single.host}" /></td>
						<td><@p.out value="${single.processId}" /></td>
						<td><@p.out value="${single.lastReportTime?string('yyyy-MM-dd HH:mm:ss')}" /></td>
						<td><@p.out value="${single.tps60}" /></td>
						<td><@p.out value="${single.tpsTotal}" /></td>
						<td><@p.a href="javascript:viewTrace('${single.host}','${single.appId}');" class="viewTrace">查看</@p.a></td>
					</tr>
				</#list>
			<#else>
				<tr>
					<td><@p.out value="test" /></td>
					<td><@p.out value="test" /></td>
					<td><@p.out value="test" /></td>
					<td><@p.out value="test" /></td>
					<td><@p.out value="test" /></td>
					<td><@p.a href="javascript:viewTrace('talos-storage','8081');" class="viewTrace">查看</@p.a></td>
				</tr>
			</#if>
		</tbody>
	</table>
</@tabledata>