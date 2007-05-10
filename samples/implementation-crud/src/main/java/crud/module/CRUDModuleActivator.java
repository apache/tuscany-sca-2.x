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

package crud.module;

import java.util.Map;

import org.apache.tuscany.assembly.AssemblyFactory;
import org.apache.tuscany.contribution.processor.StAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.core.ExtensionPointRegistry;
import org.apache.tuscany.core.ModuleActivator;
import org.apache.tuscany.core.runtime.RuntimeAssemblyFactory;
import org.apache.tuscany.interfacedef.InterfaceContractMapper;
import org.apache.tuscany.interfacedef.java.DefaultJavaInterfaceFactory;
import org.apache.tuscany.interfacedef.java.JavaInterfaceFactory;
import org.apache.tuscany.interfacedef.java.introspect.ExtensibleJavaInterfaceIntrospector;
import org.apache.tuscany.interfacedef.java.introspect.JavaInterfaceIntrospector;
import org.apache.tuscany.interfacedef.java.introspect.JavaInterfaceIntrospectorExtensionPoint;
import org.apache.tuscany.invocation.ProxyFactory;
import org.apache.tuscany.provider.ProviderFactoryExtensionPoint;

import crud.CRUDImplementationFactory;
import crud.DefaultCRUDImplementationFactory;
import crud.impl.CRUDImplementationProcessor;
import crud.provider.CRUDImplementationProviderFactory;

/**
 * Implements a module activator for the CRUD implementation extension module.
 * The module activator is responsible for contributing the CRUD implementation
 * extensions and plugging them in the extension points defined by the Tuscany
 * runtime.
 * 
 * @version $Rev$ $Date$
 */
public class CRUDModuleActivator implements ModuleActivator {

    public Map<Class, Object> getExtensionPoints() {
        // This module extension does not contribute any new
        // extension point
        return null;
    }

    public void start(ExtensionPointRegistry registry) {

        ProxyFactory proxyFactory = registry.getExtensionPoint(ProxyFactory.class);
        InterfaceContractMapper mapper = registry.getExtensionPoint(InterfaceContractMapper.class);
        // Create the CRUD implementation factory
        AssemblyFactory assemblyFactory = new RuntimeAssemblyFactory(mapper, proxyFactory);

        JavaInterfaceFactory javaFactory = new DefaultJavaInterfaceFactory();
        JavaInterfaceIntrospectorExtensionPoint visitors = registry.getExtensionPoint(JavaInterfaceIntrospectorExtensionPoint.class);
        JavaInterfaceIntrospector introspector = new ExtensibleJavaInterfaceIntrospector(javaFactory, visitors);
        CRUDImplementationFactory crudFactory = new DefaultCRUDImplementationFactory(assemblyFactory, javaFactory, introspector);

        // Add the CRUD implementation extension to the StAXArtifactProcessor
        // extension point
        StAXArtifactProcessorExtensionPoint processors = registry.getExtensionPoint(StAXArtifactProcessorExtensionPoint.class);
        CRUDImplementationProcessor implementationArtifactProcessor = new CRUDImplementationProcessor(crudFactory);
        processors.addArtifactProcessor(implementationArtifactProcessor);
        
        // Add the CRUD provider factory to the ProviderFactory extension point
        ProviderFactoryExtensionPoint providerFactories = registry.getExtensionPoint(ProviderFactoryExtensionPoint.class);
        providerFactories.addProviderFactory(new CRUDImplementationProviderFactory());
    }

    public void stop(ExtensionPointRegistry registry) {
    }
}
