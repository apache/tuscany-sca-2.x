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

package org.apache.tuscany.sca.spi.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.assembly.Implementation;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.ModelFactoryExtensionPoint;
import org.apache.tuscany.sca.core.ModuleActivator;
import org.apache.tuscany.sca.databinding.DataBindingExtensionPoint;
import org.apache.tuscany.sca.databinding.TransformerExtensionPoint;
import org.apache.tuscany.sca.databinding.impl.MediatorImpl;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.Message;
import org.apache.tuscany.sca.provider.BindingProviderFactory;
import org.apache.tuscany.sca.provider.ImplementationProvider;
import org.apache.tuscany.sca.provider.ImplementationProviderFactory;
import org.apache.tuscany.sca.provider.ProviderFactory;
import org.apache.tuscany.sca.provider.ProviderFactoryExtensionPoint;
import org.apache.tuscany.sca.provider.ReferenceBindingProvider;
import org.apache.tuscany.sca.provider.ServiceBindingProvider;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentReference;
import org.apache.tuscany.sca.runtime.RuntimeComponentService;
import org.apache.tuscany.sca.spi.BindingActivator;
import org.apache.tuscany.sca.spi.ImplementationActivator;
import org.apache.tuscany.sca.spi.InvokerFactory;
import org.apache.tuscany.sca.spi.utils.PropertyValueObjectFactory;
import org.apache.tuscany.sca.spi.utils.DefaultPropertyValueObjectFactory;

public class UtilsModuleActivator implements ModuleActivator {

    List<BindingActivator> bindingActivators;
    List<ImplementationActivator> implementationActivators;
    private AssemblyFactory assemblyFactory;
    
    public void start(ExtensionPointRegistry registry) {

        ModelFactoryExtensionPoint factories = registry.getExtensionPoint(ModelFactoryExtensionPoint.class);
        this.assemblyFactory = factories.getFactory(AssemblyFactory.class);

        DataBindingExtensionPoint dataBindings = registry.getExtensionPoint(DataBindingExtensionPoint.class);
        TransformerExtensionPoint transformers = registry.getExtensionPoint(TransformerExtensionPoint.class); 
        MediatorImpl mediator = new MediatorImpl(dataBindings, transformers);
        PropertyValueObjectFactory propertyFactory = new DefaultPropertyValueObjectFactory(mediator);
        registry.addExtensionPoint(propertyFactory);

        startBindings(registry);
        startImplementations(registry);
    }

    public void stop(ExtensionPointRegistry registry) {
        stopBindings(registry);
        stopImplementations(registry);
    }

    @SuppressWarnings("unchecked")
    public void startBindings(ExtensionPointRegistry registry) {

        this.bindingActivators = DiscoveryUtils.discoverActivators(BindingActivator.class, getClass().getClassLoader(), registry);

        StAXArtifactProcessorExtensionPoint staxProcessors = registry.getExtensionPoint(StAXArtifactProcessorExtensionPoint.class);

        for (final BindingActivator bindingActivator : bindingActivators) {

            staxProcessors.addArtifactProcessor(bindingActivator.getSCDLProcessor());

            if (bindingActivator.getModelType() != null) {
                // Add a ProviderFactory
                ProviderFactoryExtensionPoint providerFactories =
                    registry.getExtensionPoint(ProviderFactoryExtensionPoint.class);

                providerFactories.addProviderFactory(new BindingProviderFactory() {
                    public ReferenceBindingProvider createReferenceBindingProvider(RuntimeComponent rc,
                                                                                   RuntimeComponentReference rcr,
                                                                                   Binding b) {
                        return bindingActivator.createReferenceBindingProvider(rc, rcr, b);
                    }

                    public ServiceBindingProvider createServiceBindingProvider(RuntimeComponent rc,
                                                                               RuntimeComponentService rcs,
                                                                               Binding b) {
                        return bindingActivator.createServiceBindingProvider(rc, rcs, b);
                    }

                    public Class getModelType() {
                        return bindingActivator.getModelType();
                    }
                });
            }
        }
    }

    public void stopBindings(ExtensionPointRegistry registry) {
        StAXArtifactProcessorExtensionPoint staxProcessors = registry.getExtensionPoint(StAXArtifactProcessorExtensionPoint.class);
        ProviderFactoryExtensionPoint providerFactories = registry.getExtensionPoint(ProviderFactoryExtensionPoint.class);

        for (final BindingActivator bindingActivator : bindingActivators) {

            // Remove the binding SCDL processor from the runtime
            if (staxProcessors != null && bindingActivator.getSCDLProcessor() != null) {
                StAXArtifactProcessor processor = staxProcessors.getProcessor(bindingActivator.getSCDLProcessor().getClass());
                if (processor != null) {
                    staxProcessors.removeArtifactProcessor(processor);
                }
            }

            // Remove the ProviderFactory from the runtime
            if (providerFactories != null && bindingActivator.getModelType() != null) {
                ProviderFactory factory = providerFactories.getProviderFactory(bindingActivator.getModelType());
                if (factory != null) {
                    providerFactories.removeProviderFactory(factory);
                }
            }
        }
    }

    protected void startImplementations(ExtensionPointRegistry registry) {
        this.implementationActivators = DiscoveryUtils.discoverActivators(ImplementationActivator.class, getClass().getClassLoader(), registry);

        StAXArtifactProcessorExtensionPoint staxProcessors = registry.getExtensionPoint(StAXArtifactProcessorExtensionPoint.class);

        for (final ImplementationActivator implementationActivator : implementationActivators) {

            SCDLProcessor scdlProcessor = new SCDLProcessor(assemblyFactory, implementationActivator.getSCDLQName(), implementationActivator.getImplementationClass());
            staxProcessors.addArtifactProcessor(scdlProcessor);

            ProviderFactoryExtensionPoint providerFactories = registry.getExtensionPoint(ProviderFactoryExtensionPoint.class);

            if (implementationActivator.getImplementationClass() != null && providerFactories != null) {

                providerFactories.addProviderFactory(new ImplementationProviderFactory() {
                    public ImplementationProvider createImplementationProvider(final RuntimeComponent rc, final Implementation impl) {
                        return new ImplementationProvider() {
                            List<DelegaterInvoker> invokers = new ArrayList<DelegaterInvoker>();
                            public Invoker createInvoker(RuntimeComponentService arg0, final Operation op) {
                                DelegaterInvoker invoker = new DelegaterInvoker();
                                invoker.op = op;
                                invokers.add(invoker);    
                                return invoker;
                            }
                            public Invoker createCallbackInvoker(Operation arg0) {
                                throw new RuntimeException("TODO: callbacks not yet implemented"); 
                            }
                            public void start() {
                                InvokerFactory factory = implementationActivator.createInvokerFactory(rc, impl);
                                for (DelegaterInvoker invoker : invokers) {
                                    invoker.start(factory);
                                }
                            }
                            public void stop() {
                            }
                            class DelegaterInvoker implements Invoker {
                                Invoker invoker;
                                Operation op;
                                public Message invoke(Message arg0) {
                                    return invoker.invoke(arg0);
                                }
                                public void start(InvokerFactory factory) {
                                    invoker = factory.createInvoker(op);
                                }
                             }
                            };
                    }
                    public Class getModelType() {
                        return implementationActivator.getImplementationClass();
                    }
                });
            }
        }
    }

    protected void stopImplementations(ExtensionPointRegistry registry) {
        StAXArtifactProcessorExtensionPoint staxProcessors = registry.getExtensionPoint(StAXArtifactProcessorExtensionPoint.class);

        for (final ImplementationActivator implementationActivator : implementationActivators) {
            if (staxProcessors != null && implementationActivator.getSCDLQName() != null) {
                StAXArtifactProcessor processor = staxProcessors.getProcessor(implementationActivator.getSCDLQName());
                if (processor != null) {
                    staxProcessors.removeArtifactProcessor(processor);
                }
            }
        }
    }

    public Object[] getExtensionPoints() {
        return null; // not used by binding or implementation extensions
    }

}
