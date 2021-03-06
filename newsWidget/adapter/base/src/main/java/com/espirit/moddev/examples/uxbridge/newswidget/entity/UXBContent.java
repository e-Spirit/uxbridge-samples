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




import com.espirit.moddev.examples.uxbridge.newswidget.entity.type.DateAdapter;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.Date;

/**
 * The Class UXBContent represents the xml-tag 'uxb_content' defined in the FirstSpirit presentation channel.
 * It contains the information needed to build an article (com.espirit.moddev.examples.uxbridge.newswidget.Article).
 */
@XmlType(name = "uxb_content")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "uxb_content")
public class UXBContent {
	
	/** The FirstSpirit ID */
	@XmlElement
	private String fs_id;

	/** The language */
	@XmlElement
	private String language;

	/** The URL */
	@XmlElement
	private String url;

	/** The date */
	@XmlElement()
	@XmlJavaTypeAdapter(value = DateAdapter.class, type = Date.class)
	private Date date;

	/** The headline */
	@XmlElement
	private String headline;

	
	/** The subheadline */
	@XmlElement
	private String subHeadline;

	/** The teaser */
	@XmlElement
	private String teaser;

	/** The content */
	@XmlElement
	private String content;

	/**
	 * Instantiates a new UXB content.
	 */
	public UXBContent() {

	}

	/**
	 * Gets the URL.
	 *
	 * @return the URL
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * Sets the URL.
	 *
	 * @param url the new URL
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * Gets the fs_id.
	 *
	 * @return the fs_id
	 */
	public String getFs_id() {
		return fs_id;
	}

	/**
	 * Sets the fs_id.
	 *
	 * @param fs_id the new fs_id
	 */
	public void setFs_id(String fs_id) {
		this.fs_id = fs_id;
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
	 * Gets the date.
	 *
	 * @return the date
	 */
	public Date getDate() {
		return date;
	}

	/**
	 * Sets the date.
	 *
	 * @param date the new date
	 */
	public void setDate(Date date) {
		this.date = date;
	}

	/**
	 * Gets the headline.
	 *
	 * @return the headline
	 */
	public String getHeadline() {
		return headline;
	}

	/**
	 * Sets the headline.
	 *
	 * @param headline the new headline
	 */
	public void setHeadline(String headline) {
		this.headline = headline;
	}

	/**
	 * Gets the sub headline.
	 *
	 * @return the sub headline
	 */
	public String getSubHeadline() {
		return subHeadline;
	}

	/**
	 * Sets the sub headline.
	 *
	 * @param subHeadline the new sub headline
	 */
	public void setSubHeadline(String subHeadline) {
		this.subHeadline = subHeadline;
	}

	/**
	 * Gets the teaser.
	 *
	 * @return the teaser
	 */
	public String getTeaser() {
		return teaser;
	}

	/**
	 * Sets the teaser.
	 *
	 * @param teaser the new teaser
	 */
	public void setTeaser(String teaser) {
		this.teaser = teaser;
	}

	/**
	 * Gets the content.
	 *
	 * @return the content
	 */
	public String getContent() {
		return content;
	}

	/**
	 * Sets the content.
	 *
	 * @param content the new content
	 */
	public void setContent(String content) {
		this.content = content;
	}
}