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
package org.apache.tuscany.persistence.store.journal.performance;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;

import org.apache.tuscany.spi.services.store.StoreWriteException;

import org.apache.tuscany.persistence.store.journal.Journal;
import static org.apache.tuscany.persistence.store.journal.SerializationHelper.serializeRecordId;
import org.apache.tuscany.persistence.store.journal.TestUtils;

/**
 * Runs a basic throughput tests on Journal operations
 * <p/>
 * TODO this should be integrated with a Maven itest-based performance harness
 *
 * @version $Rev$ $Date$
 */
public class JournalThroughputTest {
    private static final int SIZE = 1000;
    private CyclicBarrier barrier;
    private Journal journal;
    private byte[] bytes;
    private byte[] recordId;
    private long now;
    private CountDownLatch latch = new CountDownLatch(1);

    public static void main(String[] args) throws Exception {
        JournalThroughputTest test = new JournalThroughputTest();
        test.testForcedWrites();
        test.latch.await(5000, TimeUnit.MILLISECONDS);
        test.testNonForcedWrites();
    }

    public void testForcedWrites() throws Exception {
        TestUtils.cleanupLog();
        journal = new Journal();
        journal.open();
        recordId = serializeRecordId("foo", UUID.randomUUID().toString());
        bytes = "this is a test".getBytes();
        final Thread[] threads = new Thread[SIZE];
        barrier = new CyclicBarrier(SIZE, new Runnable() {
            public void run() {
                System.out.println("-----------------------------------------------------");
                System.out.println("Journal.writeBlock() using forced writes");
                System.out.println("Approx record size :" + (recordId.length + bytes.length));
                System.out.println("Total threads :" + barrier.getNumberWaiting());
                System.out.println("Forced writes :" + barrier.getNumberWaiting());
                System.out.println("Time:" + (System.currentTimeMillis() - now));
                try {
                    journal.close();
                    latch.countDown();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        for (int i = 0; i < SIZE; i++) {
            threads[i] = new Thread(new Worker(true));
        }
        now = System.currentTimeMillis();
        for (int i = 0; i < SIZE; i++) {
            threads[i].start();
        }
    }

    public void testNonForcedWrites() throws Exception {
        TestUtils.cleanupLog();
        journal = new Journal();
        journal.open();
        recordId = serializeRecordId("foo", UUID.randomUUID().toString());
        bytes = "this is a test".getBytes();
        final Thread[] threads = new Thread[SIZE];
        barrier = new CyclicBarrier(SIZE, new Runnable() {
            public void run() {
                System.out.println("-----------------------------------------------------");
                System.out.println("Journal.writeBlock() using non-forced writes");
                System.out.println("Approx record size :" + (recordId.length + bytes.length));
                System.out.println("Total threads :" + barrier.getNumberWaiting());
                System.out.println("Forced writes :" + barrier.getNumberWaiting());
                System.out.println("Time:" + (System.currentTimeMillis() - now));
                System.out.println("-----------------------------------------------------");
                try {
                    journal.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        for (int i = 0; i < SIZE; i++) {
            threads[i] = new Thread(new Worker(false));
        }
        now = System.currentTimeMillis();
        for (int i = 0; i < SIZE; i++) {
            threads[i].start();
        }
    }

    private class Worker implements Runnable {
        boolean forced;

        public Worker(boolean forced) {
            this.forced = forced;
        }

        public void run() {
            try {
                journal.writeBlock(bytes, recordId, forced);
                barrier.await();
            } catch (StoreWriteException e) {
                e.printStackTrace();
            } catch (BrokenBarrierException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
