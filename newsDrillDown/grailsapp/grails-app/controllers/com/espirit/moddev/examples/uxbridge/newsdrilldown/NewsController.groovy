package com.espirit.moddev.examples.uxbridge.newsdrilldown

import grails.converters.JSON

class NewsController {

    def filterService

    def index() {
		params.lang = params.lang ? params.lang.toUpperCase() : "EN"
		params.max = Math.min(params.max?.toInteger() ?: 10, 100)
		
		redirect(action: "list", params: params)
    }

	def list() {
		params.lang = params.lang ? params.lang.toUpperCase() : "EN"
		params.max = Math.min(params.max?.toInteger() ?: 10, 100)
		
		flash.message = ""
		[]
	}
	
	def listNews() {
		params.max = Math.min(params.max?.toInteger() ?: 10, 100)
		params.offset = params.offset ? params.offset : 0
		params.lang = params.lang ? params.lang.toUpperCase() : "EN"
		
		def listDataMap = filterService.filter(params)
		if(listDataMap.msg) {
			switch (listDataMap.msg) {
			case "noCategory":
				flash.message = message(code: 'default.not.found.message', args: [message(code: 'category.label', default: 'Category'), params.categories])
				break;

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

			default:
				flash.message = ""
				break;
			}
		}
		render(	template:"newsListing",
				model:[	
					newsInstanceList: listDataMap.newsInstanceList, 
					newsInstanceTotal: listDataMap.newsInstanceTotal,
					categories: params.categories,
					max: params.max,
					offset: params.offset,
					lang: params.lang
				]
		)
	}

	def drilldown() {
		// will fetch article titles in this language
		def lang = params.lang ? params.lang.toUpperCase() : "EN"
		params.max = Math.min(params.max?.toInteger() ?: 10, 100)
		render(template:"drilldown", model:[metaCategoryList: MetaCategory.findAllWhere(language: lang), max: params.max, offset: params.offset])
	}
	
	def show = {
		
		// the jsonp callback
		def callback = params.callback ? params.callback : "callback"
	
		// will fetch article titles in this language
		def lang = params.lang ? params.lang.toUpperCase() : "EN"
	
		long id = params.id as long
	
		def newsInstance = News.findWhere(fs_id: id, language: lang)
	
		if (!newsInstance) {
			response.sendError(404)
			return
		}
	
		render "${callback}(${newsInstance as JSON})"
	}
}