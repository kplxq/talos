<#include "/layout/searchFromContentLayout.ftl">
<#macro title>调用链查看</#macro>
<#macro body>
<!-- 条件筛选 -->
<div class="condition clearfix">
    <div class="condis">
    	<@p.form id="form" url='/tree/query'>
        <table id="conTable">
            <tr>
                <td>
                	<div class="input"><label>TraceId: </label>
                      <span><@p.text name="traceId" id="traceId" value="${thisTraceId}"/></span>
                	</div>
                </td>
            </tr>
        </table>
        </@p.form>
    </div>
    <div class="condis-submit">
        <input type="submit" value="查&ensp;看" id="submitQuery"/>
    </div>

</div>


<!-- 数据 -->
<div id="infoList" class="data">

</div>


</#macro>
<#macro foot>
<@p.js src="third/jquery.treetable.js" />
<@p.js src="trace/treePage.js"/>
<@p.css src="jquery-treetable/jquery.treetable.css"/>
<@p.css src="jquery-treetable/custom-screen.css"/>
</#macro>