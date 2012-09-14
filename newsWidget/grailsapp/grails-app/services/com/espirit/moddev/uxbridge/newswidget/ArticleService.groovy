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

class ArticleService {

	static transactional = false

	/**
	 * returns the newest articles
	 * @param count the number of the articles to return
	 * @return list of the newest articles
	 */
	def getLatestArticles(int count, String language) {
		def articleList = []
		def candidates = Article.findAllByLanguage(language, [sort: 'created', order: 'desc', max: count])

		candidates.each { art ->
			def article = art
			article.discard()
			article.title = ellipsis(article.title, 20)
			articleList.add article
		}
		
		return articleList
	}

	/**
	 * reduces a text to the given length
	 * @param text the text to reduce
	 * @param length the length the text should be reduced to
	 * @return the reduced text ending with '...'
	 */
	def ellipsis(final String text, int length) {
		// The letters [iIl1] are slim enough to only count as half a character.
		length += Math.ceil(text.replaceAll("[^iIl]", "").length() / 2.0d)
		
		if (text.length() > length + 20) {
			return text.substring(0, length - 3) + "..."
		}
		
		return text
	}
	
}