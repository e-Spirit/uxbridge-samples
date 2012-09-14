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
 * The MetaCategory contains the typical Mithras Energy metacategory fields.
 * This includes the id, the name and the language.
 *
 */

class MetaCategory {

	Long fs_id
	String name
	String language
	Long lastmodified

	static hasMany = [categories:Category]

	static mapping = {
		table "metaCategory"
		fs_id index: "fsidindex3,IDX_fsid_lang_3"
		language index: "IDX_fsid_lang_3"
	}

    static constraints = {
		name(blank: false)
		categories(nullable: true, blank: true)
        lastmodified(blank: false)
    }

	String toString() {
		return name
	}
}