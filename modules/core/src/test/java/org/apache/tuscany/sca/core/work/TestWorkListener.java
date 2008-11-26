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

import org.apache.tuscany.sca.work.WorkSchedulerException;
import org.junit.Assert;

/**
 * A simple WorkListener that tracks invocations to it.
 * 
 * @version $Rev$ $Date$
 */
public class TestWorkListener implements WorkListener {

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
     * Count of workRejected() method calls
     */
    private AtomicInteger workRejectedCallCount = new AtomicInteger();

    /**
     * List of all exceptions thrown by Work items
     */
    private List<WorkSchedulerException> workExceptions = Collections.synchronizedList(new ArrayList<WorkSchedulerException>());

    /**
     * {@inheritDoc}
     */
    public void workAccepted(WorkEvent work) {
        workAcceptedCallCount.incrementAndGet();

        // Validate the WorkEvent
        Assert.assertNotNull(work.getWorkItem());
        Assert.assertEquals(WorkEvent.WORK_ACCEPTED, work.getType());
    }

    /**
     * {@inheritDoc}
     */
    public void workStarted(WorkEvent work) {
        workStartedCallCount.incrementAndGet();

        // Validate the WorkEvent
        Assert.assertNotNull(work.getWorkItem());
        Assert.assertEquals(WorkEvent.WORK_STARTED, work.getType());
    }

    /**
     * {@inheritDoc}
     */
    public void workCompleted(WorkEvent work) {
        if (work.getException() != null) {
            workExceptions.add(work.getException());
        }

        // Validate the WorkEvent
        Assert.assertNotNull(work.getWorkItem());
        Assert.assertEquals(WorkEvent.WORK_COMPLETED, work.getType());

        workCompletedCallCount.incrementAndGet();
    }

    /**
     * {@inheritDoc}
     */
    public void workRejected(WorkEvent work) {
        workRejectedCallCount.incrementAndGet();

        // Validate the WorkEvent
        Assert.assertNotNull(work.getWorkItem());
        Assert.assertEquals(WorkEvent.WORK_REJECTED, work.getType());
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
    public List<WorkSchedulerException> getWorkExceptions() {
        return Collections.unmodifiableList(workExceptions);
    }
}
