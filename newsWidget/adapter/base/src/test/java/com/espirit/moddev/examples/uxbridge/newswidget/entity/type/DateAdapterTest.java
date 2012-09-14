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


import org.junit.Test;

import java.util.Calendar;
import java.util.TimeZone;

import static org.junit.Assert.*;

/**
 * This class tests the date adapter
 *
 */
public class DateAdapterTest {
    @Test
    public void testMarshal() throws Exception {

    }

    /**
     * Tests if the given date '2010-12-30T11:00:31+0100' is returned in the correct format
     * @throws Exception
     */
    @Test
    public void testUnmarshal() throws Exception {
        DateAdapter dateAdapter = new DateAdapter();
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

        assertEquals(calendar.getTimeInMillis(),dateAdapter.unmarshal("2010-12-30T11:00:31+0100").getTime());
        assertNotNull(dateAdapter.unmarshal("2010-12-30T11:00:31+0100"));

    }
}
