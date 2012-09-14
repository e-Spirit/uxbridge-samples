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

/**
 * Article
 *
 * The Article
 *
 */
class Article {

	/**
	 the title
	 */
	String title
	/**
	 the content
	 */
	String content
	/**
	 date of production
	 */
	Date created
    /**
	 date of last modification
	 */
	Long lastmodified
	/**
	 the URL for linking to FirstSpirit
	 */
	String url
	/**
	 the language of the article
	 */
	String language
	/**
	 * the FirstSpirit id of the article
	 */
	Long aid

	static mapping = {
		content type: "text"
		table "article"
		aid index: "aidindex"
	}

	static constraints = {
		title(blank: false)
		content(blank: false)
		created(blank: false)
        lastmodified(blank: false)
		url(blank: true, nullable: true)
		aid(blank: false, nullable: false, unique: true)
	}
}
