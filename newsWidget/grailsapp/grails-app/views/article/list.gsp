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
		<title><g:message code="default.list.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#list-article" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="list-article" class="content scaffold-list" role="main">
			<h1><g:message code="default.list.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<table>
				<thead>
					<tr>
					
						<g:sortableColumn property="title" title="${message(code: 'article.title.label', default: 'Title')}" />
					
						<g:sortableColumn property="content" title="${message(code: 'article.content.label', default: 'Content')}" />
					
						<g:sortableColumn property="created" title="${message(code: 'article.created.label', default: 'Created')}" />
					
						<g:sortableColumn property="url" title="${message(code: 'article.url.label', default: 'Url')}" />
					
						<g:sortableColumn property="aid" title="${message(code: 'article.aid.label', default: 'Aid')}" />
					
						<g:sortableColumn property="language" title="${message(code: 'article.language.label', default: 'Language')}" />
					
					</tr>
				</thead>
				<tbody>
				<g:each in="${articleInstanceList}" status="i" var="articleInstance">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
					
						<td><g:link action="show" id="${articleInstance.id}">${fieldValue(bean: articleInstance, field: "title")}</g:link></td>
					
						<td>${fieldValue(bean: articleInstance, field: "content")}</td>
					
						<td><g:formatDate date="${articleInstance.created}" /></td>
					
						<td><a href="${fieldValue(bean: articleInstance, field: "url")}">Url</a></td>
					
						<td>${fieldValue(bean: articleInstance, field: "aid")}</td>
					
						<td>${fieldValue(bean: articleInstance, field: "language")}</td>
					
					</tr>
				</g:each>
				</tbody>
			</table>
			<div class="pagination">
				<g:paginate total="${articleInstanceTotal}" />
			</div>
		</div>
	</body>
</html>
