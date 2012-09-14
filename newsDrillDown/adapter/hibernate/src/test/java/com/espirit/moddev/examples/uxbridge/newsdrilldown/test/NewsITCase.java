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


import static org.junit.Assert.*;
import com.espirit.moddev.examples.uxbridge.newsdrilldown.News;
import com.espirit.moddev.examples.uxbridge.newsdrilldown.NewsCategory;
import com.espirit.moddev.examples.uxbridge.newsdrilldown.NewsMetaCategory;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

// TODO: Auto-generated Javadoc
/**
 * This class tests if the persistence is working properly.
 * 
 */
public class NewsITCase {

	/** The ctx. */
	private static ApplicationContext ctx;
	
	/** The emf. */
	private static EntityManagerFactory emf;

	/**
	 * Do before class.
	 *
	 * @throws Exception the exception
	 */
	@BeforeClass
	public static void doBeforeClass() throws Exception {
		ctx = new ClassPathXmlApplicationContext("/applicationContext.xml");

		emf = (EntityManagerFactory) ctx.getBean("entityManagerFactory");
	}

	/**
	 * Shutdown.
	 *
	 * @throws Exception the exception
	 */
	@AfterClass
	public static void shutdown() throws Exception {
		emf.close();

		((ClassPathXmlApplicationContext) ctx).close();
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
			Query query = em.createQuery(new StringBuilder().append(
					"SELECT x FROM news x").toString());
			List<News> newsList = query.getResultList();
			for (News news : newsList) {
				em.remove(news);
			}

			query = em.createQuery(new StringBuilder().append(
					"SELECT x FROM category x").toString());
			List<NewsCategory> catList = query.getResultList();
			for (NewsCategory temp : catList) {
				em.remove(temp);
			}

			query = em.createQuery(new StringBuilder().append(
					"SELECT x FROM metaCategory x").toString());
			List<NewsMetaCategory> metaList = query.getResultList();
			for (NewsMetaCategory temp : metaList) {
				em.remove(temp);
			}
		} finally {
			em.close();
		}
	}

	/**
	 * Test persistence.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void testPersistence() throws Exception {

		long size = countNews();

		EntityManager em = emf.createEntityManager();
		EntityTransaction tx = null;
		try {
			tx = em.getTransaction();
			tx.begin();

			News news = new News();

			news.setContent("Spider-man was seen.");
			news.setHeadline("The amazing spider-man");
			news.setFs_id(1l);
			news.setLanguage("DE");
			news.setTeaser("spider-man");

			NewsCategory sport = new NewsCategory();
			sport.setFs_id(2l);
			sport.setName("Sport");
			sport.setLanguage("DE");

			NewsMetaCategory soccer = new NewsMetaCategory();
			soccer.setFs_id(3l);
			soccer.setLanguage("DE");
			soccer.setName("Fussball");

			if (sport.getMetaCategories() == null) {
				sport.setMetaCategories(new ArrayList<NewsMetaCategory>());
			}
			sport.getMetaCategories().add(soccer);

			if (news.getCategories() == null) {
				news.setCategories(new ArrayList<NewsCategory>());
			}
			news.getCategories().add(sport);

			em.persist(news);
			em.flush();
			tx.commit();

			assertEquals("Entity not filled", size+1, countNews());

			Query query = em
					.createQuery("SELECT mc FROM metaCategory mc WHERE fs_id=3");
			NewsMetaCategory metaCat = (NewsMetaCategory) query
					.getSingleResult();
			assertNotNull(metaCat);

			query = em
					.createQuery("SELECT c FROM category c join c.metaCategories mc WHERE mc.fs_id="
							+ metaCat.getFs_id());
			NewsCategory category = (NewsCategory) query.getSingleResult();
			assertNotNull(category);

			query = em
					.createQuery("SELECT n FROM news n join n.categories c WHERE c.fs_id="
							+ category.getFs_id());
			News n = (News) query.getSingleResult();
			assertNotNull(n);

		} finally {
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			em.close();
		}

	}

	/**
	 * Count news.
	 *
	 * @return the long
	 * @throws Exception the exception
	 */
	private long countNews() throws Exception {
		EntityManager em = emf.createEntityManager();
		Query query = em.createQuery("SELECT COUNT(p.fs_id) FROM news p");
		Long countResult = (Long) query.getSingleResult();

		em.close();

		return countResult;
	}
}
