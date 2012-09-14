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
<%@ page import="com.espirit.moddev.examples.uxbridge.newsdrilldown.News" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:javascript library="jquery"/>
	</head>
	<body>
		<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
		</g:if>

		<div id="newsExample" class="contentArea">
			<div class="allNews" id="allNews">
				<g:include controller="news" action="listNews" params="[categories: 'all', lang : params.lang]"/>
			</div> <%-- allNews --%> 
		</div> <%-- newsExample --%>
		<div id="drillDownColumn">
			<g:include controller="news" action="drilldown" params="[lang : params.lang]"/> 
		</div>
		
	</body>
</html>