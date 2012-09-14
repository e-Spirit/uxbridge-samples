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



<div class="fieldcontain ${hasErrors(bean: articleInstance, field: 'title', 'error')} required">
	<label for="title">
		<g:message code="article.title.label" default="Title" />
		<span class="required-indicator">*</span>
	</label>
	<g:textField name="title" required="" value="${articleInstance?.title}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: articleInstance, field: 'content', 'error')} required">
	<label for="content">
		<g:message code="article.content.label" default="Content" />
		<span class="required-indicator">*</span>
	</label>
	<g:textField name="content" required="" value="${articleInstance?.content}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: articleInstance, field: 'created', 'error')} required">
	<label for="created">
		<g:message code="article.created.label" default="Created" />
		<span class="required-indicator">*</span>
	</label>
	<g:datePicker name="created" precision="day"  value="${articleInstance?.created}"  />
</div>

<div class="fieldcontain ${hasErrors(bean: articleInstance, field: 'url', 'error')} ">
	<label for="url">
		<g:message code="article.url.label" default="Url" />
		
	</label>
	<g:textField name="url" value="${articleInstance?.url}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: articleInstance, field: 'aid', 'error')} required">
	<label for="aid">
		<g:message code="article.aid.label" default="Aid" />
		<span class="required-indicator">*</span>
	</label>
	<g:field type="number" name="aid" required="" value="${fieldValue(bean: articleInstance, field: 'aid')}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: articleInstance, field: 'language', 'error')} ">
	<label for="language">
		<g:message code="article.language.label" default="Language" />
		
	</label>
	<g:textField name="language" value="${articleInstance?.language}"/>
</div>

