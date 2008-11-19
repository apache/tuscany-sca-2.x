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
package org.apache.tuscany.sca.contribution.updater.impl;

import java.util.List;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.MetaComponent;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilder;
import org.apache.tuscany.sca.assembly.builder.impl.CompositeBuilderImpl;
import org.apache.tuscany.sca.contribution.Artifact;
import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.contribution.DeployedArtifact;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.contribution.service.ContributionService;
import org.apache.tuscany.sca.contribution.updater.CompositeUpdater;
import org.apache.tuscany.sca.contribution.updater.CompositeUpdaterException;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.assembly.CompositeActivator;
import org.apache.tuscany.sca.core.assembly.CompositeActivatorImpl;
import org.apache.tuscany.sca.contribution.updater.impl.ArtifactsFinder;
import org.apache.tuscany.sca.assembly.xml.MetaComponentProcessor;
import org.apache.tuscany.sca.runtime.RuntimeComponent;

public class CompositeUpdaterImpl implements CompositeUpdater {

    private CompositeBuilder compositeBuilder;
    private String compositeURI;
    private AssemblyFactory assemblyFactory;
    private String contribURI;
    private CompositeActivator compositeActivator;
    private ExtensionPointRegistry registry;
    private ContributionService contribService;

    public CompositeUpdaterImpl(AssemblyFactory assembly, String contribURI,
            String compositeURI, CompositeBuilder compositeBuilder,
            CompositeActivator compositeActivator,
            ExtensionPointRegistry registry, ContributionService contribService) {
        this.compositeBuilder = compositeBuilder;
        this.compositeURI = compositeURI;
        this.assemblyFactory = assembly;
        this.contribURI = contribURI;
        this.compositeActivator = compositeActivator;
        this.registry = registry;
        this.contribService = contribService;
    }

    public Component addComponent(MetaComponent c)
            throws CompositeUpdaterException {
        StAXArtifactProcessorExtensionPoint staxProcessors = registry
                .getExtensionPoint(StAXArtifactProcessorExtensionPoint.class);
        MetaComponentProcessor processor = (MetaComponentProcessor) staxProcessors
                .getProcessor(Component.class);
        Contribution contrib = contribService.getContribution(contribURI);
        List<DeployedArtifact> artifacts = contrib.getArtifacts();
        Composite composite = ArtifactsFinder.findComposite(compositeURI,
                artifacts);
        boolean found = false;

        if (composite == null)
            throw new CompositeUpdaterException(
                    "Composite not found in contribution" + contribURI);
        else {
            processor.setComposite(composite);
            try {
                processor.read(c.build());
            } catch (Exception e) {
                throw new CompositeUpdaterException(
                        "Component error parsing in contribution" + contribURI);

            }
            found = true;
        }

        if (found) {
            Component component = processor.getParsedComponent();
            Composite augmentedComposite = processor.getParsedComposite();
            try {
                processor.resolve(component, contrib.getModelResolver());
                compositeBuilder.attach(augmentedComposite, component);
                ((CompositeActivatorImpl) compositeActivator)
                        .activateComponent(component);
            } catch (Exception e) {

                throw new CompositeUpdaterException(
                        "Cannot activate the component");
            }

            return component;
        }
        return null;
    }

    public Component findComponent(String componentName) {
        Contribution contrib = contribService.getContribution(contribURI);
        List<Composite> artifacts = contrib.getDeployables();
        Composite composite = ArtifactsFinder.findComposite(compositeURI,
                artifacts);
        return ArtifactsFinder.findComponent(composite, componentName);
    }

    public Component removeComponent(String componentName)
            throws CompositeUpdaterException {
        Contribution contrib = contribService.getContribution(contribURI);
        List<DeployedArtifact> artifacts = contrib.getArtifacts();

        Composite composite = ArtifactsFinder.findComposite(compositeURI,
                artifacts);
        List<Component> components = composite.getComponents();
        Component toRemove = null;
        for (Component component : components) {
            if (((RuntimeComponent) component).getName().equals(componentName)) {
                toRemove = component;
                break;
            }
        }
        if (toRemove == null) {
            throw new CompositeUpdaterException(
                    "Component not found in contribution" + contribURI);
        } else {
            // start again
            try {
                composite.getComponents().remove(toRemove);
                CompositeActivatorImpl impl = (CompositeActivatorImpl) compositeActivator;
                impl.stop(toRemove);
                impl.deactivateComponent(toRemove);
                CompositeBuilderImpl builder = (CompositeBuilderImpl) compositeBuilder;
                builder.detach(composite, toRemove);
            } catch (Exception e) {
                throw new CompositeUpdaterException(
                        "Cannot remove composite from the contribution"
                                + contribURI);
            }
            return toRemove;
        }
    }

}
