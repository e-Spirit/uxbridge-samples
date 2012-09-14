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

	// locations to search for config files that get merged into the main config;
	// config files can be ConfigSlurper scripts, Java properties files, or classes
	// in the classpath in ConfigSlurper format
	
	// grails.config.locations = [ "classpath:${appName}-config.properties",
	//                             "classpath:${appName}-config.groovy",
	//                             "file:${userHome}/.grails/${appName}-config.properties",
	//                             "file:${userHome}/.grails/${appName}-config.groovy"]
	
	// if (System.properties["${appName}.config.location"]) {
	//    grails.config.locations << "file:" + System.properties["${appName}.config.location"]
	// }
	
	grails.project.groupId = appName // change this to alter the default package name and Maven publishing destination
	grails.mime.file.extensions = true // enables the parsing of file extensions from URLs into the request format
	grails.mime.use.accept.header = false
	grails.mime.types = [
	    all:           '*/*',
	atom:          'application/atom+xml',
	css:           'text/css',
	csv:           'text/csv',
	form:          'application/x-www-form-urlencoded',
	html:          ['text/html','application/xhtml+xml'],
	js:            'text/javascript',
	json:          ['application/json', 'text/json'],
	multipartForm: 'multipart/form-data',
	rss:           'application/rss+xml',
	text:          'text/plain',
	xml:           ['text/xml', 'application/xml']
	]
	
	// URL Mapping Cache Max Size, defaults to 5000
	//grails.urlmapping.cache.maxsize = 1000
	
	// What URL patterns should be processed by the resources plugin
	grails.resources.adhoc.patterns = ['/images/*', '/css/*', '/js/*', '/plugins/*']
	
	// The default codec used to encode data with ${}
	grails.views.default.codec = "html" // none, html, base64
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
	// packages to include in Spring bean scanning
	grails.spring.bean.packages = []
	// whether to disable processing of multi part requests
	grails.web.disable.multipart=false
	
	// request parameters to mask when logging exceptions
	grails.exceptionresolver.params.exclude = ['password']
	
	// configure auto-caching of queries by default (if false you can cache individual queries with 'cache: true')
	grails.hibernate.cache.queries = false
	
	environments {
	    development {
	        grails.logging.jul.usebridge = true
	    }
	    production {
	        grails.logging.jul.usebridge = false
	        // TODO: grails.serverURL = "http://www.changeme.com"
	    }
	}
	
	// log4j configuration
	log4j = {
	    // Example of changing the log pattern for the default console appender:
	//
	//appenders {
	//    console name:'stdout', layout:pattern(conversionPattern: '%c{2} %m%n')
	//}
//		debug    'org.hibernate.SQL'
	
	error  'org.codehaus.groovy.grails.web.servlet',        // controllers
	   		'org.codehaus.groovy.grails.web.pages',          // GSP
	   'org.codehaus.groovy.grails.web.sitemesh',       // layouts
	   'org.codehaus.groovy.grails.web.mapping.filter', // URL mapping
	   'org.codehaus.groovy.grails.web.mapping',        // URL mapping
	   'org.codehaus.groovy.grails.commons',            // core / classloading
	   'org.codehaus.groovy.grails.plugins',            // plugins
	   'org.codehaus.groovy.grails.orm.hibernate',      // hibernate integration
	   'org.springframework',
	   'org.hibernate',
	   'net.sf.ehcache.hibernate'
	}
	
	// deployed newsdrilldown url
	firstspirit.newsUrl = "http://pm-liveserver-prod:8080"
	
	grails.header.en="http://pm-liveserver-prod:8080/mithras_news_drilldown/content/en/press/pressreleases/footer___header/header.html"
	grails.footer.en="http://pm-liveserver-prod:8080/mithras_news_drilldown/content/en/press/pressreleases/footer___header/footer.html"
	grails.navLeft.en="http://pm-liveserver-prod:8080/mithras_news_drilldown/content/en/press/pressreleases/footer___header/second_navigation.html"
	
	grails.header.de="http://pm-liveserver-prod:8080/mithras_news_drilldown/content/de/press/pressreleases/footer___header/header.html"
	grails.footer.de="http://pm-liveserver-prod:8080/mithras_news_drilldown/content/de/press/pressreleases/footer___header/footer.html"
	grails.navLeft.de="http://pm-liveserver-prod:8080/mithras_news_drilldown/content/de/press/pressreleases/footer___header/second_navigation.html"

}