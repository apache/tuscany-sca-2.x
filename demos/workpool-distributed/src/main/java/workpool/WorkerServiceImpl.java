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

import org.osoa.sca.ComponentContext;
import org.osoa.sca.RequestContext;
import org.osoa.sca.ServiceReference;
import org.osoa.sca.annotations.Callback;
import org.osoa.sca.annotations.Context;
import org.osoa.sca.annotations.Property;
import org.osoa.sca.annotations.Scope;
import org.osoa.sca.annotations.Service;
import org.apache.tuscany.sca.core.context.CallableReferenceImpl;
import org.apache.tuscany.sca.databinding.annotation.DataBinding;
import org.apache.tuscany.sca.databinding.job.Job;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.*;

/**
 * An implementation of the worker service.
 */
@Service(WorkerService.class)
@DataBinding("org.apache.tuscany.sca.databinding.job.Job")
@Scope("COMPOSITE")
public abstract class WorkerServiceImpl<T, E> implements WorkerService<T, E> {
    private Logger log = Logger.getLogger(this.getClass().getName());
    private WorkerServiceCallback workerServiceCallback;
    @Context
    protected ComponentContext workerContext;
    @Context
    protected RequestContext requestContext;
    @Property
    protected String workerName;
    private CallableReferenceImpl<WorkerManager> managerReference = null;

    /* TODO add the triggers, but before ask */
    // protected Map<String,Trigger> triggers = new HashMap<String,Trigger>();
    public abstract ResultJob computeTask(Job<T, E> job);

    private boolean stopped = false;
    private CallableReferenceImpl<WorkerService> serviceRef;
    private CallableReferenceImpl<WorkpoolService> senderService;
    private WorkpoolService wp = null;
    private WorkerManager manager = null;

    public void start() {
        log.info("Starting worker...");
        stopped = false;
        serviceRef = (CallableReferenceImpl) workerContext
                .createSelfReference(WorkerService.class);

    }

    public void init(CallableReferenceImpl<WorkpoolService> sender, Job nullJob) {
        compute(nullJob);
    }

    public void stop() {
        stopped = true;
    }

    @Callback
    public void setWorkerServiceCallback(
            WorkerServiceCallback workerServiceCallback) {
        log.info("Setting worker callback");
        this.workerServiceCallback = workerServiceCallback;
    }

    public void computeFirstTime(Job nullJob,
            CallableReferenceImpl<WorkpoolService> sender) {
        senderService = sender;
        wp = sender.getService();
        workWithCallable(nullJob);
    }

    public void registerManager(CallableReferenceImpl<WorkerManager> wm) {
        managerReference = wm;
        manager = managerReference.getService();

    }

    public void registerSender(CallableReferenceImpl<WorkpoolService> sender) {
        log.info("Registering sender..");
        senderService = sender;
        wp = sender.getService();
    }

    private void workWithInjection(Job j) {
        log.info("Worker has received job");
        if (stopped) {
            workerServiceCallback
                    .receiveResult(j, true, workerContext.getURI());
            if (managerReference != null)
                manager.removeWorker(workerContext.getURI());
        } else if (j.eos()) {
            if (managerReference != null)
                manager.removeWorker(workerContext.getURI());
        }
        if (j instanceof NullJob) {
            workerServiceCallback.receiveResult(j, false, workerContext
                    .getURI());
        } else {
            workerServiceCallback.receiveResult(computeTask(j), false,
                    workerContext.getURI());
        }
    }

    private void workWithCallable(Job j) {
        log.info("Worker " + workerContext.getURI()
                + " has received job with eos --> " + j.eos());
        if (stopped) {
            wp.handleResult(j, true, workerContext.getURI(), serviceRef, false);
            return;
        }
        if (j.eos()) {
            log.info("Got poison token...");
            if (managerReference != null) {
                log.info("Removing component " + workerContext.getURI());
                manager.removeWorker(workerContext.getURI());

            }
            return;
        }
        if (j.getType() != Job.NULL_JOB) {
            wp.handleResult(computeTask(j), false, workerContext.getURI(),
                    serviceRef, false);
        } else {
            log.info("Got a null job");
            wp.handleResult(j, false, workerContext.getURI(), serviceRef, true);
        }
    }

    public void compute(Job<T, E> j) {

        if (senderService != null) {
            log.info("Computing job using callable reference method");
            workWithCallable(j);

        } else {
            log.info("Computing job using reference injection method");
            workWithInjection(j);

        }
    }
    /*
     * public void addJobCompleteHandler(String triggerName,
     * CallableReferenceImpl<Trigger> handle) { if
     * (!triggers.containsKey(triggerName)) { triggers.put(triggerName,
     * handle.getService()); } } public void removeJobCompleteHandler(String
     * triggerName) { if (!triggers.containsKey(triggerName)) {
     * triggers.remove(triggerName); } }
     */
}
