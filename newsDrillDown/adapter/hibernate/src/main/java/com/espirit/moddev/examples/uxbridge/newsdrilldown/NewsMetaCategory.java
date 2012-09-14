package com.espirit.moddev.examples.uxbridge.newsdrilldown;

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


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Index;

// TODO: Auto-generated Javadoc
/**
 * The Class NewsMetaCategory which will be persisted to the table 'metaCategory'.
 */
@Entity(name = "metaCategory")
@Table(name = "metaCategory")
@org.hibernate.annotations.Table(appliesTo = "metaCategory", indexes = { @Index(name = "IDX_fsid_lang_3", columnNames = {
		"fs_id", "language" }) })
public class NewsMetaCategory {

	/** The name. */
	@Column(length = 1024)
	private String name;

	/** The fs_id. */
	@Index(name = "fsidindex3")
	private Long fs_id;

	/** The language. */
	private String language;

	/** The lastmodified. */
	private long lastmodified;

	/** The id. */
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	/** The version - needed for compatibility with grails. */
	private int version = 0;

	/**
	 * Instantiates a new news meta category.
	 */
	public NewsMetaCategory() {
	}

	/**
	 * Gets the name.
	 * 
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name.
	 * 
	 * @param name the new name
	 */
	public void setName(String name) {
		this.name = name;
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