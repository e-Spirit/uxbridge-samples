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
grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
grails.project.source.level = 1.6
//grails.project.war.file = "target/${appName}-${appVersion}.war"
grails.project.dependency.resolution = {
    // inherit Grails' default dependencies
    inherits("global") {
        // uncomment to disable ehcache
        // excludes 'ehcache'
    }
    log "warn" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
    repositories {
        grailsPlugins()
        grailsHome()
        grailsCentral()
        // uncomment the below to enable remote dependency resolution
        // from public Maven repositories
        //mavenLocal()
        //mavenCentral()
        //mavenRepo "http://snapshots.repository.codehaus.org"
        //mavenRepo "http://repository.codehaus.org"
        //mavenRepo "http://download.java.net/maven/2/"
        //mavenRepo "http://repository.jboss.com/maven2/"

	mavenRepo "http://artifactory.e-spirit.de/artifactory/repo"
    }
    dependencies {
        // specify dependencies here under either 'build', 'compile', 'runtime', 'test' or 'provided' scopes eg.

        // runtime 'mysql:mysql-connector-java:5.1.13'
        compile 'antlr:antlr:2.7.7'
		//runtime 'org.apache.activemq:activemq-core:5.5.1'

		compile 'postgresql:postgresql:8.4-702.jdbc4'
		runtime 'mysql:mysql-connector-java:5.1.18'

        //runtime 'org.slf4j:slf4j-api:1.6.1'
        //runtime 'org.slf4j:slf4j-log4j12:1.6.1'
        //runtime 'log4j:log4j:1.2.16'

    }
	
	plugins {
		runtime ":hibernate:2.1.0"
		runtime ":jquery:1.7.1"
		runtime ":resources:1.1.6"

		compile ":webxml:1.4.1"

		build ":tomcat:2.1.0"
		test ":spock:0.6"
	}

    //needed to avoid classloading conflicts on tomcat7
    grails.war.resources = { stagingDir ->
            delete(file:"${stagingDir}/WEB-INF/lib/slf4j-api-1.5.2.jar")
            delete(file:"${stagingDir}/WEB-INF/lib/antlr-2.7.6.jar")
    }
}
