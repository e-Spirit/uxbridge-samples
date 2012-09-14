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
/**
 * The uxb_widget module
 * The widget can be loaded with opts like:
 * $(selector).uxb_widget({
 * 		lang:'DE',
 * 		url:"http://www.example.local",
 * 		speed:2000, 
 * 		fadeFrom: "yellow", 
 * 		fadeTo: "white", 
 * 		count: 5, 
 * 		templateString: "<span>${title}</span>"
 * });
 * templateString can be a template for the jQuery tmpl plugin; you can access all fields of the jsonp response
 */
var oldResult = [];
(function($) {
	 $.widget( "ui.uxb_widget", {
		 
		    // These options will be used as defaults
		    options: { 
		      lang:"EN",
		      url:"/widgetExample/rest/v1/articles", 
		      speed:200,
		      fadeFrom:"#F7D358",
		      fadeTo:"white",
		      count:5,
		      templateString: "<span id='${id}' class='tablatest'><a href='{{if url}}${url}{{else}}${id}{{/if}}'>${title}</a></span><br/><br/>"
		    },
		 
		    // Set up the newswidget
		    _create: function() {
		    	$.template("dataLine", this.options.templateString);
				
				this.reload();
				setInterval($.proxy(function () {
					this.reload();
				}, this), this.options.speed);
		    },
		    
		    reload: function() {
		    	/*
				 * The global variable STOPRELOAD is needed for acceptancetests. Refreshing of the DOM will be suppressed in order to solve problems with Selenium
				 */
				if (typeof STOPRELOAD != "undefined" && STOPRELOAD == true) {
					return;
				}
				var parent = this;
				jQuery.ajax({
						dataType: "jsonp",
						url: this.options.url,
						data:{'lang' : this.options.lang, 'count': this.options.count},
						success: function(data, textStatus, jqXHR) {
							
							var dataString = $.tmpl("dataLine", data).get();
							$(parent.element).html(dataString);
							
							var equal;
							if(oldResult.length != data.length) {
								equal = false;
							} else {
								for(var i = 0; i < data.length; i++) {
									equal = (oldResult[i].title == data[i].title);
									if(!equal) {
										break;
									}
								}
							}
							if (!equal && oldResult.length != 0) {
								$(parent.element).color_fade({from: parent.options.fadeFrom, to: parent.options.fadeTo});					
							}
							
							oldResult = data;
						}
				});
		    },
		    
		    set: function(key, value) {
		    	this._setOption(key, value);
		    },
		 
		    // Use the _setOption method to respond to changes to options
		    _setOption: function( key, value ) {
			  //is necessary that an option gets applied immediately
		      switch( key ) {
		      	case "lang":
		        	this.options.lang=value;
		          break;
		        case "url":
		        	this.options.url=value;
		          break;
		        case "speed":
		        	this.options.speed=value;
		          break;
		        case "fadeFrom":
		        	this.options.fadeFrom=value;
		          break;
		        case "fadeTo":
		        	this.options.fadeTo=value;
		          break;
		        case "count":
		        	this.options.count=value;
		          break;
		        case "templateString":
		        	this.options.templateString=value;
		          break;
		      }
		      // In jQuery UI 1.8, you have to manually invoke the _setOption method from the base newswidget
		      $.Widget.prototype._setOption.apply( this, arguments );
		      // In jQuery UI 1.9 and above, you use the _super method instead
			  //this._super( "_setOption", key, value );
		      
		      this.reload();//is necessary that an option gets applied immediately
		    },
		 
		    // Use the destroy method to clean up any modifications your newswidget has made to the DOM
		    destroy: function() {
		      // In jQuery UI 1.8, you must invoke the destroy method from the base newswidget
		      $.Widget.prototype.destroy.call( this );
		      // In jQuery UI 1.9 and above, you would define _destroy instead of destroy and not call the base method
		    }
		  });
})(jQuery);	
