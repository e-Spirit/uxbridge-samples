package com.espirit.moddev.examples.uxbridge.newsdrilldown.test;

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




import com.espirit.moddev.examples.uxbridge.newsdrilldown.News;
import com.espirit.moddev.examples.uxbridge.newsdrilldown.NewsCategory;
import com.espirit.moddev.examples.uxbridge.newsdrilldown.NewsMetaCategory;
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

import com.espirit.moddev.examples.uxbridge.newsdrilldown.entity.UXBEntity;
import org.springframework.context.ApplicationContext;

import javax.jms.ConnectionFactory;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import java.util.List;


// TODO: Auto-generated Javadoc
/**
 * This class tests the NewsHandler. It starts an own broker sends messages which will be processed by the NewsHandler.
 * Afterwards it will query for the particular data records and check if they were written to the database successfully.
 */
public class MetaCategoryErrorITCase extends BaseTest {

	/** The ApplicationContext. */
	private static ApplicationContext ctx;
	
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
	public static void doBefore() throws Exception {
		broker = new BrokerService();
		// configure the broker
		broker.addConnector("tcp://localhost:61500");
		broker.start();

		ctx = new FileSystemXmlApplicationContext(getBasePath("hibernate") + "src/test/resources/applicationContext.xml");

		camelContext = (CamelContext) ctx.getBean("camelContext");

		camelContext.addRoutes(new RouteBuilder() {
			@Override
			public void configure() throws Exception {
				from("jms:topic:BUS_OUT").
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
		camelContext.stop();
		broker.stop();
		emf.close();

		((FileSystemXmlApplicationContext)ctx).close();
	}

	/**
	 * Before test.
	 *
	 * @throws Exception the exception
	 */
	@Before
	public void beforeTest() throws Exception {
		EntityManager em = emf.createEntityManager();
		try {
			EntityTransaction et = em.getTransaction();
			et.begin();
			Query query = em.createQuery(new StringBuilder().append("SELECT x FROM news x").toString());
			List<News> newsList = query.getResultList();
			for (News news : newsList) {
				em.remove(news);
			}

			query = em.createQuery(new StringBuilder().append("SELECT x FROM category x").toString());
			List<NewsCategory> catList = query.getResultList();
			for (NewsCategory temp : catList) {
				em.remove(temp);
			}

			query = em.createQuery(new StringBuilder().append("SELECT x FROM metaCategory x").toString());
			List<NewsMetaCategory> metaList = query.getResultList();
			for (NewsMetaCategory temp : metaList) {
				em.remove(temp);
			}
			et.commit();
		} finally {
			em.close();
		}

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
	public void testMetaCategory_Error_single_document() throws Exception {

		long size = countArticles();

		EntityManager em = emf.createEntityManager();


		String[] ids = new String[]{"1000", "1001"};

		// insert all items
		for (String id : ids) {
			// item should not be in the db
			Query query = em.createQuery(new StringBuilder().append("SELECT x FROM news x WHERE x.fs_id = ").append(id).toString());
			assertEquals(0, query.getResultList().size());

			// load content
			String content = getContent("src/test/resources/inbox/add/pressreleasesdetails_" + id + ".xml", "hibernate");
			// send content to jms broker
			template.sendBody("jms:topic:BUS_OUT", content);

			// wait
			Thread.sleep(TimeOuts.LONG);

			// item should be inserted to db
			query = em.createQuery(new StringBuilder().append("SELECT x FROM news x WHERE x.fs_id = ").append(id).toString());
			assertEquals(1, query.getResultList().size());
		}

		assertEquals("not all items are present", size + ids.length, countArticles());

		
		Query query = em.createQuery(new StringBuilder().append("SELECT x FROM category x WHERE x.fs_id = 2001").toString());
//		Query query = em.createQuery(new StringBuilder().append("SELECT x FROM category x WHERE x.fs_id = 3786").toString());
		NewsCategory cat = (NewsCategory) query.getSingleResult();
		
		assertEquals(2, cat.getMetaCategories().size());

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
		Query query = em.createQuery("SELECT COUNT(p.headline) FROM news p");
		Long countResult = (Long) query.getSingleResult();

		em.close();

		return countResult;
	}

	/**
	 * Count categories.
	 *
	 * @return the long
	 * @throws Exception the exception
	 */
	private long countCategories() throws Exception {
		EntityManager em = emf.createEntityManager();
		Query query = em.createQuery("SELECT COUNT(p.fs_id) FROM category p");
		Long countResult = (Long) query.getSingleResult();

		em.close();

		return countResult;
	}

	/**
	 * Count meta categories.
	 *
	 * @return the long
	 * @throws Exception the exception
	 */
	private long countMetaCategories() throws Exception {
		EntityManager em = emf.createEntityManager();
		Query query = em.createQuery("SELECT COUNT(p.fs_id) FROM metaCategory p");
		Long countResult = (Long) query.getSingleResult();

		em.close();

		return countResult;
	}

	/* (non-Javadoc)
	 * @see com.espirit.moddev.examples.uxbridge.newsdrilldown.test.BaseTest#getContext()
	 */
	@Override
	public CamelContext getContext() {
		return camelContext;
	}
}
