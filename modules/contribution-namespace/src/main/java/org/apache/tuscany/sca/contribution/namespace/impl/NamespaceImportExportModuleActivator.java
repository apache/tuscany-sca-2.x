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

package org.apache.tuscany.sca.contribution.namespace.impl;

import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.ConstrainingType;
import org.apache.tuscany.sca.contribution.namespace.NamespaceImportExportFactory;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.contribution.resolver.ModelResolverExtensionPoint;
import org.apache.tuscany.sca.contribution.service.ContributionListenerExtensionPoint;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.ModuleActivator;

/**
 * Namespace Import/Export module activator
 * Responsible for registering all extension points provide by the module such as :
 *    - Import/Export processors
 *    - Composite and ConstrainingType model resolvers
 *    - Import/Export listeners
 * 
 * @version $Rev$ $Date$
 */
public class NamespaceImportExportModuleActivator implements ModuleActivator {
    private static final NamespaceImportExportFactory factory = new NamespaceImportExportFactoryImpl();
    
    /**
     * Artifact processors for <import.java>
     */
    private NamespaceImportProcessor importProcessor;

    /**
     * Artifact processors for <export.java>
     */
    private NamespaceExportProcessor exportProcessor;
        
    /**
     * Java Import/Export listener
     */
    private NamespaceImportExportListener listener;

    public void start(ExtensionPointRegistry registry) {
        importProcessor = new NamespaceImportProcessor(factory);
        exportProcessor = new NamespaceExportProcessor(factory);

        //register artifact processors for java import/export
        StAXArtifactProcessorExtensionPoint processors = registry.getExtensionPoint(StAXArtifactProcessorExtensionPoint.class);
        processors.addArtifactProcessor(importProcessor);
        processors.addArtifactProcessor(exportProcessor);
        
        //register contribution listener responsible for initializing import/export model resolvers
        ContributionListenerExtensionPoint listeners = registry.getExtensionPoint(ContributionListenerExtensionPoint.class);
        listeners.addContributionListener(new NamespaceImportExportListener());
    }

    public void stop(ExtensionPointRegistry registry) {
        //unregister artifact processors
        StAXArtifactProcessorExtensionPoint processors = registry.getExtensionPoint(StAXArtifactProcessorExtensionPoint.class);
        processors.removeArtifactProcessor(importProcessor);
        processors.removeArtifactProcessor(exportProcessor);
        
        //unregister artifact model resolvers
        ModelResolverExtensionPoint resolvers = registry.getExtensionPoint(ModelResolverExtensionPoint.class);
        resolvers.removeResolver(Composite.class);
        resolvers.removeResolver(ConstrainingType.class);
        
        //unregister contribution listener
        ContributionListenerExtensionPoint listeners = registry.getExtensionPoint(ContributionListenerExtensionPoint.class);
        listeners.removeContributionListener(listener);
    }

}
