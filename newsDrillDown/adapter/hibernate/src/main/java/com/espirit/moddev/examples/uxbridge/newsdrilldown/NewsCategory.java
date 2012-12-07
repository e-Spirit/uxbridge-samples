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


import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Index;

/**
 * The Class NewsCategory which will be persisted to the table 'category'.
 */
@Entity(name= "category")
@Table(name = "category")
@org.hibernate.annotations.Table(appliesTo = "category",
indexes = {
        @Index(name = "IDX_fsid_lang_2",
                columnNames = {"fs_id", "language"}
        )
	}
)
public class NewsCategory {

	/** The name. */
	@Column(length = 1024)
	private String name;

	/** The fs_id. */
	@Index(name = "fsidindex2")
	private Long fs_id;

	/** The language. */
	private String language;

	/** The id. */
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	/** The lastmodified. */
	private long lastmodified;

	/** The version - needed for compatibility with grails. */
	private int version = 0;


	/** The meta categories. */
	@ManyToMany(cascade={CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
	@JoinTable(
			name = "metaCategory_categories",
			joinColumns = @JoinColumn(name = "category_id"),
			inverseJoinColumns = @JoinColumn(name = "meta_category_id")
	)
//	@Fetch(FetchMode.JOIN)
	private List<NewsMetaCategory> metaCategories;

	/**
	 * Instantiates a new news category.
	 */
	public NewsCategory() {
	}

	/**
	 * Gets the meta categories.
	 *
	 * @return the meta categories
	 */
	public List<NewsMetaCategory> getMetaCategories() {
		return metaCategories;
	}

	/**
	 * Sets the meta categories.
	 *
	 * @param metaCategories the new meta categories
	 */
	public void setMetaCategories(List<NewsMetaCategory> metaCategories) {
		this.metaCategories = metaCategories;
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
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof NewsMetaCategory)) {
			return false;
		}
		NewsCategory mc = (NewsCategory)obj;
		if (mc.fs_id == this.fs_id && mc.language.equals(this.language)) {
			return true;
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		int hash = 1;
		hash = hash * 31 + this.fs_id.hashCode();
		hash = hash * 31 + this.language.hashCode();
		
		
		return hash;
	}

}
