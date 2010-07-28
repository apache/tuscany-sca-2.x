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

package org.apache.tuscany.sca.contribution.processor;

import org.apache.tuscany.sca.contribution.Artifact;
import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.UtilityExtensionPoint;
import org.apache.tuscany.sca.monitor.DefaultMonitorFactory;
import org.apache.tuscany.sca.monitor.Monitor;
import org.apache.tuscany.sca.monitor.MonitorFactory;

/**
 * Context for contribution processors
 * @tuscany.spi.extension.asclient
 */
public class ProcessorContext {
    protected Contribution contribution;
    protected Artifact artifact;
    protected Monitor monitor;
    protected Object parentModel;

    /**
     * @param contribution
     * @param monitor
     */
    public ProcessorContext(Contribution contribution, Monitor monitor) {
        super();
        this.contribution = contribution;
        this.monitor = monitor;
    }

    public ProcessorContext(Monitor monitor) {
        super();
        this.monitor = monitor;
    }
    
    public ProcessorContext(ExtensionPointRegistry registry) {
        super();
        MonitorFactory monitorFactory =
            registry.getExtensionPoint(UtilityExtensionPoint.class).getUtility(MonitorFactory.class);
        this.monitor = monitorFactory.createMonitor();
    }

    public ProcessorContext() {
        super();
        this.monitor = new DefaultMonitorFactory().createMonitor();
    }

    /**
     * Get the current contribution
     * @return The current contribution
     */
    public Contribution getContribution() {
        return contribution;
    }

    /**
     * Set the current contribution
     * @param contribution
     * @return
     */
    public Contribution setContribution(Contribution contribution) {
        Contribution old = this.contribution;
        this.contribution = contribution;
        return old;
    }

    public Monitor getMonitor() {
        return monitor;
    }

    public Monitor setMonitor(Monitor monitor) {
        Monitor old = this.monitor;
        this.monitor = monitor;
        return old;
    }

    public Object getParentModel() {
        return parentModel;
    }

    public Object setParentModel(Object parentMObject) {
        Object old = this.parentModel;
        this.parentModel = parentMObject;
        return old;
    }

    /**
     * Get the current artifact
     * @return The current artifact
     */
    public Artifact getArtifact() {
        return artifact;
    }

    /**
     * Set the current artifact. This should be called by URLArtifactProcessor to set the document
     * context (such as the URI of the composite file).
     * 
     * @param artifact The new artifact
     * @return The old artifact
     */
    public Artifact setArtifact(Artifact artifact) {
        Artifact old = this.artifact;
        this.artifact = artifact;
        return old;
    }

}
