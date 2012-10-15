package com.espirit.moddev.uxbridge.samples.workflow;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.espirit.moddev.uxbridge.api.v1.service.UxbService;

import de.espirit.firstspirit.access.AdminService;
import de.espirit.firstspirit.access.Connection;
import de.espirit.firstspirit.access.Language;
import de.espirit.firstspirit.access.ReferenceEntry;
import de.espirit.firstspirit.access.UserService;
import de.espirit.firstspirit.access.project.Project;
import de.espirit.firstspirit.access.schedule.GenerateTask;
import de.espirit.firstspirit.access.schedule.ScheduleEntry;
import de.espirit.firstspirit.access.schedule.ScheduleEntryControl;
import de.espirit.firstspirit.access.schedule.ScheduleStorage;
import de.espirit.firstspirit.access.schedule.ScheduleTask;
import de.espirit.firstspirit.access.store.LockException;
import de.espirit.firstspirit.access.store.Store;
import de.espirit.firstspirit.access.store.contentstore.Content2;
import de.espirit.firstspirit.access.store.contentstore.ContentWorkflowable;
import de.espirit.firstspirit.access.store.contentstore.Dataset;
import de.espirit.firstspirit.access.store.sitestore.PageRef;
import de.espirit.firstspirit.access.store.templatestore.Schema;
import de.espirit.firstspirit.access.store.templatestore.TableTemplate;
import de.espirit.firstspirit.access.store.templatestore.Template;
import de.espirit.firstspirit.access.store.templatestore.WorkflowScriptContext;
import de.espirit.or.Session;
import de.espirit.or.schema.Entity;
import de.espirit.or.schema.Identifier;
import de.espirit.or.schema.KeyValue;

public class DeleteEntityExecutableTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test_execute() throws Exception {
		
		Long NEWS_ID = 1L;
		
		@SuppressWarnings("unchecked")
		Map<String, Object> params = mock(Map.class);
		@SuppressWarnings("unchecked")
		Map<Object, Object> session = mock(Map.class);
		
		WorkflowScriptContext context = mock(WorkflowScriptContext.class);
		when(params.get("context")).thenReturn(context);
		
		ContentWorkflowable workflowable = mock(ContentWorkflowable.class);
		when(context.getWorkflowable()).thenReturn(workflowable);
		when(context.getSession()).thenReturn(session);
		
		Entity entity = mock(Entity.class);
		when(workflowable.getEntity()).thenReturn(entity);
		when(entity.get("fs_id")).thenReturn(NEWS_ID);
		KeyValue kv = mock(KeyValue.class);
		when(entity.getKeyValue()).thenReturn(kv);
		
		Content2 content = mock(Content2.class);
		TableTemplate template = mock(TableTemplate.class);
		when(workflowable.getContent()).thenReturn(content);
		when(context.getStoreElement()).thenReturn(content);
		when(content.getTemplate()).thenReturn(template);
		
		
		Schema schema = mock(Schema.class);
		when(template.getSchema()).thenReturn(schema);
		when(content.getTemplate().getSchema()).thenReturn(schema);
		
		when(schema.getIncomingReferences()).thenReturn(new ReferenceEntry[0]);
		when(schema.getIncomingReferences(entity)).thenReturn(new ReferenceEntry[0]);
		when(schema.getIncomingReferences(entity, true)).thenReturn(new ReferenceEntry[0]);
		
		Session entitySession = mock(Session.class);
		when(schema.getSession(true)).thenReturn(entitySession);
		when(entitySession.find(kv)).thenReturn(entity);
		
		Dataset ds = mock(Dataset.class);
		when(content.getDataset(entity)).thenReturn(ds);
		
		Connection connection = mock(Connection.class);
		when(context.getConnection()).thenReturn(connection);
		
		UxbService uxbservice = mock(UxbService.class);
		when(connection.getService(UxbService.class)).thenReturn(uxbservice);
		
		Language lang = mock(Language.class);
		Project project = mock(Project.class);
		List<Language> languages = new ArrayList<Language>();
		languages.add(lang);
		when(project.getLanguages()).thenReturn(languages);
		when(context.getProject()).thenReturn(project);
		
		DeleteEntityExecutable executable = new DeleteEntityExecutable();
		executable.execute(params);
		
		
		verify(ds, times(1)).delete();
		
		
		
	}

}
