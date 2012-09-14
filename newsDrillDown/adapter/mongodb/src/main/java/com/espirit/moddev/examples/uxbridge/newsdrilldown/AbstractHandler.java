package com.espirit.moddev.examples.uxbridge.newsdrilldown;

/*
 * //**********************************************************************
 * uxbridge.samples.newsdrilldown.mongodb
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


import com.espirit.moddev.examples.uxbridge.newsdrilldown.entity.UXBEntity;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.commons.lang3.StringEscapeUtils;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Abstract base class for a handler bean
 * <p/>
 * Extend this class to inherit some helper methods to create
 * the status response message for FirstSpirit
 */
public abstract class AbstractHandler {

	/** The Constant STATUS_OK. */
	public static final String STATUS_OK = "OK";
	
	/** The Constant STATUS_FAIL. */
    public static final String STATUS_FAIL = "FAIL";

    /**
     * Send status message.
     * 
     * @param context      The camel context
     * @param channel      The channel
     * @param status       The status
     * @param errorMessage Error message, can be null
     * @param entity       UXBEntity, can be null
     * @param destination  name of the destination the original message was send to
     */
    protected void sendStatusMessage(CamelContext context, String channel, String status, final String destination, String errorMessage, UXBEntity entity) {
        ProducerTemplate template = context.createProducerTemplate();

        String message = createResponseMessage(status, destination, errorMessage, entity, System.currentTimeMillis());

        template.sendBody(channel, message);
    }

    /**
     * Send status message.
     * 
     * @param context      The camel context
     * @param channel      The channel
     * @param status       The status
     * @param destination  Name of the destination the original message was send to
     * @param errorMessage Error message, could be null
     */
    protected void sendStatusMessage(CamelContext context, String channel, String status, final String destination, String errorMessage) {
        sendStatusMessage(context, channel, status, destination, errorMessage, null);
    }


    /**
     * Creates the response message.
     *
     * @param status the status
     * @param destination the destination
     * @param errorMessage the error message
     * @param entity the entity
     * @param currentTime the current time
     * @return the string
     */
    public static String createResponseMessage(final String status, final String destination, final String errorMessage, final UXBEntity entity, final long currentTime) {
        StringBuilder message = new StringBuilder();

        message.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
        message.append("<uxb_entity");

        message.append(" destinations=\"").append(destination).append("\" ");
        message.append(" finishTime=\"").append(currentTime).append("\" ");
        message.append(" status=\"").append(status).append("\" ");
        if (entity != null) {
            message.append(" uuid=\"").append(entity.getUuid()).append("\" ");
            message.append(" path=\"").append(entity.getPath()).append("\" ");
            message.append(" command=\"").append(entity.getCommand()).append("\" ");
            message.append(" language=\"").append(entity.getLanguage()).append("\" ");
            message.append(" createTime=\"").append(entity.getCreateTime()).append("\" ");
            message.append(" startTime=\"").append(entity.getStartTime()).append("\" ");
            message.append(" schedulerId=\"").append(entity.getSchedulerId()).append("\" ");
        }


        message.append(">");
        if (errorMessage != null) {
            message.append("<errorMessage>").append(StringEscapeUtils.escapeXml(errorMessage)).append("</errorMessage>");
        }

        message.append("</uxb_entity>");
        return message.toString();
    }

    /**
     * Generates a string containing the stacktrace
     *
     * @param e The Exception
     * @return The stacktrace
     */
    protected String stackToString(Exception e) {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }
}
