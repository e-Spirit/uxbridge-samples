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



import org.apache.activemq.broker.BrokerService;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.xbean.spring.context.FileSystemXmlApplicationContext;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;

/**
 * This class tests if the VirtualTopics between the broker and the adapter are working 
 *
 */
public class VirtualTopicsITCase extends BaseTest {
	
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

        ApplicationContext ctx = new FileSystemXmlApplicationContext(getBasePath("hibernate") + "src/test/resources/applicationContext_virtual.xml");

        camelContext = (CamelContext) ctx.getBean("camelContext");

        camelContext.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
//				from("activemq:Consumer.B.VirtualTopic.Orders").
//						convertBodyTo(UXBEntity.class).to("mock:routeResponse").to("stream:out");
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
            template.sendBody("activemq:topic:VirtualTopic.Orders", content);

            // wait
            Thread.sleep(TimeOuts.LONG);

            // item should be inserted to db
            query = em.createQuery(new StringBuilder().append("SELECT x FROM article x WHERE x.aid = ").append(id).toString());
            assertEquals(1, query.getResultList().size());
        }

        assertEquals("not all items are present", ids.length, countArticles());

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
