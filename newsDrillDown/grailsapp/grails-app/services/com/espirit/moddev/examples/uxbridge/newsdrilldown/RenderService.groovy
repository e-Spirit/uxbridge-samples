package com.espirit.moddev.examples.uxbridge.newsdrilldown;

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

import javax.net.ssl.HttpsURLConnection

import java.net.*;

class RenderService {
	InputStream is = null;
	String s = null;
	HttpsURLConnection connection;
	static transactional = true

	def renderHTML(String url) {
		try{
			if(url.startsWith("https://")){

				java.security.Security.addProvider(
						new com.sun.net.ssl.internal.ssl.Provider());

				System.setProperty(
						"java.protocol.handler.pkgs",
						"com.sun.net.ssl.internal.www.protocol");

				Authenticator.setDefault (new PasswordAuthenticator());
			}
			URL toRead = new URL(url);
			is=toRead.openStream();
			s=new Scanner(is,"UTF-8").useDelimiter("\\Z").next();
		}
		catch (MalformedURLException e) {
			System.out.println("Can't get HTML");
		} finally {
			if (is != null && connection!=null)
				try {
					connection.disconnect()
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}

		return s.trim();
	}
}

