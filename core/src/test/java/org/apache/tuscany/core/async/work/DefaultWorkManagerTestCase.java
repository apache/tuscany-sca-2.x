/**
 *
 *  Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.tuscany.core.async.work;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import javax.resource.spi.work.Work;

import junit.framework.TestCase;

import org.apache.tuscany.core.async.work.DefaultWorkManager;

/**
 * Test the PooledWorkManager.
 */
public class DefaultWorkManagerTestCase extends TestCase {
    
    private Set<Thread> done;
    private int count;
    
    public void testScheduleWork() throws Exception {

        DefaultWorkManager workManager = new DefaultWorkManager();
        workManager.setScheduledMaximumPoolSize(3);
        workManager.init();
        
        int max=workManager.getScheduledMaximumPoolSize()*5;
        done=Collections.synchronizedSet(new HashSet<Thread>());
        count=0;
        
        CountDownLatch startSignal = new CountDownLatch(1);
        CountDownLatch doneSignal = new CountDownLatch(max);
        for (int i = 0; i < max; ++i) {
            workManager.scheduleWork(new Worker(startSignal, doneSignal));
        }
        startSignal.countDown();
        doneSignal.await();
        
        assertFalse(done.contains(Thread.currentThread()));
        assert(done.size()==workManager.getScheduledMaximumPoolSize());
        assert(count==max);
        
        done=null;
        count=0;

        workManager.destroy();

    }
    
    private synchronized void done(Thread thread) {
        done.add(thread);
        count++;
    }

    private class Worker implements Work {
        private final CountDownLatch startSignal;
        private final CountDownLatch doneSignal;
        
        Worker(CountDownLatch startSignal, CountDownLatch doneSignal) {
            this.startSignal = startSignal;
            this.doneSignal = doneSignal;
        }

        public void run() {
            try {
                startSignal.await();
                
                DefaultWorkManagerTestCase.this.done(Thread.currentThread());
                //System.out.println(Thread.currentThread());
                
                doneSignal.countDown();
            } catch (InterruptedException ex) {
            }
        }

        public void release() {
        }

    }

}
