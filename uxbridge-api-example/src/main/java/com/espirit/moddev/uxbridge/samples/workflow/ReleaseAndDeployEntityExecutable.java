package com.espirit.moddev.uxbridge.samples.workflow;

import de.espirit.firstspirit.access.*;
import de.espirit.firstspirit.access.schedule.*;
import de.espirit.firstspirit.access.script.ExecutionException;
import de.espirit.firstspirit.access.store.IDProvider;
import de.espirit.firstspirit.access.store.LockException;
import de.espirit.firstspirit.access.store.ReleaseProgress;
import de.espirit.firstspirit.access.store.Store;
import de.espirit.firstspirit.access.store.contentstore.ContentWorkflowable;
import de.espirit.firstspirit.access.store.mediastore.Media;
import de.espirit.firstspirit.access.store.sitestore.Content2Params;
import de.espirit.firstspirit.access.store.sitestore.PageRef;
import de.espirit.firstspirit.access.store.templatestore.Query;
import de.espirit.firstspirit.agency.ServerInformationAgent;
import de.espirit.or.schema.Entity;

import java.io.Writer;
import java.util.*;

import com.espirit.moddev.uxbridge.samples.AbstractExecutable;

public class ReleaseAndDeployEntityExecutable extends AbstractExecutable {
    @Override
    public Object execute(Map<String, Object> params, Writer out, Writer err) throws ExecutionException {

        ContentWorkflowable workflowable = (ContentWorkflowable) context.getWorkflowable();

        //ArrayList<String> pageRefsToPatchList = new ArrayList<String>();
        String QUERY_UID = "Products.pressdetailsfilter";
        String SINGLE_QUERY_UID = "Products.pressdetailfilter";
        String SCHEDULER_NAME = "UX-Bridge";
        String SCHEDULER_TASK = "UX-Bridge Generate";
        String TRANSITION_NAME = "release";
        String DETAIL_PAGE = "pressreleasesdetails";
        String QUERY_PARAM = "Id";

        boolean checkOnly = false;          // Testlauf
        boolean releaseParentPath = true;   // Vaterkette freigeben
        boolean recursive = false;          // Recursive
        IDProvider.DependentReleaseType dependentType = IDProvider.DependentReleaseType.DEPENDENT_RELEASE_NEW_AND_CHANGED;

        if(context.getSession().get("query_uid")!=null) {
            QUERY_UID = (String) context.getSession().get("query_uid");
        }
        if(context.getSession().get("single_query_uid")!=null) {
            SINGLE_QUERY_UID = (String) context.getSession().get("single_query_uid");
        }
        if(context.getSession().get("scheduler_name")!=null) {
            SCHEDULER_NAME = (String) context.getSession().get("scheduler_name");
        }
        if(context.getSession().get("transition_name")!=null) {
            TRANSITION_NAME = (String) context.getSession().get("transition_name");
        }
        if(context.getSession().get("detail_page")!=null) {
            DETAIL_PAGE = (String) context.getSession().get("detail_page");
        }
        if(context.getSession().get("query_param")!=null) {
            QUERY_PARAM = (String) context.getSession().get("query_param");
        }
        if(context.getSession().get("scheduler_task")!=null) {
            SCHEDULER_TASK = (String) context.getSession().get("scheduler_task");
        }

//        Store templateStore = context.getUserService().getStore(Store.Type.TEMPLATESTORE, false);
//        Query defaultQuery = (Query) templateStore.getStoreElement(QUERY_UID, Query.UID_TYPE);
//        Query singleQuery = (Query) templateStore.getStoreElement(SINGLE_QUERY_UID, Query.UID_TYPE);

        try {
            if (workflowable instanceof ContentWorkflowable) {
                de.espirit.firstspirit.access.store.templatestore.Schema schema = workflowable.getContent().getSchema();
                Integer news_id = ((Long) workflowable.getEntity().get("fs_id")).intValue();
                Entity entity = workflowable.getEntity();
                context.logDebug("Entity is:" + entity.getIdentifier().toString());


                ReferenceEntry[] referenceList = schema.getOutgoingReferences(entity);
                context.logDebug("Number of outgoingReferences: " + referenceList.length);

                //TODO refactor for loop to method
                for (ReferenceEntry referenceEnty : referenceList) {
                    IDProvider element = referenceEnty.getReferencedElement();
                    if (element != null) {
                        context.logDebug("outgoing reference: " + element.getUid());
                    } else {
                        context.logDebug("no outgoing references for entity ");
                    }

                    if (element instanceof Media) {
                        ServerActionHandle handle = AccessUtil.release(element, checkOnly, releaseParentPath, recursive, dependentType);
                        handle.getResult();
                        handle.checkAndThrow();
                        Set<Long> notReleased = new HashSet<Long>();
                        ReleaseProgress progress = (ReleaseProgress) handle.getProgress(true);
                        notReleased.addAll(progress.getMissingPermissionElements());
                        notReleased.addAll(progress.getLockFailedElements());
                        if (!notReleased.isEmpty()) {
                            context.logError("Couldn't release the following elements: " + notReleased);
                        }
                    }
                }
                entity.getSession().release(entity);
                entity.getSession().commit();



                // get pageref for deployment
                Store siteStore = context.getUserService().getStore(Store.Type.SITESTORE, false);
                PageRef pageRef = (PageRef) siteStore.getStoreElement(DETAIL_PAGE, PageRef.UID_TYPE);
                long detailPageId = 0;
                if (pageRef != null) {
                    detailPageId = pageRef.getId();
                } else {
                    context.logError("Specified UID isn't of type PageRef");
                    return false;
                }

                // get schedule entry
                Connection connection = context.getConnection();
                AdminService adminService = connection.getService(AdminService.class);
                ScheduleStorage scheduleStorage = adminService.getScheduleStorage();
                List<ScheduleEntry> allEntriesProject = scheduleStorage.getScheduleEntries(context.getProject());
                ScheduleEntry scheduleEntry = null;
                for (ScheduleEntry entry : allEntriesProject) {
                    if (entry.getName().equals(SCHEDULER_NAME)) {
                        scheduleEntry = entry;
                        context.logInfo("Schedule Entry found!");
                        break;
                    }
                }

                // deployment
                if (scheduleEntry != null) {

                    // patch generation task to generate only one detail page containing the selected row
                    boolean taskFound = false;
                    for(ScheduleTask scheduleTask : scheduleEntry.getTasks()) {
                        if(scheduleTask.getName().equals(SCHEDULER_TASK)) {
                            if(scheduleTask instanceof GenerateTask) {
                                if(detailPageId != 0) {
                                    scheduleEntry.lock();
                                    GenerateTask generateTask = (GenerateTask) scheduleTask;
                                    GenerateTask.EntityEntry entityEntry = generateTask.createEntityEntry(news_id, detailPageId);
                                    generateTask.getEntityStartNodes().add(entityEntry);
                                    scheduleEntry.unlock(); //TODO check if unlock has to be after scheduleEntry.execute()
                                    context.logInfo("Task found!" + news_id + "/" + detailPageId);
                                    taskFound = true;
                                    break;
                                } else {
                                    context.logError("Page with name: " + DETAIL_PAGE + " not found, please check your workflow configuration.");
                                }
                            } else {
                                context.logError("Task with name: " + SCHEDULER_TASK + " is no generation task, please check your schedule tasks.");
                            }
                        }
                    }
                    if(!taskFound) {
                        context.logError("Task with name: " + SCHEDULER_TASK + " not found, please check your schedule tasks.");
                    }

                    // execute deployment
                    ScheduleEntryControl control = scheduleEntry.execute();
                    context.logInfo("Executing Schedule Entry");
                    //control.awaitTermination();
                    context.logInfo("Terminated Schedule Entry");

                } else {
                    context.logError("No Schedule Entry found");
                }

            } else {
                context.logError("Object has to be an implementation of ContentWorkflowable");
            }

        } catch (Exception e) {
        	e.printStackTrace();
            context.logError("Error while executing workflow script", e);
        } finally {
            try {
                context.doTransition(TRANSITION_NAME);
            } catch (IllegalAccessException e) {
                context.logError("transition error: ", e);
            }
        }
        return true;
    }
}