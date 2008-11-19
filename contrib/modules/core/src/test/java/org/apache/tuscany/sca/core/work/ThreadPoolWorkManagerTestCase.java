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

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * This test case will test the ThreadPoolWorkManager
 * 
 * @version $Rev$ $Date$
 */
public class ThreadPoolWorkManagerTestCase {

    /**
     * Wait up to 20 seconds for the Work units to complete
     */
    private static final long WAIT_TIMEOUT = 20000;

    /**
     * This is the shared instance of the ThreadPoolWorkManager used by the tests
     */
    private static ThreadPoolWorkManager workManager = null;

    /**
     * Setup the ThreadPoolWorkManager
     */
    @BeforeClass
    public static void setup() {
        workManager = new ThreadPoolWorkManager(10);
    }

    /**
     * Make sure that the ThreadPoolWorkManager is stopped after running the tests
     */
    @AfterClass
    public static void destroy() {
        if (workManager != null) {
            workManager.destroy();
        }
    }

    /**
     * Tests running a single fast job on the ThreadPoolWorkManager
     */
    @Test
    public void testSingleFastJob() {
        // Create the work and register it
        TimeDelayWork fast = new TimeDelayWork(10);
        TestWorkListener listener = new TestWorkListener();
        workManager.schedule(fast, listener);

        // Wait for the 1 job to complete
        waitForWorkToComplete(listener, 1);

        // Test that the job completed successfully.
        Assert.assertEquals(1, listener.getWorkAcceptedCallCount());
        Assert.assertEquals(0, listener.getWorkRejectedCallCount());
        Assert.assertEquals(1, listener.getWorkStartedCallCount());
        Assert.assertEquals(1, listener.getWorkCompletedCallCount());
        Assert.assertEquals(0, listener.getWorkExceptions().size());
    }

    /**
     * Tests running a single job that fails on the ThreadPoolWorkManager
     */
    @Test
    public void testSingleFailingJob() {
        // Create the work and register it
        FailingWork fail = new FailingWork();
        TestWorkListener listener = new TestWorkListener();
        workManager.schedule(fail, listener);

        // Wait for the 1 job to complete
        waitForWorkToComplete(listener, 1);

        // Test that the job completed successfully.
        Assert.assertEquals(1, listener.getWorkAcceptedCallCount());
        Assert.assertEquals(0, listener.getWorkRejectedCallCount());
        Assert.assertEquals(1, listener.getWorkStartedCallCount());
        Assert.assertEquals(1, listener.getWorkCompletedCallCount());
        Assert.assertEquals(1, listener.getWorkExceptions().size());
    }

    /**
     * Tests running a mixture of fast and slow jobs on the ThreadPoolWorkManager
     */
    @Test
    public void testMultipleJobs() {
        // Create the work and register it
        TimeDelayWork fast1 = new TimeDelayWork(50);
        TimeDelayWork fast2 = new TimeDelayWork(100);
        TimeDelayWork fast3 = new TimeDelayWork(200);
        TimeDelayWork slow1= new TimeDelayWork(2000);
        TimeDelayWork slow2 = new TimeDelayWork(2000);
        TestWorkListener listener = new TestWorkListener();
        workManager.schedule(fast1, listener);
        workManager.schedule(fast2, listener);
        workManager.schedule(fast3, listener);
        workManager.schedule(slow1, listener);
        workManager.schedule(slow2, listener);

        // Wait for the 5 jobs to complete
        waitForWorkToComplete(listener, 5);

        // Test that the job completed successfully.
        Assert.assertEquals(5, listener.getWorkAcceptedCallCount());
        Assert.assertEquals(0, listener.getWorkRejectedCallCount());
        Assert.assertEquals(5, listener.getWorkStartedCallCount());
        Assert.assertEquals(5, listener.getWorkCompletedCallCount());
        Assert.assertEquals(0, listener.getWorkExceptions().size());
    }

    /**
     * Tests running a mixture of fast and slow jobs some of which fail on the 
     * ThreadPoolWorkManager
     */
    @Test
    public void testMultipleJobsSomeFail() {
        // Create the work and register it
        TimeDelayWork fast1 = new TimeDelayWork(50);
        TimeDelayWork fast2 = new TimeDelayWork(100);
        TimeDelayWork fast3 = new TimeDelayWork(200);
        TimeDelayWork slow1= new TimeDelayWork(2000);
        TimeDelayWork slow2 = new TimeDelayWork(2000);
        FailingWork fail1 = new FailingWork();
        FailingWork fail2 = new FailingWork();
        TestWorkListener listener = new TestWorkListener();
        workManager.schedule(fast1, listener);
        workManager.schedule(fast2, listener);
        workManager.schedule(fail1, listener);
        workManager.schedule(fast3, listener);
        workManager.schedule(slow1, listener);
        workManager.schedule(fail2, listener);
        workManager.schedule(slow2, listener);

        // Wait for the 7 jobs to complete
        waitForWorkToComplete(listener, 7);

        // Test that the job completed successfully.
        Assert.assertEquals(7, listener.getWorkAcceptedCallCount());
        Assert.assertEquals(0, listener.getWorkRejectedCallCount());
        Assert.assertEquals(7, listener.getWorkStartedCallCount());
        Assert.assertEquals(7, listener.getWorkCompletedCallCount());
        Assert.assertEquals(2, listener.getWorkExceptions().size());
    }

    /**
     * Tests creating a ThreadPoolWorkManager with invalid pool sizes of -10 to 0
     * inclusive
     */
    @Test
    public void testThreadPoolWorkManagerLessThan1Size() {
        for (int i = 0; i >= -10; i--) {
            try {
                new ThreadPoolWorkManager(i);
                Assert.fail("Should have thrown IllegalArgumentException");
            } catch (IllegalArgumentException ex) {
                Assert.assertTrue(ex.toString().indexOf(Integer.toString(i)) != -1);
            }
        }
    }

    /**
     * Tests running a single job that has no listener
     */
    @Test
    public void testSingleFastJobWithNoListener() {
        // Create the work and register it
        TimeDelayWork fast = new TimeDelayWork(10);
        workManager.schedule(fast);

        // Wait for the job to complete
        long startTime = System.currentTimeMillis();
        while (true) {
            int completedCount = fast.getRunCompletedCount();
            if (completedCount == 1) {
                break;
            }

            if (System.currentTimeMillis() - startTime > WAIT_TIMEOUT) {
                Assert.fail("Only " + completedCount + " work items completed before timeout");
                return;
            }

            // Lets wait for the job to complete
            try {
                Thread.sleep(25);
            } catch (InterruptedException ex) {
                Assert.fail("Unexpected exception: " + ex);
            }
        }

        // Make sure we have got one completed run
        Assert.assertEquals(1, fast.getRunCompletedCount());
    }

    /**
     * Waits for the specified number of jobs to complete or the timeout to fire.
     * 
     * @param listener The listener to use to track Work unit completion
     * @param completedWorkItemsToWaitFor The number of Work items to complete
     */
    private void waitForWorkToComplete(TestWorkListener listener, int completedWorkItemsToWaitFor) {
        long startTime = System.currentTimeMillis();
        while (true) {
            int completedCount = listener.getWorkCompletedCallCount();
            if (completedCount == completedWorkItemsToWaitFor) {
                return;
            }

            if (System.currentTimeMillis() - startTime > WAIT_TIMEOUT) {
                Assert.fail("Only " + completedCount + " work items completed before timeout");
                return;
            }

            // Lets wait for more jobs to complete
            try {
                Thread.sleep(25);
            } catch (InterruptedException ex) {
                Assert.fail("Unexpected exception: " + ex);
            }
        }
    }
}
