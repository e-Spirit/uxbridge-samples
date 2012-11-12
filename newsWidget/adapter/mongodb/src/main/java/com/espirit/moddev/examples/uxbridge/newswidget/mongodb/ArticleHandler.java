package com.espirit.moddev.examples.uxbridge.newswidget.mongodb;

/*
 * //**********************************************************************
 * uxbridge.samples.newswidget.mongodb
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
import com.mongodb.*;

import org.apache.camel.CamelContext;
import org.apache.log4j.Logger;

/**
 * This class has the ability to add, delete and cleanup data records in a MongoDB database
 * which might be used as content-repository for an UX-Bridge application.
 */
public class ArticleHandler {

    /**
     * The Constant logger.
     */
    private static final Logger logger = Logger.getLogger(ArticleHandler.class);

    /**
     *  The Constant NEWS_COLLECTION_NAME contains the name of the collection to store the news in
     */
    private static final String COLLECTION_NAME = "article";

    /**
     * The Constant DESTINATION contains the destination of the messages coming from the broker.
     */
    private static final String DESTINATION = "mongodb";
    
    /** The Constant STATUS_OK. */
	public static final String STATUS_OK = "OK";
    
    /** The Constant STATUS_FAIL. */
    public static final String STATUS_FAIL = "FAIL";

    /**
     * The context.
     */
    private CamelContext context;


    /**
     * The Mongo object.
     */
    private Mongo m;
    
    /**
     * The database.
     */
    private DB mdb;
    
    /**
     * The DBCollection to store the articles in
     */
    private DBCollection articles;
    
    /**
     * The response route.
     */
    private String responseRoute;

    /**     
     * Instantiates a new news handler.
     * 
     * @param context       The Camelcontext
     * @param host          Host the mongodb is running
     * @param port          Port of the mongodb
     * @param db            The name of the mongodb
     * @param responseRoute The route for the response message
     * @throws Exception Exception will thrown an database errors
     */
    public ArticleHandler(CamelContext context, String host, int port, String db, String responseRoute) throws Exception {
        this.context = context;
        this.responseRoute = responseRoute;

        m = new Mongo(host, port);
        mdb = m.getDB(db);
        articles = mdb.getCollection(COLLECTION_NAME);
    }

    /**
     * destroy method to close database connection
     */
    public void destroy() {
        if (m != null) {
            m.close();
        }
    }

    /**     
     * Instantiates a new news handler.
     */
    public ArticleHandler() {
    }


    /**
     * Add or update a news article in the db
     *
     * @param entity The news article
     */
    public void add(UXBEntity entity) throws Exception{

    	DBObject item = getById(Long.parseLong(entity.getUxb_content().getFs_id()), entity.getLanguage());

        /*
                    * If the item exists in the db, we update the content and the title of the existing item
                    */
        if (item != null) {
            item.put("content", entity.getUxb_content().getContent());
            item.put("title", entity.getUxb_content().getHeadline());
            item.put("url", entity.getUxb_content().getUrl());
            item.put("aid", Long.parseLong(entity.getUxb_content().getFs_id()));
            item.put("language", entity.getUxb_content().getLanguage());
            item.put("lastmodified", entity.getCreateTime());

            BasicDBObject query = new BasicDBObject();
            query.put("_id", item.get("_id"));
            articles.update(query, item);
        } else {
            item = buildArticle(entity);
            item.put("_id", generateIdentifier(COLLECTION_NAME, mdb));

            articles.insert(item);
        }
    }

    /**
     * Deletes an article from the db
     *
     * @param entity The article to delete
     */
    public void delete(UXBEntity entity) throws Exception{

    	// delete item
        DBObject item = getById(Long.parseLong(entity.getUuid()), entity.getLanguage());
        if (item != null) {
            // delete the article
            DBObject query = new BasicDBObject();
            query.put("_id", item.get("_id"));
            query.put("aid", item.get("aid"));
            query.put("language", item.get("language"));
            articles.remove(query);
        }
    }

    /**
     * Deletes every article  older than expireDate
     *
     * @param entity Entity containing the expireDate (= createTime of the entity)
     */
    public void cleanup(UXBEntity entity) throws Exception{

    	// delete items
        DBCursor items = getByLastmodified(entity.getCreateTime());
        for (DBObject item : items) {
            DBObject query = new BasicDBObject();
            query.put("_id", item.get("_id"));
            query.put("aid", item.get("aid"));
            query.put("language", item.get("language"));
            articles.remove(query);
        }
    }

    /**
     * loads an article by the document id
     *
     * @param id       the id of the document
     * @param language language abbreviation like EN or DE
     * @return the document or null
     */
    private DBObject getById(Long id, String language) {
        BasicDBObject query = new BasicDBObject();
        query.put("aid", id);
        query.put("language", language);

        DBCursor cur = articles.find(query);

        if (cur.hasNext()) {
            return cur.next();
        }
        return null;
    }

    /**
     * loads items by lastmodified date
     *
     * @param expiredate date of expiration
     * @return the documents or null
     */
    private DBCursor getByLastmodified(long expiredate) {
        BasicDBObject query = new BasicDBObject();
        query.put("lastmodified", new BasicDBObject("$lt", expiredate));

        return articles.find(query);
    }

    /**
     * builds the Article for the UXBentity JAXB-Entity
     *
     * @param entity The UXBEntity
     * @return a new Article instance
     */
    private BasicDBObject buildArticle(UXBEntity entity) {
        BasicDBObject art = new BasicDBObject();
        art.put("aid", Long.parseLong(entity.getUxb_content().getFs_id()));
        art.put("content", entity.getUxb_content().getContent());
        art.put("created", entity.getUxb_content().getDate());
        art.put("title", entity.getUxb_content().getHeadline());
        art.put("language", entity.getUxb_content().getLanguage());
        art.put("url", entity.getUxb_content().getUrl());
        art.put("lastmodified", entity.getCreateTime());

        return art;
    }

    /**
     * Mongo ID generation like it is done in the grails gorm framework
     *
     * @param collectionName The name of the collection the id should be generated for
     * @param db             The mongodb connection
     * @return a new id
     */
    private Long generateIdentifier(String collectionName, DB db) {

        // get or create the Collection for the ID storage
        DBCollection dbCollection = db.getCollection(collectionName + ".next_id");
        // create entry to store the newly generated id
        DBObject nativeEntry = new BasicDBObject();

        while (true) {
            DBCursor result = dbCollection.find().sort(new BasicDBObject("_id", -1)).limit(1);

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
