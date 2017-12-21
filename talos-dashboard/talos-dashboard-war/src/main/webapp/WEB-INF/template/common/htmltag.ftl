<#--
html标签定义
-->
<#assign jsPath="${resourcesPath}/scripts">
<#assign tempsPath="${resourcesPath}/temps">
<#assign cssPath="${themePath}/styles">
<#assign imgPath="${themePath}/images">
<#-- 版本号 -->
<#assign version="${Application.systemStartupTime}">

<#macro js src>
    <script type="text/javascript" src="${jsPath}/${src}?v=${version}"></script>
</#macro>

<#macro url src>
    ${ctx}${src}<#t>
</#macro>

<#macro script type="text/javascript">
    <script type="${type}">
        <#nested>
    </script>
</#macro>

<#macro css src>
    <link rel="stylesheet" type="text/css" href="${cssPath}/${src}?v=${version}"/>
</#macro>
<#macro style type="text/css">
    <style type="${type}">
        <#nested>
    </style>
</#macro>


<#-- 分页控件 pageHandler用于控制js行为 -->
<#macro pageControl pageHandler="" loadFirst="">
    <#include "/common/page.ftl">
</#macro>

<#-- table title显示层 -->
<#macro tableTitle heads="" >
    <#assign head=heads?replace('\r\n', '' )>
    <#assign arrhead=(head?split(","))>
    <#list arrhead as name>
    <th><@p.out value="${name}" /></th>
    </#list >
</#macro>
<#-- table 数据显示层 -->
<#macro tableData keys="" >
    <#assign key=keys?replace('\r\n', '' )>
    <#assign arrkey=(key?split(","))>
    <#if (data?size>0)>
        <#list data as info>
        <tr <#if info_index%2==1>class="odd"</#if> >
            <#list arrkey as name>
                <#assign nameArr=(name?split("_"))>

                <#if name?index_of("indexSort")!=-1>
                    <td><@p.out value="${info_index+1}" /></td>
                <#elseif name?index_of("yuan")!=-1>
                    <td><#if info[nameArr[1]]??><@p.out value="${info[nameArr[1]]/100}" /></#if></td>
                <#elseif name?index_of("codeShow")!=-1>
                    <td><@t.codeShow typeId="${t[nameArr[1]]}" codeKey="${info[nameArr[2]]}" /></td>
                <#elseif name?index_of("class")!=-1>
                    <td class='${nameArr[1]}'><@p.out value="${info[nameArr[1]]}" /></td>
                <#elseif name?index_of("timeDate")!=-1>
                    <td><#if info[nameArr[1]]??><@p.out value="${info[nameArr[1]]?string('yyyy-MM-dd HH:mm:ss')}" /></#if></td>
                <#elseif name?index_of("dayDate")!=-1>
                    <td><#if info[nameArr[1]]??><@p.out value="${info[nameArr[1]]?string('yyyy-MM-dd')}" /></#if></td>
                <#else>
                    <td><@p.out value="${info[name?trim]}" /></td>
                </#if>
            </#list >
            <#nested info/>
        </tr>
        </#list>
    </#if>
</#macro>



<#macro closeTag>
<#if xhtmlCompliant?exists && xhtmlCompliant>/><#else>></#if>
</#macro>

<#macro out value="" escape="true">
<#if escape=="false">${value}<#rt/>
<#else><#escape x as x?html>${value}</#escape><#rt/>
</#if>
</#macro>

<#macro commonset id="" name="" value="" class="" style="">
<#if id!="">id="${id}"</#if><#rt/>
<#if name!="">name="${name}"</#if><#rt/>
<#if class!="">class="${class}"</#if><#rt/>
<#if style!="">style="${style}"</#if><#rt/>
<#if value!="">value="<@p.out value='${value}'/>"</#if><#rt/>
</#macro>

<#--
<img src="">
-->
<#macro img src width="" height="" title="" id="" style=""><img src="<@imgurl src="${src}"/>"<#rt/>
    <#if width!="">width="${width}"</#if><#rt/>
    <#if title!="">title="${title}"</#if><#rt/> 
    <#if height!="">height="${height}"</#if><#rt/>
    <#if id!="">id="${id}"</#if><#rt/>
    <#if style!="">style="${style}"</#if><#rt/>
></#macro>
<#macro imgurl src>${imgPath}/${src}</#macro>

<#macro tempsimg src id="" width="" height="" usemap="" title="" style="">
<img src="<@tempsimgurl src="${src}"/>" <#rt/>
<#if width!="">width="${width}"</#if><#rt/>
<#if height!="">height="${height}"</#if><#rt/>
<#if usemap!="">usemap="${usemap}"</#if><#rt/>
<#if id!="">id="${id}"</#if><#rt/>
<#if title!="">title="${title}"</#if><#rt/>
<#if style!="">style="${style}"</#if><#rt/>
></#macro>
<#macro tempsimgurl src>${tempsPath}/${src}</#macro>

<#macro token id="">
<input type="hidden" <#if id!=""> id="${id}"</#if><#rt/> name="_page_token" <#if Session["_page_token"]??><#assign _page_token = Session["_page_token"]> value="${_page_token}"/> </#if>
</#macro>

<#--
<form></form>
-->
<#macro form id="" name="" action="" method="post" enctype="" target="" onsubmit="" autocomplete="" url="">
<form<#rt/>
<#if id!=""> id="${id}"</#if><#rt/>
<#if url!=""> data-url="${url}"</#if><#rt/>
<#if name!=""> name="${name}"</#if><#rt/>
<#if action!=""> action="${ctx}${action}"</#if><#rt/>
method="${method}"<#rt/>
<#if enctype!=""> enctype="${enctype}"</#if><#rt/>
<#if target!=""> target="${target}"</#if><#rt/>
<#if onsubmit!=""> onsubmit="${onsubmit}"</#if><#rt/>
<#if autocomplete!=""> autocomplete="${autocomplete}"</#if><#rt/>
>
<#nested/><#rt/>
</form>
</#macro>

<#-- a标签 -->
<#macro a href="" id="" target="" class="" title="" style="">
<a <#rt/>
<#if id!=""> id="${id}"</#if><#rt/>
<#if href!=""> href="<#if "${href}"?starts_with("/")>${ctx}</#if>${href}"</#if><#rt/>
<#if class!=""> class="${class}"</#if><#rt/>
<#if target!=""> target="${target}"</#if><#rt/>
<#if title!=""> title="${title}"</#if><#rt/>
<#if style!=""> style="${style}"</#if><#rt/>
><#nested/><#rt/></a>
</#macro>

<#--
<input type="text"/>
-->
<#macro text id="" name="" value="" class=""  style="" size="" disabled="" readonly="" maxlength="" tabindex=""  autocomplete="" onfocus="" onkeydown="" placeholder="">
<input type="text"<#rt/>
<@p.commonset id="${id}" name="${name}" value="${value}" class="${class}"  style="${style}" />
<#if size!="">size="${size}"</#if><#rt/>
<#if disabled!="">disabled="${disabled}"</#if><#rt/>
<#if readonly!="">readonly="${readonly}"</#if><#rt/>
<#if maxlength!="">maxlength="${maxlength}"</#if><#rt/>
<#if tabindex!="">tabindex="${tabindex}"</#if><#rt/>
<#if autocomplete!="">autocomplete="${autocomplete}"</#if><#rt/>
<#if onfocus!="">onfocus="${onfocus}"</#if><#rt/>
<#if onkeydown!="">onkeydown="${onkeydown}"</#if><#rt/>
<#if placeholder!="">placeholder="${placeholder}"</#if><#rt/>
/><#rt/>
</#macro>

<#--
<input type="hidden"/>
-->
<#macro hidden id="" name="" value="" class="">
<input type="hidden"<#rt/>
<@p.commonset id="${id}" name="${name}" value="${value}" class="${class}"  style="" />
/>
</#macro>

<#--
<input type="button"/>
-->
<#macro button id="" name="" value="" class=""  style="" disabled="" onclick="">
<input type="button"<#rt/>
<@p.commonset id="${id}" name="${name}" value="${value}" class="${class}"  style="${style}" />
<#if disabled!="">disabled="${disabled}"</#if><#rt/>
<#if onclick!="">onclick="${onclick}"</#if><#rt/>
/><#rt/>
</#macro>

<#--
<input type="submit"/>
-->
<#macro submit id="" name="" value="" class=""  style="" disabled="">
<input type="submit"<#rt/>
<@p.commonset id="${id}" name="${name}" value="${value}" class="${class}"  style="${style}" />
<#if disabled!="">disabled="${disabled}"</#if><#rt/>
/><#rt/>
</#macro>

<#--
<input type="reset"/>
-->
<#macro reset id="" name="" value="" class=""  style="" disabled="">
<input type="reset"<#rt/>
<@p.commonset id="${id}" name="${name}" value="${value}" class="${class}"  style="${style}" />
<#if disabled!="">disabled="${disabled}"</#if><#rt/>
/><#rt/>
</#macro>

<#--
<input type="password"/>
-->
<#macro password id="" name="" value="" class=""  style="" size="" disabled="" readonly="" maxlength="" placeholder="" autocomplete="">
<input type="password"<#rt/>
<@p.commonset id="${id}" name="${name}" value="${value}" class="${class}"  style="${style}" />
<#if size!="">size="${size}"</#if><#rt/>
<#if disabled!="">disabled="${disabled}"</#if><#rt/>
<#if readonly!="">readonly="${readonly}"</#if><#rt/>
<#if maxlength!="">maxlength="${maxlength}"</#if><#rt/>
<#if placeholder!="">placeholder="${placeholder}"</#if><#rt/>
<#if autocomplete!="">autocomplete="${autocomplete}"</#if><#rt/>
/><#rt/>
</#macro>

<#--
<input type="checkbox"/>
-->
<#macro checkbox id="" name="" value="" class=""  style="" checked="" autocomplete="" disabled="">
<input type="checkbox"<#rt/>
<@p.commonset id="${id}" name="${name}" value="${value}" class="${class}"  style="${style}" />
<#if autocomplete!="">autocomplete="${autocomplete}"</#if><#rt/>
<#if checked!="">checked="${checked}"</#if><#rt/>
<#if disabled!="">disabled="${disabled}"</#if><#rt/>
/><#rt/>
</#macro>

<#--
<input type="radio"/>
-->
<#macro radio id="" name="" value="" class=""  style="" checked="" defaultValue="" disabled="">
<input type="radio"<#rt/>
<@p.commonset id="${id}" name="${name}" value="${value}" class="${class}"  style="${style}" />
<#if checked!="">checked="${checked}"</#if><#rt/>
<#if disabled!="">disabled="${disabled}"</#if><#rt/>
<#if defaultValue?string==value || (defaultValue=="" && value=="0")>checked="checked"</#if><#rt/>
/><#rt/>
</#macro>

<#--
<input type="file"/>
-->
<#macro file id="" name="" value="" class="" style="" onchange="">
<input type="file"<#rt/>
<@p.commonset id="${id}" name="${name}" value="${value}" class="${class}"  style="${style}" />
<#if onchange!="">onchange="#{onchange}"</#if><#rt/>
/><#rt/>
</#macro>
<#macro permit code >
    <#assign  authCodes = Session["AdminUserSession"].authCodes>
    <#if Session["neverBeDuplicatedTestModeSessionKey"]||authCodes?seq_contains("${code}")><#nested></#if>
</#macro>

