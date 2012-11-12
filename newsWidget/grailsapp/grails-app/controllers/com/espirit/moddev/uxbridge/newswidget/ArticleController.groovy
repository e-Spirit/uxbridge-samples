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
package com.espirit.moddev.uxbridge.newswidget

import com.espirit.moddev.uxbridge.Article
import org.springframework.dao.DataIntegrityViolationException

class ArticleController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    }

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [articleInstanceList: Article.list(params), articleInstanceTotal: Article.count()]
    }

    def create() {
        [articleInstance: new Article(params)]
    }

    def save() {
        def articleInstance = new Article(params)
        if (!articleInstance.save(flush: true)) {
            render(view: "create", model: [articleInstance: articleInstance])
            return
        }

		flash.message = message(code: 'default.created.message', args: [message(code: 'article.label', default: 'Article'), articleInstance.id])
        redirect(action: "show", id: articleInstance.id)
    }

    def show() {
        def articleInstance = Article.get(params.id)
        if (!articleInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'article.label', default: 'Article'), params.id])
            redirect(action: "list")
            return
        }

        [articleInstance: articleInstance]
    }

    def edit() {
        def articleInstance = Article.get(params.id)
        if (!articleInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'article.label', default: 'Article'), params.id])
            redirect(action: "list")
            return
        }

        [articleInstance: articleInstance]
    }

    def update() {
        def articleInstance = Article.get(params.id)
        if (!articleInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'article.label', default: 'Article'), params.id])
            redirect(action: "list")
            return
        }

        if (params.version) {
            def version = params.version.toLong()
            if (articleInstance.version > version) {
                articleInstance.errors.rejectValue("version", "default.optimistic.locking.failure",
                          [message(code: 'article.label', default: 'Article')] as Object[],
                          "Another user has updated this Article while you were editing")
                render(view: "edit", model: [articleInstance: articleInstance])
                return
            }
        }

        articleInstance.properties = params
        articleInstance.setLastmodified(new Date().getTime())
        if (!articleInstance.save(flush: true)) {
            render(view: "edit", model: [articleInstance: articleInstance])
            return
        }

		flash.message = message(code: 'default.updated.message', args: [message(code: 'article.label', default: 'Article'), articleInstance.id])
        redirect(action: "show", id: articleInstance.id)
    }

    def delete() {
        def articleInstance = Article.get(params.id)
        if (!articleInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'article.label', default: 'Article'), params.id])
            redirect(action: "list")
            return
        }

        try {
            articleInstance.delete(flush: true)
			flash.message = message(code: 'default.deleted.message', args: [message(code: 'article.label', default: 'Article'), params.id])
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
			flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'article.label', default: 'Article'), params.id])
            redirect(action: "show", id: params.id)
        }
    }
}
