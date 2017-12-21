<#-- 基本布局模板 -->
<#include "/common/baselib.ftl">
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <title><@html_title/> - Talos</title>
    <#if head_meta??>  
        <@head_meta/>
    </#if>
    <link rel="shortcut icon" href="${resourcesPath}/theme/favicon.ico" type="image/x-icon" />
    <@p.css src="style.css"/>
    <#if html_head??>  
    	<@html_head/>
    </#if>
</head>
<body>
    <#compress>
    <@html_body/>
    <script type="text/javascript" src="${resourcesPath}/scripts/third/jquery-1.8.0.min.js"></script>
    <script type="text/javascript" src="${resourcesPath}/scripts/third/layer/layer.js"></script>
    <script type="text/javascript">
    var KXin_Config = {
		bathPath : "${ctx}"
	};
	</script>
    <@p.js src="common/global.js"/>	
    <#if html_foot??>
        <@html_foot/>
    </#if>
    </#compress>
</body>
</html>