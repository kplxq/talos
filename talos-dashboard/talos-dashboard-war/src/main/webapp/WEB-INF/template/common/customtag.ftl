
<#-- 用户类型  -->
<#assign userType = "0101" > 

<#-- 码表转译 -->
<#macro codeShow typeId codeKey>
	<@codeTag typeId="${typeId}" codeKey="${codeKey}"></@codeTag>
</#macro>