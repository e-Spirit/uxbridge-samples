package com.espirit.moddev.examples.uxbridge.newsdrilldown.jpa;

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

import com.espirit.moddev.examples.uxbridge.newsdrilldown.News;
import com.espirit.moddev.examples.uxbridge.newsdrilldown.NewsCategory;
import com.espirit.moddev.examples.uxbridge.newsdrilldown.NewsMetaCategory;
import com.espirit.moddev.examples.uxbridge.newsdrilldown.entity.UXBCategory;
import com.espirit.moddev.examples.uxbridge.newsdrilldown.entity.UXBEntity;
import com.espirit.moddev.examples.uxbridge.newsdrilldown.entity.UXBMetaCategory;
import com.mongodb.*;

import org.apache.camel.CamelContext;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.util.ArrayList;

/**
 * This class has the ability to add, delete and cleanup data records in a
 * mongoDB database which might be used as content-repository for an UX-Bridge
 * application.
 *
 */
public class NewsHandler {

	/**
	 * The Constant logger.
	 */
	private static final Logger logger = Logger.getLogger(NewsHandler.class);

	/**
	 * The Constant NEWS_COLLECTION_NAME contains the name of the collection to
	 * store the news in
	 */
	public static final String NEWS_COLLECTION_NAME = "news";

	/**
	 * The Constant CATEGORY_COLLECTION_NAME contains the name of the collection
	 * to store the categories in
	 */
	public static final String CATEGORY_COLLECTION_NAME = "category";

	/**
	 * The Constant META_CATEGORY_COLLECTION_NAME contains the name of the
	 * collection to store the metacategories in
	 */
	public static final String META_CATEGORY_COLLECTION_NAME = "metaCategory";

	/**
	 * The Constant DESTINATION contains the destination of the messages coming
	 * from the broker.
	 */
	private static final String DESTINATION = "mongodb";

	/**
	 * The Mongo object.
	 */
	private Mongo m;

	/**
	 * The database.
	 */
	private DB mdb;

	/**
	 * The DBCollection to store the news in
	 */
	private DBCollection dbNews;

	/**
	 * The DBCollection to store the categories in
	 */
	private DBCollection dbCategories;

	/**
	 * The DBCollection to store the metacategories in
	 */
	private DBCollection dbMetaCategories;

	/**
	 * The webpath.
	 */
	private String webpath;

	/**
	 * Instantiates a new news handler.
	 *
	 * @param host
	 *            Host the mongodb is running
	 * @param port
	 *            Port of the mongodb
	 * @param db
	 *            The name of the mongodb
	 * @throws Exception
	 *             Exception will thrown an database errors
	 * @param webpath
	 *            the webpath
	 */
	public NewsHandler(String host, int port, String db, String webpath) throws Exception {
		this.webpath = webpath;

		m = new Mongo(host, port);
		mdb = m.getDB(db);
		dbNews = mdb.getCollection(NEWS_COLLECTION_NAME);
		dbCategories = mdb.getCollection(CATEGORY_COLLECTION_NAME);
		dbMetaCategories = mdb.getCollection(META_CATEGORY_COLLECTION_NAME);
	}

	/**
	 * Instantiates a new news handler.
	 */
	public NewsHandler() {
	}

	/**
	 * The destroy method of the class
	 */
	public void destroy() {
		if (m != null) {
			m.close();
		}
	}

	/**
	 * Add or update a newsdrilldown item in the db
	 *
	 * @param entity
	 *            The NewsItem
	 * @throws Exception
	 */
	public void add(UXBEntity entity) throws Exception {

		if (entity == null) {
			logger.warn("no UXBEntity given, no action");
			return;
		}

		News news = buildNews(entity);
		handleNews(news);

	}

	/**
	 * Deletes an item from the db
	 *
	 * @param entity
	 *            The item to delete
	 * @throws IOException
	 */
	public void delete(UXBEntity entity) throws IOException {
		if (entity == null) {
			logger.warn("no UXBEntity given, no action");
			return;
		}

		// delete item
		DBObject item = getNewsById(Long.parseLong(entity.getUuid()),
				entity.getLanguage());
		if (item != null) {
			// delete file from filesystem
			URL url = new URL((String) item.get("url"));
			File file = new File(webpath + url.getPath());
			if (file.exists()) {
				// Try acquiring the lock without blocking. This method returns
				// null or throws an exception if the file is already locked.
				try {
					FileChannel channel = new RandomAccessFile(file, "rw")
							.getChannel();
					// Try to lock the file
					FileLock lock = channel.tryLock();
					// Delete the file
					boolean deleteSuccess = file.delete();
					if (!deleteSuccess) {
						logger.debug("Couldn't delete file, file not found. Maybe someone else has deleted it.");
					}
					// Release the lock
					lock.release();
					lock.channel().close();
				} catch (OverlappingFileLockException e) {
					logger.info("File is already locked in this thread or virtual machine");
				}
			}
			// delete the article
			DBObject query = new BasicDBObject();
			query.put("_id", item.get("_id"));
			query.put("fs_id", item.get("fs_id"));
			query.put("language", item.get("language"));
			dbNews.remove(query);
		}

	}

	/**
	 * Deletes every item older than the creationTime of the UXBEntity.
	 *
	 * @param entity
	 *            Entity containing the expireDate (= createTime of the entity)
	 */
	public void cleanup(UXBEntity entity) {

		if (entity == null) {
			logger.warn("no UXBEntity given, no action");
			return;
		}

		// delete newsdrilldown
		DBCursor items = getNewsByLastmodified(entity.getCreateTime());
		for (DBObject item : items) {
			DBObject query = new BasicDBObject();
			query.put("_id", item.get("_id"));
			query.put("fs_id", item.get("fs_id"));
			query.put("language", item.get("language"));
			dbNews.remove(query);
		}

		// delete categories
		items = getCategoriesByLastmodified(entity.getCreateTime());
		for (DBObject item : items) {
			DBObject query = new BasicDBObject();
			query.put("_id", item.get("_id"));
			query.put("fs_id", item.get("fs_id"));
			query.put("language", item.get("language"));
			dbCategories.remove(query);
		}

		// delete metaCategories
		items = getMetaCategoriesByLastmodified(entity.getCreateTime());
		for (DBObject item : items) {
			DBObject query = new BasicDBObject();
			query.put("_id", item.get("_id"));
			query.put("fs_id", item.get("fs_id"));
			query.put("language", item.get("language"));
			dbMetaCategories.remove(query);
		}

	}

	/**
	 * handle the update and save process for the news.
	 *
	 * @param news
	 *            the News object
	 * @throws Exception
	 *             the exception
	 */
	private void handleNews(News news) throws Exception {

		// 1. save the product
		DBObject item = getNewsById(news.getFs_id(), news.getLanguage());
		if (item != null) {
			item.put("fs_id", news.getFs_id());
			item.put("language", news.getLanguage());
			item.put("headline", news.getHeadline());
			item.put("subHeadline", news.getSubheadline());
			item.put("headline", news.getHeadline());
			item.put("teaser", news.getTeaser());
			item.put("content", news.getContent());
			item.put("date", news.getDate());
			item.put("url", news.getUrl());
			item.put("version", news.getVersion());
			item.put("lastmodified", news.getLastmodified());



			BasicDBObject query = new BasicDBObject();
			news.setId((Long) item.get("_id"));
			query.put("_id", news.getId());

			dbNews.update(query, item);
		} else {
			item = new BasicDBObject();

			item.put("fs_id", news.getFs_id());
			item.put("language", news.getLanguage());
			item.put("headline", news.getHeadline());
			item.put("subHeadline", news.getSubheadline());
			item.put("headline", news.getHeadline());
			item.put("teaser", news.getTeaser());
			item.put("content", news.getContent());
			item.put("date", news.getDate());
			item.put("url", news.getUrl());
			item.put("version", news.getVersion());
			item.put("lastmodified", news.getLastmodified());

			news.setId(generateIdentifier(NEWS_COLLECTION_NAME, mdb));
			item.put("_id", news.getId());

			dbNews.insert(item);
		}

		// 2. save categories
		// add categories to news
		BasicDBList catList = new BasicDBList();
		if (news.getCategories() != null) {
			for (NewsCategory cat : news.getCategories()) {
				handleCategory(cat, news);

				DBObject tmpCat = getCategoryById(cat.getFs_id(), cat.getLanguage());
				//catList.add(new DBRef(this.mdb, this.CATEGORY_COLLECTION_NAME, tmpCat.get("_id")));
				catList.add(tmpCat.get("_id"));
			}
		}


		// add list of categories to the news
//		item.put("categories", catList);
		item.put("categories_$$manyToManyIds", catList);

		BasicDBObject query = new BasicDBObject();
		query.put("_id", news.getId());

		dbNews.update(query, item);
	}

	/**
	 * handle the update and save process for the categories.
	 *
	 * @param category
	 *            the NewsCategory object
	 * @param news
	 *            the News object to reference to
	 * @throws Exception
	 *             the exception
	 */
	private void handleCategory(NewsCategory category, News news)
			throws Exception {

		// 1. save the category
		DBObject item = getCategoryById(category.getFs_id(),
				category.getLanguage());

		if (item != null) {
			item.put("fs_id", category.getFs_id());
			item.put("language", category.getLanguage());
			item.put("name", category.getName());
			item.put("version", category.getVersion());
			item.put("lastmodified", category.getLastmodified());

			// newsdrilldown
//			DBObject tmpNews = getNewsById(news.getFs_id(), news.getLanguage());
//			if (tmpNews != null) {
//				DBRef ref = new DBRef(mdb, NEWS_COLLECTION_NAME,
//						tmpNews.get("_id"));
//				item.put("newsdrilldown", ref);
//			}

			BasicDBObject query = new BasicDBObject();
			category.setId((Long) item.get("_id"));
			query.put("_id", category.getId());

			dbCategories.update(query, item);
		} else {
			item = new BasicDBObject();

			item.put("fs_id", category.getFs_id());
			item.put("language", category.getLanguage());
			item.put("name", category.getName());
			item.put("version", category.getVersion());
			item.put("lastmodified", category.getLastmodified());

			// newsdrilldown
//			DBObject tmpNews = getNewsById(news.getFs_id(), news.getLanguage());
//			if (tmpNews != null) {
//				DBRef ref = new DBRef(mdb, NEWS_COLLECTION_NAME,
//						tmpNews.get("_id"));
//				item.put("newsdrilldown", ref);
//			}

			category.setId(generateIdentifier(CATEGORY_COLLECTION_NAME, mdb));
			item.put("_id", category.getId());

			dbCategories.insert(item);
		}

		// 2. save the metaCategories
		if (category.getMetaCategories() != null) {


			BasicDBList metaCatlist = (BasicDBList) item.get("metaCategories_$$manyToManyIds");
			if (metaCatlist == null) {
				metaCatlist = new BasicDBList();
			}

			for (NewsMetaCategory metaCat : category.getMetaCategories()) {
				handleMetaCategory(metaCat, category);

				if (!metaCatlist.contains(metaCat.getId())) {
					metaCatlist.add(metaCat.getId());
				}

				DBObject obj = getMetaCategoryById(metaCat.getFs_id(), metaCat.getLanguage());

				//BasicDBList catlist = (BasicDBList) obj.get("categories");
				BasicDBList catlist = (BasicDBList) obj.get("categories_$$manyToManyIds");
				if (catlist == null) {
					catlist = new BasicDBList();
				}


				//DBRef catRef = new DBRef(this.mdb, this.CATEGORY_COLLECTION_NAME, category.getId());
				if (!catlist.contains(category.getId())) {
					catlist.add(category.getId());
				}

				obj.put("categories_$$manyToManyIds", catlist);

				BasicDBObject query = new BasicDBObject();
				query.put("_id", (Long) obj.get("_id"));

				dbMetaCategories.update(query, obj);
			}



			item.put("metaCategories_$$manyToManyIds", metaCatlist);

			BasicDBObject query = new BasicDBObject();
			query.put("_id", (Long) item.get("_id"));

			dbCategories.update(query, item);
		}

	}

	/**
	 * handle the update and save process for the metacategories.
	 *
	 * @param metaCat
	 *            the NewsMetaCategory object
	 * @param category
	 *            the NewsCategory object to reference to
	 * @throws Exception
	 *             the exception
	 */
	private void handleMetaCategory(NewsMetaCategory metaCat,
			NewsCategory category) {
		DBObject item = getMetaCategoryById(metaCat.getFs_id(),
				metaCat.getLanguage());

		if (item != null) {
			item.put("fs_id", metaCat.getFs_id());
			item.put("language", metaCat.getLanguage());
			item.put("name", metaCat.getName());
			item.put("version", metaCat.getVersion());
			item.put("lastmodified", metaCat.getLastmodified());

			// category
			DBObject cat = getCategoryById(category.getFs_id(),
					category.getLanguage());
			if (cat != null) {
				DBRef ref = new DBRef(mdb, CATEGORY_COLLECTION_NAME,
						cat.get("_id"));
				item.put("category", ref);
			}

			BasicDBObject query = new BasicDBObject();
			query.put("_id", item.get("_id"));
			dbMetaCategories.update(query, item);
		} else {
			item = new BasicDBObject();

			item.put("fs_id", metaCat.getFs_id());
			item.put("language", metaCat.getLanguage());
			item.put("name", metaCat.getName());
			item.put("version", metaCat.getVersion());
			item.put("lastmodified", metaCat.getLastmodified());

			// category
			DBObject cat = getCategoryById(category.getFs_id(),
					category.getLanguage());
			if (cat != null) {
				DBRef ref = new DBRef(mdb, CATEGORY_COLLECTION_NAME,
						cat.get("_id"));
				item.put("category", ref);
			}

			item.put("_id",
					generateIdentifier(META_CATEGORY_COLLECTION_NAME, mdb));

			dbMetaCategories.insert(item);
		}
	}

	/**
	 * loads a news by the document id
	 *
	 * @param id
	 *            the id of the document
	 * @param language
	 *            language abbreviation like EN or DE
	 * @return the document or null
	 */
	private DBObject getNewsById(Long id, String language) {
		BasicDBObject query = new BasicDBObject();
		query.put("fs_id", id);
		query.put("language", language);

		DBCursor cur = dbNews.find(query);

		if (cur.hasNext()) {
			return cur.next();
		}
		return null;
	}

	/**
	 * loads a category by the document id
	 *
	 * @param id
	 *            the id of the document
	 * @param language
	 *            language abbreviation like EN or DE
	 * @return the document or null
	 */
	private DBObject getCategoryById(Long id, String language) {
		BasicDBObject query = new BasicDBObject();
		query.put("fs_id", id);
		query.put("language", language);

		DBCursor cur = dbCategories.find(query);

		if (cur.hasNext()) {
			return cur.next();
		}
		return null;
	}

	/**
	 * loads a metacategory by the document id
	 *
	 * @param id
	 *            the id of the document
	 * @param language
	 *            language abbreviation like EN or DE
	 * @return the document or null
	 */
	private DBObject getMetaCategoryById(Long id, String language) {
		BasicDBObject query = new BasicDBObject();
		query.put("fs_id", id);
		query.put("language", language);

		DBCursor cur = dbMetaCategories.find(query);

		if (cur.hasNext()) {
			return cur.next();
		}
		return null;
	}

	/**
	 * loads newsdrilldown by lastmodified date
	 *
	 * @param expiredate
	 *            date of expiration
	 * @return the documents or null
	 */
	private DBCursor getNewsByLastmodified(long expiredate) {
		BasicDBObject query = new BasicDBObject();
		query.put("lastmodified", new BasicDBObject("$lt", expiredate));

		return dbNews.find(query);
	}

	/**
	 * loads categories by lastmodified date
	 *
	 * @param expiredate
	 *            date of expiration
	 * @return the documents or null
	 */
	private DBCursor getCategoriesByLastmodified(long expiredate) {
		BasicDBObject query = new BasicDBObject();
		query.put("lastmodified", new BasicDBObject("$lt", expiredate));

		return dbCategories.find(query);
	}

	/**
	 * loads metacategories by lastmodified date
	 *
	 * @param expiredate
	 *            date of expiration
	 * @return the documents or null
	 */
	private DBCursor getMetaCategoriesByLastmodified(long expiredate) {
		BasicDBObject query = new BasicDBObject();
		query.put("lastmodified", new BasicDBObject("$lt", expiredate));

		return dbMetaCategories.find(query);
	}

	/**
	 * builds the news for the UXBEntity JAXB-Entity
	 *
	 * @param entity
	 *            The UXBEntity
	 * @return a new News instance
	 */
	public News buildNews(UXBEntity entity) {
		News news = new News();
		news.setFs_id(Long.parseLong(entity.getUuid()));
		news.setContent(entity.getUxb_content().getContent());
		news.setDate(entity.getUxb_content().getDate());
		news.setHeadline(entity.getUxb_content().getHeadline());
		news.setSubheadline(entity.getUxb_content().getSubHeadline());
		news.setTeaser(entity.getUxb_content().getTeaser());
		news.setLanguage(entity.getUxb_content().getLanguage());
		news.setUrl(entity.getUxb_content().getUrl());
		news.setVersion(1);
		news.setLastmodified(entity.getCreateTime());

		news.setCategories(new ArrayList<NewsCategory>());
		// add categories
		if (entity.getUxb_content().getMetaCategories() != null) {
			for (UXBMetaCategory uxMetaCat : entity.getUxb_content()
					.getMetaCategories()) {
				if (uxMetaCat.getCategories() != null) {
					for (UXBCategory uxCat : uxMetaCat.getCategories()) {
						NewsCategory newsCat = buildNewsCategory(uxCat, entity);
						news.getCategories().add(newsCat);
					}
				}
			}
		}

		return news;
	}

	/**
	 * builds the category for the UXBCategory JAXB-Entity
	 *
	 * @param uxCat
	 *            The UXBCategory
	 * @param entity
	 *            The UXBEntity
	 * @return a new NewsCategory instance
	 */
	private NewsCategory buildNewsCategory(UXBCategory uxCat, UXBEntity entity) {
		NewsCategory newsCat = new NewsCategory();
		newsCat.setMetaCategories(new ArrayList<NewsMetaCategory>());
		newsCat.setFs_id(Long.parseLong(uxCat.getFs_id()));
		newsCat.setLanguage(entity.getLanguage());
		newsCat.setName(uxCat.getName());
		newsCat.setVersion(1);
		newsCat.setLastmodified(entity.getCreateTime());

		newsCat.setMetaCategories(new ArrayList<NewsMetaCategory>());

		if (entity.getUxb_content().getMetaCategories() != null) {
			for (UXBMetaCategory uxMetaCat : entity.getUxb_content()
					.getMetaCategories()) {
				newsCat.getMetaCategories().add(
						buildNewsMetaCategory(uxMetaCat, entity));
			}
		}

		return newsCat;
	}

	/**
	 * builds the metacategory for the UXBCategory JAXB-Entity
	 *
	 * @param uxMetaCat
	 *            The UXBMetaCategory
	 * @param entity
	 *            The UXBEntity
	 * @return a new NewsMetaCategory instance
	 */
	private NewsMetaCategory buildNewsMetaCategory(UXBMetaCategory uxMetaCat,
			UXBEntity entity) {
		NewsMetaCategory newsMetaCategory = new NewsMetaCategory();
		newsMetaCategory.setFs_id(Long.parseLong(uxMetaCat.getFs_id()));
		newsMetaCategory.setName(uxMetaCat.getName());
		newsMetaCategory.setLanguage(entity.getLanguage());
		newsMetaCategory.setVersion(1);
		newsMetaCategory.setLastmodified(entity.getCreateTime());

		return newsMetaCategory;
	}

	/**
	 * Mongo ID generation like it is done in the grails gorm framework
	 *
	 * @param collectionName
	 *            The name of the collection the id should be generated for
	 * @param db
	 *            The mongodb connection
	 * @return a new id
	 */
	private Long generateIdentifier(String collectionName, DB db) {

		// get or create the Collection for the ID storage
		DBCollection dbCollection = db.getCollection(collectionName
				+ ".next_id");
		// create entry to store the newly generated id
		DBObject nativeEntry = new BasicDBObject();

		while (true) {
			DBCursor result = dbCollection.find()
					.sort(new BasicDBObject("_id", -1)).limit(1);

			long nextId;
			if (result.hasNext()) {
				final Long current = (Long) result.next().get("_id");
				nextId = current + 1;
			} else {
				nextId = 1;
			}

			nativeEntry.put("_id", nextId);
			final WriteResult writeResult = dbCollection.insert(nativeEntry);
			final CommandResult lastError = writeResult.getLastError();
			if (lastError.ok()) {
				break;
			}

			final Object code = lastError.get("code");
			// duplicate key error try again
			if (code != null && code.equals(11000)) {
				continue;
			}
			break;
		}

		return (Long) nativeEntry.get("_id");
	}

}
