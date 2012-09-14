package com.espirit.moddev.uxbridge.newswidget

import grails.converters.JSON
import com.espirit.moddev.uxbridge.Article

/**
 * Provides methods which are used by the newswidget
 */
class ArticleRestController {

	// service to get the latest articles 
	ArticleService articleService

	/**
	 * controller action which will render a jsonp response with the newest articles
	 * takes a params Map like [ lang: "EN", callback: callbackFunction()]
	 * where callback should be a jsonp callback function
	 */
	def list = {
		// count of the articles
		def maxCount = params.count ? params.count as int : 5
		// the jsonp callback
		def callback = params.callback ? params.callback : "callback"

		// will fetch article titles in this language
		def lang = params.lang ? params.lang.toUpperCase() : "EN"

		def arts = Article.findAll();

/*
 * //**********************************************************************
 * UX-Bridge NewsWidget
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

		
		// the articles which are shown later
		def articleList = []

		articleList = articleService.getLatestArticles(maxCount, lang)
		render "${callback}(${articleList as JSON})"
	}
	
	//TODO javaDoc
	def show = {

		// the jsonp callback
		def callback = params.callback ? params.callback : "callback"

		// will fetch article titles in this language
		def lang = params.lang ? params.lang.toUpperCase() : "EN"

        long id = params.id as long

		def articleInstance = Article.findWhere(aid: id, language: lang)

        if (!articleInstance) {
			response.sendError(404)
            return
        }

        render "${callback}(${articleInstance as JSON})"
    }
}
