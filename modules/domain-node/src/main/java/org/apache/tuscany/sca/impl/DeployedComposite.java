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
import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.contribution.processor.ContributionResolveException;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.deployment.Deployer;
import org.apache.tuscany.sca.monitor.Monitor;
import org.apache.tuscany.sca.monitor.ValidationException;
import org.apache.tuscany.sca.runtime.ActivationException;
import org.apache.tuscany.sca.runtime.CompositeActivator;
import org.apache.tuscany.sca.runtime.DomainRegistry;

public class DeployedComposite {
    
    private Composite composite; 
    private Contribution contribution;
    private List<Contribution> dependedOnContributions;
    private Composite builtComposite;
    
    private CompositeActivator compositeActivator;
    private CompositeContext compositeContext;
    private Deployer deployer;
    private DomainRegistry domainRegistry;
    private ExtensionPointRegistry extensionPointRegistry;

    public DeployedComposite(Composite composite,
                             Contribution contribution,
                             List<Contribution> dependedOnContributions,
                             Deployer deployer,
                             CompositeActivator compositeActivator,
                             DomainRegistry domainRegistry,
                             ExtensionPointRegistry extensionPointRegistry) throws ValidationException, ActivationException {
        this.composite = composite;
        this.contribution = contribution;
        this.dependedOnContributions = dependedOnContributions;
        this.deployer = deployer;
        this.compositeActivator = compositeActivator;
        this.domainRegistry = domainRegistry;
        this.extensionPointRegistry = extensionPointRegistry;
        try {
            build();
        } catch (ContributionResolveException e) {
            throw new ActivationException(e);
        } catch (CompositeBuilderException e) {
            throw new ActivationException(e);
        }
    }

    protected void build() throws ValidationException, ActivationException, ContributionResolveException, CompositeBuilderException {
        
        List<Contribution> contributions = new ArrayList<Contribution>();
        contributions.add(contribution);
        contributions.get(0).getDeployables().clear();
        contributions.get(0).getDeployables().add(composite);

        Monitor monitor = deployer.createMonitor();
        builtComposite = deployer.build(contributions, dependedOnContributions, new HashMap<QName, List<String>>(), monitor);
        // TODO: Ideally deployer.build would set the name and uri to what this needs
        builtComposite.setName(composite.getName());
        builtComposite.setURI(composite.getURI());
        builtComposite.setContributionURI(composite.getContributionURI());
        monitor.analyzeProblems();

        compositeContext = new CompositeContext(extensionPointRegistry, 
                                                domainRegistry, 
                                                builtComposite, 
                                                null, // nothing appears to use the domain name in CompositeContext 
                                                null, // don't need node uri
                                                deployer.getSystemDefinitions());
    }

    public void start() throws ActivationException {
        try {
            compositeActivator.activate(compositeContext, builtComposite);
            compositeActivator.start(compositeContext, builtComposite);
            domainRegistry.addRunningComposite(contribution.getURI(), builtComposite);
        } catch (ActivationException ex){
            stop();
            throw ex;
        } catch (Exception ex){
            stop();
            throw new ActivationException(ex);
        }
    }

    public void stop() throws ActivationException {
        domainRegistry.removeRunningComposite(contribution.getURI(), builtComposite.getURI());
        compositeActivator.stop(compositeContext, builtComposite);
        compositeActivator.deactivate(builtComposite);
    }
    
    public String getURI() {
        return composite.getURI();
    }
}
