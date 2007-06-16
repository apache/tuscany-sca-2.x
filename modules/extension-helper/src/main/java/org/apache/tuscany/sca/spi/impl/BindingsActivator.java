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

import javax.xml.namespace.QName;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.assembly.xml.Constants;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.ModelFactoryExtensionPoint;
import org.apache.tuscany.sca.core.ModuleActivator;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.Message;
import org.apache.tuscany.sca.provider.BindingProviderFactory;
import org.apache.tuscany.sca.provider.ProviderFactory;
import org.apache.tuscany.sca.provider.ProviderFactoryExtensionPoint;
import org.apache.tuscany.sca.provider.ReferenceBindingProvider;
import org.apache.tuscany.sca.provider.ServiceBindingProvider;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentReference;
import org.apache.tuscany.sca.runtime.RuntimeComponentService;
import org.apache.tuscany.sca.spi.BindingActivator;
import org.apache.tuscany.sca.spi.ComponentLifecycle;
import org.apache.tuscany.sca.spi.InvokerFactory;

public class BindingsActivator implements ModuleActivator {

    protected List<BindingActivator> bindingActivators;
    protected AssemblyFactory assemblyFactory;
    
    public void start(ExtensionPointRegistry registry) {

        ModelFactoryExtensionPoint factories = registry.getExtensionPoint(ModelFactoryExtensionPoint.class);
        this.assemblyFactory = factories.getFactory(AssemblyFactory.class);

        this.bindingActivators = DiscoveryUtils.discoverActivators(BindingActivator.class, getClass().getClassLoader(), registry);

        StAXArtifactProcessorExtensionPoint staxProcessors = registry.getExtensionPoint(StAXArtifactProcessorExtensionPoint.class);

        for (final BindingActivator bindingActivator : bindingActivators) {

            QName scdlQName = getBindingQName(bindingActivator.getBindingClass());
            staxProcessors.addArtifactProcessor(new BindingSCDLProcessor(scdlQName, bindingActivator.getBindingClass()));

            if (bindingActivator.getBindingClass() != null) {
                // Add a ProviderFactory
                ProviderFactoryExtensionPoint providerFactories =
                    registry.getExtensionPoint(ProviderFactoryExtensionPoint.class);

                providerFactories.addProviderFactory(new BindingProviderFactory() {
                    public ReferenceBindingProvider createReferenceBindingProvider(final RuntimeComponent rc,
                                                                                   final RuntimeComponentReference rcr,
                                                                                   final Binding b) {
                        return new ReferenceBindingProvider() {
                            List<InvokerProxy> invokers = new ArrayList<InvokerProxy>();
                            private InvokerFactory factory;
                            public Invoker createInvoker(Operation operation, boolean isCallback) {
                                InvokerProxy invoker = new InvokerProxy(operation);
                                invokers.add(invoker);    
                                return invoker;
                            }
                            public InterfaceContract getBindingInterfaceContract() {
                                return null;
                            }
                            public void start() {
                                factory = bindingActivator.createInvokerFactory(rc, rcr, b);
                                if (factory instanceof ComponentLifecycle) {
                                    ((ComponentLifecycle)factory).start();
                                }
                                for (InvokerProxy invoker : invokers) {
                                    invoker.start(factory);
                                }
                            }
                            public void stop() {
                                if (factory instanceof ComponentLifecycle) {
                                    ((ComponentLifecycle)factory).stop();
                                }
                            }};
                    }

                    public ServiceBindingProvider createServiceBindingProvider(final RuntimeComponent rc,
                                                                               final RuntimeComponentService rcs,
                                                                               final Binding b) {
                        return new ServiceBindingProvider(){
                            ComponentLifecycle listener = bindingActivator.createService(rc, rcs, b);
                            public InterfaceContract getBindingInterfaceContract() {
                                return null;
                            }
                            public void start() {
                                listener.start();
                            }
                            public void stop() {
                                listener.stop();
                            }};
                    }

                    public Class getModelType() {
                        return bindingActivator.getBindingClass();
                    }
                });
            }
        }
    }

    public void stop(ExtensionPointRegistry registry) {
        StAXArtifactProcessorExtensionPoint staxProcessors = registry.getExtensionPoint(StAXArtifactProcessorExtensionPoint.class);
        ProviderFactoryExtensionPoint providerFactories = registry.getExtensionPoint(ProviderFactoryExtensionPoint.class);

        for (final BindingActivator bindingActivator : bindingActivators) {

            // Remove the binding SCDL processor from the runtime
            if (staxProcessors != null) {
                StAXArtifactProcessor processor = staxProcessors.getProcessor(getBindingQName(bindingActivator.getBindingClass()));
                if (processor != null) {
                    staxProcessors.removeArtifactProcessor(processor);
                }
            }

            // Remove the ProviderFactory from the runtime
            if (providerFactories != null && bindingActivator.getBindingClass() != null) {
                ProviderFactory factory = providerFactories.getProviderFactory(bindingActivator.getBindingClass());
                if (factory != null) {
                    providerFactories.removeProviderFactory(factory);
                }
            }
        }
    }

    protected QName getBindingQName(Class bindingClass) {
        String localName = bindingClass.getName();
        if (localName.lastIndexOf('.') > -1) {
            localName = localName.substring(localName.lastIndexOf('.') + 1);
        }
        if (localName.endsWith("Binding")) {
            localName = localName.substring(0, localName.length()-7);
        }
        StringBuilder sb = new StringBuilder(localName);
        for (int i=0; i<sb.length(); i++) {
            if (Character.isUpperCase(sb.charAt(i))) {
                sb.setCharAt(i, Character.toLowerCase(sb.charAt(i)));
            } else {
                break;
            }
        }
        return new QName(Constants.SCA10_NS, "binding." + sb.toString());
    }


    public Object[] getExtensionPoints() {
        return null; // not used by binding or implementation extensions
    }

}
class InvokerProxy implements Invoker {
    Invoker invoker;
    Operation op;
    InvokerProxy(Operation op) {
        this.op = op;
    }
    public Message invoke(Message arg0) {
        return invoker.invoke(arg0);
    }
    public void start(InvokerFactory factory) {
        invoker = factory.createInvoker(op);
    }
 }

