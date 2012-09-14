package com.espirit.moddev.examples.uxbridge.newsdrilldown

class FilterService {
	
	def filter(params) {
		def newsInstanceList = []
		def newsInstanceTotal = null;

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

		def msg = ""
		
		boolean listAll = true
		
		
		// list the requested categories
		if(params.categories) {
			if(!params.categories.equals("all")) {
				listAll = false
				def categories = params.categories.split("cat_")
				categories.each { // iterate over all categories and add all their newsdrilldown to the newInstanceList if the category can be found
					if(Category.get(it) ) {
						def result = filterForCategory(it)
						newsInstanceList += result.newsInstanceList
					} else {
						msg = "noCategory"
					}
				}
				newsInstanceList = newsInstanceList.unique {
					news -> news.id
				}
				newsInstanceTotal = newsInstanceList.size()
				// shorten the list to fit to params.max and params.offset
				def tmpList = []
				for(int i = params.offset.toInteger(); i < (params.offset.toInteger() + params.max.toInteger()); i++ ) {
					// offset+max would be larger than the size of the list if the last page in a pagination is not filled completely
					if(i >= newsInstanceList.size()) {
						break
					}
					
					tmpList.add(newsInstanceList.get(i))
				}
				newsInstanceList = tmpList
			}
		}
		
		// on intial page load all News will be listed
		if(listAll) {
			newsInstanceList += News.findAllByLanguage(params.lang, params)
			newsInstanceTotal = News.findAllByLanguage(params.lang).size()
		}
		
		newsInstanceList = newsInstanceList.sort{
			a,b -> a.date.compareTo(b.date) // sort newsdrilldown according to their dates
		}
		
		return [newsInstanceList: newsInstanceList, newsInstanceTotal: newsInstanceTotal, msg: msg]
	}

    def filterForCategory(categoryId) {
		def newsInstanceList = Category.get(categoryId).news
		def newsInstanceTotal = newsInstanceList.size()
		
		return [newsInstanceList: newsInstanceList, newsInstanceTotal: newsInstanceTotal]
    }
}
