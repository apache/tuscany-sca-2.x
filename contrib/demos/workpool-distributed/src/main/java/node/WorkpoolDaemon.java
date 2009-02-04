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

package node;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.commons.daemon.Daemon;
import org.apache.commons.daemon.DaemonContext;
import org.apache.commons.daemon.DaemonController;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.Service;
import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.contribution.DeployedArtifact;
import org.apache.tuscany.sca.contribution.service.impl.ContributionServiceImpl;
import org.apache.tuscany.sca.domain.SCADomain;
import org.apache.tuscany.sca.node.NodeException;
import org.apache.tuscany.sca.node.NodeManagerInitService;
import org.apache.tuscany.sca.node.SCANode;
import org.apache.tuscany.sca.node.SCANodeFactory;
import org.apache.tuscany.sca.node.impl.SCANodeImpl;
import org.apache.tuscany.sca.node.management.SCANodeManagerInitService;

import java.net.URI;

import workpool.WorkerManager;
import workpool.WorkerManagerImpl;
import workpool.WorkpoolManager;
import workpool.WorkpoolService;
import workpool.WorkpoolServiceImpl;

/**
 * This client program shows how to run a distributed SCA node. In this case a
 * calculator node has been constructed specifically for running the calculator
 * composite. Internally it creates a representation of a node and associates a
 * distributed domain with the node. This separation is made different
 * implementations of the distributed domain can be provided.
 */
public class WorkpoolDaemon implements Daemon, Runnable {
    private String domainName;
    private String nodeName;
    private long iterations;
    private long jobsNo;
    private long workerNo;
    private SCANode node;
    private boolean stopped = false;
    private DaemonController controller = null;
    private Thread thread = null;
    private String ruleFile = "workerRules.drl";

    /*
     * public static void main(String[] args) throws Exception {
     *  // Check that the correct arguments have been provided if (null == args ||
     * args.length < 4) { System.err.println("Usage: java WorkpoolNode
     * domainname nodename iterTest workerNo"); System.exit(1); }
     * 
     * try { String domainName = args[0]; String nodeName = args[1]; long
     * iterations = Long.parseLong(args[2]); long jobsNo =
     * Long.parseLong(args[3]); long workerNo = Long.parseLong(args[4]);
     * ClassLoader cl = WorkpoolDaemon.class.getClassLoader();
     * 
     * SCANodeFactory nodeFactory = SCANodeFactory.newInstance(); node =
     * nodeFactory.createSCANode(null, domainName);
     * node.addContribution(nodeName, cl.getResource(nodeName + "/"));
     * node.addToDomainLevelComposite(new QName("http://sample", "Workpool"));
     * node.start(); // nodeA is the head node and runs some tests while all
     * other nodes // simply listen for incoming messages
     * 
     * FileReader rules = new FileReader("workerRules.drl"); StringBuffer buffer =
     * new StringBuffer();
     * 
     * BufferedReader br = new BufferedReader(rules); String ruleString; do {
     * ruleString = br.readLine(); if (ruleString!=null) {
     * buffer.append(ruleString);} } while (ruleString!=null);
     * 
     * if ( nodeName.equals("nodeA") ) { // do some application stuff
     * WorkpoolService workpoolService =
     * node.getDomain().getService(WorkpoolService.class,
     * "WorkpoolServiceComponent"); workpoolService.start();
     * NodeManagerInitService nodeInit =
     * node.getDomain().getService(NodeManagerInitService.class,
     * "WorkpoolManagerComponent/NodeManagerInitService");
     * nodeInit.setNode(node); WorkpoolManager workpoolManager =
     * node.getDomain().getService(WorkpoolManager.class,
     * "WorkpoolManagerComponent/WorkpoolManager");
     * workpoolManager.setWorkpoolReference(node.getDomain().getServiceReference(WorkpoolService.class,
     * "WorkpoolServiceComponent"));
     * workpoolManager.acceptRules(buffer.toString()); workpoolManager.start();
     * int items[] = {3,4,5,6,3,6,3,5,9,5,6};
     * 
     * double x = 398349; for (int i = 0; i < jobsNo; ++i)
     * workpoolService.submit(new TestJob(x,iterations,items));
     * 
     * TestJob j = new TestJob(-1.0,true); for (int i = 0; i < workerNo+1; ++i){
     * j.setEOS(); workpoolService.submit(j); } } try { if
     * (nodeName.equals("nodeB")) { NodeManagerInitService serviceNodeB =
     * node.getDomain().getService(NodeManagerInitService.class,"WorkerManagerNodeBComponent/NodeManagerInitService");
     * serviceNodeB.setNode(node); } if (nodeName.equals("nodeC")) {
     * NodeManagerInitService workerManagerC =
     * node.getDomain().getService(NodeManagerInitService.class,"WorkerManagerNodeCComponent/NodeManagerInitService");
     * workerManagerC.setNode(node); } if (nodeName.equals("nodeD")) {
     * NodeManagerInitService workerManagerD =
     * node.getDomain().getService(NodeManagerInitService.class,"WorkerManagerNodeDComponent/NodeManagerInitService");
     * workerManagerD.setNode(node); } if (nodeName.equals("nodeE")) {
     * NodeManagerInitService workerManagerE =
     * node.getDomain().getService(NodeManagerInitService.class,"WorkerManagerNodeEComponent/NodeManagerInitService");
     * workerManagerE.setNode(node); }
     * 
     * System.out.println("Node started (press enter to shutdown)");
     * System.in.read(); } catch (IOException e) { e.printStackTrace(); } //
     * stop the node and all the domains in it node.stop(); node.destroy();
     * System.exit(0); } catch(Exception ex) { System.err.println("Exception in
     * node - " + ex.getMessage()); ex.printStackTrace(System.err); } }
     */
    public void destroy() {
        // TODO Auto-generated method stub

    }

    public void init(DaemonContext arg0) throws Exception {
        String[] args = arg0.getArguments();
        domainName = args[0];
        nodeName = args[1];
        iterations = Long.parseLong(args[2]);
        jobsNo = Long.parseLong(args[3]);
        workerNo = Long.parseLong(args[4]);
        if (args.length == 6) {
            ruleFile = args[5];
        }
        this.controller = arg0.getController();
        // this.thread=new Thread(this);
    }

    public void start() throws Exception {

        ClassLoader cl = WorkpoolDaemon.class.getClassLoader();

        SCANodeFactory nodeFactory = SCANodeFactory.newInstance();
        node = nodeFactory.createSCANode(null, domainName);
        node.addContribution(nodeName, cl.getResource(nodeName + "/"));
        node.addToDomainLevelComposite(new QName("http://sample", "Workpool"));
        node.start();
        // nodeA is the head node and runs some tests while all other nodes
        // simply listen for incoming messages

        FileReader rules = new FileReader(ruleFile);
        StringBuffer buffer = new StringBuffer();

        BufferedReader br = new BufferedReader(rules);
        String ruleString;
        do {
            ruleString = br.readLine();
            if (ruleString != null) {
                buffer.append(ruleString + "\n");
            }
        } while (ruleString != null);

        if (nodeName.equals("nodeA")) {
            // do some application stuff
            WorkpoolService workpoolService = node.getDomain().getService(
                    WorkpoolService.class, "WorkpoolServiceComponent");
            workpoolService.start();
            SCANodeManagerInitService nodeInit = node.getDomain().getService(
                    SCANodeManagerInitService.class,
                    "WorkpoolManagerComponent/NodeManagerInitService");
            nodeInit.setNode(node);
            WorkpoolManager workpoolManager = node.getDomain().getService(
                    WorkpoolManager.class,
                    "WorkpoolManagerComponent/WorkpoolManager");
            workpoolManager.setWorkpoolReference(node.getDomain()
                    .getServiceReference(WorkpoolService.class,
                            "WorkpoolServiceComponent"));
            workpoolManager.acceptRules(buffer.toString());
            workpoolManager.start();

            int items[] = { 3, 4, 5, 6, 3, 6, 3, 5, 9, 5, 6 };

            double x = 398349;
            for (int i = 0; i < jobsNo; ++i) {
                workpoolService.submit(new TestJob(x, iterations, items));
            }
            TestJob j = new TestJob(-1.0, true);
            for (int i = 0; i < workerNo + 1; ++i) {
                j.setEOS();
                workpoolService.submit(j);
            }

        }
        if (nodeName.equals("nodeB")) {
            SCANodeManagerInitService workerManagerNodeB = node
                    .getDomain()
                    .getService(SCANodeManagerInitService.class,
                            "WorkerManagerNodeBComponent/NodeManagerInitService");
            workerManagerNodeB.setNode(node);
        }

        if (nodeName.equals("nodeC")) {
            SCANodeManagerInitService workerManagerNodeC = node
                    .getDomain()
                    .getService(SCANodeManagerInitService.class,
                            "WorkerManagerNodeCComponent/NodeManagerInitService");
            workerManagerNodeC.setNode(node);
        }

        if (nodeName.equals("nodeD")) {
            SCANodeManagerInitService workerManagerNodeD = node
                    .getDomain()
                    .getService(SCANodeManagerInitService.class,
                            "WorkerManagerNodeDComponent/NodeManagerInitService");
            workerManagerNodeD.setNode(node);
        }

        if (nodeName.equals("nodeE")) {
            SCANodeManagerInitService workerManagerNodeE = node
                    .getDomain()
                    .getService(SCANodeManagerInitService.class,
                            "WorkerManagerNodeEComponent/NodeManagerInitService");
            workerManagerNodeE.setNode(node);
        }

        this.waitForever();
        // this.thread.start();
    }

    public void stop() throws Exception {
        Thread.currentThread().interrupt();
        // thread.interrupt();
        node.stop();
        node.destroy();
    }

    private synchronized void waitForever() {
        while (!stopped) {
            try {
                wait();
            } catch (InterruptedException ex) {
                stopped = true;
                return;
            }
        }
    }

    public void run() {
        waitForever();
    }
}
