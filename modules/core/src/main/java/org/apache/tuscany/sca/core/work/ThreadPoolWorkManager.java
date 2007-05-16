/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.    
 */
package org.apache.tuscany.sca.core.work;

import java.rmi.server.UID;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;

import org.osoa.sca.annotations.Destroy;
import org.osoa.sca.annotations.Property;

import commonj.work.Work;
import commonj.work.WorkEvent;
import commonj.work.WorkException;
import commonj.work.WorkItem;
import commonj.work.WorkListener;
import commonj.work.WorkManager;
import commonj.work.WorkRejectedException;

/**
 * A thread-pool based implementation for the JSR-237 work manager.
 * <p/>
 * <p/>
 * This implementation supports only local work.
 * <p/>
 * TODO Elaborate the implementation. </p>
 */
public class ThreadPoolWorkManager implements WorkManager {

    // Map of work items currently handled by the work manager
    private Map<WorkItemImpl, WorkListener> workItems = new ConcurrentHashMap<WorkItemImpl, WorkListener>();

    // Thread-pool
    private ExecutorService executor;

    /**
     * Initializes the thread-pool.
     *
     * @param threadPoolSize Thread-pool size.
     */
    public ThreadPoolWorkManager(@Property(name = "poolSize") int threadPoolSize) {
        executor = Executors.newFixedThreadPool(threadPoolSize);
    }

    /**
     * Schedules a unit of work asynchronously.
     *
     * @param work Work that needs to be scheduled.
     * @return Work Work item representing the asynchronous work
     */
    public WorkItem schedule(Work work) throws WorkException {
        return schedule(work, null);
    }

    /**
     * Schedules a unit of work asynchronously.
     *
     * @param work         Work that needs to be scheduled.
     * @param workListener Work listener for callbacks.
     * @return Work Work item representing the asynchronous work
     */
    public WorkItem schedule(Work work, WorkListener workListener) throws WorkRejectedException {

        WorkItemImpl workItem = new WorkItemImpl(new UID().toString(), work);
        if (workListener != null) {
            workItems.put(workItem, workListener);
        }
        workAccepted(workItem, work);
        if (scheduleWork(work, workItem)) {
            return workItem;
        } else {
            workItem.setStatus(WorkEvent.WORK_REJECTED);
            if (workListener != null) {
                workListener.workRejected(new WorkEventImpl(workItem));
            }
            throw new WorkRejectedException("Unable to schedule work");
        }
    }

    /**
     * Wait for all the specified units of work to finish.
     *
     * @param works   Units of the work that need to finish.
     * @param timeout Timeout for waiting for the units of work to finish.
     */
    public boolean waitForAll(Collection works, long timeout) {
        throw new UnsupportedOperationException("waitForAll not supported");
    }

    /**
     * Wait for any of the specified units of work to finish.
     *
     * @param works   Units of the work that need to finish.
     * @param timeout Timeout for waiting for the units of work to finish.
     */
    public Collection waitForAny(Collection works, long timeout) {
        throw new UnsupportedOperationException("waitForAny not supported");
    }

    /**
     * Method provided for subclasses to indicate a work accptance.
     *
     * @param workItem Work item representing the work that was accepted.
     * @param work     Work that was accepted.
     */
    private void workAccepted(final WorkItemImpl workItem, final Work work) {
        WorkListener listener = workItems.get(workItem);
        if (listener != null) {
            workItem.setStatus(WorkEvent.WORK_ACCEPTED);
            WorkEvent event = new WorkEventImpl(workItem);
            listener.workAccepted(event);
        }
    }

    /*
     * Method to indicate a work start.
     */
    private void workStarted(final WorkItemImpl workItem, final Work work) {
        WorkListener listener = workItems.get(workItem);
        if (listener != null) {
            workItem.setStatus(WorkEvent.WORK_STARTED);
            WorkEvent event = new WorkEventImpl(workItem);
            listener.workStarted(event);
        }
    }

    /*
     * Method to indicate a work completion.
     */
    private void workCompleted(final WorkItemImpl workItem, final Work work) {
        workCompleted(workItem, work, null);
    }

    /*
     * Method to indicate a work completion.
     */
    private void workCompleted(final WorkItemImpl workItem, final Work work, final WorkException exception) {
        WorkListener listener = workItems.get(workItem);
        if (listener != null) {
            workItem.setStatus(WorkEvent.WORK_COMPLETED);
            workItem.setResult(work);
            workItem.setException(exception);
            WorkEvent event = new WorkEventImpl(workItem);
            listener.workCompleted(event);
            workItems.remove(workItem);
        }
    }

    /*
     * Schedules the work using the threadpool.
     */
    private boolean scheduleWork(final Work work, final WorkItemImpl workItem) {
        try {
            executor.execute(new DecoratingWork(workItem, work));
            return true;
        } catch (RejectedExecutionException ex) {
            return false;
        }
    }

    /*
     * Class that decorates the original worker so that it can get callbacks when work is done.
     */
    private final class DecoratingWork implements Runnable {

        // Work item for this work.
        private WorkItemImpl workItem;

        // The original work.
        private Work decoratedWork;

        /*
         * Initializes the work item and underlying work.
         */
        private DecoratingWork(final WorkItemImpl workItem, final Work decoratedWork) {
            this.workItem = workItem;
            this.decoratedWork = decoratedWork;
        }

        /*
         * Overrides the run method.
         */
        public void run() {
            workStarted(workItem, decoratedWork);
            try {
                decoratedWork.run();
                workCompleted(workItem, decoratedWork);
            } catch (Throwable th) {
                workCompleted(workItem, decoratedWork, new WorkException(th.getMessage(), th));
            }
        }

    }

    @Destroy
    public void destroy() {
        executor.shutdown();
    }

}
