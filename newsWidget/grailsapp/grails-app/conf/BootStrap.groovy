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
import com.espirit.moddev.uxbridge.Article
import grails.util.Environment
import com.mongodb.Mongo
import com.mongodb.DBAddress
import com.mongodb.DB

class BootStrap {

	def grailsApplication

	private static String subject = "TESTQUEUE"

	def init = { servletContext ->
		if (Environment.current == Environment.DEVELOPMENT || Environment.current == Environment.TEST || Environment.current == Environment.CUSTOM) {
		//	developmentData()
		}
	}

	def destroy = {
	}

	def developmentData() {

		if (grailsApplication.config.grails.plugin.excludes == "hibernate") {
			DBAddress address = new DBAddress(grailsApplication.config.grails.mongo.server, grailsApplication.config.grails.mongo.port, grailsApplication.config.grails.mongo.dbName)
			Mongo mongo = new Mongo(address)
			DB db = mongo.getDB(grailsApplication.config.grails.mongo.dbName)
			if(grailsApplication.config.grails.mongo.username != "" && grailsApplication.config.grails.mongo.password != "") {
				db.authenticate(grailsApplication.config.grails.mongo.username, grailsApplication.config.grails.mongo.password.toCharArray())
			}
			if (Environment.current != Environment.PRODUCTION) {
				db.getCollection("article").drop()
				db.getCollection("article.next_id").drop()
			}
		}

		if (Environment.current != Environment.PRODUCTION) {
			Article.findAll().each {temp ->
				temp.delete()
			}
		}

		if (Environment.current != Environment.PRODUCTION) {
			def id_list = [704, 256, 132, 131, 130, 128]

			for (i in id_list) {

				Calendar cal = Calendar.instance
				cal.add(Calendar.MINUTE, (int) i)

				String title = "This is the title of $i"
				String url = null
				Long aid = null;
				if (i == 128) {
					title = "Mithras Energy receives solar prize from the City of Sonningen"
					url = grailsApplication.config.firstspirit.newsUrl + "/mithras/content/de/press/pressreleases/pressreleasesdetails_704.html"
					aid = 128
				} else if (i == 704) {
					title = "Achievable optimum"
					url = grailsApplication.config.firstspirit.newsUrl + "/mithras/content/de/press/pressreleases/pressreleasesdetails_704.html"
					aid = 704
				} else if (i == 132) {
					title = "New product range"
					url = grailsApplication.config.firstspirit.newsUrl + "/mithras/content/de/press/pressreleases/pressreleasesdetails_132.html"
					aid = 132
				} else if (i == 131) {
					title = "Solar diversity"
					url = grailsApplication.config.firstspirit.newsUrl + "/mithras/content/de/press/pressreleases/pressreleasesdetails_131.html"
					aid = 131
				} else if (i == 130) {
					title = "New Director of Mithras Energy"
					url = grailsApplication.config.firstspirit.newsUrl + "/mithras/content/de/press/pressreleases/pressreleasesdetails_130.html"
					aid = 130
				} else if (i == 256) {
					title = "Mithras Energy again awarded the Solar Prize of the City of Sonningen"
					url = grailsApplication.config.firstspirit.newsUrl + "/mithras/content/de/press/pressreleases/pressreleasesdetails_256.html"
					aid = 256
				}

				def a = new Article(aid: aid, title: title, content: "This content belongs to $i", created: cal.time, url: url, language: "EN", lastmodified: 0)
				a.save(failOnError:true)
			}
		}
	}
}



