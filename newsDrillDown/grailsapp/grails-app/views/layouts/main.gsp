<!--
  //**********************************************************************
  UX-Bridge NewsDrillDown
  %%
  Copyright (C) 2012 e-Spirit AG
  %%
  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at
  
       http://www.apache.org/licenses/LICENSE-2.0
  
  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
  *********************************************************************//*
  -->
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
<meta http-equiv="X-UA-Compatible" content="IE=8" />
<meta charset="UTF-8">
<meta http-equiv="Content-type" content="text/html;charset=UTF-8">
<r:layoutResources />

<link rel="stylesheet" href="${resource(dir: 'css', file: 'newsDrilldown.css')}" type="text/css">

</head>
<body>

<g:renderHeader lang="${params.lang}" />

<div id="list-news">
	<g:renderNavLeft lang="${params.lang}" />
	<g:layoutBody/>
</div>
		
<g:javascript library="application"/>

<r:layoutResources />
<div class="clearfix"></div>
<g:renderFooter lang="${params.lang}" />
</body>
</html>