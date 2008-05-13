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

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Simple Runnable that is used for testing Jsr237WorkScheduler
 *
 * @version $Rev$ $Date$
 */
public class JSR237MyRunnable implements Runnable {

    /**
     * Count of workAccepted() method calls
     */
    private AtomicInteger runCompletedCount = new AtomicInteger();

    /**
     * The amount of time to sleep in the Run loop
     */
    private final long sleepTime;

    /**
     * Constructor
     *
     * @param sleepTime The amount of time to sleep (in milliseconds) in the run() method
     */
    public JSR237MyRunnable(long sleepTime) {
        this.sleepTime = sleepTime;
    }

    /**
     * Sleeps for a period of time defined by sleepTime
     */
    public void run() {
        System.out.println("Starting " + this);
        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Done " + this);
        runCompletedCount.incrementAndGet();
    }

    /**
     * Returns the number of completed calls to run()
     *
     * @return The number of completed calls to run()
     */
    public int getRunCompletedCount() {
        return runCompletedCount.get();
    }
}
