package com.espirit.moddev.examples.uxbridge.newsdrilldown.test;

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



import com.mongodb.*;
import jmockmongo.MockMongo;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.camel.CamelContext;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.xbean.spring.context.FileSystemXmlApplicationContext;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;

import javax.jms.ConnectionFactory;

/**
 * This class tests the NewsHandler. It starts an own broker sends messages which will be processed by the NewsHandler.
 * Afterwards it will query for the particular data records if they were written to the database successfully.
 */
public class MongoCommandITCase extends BaseTest {
    /** The MongoDB mock */
	private static MockMongo mockMongo;

	/** The broker. */
    private static BrokerService broker;
    
	/** The camel context. */
    private static CamelContext camelContext;

    /**
     * The Mongo object.
     */
    private static Mongo m;
    
    /**
     * The DBCollection to store the news in
     */
    private static DBCollection dbNews;
    
    /**
     * The DBCollection to store the categories in
     */
    private static DBCollection dbCategories;
    
    /**
     * The DBCollection to store the metacategories in
     */
    private static DBCollection dbMetaCategories;

	/**
	 * Do before.
	 *
	 * @throws Exception the exception
	 */
    @BeforeClass
    public static void doBeforeClass() throws Exception {

        // start mongomock
        mockMongo = new MockMongo();
        mockMongo.start();

        broker = new BrokerService();
        // configure the broker
        broker.addConnector("tcp://localhost:61501");
        broker.start();

        ApplicationContext ctx = new FileSystemXmlApplicationContext(getBasePath("mongodb") + "src/test/resources/applicationContext.xml");

        camelContext = (CamelContext) ctx.getBean("camelContext");
        camelContext.start();

        m = new Mongo("localhost", 2307);
        DB db = m.getDB("uxbridge_mongo_test");
        dbNews = db.getCollection("newsdrilldown");
        dbCategories = db.getCollection("newsCategory");
        dbMetaCategories = db.getCollection("newsMetaCategory");

    }

	/**
	 * Shutdown.
	 *
	 * @throws Exception the exception
	 */
    @AfterClass
    public static void shutdown() throws Exception {
        broker.stop();
        camelContext.stop();
        //db.dropDatabase();
        m.close();

        if (mockMongo != null)
            mockMongo.stop();
    }

    /**
	 * Before test.
	 *
	 * @throws Exception the exception
	 */
    @Before
    public void beforeTest() throws Exception {
        dbNews.remove(new BasicDBObject());
    }

    /**
     * Creates the CamelContext
     * @return The CamelContext
     */
    protected CamelContext createCamelContext() throws Exception {
        CamelContext camelContext = super.createCamelContext();

        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61501");
        camelContext.addComponent("jms", JmsComponent.jmsComponentClientAcknowledge(connectionFactory));

        return camelContext;
    }

	/**
	 * Test add.
	 *
	 * @throws Exception the exception
	 */
    @Test
    public void testAdd() throws Exception {

        assertEquals("DB not empty", 0, countNews());

        String[] ids = new String[]{"132", "256"};

        // insert all items
        for (String id : ids) {
            // item should not be in the db
            DBObject query = new BasicDBObject();
            query.put("fs_id", Long.parseLong(id));
            assertEquals(0, dbNews.count(query));

            // load content
            String content = getContent("src/test/resources/inbox/add/pressreleasesdetails_" + id + ".xml", "mongodb");
            // send content to jms broker
            template.sendBody("jms:topic:BUS_OUT", content);

            // wait
            Thread.sleep(2000);

            // item should be inserted to db
            assertEquals(1, dbNews.count(query));
        }

        assertEquals("not all items are present", ids.length, countNews());

        // now check, that items are not insert twice if resend the same content
        for (String id : ids) {
            // load content
            String content = getContent("src/test/resources/inbox/add/pressreleasesdetails_" + id + ".xml", "mongodb");
            // send content to jms broker
            template.sendBody("jms:topic:BUS_OUT", content);
            // wait
            Thread.sleep(2000);

            // only one item should be present
            DBObject query = new BasicDBObject();
            query.put("fs_id", Long.parseLong(id));
            assertEquals(1, dbNews.count(query));
        }
    }

    /**
	 * Test delete.
	 *
	 * @throws Exception the exception
	 */
    @Test
    public void testDelete() throws Exception {


        String[] ids = new String[]{"132", "256"};
        for (String id : ids) {
            DBObject query = new BasicDBObject();
            query.put("fs_id", Long.parseLong(id));
            dbNews.remove(query);
        }

        // insert all items
        for (String id : ids) {
            // item should not be in the db
            DBObject query = new BasicDBObject();
            query.put("fs_id", Long.parseLong(id));
            assertEquals(0, dbNews.count(query));

            // load content
            String content = getContent("src/test/resources/inbox/add/pressreleasesdetails_" + id + ".xml", "mongodb");
            // send content to jms broker
            template.sendBody("jms:topic:BUS_OUT", content);
        }
        // wait
        Thread.sleep(10000);

        assertEquals("not all items are present", ids.length, countNews());

        // now check, that items are not insert twice if resend the same content
        for (String id : ids) {
            // load content
            String content = getContent("src/test/resources/inbox/delete/pressreleasesdetails_" + id + ".xml", "mongodb");
            // send content to jms broker
            template.sendBody("jms:topic:BUS_OUT", content);
            // wait
            Thread.sleep(10000);

            // item should be deleted
            DBObject query = new BasicDBObject();
            query.put("fs_id", Long.parseLong(id));
            assertEquals(id+" is not deleted",0, dbNews.count(query));
        }

        assertEquals("ups, there are items left in the db", 0, countNews());
    }

	/**
	 * Test cleanup.
	 *
	 * @throws Exception the exception
	 */
    @Test
    public void testCleanup() throws Exception {


        String[] ids = new String[]{"489", "490"};
        for (String id : ids) {
            DBObject query = new BasicDBObject();
            query.put("fs_id", Long.parseLong(id));
            dbNews.remove(query);
        }

        int numberOfArticles = ids.length;

        // insert all items
        for (String id : ids) {
            // item should not be in the db
            DBObject query = new BasicDBObject();
            query.put("fs_id", Long.parseLong(id));
            assertEquals(0, dbNews.count(query));

            // load content
            String content = getContent("src/test/resources/inbox/add/pressreleasesdetails_" + id + ".xml", "mongodb");
            // patch content - add createTime
            content = content.replace("<uxb_entity ", "<uxb_entity createTime=\"" + System.currentTimeMillis() + "\" ");
            // send content to jms broker
            template.sendBody("jms:topic:BUS_OUT", content);
        }
        // wait
        Thread.sleep(10000);

        assertEquals("not all items are present", numberOfArticles, countNews());

        // Save the current time as expiredate
        long expiredate = System.currentTimeMillis();

        ids = new String[]{"490"};
        int numberOfNewArticles = ids.length;

        // insert new items
        for (String id : ids) {
            // item should be in the db
            DBObject query = new BasicDBObject();
            query.put("fs_id", Long.parseLong(id));
            assertEquals(1, dbNews.count(query));

            // load content
            String content = getContent("src/test/resources/inbox/add/pressreleasesdetails_" + id + ".xml", "mongodb");
            // patch content - add createTime
            content = content.replace("<uxb_entity ", "<uxb_entity createTime=\"" + System.currentTimeMillis() + "\" ");
            // send content to jms broker
            template.sendBody("jms:topic:BUS_OUT", content);
        }
        // wait
        Thread.sleep(10000);

        assertEquals("not all items are present", numberOfArticles, countNews());

        // now check, that old items are cleaned up
        for (String id : ids) {
            // load content
            String content = getContent("src/test/resources/inbox/cleanup/pressreleasesdetails.xml", "mongodb");
            // patch content - add createTime
            content = content.replace("<uxb_entity ", "<uxb_entity createTime=\"" + expiredate + "\" ");
            // send content to jms broker
            template.sendBody("jms:topic:BUS_OUT", content);
        }
        // wait
        Thread.sleep(10000);

        // Check number of dbNews
        assertEquals("ups, there are too many items left in the db", numberOfNewArticles, countNews());

        // Check article ids
        for (String id : ids) {
            // item should be in the db
            DBObject query = new BasicDBObject();
            query.put("fs_id", Long.parseLong(id));
            assertEquals(1, dbNews.count(query));
        }

        // Check category ids
        ids = new String[]{"4160", "4161", "4163", "4164", "4165", "4166"};
        for (String id : ids) {
            DBObject query = new BasicDBObject();
            query.put("fs_id", Long.parseLong(id));
            assertEquals(1, dbCategories.count(query));
        }

        // Check metaCategory ids
        ids = new String[]{"4096", "4097"};
        for (String id : ids) {
            DBObject query = new BasicDBObject();
            query.put("fs_id", Long.parseLong(id));
            assertEquals(1, dbMetaCategories.count(query));
        }

        // Check metaCategory ids
        ids = new String[]{"4098"};
        for (String id : ids) {
            DBObject query = new BasicDBObject();
            query.put("fs_id", Long.parseLong(id));
            assertEquals(0, dbMetaCategories.count(query));
        }

        //Check category ids
        ids = new String[]{"4167", "4168", "4169", "4170", "4171", "4172"};
        for (String id : ids) {
            DBObject query = new BasicDBObject();
            query.put("fs_id", Long.parseLong(id));
            assertEquals(0, dbCategories.count(query));
        }

    }

	/**
	 * Count articles.
	 *
	 * @return the long
	 * @throws Exception the exception
	 */
    private long countNews() throws Exception {
        return dbNews.count();
    }

	/* (non-Javadoc)
	 * @see com.espirit.moddev.examples.uxbridge.newsdrilldown.test.BaseTest#getContext()
	 */
    @Override
    public CamelContext getContext() {
        return camelContext;
    }
}
