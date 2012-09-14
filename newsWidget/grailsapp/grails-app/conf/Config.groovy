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
 
if (System.properties["config.location"]) {
	
	/*
	Try to load the external configuration for the context name of the webapp
	Problem: there is no way to get the current context name
	 */

	println("load external configuration")

	/*
	Workaround to detect the context name of the webapp
	 */
	def pathName = getClass().getProtectionDomain().getCodeSource().getLocation().getFile().replace("/WEB-INF/classes/" + getClass().getSimpleName() + ".class", "").substring(1);
	pathName = pathName.substring(pathName.lastIndexOf("/") + 1)

	// if null or empty use the appName, this is useful when running from IDE
	if (pathName == null || pathName == "") {
		pathName = appName
	}
	// if not present, and ending slash
	String configPath = System.properties["config.location"]
	if (!configPath.endsWith("/")) {
		configPath += "/"
	}

	println("loading configuration from: " + configPath + pathName)
	println ("loading file file:${configPath}${pathName}-DataSource.groovy")
	println ("loading file file:${configPath}${pathName}-Config.groovy")

	/*
	give grails some extra configuration files.
	The configuration from these files are merged with the existing configuration, existing properties will be overwritten
	 */
	grails.config.locations = [
			"file:${configPath}${pathName}-DataSource.groovy",
			"file:${configPath}${pathName}-Config.groovy"
	]
	//grails.config.locations << "file:" + System.properties["config.location"]
} else {
	println("using default configuration")

	grails.project.groupId = appName // change this to alter the default package name and Maven publishing destination
	grails.mime.file.extensions = true // enables the parsing of file extensions from URLs into the request format
	grails.mime.use.accept.header = false
	grails.mime.types = [html: ['text/html', 'application/xhtml+xml'],
			xml: ['text/xml', 'application/xml'],
			text: 'text/plain',
			js: 'text/javascript',
			rss: 'application/rss+xml',
			atom: 'application/atom+xml',
			css: 'text/css',
			csv: 'text/csv',
			all: '*/*',
			json: ['application/json', 'text/json'],
			form: 'application/x-www-form-urlencoded',
			multipartForm: 'multipart/form-data'
	]

	// URL Mapping Cache Max Size, defaults to 5000
	//grails.urlmapping.cache.maxsize = 1000

	// The default codec used to encode data with ${}
	grails.views.default.codec = "none" // none, html, base64
	grails.views.gsp.encoding = "UTF-8"
	grails.converters.encoding = "UTF-8"
	// enable Sitemesh preprocessing of GSP pages
	grails.views.gsp.sitemesh.preprocess = true
	// scaffolding templates configuration
	grails.scaffolding.templates.domainSuffix = 'Instance'

	// Set to false to use the new Grails 1.2 JSONBuilder in the render method
	grails.json.legacy.builder = false
	// enabled native2ascii conversion of i18n properties files
	grails.enable.native2ascii = true
	// whether to install the java.util.logging bridge for sl4j. Disable for AppEngine!
	grails.logging.jul.usebridge = true
	// packages to include in Spring bean scanning
	grails.spring.bean.packages = []

	// request parameters to mask when logging exceptions
	grails.exceptionresolver.params.exclude = ['password']

	// set per-environment serverURL stem for creating absolute links
	environments {
		production {
			grails.serverURL = "http://localhost:8080/${appName}"
		}
		development {
			grails.serverURL = "http://localhost:8080/${appName}"
		}
		test {
			grails.serverURL = "http://localhost:8080/${appName}"
		}
		testvm {
			grails.serverURL = "http://localhost:8080/${appName}"
		}
		mongo {
			grails.mongo.server = "localhost"
			grails.mongo.port = 27017
			grails.mongo.username = ""
			grails.mongo.password = ""
			grails.mongo.dbName = ${appName} //widgetExample
			grails.serverURL = "http://localhost:8080/${appName}"
			grails.plugin.excludes = 'hibernate'
		}
	}

	// log4j configuration
	log4j = {
		// Example of changing the log pattern for the default console
		// appender:
		//
		//appenders {
		//    console name:'stdout', layout:pattern(conversionPattern: '%c{2} %m%n')sa
		//}
		appenders {
			rollingFile name: "stacktrace", maxFileSize: 1024, file: "/tmp/stacktrace.log"
		}

		error 'org.codehaus.groovy.grails.web.servlet',  // controllers
				'org.codehaus.groovy.grails.web.pages', // GSP
				'org.codehaus.groovy.grails.web.sitemesh', //  layouts
				'org.codehaus.groovy.grails.web.mapping.filter', // URL mapping
				'org.codehaus.groovy.grails.web.mapping', // URL mapping
				'org.codehaus.groovy.grails.commons', // core / classloading
				'org.codehaus.groovy.grails.plugins', // plugins
				'org.codehaus.groovy.grails.orm.hibernate', // hibernate integration
				'org.springframework',
				'org.hibernate',
				'net.sf.ehcache.hibernate'

		warn 'org.mortbay.log'
	}

	// JQuery
	grails.views.javascript.library = "jquery"
	
	// deployed newsdrilldown url
	firstspirit.newsUrl = "http://localhost:8080"

	
	// Grails Dokumentation
	grails.doc.title = "Widget"
	grails.doc.subtitle = "Example Widget"
	grails.doc.authors = "e-Spirit AG"
	grails.doc.copyright = "&copy;e-Spirit AG"
	grails.doc.logo = "<img src=\"../img/e-spirit-logo.png\" />"
	grails.doc.images = new File("src/docs/img")
	grails.doc.footer = "- All rights reserved."
	grails.gapi.title = "Widget"
	
}
