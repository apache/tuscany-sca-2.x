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
 * Test case for Jsr237WorkScheduler
 *
 * @version $Rev$ $Date$
 */
public class Jsr237WorkSchedulerTestCase {

    /**
     * Wait up to 20 seconds for the Work units to complete
     */
    private static final long WAIT_TIMEOUT = 20000;

    /**
     * This is the shared instance of the ThreadPoolWorkManager used by the tests
     */
    private static DefaultWorkScheduler workSchedular = null;

    /**
     * Setup the Jsr237WorkScheduler
     */
    @BeforeClass
    public static void setup() {
        workSchedular = new DefaultWorkScheduler();
    }

    /**
     * Make sure that the Jsr237WorkScheduler is stopped after running the tests
     */
    @AfterClass
    public static void destroy() {
        if (workSchedular != null) {
            workSchedular.destroy();
        }
    }

    /**
     * Tests running a single fast job on the Jsr237WorkScheduler
     */
    @Test
    public void testSingleFastJob() {
        // Create the work and register it
        JSR237MyRunnable fast = new JSR237MyRunnable(10);
        JSR237MyRunnerListener listener = new JSR237MyRunnerListener();
        workSchedular.scheduleWork(fast, listener);

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
     * Tests running a single job that fails on the Jsr237WorkScheduler
     */
    @Test
    public void testSingleFailingJob() {
        // Create the work and register it
        JSR237MyFailingRunnable fail = new JSR237MyFailingRunnable();
        JSR237MyRunnerListener listener = new JSR237MyRunnerListener();
        workSchedular.scheduleWork(fail, listener);

        // Wait for the 1 job to complete
        waitForWorkToComplete(listener, 1);

        // Test that the job completed successfully.
        Assert.assertEquals(1, listener.getWorkAcceptedCallCount());
        Assert.assertEquals(0, listener.getWorkRejectedCallCount());
        Assert.assertEquals(1, listener.getWorkStartedCallCount());
        Assert.assertEquals(0, listener.getWorkCompletedCallCount());
        Assert.assertEquals(1, listener.getWorkFailedCallCount());
        Assert.assertEquals(1, listener.getWorkExceptions().size());
    }

    /**
     * Tests running a mixture of fast and slow jobs on the Jsr237WorkScheduler
     */
    @Test
    public void testMultipleJobs() {
        // Create the work and register it
        JSR237MyRunnable fast1 = new JSR237MyRunnable(50);
        JSR237MyRunnable fast2 = new JSR237MyRunnable(100);
        JSR237MyRunnable fast3 = new JSR237MyRunnable(200);
        JSR237MyRunnable slow1= new JSR237MyRunnable(2000);
        JSR237MyRunnable slow2 = new JSR237MyRunnable(2000);
        JSR237MyRunnerListener listener = new JSR237MyRunnerListener();
        workSchedular.scheduleWork(fast1, listener);
        workSchedular.scheduleWork(fast2, listener);
        workSchedular.scheduleWork(fast3, listener);
        workSchedular.scheduleWork(slow1, listener);
        workSchedular.scheduleWork(slow2, listener);

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
     * Jsr237WorkScheduler
     */
    @Test
    public void testMultipleJobsSomeFail() {
        // Create the work and register it
        JSR237MyRunnable fast1 = new JSR237MyRunnable(50);
        JSR237MyRunnable fast2 = new JSR237MyRunnable(100);
        JSR237MyRunnable fast3 = new JSR237MyRunnable(200);
        JSR237MyRunnable slow1= new JSR237MyRunnable(2000);
        JSR237MyRunnable slow2 = new JSR237MyRunnable(2000);
        JSR237MyFailingRunnable fail1 = new JSR237MyFailingRunnable();
        JSR237MyFailingRunnable fail2 = new JSR237MyFailingRunnable();
        JSR237MyRunnerListener listener = new JSR237MyRunnerListener();
        workSchedular.scheduleWork(fast1, listener);
        workSchedular.scheduleWork(fast2, listener);
        workSchedular.scheduleWork(fail1, listener);
        workSchedular.scheduleWork(fast3, listener);
        workSchedular.scheduleWork(slow1, listener);
        workSchedular.scheduleWork(fail2, listener);
        workSchedular.scheduleWork(slow2, listener);

        // Wait for the 7 jobs to complete
        waitForWorkToComplete(listener, 7);

        // Test that the job completed successfully.
        Assert.assertEquals(7, listener.getWorkAcceptedCallCount());
        Assert.assertEquals(0, listener.getWorkRejectedCallCount());
        Assert.assertEquals(7, listener.getWorkStartedCallCount());
        Assert.assertEquals(5, listener.getWorkCompletedCallCount());
        Assert.assertEquals(2, listener.getWorkFailedCallCount());
        Assert.assertEquals(2, listener.getWorkExceptions().size());
    }

    /**
     * Tests running a single job that has no listener
     */
    @Test
    public void testSingleFastJobWithNoListener() {
        // Create the work and register it
        JSR237MyRunnable fast = new JSR237MyRunnable(10);
        workSchedular.scheduleWork(fast);

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
    }

    /**
     * Tests scheduling a null as the work item
     */
    @Test
    public void testNullWork() {
        try {
            workSchedular.scheduleWork(null);
            Assert.fail("Should have thrown IllegalArgumentException ");
        } catch (IllegalArgumentException ex) {
            // As expected
            Assert.assertTrue(ex.toString().indexOf("null") != -1);
        }
    }

    /**
     * Waits for the specified number of jobs to complete or the timeout to fire.
     *
     * @param listener The listener to use to track Work unit completion
     * @param completedWorkItemsToWaitFor The number of Work items to complete
     */
    private void waitForWorkToComplete(JSR237MyRunnerListener listener, int completedWorkItemsToWaitFor) {
        long startTime = System.currentTimeMillis();
        while (true) {
            int completedCount = listener.getWorkCompletedCallCount() + listener.getWorkFailedCallCount();
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
