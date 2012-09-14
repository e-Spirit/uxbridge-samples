package com.espirit.moddev.examples.uxbridge.newsdrilldown.test;

/*
 * //**********************************************************************
 * uxbridge.samples.newsdrilldown.hibernate
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


import org.apache.camel.CamelContext;
import org.apache.camel.test.junit4.CamelTestSupport;

import java.io.File;
import java.io.FileNotFoundException;

// TODO: Auto-generated Javadoc
/**
 * This class provides general methods for UXBridge testing purposes
 * 
 * TODO this class should be moved to the test directory, because it's only used within the test scope
 * it's here, because other submodules are using this class as well
 * RBr: already done or still to do?
 */
public abstract class BaseTest extends CamelTestSupport {

	/**
	 * Gets the base path.
	 *
	 * @param projectName the project name
	 * @return the base path
	 * @throws Exception the exception
	 */
	protected static String getBasePath (String projectName) throws Exception {
		String currentDirName =  new File(".").getCanonicalPath().toLowerCase();

		if (currentDirName.contains(projectName)) {
			return "";
		}
		return projectName+"/";
	}

	/**
	 * Gets the content.
	 *
	 * @param filename the filename
	 * @param projectName the project name
	 * @return the content
	 * @throws Exception the exception
	 */
	protected String getContent (String filename, String projectName) throws Exception {
		String currentDirName =  new File(".").getCanonicalPath().toLowerCase();
		System.out.println("loading content from " + currentDirName);
		File target = new File(projectName + "/" + filename);
		if (target == null || currentDirName.contains(projectName)) {
			target = new File(filename);
		}
        System.out.println("loading content from file " + target.getAbsolutePath());
        if(!target.exists()) {
            throw new FileNotFoundException("File not found! " + target.getAbsolutePath());
        }

		return getContext().getTypeConverter().convertTo(String.class, target);
	}

	/**
	 * Gets the context.
	 *
	 * @return the context
	 */
	abstract public CamelContext getContext ();
}
