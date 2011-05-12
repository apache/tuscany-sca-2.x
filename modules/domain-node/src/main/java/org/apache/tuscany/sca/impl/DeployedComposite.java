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

package org.apache.tuscany.sca.impl;

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
    private Contribution contribution;
    private List<Contribution> dependedOnContributions;
    private Composite builtComposite;
    
    private CompositeActivator compositeActivator;
    private CompositeContext compositeContext;
    private Deployer deployer;
    private EndpointRegistry endpointRegistry;
    private ExtensionPointRegistry extensionPointRegistry;

    public DeployedComposite(Composite composite,
                             Contribution contribution,
                             List<Contribution> dependedOnContributions,
                             Deployer deployer,
                             CompositeActivator compositeActivator,
                             EndpointRegistry endpointRegistry,
                             ExtensionPointRegistry extensionPointRegistry) throws ValidationException, ActivationException {
        this.composite = composite;
        this.contribution = contribution;
        this.dependedOnContributions = dependedOnContributions;
        this.deployer = deployer;
        this.compositeActivator = compositeActivator;
        this.endpointRegistry = endpointRegistry;
        this.extensionPointRegistry = extensionPointRegistry;
        try {
            init();
        } catch (ContributionResolveException e) {
            throw new ActivationException(e);
        } catch (CompositeBuilderException e) {
            throw new ActivationException(e);
        }
    }

    protected void init() throws ValidationException, ActivationException, ContributionResolveException, CompositeBuilderException {
        
        List<Contribution> contributions = new ArrayList<Contribution>();
        contributions.add(contribution);
        contributions.get(0).getDeployables().clear();
        contributions.get(0).getDeployables().add(composite);

        Monitor monitor = deployer.createMonitor();
        builtComposite = deployer.build(contributions, dependedOnContributions, new HashMap<QName, List<String>>(), monitor);
        builtComposite.setName(composite.getName());
        monitor.analyzeProblems();

        compositeContext = new CompositeContext(extensionPointRegistry, 
                                                endpointRegistry, 
                                                builtComposite, 
                                                null, // nothing appears to use the domain name in CompositeContext 
                                                null, // don't need node uri
                                                deployer.getSystemDefinitions());
                       
        this.uri = getCompositeURI(composite, contribution);
    }

    public void start() throws ActivationException {
        compositeActivator.activate(compositeContext, builtComposite);
        compositeActivator.start(compositeContext, builtComposite);
        endpointRegistry.addRunningComposite(contribution.getURI(), builtComposite);
    }

    public void stop() throws ActivationException {
        endpointRegistry.removeRunningComposite(contribution.getURI(), builtComposite.getName());
        compositeActivator.stop(compositeContext, builtComposite);
        compositeActivator.deactivate(builtComposite);
    }
    
    public String getURI() {
        return uri;
    }

    /**
     * Deployable composites don't have the uri set so get it from the artifact in the contribution
     * // TODO: fix the Tuscany code so this uri is correctly set and this method isn't needed
     */
    private static String getCompositeURI(Composite c, Contribution contribution) {
        for (Artifact a : contribution.getArtifacts()) {
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
