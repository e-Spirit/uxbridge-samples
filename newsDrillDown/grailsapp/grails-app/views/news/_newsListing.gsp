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
<g:if test="${newsInstanceTotal > max}">
	<div class="newsPagination">
		<util:remotePaginate controller="news" action="listNews" total="${newsInstanceTotal}" update="allNews" params="[categories:categories, lang:params.lang]"/>
	</div>
</g:if>
<g:else>
 <div class="paginationReplace">&nbsp;</div>
</g:else>

<g:each in="${newsInstanceList}" status="i" var="newsInstance">
	<div class="newsBlock">
		<span class="date"><g:formatDate date="${newsInstance.date}" format="dd.MM.yyyy" /></span>
		<h2 class="headline">${fieldValue(bean: newsInstance, field: "headline")}</h2>
		<h4 class="subheadline">${fieldValue(bean: newsInstance, field: "subheadline")}</h4>
		<p class="content">
			<a href="${fieldValue(bean: newsInstance, field: "url")}">
				${fieldValue(bean: newsInstance, field: "teaser")} 
				<span><g:img dir="images" file="icon-SignLink.gif"/></span>
			</a>
		</p>
		<span class="category">
			<g:each in="${newsInstance.categories.sort {it.name}}" status="j" var="categoryInstance"><g:if test="${j > 0}">, </g:if>${fieldValue(bean: categoryInstance, field: "name")}</g:each>
		</span>
	</div>
</g:each>