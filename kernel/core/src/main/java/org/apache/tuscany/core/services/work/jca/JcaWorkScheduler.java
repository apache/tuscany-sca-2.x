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
package org.apache.tuscany.core.services.work.jca;

import javax.resource.spi.work.Work;
import javax.resource.spi.work.WorkEvent;
import javax.resource.spi.work.WorkException;
import javax.resource.spi.work.WorkListener;
import javax.resource.spi.work.WorkManager;
import javax.resource.spi.work.WorkRejectedException;

import org.apache.tuscany.spi.services.work.NotificationListener;
import org.apache.tuscany.spi.services.work.WorkScheduler;
import org.apache.tuscany.spi.services.work.WorkSchedulerException;

/**
 * A work scheduler implementation based on the JCA SPI work manager.
 * <p/>
 * <p/>
 * This needs a JCA SPI work manager implementation available for scheduling work. Instances can be configured with a
 * work manager implementation that is injected in. It is the responsibility of the runtime environment to make a work
 * manager implementaion available. </p>
 */
public class JcaWorkScheduler implements WorkScheduler {

    /**
     * Underlying JCA work manager
     */
    private WorkManager jcaWorkManager;

    /**
     * Initializes the JCA work manager.
     *
     * @param jcaWorkManager JCA work manager.
     */
    public JcaWorkScheduler(WorkManager jcaWorkManager) {

        if (jcaWorkManager == null) {
            throw new IllegalArgumentException("Work manager cannot be null");
        }
        this.jcaWorkManager = jcaWorkManager;

    }

    /**
     * Schedules a unit of work for future execution. The notification listener is used to register interest in
     * callbacks regarding the status of the work.
     *
     * @param work The unit of work that needs to be asynchronously executed.
     */
    public <T extends Runnable> void scheduleWork(T work) {
        scheduleWork(work, null);
    }

    /**
     * Schedules a unit of work for future execution. The notification listener is used to register interest in
     * callbacks regarding the status of the work.
     *
     * @param work     The unit of work that needs to be asynchronously executed.
     * @param listener Notification listener for callbacks.
     */
    public <T extends Runnable> void scheduleWork(T work, NotificationListener<T> listener) {

        if (work == null) {
            throw new IllegalArgumentException("Work cannot be null");
        }

        JcaWork<T> jcaWork = new JcaWork<T>(work);
        try {
            if (listener == null) {
                jcaWorkManager.scheduleWork(jcaWork);
            } else {
                JcaWorkListener<T> jcaWorkListener = new JcaWorkListener<T>(listener);
                // TODO Clarify the usage of timeout and execution context
                jcaWorkManager.scheduleWork(jcaWork, -1, null, jcaWorkListener);
            }
        } catch (WorkRejectedException ex) {
            if (listener != null) {
                listener.workRejected(work);
            } else {
                throw new WorkSchedulerException(ex);
            }
        } catch (WorkException ex) {
            throw new WorkSchedulerException(ex);
        }

    }

    /*
     * Worklistener for keeping track of work status callbacks.
     *
     */
    private class JcaWorkListener<T extends Runnable> implements WorkListener {

        // Notification listener
        private NotificationListener<T> listener;

        /*
         * Initializes the notification listener.
         */
        public JcaWorkListener(NotificationListener<T> listener) {
            this.listener = listener;
        }

        /*
         * Callback when the work is accepted.
         */
        public void workAccepted(WorkEvent workEvent) {
            T work = getWork(workEvent);
            listener.workAccepted(work);
        }

        /*
         * Callback when the work is rejected.
         */
        public void workRejected(WorkEvent workEvent) {
            T work = getWork(workEvent);
            listener.workRejected(work);
        }

        /*
         * Callback when the work is started.
         */
        public void workStarted(WorkEvent workEvent) {
            T work = getWork(workEvent);
            listener.workStarted(work);
        }

        /*
         * Callback when the work is completed.
         */
        public void workCompleted(WorkEvent workEvent) {
            T work = getWork(workEvent);
            Exception exception = workEvent.getException();
            if (exception != null) {
                listener.workFailed(work, exception);
            } else {
                listener.workCompleted(work);
            }
        }

        /*
        * Gets the underlying work from the work event.
        */
        @SuppressWarnings("unchecked")
        private T getWork(WorkEvent workEvent) {
            JcaWork<T> jcaWork = (JcaWork<T>) workEvent.getWork();
            return jcaWork.getWork();
        }

    }

    /*
     * JCA work wrapper.
     */
    private class JcaWork<T extends Runnable> implements Work {

        // Work that is being executed.
        private T work;

        /*
         * Initializes the work instance.
         */
        public JcaWork(T work) {
            this.work = work;
        }

        /*
         * Releases the work.
         */
        public void release() {
        }

        /*
         * Performs the work.
         */
        public void run() {
            work.run();
        }

        /*
         * Returns the completed work.
         */
        public T getWork() {
            return work;
        }

    }

}
