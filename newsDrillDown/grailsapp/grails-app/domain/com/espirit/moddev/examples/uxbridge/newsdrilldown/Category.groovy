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
package com.espirit.moddev.examples.uxbridge.newsdrilldown

/**
 * The Category contains the typical Mithras Energy category fields.
 * This includes the id and the name.
 *
 */

class Category {

	Long fs_id
	String name
	String language
	Long lastmodified=new Date().getTime()

	static belongsTo = MetaCategory
	static hasMany = [news:News, metaCategories:MetaCategory]

	static mapping = {
		news joinTable: [name: "category_news", key: 'category_id' ]
		//collection "newsCategory"
	}
	
	static constraints = {
		name(blank: false)
		metaCategories(nullable: false, blank: false)
        lastmodified(blank: false)
		news(nullable: true, blank: true)
		fs_id index: "fsidindex2,IDX_fsid_lang_2"
		language index: "IDX_fsid_lang_2"
	}

	String toString() {
		return name
	}
}