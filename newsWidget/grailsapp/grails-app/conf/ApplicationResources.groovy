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
def jqver = org.codehaus.groovy.grails.plugins.jquery.JQueryConfig.SHIPPED_VERSION

modules = {
    application {
		defaultBundle 'ui' 
		
        resource url:'js/application.js', disposition: 'head'
    }
	
	master {
		defaultBundle 'ui'
		
		dependsOn 'jquery2'
		
		resource url:'/js/jquery-ui-1.8.16.custom.min.js', disposition: 'head'
		resource url:'/js/jquery.tmpl.js', disposition: 'head'
		resource url:'/js/jquery.color_fade.js', disposition: 'head'
		resource url:'/js/uxb_widget.js', disposition: 'head'
		
		resource url:'/css/custom-theme/jquery-ui-1.8.16.custom.css', disposition: 'head'
		resource url:'/css/newswidget.css', disposition: 'head'
	}
	
	jquery2 {
		defaultBundle 'ui'
		
		resource plugin: 'jquery', url:[plugin: 'jquery', dir:'js/jquery', file:"jquery-${jqver}.min.js"], disposition: 'head'
	}
}