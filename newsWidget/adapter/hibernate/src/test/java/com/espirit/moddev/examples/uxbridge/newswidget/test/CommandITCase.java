package com.espirit.moddev.examples.uxbridge.newswidget.test;

/*
 * //**********************************************************************
 * uxbridge.samples.newswidget.hibernate
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
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.xbean.spring.context.FileSystemXmlApplicationContext;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;

import javax.jms.ConnectionFactory;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;

/**
 * This class tests the ArticleHandler. It starts an own broker sends messages which will be processed by the ArticleHandler.
 * Afterwards it will query for the particular data records and check if they were written to the database successfully.
 */
public class CommandITCase extends BaseTest {

	/** The broker. */
	private static BrokerService broker;
	
	/** The camel context. */
	private static CamelContext camelContext;
	
	/** The emf. */
	private static EntityManagerFactory emf;

	/**
	 * Do before.
	 *
	 * @throws Exception the exception
	 */
    @BeforeClass
    public static void doBeforeClass() throws Exception {
        broker = new BrokerService();
        // configure the broker
        broker.addConnector("tcp://localhost:61500");
        broker.start();

        ApplicationContext ctx = new FileSystemXmlApplicationContext(getBasePath("hibernate") + "src/test/resources/applicationContext.xml");

        camelContext = (CamelContext) ctx.getBean("camelContext");

        camelContext.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("jms:topic:BUS_IN").
                        convertBodyTo(UXBEntity.class).to("mock:routeResponse").to("stream:out");
            }
        });

        camelContext.start();

        emf = (EntityManagerFactory) ctx.getBean("entityManagerFactory");
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
        emf.close();
    }

	/**
	 * Before test.
	 *
	 * @throws Exception the exception
	 */
    @Before
    public void beforeTest() throws Exception {
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();
        em.createQuery("DELETE FROM article").executeUpdate();
        tx.commit();
        em.close();
    }

	/* (non-Javadoc)
	 * @see org.apache.camel.test.junit4.CamelTestSupport#createCamelContext()
	 */
    protected CamelContext createCamelContext() throws Exception {
        CamelContext camelContext = super.createCamelContext();

        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61500");
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

        EntityManager em = emf.createEntityManager();


        String[] ids = new String[]{"128", "130", "131", "132", "256", "704"};

        // insert all items
        for (String id : ids) {
            // item should not be in the db
            Query query = em.createQuery(new StringBuilder().append("SELECT x FROM article x WHERE x.aid = ").append(id).toString());
            assertEquals(0, query.getResultList().size());

            // load content
            String content = getContent("src/test/resources/inbox/add/pressreleasesdetails_" + id + ".xml", "hibernate");
            // send content to jms broker
            template.sendBody("jms:topic:BUS_OUT", content);

            // wait
            Thread.sleep(TimeOuts.LONG);

            // item should be inserted to db
            query = em.createQuery(new StringBuilder().append("SELECT x FROM article x WHERE x.aid = ").append(id).toString());
            assertEquals(1, query.getResultList().size());
        }

        assertEquals("not all items are present", ids.length, countArticles());

        // now check, that items are not insert twice if resend the same content
        for (String id : ids) {
            // load content
            String content = getContent("src/test/resources/inbox/add/pressreleasesdetails_" + id + ".xml", "hibernate");
            // send content to jms broker
            template.sendBody("jms:topic:BUS_OUT", content);
            // wait
            Thread.sleep(TimeOuts.LONG);

            // only one item should be present
            Query query = em.createQuery(new StringBuilder().append("SELECT x FROM article x WHERE x.aid = ").append(id).toString());
            assertEquals(1, query.getResultList().size());
        }

        em.close();
    }

	/**
	 * Test delete.
	 *
	 * @throws Exception the exception
	 */
    @Test
    public void testDelete() throws Exception {

        assertEquals("DB not empty", 0, countArticles());

        EntityManager em = emf.createEntityManager();


        String[] ids = new String[]{"128", "130", "131", "132", "256", "704"};

        // insert all items
        for (String id : ids) {
            // item should not be in the db
            Query query = em.createQuery(new StringBuilder().append("SELECT x FROM article x WHERE x.aid = ").append(id).toString());
            assertEquals(0, query.getResultList().size());

            // load content
            String content = getContent("src/test/resources/inbox/add/pressreleasesdetails_" + id + ".xml", "hibernate");
            // send content to jms broker
            template.sendBody("jms:topic:BUS_OUT", content);
        }
        // wait
        Thread.sleep(TimeOuts.LONG);

        assertEquals("not all items are present", ids.length, countArticles());

        // now check, that items are not insert twice if resend the same content
        for (String id : ids) {
            // load content
            String content = getContent("src/test/resources/inbox/delete/pressreleasesdetails_" + id + ".xml", "hibernate");
            // send content to jms broker
            template.sendBody("jms:topic:BUS_OUT", content);
            // wait
            Thread.sleep(TimeOuts.LONG);

            // item should be deleted
            Query query = em.createQuery(new StringBuilder().append("SELECT x FROM article x WHERE x.aid = ").append(id).toString());
            assertEquals(0, query.getResultList().size());
        }

        assertEquals("ups, there are items left in the db", 0, countArticles());

        em.close();
    }

	/**
	 * Test cleanup.
	 *
	 * @throws Exception the exception
	 */
    @Test
    public void testCleanup() throws Exception {

        assertEquals("DB not empty", 0, countArticles());

        EntityManager em = emf.createEntityManager();


        String[] ids = new String[]{"128", "130", "131", "132", "256", "704"};
        int numberOfArticles = ids.length;

        // insert all items
        for (String id : ids) {
            // item should not be in the db
            Query query = em.createQuery(new StringBuilder().append("SELECT x FROM article x WHERE x.aid = ").append(id).toString());
            assertEquals(0, query.getResultList().size());

            // load content
            String content = getContent("src/test/resources/inbox/add/pressreleasesdetails_" + id + ".xml", "hibernate");
            // patch content - add createTime
            content = content.replace("<uxb_entity ", "<uxb_entity createTime=\"" + System.currentTimeMillis() + "\" ");
            // send content to jms broker
            template.sendBody("jms:topic:BUS_OUT", content);
        }
        // wait
        Thread.sleep(TimeOuts.LONG);

        assertEquals("not all items are present", numberOfArticles, countArticles());

        // Save the current time as expiredate
        long expiredate = System.currentTimeMillis();

        ids = new String[]{"128", "130", "131"};
        int numberOfNewArticles = ids.length;

        // insert new items
        for (String id : ids) {
            // item should be in the db
            Query query = em.createQuery(new StringBuilder().append("SELECT x FROM article x WHERE x.aid = ").append(id).toString());
            assertEquals(1, query.getResultList().size());

            // load content
            String content = getContent("src/test/resources/inbox/add/pressreleasesdetails_" + id + ".xml", "hibernate");
            // patch content - add createTime
            content = content.replace("<uxb_entity ", "<uxb_entity createTime=\"" + System.currentTimeMillis() + "\" ");
            // send content to jms broker
            template.sendBody("jms:topic:BUS_OUT", content);
        }
        // wait
        Thread.sleep(TimeOuts.LONG);

        assertEquals("not all items are present", numberOfArticles, countArticles());

        // now check, that old items are cleaned up
        for (String id : ids) {
            // load content
            String content = getContent("src/test/resources/inbox/cleanup/pressreleasesdetails.xml", "hibernate");
            // patch content - add createTime
            content = content.replace("<uxb_entity ", "<uxb_entity createTime=\"" + expiredate + "\" ");
            // send content to jms broker
            template.sendBody("jms:topic:BUS_OUT", content);
        }
        // wait
        Thread.sleep(TimeOuts.LONG);

        // Check number of articles
        assertEquals("ups, there are too many items left in the db", numberOfNewArticles, countArticles());

        // Check article ids
        for (String id : ids) {
            // item should be in the db
            Query query = em.createQuery(new StringBuilder().append("SELECT x FROM article x WHERE x.aid = ").append(id).toString());
            assertEquals(1, query.getResultList().size());
        }

        em.close();
    }

	/**
	 * Count articles.
	 *
	 * @return the long
	 * @throws Exception the exception
	 */
    private long countArticles() throws Exception {
        EntityManager em = emf.createEntityManager();
        Query query = em.createQuery("SELECT COUNT(p.title) FROM article p");
        Long countResult = (Long) query.getSingleResult();

        em.close();

        return countResult;
    }

	/* (non-Javadoc)
	 * @see com.espirit.moddev.examples.uxbridge.newswidget.test.BaseTest#getContext()
	 */
    @Override
    public CamelContext getContext() {
        return camelContext;
    }
}
