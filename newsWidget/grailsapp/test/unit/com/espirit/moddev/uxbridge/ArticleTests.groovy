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
package com.espirit.moddev.uxbridge

import grails.test.*

class ArticleTests extends GrailsUnitTestCase {
    protected void setUp() {
        super.setUp()
    }

    protected void tearDown() {
        super.tearDown()
    }

    void testConstraints() {
		mockForConstraintsTests(Article)


		Article a = new Article(aid: 3, title: "t", created: new Date(), content: "c", language: "DE", lastmodified: 0)

		assertTrue a.validate()
    }

	void testGetSetID () {
		Article a = new Article()
		a.id = 1

		assertEquals("id not set", 1l, a.id)
	}

	void testGetSetTitle () {
		Article a = new Article()
		a.title = "ein Titel"

		assertEquals("title not set", "ein Titel", a.title)
	}

	void testGetSetCreated () {
		Date created = new Date()

		Article a = new Article()
		a.created = created

		assertEquals("created not set", created, a.created)
	}

	void testGetSetContent () {
		Article a = new Article()
		a.content = "der inhalt"

		assertEquals("content not set", "der inhalt", a.content)
	}

    void testGetSetLastModified () {
		Article a = new Article()
		a.setLastmodified(1)

		assertEquals("lastmodified not set", 1l, a.lastmodified)
	}

	void testSave() {
    	def testInstances=[]
    	mockDomain(Article, testInstances)
    	assertEquals(0, Article.count())
    	new Article(aid: 3, title: "t", created: new Date(), content: "c", url: "none", language: "DE", lastmodified: 0).save()
    	assertEquals(1, Article.count())
}
}
