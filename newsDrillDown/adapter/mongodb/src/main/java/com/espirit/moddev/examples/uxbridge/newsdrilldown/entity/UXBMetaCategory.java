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




import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * The Class UXBMetaCategory represents the xml-tag 'uxb_meta_category' defined in the FirstSpirit presentation channel.
 * It contains the information needed to build a NewsMetaCategory (com.espirit.moddev.examples.uxbridge.newsdrilldown.NewsMetaCategory).
 */
@XmlType(name = "uxb_meta_category")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "metaCategory")
public class UXBMetaCategory {
	
	/** The fs_id. */
	@XmlElement
	private String fs_id;
	
	/** The name. */
	@XmlElement
	private String name;
	
	/** The categories. */
	@XmlElementWrapper(name = "categories")
	@XmlElement(type = UXBCategory.class, name = "category")
	private List<UXBCategory> categories;

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
	 * Gets the categories.
	 *
	 * @return the categories
	 */
	public List<UXBCategory> getCategories() {
		return categories;
	}

	/**
	 * Sets the categories.
	 *
	 * @param categories the new categories
	 */
	public void setCategories(List<UXBCategory> categories) {
		this.categories = categories;
	}
			
}