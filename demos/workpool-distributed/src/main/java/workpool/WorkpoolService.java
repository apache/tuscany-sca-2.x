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
package workpool;

import org.apache.tuscany.sca.core.context.CallableReferenceImpl;
import org.apache.tuscany.sca.databinding.annotation.DataBinding;
import org.apache.tuscany.sca.databinding.job.Job;
import org.osoa.sca.annotations.OneWay;
import org.osoa.sca.annotations.Remotable;
import org.osoa.sca.ServiceReference;

@DataBinding("org.apache.tuscany.sca.databinding.job.Job")
@Remotable
public interface WorkpoolService {

    /* this the functional part */
    void submit(Job i);

    /* the time between two subsequent worker invocations */
    double getServiceTime();

    /* the number of ResultJob received */
    long getJobComputed();

    /* the time elapsed between the stream has initiated and now */
    long getElapsedTime();

    /* the size of the internal queue : it's not accurate */
    int estimatedQueueSize();

    /* the average time between two consuecutive submit */
    double getArrivalTime();

    void start();

    void stop();

    /*
     * this is the part needed by management. May be in future i'll refactor it
     * order to hide this part.
     */
    @OneWay
    void handleResult(Job j, boolean reuse, String string,
            CallableReferenceImpl<WorkerService> worker, boolean newJob);

    void addTrigger(CallableReferenceImpl<Trigger> reference);

    void removeTrigger();

    void registerManager(
            CallableReferenceImpl<WorkpoolManager> createSelfReference);

    /*
     * This could placed in another interface definition - think about it These
     * methods evict, and evictAll are needed when a worker finish to exist and
     * it needs to be evicted by the WorkpoolManager. In the system I have two
     * caches: 1) a domain cache, which holds the components URI 2) a
     * workerReference cache (implemented by a ConcurrentHashMap), which holds a
     * proxy to each worker. Every proxy gets built from the worker callable
     * reference. I'm thinking for placing the workerReferenceCache in a local
     * interface. Assuming that WorkpoolService and WorkpoolManager are in the
     * same JVM.
     */
    void evict(String workerURI);

    void evictAll();

    /*
     * these two are no longer needed. I leave it because if i'll have time to
     * do dynamic wiring the first one is needed. void PostWorkerName(String
     * referenceName);
     */
    void PostWorkerReference(CallableReferenceImpl<WorkerService> worker);

}
