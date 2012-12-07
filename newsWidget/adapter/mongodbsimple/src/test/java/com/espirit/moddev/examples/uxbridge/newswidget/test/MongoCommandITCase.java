package com.espirit.moddev.examples.uxbridge.newswidget.test;

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
 * This class tests the ArticleHandler. It starts an own broker sends messages which will be processed by the ArticleHandler.
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
     * The DBCollection to store the articles in
     */
    private static DBCollection articles;

	/**
	 * Do before.
	 *
	 * @throws Exception the exception
	 */
    @BeforeClass
    public static void doBeforeClass() throws Exception {

        // start mongomock
        //mockMongo = new MockMongo();
        //mockMongo.start();

        broker = new BrokerService();
        // configure the broker
        broker.addConnector("tcp://localhost:61501");
        broker.start();

        ApplicationContext ctx = new FileSystemXmlApplicationContext(getBasePath("mongodbsimple") + "src/test/resources/applicationContext.xml");

        camelContext = (CamelContext) ctx.getBean("camelContext");
        camelContext.start();

        m = new Mongo("localhost", 27017);
	    DB db = m.getDB("newsWidget");
	    articles = db.getCollection("article");
        
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

        //if (mockMongo != null)
        //    mockMongo.stop();
    }

    /**
	 * Before test.
	 *
	 * @throws Exception the exception
	 */
    @Before
    public void beforeTest() throws Exception{
    	articles.remove(new BasicDBObject());
     }

    /**
     * Creates the CamelContext
     * @return The CamelContext
     */
    @Override
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

        assertEquals("DB not empty", 0, countArticles());

        String[] ids = new String[]{"128", "130", "131", "132", "256", "704"};

        // insert all items
        for (String id : ids) {
            // item should not be in the db
            BasicDBObject query = new BasicDBObject();
            query.put("aid",Integer.parseInt(id));
            assertEquals(id+" is still available",0, articles.count(query));

            // load content
            String content = getContent("src/test/resources/inbox/add/pressreleasesdetails_" + id + ".xml", "mongodbsimple");
            // send content to jms broker
            template.sendBody("jms:topic:BUS_OUT", content);

            // wait
            Thread.sleep(20000);

            // item should be inserted to db
            assertEquals(id+" is not inserted",1, articles.count(query));
        }

        assertEquals("not all items are present", ids.length, countArticles());

        // now check, that items are not insert twice if resend the same content
        for (String id : ids) {
            // load content
            String content = getContent("src/test/resources/inbox/add/pressreleasesdetails_" + id + ".xml", "mongodbsimple");
            // send content to jms broker
            template.sendBody("jms:topic:BUS_OUT", content);
            // wait
            Thread.sleep(20000);

            // only one item should be present
            DBObject query = new BasicDBObject();
            query.put("aid", Integer.parseInt(id));
            assertEquals(id+" is inserted twice",1, articles.count(query));
        }
    }

    /**
	 * Test delete.
	 *
	 * @throws Exception the exception
	 */
    @Test
    public void testDelete() throws Exception {


        String[] ids = new String[]{"128", "130", "131", "132", "256", "704"};
        for (String id : ids) {
            DBObject query = new BasicDBObject();
            query.put("aid", Long.parseLong(id));
            articles.remove(query);
        }

        // insert all items
        for (String id : ids) {
            // item should not be in the db
            DBObject query = new BasicDBObject();
            query.put("aid", Long.parseLong(id));
            assertEquals(id+" is still available (insert)",0, articles.count(query));

            // load content
            String content = getContent("src/test/resources/inbox/add/pressreleasesdetails_" + id + ".xml", "mongodbsimple");
            // send content to jms broker
            template.sendBody("jms:topic:BUS_OUT", content);
        }
        // wait
        Thread.sleep(30000);

        assertEquals("not all items are present", ids.length, countArticles());

        // now check, that items are not insert twice if resend the same content
        for (String id : ids) {
            // load content
            String content = getContent("src/test/resources/inbox/delete/pressreleasesdetails_" + id + ".xml", "mongodbsimple");
            // send content to jms broker
            template.sendBody("jms:topic:BUS_OUT", content);
            // wait
            Thread.sleep(10000);

            // item should be deleted
            DBObject query = new BasicDBObject();
            query.put("aid",Long.parseLong(id));
            assertEquals(id+" is still available (remove)",0, articles.count(query));
        }

        assertEquals("ups, there are items left in the db", 0, countArticles());
    }

	/**
	 * Test cleanup.
	 *
	 * @throws Exception the exception
	 */
    @Test
    public void testCleanup() throws Exception {


        String[] ids = new String[]{"128", "130", "131", "132", "256", "704"};
        for (String id : ids) {
            DBObject query = new BasicDBObject();
            query.put("aid",id);// Long.parseLong(id));
            articles.remove(query);
        }

        int numberOfArticles = ids.length;

        // insert all items
        for (String id : ids) {
            // item should not be in the db
            DBObject query = new BasicDBObject();
            query.put("aid", Long.parseLong(id));
            assertEquals(0, articles.count(query));

            // load content
            String content = getContent("src/test/resources/inbox/add/pressreleasesdetails_" + id + ".xml", "mongodbsimple");
            // patch content - add createTime
            content = content.replace("\"lastmodified\":\"\"", "\"lastmodified\":" + System.currentTimeMillis() );
            // send content to jms broker
            template.sendBody("jms:topic:BUS_OUT", content);
        }
        // wait
        Thread.sleep(10000);

        assertEquals("not all items are present", numberOfArticles, countArticles());

        // Save the current time as expiredate
        long expiredate = System.currentTimeMillis();

        ids = new String[]{"128", "130", "131"};
        int numberOfNewArticles = ids.length;

        // insert new items
        for (String id : ids) {
            // item should be in the db
            DBObject query = new BasicDBObject();
            query.put("aid", Long.parseLong(id));
            assertEquals(1, articles.count(query));

            // load content
            String content = getContent("src/test/resources/inbox/add/pressreleasesdetails_" + id + ".xml", "mongodbsimple");
            // patch content - add createTime
            content = content.replace("\"lastmodified\":\"\"", "\"lastmodified\":" + System.currentTimeMillis());
            // send content to jms broker
            template.sendBody("jms:topic:BUS_OUT", content);
        }
        // wait
        Thread.sleep(10000);

        assertEquals("not all items are present", numberOfArticles, countArticles());

        // now check, that old items are cleaned up
        for (String id : ids) {
            // load content
            String content = getContent("src/test/resources/inbox/cleanup/pressreleasesdetails.xml", "mongodbsimple");
            // patch content - add createTime
            content = content.replace("createTime=\"134678\"", "createTime=\"" + expiredate + "\"");
            // send content to jms broker
            template.sendBody("jms:topic:BUS_OUT", content);
        }
        // wait
        Thread.sleep(10000);

        // Check number of articles
        assertEquals("ups, there are too many items left in the db", numberOfNewArticles, countArticles());

        // Check article ids
        for (String id : ids) {
            // item should be in the db
            DBObject query = new BasicDBObject();
            query.put("aid", Long.parseLong(id));
            assertEquals(1, articles.count(query));
        }

    }

	/**
	 * Count articles.
	 *
	 * @return the long
	 * @throws Exception the exception
	 */
    private long countArticles() throws Exception {
        return articles.count();
    }

	/* (non-Javadoc)
	 * @see com.espirit.moddev.examples.uxbridge.newswidget.test.BaseTest#getContext()
	 */
    @Override
    public CamelContext getContext() {
        return camelContext;
    }
}
