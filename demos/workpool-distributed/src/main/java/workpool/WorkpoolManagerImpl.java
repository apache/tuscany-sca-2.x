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
import org.osoa.sca.ServiceReference;
import java.util.Collections;
import java.util.Enumeration;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Collection;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

import javax.xml.stream.XMLStreamException;

import node.TestJob;
import java.io.File;
import java.util.Vector;
import org.apache.axiom.om.OMElement;
import org.apache.tuscany.sca.contribution.service.ContributionResolveException;
import org.apache.tuscany.sca.core.context.CallableReferenceImpl;
import org.apache.tuscany.sca.core.context.ServiceReferenceImpl;
import org.apache.tuscany.sca.databinding.job.Job;
import org.apache.tuscany.sca.node.NodeManagerInitService;
import org.apache.tuscany.sca.node.SCANode;
import org.apache.tuscany.sca.node.impl.SCANodeImpl;
import org.osoa.sca.CallableReference;
import org.drools.FactHandle;
import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.StatefulSession;
import org.drools.StatelessSession;
import org.drools.compiler.DroolsParserException;
import org.drools.compiler.PackageBuilder;
import org.drools.rule.Package;
import org.osoa.sca.annotations.Constructor;
import org.osoa.sca.annotations.Context;
import org.osoa.sca.annotations.Destroy;
import org.osoa.sca.annotations.Property;
import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Scope;
import org.osoa.sca.annotations.Service;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

@Service(interfaces = { NodeManagerInitService.class, WorkpoolManager.class })
@Scope("COMPOSITE")
/*
 * This is the core manager of the workpool application. The Workpool Manager
 * holds the reference to each remote node manager. Inside it we've a rule
 * engine instance.
 */
public class WorkpoolManagerImpl implements WorkpoolManager,
        NodeManagerInitService, WorkpoolBeanListener {
    /*
     * This inner class trigs the rule engine, at given times: 1. It checks the
     * different loads for each nodes and sets the WorkpoolBean 2. It checks the
     * Workpool AverageService Time and sets the WorkpoolBean 3. It checks how
     * many jobs are already computed and sets the WorkpoolBean Then given the
     * configured bean and the rules, run the Rule Engine for executing the
     * business logic
     */
    class RuleEngineTrigger extends TimerTask {
        // private ReentrantLock triggerLock = new ReentrantLock();
        @Override
        public void run() {

            System.out.println("Updating WorkpoolBean..");
            // checkActiveWorkers();
            // checkLoadInNodes();
            checkServiceTime();
            // checkEstimedQueueSize();
            // checkArrivalTime();
            getProcessedItem();
            // computeUsageFactor();
            doRun(bean);
        }

    }

    private WorkerManager managerNodeB;
    private WorkerManager managerNodeC;
    private WorkerManager managerNodeD;
    private WorkerManager managerNodeE;

    private SCANodeImpl node;
    private WorkpoolBean bean = new WorkpoolBean();
    private ReentrantLock handleEventLock = new ReentrantLock();
    private ReentrantLock updateRuleLock = new ReentrantLock();

    private ServiceReference<WorkpoolService> reference;
    private AtomicInteger activeWorkers = new AtomicInteger(0);
    private Logger log = Logger.getLogger(WorkpoolManagerImpl.class.getName());
    @Property
    protected String workers;
    @Property
    protected String nodes;
    @Property
    protected String injection;
    @Context
    protected ComponentContext workpoolManagerContext;
    private CallableReferenceImpl<WorkpoolManager> myReference;
    private String rules = null;
    private boolean referenceInjection = false;
    private ConcurrentHashMap<String, WorkerManager> workerManagerTable = new ConcurrentHashMap<String, WorkerManager>();
    private int workersNo;
    private int nodesNo;
    private Timer timer = new Timer();
    /* this handle facts */
    private RuleBase ruleBase = null;
    private FactHandle handle = null;
    private StatefulSession wm = null;
    private long cycleTime = 5000;

    @Reference
    public void setManagerNodeB(WorkerManager managerNodeB) {
        this.managerNodeB = managerNodeB;
        workerManagerTable.put("nodeB", managerNodeB);
    }

    @Reference
    public void setManagerNodeC(WorkerManager managerNodeC) {
        this.managerNodeC = managerNodeC;
        workerManagerTable.put("nodeC", managerNodeC);
    }

    @Reference
    public void setManagerNodeD(WorkerManager managerNodeD) {
        this.managerNodeD = managerNodeD;
        workerManagerTable.put("nodeD", managerNodeD);
    }

    @Reference
    public void setManagerNodeE(WorkerManager managerNodeE) {
        this.managerNodeE = managerNodeE;
        workerManagerTable.put("nodeE", managerNodeE);
    }

    private void startNewComponents(
            Vector<CallableReferenceImpl<WorkerService>> vector) {
        log.info("Starting new components");
        WorkpoolService wp = reference.getService();
        // CallableReferenceImpl<WorkpoolService> sink =
        // (CallableReferenceImpl<WorkpoolService>) reference;
        Job j = new NullJob();
        for (CallableReferenceImpl<WorkerService> item : vector) {
            // WorkerService service = item.getService();
            // service.start();
            // service.computeFirstTime(j, sink);
            log.info("Send PostWorkerReference...");
            wp.PostWorkerReference(item);
        }
        if (myReference != null)
            wp.registerManager(myReference);
    }

    public void setCycleTime(long cycle) {
        this.cycleTime = cycle;
    }

    @SuppressWarnings("unchecked")
    /*
     * This gets the number of workers workerNo and instantiates them
     */
    public void start() {
        this.myReference = (CallableReferenceImpl<WorkpoolManager>) workpoolManagerContext
                .createSelfReference(WorkpoolManager.class, "WorkpoolManager");
        this.workersNo = Integer.parseInt(this.workers);
        this.nodesNo = Integer.parseInt(this.nodes);
        this.referenceInjection = (Integer.parseInt(this.injection) != 0);
        log.info("Starting WorkpoolManager Component with #" + workersNo
                + " workers and #" + nodes + " nodes");
        nodesNo = workerManagerTable.values().size();
        // Sets info in the bean.
        bean.setWorkers(this.workersNo);
        bean.setNodeNumbers(nodesNo);
        Vector<CallableReferenceImpl<WorkerService>> workerRefs = new Vector<CallableReferenceImpl<WorkerService>>();
        int exactTimes = workersNo / nodesNo;
        for (int i = 0; i < exactTimes; ++i) {
            for (WorkerManager manager : workerManagerTable.values()) {
                manager.start();
                if (manager != null) {
                    System.err.println("Actual load  = "
                            + manager.getNodeLoad() + " for node ");
                    addNewComponent(manager, workerRefs);
                }
            }
        }

        int module = (workersNo % nodesNo);
        int n = 0;
        if (module > 0) {
            Vector<String> v = new Vector(workerManagerTable.keySet());
            Collections.sort(v);
            // Iterator<WorkerManager> iter =
            // workerManagerTable.values().iterator();
            // Display (sorted) hashtable.
            for (Enumeration<String> e = v.elements(); (e.hasMoreElements() && n < module); ++n) {
                String key = e.nextElement();
                WorkerManager m = workerManagerTable.get(key);
                System.err.println("Module Actual load  = " + m.getNodeLoad()
                        + " for node ");
                addNewComponent(m, workerRefs);
            }
        }
        startNewComponents(workerRefs);
        bean.addListener(this);
        TimerTask task = new WorkpoolManagerImpl.RuleEngineTrigger();
        timer.scheduleAtFixedRate(task, 3000, cycleTime);
    }

    private void checkLoadInNodes() {
        System.out.println("CheckLoadInNodes");
        int number = 1;
        double loadAverage = 0;
        for (WorkerManager manager : workerManagerTable.values()) {
            loadAverage += manager.getNodeLoad();
            number++;
        }
        bean.setLoadAverage(loadAverage / number);
    }

    private void computeUsageFactor() {
        bean.setUsageFactor();
    }

    private void checkEstimedQueueSize() {
        WorkpoolService wp = reference.getService();

        if (wp != null) {
            int size = wp.estimatedQueueSize();
            log.info("Estimed Queue Size =" + size);
            bean.setEstimedQueueSize(size);
        }
    }

    private WorkerManager findMinLoad() {
        double load = 0;
        // workerManagerTable.values().iterator().next().getNodeLoad();
        WorkerManager toFind = null;
        for (WorkerManager manager : workerManagerTable.values()) {
            if (load == 0) {
                load = manager.getNodeLoad();
                toFind = manager;
            } else if (manager.getNodeLoad() < load) {
                load = manager.getNodeLoad();
                toFind = manager;
            }
        }
        return toFind;
    }

    private void checkServiceTime() {
        WorkpoolService wp = reference.getService();

        if (wp != null) {
            double time = wp.getServiceTime();
            log.info("Average System Service Time =" + time);
            bean.setAverageServiceTime(time);
        }
    }

    private void checkArrivalTime() {
        WorkpoolService wp = reference.getService();

        if (wp != null) {
            double time = wp.getArrivalTime();
            log.info("Average Arrival Service Time =" + time);
            bean.setAverageArrivalTime(time);
        }
    }

    private void checkActiveWorkers() {
        bean.setWorkers(this.activeWorkers());
    }

    private void getProcessedItem() {
        WorkpoolService wp = reference.getService();
        if (wp != null) {
            long computed = wp.getJobComputed();
            log.info("The system has already computed " + computed + " jobs");
            bean.setJobComputed(computed);
        }
    }

    private boolean removeComponent(WorkerManager manager, int k) {
        manager.removeWorkers(k);
        activeWorkers.decrementAndGet();
        return true;
    }

    @SuppressWarnings("unchecked")
    private boolean addNewComponent(WorkerManager manager,
            Vector<CallableReferenceImpl<WorkerService>> workerRefs) {
        CallableReferenceImpl<WorkerService> workerReference = (CallableReferenceImpl<WorkerService>) manager
                .addWorker();

        if (workerReference != null) {
            /* if i'll decide to use dynamically generated references */
            if (referenceInjection) {
                workerReference.getService();
                String uri = workerReference.getEndpointReference().getURI();
                int nameIndex = uri.indexOf("/");
                String componentName = uri.substring(0, nameIndex);
                if (componentName.startsWith("/"))
                    componentName = uri.substring(1, uri.length());
                if (componentName.endsWith("/"))
                    componentName = uri.substring(0, uri.length() - 1);
                // String componentName = uri.substring(0, nameIndex-1);

                log.info("Adding wire from WorkpoolComponentService to "
                        + componentName);
                String referenceName = "ref" + componentName;

                /*
                 * I'm updating the WorkpoolServiceComponent with a new
                 * reference to a just created component I assume that the
                 * WorkpoolManagerService and the WorkpoolServiceComponent stay
                 * in the same JVM It's like in the scdl there were: <reference
                 * name=referenceName target="componentName"/> With this then
                 * I've a wire WorkpoolService---> a new Worker
                 */
                try {
                    node.addComponentReferenceWire(referenceName, "nodeA",
                            "Workpool.composite", "workpool.WorkerServiceImpl",
                            WorkerService.class, "WorkpoolServiceComponent",
                            componentName);
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
                log.info("Sending reference name " + referenceName
                        + " to WorkpoolService");
                // TODO: this was part of dynamic wiring, but it doesn't work.
                // reference.getService().PostWorkerName(referenceName);

            } else {
                // log.info("Sending callable reference to WorkpoolService
                // placed at -->"+reference);
                // reference.getService().PostWorkerReference(workerReference);
                workerRefs.add(workerReference);
            }
            activeWorkers.incrementAndGet();
            return true;
        }
        return false;
    }

    public int activeWorkers() {

        return activeWorkers.get();
    }

    private void doRun(WorkpoolBean bean) {

        long startTime = System.currentTimeMillis();
        updateRuleLock.lock();
        if (wm == null)
            wm = ruleBase.newStatefulSession();
        if (this.handle == null)
            handle = wm.insert(bean);
        else {
            wm.update(handle, bean);
        }
        wm.fireAllRules();
        updateRuleLock.unlock();

        System.out.println("Engine rule overhead = "
                + (System.currentTimeMillis() - startTime));
    }

    private RuleBase readRule(String rule) {

        PackageBuilder packBuilder = new PackageBuilder();
        try {
            packBuilder.addPackageFromDrl(new StringReader(rule));
        } catch (DroolsParserException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Package pkg = packBuilder.getPackage();
        RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        try {
            ruleBase.addPackage(pkg);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ruleBase;
    }

    public void acceptRules(String rules) {
        this.rules = rules;
        if (ruleBase == null) {
            RuleBase base = readRule(rules);
            if (base != null) {
                ruleBase = base;
            }
        } else {
            updateRuleLock.lock();
            // i have already a rule: updating
            ruleBase = readRule(rules);
            wm = ruleBase.newStatefulSession();
            handle = null;
            updateRuleLock.unlock();
        }

        System.out.println("Accepted rules = " + rules);
    }

    public String getRules() {
        return rules;
    }

    private WorkerManager findMaxLoadNode() {
        double load = 0.0;
        WorkerManager toFind = null;
        for (WorkerManager manager : workerManagerTable.values()) {
            if (manager.getNodeLoad() > load) {
                load = manager.getNodeLoad();
                toFind = manager;
            }
        }
        return toFind;

    }

    public void setWorkpoolReference(
            ServiceReference<WorkpoolService> serviceReference) {
        reference = serviceReference;
    }

    public void setNode(SCANode arg0) {
        node = (SCANodeImpl) arg0;
    }

    public void handleEvent(WorkpoolEvent ev) {
        if (ev == null)
            return;

        String nodeName = ev.getNodeName();

        switch (ev.getType()) {
        case WorkpoolEvent.SINGLE_ADD_WORKER: {
            if (nodeName != null) {
                Vector<CallableReferenceImpl<WorkerService>> workerRefs = new Vector<CallableReferenceImpl<WorkerService>>();

                // in this case I have a nodeName
                if (!nodeName.equals("")
                        && (workerManagerTable.containsKey(nodeName))) {
                    WorkerManager manager = workerManagerTable.get(nodeName);
                    addNewComponent(manager, workerRefs);
                    startNewComponents(workerRefs);
                } else if (nodeName.equals("")) {
                    WorkerManager manager = findMinLoad();
                    addNewComponent(manager, workerRefs);
                    startNewComponents(workerRefs);
                }
            }
            break;
        }
        case WorkpoolEvent.EVENT_MULTIPLE_ADD_WORKER: {
            Vector<CallableReferenceImpl<WorkerService>> workerRefs = new Vector<CallableReferenceImpl<WorkerService>>();

            if (nodeName.equals("")) {

                WorkerManager manager = findMinLoad();
                int k = ev.workers();
                for (int h = 0; h < k; ++h) {
                    addNewComponent(manager, workerRefs);
                }
            } else {
                WorkerManager manager = workerManagerTable
                        .get(ev.getNodeName());
                int k = ev.workers();
                for (int h = 0; h < k; ++h) {
                    addNewComponent(manager, workerRefs);
                }
            }
            startNewComponents(workerRefs);
            break;
        }
        case WorkpoolEvent.SINGLE_REMOVE_WORKER: {
            if (nodeName != null) {
                // in this case I have a nodeName
                if (!nodeName.equals("")
                        && (workerManagerTable.containsKey(nodeName))) {
                    WorkerManager manager = workerManagerTable.get(nodeName);
                    removeComponent(manager, 1);
                } else if (nodeName.equals("")) {
                    WorkerManager manager = findMaxLoadNode();
                    removeComponent(manager, 1);
                }
            }
            break;
        }
        case WorkpoolEvent.EVENT_MULTIPLE_REMOVE_WORKER: {
            if (nodeName.equals("")) {
                WorkerManager manager = findMaxLoadNode();
                removeComponent(manager, ev.workers());

            } else {
                WorkerManager manager = workerManagerTable.get(nodeName);
                removeComponent(manager, ev.workers());
            }
            break;
        }
        }

    }

    @Destroy
    public void onExit() {
        // do cleanup
        this.timer.cancel();
        this.timer.purge();
    }

    public void stopAutonomicCycle() {
        this.timer.cancel();
        this.timer.purge();
        this.timer = null;
    }

    public void startAutonomicCycle() {
        if (this.timer == null) {
            this.timer = new Timer();
            TimerTask task = new WorkpoolManagerImpl.RuleEngineTrigger();
            timer.schedule(task, 3000, cycleTime);
        }
    }
}
