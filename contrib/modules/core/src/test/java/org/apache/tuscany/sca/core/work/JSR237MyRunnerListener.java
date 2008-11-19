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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.tuscany.sca.work.NotificationListener;

/**
 * Simple NotificationListener that is used for testing Jsr237WorkScheduler
 *
 * @version $Rev$ $Date$
 */
public class JSR237MyRunnerListener implements NotificationListener<JSR237MyRunnable> {

    /**
     * Count of workAccepted() method calls
     */
    private AtomicInteger workAcceptedCallCount = new AtomicInteger();

    /**
     * Count of workStarted() method calls
     */
    private AtomicInteger workStartedCallCount = new AtomicInteger();

    /**
     * Count of workCompleted() method calls
     */
    private AtomicInteger workCompletedCallCount = new AtomicInteger();

    /**
     * Count of workFailed() method calls
     */
    private AtomicInteger workFailedCallCount = new AtomicInteger();

    /**
     * Count of workRejected() method calls
     */
    private AtomicInteger workRejectedCallCount = new AtomicInteger();

    /**
     * List of all exceptions thrown by Work items
     */
    private List<Throwable> workExceptions = Collections.synchronizedList(new ArrayList<Throwable>());

    /**
     * {@inheritDoc}
     */
    public void workAccepted(JSR237MyRunnable work) {
        workAcceptedCallCount.incrementAndGet();
    }

    /**
     * {@inheritDoc}
     */
    public void workCompleted(JSR237MyRunnable work) {
        workCompletedCallCount.incrementAndGet();
    }

    /**
     * {@inheritDoc}
     */
    public void workFailed(JSR237MyRunnable work, Throwable error) {
        workExceptions.add(error);
        workFailedCallCount.incrementAndGet();
    }

    /**
     * {@inheritDoc}
     */
    public void workRejected(JSR237MyRunnable work) {
        workRejectedCallCount.incrementAndGet();
    }

    /**
     * {@inheritDoc}
     */
    public void workStarted(JSR237MyRunnable work) {
        workStartedCallCount.incrementAndGet();
    }

    /**
     * Returns the number of calls to workAccepted()
     *
     * @return The number of calls to workAccepted()
     */
    public int getWorkAcceptedCallCount() {
        return workAcceptedCallCount.get();
    }

    /**
     * Returns the number of calls to workStarted()
     *
     * @return The number of calls to workStarted()
     */
    public int getWorkStartedCallCount() {
        return workStartedCallCount.get();
    }

    /**
     * Returns the number of calls to workCompleted()
     *
     * @return The number of calls to workCompleted()
     */
    public int getWorkCompletedCallCount() {
        return workCompletedCallCount.get();
    }

    /**
     * Returns the number of calls to workFailed()
     *
     * @return The number of calls to workFailed()
     */
    public int getWorkFailedCallCount() {
        return workFailedCallCount.get();
    }

    /**
     * Returns the number of calls to workRejected()
     *
     * @return The number of calls to workRejected()
     */
    public int getWorkRejectedCallCount() {
        return workRejectedCallCount.get();
    }

    /**
     * Returns a List of all exceptions that are thrown by the Work items
     *
     * @return A List of all exceptions that are thrown by the Work items
     */
    public List<Throwable> getWorkExceptions() {
        return Collections.unmodifiableList(workExceptions);
    }
}
