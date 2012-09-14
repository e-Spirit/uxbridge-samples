package com.espirit.moddev.examples.uxbridge.newsdrilldown.entity;

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


import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * This class represents the xml defined in the FirstSpirit presentation channel.
 * To send data back to FirstSpirit there are some extra attributes like finishTime, startTime, status and errorMessage.
 * They tell the UXBService how long processing takes and when something goes wrong what was going wrong.
 */
@XmlRootElement(name = "uxb_entity")
@XmlAccessorType(XmlAccessType.FIELD)
public class UXBEntity {
	
	/** The uuid. */
	@XmlAttribute
	private String uuid;
	
	/** The language. */
	@XmlAttribute
	private String language;
	
	/** The destinations. */
	@XmlAttribute
	private String destinations;
	
	/** The uxb_content. */
	@XmlElement(type = UXBContent.class)
	private UXBContent uxb_content;
	
	/** The command. */
	@XmlAttribute
	private String command;
	
	/** The create time. */
	@XmlAttribute
	private long createTime;
	
	/** The finish time. */
	@XmlAttribute
	private long finishTime;
	
	/** The scheduler id. */
	@XmlAttribute
	private String schedulerId;
	
	/** The status. */
	@XmlAttribute
	private String status;
	
	/** The error message. */
	@XmlElement
	private String errorMessage;
    
    /** The start time. */
    @XmlAttribute
    private long startTime;
    
    /** The path. */
    @XmlAttribute
    private String path;

    /**
     * Instantiates a new uXB entity.
     */
    public UXBEntity () {
	}

	/**
	 * Gets the finish time.
	 *
	 * @return the finish time
	 */
	public long getFinishTime() {
		return finishTime;
	}

	/**
	 * Sets the finish time.
	 *
	 * @param finishTime the new finish time
	 */
	public void setFinishTime(long finishTime) {
		this.finishTime = finishTime;
	}

	/**
	 * Gets the creates the time.
	 *
	 * @return the creates the time
	 */
	public long getCreateTime() {
		return createTime;
	}

	/**
	 * Gets the error message.
	 *
	 * @return the error message
	 */
	public String getErrorMessage() {
		return errorMessage;
	}

	/**
	 * Sets the error message.
	 *
	 * @param errorMessage the new error message
	 */
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	/**
	 * Sets the creates the time.
	 *
	 * @param createTime the new creates the time
	 */
	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

	/**
	 * Gets the scheduler id.
	 *
	 * @return the scheduler id
	 */
	public String getSchedulerId() {
		return schedulerId;
	}

	/**
	 * Sets the scheduler id.
	 *
	 * @param schedulerId the new scheduler id
	 */
	public void setSchedulerId(String schedulerId) {
		this.schedulerId = schedulerId;
	}

	/**
	 * Gets the status.
	 *
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * Sets the status.
	 *
	 * @param status the new status
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * Gets the uxb_content.
	 *
	 * @return the uxb_content
	 */
	public UXBContent getUxb_content() {
		return uxb_content;
	}

	/**
	 * Sets the uxb_content.
	 *
	 * @param uxb_content the new uxb_content
	 */
	public void setUxb_content(UXBContent uxb_content) {
		this.uxb_content = uxb_content;
	}
	
	/**
	 * Gets the uuid.
	 *
	 * @return the uuid
	 */
	public String getUuid() {
		return uuid;
	}

	/**
	 * Sets the uuid.
	 *
	 * @param uuid the new uuid
	 */
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	/**
	 * Gets the language.
	 *
	 * @return the language
	 */
	public String getLanguage() {
		return language;
	}

	/**
	 * Sets the language.
	 *
	 * @param language the new language
	 */
	public void setLanguage(String language) {
		this.language = language;
	}

	/**
	 * Gets the destinations.
	 *
	 * @return the destinations
	 */
	public String getDestinations() {
		return destinations;
	}

	/**
	 * Sets the destinations.
	 *
	 * @param destinations the new destinations
	 */
	public void setDestinations(String destinations) {
		this.destinations = destinations;
	}

	/**
	 * Sets the command.
	 *
	 * @param command the new command
	 */
	public void setCommand (String command) {
		this.command = command;
	}
	
	/**
	 * Gets the command.
	 *
	 * @return the command
	 */
	public String getCommand () {
		return this.command;
	}

    /**
     * Gets the start time.
     *
     * @return the start time
     */
    public long getStartTime() {
        return startTime;
    }

    /**
     * Sets the start time.
     *
     * @param startTime the new start time
     */
    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    /**
     * Gets the path.
     *
     * @return the path
     */
    public String getPath() {
        return path;
    }

    /**
     * Sets the path.
     *
     * @param path the new path
     */
    public void setPath(String path) {
        this.path = path;
    }

}
