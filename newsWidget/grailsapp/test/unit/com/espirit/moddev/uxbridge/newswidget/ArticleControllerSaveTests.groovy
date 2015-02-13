package com.espirit.moddev.uxbridge.newswidget



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


import grails.test.mixin.*
import spock.lang.Specification
import com.espirit.moddev.uxbridge.Article;


@TestFor(ArticleController)
@Mock(Article)
class ArticleControllerSaveTests extends Specification {


    def populateValidParams(params) {
		assert params != null
		Calendar cal = Calendar.instance
		cal.add(Calendar.MINUTE, (int)1)

		params["aid"] = 1
		params["content"] = "Content"
		params["title"] = "title"
		params["created"] = cal.time
		params["url"] = "none"
		params["language"] = "DE"
		params["lastmodified"] = 0
    }

    
    void "test save an invalid article"() {
        when:
		request.method = 'POST'
		controller.save()

		then:
		model != null
		model.articleInstance != null
		view == '/article/create'
    }
    void "test save a valid article"() {
        when:
		request.method = 'POST'
		populateValidParams(params)
		controller.save()

		then:
		response.redirectedUrl == '/article/show/1'
		controller.flash.message != null
		Article.count() == 1
    }
}

