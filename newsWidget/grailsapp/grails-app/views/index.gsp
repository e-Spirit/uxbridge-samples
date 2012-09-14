<!--
  //**********************************************************************
  UX-Bridge NewsWidget
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
<html>
<head>
    <title>UXB WidgetExample</title>
    <meta name="layout" content="main"/>
		<g:javascript>
			$(document).ready(function () {
				$("#uxbWidgetContent").uxb_widget({lang:'${params.lang }',url:"${request.contextPath}/rest/v1/articles",speed:2000, fadeFrom: "#F7D358", fadeTo: "white", count: 5});
			});
		</g:javascript>
</head>

<body>
<div id="pageBody">
	<div class="uxbWidgetContainer">
		<div class="uxbWidgetHeader">
			<span><g:message code="uxbWidget.latest.articles"/></span>
		</div>
	    <div id="uxbWidgetContent"></div>
	</div>

	<div class="content">
		<div class="section">
		    <h1>UX-Bridge: Connecting content and user experience</h1>
		
		    <p>
		    	Mit FirstSpirit 5 stellt e-Spirit eine neue Infrastruktur für hochdynamische Webseiten zur Verfügung: 
		    	<a href="http://cebit.e-spirit.com/en/user_experience/">UX-Bridge</a> sorgt für eine positive User Experience im Frontend. Sie ermöglicht Live-Updates von Inhalten 
		    	direkt auf der Webseite und fungiert als Integrationsplattform für die Live-Website, z. B. zur 
		    	Einbindung von Social Media-Lösungen, für Content-Targeting oder User Generated Content.
			</p>	
		    
		    <span class="sectionLink"><g:link controller="article">Zeige Artikelliste</g:link></span>
	    </div>
	</div>
</div>
</body>
</html>
