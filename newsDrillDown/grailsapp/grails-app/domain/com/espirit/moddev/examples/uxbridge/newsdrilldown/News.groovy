package com.espirit.moddev.examples.uxbridge.newsdrilldown

import com.sun.java.util.jar.pack.ConstantPool.Index;

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


/**
 * The News contains the typical Mithras Energy press releases fields.
 * This includes the FirstSpirit id of the article, the language, the url,
 * the date, the headline, the subheadline, the teaser and the content.
 *
 */
class News implements Comparable<News> {

	Long fs_id
	String language
	String url
	Date date
	String headline
	String subheadline
	String teaser
	String content
	Long lastmodified

	static mapping = {
		content type: "text"
		teaser type: "text"
		table "newsdrilldown"
		fs_id index: "fsidindex1,IDX_fsid_lang_1"
		language index : "IDX_fsid_lang_1"
	}

	static belongsTo = Category
	static hasMany = [categories:Category]

	static constraints = {
		fs_id(blank: false, nullable: false)
		language(blank:false)
		url(blank: true, nullable: true)
		date(blank: false)
		headline(blank: false)
		subheadline(nullable: true, blank: true)
		teaser(nullable: true, blank: true)
		content(blank: false)
		categories(nullable: false, blank: false)
        lastmodified(blank: false)
	}

	String toString() {
		return headline
	}
	@Override
	public int compareTo(News o) {
		if (o.getFs_id() != getFs_id()) {
			return 1;
		}
		if (o.getLanguage() != getLanguage()) {
			return 1;
		}
		return 0;
	}
	
}