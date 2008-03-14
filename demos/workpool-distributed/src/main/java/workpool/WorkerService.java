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

import org.osoa.sca.ServiceReference;
import org.osoa.sca.annotations.Callback;
import org.osoa.sca.annotations.Remotable;
import org.osoa.sca.annotations.OneWay;
import org.apache.tuscany.sca.core.context.CallableReferenceImpl;
import org.apache.tuscany.sca.databinding.annotation.DataBinding;
import org.apache.tuscany.sca.databinding.job.Job;

/**
 * The interface for the multiply service
 */
@Remotable
@Callback(WorkerServiceCallback.class)
@DataBinding("org.apache.tuscany.sca.databinding.job.Job")
public interface WorkerService<T, E> {
    @OneWay
    void compute(Job<T, E> j);

    void start();

    void stop();

    // void addJobCompleteHandler(String triggerName,
    // CallableReferenceImpl<Trigger> handle);
    // void removeJobCompleteHandler(String triggerName);
    /* The worker manager */
    void registerManager(CallableReferenceImpl<WorkerManager> wm);

    void registerSender(CallableReferenceImpl<WorkpoolService> sender);

    // void init(Job nullJob);
    @OneWay
    void computeFirstTime(Job nullJob,
            CallableReferenceImpl<WorkpoolService> myReference);

}
