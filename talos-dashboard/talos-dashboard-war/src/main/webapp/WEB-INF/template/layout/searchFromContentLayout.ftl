<#-- 框架内容页 -->
<#include "/layout/baseLayout.ftl">

<#macro html_title>
    <#if title??>
        <@title/><#t>
    </#if>
</#macro>

<#macro html_head>
    <link rel="stylesheet" type="text/css" href="${resourcesPath}/theme/${theme}/styles/daterangepicker.css">
    <#if head??>  
        <@head/>
    </#if>
</#macro>

<#macro html_body>
    <@body/>
</#macro>

<#macro html_foot>
    <script type="text/javascript" src="${resourcesPath}/scripts/third/moment.min.js"></script>
    <script type="text/javascript" src="${resourcesPath}/scripts/third/jquery.daterangepicker.js"></script>
    <script type="text/javascript" src="${resourcesPath}/scripts/third/selectList-v0.2.js"></script>
    <script type="text/javascript" src="${resourcesPath}/scripts/third/jquery.validation/jquery.validate.js"></script>
	<@p.js src="third/jquery.validation/additional-methods.js"/>
    <script type="text/javascript" src="${resourcesPath}/scripts/third/jquery.validation/messages_zh.js"></script>
    <script type="text/javascript" src="${resourcesPath}/scripts/third/jquery.validation/validate-settings.js"></script>
    <@p.js src="common/searchForm.js"/>
    <@p.js src="common/code.js"/>
    <#if foot??>  
        <@foot/>
    </#if>
</#macro>