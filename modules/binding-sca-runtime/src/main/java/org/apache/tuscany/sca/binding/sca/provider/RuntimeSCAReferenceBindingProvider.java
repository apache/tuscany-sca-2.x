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

package org.apache.tuscany.sca.binding.sca.provider;

import java.net.URI;
import java.util.logging.Logger;

import org.apache.tuscany.sca.assembly.DistributedSCABinding;
import org.apache.tuscany.sca.assembly.Endpoint;
import org.apache.tuscany.sca.assembly.EndpointReference;
import org.apache.tuscany.sca.assembly.OptimizableBinding;
import org.apache.tuscany.sca.assembly.SCABinding;
import org.apache.tuscany.sca.assembly.SCABindingFactory;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.InvocationChain;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.provider.BindingProviderFactory;
import org.apache.tuscany.sca.provider.ProviderFactoryExtensionPoint;
import org.apache.tuscany.sca.provider.ReferenceBindingProvider;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentReference;
import org.apache.tuscany.sca.runtime.RuntimeComponentService;
import org.apache.tuscany.sca.runtime.RuntimeWire;
import org.oasisopen.sca.ServiceUnavailableException;

/**
 * The sca reference binding provider mediates between the twin requirements of
 * local sca bindings and remote sca bindings. In the local case is does
 * very little. When the sca binding model is set as being remote (because a
 * reference target can't be resolved in the current model) this binding will
 * try and create a remote connection to it
 *
 * @version $Rev$ $Date$
 */
public class RuntimeSCAReferenceBindingProvider implements ReferenceBindingProvider {

    private static final Logger logger = Logger.getLogger(RuntimeSCAReferenceBindingProvider.class.getName());

    private RuntimeComponent component;
    private RuntimeComponentReference reference;
    private SCABinding binding;
    private boolean started = false;

    private BindingProviderFactory<DistributedSCABinding> distributedProviderFactory = null;
    private ReferenceBindingProvider distributedProvider = null;
    private SCABindingFactory scaBindingFactory;

    public RuntimeSCAReferenceBindingProvider(ExtensionPointRegistry extensionPoints,
                                              RuntimeComponent component,
                                              RuntimeComponentReference reference,
                                              SCABinding binding) {
        this.component = component;
        this.reference = reference;
        this.binding = binding;
        this.scaBindingFactory =
            extensionPoints.getExtensionPoint(FactoryExtensionPoint.class).getFactory(SCABindingFactory.class);

        // look to see if a distributed SCA binding implementation has
        // been included on the classpath. This will be needed by the
        // provider itself to do it's thing
        ProviderFactoryExtensionPoint factoryExtensionPoint =
            extensionPoints.getExtensionPoint(ProviderFactoryExtensionPoint.class);
        distributedProviderFactory =
            (BindingProviderFactory<DistributedSCABinding>)factoryExtensionPoint
                .getProviderFactory(DistributedSCABinding.class);

    }

    public boolean isTargetRemote() {
        boolean targetIsRemote = false;

        // first look at the target service and see if this has been resolved
        OptimizableBinding optimizableBinding = (OptimizableBinding)binding;

        // The decision is based on the results of the wiring process in the assembly model
        // The SCA binding is used to represent unresolved reference targets, i.e. those
        // reference targets that need resolving at run time. We can tell by lookin if the
        // service to which this binding refers is resolved or not.
        //
        // TODO - When a callback is in operation. A callback reference bindings sometimes has to
        //        act as though there is a local wire and sometimes as if there is a remote wire
        //        what are the implications of this here?

        if (RemoteBindingHelper.isTargetRemote()) {
            if (reference.getInterfaceContract() != null) {
                targetIsRemote = reference.getInterfaceContract().getInterface().isRemotable();
            } else {
                targetIsRemote = true;
            }
        } else if (optimizableBinding.getTargetComponentService() != null) {
            if (optimizableBinding.getTargetComponentService().isUnresolved() == true) {
                targetIsRemote = true;
            } else {
                targetIsRemote = false;
            }
        } else {
            // the case where the wire is specified by URI, e.g. callbacks or user specified bindings, and
            // look at the provided URI to decide whether it is a local or remote case
            try {
                if (binding.getURI() != null) {
                    URI uri = new URI(binding.getURI());
                    if (uri.isAbsolute()) {
                        targetIsRemote = true;
                    } else {
                        targetIsRemote = false;
                    }
                } else {
                    targetIsRemote = false;
                }
            } catch (Exception ex) {
                targetIsRemote = false;
            }
        }
        return targetIsRemote;
    }

    private ReferenceBindingProvider getDistributedProvider() {

        if (isTargetRemote()) {
            // initialize the remote provider if it hasn't been done already
            if (distributedProvider == null) {
                if (reference.getInterfaceContract() != null && !reference.getInterfaceContract().getInterface().isRemotable()) {
                    throw new IllegalStateException("Reference interface not remotable for component: " + component
                        .getName()
                        + " and reference: "
                        + reference.getName());
                }

                if (distributedProviderFactory == null) {
                    throw new IllegalStateException("No distributed SCA binding available for component: " + component
                        .getName()
                        + " and reference: "
                        + reference.getName());
                }

                // create the remote provider
                DistributedSCABinding distributedBinding = scaBindingFactory.createDistributedSCABinding();
                distributedBinding.setSCABinding(binding);

                distributedProvider =
                    distributedProviderFactory.createReferenceBindingProvider(component, reference, distributedBinding);
            }
        }

        return distributedProvider;
    }

    public InterfaceContract getBindingInterfaceContract() {
        if (isTargetRemote()) {
            return getDistributedProvider().getBindingInterfaceContract();
        } else {
            if (reference.getReference() != null) {
                return reference.getReference().getInterfaceContract();
            } else {
                return reference.getInterfaceContract();
            }
        }
    }

    public boolean supportsOneWayInvocation() {
        if (isTargetRemote()) {
            return getDistributedProvider().supportsOneWayInvocation();
        } else {
            return false;
        }
    }

    private Invoker getInvoker(RuntimeWire wire, Operation operation) {
        Endpoint target = wire.getEndpoint();
        if (target != null) {
            RuntimeComponentService service = (RuntimeComponentService)target.getService();
            if (service != null) { // not a callback wire
                SCABinding scaBinding = service.getBinding(SCABinding.class);
                InvocationChain chain =
                    service.getInvocationChain(scaBinding, wire.getEndpointReference().getInterfaceContract(), operation);
                return chain == null ? null : new SCABindingInvoker(chain);
            }
        }
        return null;
    }

    public Invoker createInvoker(Operation operation) {
        if (isTargetRemote()) {
            return getDistributedProvider().createInvoker(operation);
        } else {
            RuntimeWire wire = reference.getRuntimeWire(binding);
            Invoker invoker = getInvoker(wire, operation);
            if (invoker == null) {
                throw new ServiceUnavailableException("Unable to create SCA binding invoker for local target " + component.getName()
                    + " reference "
                    + reference.getName()
                    + " (bindingURI="
                    + binding.getURI()
                    + " operation="
                    + operation.getName()
                    + ")" );
            }
            return invoker;
        }
    }

    public void start() {
        if (started) {
            return;
        } else {
            started = true;
        }

        if (getDistributedProvider() != null) {
            distributedProvider.start();
        }
    }

    public void stop() {
        if (!started) {
            return;
        } else {
            started = false;
        }

        if (getDistributedProvider() != null) {
            distributedProvider.stop();
        }
    }

}
