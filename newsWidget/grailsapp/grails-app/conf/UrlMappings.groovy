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
class UrlMappings {

	static mappings = {

		"/rest/v1/articles"(controller:"articleRest") {
       		action = [GET:"list"]
   		}

		"/rest/v1/article/$id"(controller:"articleRest") {
       		action = [GET:"show"]
   		}

		"/$controller/$action?/$id?"{
			constraints {
				// apply constraints here
			}
		}

		"/"(view:"/index")
		"500"(view:'/error')
	}
}
