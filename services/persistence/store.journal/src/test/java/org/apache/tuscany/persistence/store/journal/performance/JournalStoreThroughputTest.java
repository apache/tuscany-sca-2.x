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

import org.apache.tuscany.spi.component.SCAObject;
import org.apache.tuscany.spi.services.store.StoreWriteException;

import org.apache.tuscany.persistence.store.journal.JournalShutdownException;
import org.apache.tuscany.persistence.store.journal.JournalStore;
import static org.apache.tuscany.persistence.store.journal.SerializationHelper.serialize;
import static org.apache.tuscany.persistence.store.journal.SerializationHelper.serializeRecordId;
import org.apache.tuscany.persistence.store.journal.TestUtils;

/**
 * Runs a basic throughput tests on JournalStore operations
 * <p/>
 * TODO this should be integrated with a Maven itest-based performance harness
 *
 * @version $Rev$ $Date$
 */
public class JournalStoreThroughputTest {
    private static final int SIZE = 1000;
    private CyclicBarrier barrier;
    private JournalStore store;
    private long now;
    private SCAObject owner = new MockSCAObject();
    private String id = UUID.randomUUID().toString();
    private CountDownLatch latch = new CountDownLatch(1);
    private long expire = System.currentTimeMillis() + 10000;
    private Foo object = new Foo("this is a test", 1);

    public static void main(String[] args) throws Exception {
        JournalStoreThroughputTest test = new JournalStoreThroughputTest();
        test.testAppend();
        test.latch.await(5000, TimeUnit.MILLISECONDS);
    }

    public void testAppend() throws Exception {
        TestUtils.cleanupLog();
        store = new JournalStore(new MockMonitor());
        store.init();
        final Thread[] threads = new Thread[SIZE];
        barrier = new CyclicBarrier(SIZE, new Runnable() {
            public void run() {
                try {
                    System.out.println("-----------------------------------------------------");
                    System.out.println("JournalStore.append()");
                    byte[] idBytes = serializeRecordId(owner.getUri().toString(), id);
                    byte[] bytes = serialize(object);
                    System.out.println("Approx record size :" + (bytes.length + idBytes.length));
                    System.out.println("Total threads :" + barrier.getNumberWaiting());
                    System.out.println("Forced writes :" + barrier.getNumberWaiting());
                    System.out.println("Time:" + (System.currentTimeMillis() - now));
                    store.destroy();
                    latch.countDown();
                } catch (JournalShutdownException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        for (int i = 0; i < SIZE; i++) {
            threads[i] = new Thread(new InsertWorker(true));
        }
        now = System.currentTimeMillis();
        for (int i = 0; i < SIZE; i++) {
            threads[i].start();
        }
    }

    private class InsertWorker implements Runnable {
        boolean forced;

        public InsertWorker(boolean forced) {
            this.forced = forced;
        }

        public void run() {
            try {
                store.insertRecord(owner, id, object, expire);
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
