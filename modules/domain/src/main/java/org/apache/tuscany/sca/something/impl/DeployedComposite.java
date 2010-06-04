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

package org.apache.tuscany.sca.something.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilderException;
import org.apache.tuscany.sca.context.CompositeContext;
import org.apache.tuscany.sca.contribution.Artifact;
import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.contribution.processor.ContributionResolveException;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.deployment.Deployer;
import org.apache.tuscany.sca.monitor.Monitor;
import org.apache.tuscany.sca.monitor.ValidationException;
import org.apache.tuscany.sca.runtime.ActivationException;
import org.apache.tuscany.sca.runtime.CompositeActivator;
import org.apache.tuscany.sca.runtime.EndpointRegistry;

public class DeployedComposite {
    
    private String uri;
    private Composite composite; 
    private InstalledContribution installedContribution; 
    private List<Contribution> dependedOnContributions;
    private Composite domainComposite; // TODO: this is misleadingly named
    
    private CompositeActivator compositeActivator;
    private CompositeContext compositeContext;
    private Deployer deployer;
    private EndpointRegistry endpointRegistry;
    private ExtensionPointRegistry extensionPointRegistry;

    public DeployedComposite(Composite composite,
                             InstalledContribution ic,
                             List<Contribution> dependedOnContributions,
                             Deployer deployer,
                             CompositeActivator compositeActivator,
                             EndpointRegistry endpointRegistry,
                             ExtensionPointRegistry extensionPointRegistry) throws ActivationException {
        this.composite = composite;
        this.installedContribution = ic;
        this.dependedOnContributions = dependedOnContributions;
        this.deployer = deployer;
        this.compositeActivator = compositeActivator;
        this.endpointRegistry = endpointRegistry;
        this.extensionPointRegistry = extensionPointRegistry;
        try {
            init();
        } catch (Exception e) {
            throw new ActivationException(e);
        }
    }

    protected void init() throws ValidationException, ContributionResolveException, CompositeBuilderException, ActivationException {
        
        List<Contribution> contribution = new ArrayList<Contribution>();
        contribution.add(installedContribution.getContribution());
        contribution.get(0).getDeployables().clear();
        contribution.get(0).getDeployables().add(composite);
        
        Monitor monitor = deployer.createMonitor();
// TODO: is the ContextMonitor neccessary here?         
//        Monitor tcm = monitorFactory.setContextMonitor(monitor);
//        try {
            
            domainComposite = deployer.build(contribution, dependedOnContributions, new HashMap<QName, List<String>>(), monitor);
            monitor.analyzeProblems();

//        } finally {
//            monitorFactory.setContextMonitor(tcm);
//        }
        
        compositeContext = new CompositeContext(extensionPointRegistry, 
                                                endpointRegistry, 
                                                domainComposite, 
                                                null, // nothing appears to use the domain name in CompositeContext 
                                                null, // don't need node uri
                                                deployer.getSystemDefinitions());
                       
        CompositeContext.setThreadCompositeContext(compositeContext); // TODO: what is this doing?

        compositeActivator.activate(compositeContext, domainComposite);
        compositeActivator.start(compositeContext, domainComposite);

        this.uri = getCompositeURI(composite, installedContribution);
    }

    public void unDeploy() throws ActivationException {
        compositeActivator.stop(compositeContext, domainComposite);
        compositeActivator.deactivate(domainComposite);
    }
    
    public String getURI() {
        return uri;
    }

    /**
     * Deployable composites don't have the uri set so get it from the artifact in the contribution
     * // TODO: fix the Tuscany code so this uri is correctly set and this method isn't needed
     */
    protected String getCompositeURI(Composite c, InstalledContribution ic) {
        for (Artifact a : ic.getContribution().getArtifacts()) {
            if (a.getModel() != null) {
                if (a.getModel() instanceof Composite) {
                    Composite cm = a.getModel();
                    if (c.getName().equals(cm.getName())) {
                        return cm.getURI();
                    }
                }
            }
        }
        // shouldn't ever happen
        throw new IllegalStateException("can't determine composte uri");
    }
}
