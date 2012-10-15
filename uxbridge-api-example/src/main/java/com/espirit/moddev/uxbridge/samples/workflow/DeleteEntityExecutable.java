package com.espirit.moddev.uxbridge.samples.workflow;

import java.io.PrintWriter;
import java.io.Writer;
import java.util.Map;

import com.espirit.moddev.uxbridge.samples.AbstractExecutable;
import com.espirit.moddev.uxbridge.api.v1.service.UxbService;

import de.espirit.firstspirit.access.Language;
import de.espirit.firstspirit.access.Workflowable;
import de.espirit.firstspirit.access.script.Executable;
import de.espirit.firstspirit.access.script.ExecutionException;
import de.espirit.firstspirit.access.store.LockException;
import de.espirit.firstspirit.access.store.contentstore.Content2;
import de.espirit.firstspirit.access.store.contentstore.ContentWorkflowable;
import de.espirit.firstspirit.access.store.contentstore.Dataset;
import de.espirit.firstspirit.access.store.templatestore.Schema;

import de.espirit.or.Session;
import de.espirit.or.schema.Entity;

public class DeleteEntityExecutable extends AbstractExecutable {
	private static final Class<?> LOGGER = DeleteEntityExecutable.class;

	/*
	 * the destinations the message should be send to
	 */
	private String DESTINATIONS = "postgres,mongodb";
	/*
	 * the command
	 */
	private String TRANSITION_NAME = "delete";
	/*
	 * the object type of the message
	 */
	private String OBJECT_TYPE = "news";

	public Object execute(Map<String, Object> params, Writer writer,
			Writer writer1) throws ExecutionException {

		Workflowable workflowable = context.getWorkflowable();
		Content2 content2 = (Content2) context.getStoreElement();
		boolean result = false;

		try {
			if (context.getSession().get("destinations") != null) {
				this.DESTINATIONS = (String) context.getSession().get(
						"destinations");
			}
			if (context.getSession().get("transition_name") != null) {
				this.TRANSITION_NAME = (String) context.getSession().get(
						"transition_name");
			}
			if (context.getSession().get("object_type") != null) {
				this.OBJECT_TYPE = (String) context.getSession().get(
						"object_type");
			}
			// delete in FS
			if (workflowable instanceof ContentWorkflowable) {
				Entity entity = ((ContentWorkflowable) workflowable)
						.getEntity();
				Long id;
				Schema s = content2.getTemplate().getSchema();
				if (s.getIncomingReferences(entity, true).length == 0
						&& s.getIncomingReferences(entity).length == 0) {
					workflowable.delete();
					deleteEntityInReleaseStand(content2, entity);
					result = true;
					// delete in content repository
					try {
						id = (Long) entity.getKeyValue().getValue(0);
					} catch (ClassCastException c) {
						id = Long.valueOf((Integer) entity.getKeyValue()
								.getValue(0));
					}
					for (Language language : context.getProject()
							.getLanguages()) {
						String msg = new StringBuilder()
								.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n")
								.append("<uxb_entity uuid=\"").append(id)
								.append("\" language=\"").append(language)
								.append("\" destinations=\"")
								.append(DESTINATIONS)
								.append("\" objectType=\"").append(OBJECT_TYPE)
								.append("\" command=\"delete\" />").toString();
						UxbService uxbService = context.getConnection()
								.getService(UxbService.class);
						uxbService.removeUxbEntry(msg);
					}
				} else {
					context.logError("Incoming references found");
				}

				if (!result) {
					context.logError("Could not delete entity");
				} else {
					context.logInfo("Deletion of entity successful");
				}
			} else {
				context.logError("Object has to be an implementation of ContentWorkflowable");
			}
		} catch (LockException e) {
			context.logError(
					"Error while executing workflow script. Item already locked",
					e);
		} finally {
			try {
				context.doTransition(TRANSITION_NAME);
			} catch (IllegalAccessException e) {
				context.logError("transition error: ", e);
			}
		}

		return true;
	}

	boolean deleteEntityInReleaseStand(Content2 c, Entity e) {
		boolean result = false;
		Schema s = c.getTemplate().getSchema();
		Session releaseSession = c.getTemplate().getSchema().getSession(true);
		if (s.getIncomingReferences(e, true).length == 0) {
			releaseSession.rollback();
			Entity eRelease = releaseSession.find(e.getKeyValue());
			try {
				Dataset eDS = c.getDataset(eRelease);
				eDS.delete();
			} catch (LockException le) {
				context.logError("Lockexception: ", le);
				le.printStackTrace();
			} finally {
				try {
					c.unlock(eRelease);
					releaseSession.commit();
					result = true;
				} catch (LockException le) {
					le.printStackTrace();
				}
				releaseSession.rollback();
			}
		} else {
			context.logError("Incoming references found");
		}
		return result;
	}
}
