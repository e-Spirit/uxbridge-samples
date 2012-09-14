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

<%@ page import="com.espirit.moddev.uxbridge.Article" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'article.label', default: 'Article')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#show-article" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="show-article" class="content scaffold-show" role="main">
			<h1><g:message code="default.show.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<ol class="property-list article">
			
				<g:if test="${articleInstance?.title}">
				<li class="fieldcontain">
					<span id="title-label" class="property-label"><g:message code="article.title.label" default="Title" /></span>
					
						<span class="property-value" aria-labelledby="title-label"><g:fieldValue bean="${articleInstance}" field="title"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${articleInstance?.content}">
				<li class="fieldcontain">
					<span id="content-label" class="property-label"><g:message code="article.content.label" default="Content" /></span>
					
						<span class="property-value" aria-labelledby="content-label"><g:fieldValue bean="${articleInstance}" field="content"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${articleInstance?.created}">
				<li class="fieldcontain">
					<span id="created-label" class="property-label"><g:message code="article.created.label" default="Created" /></span>
					
						<span class="property-value" aria-labelledby="created-label"><g:formatDate date="${articleInstance?.created}" /></span>
					
				</li>
				</g:if>
			
				<g:if test="${articleInstance?.url}">
				<li class="fieldcontain">
					<span id="url-label" class="property-label"><g:message code="article.url.label" default="Url" /></span>
					
						<span class="property-value" aria-labelledby="url-label"><a href="${fieldValue(bean: articleInstance, field: "url")}">Url</a></span>
					
				</li>
				</g:if>
			
				<g:if test="${articleInstance?.aid}">
				<li class="fieldcontain">
					<span id="aid-label" class="property-label"><g:message code="article.aid.label" default="Aid" /></span>
					
						<span class="property-value" aria-labelledby="aid-label"><g:fieldValue bean="${articleInstance}" field="aid"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${articleInstance?.language}">
				<li class="fieldcontain">
					<span id="language-label" class="property-label"><g:message code="article.language.label" default="Language" /></span>
					
						<span class="property-value" aria-labelledby="language-label"><g:fieldValue bean="${articleInstance}" field="language"/></span>
					
				</li>
				</g:if>
			
			</ol>
			<g:form>
				<fieldset class="buttons">
					<g:hiddenField name="id" value="${articleInstance?.id}" />
					<g:link class="edit" action="edit" id="${articleInstance?.id}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
					<g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
				</fieldset>
			</g:form>
		</div>
	</body>
</html>
