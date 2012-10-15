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

import de.espirit.firstspirit.access.AdminService;
import de.espirit.firstspirit.access.Connection;
import de.espirit.firstspirit.access.ReferenceEntry;
import de.espirit.firstspirit.access.UserService;
import de.espirit.firstspirit.access.schedule.GenerateTask;
import de.espirit.firstspirit.access.schedule.ScheduleEntry;
import de.espirit.firstspirit.access.schedule.ScheduleEntryControl;
import de.espirit.firstspirit.access.schedule.ScheduleStorage;
import de.espirit.firstspirit.access.schedule.ScheduleTask;
import de.espirit.firstspirit.access.store.LockException;
import de.espirit.firstspirit.access.store.Store;
import de.espirit.firstspirit.access.store.contentstore.Content2;
import de.espirit.firstspirit.access.store.contentstore.ContentWorkflowable;
import de.espirit.firstspirit.access.store.sitestore.PageRef;
import de.espirit.firstspirit.access.store.templatestore.Schema;
import de.espirit.firstspirit.access.store.templatestore.WorkflowScriptContext;
import de.espirit.or.Session;
import de.espirit.or.schema.Entity;
import de.espirit.or.schema.Identifier;

public class ReleaseAndDeployEntityExecutableTest {

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
		
		String QUERY_UID = "Products.pressdetailsfilter";
        String SINGLE_QUERY_UID = "Products.pressdetailfilter";
        String SCHEDULER_NAME = "UX-Bridge";
        String SCHEDULER_TASK = "UX-Bridge Generate";
        String TRANSITION_NAME = "release";
        String DETAIL_PAGE = "pressreleasesdetails";
        String QUERY_PARAM = "Id";
        
        Long NEWS_ID = 1L;
        Long detailPageId = 15L;
		
		@SuppressWarnings("unchecked")
		Map<String, Object> params = mock(Map.class);
		@SuppressWarnings("unchecked")
		Map<Object, Object> session = mock(Map.class);
		
		WorkflowScriptContext context = mock(WorkflowScriptContext.class);
		when(params.get("context")).thenReturn(context);
		
		ContentWorkflowable workflowable = mock(ContentWorkflowable.class);
		when(context.getWorkflowable()).thenReturn(workflowable);
		when(context.getSession()).thenReturn(session);
		
		when(session.get("query_uid")).thenReturn(QUERY_UID);
		when(session.get("single_query_uid")).thenReturn(SINGLE_QUERY_UID);
		when(session.get("scheduler_name")).thenReturn(SCHEDULER_NAME);
		when(session.get("transition_name")).thenReturn(TRANSITION_NAME);
		when(session.get("detail_page")).thenReturn(DETAIL_PAGE);
		when(session.get("query_param")).thenReturn(QUERY_PARAM);
		when(session.get("scheduler_task")).thenReturn(SCHEDULER_TASK);
		
		Content2 content = mock(Content2.class);
		when(workflowable.getContent()).thenReturn(content);
		
		Schema schema = mock(Schema.class);
		when(content.getSchema()).thenReturn(schema);
		
		Entity entity = mock(Entity.class);
		when(workflowable.getEntity()).thenReturn(entity);
		when(entity.get("fs_id")).thenReturn(NEWS_ID);
		
		
		Identifier identifier = mock(Identifier.class);
		when(entity.getIdentifier()).thenReturn(identifier);
		
		when(schema.getOutgoingReferences(entity)).thenReturn(new ReferenceEntry[0]);
		
		Session entitySession = mock(Session.class);
		when(entity.getSession()).thenReturn(entitySession);
		
		UserService userService = mock(UserService.class);
		when(context.getUserService()).thenReturn(userService);
		
		Store siteStore = mock(Store.class);
		when(context.getUserService().getStore(Store.Type.SITESTORE, false)).thenReturn(siteStore);
		
		PageRef pageRef = mock(PageRef.class);
		when(siteStore.getStoreElement(DETAIL_PAGE, PageRef.UID_TYPE)).thenReturn(pageRef);
		when(pageRef.getId()).thenReturn(detailPageId);
		
		Connection connection = mock(Connection.class);
		when(context.getConnection()).thenReturn(connection);
		
		AdminService adminService = mock(AdminService.class);
		when(connection.getService(AdminService.class)).thenReturn(adminService);
		
		ScheduleStorage scheduleStorage = mock(ScheduleStorage.class);
		when(adminService.getScheduleStorage()).thenReturn(scheduleStorage);
		
		List<ScheduleEntry> allEntriesProject = new ArrayList<ScheduleEntry>();
		ScheduleEntry scheduleEntry = mock(ScheduleEntry.class);
		when(scheduleEntry.getName()).thenReturn(SCHEDULER_NAME);
		allEntriesProject.add(scheduleEntry);
		
		when(scheduleStorage.getScheduleEntries(context.getProject())).thenReturn(allEntriesProject);
		
		
		List<ScheduleTask> scheduleTasks = new ArrayList<ScheduleTask>();
		GenerateTask task = mock(GenerateTask.class);
		when(task.getName()).thenReturn(SCHEDULER_TASK);
		when(task.getEntityStartNodes()).thenReturn(new ArrayList<GenerateTask.EntityEntry>());
		scheduleTasks.add(task);
		
		when(scheduleEntry.getTasks()).thenReturn(scheduleTasks);

		ScheduleEntryControl scheduleEntryControl = mock(ScheduleEntryControl.class);
		when(scheduleEntry.execute()).thenReturn(scheduleEntryControl);
		
		
		
		
		GenerateTask.EntityEntry entityEntry = mock(GenerateTask.EntityEntry.class);
		when(task.createEntityEntry(NEWS_ID, detailPageId)).thenReturn(entityEntry);
		
				
		
		ReleaseAndDeployEntityExecutable executable = new ReleaseAndDeployEntityExecutable();
		executable.execute(params);
		
		
		verify(scheduleEntry, times(1)).lock();
//		verify(task, times(1)).getEntityStartNodes().add(entityEntry);
		assertEquals(1, task.getEntityStartNodes().size());
		verify(scheduleEntry, times(1)).unlock();
		verify(scheduleEntry, times(1)).execute();
	}

}
