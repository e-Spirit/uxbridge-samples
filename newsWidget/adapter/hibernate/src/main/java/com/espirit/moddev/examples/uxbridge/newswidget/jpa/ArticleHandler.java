package com.espirit.moddev.examples.uxbridge.newswidget.jpa;

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


import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;

import com.espirit.moddev.examples.uxbridge.newswidget.AbstractHandler;
import com.espirit.moddev.examples.uxbridge.newswidget.Article;
import org.apache.camel.CamelContext;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.espirit.moddev.examples.uxbridge.newswidget.entity.UXBEntity;

/**
 * This class has the ability to add, delete and cleanup data records in a hibernate database
 * which might be used as content-repository for an UX-Bridge application.
 */
@Repository
public class ArticleHandler extends AbstractHandler {

    /**
     * The Constant logger.
     */
	private static final Logger logger = Logger.getLogger(ArticleHandler.class);

    /**
     * The Constant DESTINATION contains the destination of the messages coming from the broker.
     */
	private static final String DESTINATION = "postgres";

    /**
     * The emf.
     */
	private EntityManagerFactory emf;

    /**
     * The context.
     */
	private CamelContext context;

    /**
     * The response route.
     */
	private String responseRoute;
	
    /**
     * Instantiates a new ArticleHandler.
     *
     * @param context       the context
     * @param responseRoute the response route
     * @param emf           the emf
     */
	public ArticleHandler(CamelContext context, String responseRoute, EntityManagerFactory emf) {
		this.context = context;

		this.responseRoute = responseRoute;

		this.emf = emf;
	}


	public ArticleHandler() {
	}

	/**
	 * Add or update a news article in the db
	 *
	 * @param entity The NewsItem
	 */
	public void add(UXBEntity entity) {

		if (entity == null) {
			logger.warn("no UXBentity given, no action");
			sendStatusMessage(context, this.responseRoute, STATUS_FAIL , DESTINATION, "no UXBentity");
			return;
		}

		Article art = buildArticle(entity);

		EntityManager em = null;
		EntityTransaction tx = null;
		try {
			em = emf.createEntityManager();
			tx = em.getTransaction();
			tx.begin();

			Query query = em.createQuery(new StringBuilder().append("SELECT x FROM article x WHERE x.aid = :fsid AND x.language=:language").toString());
			query.setParameter("fsid", art.getAid());
			query.setParameter("language", art.getLanguage());

			/*
			* If the item exists in the db, we update the content and the title of the existing item
			*/
			if (query.getResultList().size() > 0) {
				Article db = (Article) query.getSingleResult();
				db.setContent(art.getContent());
				db.setTitle(art.getTitle());
				db.setUrl(art.getUrl());
				db.setCreated(art.getCreated());
				db.setAid(art.getAid());
				db.setLanguage(art.getLanguage());
				db.setVersion(art.getVersion());
				db.setLastmodified(art.getLastmodified());

				art = db;
			}
			// save to db
			em.persist(art);
			em.flush();

			sendStatusMessage(context, this.responseRoute, STATUS_OK, DESTINATION, null, entity);

			tx.commit();
		} catch (Exception e) {
			if (tx != null) {
				tx.setRollbackOnly();
			}
			logger.error("Failure while writing to the database", e);
			sendStatusMessage(context, this.responseRoute, STATUS_FAIL, DESTINATION, stackToString(e), entity);
		} finally {
			if (tx != null && tx.isActive()) {
				if (tx.getRollbackOnly()) {
					tx.rollback();
				}
			}
			if (em != null) {
				em.close();
			}
		}
	}

	/**
	 * Deletes a news article from the db
	 *
	 * @param entity The article to delete
	 */
	public void delete(UXBEntity entity) {

		if (entity == null) {
			logger.warn("no UXBentity given, no action");

			sendStatusMessage(context, this.responseRoute, STATUS_FAIL , DESTINATION, "no UXBentity");
			return;
		}

		EntityManager em = null;
		EntityTransaction tx = null;
		try {

			em = emf.createEntityManager();
			tx = em.getTransaction();
			tx.begin();

//			Query query = em.createQuery(new StringBuilder().append("SELECT x FROM Article x WHERE x.aid = ").append(entity.getUuid()).append(" AND x.language='").append(entity.getLanguage()).append("'").toString());
			Query query = em.createQuery(new StringBuilder().append("SELECT x FROM article x WHERE x.aid = :fsid AND x.language=:language").toString());
			query.setParameter("fsid", Long.parseLong(entity.getUuid()));
			query.setParameter("language", entity.getLanguage());

			if (!query.getResultList().isEmpty()) {
				Article art = (Article) query.getSingleResult();
                em.remove(art);
			}
			sendStatusMessage(context, this.responseRoute, STATUS_OK , DESTINATION, null, entity);

			tx.commit();
		} catch (Exception e) {
			if (tx != null) {
				tx.setRollbackOnly();
			}
			logger.error("Failure while deleting from the database", e);
			sendStatusMessage(context, this.responseRoute, STATUS_FAIL , DESTINATION, stackToString(e), entity);
		} finally {
			if (tx != null && tx.isActive()) {
				if (tx.getRollbackOnly()) {
					tx.rollback();
				}
			}
			if (em != null) {
				em.close();
			}
		}
	}


	/**
	 * Deletes every article older than the creationTime of the UXBEntity
	 *
	 * @param entity Entity containing the expireDate (= createTime of the entity)
	 */
	public void cleanup(UXBEntity entity) {

		if (entity == null) {
			logger.warn("no UXBentity given, no action");

			sendStatusMessage(context, this.responseRoute, STATUS_FAIL , DESTINATION, "no UXBentity");
			return;
		}

		EntityManager em = null;
		EntityTransaction tx = null;
		try {

			em = emf.createEntityManager();
			tx = em.getTransaction();
			tx.begin();

			Query query = em.createQuery(new StringBuilder().append("SELECT x FROM article x WHERE x.lastmodified<:expiredate ").toString());
			query.setParameter("expiredate", entity.getCreateTime());

			if (!query.getResultList().isEmpty()) {
				for (Object obj : query.getResultList()) {
					Article art = (Article) obj;
					em.remove(art);
				}
			}
			sendStatusMessage(context, this.responseRoute, STATUS_OK , DESTINATION, null, entity);

			tx.commit();
		} catch (Exception e) {
			if (tx != null) {
				tx.setRollbackOnly();
			}
			logger.error("Failure while deleting from the database", e);
			sendStatusMessage(context, this.responseRoute, STATUS_FAIL , DESTINATION, stackToString(e), entity);
		} finally {
			if (tx != null && tx.isActive()) {
				if (tx.getRollbackOnly()) {
					tx.rollback();
				}
			}
			if (em != null) {
				em.close();
			}
		}

	}

	/**
	 * Builds the news NewsArticle from an UXBEntity
	 * @param entity The entity
	 * @return The new article
	 */
	private Article buildArticle(UXBEntity entity) {
		Article art = new Article();
		art.setAid(Long.parseLong(entity.getUuid()));
		art.setContent(entity.getUxb_content().getContent());
		art.setCreated(entity.getUxb_content().getDate());
		art.setTitle(entity.getUxb_content().getHeadline());
		art.setLanguage(entity.getUxb_content().getLanguage());
		art.setUrl(entity.getUxb_content().getUrl());
		art.setLastmodified(entity.getCreateTime());

		return art;
	}
}
