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
if (System.properties['config.location']) {
// nothing to do, settings get processed in Config.groovy
} else {

println("using default configuration")

dataSource {
	pooled = true
//	driverClassName = "org.hsqldb.jdbcDriver"
driverClassName = "org.h2.Driver"
dialect = org.hibernate.dialect.H2Dialect
	username = "sa"
	password = ""
}
hibernate {
	cache.use_second_level_cache = true
	cache.use_query_cache = true
	cache.provider_class = 'net.sf.ehcache.hibernate.EhCacheProvider'
}
// environment specific settings
environments {
	development {
		
		dataSource {
			dbCreate = "create-drop"
			url = "jdbc:h2:mem:devDb;MVCC=TRUE"
			driverClassName = "org.h2.Driver"
			username = "sa"
			password = ""
		}
		
	}
	test {
		dataSource {
			dbCreate = "create-drop"
			 url = "jdbc:h2:file:devDb;MVCC=TRUE"
			driverClassName = "org.h2.Driver"
			username = "sa"
			password = ""

		}
	}


	testvm {
		dataSource {
				dbCreate = "create-drop"
				url = "jdbc:postgresql://localhost/widgetexample"
				driverClassName = "org.postgresql.Driver"
				dialect = org.hibernate.dialect.PostgreSQLDialect
				username = "postgres"
				password = "postgres"
			}
	}


	production {
		dataSource {
			dbCreate = "update" // one of 'create', 'create-drop','update'
			url = "jdbc:postgresql://localhost/news_widget?useUnicode=yes&characterEncoding=UTF-8"
			driverClassName = "org.postgresql.Driver"
			dialect = org.hibernate.dialect.PostgreSQLDialect
			username = "postgres"
			password = "postgres"
		}
	}
	}
}