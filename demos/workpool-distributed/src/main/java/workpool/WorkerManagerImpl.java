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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;
import java.util.logging.Logger;

import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.contribution.DeployedArtifact;
import org.apache.tuscany.sca.contribution.service.impl.ContributionServiceImpl;
import org.osoa.sca.CallableReference;
import org.apache.tuscany.sca.node.management.SCANodeManagerInitService;
import org.apache.tuscany.sca.node.SCANode;
import org.apache.tuscany.sca.node.impl.SCANodeImpl;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.osoa.sca.ComponentContext;
import org.osoa.sca.annotations.Context;
import org.osoa.sca.annotations.Property;
import org.osoa.sca.annotations.Scope;
import org.osoa.sca.annotations.Service;
import java.util.LinkedList;
import java.util.ArrayList;

@Scope("COMPOSITE")
@Service(interfaces = { SCANodeManagerInitService.class, WorkerManager.class })
public class WorkerManagerImpl implements WorkerManager, SCANodeManagerInitService {
    private Logger log = Logger.getLogger(WorkerManagerImpl.class.getName());
    private LinkedList<CallableReference<WorkerService>> activeWorkers = new LinkedList<CallableReference<WorkerService>>();
    private List<String> workerComponentNames = new ArrayList<String>();
    private SCANodeImpl node;
    @Property
    protected String nodeName;
    @Property
    protected String compositeName;
    @Property
    protected String workerClass;
    @Context
    protected ComponentContext context;
    private double loadAverage;

    /* This method is used to find a composite inside all deployed artifacts */
    private Composite findComposite(List<Composite> artifacts) {
        for (Composite fact : artifacts) {
                log.info("Searching in a contribution deployed artifacts -"
                        + compositeName);
                Composite augmented = (Composite) fact;
                // found
                if (augmented.getURI().equals(compositeName)) {
                    log.info("Found composite..." + compositeName);
                    return augmented;
                }
            }
        }
        return null;
    }

    public CallableReference<WorkerService> addWorker() {
        log.info("Adding a new worker call..");
        long addWorkerStartTime = System.nanoTime();
        ContributionServiceImpl cServiceImpl = (ContributionServiceImpl) node.getContributionService();
        Contribution contribution = cServiceImpl.getContribution(nodeName);
        List<Composite> artifacts = contribution.getDeployables();
        CallableReference<WorkerService> workerReference = null;
        CallableReference<WorkerService> ref = null;
        log.info("Creating a MetaComponentWorker..");
        MetaComponentWorker mcw = new MetaComponentWorker();
        boolean found = false;
        mcw.setWorkerClass(workerClass);
        Composite augmented = findComposite(artifacts);
        try {
            if (augmented != null) {
                long startCreation = System.nanoTime();
                node.addComponentToComposite(mcw, contribution.getURI(),
                        augmented.getURI());
                System.out.println("addComponentToComposite time = "
                        + (System.nanoTime() - startCreation));
                RuntimeComponent workerComponent = (RuntimeComponent) node
                        .getComponent(mcw.getName());
                if (workerComponent != null) {
                    ref = (CallableReference<WorkerService>) workerComponent
                            .getComponentContext().createSelfReference(
                                    WorkerService.class);
                    ref.getService().start();
                    activeWorkers.addLast(ref);
                    workerComponentNames.add(mcw.getName());
                    CallableReference<WorkerManager> manager = (CallableReference) context
                            .createSelfReference(WorkerManager.class,
                                    "WorkerManager");
                    ref.getService().registerManager(manager);
                    return ref;
                }
            } else {
                log.info("Workpool composite not found!");
            }
        } catch (Exception e) {
            log.info("Exception activation");
            e.printStackTrace();
        }
        ;
        System.out.println("Component Creation Time ="
                + (System.nanoTime() - addWorkerStartTime));
        return ref;
    }

    public boolean removeAllWorkers() {
        for (CallableReference<WorkerService> callable : activeWorkers) {
            callable.getService().stop();
        }
        return true;
    }

    public boolean removeWorker() {
        CallableReference<WorkerService> callable = activeWorkers
                .removeLast();
        callable.getService().stop();
        return true;
    }

    public boolean removeWorkers(int k) {
        if (k >= activeWorkers.size())
            return false;
        for (int i = 0; i < k; ++i) {
            if (!removeWorker())
                return false;
        }
        return true;
    }

    public void setNode(SCANode node) {
        this.node = (SCANodeImpl) node;

    }

    public double getNodeLoad() {
        /*
         * FIXME [jo] this works only on Linux To be replaced with an JNI
         * extension
         */
        RandomAccessFile statfile;

        this.loadAverage = 1.0;
        // load = 0;
        int NoProcessors = 0;
        String cpuLine = null;
        try {
            NoProcessors = Runtime.getRuntime().availableProcessors();
            if (NoProcessors > 1)
                this.loadAverage = 1 / (1.0 * NoProcessors);
            statfile = new RandomAccessFile("/proc/loadavg", "r");
            try {
                statfile.seek(0);
                cpuLine = statfile.readLine();

            } catch (IOException e) {
                // FIX ME: Better exception handling.
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        double min1;
        if (cpuLine != null) {
            java.util.StringTokenizer st = new java.util.StringTokenizer(
                    cpuLine, " ");
            min1 = Double.parseDouble(st.nextToken());
        } else
            min1 = 0;

        return min1 * this.loadAverage;
    }

    public int activeWorkers() {
        return activeWorkers.size();
    }

    public boolean removeWorker(String workerName) {
        RuntimeComponent workerComponent = (RuntimeComponent) node
                .getComponent(workerName);
        if (workerComponent != null) {
            log.info("Removing component " + workerName);
            node.removeComponentFromComposite(nodeName, "Workpool.composite",
                    workerName);
            return true;
        }
        return false;
    }

    public void start() {
        // do nothing for now.
    }
}
