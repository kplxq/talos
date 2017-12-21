<#-- 默认样式 -->
<#global theme="default">
<#-- SESSION KEY -->
<#global UserSession="UserSession">
<#if Session["${AdminUserSession}"]??><#global currentUser = Session["${AdminUserSession}"]></#if>
<#-- 上下文 -->
<#global ctx="${request.contextPath}">
<#-- 静态资源 -->
<#global resourcesPath="${ctx}/resources">
<#-- 主题 -->
<#global themePath="${resourcesPath}/theme/${theme}">
<#-- 自定义html标签 -->
<#import "/common/htmltag.ftl" as p>
