<#-- 空白内容布局模板 -->
<#include "/layout/baseLayout.ftl">

<#macro html_title>
    <#if title??>
        <@title/><#t>
    </#if>
</#macro>

<#macro html_head>
    <#if head??>  
        <@head/>
    </#if>
</#macro>

<#macro html_body>
    <@body/>
</#macro>

<#macro html_foot>
    <#if foot??>  
        <@foot/>
    </#if> 
</#macro>