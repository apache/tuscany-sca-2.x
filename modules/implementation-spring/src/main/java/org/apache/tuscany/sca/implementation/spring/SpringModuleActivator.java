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

package org.apache.tuscany.sca.implementation.spring;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.DefaultAssemblyFactory;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.ModuleActivator;
import org.apache.tuscany.sca.core.invocation.ProxyFactoryExtensionPoint;
import org.apache.tuscany.sca.databinding.DataBindingExtensionPoint;
import org.apache.tuscany.sca.databinding.TransformerExtensionPoint;
import org.apache.tuscany.sca.databinding.impl.MediatorImpl;
import org.apache.tuscany.sca.implementation.java.context.JavaPropertyValueObjectFactory;
import org.apache.tuscany.sca.implementation.java.introspect.DefaultJavaClassIntrospectorExtensionPoint;
import org.apache.tuscany.sca.implementation.java.introspect.JavaClassIntrospectorExtensionPoint;
import org.apache.tuscany.sca.interfacedef.java.DefaultJavaInterfaceFactory;
import org.apache.tuscany.sca.interfacedef.java.JavaInterfaceFactory;
import org.apache.tuscany.sca.interfacedef.java.introspect.ExtensibleJavaInterfaceIntrospector;
import org.apache.tuscany.sca.interfacedef.java.introspect.JavaInterfaceIntrospector;
import org.apache.tuscany.sca.interfacedef.java.introspect.JavaInterfaceIntrospectorExtensionPoint;
import org.apache.tuscany.sca.policy.DefaultPolicyFactory;
import org.apache.tuscany.sca.policy.PolicyFactory;
import org.apache.tuscany.sca.provider.ProviderFactoryExtensionPoint;

/**
 * ModuleActivator class for the Spring module
 * - this class is invoked by the Tuscany SCA core when the Spring module is included in the
 * runtime package
 * - it registers a series of extension points which the core will use when dealing with
 * components which use <implementation.spring.../>
 * @version $Rev: 511195 $ $Date: 2007-02-24 02:29:46 +0000 (Sat, 24 Feb 2007) $ 
 */
public class SpringModuleActivator implements ModuleActivator {

    private SpringArtifactProcessor springArtifactProcessor;
    private AssemblyFactory assemblyFactory;
    private JavaInterfaceFactory javaFactory;
    private PolicyFactory policyFactory;
    private JavaInterfaceIntrospector interfaceIntrospector;
    private JavaClassIntrospectorExtensionPoint classVisitors;

    public SpringModuleActivator() {
        assemblyFactory = new DefaultAssemblyFactory();
        javaFactory = new DefaultJavaInterfaceFactory();
        policyFactory = new DefaultPolicyFactory();

    } // end constructor

    /*
     * start() is called when the Tuscany core starts the Spring module
     * 
     * It is in this method that the registration of the extension points takes place:
     * - SpringArtifactProcessor processes the XML for <implementation.spring.../>
     * - SpringComponentBuilder actually builds components based on <implementation.spring.../>
     * - SpringPropertyValueObjectFactory handles properties for implementation.spring components
     */
    public void start(ExtensionPointRegistry registry) {

        ProxyFactoryExtensionPoint proxyFactory = registry.getExtensionPoint(ProxyFactoryExtensionPoint.class);

        // TODO: could the runtime have a default PropertyValueObjectFactory in the registry
        DataBindingExtensionPoint dataBindings = registry.getExtensionPoint(DataBindingExtensionPoint.class);
        TransformerExtensionPoint transformers = registry.getExtensionPoint(TransformerExtensionPoint.class);
        MediatorImpl mediator = new MediatorImpl(dataBindings, transformers);
        JavaPropertyValueObjectFactory propertyFactory = new JavaPropertyValueObjectFactory(mediator);

        // Tools for Java interface handling
        JavaInterfaceIntrospectorExtensionPoint interfaceVisitors =
            registry.getExtensionPoint(JavaInterfaceIntrospectorExtensionPoint.class);
        interfaceIntrospector = new ExtensibleJavaInterfaceIntrospector(javaFactory, interfaceVisitors);

        // Create the artifact processor for Spring artifacts and add to artifact processors
        springArtifactProcessor =
            new SpringArtifactProcessor(assemblyFactory, javaFactory, interfaceIntrospector, policyFactory,
                                        propertyFactory);

        StAXArtifactProcessorExtensionPoint staxProcessors =
            registry.getExtensionPoint(StAXArtifactProcessorExtensionPoint.class);
        staxProcessors.addArtifactProcessor(springArtifactProcessor);

        // Create SpringImplementationFactory and add to provider factories 
        SpringImplementationProviderFactory springImplementationProviderFactory =
            new SpringImplementationProviderFactory(proxyFactory, propertyFactory);

        ProviderFactoryExtensionPoint providerFactories =
            registry.getExtensionPoint(ProviderFactoryExtensionPoint.class);
        providerFactories.addProviderFactory(springImplementationProviderFactory);
    }

    /*
     * stop() is called when the Tuscany core stops the Spring module
     * At present, no action is taken
     */
    public void stop(ExtensionPointRegistry registry) {
        StAXArtifactProcessorExtensionPoint staxProcessors =
            registry.getExtensionPoint(StAXArtifactProcessorExtensionPoint.class);
        staxProcessors.removeArtifactProcessor(springArtifactProcessor);
    }

    /*
     * Return a map of the extension points for the Spring module.
     * Not implemented at present (the core does not seem to require it)
     */
    public Object[] getExtensionPoints() {
        classVisitors = new DefaultJavaClassIntrospectorExtensionPoint();
        return new Object[] {classVisitors};
    }

}
