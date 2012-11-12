package com.espirit.moddev.examples.uxbridge.newswidget.jpa;

/*
 * //**********************************************************************
 * uxbridge.samples.newswidget.hibernate
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


import com.espirit.moddev.examples.uxbridge.newswidget.entity.UXBEntity;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

/**
 * This class tests the article handler 
 *
 */
public class ArticleHandlerTest {
    /**
     * The message if the command was successful
     */
	final String okMessage = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?><uxb_entity destinations=\"postgres\"  finishTime=\"1322687803860\"  status=\"" + ArticleHandler.STATUS_OK + "\"  uuid=\"123\"  path=\"\"  command=\"add\"  language=\"de\"  createTime=\"1322687803069\"  startTime=\"0\"  schedulerId=\"123456\" ></uxb_entity>";
    
	/**
	 * This method tests if the generated responseMessage is correct
	 */
    @Test
    public void testOkResponseMessage() {
        final String status = ArticleHandler.STATUS_OK; 
        final String errorMessage = null;
        UXBEntity uxbEntity = mock(UXBEntity.class);
        when(uxbEntity.getUuid()).thenReturn("123");
        when(uxbEntity.getPath()).thenReturn("");
        when(uxbEntity.getCommand()).thenReturn("add");
        when(uxbEntity.getLanguage()).thenReturn("de");
        when(uxbEntity.getCreateTime()).thenReturn(1322687803069L);
        when(uxbEntity.getSchedulerId()).thenReturn("123456");

        ArticleHandler articleHandler = new ArticleHandler();
    }
}
