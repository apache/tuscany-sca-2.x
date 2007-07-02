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

package org.apache.tuscany.sca.implementation.osgi.module;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.ModelFactoryExtensionPoint;
import org.apache.tuscany.sca.core.ModuleActivator;
import org.apache.tuscany.sca.databinding.DataBindingExtensionPoint;
import org.apache.tuscany.sca.databinding.TransformerExtensionPoint;
import org.apache.tuscany.sca.databinding.impl.MediatorImpl;
import org.apache.tuscany.sca.implementation.osgi.context.OSGiPropertyValueObjectFactory;
import org.apache.tuscany.sca.implementation.osgi.invocation.OSGiImplementationProviderFactory;
import org.apache.tuscany.sca.implementation.osgi.xml.OSGiImplementationProcessor;
import org.apache.tuscany.sca.interfacedef.java.DefaultJavaInterfaceFactory;
import org.apache.tuscany.sca.interfacedef.java.JavaInterfaceFactory;
import org.apache.tuscany.sca.interfacedef.java.introspect.ExtensibleJavaInterfaceIntrospector;
import org.apache.tuscany.sca.interfacedef.java.introspect.JavaInterfaceIntrospector;
import org.apache.tuscany.sca.interfacedef.java.introspect.JavaInterfaceIntrospectorExtensionPoint;
import org.apache.tuscany.sca.policy.PolicyFactory;
import org.apache.tuscany.sca.provider.ProviderFactoryExtensionPoint;

/**
 * Module activator for OSGi implementation type
 * 
 * Registers OSGiImplementationProcessor and OSGiImplementationProviderFactory
 */
public class OSGiModuleActivator implements ModuleActivator {

    public OSGiModuleActivator() {
    }

    public void start(ExtensionPointRegistry registry) {
        
        StAXArtifactProcessorExtensionPoint processors = registry.getExtensionPoint(StAXArtifactProcessorExtensionPoint.class);
        
        ModelFactoryExtensionPoint factories = registry.getExtensionPoint(ModelFactoryExtensionPoint.class);
        AssemblyFactory assemblyFactory = factories.getFactory(AssemblyFactory.class);
        PolicyFactory policyFactory = factories.getFactory(PolicyFactory.class);
        
        JavaInterfaceFactory javaInterfaceFactory = new DefaultJavaInterfaceFactory();
        JavaInterfaceIntrospectorExtensionPoint interfaceVisitors = 
            registry.getExtensionPoint(JavaInterfaceIntrospectorExtensionPoint.class);
        JavaInterfaceIntrospector interfaceIntrospector = 
            new ExtensibleJavaInterfaceIntrospector(javaInterfaceFactory, interfaceVisitors);

        OSGiImplementationProcessor implementationLoader = new OSGiImplementationProcessor(
                interfaceIntrospector, 
                javaInterfaceFactory, 
                assemblyFactory,
                policyFactory);
        
        processors.addArtifactProcessor(implementationLoader);
        
        DataBindingExtensionPoint dataBindings = registry.getExtensionPoint(DataBindingExtensionPoint.class);
        TransformerExtensionPoint transformers = registry.getExtensionPoint(TransformerExtensionPoint.class);
        MediatorImpl mediator =new MediatorImpl(dataBindings, transformers);
        OSGiPropertyValueObjectFactory factory = new OSGiPropertyValueObjectFactory(mediator);
        
        OSGiImplementationProviderFactory providerFactory =
            new OSGiImplementationProviderFactory(dataBindings, factory);
        
        ProviderFactoryExtensionPoint providerFactories = registry.getExtensionPoint(ProviderFactoryExtensionPoint.class);
        providerFactories.addProviderFactory(providerFactory);
        
       
    }

    public Object[] getExtensionPoints() {
        return null;
    }
    
    public void stop(ExtensionPointRegistry registry) {
    }
}
