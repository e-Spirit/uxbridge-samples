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


import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * News
 * 
 * The News which will be persisted to the database'.
 */
public class News implements Serializable {

	/** The Constant serialVersionUID. */
	static final long serialVersionUID = 2098738316335022463L;

	/** The headline. */
    private String headline;
	
    /** The subheadline. */
    private String subheadline;
	
    /** The teaser. */
    private String teaser;
	
    /** The content. */
    private String content;
    
    /** The date. */
    private Date date;

    /** The lastmodified. */
    private long lastmodified;

    /** The fs_id. */
    private Long fs_id;
    
    /** The url used for linking to firstspirit. */
	private String url;
	
	/** The version - needed for compatibility with grails. */
	private int version = 0;

	/** The language of the article. */
	private String language;
	
	/** The id. */
	private Long id;
	
	/** The categories. */
	private List<NewsCategory> categories;

	/**
	 * Instantiates a new news.
	 */
	public News () {
	}
	
	/**
	 * Gets the categories.
	 *
	 * @return the categories
	 */
	public List<NewsCategory> getCategories() {
		return categories;
	}

	/**
	 * Sets the categories.
	 *
	 * @param categories the new categories
	 */
	public void setCategories(List<NewsCategory> categories) {
		this.categories = categories;
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
	 * Gets the subheadline.
	 *
	 * @return the subheadline
	 */
	public String getSubheadline() {
		return subheadline;
	}

	/**
	 * Sets the subheadline.
	 *
	 * @param subheadline the new subheadline
	 */
	public void setSubheadline(String subheadline) {
		this.subheadline = subheadline;
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
	 * Gets the fs_id.
	 *
	 * @return the fs_id
	 */
	public Long getFs_id() {
		return fs_id;
	}

	/**
	 * Sets the fs_id.
	 *
	 * @param fs_id the new fs_id
	 */
	public void setFs_id(Long fs_id) {
		this.fs_id = fs_id;
	}

	/**
	 * Gets the url.
	 *
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * Sets the url.
	 *
	 * @param url the new url
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * Gets the version.
	 *
	 * @return the version
	 */
	public int getVersion() {
		return version;
	}

	/**
	 * Sets the version.
	 *
	 * @param version the new version
	 */
	public void setVersion(int version) {
		this.version = version;
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
	 * Gets the id.
	 *
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * Sets the id.
	 *
	 * @param id the new id
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * Gets the lastmodified.
	 *
	 * @return the lastmodified
	 */
	public long getLastmodified() {
		return lastmodified;
	}

	/**
	 * Sets the lastmodified.
	 *
	 * @param lastmodified the new lastmodified
	 */
	public void setLastmodified(long lastmodified) {
		this.lastmodified = lastmodified;
	}
}