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
package org.apache.tuscany.sca.itest.builder;

import javax.xml.stream.XMLOutputFactory;

import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.UtilityExtensionPoint;
import org.apache.tuscany.sca.core.assembly.CompositeActivator;
import org.apache.tuscany.sca.monitor.Monitor;
import org.apache.tuscany.sca.monitor.MonitorFactory;
import org.apache.tuscany.sca.node.Contribution;
import org.apache.tuscany.sca.node.ContributionLocationHelper;
import org.apache.tuscany.sca.node.Node;
import org.apache.tuscany.sca.node.NodeFactory;
import org.apache.tuscany.sca.node.impl.NodeImpl;


public class CustomCompositeBuilder {

    private boolean nonWiring;
    private Node node;
    private ExtensionPointRegistry extensionPoints;
    private Monitor monitor;
    private StAXArtifactProcessor<Composite> compositeProcessor;
    private Composite domainComposite;

    public CustomCompositeBuilder(boolean nonWiring) {
        this.nonWiring = nonWiring;
    }
    
    public void loadContribution(String compositeURL, String sourceURI, String sourceURL) throws Exception {
        NodeFactory nodeFactory = NodeFactory.newInstance();
        node = nodeFactory.createNode(compositeURL, new Contribution(sourceURI, sourceURL));
        node.start();
        
        // get some things out of the extension registry
        extensionPoints = ((NodeImpl)node).getExtensionPoints();
        
        UtilityExtensionPoint utilities = extensionPoints.getExtensionPoint(UtilityExtensionPoint.class);
        MonitorFactory monitorFactory = utilities.getUtility(MonitorFactory.class);
        monitor = monitorFactory.createMonitor(); 
        
        StAXArtifactProcessorExtensionPoint xmlProcessors = extensionPoints.getExtensionPoint(StAXArtifactProcessorExtensionPoint.class);
        compositeProcessor = xmlProcessors.getProcessor(Composite.class);
        
        CompositeActivator compositeActivator = utilities.getUtility(CompositeActivator.class);
        domainComposite = compositeActivator.getDomainComposite();
    }
    
    /**
     * Returns the delegating model processor.
     * @return the delegating model processor
     */
    public StAXArtifactProcessor<Object> getModelProcessor() {
        return null;//compositeProcessor;
    }
    
    /**
     * Returns the XML output factory.
     * @return the XML output factory
     */
    public XMLOutputFactory getOutputFactory() {
        return null; //outputFactory;
    }
    
    /**
     * Returns the domain composite.
     * @return the domain composite model object
     */
    public Composite getDomainComposite() {
        return (Composite) ((NodeImpl)node).getDomainComposite();
    }
    
    /**
     * Returns the monitor.
     * @return the monitor instance
     */
    public Monitor getMonitor() {
        return monitor;
    }

}
