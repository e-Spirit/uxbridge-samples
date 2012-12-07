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
import grails.util.Environment

import com.espirit.moddev.examples.uxbridge.newsdrilldown.Category
import com.espirit.moddev.examples.uxbridge.newsdrilldown.MetaCategory
import com.espirit.moddev.examples.uxbridge.newsdrilldown.News

class BootStrap {

	def grailsApplication

	private static String subject = "TESTQUEUE"

	def init = { servletContext ->
		if (Environment.current == Environment.DEVELOPMENT || Environment.current == Environment.TEST || Environment.current == Environment.CUSTOM) {
//			developmentData()
		}
	}

	def destroy = {
	}

	def developmentData() {

		if (Environment.current != Environment.PRODUCTION) {
			MetaCategory.findAll().each { temp ->
				temp.delete()
			}
			Category.findAll().each { temp ->
				temp.delete()
			}
			News.findAll().each { temp ->
				temp.delete()
			}
		}

		if (Environment.current != Environment.PRODUCTION) {

			def languages = ["EN", "DE"];

			for (lang in languages) {
				// Create MetaCategories
				MetaCategory sports = new MetaCategory(fs_id: 1, name: lang == "EN" ? "Sports" : "Sport", language: lang, lastmodified: 1).save(flush: true, failOnError: true)
				MetaCategory economy = new MetaCategory(fs_id: 2, name: lang == "EN" ? "Economy" : "Wirtschaft", language: lang, lastmodified: 1).save(flush: true, failOnError: true)

				// Create Categories
				Category soccer = new Category(fs_id : 1, name: lang == "EN" ? "Soccer" : "Fußball", language: lang, lastmodified: 1).addToMetaCategories(sports).save(flush: true, failOnError: true)
				Category sailing = new Category(fs_id : 2, name: lang == "EN" ? "Sailing" : "Segeln", language: lang, lastmodified: 1).addToMetaCategories(sports).save(flush: true, failOnError: true)
				Category dax = new Category(fs_id : 3, name: "DAX", language: lang, lastmodified: 1).addToMetaCategories(economy).save(flush: true, failOnError: true)
				Category wallStreet = new Category(fs_id : 4, name : "Wall Street", language: lang, lastmodified: 1).addToMetaCategories(economy).save(flush: true, failOnError: true)

				
				sports.addToCategories(soccer)
				sports.addToCategories(sailing).save(flush: true, failOnError: true)
				
				economy.addToCategories(dax)
				economy.addToCategories(wallStreet).save(flush: true, failOnError: true)
				
				// Create newsdrilldown articles
				def id_list = [128, 130, 131, 132, 256]

				for (i in id_list) {
					Calendar cal = Calendar.instance
					cal.add(Calendar.MINUTE, (int) i)

					String headline = "This is the title of $i"
					String subheadline = "This is the subheadline of $i"
					String teaser = "This is the teaser of $i"
					if (lang == "DE") {
						headline = "Dies ist der Titel von $i"
						subheadline = "Das ist die SubHeadline für $i"
						teaser = "Das ist der Teaser für $i"
					}
					String url = null
					Long fs_id = null
					Category category = null
					if (i == 128) {
//						headline = "Mithras Energy receives solar prize from the City of Sonningen"
//						subheadline = "Constant innovation pays"
//						teaser = "Mithras Energy was awarded the Sonnigen Solar Prize by Mayor Karl-Heinz Schmidt during an official ceremony. The prize honours the company and its constant commitment to solar energy in German speaking countries. The managing director of Mithras Energy received this prize gratefully: We are pleased that the effort we have been making for years has become known beyond the boundaries of industry and do all that we can to ensure our efforts are communicated even further still."
						url = grailsApplication.config.firstspirit.newsUrl + "/mithras_news_drilldown/content/en/press/pressreleases/press_releases_details_128.html"
						fs_id = 128
						category = soccer
					} else if (i == 130) {
//						headline = "New Director of Mithras Energy"
//						subheadline = "Hans Energie reinforces the executive management of the Sonninger company"
//						teaser = "Since 1 January 2008, the executive management of Mithras Energy Solartechnik GmbH has been reinforced by Hans Energie. This is the company's response to the growth of recent months and reflects the success of the company."
						url = grailsApplication.config.firstspirit.newsUrl + "/mithras_news_drilldown/content/en/press/pressreleases/press_releases_details_130.html"
						fs_id = 130
						category = dax
					} else if (i == 131) {
//						headline = "Solar diversity"
//						subheadline = "Widespread use of mono-crystalline modules"
//						teaser = "Mono-crystalline modules are the so-called all-rounders of the solar industry. Due to their compact design and high output, these modules are optimally suitable for both PV systems for residential use, large systems on pitched roofs or even for mobile use. The anodised housing frame is reinforced and, together with the special hardened front glass, forms an outstanding protection unit for extreme weather conditions."
						url = grailsApplication.config.firstspirit.newsUrl + "/mithras_news_drilldown/content/en/press/pressreleases/press_releases_details_131.html"
						fs_id = 131
						category = dax
					} else if (i == 132) {
//						headline = "New product range"
//						subheadline = "Thin film modules are gaining ground"
//						teaser = "The thin film modules in our new product range are particularly recommended for highly fluctuating weather conditions. Whether in high temperatures or diffuse light conditions, the modules have a consistently uniform production capacity. The amorphous silicon layer enables efficiencies of around 20 % to be achieved as, among other things, light soaking effects are prevented."
						url = grailsApplication.config.firstspirit.newsUrl + "/mithras_news_drilldown/content/en/press/pressreleases/press_releases_details_132.html"
						fs_id = 132
						category = dax
					} else if (i == 256) {
//						headline = "Mithras Energy again awarded the Solar Prize of the City of Sonningen"
//						subheadline = "The thin film modules have now been honoured too"
//						teaser = "As in 2007, Mithras Energy received the Solar Price of the City of Sonningen in 2008 too. This year the accolades were focussed on the thin film modules: the prize was accepted by the management and workforce of Mithras Energy at an official event at the end of the year. The newly developed tandem structure has increased the effectiveness of the modules by 15 %."
						url = grailsApplication.config.firstspirit.newsUrl + "/mithras_news_drilldown/content/en/press/pressreleases/press_releases_details_256.html"
						fs_id = 256
						category = sailing
					}

					def n = new News(fs_id: fs_id, headline: headline, subheadline: subheadline, teaser: teaser, content: "This content belongs to $i", date: cal.time, url: url, language: lang, lastmodified: 1).addToCategories(category)
					if (i == 256) {
						n.addToCategories(soccer);
					}
					n.save(flush: true, failOnError: true)
				}
			}
		}
	}
}



