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

package org.apache.tuscany.sca.node.impl;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.contribution.ContributionFactory;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.ModuleActivator;
import org.apache.tuscany.sca.core.assembly.ActivationException;
import org.apache.tuscany.sca.core.scope.CompositeScopeContainerFactory;
import org.apache.tuscany.sca.core.scope.ConversationalScopeContainerFactory;
import org.apache.tuscany.sca.core.scope.RequestScopeContainerFactory;
import org.apache.tuscany.sca.core.scope.ScopeContainerFactory;
import org.apache.tuscany.sca.core.scope.ScopeRegistry;
import org.apache.tuscany.sca.core.scope.ScopeRegistryImpl;
import org.apache.tuscany.sca.core.scope.StatelessScopeContainerFactory;
import org.apache.tuscany.sca.extensibility.ServiceDeclaration;
import org.apache.tuscany.sca.extensibility.ServiceDiscovery;
import org.apache.tuscany.sca.node.SCAContribution;

/**
 * NodeUtil
 *
 * @version $Rev: $ $Date: $
 */
public class NodeUtil {
    private static final Logger logger = Logger.getLogger(NodeImpl.class.getName());

    static Contribution contribution(ContributionFactory contributionFactory, SCAContribution c) {
        Contribution contribution = contributionFactory.createContribution();
        contribution.setURI(c.getURI());
        contribution.setLocation(c.getLocation());
        contribution.setUnresolved(true);
        return contribution;
    }

    /**
     * Escape the space in URL string
     * @param uri
     * @return
     */
    static URI createURI(String uri) {
        if (uri.indexOf(' ') != -1) {
            uri = uri.replace(" ", "%20");
        }
        return URI.create(uri);
    }

    static List<ModuleActivator> loadModules(ExtensionPointRegistry registry) throws ActivationException {

        // Load and instantiate the modules found on the classpath (or any registered ClassLoaders)
        List<ModuleActivator> modules = new ArrayList<ModuleActivator>();
        try {
            Set<ServiceDeclaration> moduleActivators = ServiceDiscovery.getInstance().getServiceDeclarations(ModuleActivator.class.getName());
            Set<String> moduleClasses = new HashSet<String>();
            for (ServiceDeclaration moduleDeclarator : moduleActivators) {
                if (moduleClasses.contains(moduleDeclarator.getClassName())) {
                    continue;
                }
                moduleClasses.add(moduleDeclarator.getClassName());
                Class<?> moduleClass = moduleDeclarator.loadClass();
                ModuleActivator module = (ModuleActivator)moduleClass.newInstance();
                modules.add(module);
            }
        } catch (IOException e) {
            throw new ActivationException(e);
        } catch (ClassNotFoundException e) {
            throw new ActivationException(e);
        } catch (InstantiationException e) {
            throw new ActivationException(e);
        } catch (IllegalAccessException e) {
            throw new ActivationException(e);
        }

        return modules;
    }

    static void startModules(ExtensionPointRegistry registry, List<ModuleActivator> modules) throws ActivationException {
        boolean debug = logger.isLoggable(Level.FINE);
        
        // Start all the extension modules
        for (ModuleActivator module : modules) {
            long start = 0L;
            if (debug) {
                logger.fine(module.getClass().getName() + " is starting.");
                start = System.currentTimeMillis();
            }
            try {
                module.start(registry);
                if (debug) {
                    long end = System.currentTimeMillis();
                    logger.fine(module.getClass().getName() + " is started in " + (end - start) + " ms.");
                }
            } catch (Throwable e) {
                logger.log(Level.WARNING, "Exception starting module " + module.getClass().getName()
                    + " :"
                    + e.getMessage());
                logger.log(Level.FINE, "Exception starting module " + module.getClass().getName(), e);
            }
        }
    }

    static void stopModules(final ExtensionPointRegistry registry, List<ModuleActivator> modules) {
        boolean debug = logger.isLoggable(Level.FINE);
        for (ModuleActivator module : modules) {
            long start = 0L;
            if (debug) {
                logger.fine(module.getClass().getName() + " is stopping.");
                start = System.currentTimeMillis();
            }
            module.stop(registry);
            if (debug) {
                long end = System.currentTimeMillis();
                logger.fine(module.getClass().getName() + " is stopped in " + (end - start) + " ms.");
            }
        }
    }

//    private void loadSCADefinitions() throws ActivationException {
//        try {
//            URLArtifactProcessorExtensionPoint documentProcessors =
//                registry.getExtensionPoint(URLArtifactProcessorExtensionPoint.class);
//            URLArtifactProcessor<SCADefinitions> definitionsProcessor =
//                documentProcessors.getProcessor(SCADefinitions.class);
//            SCADefinitionsProviderExtensionPoint scaDefnProviders =
//                registry.getExtensionPoint(SCADefinitionsProviderExtensionPoint.class);
//
//            SCADefinitions systemSCADefinitions = new SCADefinitionsImpl();
//            SCADefinitions aSCADefn = null;
//            for (SCADefinitionsProvider aProvider : scaDefnProviders.getSCADefinitionsProviders()) {
//                aSCADefn = aProvider.getSCADefinition();
//                SCADefinitionsUtil.aggregateSCADefinitions(aSCADefn, systemSCADefinitions);
//            }
//
//            policyDefinitions.add(systemSCADefinitions);
//
//            //we cannot expect that providers will add the intents and policysets into the resolver
//            //so we do this here explicitly
//            for (Intent intent : systemSCADefinitions.getPolicyIntents()) {
//                policyDefinitionsResolver.addModel(intent);
//            }
//
//            for (PolicySet policySet : systemSCADefinitions.getPolicySets()) {
//                policyDefinitionsResolver.addModel(policySet);
//            }
//
//            for (IntentAttachPointType attachPoinType : systemSCADefinitions.getBindingTypes()) {
//                policyDefinitionsResolver.addModel(attachPoinType);
//            }
//
//            for (IntentAttachPointType attachPoinType : systemSCADefinitions.getImplementationTypes()) {
//                policyDefinitionsResolver.addModel(attachPoinType);
//            }
//
//            //now that all system sca definitions have been read, lets resolve them right away
//            definitionsProcessor.resolve(systemSCADefinitions, policyDefinitionsResolver);
//            
//        } catch (Exception e) {
//            throw new ActivationException(e);
//        }
//    }

}
