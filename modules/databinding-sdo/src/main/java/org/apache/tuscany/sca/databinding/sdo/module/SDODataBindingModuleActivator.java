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

package org.apache.tuscany.sca.databinding.sdo.module;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.contribution.ModelFactoryExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.ModuleActivator;
import org.apache.tuscany.sca.databinding.sdo.HelperContextProcessor;
import org.apache.tuscany.sca.databinding.sdo.HelperContextRegistry;
import org.apache.tuscany.sca.databinding.sdo.HelperContextRegistryImpl;
import org.apache.tuscany.sca.databinding.sdo.ImportSDOProcessor;
import org.apache.tuscany.sca.implementation.java.JavaImplementationFactory;

/**
 * @version $Rev$ $Date$
 */
public class SDODataBindingModuleActivator implements ModuleActivator {

    public void start(ExtensionPointRegistry registry) {
        ModelFactoryExtensionPoint factories = registry.getExtensionPoint(ModelFactoryExtensionPoint.class);
        AssemblyFactory assemblyFactory = factories.getFactory(AssemblyFactory.class);
        
        StAXArtifactProcessorExtensionPoint processors = registry.getExtensionPoint(StAXArtifactProcessorExtensionPoint.class);
        HelperContextRegistry contextRegistry = new HelperContextRegistryImpl();
        processors.addArtifactProcessor(new ImportSDOProcessor(contextRegistry));

        JavaImplementationFactory javaImplementationFactory = registry.getExtensionPoint(JavaImplementationFactory.class);
        javaImplementationFactory.addClassVisitor(new HelperContextProcessor(assemblyFactory, contextRegistry));

    }

    public void stop(ExtensionPointRegistry registry) {
    }

}
