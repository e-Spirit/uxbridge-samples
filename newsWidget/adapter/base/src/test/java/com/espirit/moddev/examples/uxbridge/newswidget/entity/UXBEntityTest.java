package com.espirit.moddev.examples.uxbridge.newswidget.entity;

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


import com.espirit.moddev.examples.uxbridge.newswidget.test.BaseTest;
import org.apache.camel.CamelContext;
import org.junit.Test;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.util.Calendar;
import java.util.TimeZone;

import static org.junit.Assert.assertEquals;

/**
 * This class tests if the JAXB class UXBEntity fits to the given xml so that an object can be build successfully.
 *
 */
public class UXBEntityTest extends BaseTest {

	/**
	 * Tests if parsing is working
	 * @throws Exception
	 */
	@Test
	public void testParseEntireDocument() throws Exception {
		JAXBContext ctx = JAXBContext.newInstance(new Class[]{UXBEntity.class});
		Unmarshaller um = ctx.createUnmarshaller();
		String content = getContent("src/test/resources/inbox/pressreleasesdetails_128.xml", "base");
		UXBEntity entity = (UXBEntity) um.unmarshal(new StringReader(content));

		assertNotNull("Content not loaded", entity);

		assertNotNull(entity.getUxb_content());
		assertNotNull(entity.getUxb_content().getFs_id());
        assertEquals("FS_ID doesn't match", entity.getUxb_content().getFs_id(), "128");

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2010);
        calendar.set(Calendar.MONTH, Calendar.DECEMBER);
        calendar.set(Calendar.DAY_OF_MONTH, 30);
        calendar.set(Calendar.HOUR_OF_DAY, 10);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 31);
        calendar.set(Calendar.MILLISECOND,0);

        TimeZone timeZone = TimeZone.getTimeZone("MEZ");
        calendar.setTimeZone(timeZone);

        assertEquals("Date not correctly loaded", calendar.getTimeInMillis(), entity.getUxb_content().getDate().getTime());
	}

	/**
	 * Helper method to get the context
	 */
	@Override
	public CamelContext getContext() {
		return context;
	}
}
