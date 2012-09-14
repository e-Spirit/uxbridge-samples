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
(function($) {
	/*

	 Jquery Color Fade Plugin
	 ------------------------

	 REQUIREMENTS:

	 - Jquery 1.3.2 or higher
	 - Jquery UI 1.7.2 or higher

	 Creates a color fade technique made popular by 37signals' Yellow Fade Technique

	 USAGE:

	 color_fade({from:"yellow",to"white",speed:500})
	 - from : color that element will fade from
	 - to : color that element will fade to
	 - speed : the speed of the animation

	 */

	$.fn.color_fade = function(opts) {

		opts = $.extend({from:"yellow",to:"white",speed:1000}, opts || {});

		$(this).css('background-color', opts['to']);
		$(this).animate(
				{ backgroundColor:opts['from'] },
				{ duration: 200,
					easing: "linear",
					complete: function() {
						$(this).delay(200).animate(
								{ backgroundColor:opts['to'] },
								{ duration: opts['speed'],
									easing: "linear",
									complete: function() {
										$(this).animate({backgroundColor: opts['to']}, opts['speed'])
									} });
					} });

	}


})(jQuery);