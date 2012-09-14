package com.espirit.moddev.examples.uxbridge.newswidget;

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


import org.hibernate.annotations.Index;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Artikel
 *
 * Der Artikel
 *
 */
@Entity(name= "article")
@Table(name = "article")
public class Article implements Serializable {
	static final long serialVersionUID = 2098738316335022463L;

	/** The Title */
	@Column(length = 500)
    private String title;

	/*
	for hints about problems with Hibernate + Postgres + LOB
	visit http://www.shredzone.de/cilla/page/299/string-lobs-on-postgresql-with-hibernate-36.html
	 */
	/** The content	 */
	@Lob
	@Type(type = "org.hibernate.type.TextType")
    private String content;

	/** The creation date */
    private Date created;
	
    /** The lastmodified */
	private long lastmodified;

	/** The FirstSpirit id */
	@Index(name = "aidindex")
	private Long aid;

	/** The url for linking to Firstspirit */
	private String url;

	/** The version - needed for compatibility with grails. */
	private int version = 0;

	/** The language */
	private String language;

	/** The id */
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	/**
	 * Instantiates a new article.
	 */
	public Article () {
	}

	/**
	 * Gets the id
	 * @return The id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * Sets the id
	 * @param id The new id
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * Gets the title
	 * @return The title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Sets the title
	 * @param title The new title
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * Gets the content
	 * @return The content
	 */
	public String getContent() {
		return content;
	}

	/**
	 * Sets the content
	 * @param content The new content
	 */
	public void setContent(String content) {
		this.content = content;
	}

	/**
	 * Gets the date
	 * @return The date
	 */
	public Date getCreated() {
		return created;
	}

	/**
	 * Sets the date
	 * @param created The new date
	 */
	public void setCreated(Date created) {
		this.created = created;
	}

	/**
	 * Gets the Firstspirit id
	 * @return The FirstSpirit id
	 */
	public long getAid() {
		return aid;
	}

	/**
	 * Sets the FirstSpirit id
	 * @param aid The new aid
	 */
	public void setAid(long aid) {
		this.aid = aid;
	}

	/**
	 * Gets the URL
	 * @return the URL
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * Sets the URL
	 * @param url The new URL
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * Gets the version
	 * @return The version
	 */
	public int getVersion() {
		return version;
	}

	/**
	 * Sets the version
	 * @param version The new version
	 */
	public void setVersion(int version) {
		this.version = version;
	}

	/**
	 * Gets the language
	 * @return The language
	 */
	public String getLanguage() {
		return language;
	}

	/**
	 * Sets the language
	 * @param language The new language
	 */
	public void setLanguage(String language) {
		this.language = language;
	}

	/**
	 * Gets the lastmodified timestamp
	 * @return The lastmodified timestamp
	 */
	public long getLastmodified() {
		return lastmodified;
	}

	/**
	 * Sets the lastmodified timestamp
	 * @param lastmodified The new timestamp
	 */
	public void setLastmodified(long lastmodified) {
		this.lastmodified = lastmodified;
	}
}
