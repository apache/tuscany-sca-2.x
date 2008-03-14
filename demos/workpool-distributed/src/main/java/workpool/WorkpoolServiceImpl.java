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

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;
import org.apache.tuscany.sca.core.context.CallableReferenceImpl;
import org.apache.tuscany.sca.databinding.annotation.DataBinding;
import org.osoa.sca.ComponentContext;
import org.osoa.sca.annotations.Context;
import org.osoa.sca.annotations.Scope;
import org.osoa.sca.annotations.Service;
import org.apache.tuscany.sca.databinding.job.Job;
import org.apache.tuscany.sca.databinding.job.JobDataMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * An implementation of the Workpool service.
 */
@Service(WorkpoolService.class)
@Scope("COMPOSITE")
@DataBinding("org.apache.tuscany.sca.databinding.job.Job")
public class WorkpoolServiceImpl implements WorkpoolService,
        WorkerServiceCallback {

    /* incoming job queue */
    private LinkedBlockingQueue<Job> queue = new LinkedBlockingQueue<Job>(5000);
    private CallableReferenceImpl<Trigger> trigger = null;
    private Trigger forwardResult = null;
    /* counter for job's number fetched from the queue and sent to the Worker */
    private AtomicInteger jobSent = new AtomicInteger(0);
    /* time for initHandleResult */
    private AtomicLong initHandleResult = new AtomicLong(0);
    /* time for endHandleResult */
    private AtomicLong endHandleResult = new AtomicLong(0);
    /*
     * number of job computed, this will be exposed in order to be used to
     * firing rules
     */
    private long jobComputed = 0;
    /* same as above */
    private AtomicLong elapsedTime = new AtomicLong(0);
    /* this is for comuputing averageServiceTime */
    private long times = 1;
    /* this is for computing averageArrivalTime */
    private long timesArrival = 1;
    private ReentrantLock arrivalLock = new ReentrantLock();
    private long arrivalPrevious = -1;
    // private AtomicBoolean processingStopped = new AtomicBoolean(false);
    private boolean processingStopped = false;
    // private LinkedBlockingQueue<Trigger> triggers = new
    // LinkedBlockingQueue<Trigger>();
    @Context
    protected ComponentContext workpoolContext;
    private CallableReferenceImpl<WorkpoolManager> manager;
    private long previousSubmitTime = -1;
    private boolean firstTime = true;
    private boolean first = true;
    private long start = 0;
    private long end = 0;
    private double averageServiceTime = 0;
    private double averageArrivalTime = 0;
    private int workersNo = 0;
    private final Job nullJob = new NullJob();
    /* This is useful for counting the start and end */
    private Logger log = Logger.getLogger(WorkpoolServiceImpl.class.getName());
    private ReentrantLock handleResultLock = new ReentrantLock();
    private ReentrantLock postWorkerReferenceLock = new ReentrantLock();
    private ConcurrentHashMap<String, WorkerService> cacheReference = new ConcurrentHashMap<String, WorkerService>();
    private CallableReferenceImpl<WorkpoolService> myReference;
    private String previuosURI = "";
    private long time = 0;

    private void computeAverageTime() {
        long actualServiceTime = 0;
        // if the processing is finished
        if (processingStopped)
            return;

        if (firstTime == true) {
            this.previousSubmitTime = System.currentTimeMillis();
            this.averageServiceTime = 0;
            firstTime = false;
        } else {
            actualServiceTime = System.currentTimeMillis()
                    - this.previousSubmitTime;
            this.previousSubmitTime = System.currentTimeMillis();
            averageServiceTime = ((averageServiceTime * times) + actualServiceTime)
                    / (times + 1);
            ++times;
        }
    }

    public void submit(Job j) {
        try {
            // log.info("Submit job in queue -->"+ j.getType());
            // processingStopped.set(false);
            try {
                arrivalLock.lock();
                if (this.arrivalPrevious == -1) {
                    arrivalPrevious = System.currentTimeMillis();
                    averageArrivalTime = 0;
                }
                double actualArrivalTime = System.currentTimeMillis()
                        - arrivalPrevious;
                averageArrivalTime = ((averageArrivalTime * timesArrival) + actualArrivalTime)
                        / (timesArrival + 1);
                arrivalPrevious = System.currentTimeMillis();
                ++timesArrival;
            } finally {
                arrivalLock.unlock();
            }
            queue.put(j);
        } catch (Exception e) {
            log.info("Exception in queue");
            queue.clear();
            e.printStackTrace();
        }
    }

    public double getArrivalTime() {
        return this.averageArrivalTime;
    }

    public double getServiceTime() {
        return this.averageServiceTime;
    }

    public void receiveResult(Job resultType, boolean reuse, String workerURI) {

        if (reuse) {
            queue.add(resultType);
            return;
        }

        computeAverageTime();
        Job job = null;
        try {
            job = queue.take();
        } catch (InterruptedException e) {
            // TODO Better exception handling --> see Exception antipattern doc
            e.printStackTrace();
            return;
        }

        if ((job != null) && (job.eos() == false)) {
            int nameIndex = workerURI.indexOf("/");
            String workerName = workerURI.substring(0, nameIndex - 1);
            log.info("Sending job to worker --> " + workerName);
            WorkerService worker = workpoolContext.getService(
                    WorkerService.class, workerName);
            worker.compute(job);
        }

        JobDataMap map = ((ResultJob) resultType).getDataMap();
        if (map != null) {
            ++jobComputed;
            Object obj = map.getJobDataObject("result");
            System.out.println("Result = " + ((Double) obj).doubleValue());
        }

    }

    public void start() {
        log.info("WorkpoolServiceComponent started...");
        myReference = (CallableReferenceImpl) workpoolContext
                .createSelfReference(WorkpoolService.class, "WorkpoolService");
        myReference.getService();
    }

    /*
     * 
     * This method is called by WorkpoolManagerImpl, when it creates a new
     * worker component in order to dispatch worker to the WorkpoolServiceImpl
     * @param CallableReferenceImpl reference - a dynamically created reference
     * from the Worker
     */
    public void PostWorkerReference(
            CallableReferenceImpl<WorkerService> reference) {

        try {
            long initPostWorkerReference;
            long endPostWorkerReference;
            this.postWorkerReferenceLock.lock();

            initPostWorkerReference = System.currentTimeMillis();
            WorkerService worker;
            worker = reference.getService();
            worker.start();

            ++workersNo;
            if (myReference != null) {

                // Job poison = new ResultJob();
                this.postWorkerReferenceLock.unlock();
                log.info("Sending null job to worker");
                worker.computeFirstTime(nullJob, myReference);
                // queue.put(poison);
                endPostWorkerReference = System.currentTimeMillis();
                System.out.println("Time PostWorker ="
                        + (endPostWorkerReference - initPostWorkerReference));
            } else {
                log.info("myReference is null");

            }
        } catch (Exception e) {
            postWorkerReferenceLock.unlock();
        } finally {
        }

    }

    /*
     * FIXME This method currently is not used because i've not yet ready
     * dynamic wire injection
     */

    public void PostWorkerName(String referenceName) {
        /* TODO Do something similar to PostWorkerReference */
    }

    private void printComputingTime(Job j) {

        if (first == true) {
            first = false;
            start = System.currentTimeMillis();
            end = System.currentTimeMillis();
        } else {
            end = System.currentTimeMillis();
            System.out.println("Elapsed Time = " + (end - start));
            elapsedTime.set(end - start);
        }
        /*
         * i could use reflection or instance of (but it's a penalty kick) , or
         * an object as result, but i'd prefer a job so i've defined a
         * RESULT_JOB There're in the system three kind of jobs: RESULT_JOB,
         * NULL_JOB, DEFAULT_JOB
         */
        if ((j != null) && (j.getType() == Job.RESULT_JOB)) {
            jobComputed++;
            ResultJob result = (ResultJob) j;
            JobDataMap map = result.getDataMap();
            if (map != null) {
                Double doubleValue = (Double) map.getJobDataObject("result");
                System.out
                        .println("ResultValue = " + doubleValue.doubleValue());
            }

        }

    }

    public void handleResult(Job resultType, boolean reuse, String workerURI,
            CallableReferenceImpl<WorkerService> worker, boolean newWorker) {
        initHandleResult.set(System.nanoTime());
        if (reuse) {
            log.info("Reusing a job..");
            queue.add(resultType);
            return;
        }
        // init job variable
        Job job;
        if (newWorker)
            System.out.println("newWorkerActivation= " + System.nanoTime());
        printComputingTime(resultType);

        try {
            job = queue.take();
        } catch (Exception e) {
            log.info("Exception during fetching the queue");
            e.printStackTrace();
            return;
        }

        try {
            // it needs to be locked because multiple threads could invoke this.
            handleResultLock.lock();
            if (previuosURI.equals("")) {
                time = System.currentTimeMillis();
                this.previuosURI = workerURI;
            } else {
                if (previuosURI.equals(workerURI))
                    System.out.println("Complete ComputeTime for an item ="
                            + (time - System.currentTimeMillis()));
            }
            if (job.eos()) {
                long endTime = System.currentTimeMillis();
                /* checking for EOS */
                if (processingStopped == false) {
                    processingStopped = true;
                    System.out.println("GOT EOS in time=" + (endTime - start));
                    log.info("Stop autonomic cycle..");
                    /*
                     * I'm doing this because i want that in the termination i
                     * would have more jobs with eos == true than workers. So
                     * i'm sure that every worker removes itself from its
                     * manager. I do it only one time. This is necessary because
                     * i have a variable number of workers. The number of
                     * workers in the system might change every time the rule
                     * engine cycle gets executed.
                     */
                    ResultJob poison = new ResultJob();
                    for (int i = 0; i < workersNo; ++i) {
                        try {

                            queue.put(poison);

                        } catch (Exception e) {
                            log.info("Cannot duplicate poison tokens");
                            break;
                        }

                    }
                    manager.getService().stopAutonomicCycle();
                }
            }
            computeAverageTime();
            System.out.println("AverageTime =" + averageServiceTime);
            if (job != null) {

                WorkerService workerService;
                /*
                 * the workpool has a high reuse, i always call the same
                 * component set or un superset or subset, so i cache it. When
                 * the WorkpoolManager will remove an item, it removes still
                 * this cache entry
                 */
                if (!cacheReference.containsKey(workerURI)) {
                    workerService = worker.getService();
                    handleResultLock.unlock();
                    cacheReference.put(workerURI, workerService);
                } else {
                    handleResultLock.unlock();
                    workerService = cacheReference.get(workerURI);
                }
                // it's still a penalty kick locking compute because it's going
                // to be scheduled whereas it's async.
                workerService.compute(job);
                log.info("Sent job #" + jobSent.incrementAndGet()
                        + " Queue size " + queue.size());
                endHandleResult.set(System.nanoTime());
                System.out
                        .println("begin:handleResult ==> end:handleResult:compute = "
                                + (endHandleResult.addAndGet(-(initHandleResult
                                        .get())) / 1000000));
            }
        } catch (Exception e) {
            handleResultLock.unlock();
        }
    }

    public void evictAll() {
        cacheReference.clear();
    }

    public void evict(String workerURI) {
        if (cacheReference.containsKey(workerURI)) {
            cacheReference.remove(workerURI);
        }

    }

    public int estimatedQueueSize() {
        return queue.size();
    }

    public long getElapsedTime() {
        return elapsedTime.get();
    }

    public long getJobComputed() {
        return jobComputed;
    }

    public void registerManager(
            CallableReferenceImpl<WorkpoolManager> createSelfReference) {
        manager = createSelfReference;

    }

    public void stop() {
        // TODO Auto-generated method stub

    }

    public void addTrigger(CallableReferenceImpl<Trigger> reference) {
        this.trigger = reference;
        this.forwardResult = reference.getService();

    }

    public void removeTrigger() {
        this.trigger = null;
        this.forwardResult = null;
    }
}
