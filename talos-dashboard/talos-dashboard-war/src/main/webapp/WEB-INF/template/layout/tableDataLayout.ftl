<#-- 基本布局模板 -->
<#include "/common/baselib.ftl">
<#macro tabledata page="true" pageSize="10" pageHandler="pageHandler" loadFirst="true">
    <#nested/>
    <#if page="true">
        <@p.pageControl pageHandler=pageHandler loadFirst=loadFirst/>
    </#if>
</#macro>