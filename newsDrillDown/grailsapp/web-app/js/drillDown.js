/*
 * //**********************************************************************
 * UX-Bridge NewsDrillDown
 * %%
 * Copyright (C) 2012 e-Spirit AG
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * *********************************************************************//*
 */
$(document).ready(function() {

	// will be called on any checkbox click
	$("input[type='checkbox']").click(function() {

		// if the clicked-checkbox belongs to a meta-category it will check/uncheck all categories which belong to this meta-category
		if($(this).is("[name^='metaCat_']")) {
			if($(this).is(':checked')){
				$(this).next().children("li").find("input[type='checkbox']").attr("checked", true); // check all
			} else {
				$(this).next().children("li").find("input[type='checkbox']").attr("checked", false); // uncheck all
			}

			
		} else if($(this).is("[name^='cat_']")) { // if the clicked-checkbox belongs to a category 

			// check if all categories which belong to the same meta-category are unchecked
			if(!$(this).is(':checked') && ($(this).parent().parent().find(":checked").length == 0)){
				$(this).parent().parent().parent().find("input[type='checkbox'][name^='metaCat_']").attr("checked", false); // uncheck meta-category
			} else { // if one category of the parental meta-category is checked
				$(this).parent().parent().parent().find("input[type='checkbox'][name^='metaCat_']").attr("checked", true); // check meta-category 
			}
		}


		/*
		 * Put all checked categories into the variable data
		 */
		var data = "";

		$.each($("[name^='cat_']:checked"), function(i, val) {
	    	data += val.name;
	    });
					
		// if no checkbox is checked no newsdrilldown will be listed
		if(data.length == 0) {
			$('#allNews').html("<div class=\"newsPagination\"></div");
			return;	
		}

		var ajaxData = new Object();
		ajaxData.categories = data;

		if(max != "") {
			ajaxData.max = max;
		}

		// do the ajax call
		$.ajax({
			url: url,
			type: 'POST',
			data: ajaxData,
			success: updateNewsList
		});
	});

	// the callback function which fills the div #allNews with the reponse-data of the ajax call
	function updateNewsList(data, textStatus) {
		$('#allNews').html(data);
	}
});
