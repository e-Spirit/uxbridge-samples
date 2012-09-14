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


class RenderTagLib {
	def renderService
	
	def renderHeader={ attrs, body ->
		if (attrs.lang == "DE") {
			out<<renderService.renderHTML(grailsApplication.config.grails.header.de)
		} else {
			out<<renderService.renderHTML(grailsApplication.config.grails.header.en)
		}
	}

	def renderFooter={ attrs, body ->
		if (attrs.lang == "DE") {
			out<<renderService.renderHTML(grailsApplication.config.grails.footer.de)
		} else {
			out<<renderService.renderHTML(grailsApplication.config.grails.footer.en)
		}
	}
	def renderNavLeft={ attrs, body ->
		if (attrs.lang == "DE") {
			out<<renderService.renderHTML(grailsApplication.config.grails.navLeft.de)
		} else {
			out<<renderService.renderHTML(grailsApplication.config.grails.navLeft.en)
		}
		
	}
    /**
     * @attr url
     */
	def renderHTMLsnippet={attrs->
		out << renderService.renderHTML(attrs.url)
	}
}
