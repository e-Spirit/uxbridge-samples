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
<script type="text/javascript">
	var max = "${max}";
	var url = "${request.contextPath}/news/listNews";
</script>
<g:javascript library="drillDown" ></g:javascript>

<ul class="metaCategoryList">
	<g:each in="${ metaCategoryList }" var="metaCategory">
		<li>
			<g:checkBox name="metaCat_${metaCategory.id}" value="true" /> ${metaCategory}
			<ul class="categoryList">
				<%-- iterate over category; sorting ensures that the categories are always in the same order --%>
				<g:each in="${metaCategory.categories.sort{it.name}}" var="${category}"> 
					<li>
							<g:checkBox name="cat_${category.id }" value="true" /> ${category}				
					</li>
				</g:each>
			</ul>
		</li>
	</g:each>
</ul>