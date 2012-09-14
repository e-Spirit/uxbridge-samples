package com.espirit.moddev.uxbridge

import com.espirit.moddev.uxbridge.newswidget.ArticleRestController

import grails.converters.JSON

class ArticleRestControllerTests extends GroovyTestCase {

	def articleService

    private void createEntry (Long i) {

		Calendar cal = Calendar.instance
		cal.add(Calendar.MINUTE, (int)i)
		def a = new Article(aid: i, title: "title $i", content: "content $i", created: cal.time, url: "none", language: "EN", lastmodified: 0)

		a = a.save(flush : true, failOnError : true)
		assert a != null
    }

    private void createEntries (int count) {
        (1..count).each {
            index -> createEntry(index)
        }
    }

	private void deleteAllArticles() {
		def articleList = Article.getAll()
		for( Article article : articleList ) {
			article.delete(flush : true, failOnError: true)
		}
	}

    protected void setUp() {
        super.setUp()
		deleteAllArticles()
		createEntries(10)
    }
    protected void tearDown() {
        super.tearDown()

		deleteAllArticles()
    }

    void testController() {
		def listArt = Article.list()
        def ac = new ArticleRestController()
		ac.articleService = articleService
		ac.list()
        def content = ac.response.contentAsString
        assertEquals 200, ac.response.status
        assert content.size() > 2

		def jsonContent = content.substring("callback(".length(), content.length()-1);

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

        def jsonObj = JSON.parse(jsonContent);
        assert jsonObj instanceof List
    }

    void testArticleCount() {
        def ac = new ArticleRestController()
		ac.articleService = articleService
		ac.params.count = 2
        ac.list()

		def content = ac.response.contentAsString
		def jsonContent = content.substring("callback(".length(), content.length()-1);
		def jsonObj = JSON.parse(jsonContent);
        assertEquals 2, jsonObj.size()
    }

    void testDefaultMaxCount() {
        def ac = new ArticleRestController()
		ac.articleService = articleService
        ac.list()

        def content = ac.response.contentAsString
		def jsonContent = content.substring("callback(".length(), content.length()-1);
		def jsonObj = JSON.parse(jsonContent);
        assertEquals 5, jsonObj.size()
    }

    void testOrder() {
        def ac = new ArticleRestController()
		ac.articleService = articleService
        ac.list()
        def content = ac.response.contentAsString
        assertEquals 200, ac.response.status
        assertNotNull content

		def jsonContent = content.substring("callback(".length(), content.length()-1);
		def jsonObj = JSON.parse(jsonContent);

        assertEquals "title 10", jsonObj[0].title
        assertEquals "title 9", jsonObj[1].title
        assertEquals "title 8", jsonObj[2].title
    }

	void testShow_200() {
		def ac = new ArticleRestController()
		ac.articleService = articleService
		ac.params.id = 1
        ac.show()
        assertEquals 200, ac.response.status
	}

	void testShow_404() {
		def ac = new ArticleRestController()
		ac.articleService = articleService
		ac.params.id = 11
		ac.show()
		assertEquals 404, ac.response.status
	}
}