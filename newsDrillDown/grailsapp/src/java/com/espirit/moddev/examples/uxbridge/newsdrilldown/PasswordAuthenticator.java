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


import java.net.Authenticator;
import java.net.PasswordAuthentication;

public class PasswordAuthenticator extends Authenticator {

	protected PasswordAuthentication getPasswordAuthentication() {
//	        System.out.println("getPasswordAuthentication() called for https connection!!!");
		
	        //insert user and password here
	    	return new PasswordAuthentication("user", "password".toCharArray());
	    }

}