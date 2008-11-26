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
import java.util.concurrent.ThreadFactory;

import org.apache.tuscany.sca.work.WorkSchedulerException;
import org.osoa.sca.annotations.Destroy;

/**
 * A thread-pool based implementation for the JSR-237 work manager.
 * <p/>
 * <p/>
 * This implementation supports only local work.
 * <p/>
 * TODO Elaborate the implementation. </p>
 *
 * @version $Rev$ $Date$
 */
public class ThreadPoolWorkManager {

    // Map of work items currently handled by the work manager
    private Map<WorkItem, WorkListener> workItems = new ConcurrentHashMap<WorkItem, WorkListener>();

    // Thread-pool
    private ExecutorService executor;

    /**
     * Initializes the thread-pool.
     *
     * @param threadPoolSize Thread-pool size.
     * @throws IllegalArgumentException if threadPoolSize < 1
     */
    public ThreadPoolWorkManager(int threadPoolSize) {
        if (threadPoolSize < 1) {
            throw new IllegalArgumentException("Invalid threadPoolSize of " 
                    + threadPoolSize + ". It must be >= 1");
        }

        // Creates a new Executor, use a custom ThreadFactory that
        // creates daemon threads.
        executor = Executors.newFixedThreadPool(threadPoolSize, new ThreadFactory() {
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r);
                thread.setDaemon(true);
                return thread;
            }
        });
    }

    /**
     * Schedules a unit of work asynchronously.
     *
     * @param work Work that needs to be scheduled.
     * @return Work Work item representing the asynchronous work
     */
    public WorkItem schedule(Work work) throws IllegalArgumentException {
        return schedule(work, null);
    }

    /**
     * Schedules a unit of work asynchronously.
     *
     * @param work         Work that needs to be scheduled.
     * @param workListener Work listener for callbacks.
     * @return Work Work item representing the asynchronous work
     */
    public WorkItem schedule(Work work, WorkListener workListener) throws IllegalArgumentException {

        WorkItem workItem = new WorkItem(new UID().toString(), work);
        if (workListener != null) {
            workItems.put(workItem, workListener);
        }
        workAccepted(workItem, work);
        if (scheduleWork(work, workItem)) {
            return workItem;
        } else {
            workItem.setStatus(WorkEvent.WORK_REJECTED);
            if (workListener != null) {
                workListener.workRejected(new WorkEvent(workItem));
            }
            throw new IllegalArgumentException("Unable to schedule work");
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
     * Method provided for subclasses to indicate a work acceptance.
     *
     * @param workItem Work item representing the work that was accepted.
     * @param work     Work that was accepted.
     */
    private void workAccepted(final WorkItem workItem, final Work work) {
        WorkListener listener = workItems.get(workItem);
        if (listener != null) {
            workItem.setStatus(WorkEvent.WORK_ACCEPTED);
            WorkEvent event = new WorkEvent(workItem);
            listener.workAccepted(event);
        }
    }

    /*
     * Method to indicate a work start.
     */
    private void workStarted(final WorkItem workItem, final Work work) {
        WorkListener listener = workItems.get(workItem);
        if (listener != null) {
            workItem.setStatus(WorkEvent.WORK_STARTED);
            WorkEvent event = new WorkEvent(workItem);
            listener.workStarted(event);
        }
    }

    /*
     * Method to indicate a work completion.
     */
    private void workCompleted(final WorkItem workItem, final Work work) {
        workCompleted(workItem, work, null);
    }

    /*
     * Method to indicate a work completion.
     */
    private void workCompleted(final WorkItem workItem, final Work work, final WorkSchedulerException exception) {
        WorkListener listener = workItems.get(workItem);
        if (listener != null) {
            workItem.setStatus(WorkEvent.WORK_COMPLETED);
            workItem.setResult(work);
            workItem.setException(exception);
            WorkEvent event = new WorkEvent(workItem);
            listener.workCompleted(event);
            workItems.remove(workItem);
        }
    }

    /*
     * Schedules the work using the ThreadPool.
     */
    private boolean scheduleWork(final Work work, final WorkItem workItem) {
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
        private WorkItem workItem;

        // The original work.
        private Work decoratedWork;

        /*
         * Initializes the work item and underlying work.
         */
        private DecoratingWork(final WorkItem workItem, final Work decoratedWork) {
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
                workCompleted(workItem, decoratedWork, new WorkSchedulerException(th.getMessage(), th));
            }
        }

    }

    @Destroy
    public void destroy() {
        executor.shutdown();
    }

}
