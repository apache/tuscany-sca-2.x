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

import java.beans.*;
import java.util.Vector;
import java.util.logging.*;

public class WorkpoolBean {
    private Vector<WorkpoolBeanListener> listeners = new Vector<WorkpoolBeanListener>();
    double loadAverage = 0;
    int nodeNumbers = 0;
    int workers = 0;
    int estimedQueueSize = 0;
    double averageServiceTime = 0;
    double averageArrivalTime = 0;
    double usageFactor = 0;
    private final PropertyChangeSupport changes = new PropertyChangeSupport(
            this);
    long jobComputed = 0;
    boolean singleAction = false;
    private Logger log = Logger.getLogger(WorkpoolBean.class.getName());

    public void setNodeNumbers(int n) {
        this.nodeNumbers = n;
    }

    public void setWorkers(int w) {
        this.workers = w;
    }

    public void setLoadAverage(double loadAverage) {
        this.loadAverage = loadAverage;
    }

    public void setAverageServiceTime(double service) {
        this.averageServiceTime = service;
    }

    public void setAverageArrivalTime(double service) {
        this.averageArrivalTime = service;
    }

    public double getAverageArrivalTime() {
        return this.averageArrivalTime;
    }

    public double getUtilizationFactor() {
        return usageFactor;
    }

    public void setUsageFactor() {
        usageFactor = averageServiceTime / averageArrivalTime;
    }

    public void setEstimedQueueSize(int size) {
        estimedQueueSize = size;
    }

    public int getEstimedQueueSize() {
        return estimedQueueSize;
    }

    public double getLoadAverage() {
        return this.loadAverage;
    }

    public int getWorkers() {
        return this.workers;
    }

    public int getNodeNumbers() {
        return this.nodeNumbers;
    }

    public double getAverageServiceTime() {
        return this.averageServiceTime;
    }

    public void addPropertyChangeListener(final PropertyChangeListener l) {
        this.changes.addPropertyChangeListener(l);
    }

    public void removePropertyChangeListener(final PropertyChangeListener l) {
        this.changes.removePropertyChangeListener(l);
    }

    private synchronized void fireWorkpoolEvent(WorkpoolEvent ev) {
        for (WorkpoolBeanListener l : listeners) {
            l.handleEvent(new WorkpoolEvent(ev));
        }
    }

    public void addWorkersToNode(int k, String nodeName) {
        log.info("Adding a worker to node " + nodeName);
        WorkpoolEvent ev = new WorkpoolEvent(this,
                WorkpoolEvent.EVENT_MULTIPLE_ADD_WORKER, k, nodeName);
        fireWorkpoolEvent(ev);
    }

    public void addWorkerToNode(String nodeName) {
        log.info("Adding a worker to node " + nodeName);
        WorkpoolEvent ev = new WorkpoolEvent(this,
                WorkpoolEvent.SINGLE_ADD_WORKER, 1, nodeName);
        fireWorkpoolEvent(ev);
    }

    public void removeWorkersToNode(int k, String nodeName) {
        log.info("Removing a worker to node " + nodeName);
        WorkpoolEvent ev = new WorkpoolEvent(this,
                WorkpoolEvent.EVENT_MULTIPLE_REMOVE_WORKER, k, nodeName);
        fireWorkpoolEvent(ev);
    }

    public void removeWorkerToNode(String nodeName) {
        log.info("Removing a worker to node " + nodeName);
        WorkpoolEvent ev = new WorkpoolEvent(this,
                WorkpoolEvent.SINGLE_REMOVE_WORKER, 1, nodeName);
        fireWorkpoolEvent(ev);
    }

    public synchronized void addListener(WorkpoolBeanListener l) {
        this.listeners.add(l);
    }

    public synchronized void removeListener(WorkpoolBeanListener l) {
        this.listeners.remove(l);
    }

    public void setJobComputed(long jobComputed) {
        this.jobComputed = jobComputed;

    }

    public void setSingleAction() {
        singleAction = true;
    }

    public boolean getSingleAction() {
        return singleAction;
    }

    public long getJobComputed() {
        return this.jobComputed;
    }
}
