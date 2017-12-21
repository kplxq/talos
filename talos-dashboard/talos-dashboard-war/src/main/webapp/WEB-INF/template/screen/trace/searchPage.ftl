<#include "/layout/searchFromContentLayout.ftl">
<#macro title>调用链搜索</#macro>
<#macro body>
<!-- 条件筛选 -->
<div class="condition clearfix">
    <div class="condis">
    	<@p.form id="form" url='/search/query' onsubmit="return false;">
        <table id="conTable">
            <tr>
                <td>
                	<div class="input">
                      <span><@p.text name="content" id="content"/></span>
                	</div>
                </td>
            </tr>
        </table>
        </@p.form>
    </div>
    <div class="condis-submit">
        <input type="submit" value="搜&ensp;索" id="submitQuery"/>
    </div>

</div>


<!-- 数据 -->
<div id="infoList" class="data">

</div>

<@p.hidden id="queryTotalMax" value="${queryTotalMax}" />

</#macro>
<#macro foot>
<@p.js src="trace/searchPage.js"/>
</#macro>