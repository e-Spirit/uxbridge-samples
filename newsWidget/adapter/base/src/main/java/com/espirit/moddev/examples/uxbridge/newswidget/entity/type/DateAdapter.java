package com.espirit.moddev.examples.uxbridge.newswidget.entity.type;

/*
 * //**********************************************************************
 * uxbridge.samples.newswidget.base
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


import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.bind.annotation.adapters.XmlAdapter;

// TODO: Auto-generated Javadoc
/**
 * The Class DateAdapter.
 */
public class DateAdapter extends XmlAdapter<String, Date> {

	/** The date format which follows the pattern: 2010-12-30T11:00:31MEZ */
	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");


	/**
	 * Instantiates a new date adapter.
	 */
	public DateAdapter () {
	}

    /* (non-Javadoc)
     * @see javax.xml.bind.annotation.adapters.XmlAdapter#marshal(java.lang.Object)
     */
    @Override
    public String marshal(Date v) throws Exception {
        return dateFormat.format(v);
    }

    /* (non-Javadoc)
     * @see javax.xml.bind.annotation.adapters.XmlAdapter#unmarshal(java.lang.Object)
     */
    @Override
    public Date unmarshal(String v) throws Exception {
        return dateFormat.parse(v);
    }
}
