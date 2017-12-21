<#include "/layout/searchFromContentLayout.ftl">
<#macro title>Talos</#macro>
<#macro body>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Talos</title>
    <style>
        html{color:#333;background:#01000E;font-size:16px;-webkit-text-size-adjust:100%;-ms-text-size-adjust:100%;text-rendering:optimizelegibility}
        article,aside,blockquote,body,button,code,dd,details,dl,dt,fieldset,figcaption,figure,footer,form,h1,h2,h3,h4,h5,h6,header,hr,input,legend,li,menu,nav,ol,p,pre,section,td,textarea,th,ul{margin:0;padding:0}
        body,button,input,select,textarea{font:300 1rem/1.8 PingHei,'PingFang SC','Helvetica Neue',Helvetica,STHeitiSC-Light,Microsoft Yahei,Hiragino Sans GB,Microsoft Sans Serif,WenQuanYi Micro Hei,sans}
        table{border-collapse:collapse;border-spacing:0}
        fieldset,img{border:0}
        address,caption,cite,code,dfn,em,th,var{font-style:normal;font-weight:400}
        ol,ul{list-style:none}
        caption,th{text-align:left}
        a{text-decoration:none;color:#5096D7}
        a:active{color: #aed6fb}
        a,button,input,select,textarea{-webkit-tap-highlight-color:transparent}
        input,textarea{outline:0}
        body {
            position: absolute;
            width: 100%;
            height: 100%;
            left: 0;
            top: 0;
            background: url(/talos-dashboard/resources/images/bg.png) center no-repeat;
            background-size: cover;
            color: #FFF;
        }
        .menu {
            position: relative;
            width: 600px;
            margin: 0 auto;
            top: 47%;
            text-align: center;
        }
        .menu a {
            display: inline-block;
            background: #FDD630;
            color: #2D2D2D;
            padding: 7px 30px;
            margin-left: 30px;
            -ms-transition: all ease-in .3s;
            transition: all ease-in .3s;
        }
        .menu a:hover {
            background: #c1a227;
        }
        .menu a:first-child {
            margin-left: 0;
        }
    </style>
</head>
<body>
<div class="menu">
    <a href="javascript:traceSearch()">调用链搜索</a><a href="javascript:traceTree()">调用树</a><a href="javascript:monitorReport()">系统健康度</a>
</div>
</body>
</html>
</#macro>
<#macro foot>
<@p.js src="system/index.js"/>
</#macro>