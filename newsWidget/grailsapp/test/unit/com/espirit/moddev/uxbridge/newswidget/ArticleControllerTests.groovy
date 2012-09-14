package com.espirit.moddev.uxbridge.newswidget

import com.espirit.moddev.uxbridge.Article;

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

@TestFor(ArticleController)
@Mock(Article)
class ArticleControllerTests {


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

    void testIndex() {
        controller.index()
        assert "/article/list" == response.redirectedUrl
    }

    void testList() {

        def model = controller.list()

        assert model.articleInstanceList.size() == 0
        assert model.articleInstanceTotal == 0
    }

    void testCreate() {
       def model = controller.create()

       assert model.articleInstance != null
    }

    void testSave() {
        controller.save()

        assert model.articleInstance != null
        assert view == '/article/create'

        response.reset()

        populateValidParams(params)
        controller.save()

        assert response.redirectedUrl == '/article/show/1'
        assert controller.flash.message != null
        assert Article.count() == 1
    }

    void testShow() {
        controller.show()

        assert flash.message != null
        assert response.redirectedUrl == '/article/list'


        populateValidParams(params)
        def article = new Article(params)

        assert article.save() != null

        params.id = article.id

        def model = controller.show()

        assert model.articleInstance == article
    }

    void testEdit() {
        controller.edit()

        assert flash.message != null
        assert response.redirectedUrl == '/article/list'


        populateValidParams(params)
        def article = new Article(params)

        assert article.save() != null

        params.id = article.id

        def model = controller.edit()

        assert model.articleInstance == article
    }

    void testUpdate() {
        controller.update()

        assert flash.message != null
        assert response.redirectedUrl == '/article/list'

        response.reset()


        populateValidParams(params)
        def article = new Article(params)

        assert article.save() != null

        // test invalid parameters in update
        params.id = article.id
		params['aid'] = null

        controller.update()

        assert view == "/article/edit"
        assert model.articleInstance != null

        article.clearErrors()

        populateValidParams(params)
        controller.update()

        assert response.redirectedUrl == "/article/show/$article.id"
        assert flash.message != null

        //test outdated version number
        response.reset()
        article.clearErrors()

        populateValidParams(params)
        params.id = article.id
        params.version = -1
        controller.update()

        assert view == "/article/edit"
        assert model.articleInstance != null
        assert model.articleInstance.errors.getFieldError('version')
        assert flash.message != null
    }

    void testDelete() {
        controller.delete()
        assert flash.message != null
        assert response.redirectedUrl == '/article/list'

        response.reset()

        populateValidParams(params)
        def article = new Article(params)

        assert article.save() != null
        assert Article.count() == 1

        params.id = article.id

        controller.delete()

        assert Article.count() == 0
        assert Article.get(article.id) == null
        assert response.redirectedUrl == '/article/list'
    }
}

