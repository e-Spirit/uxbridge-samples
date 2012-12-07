package com.espirit.moddev.examples.uxbridge.newsdrilldown.jpa;

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


import com.espirit.moddev.examples.uxbridge.newsdrilldown.AbstractHandler;
import com.espirit.moddev.examples.uxbridge.newsdrilldown.News;
import com.espirit.moddev.examples.uxbridge.newsdrilldown.NewsCategory;
import com.espirit.moddev.examples.uxbridge.newsdrilldown.NewsMetaCategory;
import com.espirit.moddev.examples.uxbridge.newsdrilldown.entity.UXBCategory;
import com.espirit.moddev.examples.uxbridge.newsdrilldown.entity.UXBEntity;
import com.espirit.moddev.examples.uxbridge.newsdrilldown.entity.UXBMetaCategory;
import org.apache.camel.CamelContext;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import java.io.File;
import java.io.RandomAccessFile;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * This class has the ability to add, delete and cleanup data records in a hibernate database
 * which might be used as content-repository for an UX-Bridge application.
 *
 */
@Repository
public class NewsHandler{

    /**
     * The Constant logger.
     */
    private static final Logger logger = Logger.getLogger(NewsHandler.class);

    /**
     * The Constant DESTINATION contains the destination of the messages coming from the broker.
     */
    private static final String DESTINATION = "postgres";

    /**
     * The emf.
     */
    private EntityManagerFactory emf;

    /**
     * The webpath.
     */
    private String webpath;

    /**
     * Instantiates a new news handler.
     *
     * @param emf           the emf
     * @param webpath       the webpath
     */
    public NewsHandler(EntityManagerFactory emf, String webpath) {
        this.emf = emf;

        this.webpath = webpath;
    }

    /**
     * Instantiates a new news handler.
     */
    public NewsHandler() {
    }

    /**
     * Add or update a newsdrilldown in the db.
     *
     * @param entity The newsdrilldown
     */
    public void add(UXBEntity entity) throws Exception {

    		handle(entity);

        }

    /**
     * Deletes a newsdrilldown from the db.
     *
     * @param entity The newsdrilldown to delete
     */
    public void delete(UXBEntity entity) throws Exception{

        EntityManager em = null;
        EntityTransaction tx = null;
        try {

            em = emf.createEntityManager();
            tx = em.getTransaction();
            tx.begin();

            Query query = em
                    .createQuery(new StringBuilder()
                            .append("FROM news x WHERE x.fs_id = :fs_id AND x.language = :language")
                            .toString());
            query.setParameter("fs_id", Long.parseLong(entity.getUuid()));
            query.setParameter("language", entity.getLanguage());

            if (!query.getResultList().isEmpty()) {
                News art = (News) query.getSingleResult();
                // delete file from filesystem
                URL url = new URL(art.getUrl());
                File file = new File(webpath + url.getPath());
                if (file.exists()) {
                    // Try acquiring the lock without blocking. This method returns
                    // null or throws an exception if the file is already locked.
                    try {
                        FileChannel channel = new RandomAccessFile(file, "rw").getChannel();
                        // Try to lock the file
                        FileLock lock = channel.tryLock();
                        // Delete the file
                        file.delete();
                        // Release the lock
                        lock.release();
                        lock.channel().close();
                    } catch (OverlappingFileLockException e) {
                        logger.info("File is already locked in this thread or virtual machine");
                    } catch (MalformedURLException e) {
                    	logger.info("wrong url", e);
					}
                }
                // remove article from content repository

                em.remove(art);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx != null) {
                tx.setRollbackOnly();
            }
            throw e;
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
     * Deletes every item older than the creationTime of the UXBEntity.
     *
     * @param entity Entity containing the expireDate (= createTime of the entity)
     */
    public void cleanup(UXBEntity entity)  throws Exception {

        EntityManager em = null;
        EntityTransaction tx = null;
        try {

            em = emf.createEntityManager();
            tx = em.getTransaction();
            tx.begin();

            // Remove old newsdrilldown
            Query query = em.createQuery(new StringBuilder().append("SELECT x FROM news x WHERE x.lastmodified<:expiredate ").toString());
            query.setParameter("expiredate", entity.getCreateTime());

            if (!query.getResultList().isEmpty()) {
                for (Object obj : query.getResultList()) {
                    News art = (News) obj;
                    em.remove(art);
                }
            }

            // Remove old newsCategories
            query = em.createQuery(new StringBuilder().append("SELECT x FROM category x WHERE x.lastmodified<:expiredate ").toString());
            query.setParameter("expiredate", entity.getCreateTime());

            if (!query.getResultList().isEmpty()) {
                for (Object obj : query.getResultList()) {
                    NewsCategory art = (NewsCategory) obj;
                    em.remove(art);
                }
            }

            // Remove old newsMetaCategories
            query = em.createQuery(new StringBuilder().append("SELECT x FROM metaCategory x WHERE x.lastmodified<:expiredate ").toString());
            query.setParameter("expiredate", entity.getCreateTime());

            if (!query.getResultList().isEmpty()) {
                for (Object obj : query.getResultList()) {
                    NewsMetaCategory art = (NewsMetaCategory) obj;
                    em.remove(art);
                }
            }

            tx.commit();
        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.setRollbackOnly();
            }
            logger.error("Failure while deleting from the database", e);
            throw e;
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
     * handle the update and save process.
     *
     * @param entity the newsdrilldown entity
     * @throws Exception the exception
     */
    private void handle(UXBEntity entity) throws Exception {
        /**
         * Due to a hibernate problem when updating or saving a newsdrilldown with
         * related categories we have to perform these step for saving the newsdrilldown
         *
         * 1. save/update all categories and remove them from the newsdrilldown
         * 	1.1 save/update all metaCategories and remove them from the categories
         * 	1.2 save the categories
         * 	1.3 read all metaCategories to the categories
         * 	1.4 save the categories again to create the relations
         * 2. save the newsdrilldown
         * 3. read all categories to the newsdrilldown
         */

        EntityManager em = null;
        EntityTransaction tx = null;
        try {
            News news = buildNews(entity);
            List<Long> categories = new ArrayList<Long>();

            /*
                * 1. update or save all categories
                * Steps 1.1 - 1.4
                */
            if (news.getCategories() != null && !news.getCategories().isEmpty()) {
                for (NewsCategory cat : news.getCategories()) {
                    cat = saveNewsCategory(cat);
                    if (!categories.contains(cat.getFs_id())) {
                    	categories.add(cat.getFs_id());
                    }
                }
                news.setCategories(new ArrayList<NewsCategory>());
            }


            em = emf.createEntityManager();
            tx = em.getTransaction();
            tx.begin();

            // 2. save the newsdrilldown
            news = saveNews(news, em);


            // 3. read all categories to the newsdrilldown
            if (!categories.isEmpty()) {
                for (Long cat : categories) {
                	NewsCategory ncat = getNewsCategory(cat, news.getLanguage(), em);
                	news.getCategories().add(ncat);
                }
            }


            tx.commit();

        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.setRollbackOnly();
            }
            throw e;
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
     * selects a newsdrilldown by the FirstSpirit-ID.
     *
     * @param fs_id    the FirstSpirit ID
     * @param language the language
     * @return the newsdrilldown or null
     * @throws Exception the exception
     */
    private News getNews(Long fs_id, String language) throws Exception {

        EntityManager em = null;
        try {
            em = emf.createEntityManager();

            Query query = em.createQuery(new StringBuilder().append(
                    "SELECT x FROM news x WHERE x.fs_id = :fs_id AND x.language = :language").toString());
            query.setParameter("fs_id", fs_id);
            query.setParameter("language", language);

            if (query.getResultList().size() > 0) {
                return (News) query.getSingleResult();
            }

            return null;
        } finally {

            if (em != null) {
                em.close();
            }
        }
    }

    /**
     * saves or update a newsdrilldown.
     *
     * @param news the newsdrilldown to save
     * @param em   the em
     * @return the saved newsdrilldown, the new db id will be set
     * @throws Exception the exception
     */
    private News saveNews(News news, EntityManager em) throws Exception {


        News n = getNews(news.getFs_id(), news.getLanguage());
        // newsdrilldown with FirstSpirit-ID exists

        if (n != null) {
            // update newsdrilldown

            n.setFs_id(news.getFs_id());
            n.setContent(news.getContent());
            n.setDate(news.getDate());
            n.setHeadline(news.getHeadline());
            n.setSubheadline(news.getSubheadline());
            n.setTeaser(news.getTeaser());
            n.setLanguage(news.getLanguage());
            n.setUrl(news.getUrl());
            n.setVersion(1);
            n.setLastmodified(news.getLastmodified());

            n.setCategories(news.getCategories());

            news = em.merge(n);
        } else {
            // save new newsdrilldown
            em.persist(news);
        }

        return news;

    }

    /**
     * saves or updates a newscategory.
     *
     * @param category the category
     * @return the category with the new id
     * @throws Exception the exception
     */
    private NewsCategory saveNewsCategory(NewsCategory category)
            throws Exception {

        EntityManager em = null;
        EntityTransaction tx = null;
        try {
            em = emf.createEntityManager();
            tx = em.getTransaction();
            tx.begin();
            // try loading the category for the firstspirit id
            NewsCategory cat = getNewsCategory(category.getFs_id(),
                    category.getLanguage(), em);

            if (cat != null) {

                List<NewsMetaCategory> metaCats = category.getMetaCategories();

                // the already persistent categories
                List<NewsMetaCategory> original_metaCats = cat.getMetaCategories();

                // update existing category
                cat.setMetaCategories(new ArrayList<NewsMetaCategory>());

                for (NewsMetaCategory metaCat : metaCats) {
                    metaCat = saveNewsMetaCategory(metaCat, em);
                    cat.getMetaCategories().add(metaCat);

                    original_metaCats.remove(metaCat);
                }
                for (NewsMetaCategory mc : original_metaCats) {
                	mc.setLastmodified(category.getLastmodified());
                }
                cat.getMetaCategories().addAll(original_metaCats);


                cat.setFs_id(category.getFs_id());
                cat.setLanguage(category.getLanguage());
                cat.setName(category.getName());
                cat.setVersion(category.getVersion());
                cat.setLastmodified(category.getLastmodified());

                // update
                category = em.merge(cat);
            } else {
                updateMetaCategories(category, em);
                // save to db
                em.persist(category);
            }

            tx.commit();

            return category;

        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.setRollbackOnly();
            }
            logger.error("", e);
            throw e;
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
     * Update meta categories.
     *
     * @param category the category
     * @param em       the em
     * @throws Exception the exception
     */
    private void updateMetaCategories(NewsCategory category, EntityManager em)
            throws Exception {
        for (NewsMetaCategory metaCat : category.getMetaCategories()) {
            saveNewsMetaCategory(metaCat, em);
        }
        List<NewsMetaCategory> mcats = category.getMetaCategories();
        category.setMetaCategories(new ArrayList<NewsMetaCategory>());
        for (NewsMetaCategory metaCat : mcats) {
            category.getMetaCategories().add(
                    getNewsMetaCategory(metaCat.getFs_id(),
                            metaCat.getLanguage(), em));
        }
    }

    /**
     * saves or updates a newscategory.
     *
     * @param metaCategory the category
     * @param em           the em
     * @return the category with the new id
     * @throws Exception the exception
     */
    private NewsMetaCategory saveNewsMetaCategory(
            NewsMetaCategory metaCategory, EntityManager em) throws Exception {

        // try loading the category for the firstspirit id
        NewsMetaCategory cat = getNewsMetaCategory(metaCategory.getFs_id(),
                metaCategory.getLanguage(), em);

        if (cat != null) {
            // update existing category
            cat.setFs_id(metaCategory.getFs_id());
            cat.setLanguage(metaCategory.getLanguage());
            cat.setName(metaCategory.getName());
            cat.setVersion(metaCategory.getVersion());
            cat.setLastmodified(metaCategory.getLastmodified());

            // update
            cat = em.merge(cat);

            return cat;
        } else {
            // save to db
            em.persist(metaCategory);
        }

        return metaCategory;
    }

    /**
     * builds the newsdrilldown from a UXBEntity.
     *
     * @param entity the entity
     * @return the newsdrilldown
     */
    private News buildNews(UXBEntity entity) {
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

        Map<NewsCategory, List<NewsMetaCategory>> cat_metacats = new HashMap<NewsCategory, List<NewsMetaCategory>>();

        if (entity.getUxb_content().getMetaCategories() != null) {
            for (UXBMetaCategory uxMetaCat : entity.getUxb_content()
                    .getMetaCategories()) {

            	NewsMetaCategory mcat = buildMetaCategory(uxMetaCat, entity);

                if (uxMetaCat.getCategories() != null) {
                    for (UXBCategory uxCat : uxMetaCat.getCategories()) {
                        NewsCategory newsCat = buildNewsCategory(uxCat, entity);

                        if (!cat_metacats.containsKey(newsCat)) {
                        	cat_metacats.put(newsCat, new ArrayList<NewsMetaCategory>());
                        }
                        if (!cat_metacats.get(newsCat).contains(mcat)) {
                        	cat_metacats.get(newsCat).add(mcat);
                        }
//                        news.getCategories().add(newsCat);
                    }
                }
            }
        }
        for (NewsCategory category : cat_metacats.keySet()) {
        	category.getMetaCategories().addAll(cat_metacats.get(category));

        	news.getCategories().add(category);
        }

        return news;
    }

    /**
     * gets the category for the UXBCategory JAXB-Entity.
     *
     * @param uxCat  The UXBCategory
     * @param entity The UXBEntity
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

        /*
        if (entity.getUxb_content().getMetaCategories() != null) {
            for (UXBMetaCategory uxMetaCat : entity.getUxb_content()
                    .getMetaCategories()) {
                newsCat.getMetaCategories().add(
                        buildMetaCategory(uxMetaCat, entity));
            }
        }
        */

        return newsCat;
    }

    /**
     * gets the category for its fs_id.
     *
     * @param fs_id    The fs_id
     * @param language the language
     * @param em       the em
     * @return the NewsCategory instance
     */
    private NewsCategory getNewsCategory(Long fs_id, String language,
                                         EntityManager em) {

        boolean close = false;
        try {
            if (em == null) {
                em = emf.createEntityManager();
                close = true;
            }

            Query categoryQuery = em
                    .createQuery(new StringBuilder()
                            .append("SELECT x FROM category x WHERE x.fs_id = :fs_id AND x.language = :language")
                            .toString()); // or fsidindex2?
            categoryQuery.setParameter("fs_id", fs_id);
            categoryQuery.setParameter("language", language);

            if (categoryQuery.getResultList().size() > 0) {
                return (NewsCategory) categoryQuery.getSingleResult();
            }

            return null;
        } finally {
            if (close && em != null) {
                em.close();
            }
        }
    }

    /**
     * Builds the meta category.
     *
     * @param uxMetaCat The UXBMetaCategory
     * @param entity    The UXBEntity
     * @return a new MetaCategory instance
     */
    private NewsMetaCategory buildMetaCategory(UXBMetaCategory uxMetaCat,
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
     * gets the metaCategory for its fs_id.
     *
     * @param fs_id    The fs_id
     * @param language the language
     * @param em       the em
     * @return the NewsCategory instance
     */
    private NewsMetaCategory getNewsMetaCategory(Long fs_id, String language,
                                                 EntityManager em) {

        Query categoryQuery = em
                .createQuery(new StringBuilder()
                        .append("SELECT x FROM metaCategory x WHERE x.fs_id = :fs_id AND x.language = :language")
                        .toString()); // or fsidindex2?
        categoryQuery.setParameter("fs_id", fs_id);
        categoryQuery.setParameter("language", language);

        if (categoryQuery.getResultList().size() > 0) {
            return (NewsMetaCategory) categoryQuery.getSingleResult();
        }

        return null;
    }

}
