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

package org.apache.tuscany.sca.core.assembly.impl;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.ComponentReference;
import org.apache.tuscany.sca.assembly.ComponentService;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.Endpoint;
import org.apache.tuscany.sca.assembly.EndpointReference;
import org.apache.tuscany.sca.assembly.Implementation;
import org.apache.tuscany.sca.assembly.Service;
import org.apache.tuscany.sca.context.CompositeContext;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.core.UtilityExtensionPoint;
import org.apache.tuscany.sca.core.scope.ScopeContainer;
import org.apache.tuscany.sca.core.scope.ScopeRegistry;
import org.apache.tuscany.sca.core.scope.ScopedRuntimeComponent;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.provider.ImplementationProvider;
import org.apache.tuscany.sca.provider.ImplementationProviderFactory;
import org.apache.tuscany.sca.provider.PolicyProvider;
import org.apache.tuscany.sca.provider.PolicyProviderFactory;
import org.apache.tuscany.sca.provider.ProviderFactoryExtensionPoint;
import org.apache.tuscany.sca.provider.ReferenceBindingProvider;
import org.apache.tuscany.sca.provider.ServiceBindingProvider;
import org.apache.tuscany.sca.runtime.ActivationException;
import org.apache.tuscany.sca.runtime.CompositeActivator;
import org.apache.tuscany.sca.runtime.EndpointRegistry;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentReference;
import org.apache.tuscany.sca.runtime.RuntimeComponentService;
import org.apache.tuscany.sca.runtime.RuntimeEndpoint;
import org.apache.tuscany.sca.runtime.RuntimeEndpointReference;

/**
 * @version $Rev$ $Date$
 */
public class CompositeActivatorImpl implements CompositeActivator {
    final Logger logger = Logger.getLogger(CompositeActivatorImpl.class.getName());

    private final AssemblyFactory assemblyFactory;
    private final ScopeRegistry scopeRegistry;
    private final ProviderFactoryExtensionPoint providerFactories;

    public CompositeActivatorImpl(ExtensionPointRegistry extensionPoints) {
        UtilityExtensionPoint utilities = extensionPoints.getExtensionPoint(UtilityExtensionPoint.class);
        FactoryExtensionPoint factories = extensionPoints.getExtensionPoint(FactoryExtensionPoint.class);
        this.assemblyFactory = factories.getFactory(AssemblyFactory.class);
        this.scopeRegistry = utilities.getUtility(ScopeRegistry.class);
        this.providerFactories = extensionPoints.getExtensionPoint(ProviderFactoryExtensionPoint.class);
    }

    //=========================================================================
    // Activation
    //=========================================================================

    // Composite activation/deactivation

    public void activate(CompositeContext compositeContext, Composite composite) throws ActivationException {
        try {
            if (logger.isLoggable(Level.FINE)) {
                logger.fine("Activating composite: " + composite.getName());
            }
            for (Component component : composite.getComponents()) {
                activateComponent(compositeContext, component);
            }
        } catch (Exception e) {
            throw new ActivationException(e);
        }
    }

    public void deactivate(Composite composite) throws ActivationException {
        try {
            if (logger.isLoggable(Level.FINE)) {
                logger.fine("Deactivating composite: " + composite.getName());
            }
            for (Component component : composite.getComponents()) {
                deactivateComponent(component);
            }
        } catch (Exception e) {
            throw new ActivationException(e);
        }
    }

    // Component activation/deactivation

    public void activateComponent(CompositeContext compositeContext, Component component)
            throws ActivationException {
        try {
            if (logger.isLoggable(Level.FINE)) {
                logger.fine("Activating component: " + component.getURI());
            }

            Implementation implementation = component.getImplementation();
            if (implementation instanceof Composite) {
                activate(compositeContext, (Composite) implementation);
            } else if (implementation != null) {
                addImplementationProvider((RuntimeComponent) component,
                        implementation);
                addScopeContainer(component);
            }

            for (ComponentService service : component.getServices()) {
                activate(compositeContext,
                        (RuntimeComponent) component, (RuntimeComponentService) service);
            }

            for (ComponentReference reference : component.getReferences()) {
                activate(compositeContext,
                        (RuntimeComponent) component, (RuntimeComponentReference) reference);
            }
        } catch (Exception e) {
            throw new ActivationException(e);
        }
    }

    public void deactivateComponent(Component component)
            throws ActivationException {
        try {
            if (logger.isLoggable(Level.FINE)) {
                logger.fine("Deactivating component: " + component.getURI());
            }
            for (ComponentService service : component.getServices()) {
                deactivate((RuntimeComponent) component,
                        (RuntimeComponentService) service);
            }

            for (ComponentReference reference : component.getReferences()) {
                deactivate((RuntimeComponent) component,
                        (RuntimeComponentReference) reference);
            }

            Implementation implementation = component.getImplementation();
            if (implementation instanceof Composite) {
                deactivate((Composite) implementation);
            } else if (implementation != null) {
                removeImplementationProvider((RuntimeComponent) component);
                removeScopeContainer(component);
            }
        } catch (Exception e) {
            throw new ActivationException(e);
        }
    }

    // add/remove artifacts required to get the implementation going

    private void addImplementationProvider(RuntimeComponent component, Implementation implementation) {
        ImplementationProviderFactory providerFactory =
            (ImplementationProviderFactory)providerFactories.getProviderFactory(implementation.getClass());
        if (providerFactory != null) {
            @SuppressWarnings("unchecked")
            ImplementationProvider implementationProvider =
                providerFactory.createImplementationProvider(component, implementation);
            if (implementationProvider != null) {
                component.setImplementationProvider(implementationProvider);
            }
        } else {
            throw new IllegalStateException("Provider factory not found for class: " + implementation.getClass()
                .getName());
        }
        for (PolicyProviderFactory f : providerFactories.getPolicyProviderFactories()) {
            PolicyProvider policyProvider = f.createImplementationPolicyProvider(component);
            if (policyProvider != null) {
                component.addPolicyProvider(policyProvider);
            }
        }

    }

    private void removeImplementationProvider(RuntimeComponent component) {
        component.setImplementationProvider(null);
        component.getPolicyProviders().clear();
    }

    private void addScopeContainer(Component component) {
        if (!(component instanceof ScopedRuntimeComponent)) {
            return;
        }
        ScopedRuntimeComponent runtimeComponent = (ScopedRuntimeComponent)component;
        ScopeContainer scopeContainer = scopeRegistry.getScopeContainer(runtimeComponent);
        runtimeComponent.setScopeContainer(scopeContainer);
    }

    private void removeScopeContainer(Component component) {
        if (!(component instanceof ScopedRuntimeComponent)) {
            return;
        }
        ScopedRuntimeComponent runtimeComponent = (ScopedRuntimeComponent)component;
        ScopeContainer scopeContainer = runtimeComponent.getScopeContainer();
        runtimeComponent.setScopeContainer(null);
    }


    // Service activation/deactivation

    public void activate(CompositeContext compositeContext, RuntimeComponent component, RuntimeComponentService service) {
        if (service.getService() == null) {
            if (logger.isLoggable(Level.WARNING)) {
                logger.warning("Skipping component service not defined in the component type: " + component.getURI()
                    + "#"
                    + service.getName());
            }
            return;
        }

        if (logger.isLoggable(Level.FINE)) {
            logger.fine("Activating component service: " + component.getURI() + "#" + service.getName());
        }

        // Add a wire for each service Endpoint
        for ( Endpoint endpoint : service.getEndpoints()){
            RuntimeEndpoint ep = (RuntimeEndpoint) endpoint;
            ep.bind(compositeContext);

            // create the interface contract for the binding and service ends of the wire
            // that are created as forward only contracts
            // FIXME: [rfeng] We might need a better way to get the impl interface contract
            Service targetService = service.getService();
            if (targetService == null) {
                targetService = service;
            }
            // endpoint.setInterfaceContract(targetService.getInterfaceContract().makeUnidirectional(false));
        }
    }

    public void deactivate(RuntimeComponent component, RuntimeComponentService service) {
        if (logger.isLoggable(Level.FINE)) {
            logger.fine("Deactivating component service: " + component.getURI() + "#" + service.getName());
        }
        for(Endpoint ep: service.getEndpoints()) {
            if(ep instanceof RuntimeEndpoint) {
                ((RuntimeEndpoint) ep).unbind();
            }
        }
    }

    // Reference activation/deactivation

    public void activate(CompositeContext compositeContext, RuntimeComponent component, RuntimeComponentReference reference) {
        if (logger.isLoggable(Level.FINE)) {
            logger.fine("Activating component reference: " + component.getURI() + "#" + reference.getName());
        }

        // set the parent component onto the reference. It's used at start time when the
        // reference is asked to return it's runtime wires. If there are none the reference
        // asks the component context to start the reference which creates the wires
        reference.setComponent(component);
        for(EndpointReference epr: reference.getEndpointReferences()) {
            addReferenceWire(compositeContext, epr);
        }

        // TODO reference wires are added at component start for some reason
    }

    public void deactivate(RuntimeComponent component, RuntimeComponentReference reference) {
        if (logger.isLoggable(Level.FINE)) {
            logger.fine("Deactivating component reference: " + component.getURI() + "#" + reference.getName());
        }
        for(EndpointReference endpointReference: reference.getEndpointReferences()) {
            ((RuntimeEndpointReference) endpointReference).unbind();
        }
    }

    //=========================================================================
    // Start
    //=========================================================================

    // Composite start/stop

    public void start(CompositeContext compositeContext, Composite composite) {
        if (logger.isLoggable(Level.FINE)) {
            logger.fine("Starting composite: " + composite.getName());
        }
        for (Component component : composite.getComponents()) {
            start(compositeContext, component);
        }
    }

    public void stop(CompositeContext compositeContext, Composite composite) {
        if (logger.isLoggable(Level.FINE)) {
            logger.fine("Stopping composite: " + composite.getName());
        }
        for (final Component component : composite.getComponents()) {
            stop(compositeContext, component);
        }
    }

    // Component start/stop

    public void start(CompositeContext compositeContext, Component component) {
        if (logger.isLoggable(Level.FINE)) {
            logger.fine("Starting component: " + component.getURI());
        }
        RuntimeComponent runtimeComponent = ((RuntimeComponent)component);
        if(runtimeComponent.isStarted()) {
            return;
        }

        compositeContext.bindComponent(runtimeComponent);
        Implementation implementation = component.getImplementation();
        
        if (implementation instanceof Composite) {
            start(compositeContext, (Composite)implementation);
        } else {
            for (PolicyProvider policyProvider : runtimeComponent.getPolicyProviders()) {
                policyProvider.start();
            }
            ImplementationProvider implementationProvider = runtimeComponent.getImplementationProvider();
            if (implementationProvider != null) {
                implementationProvider.start();
            }
        }

        if (component instanceof ScopedRuntimeComponent) {
            ScopedRuntimeComponent scopedRuntimeComponent = (ScopedRuntimeComponent)component;
            if (scopedRuntimeComponent.getScopeContainer() != null) {
                scopedRuntimeComponent.getScopeContainer().start();
            }
        }
        // Reference bindings aren't started until the wire is first used

        for (ComponentService service : component.getServices()) {
            if (logger.isLoggable(Level.FINE)) {
                logger.fine("Starting component service: " + component.getURI() + "#" + service.getName());
            }
            for (Endpoint endpoint : service.getEndpoints()) {
                RuntimeEndpoint ep = (RuntimeEndpoint) endpoint;
                // FIXME: Should the policy providers be started before the endpoint is started?
                for (PolicyProvider policyProvider : ep.getPolicyProviders()) {
                    policyProvider.start();
                }

                final ServiceBindingProvider bindingProvider = ep.getBindingProvider();
                if (bindingProvider != null) {
                    // bindingProvider.start();
                    // Allow bindings to add shutdown hooks. Requires RuntimePermission shutdownHooks in policy.
                    AccessController.doPrivileged(new PrivilegedAction<Object>() {
                        public Object run() {
                            bindingProvider.start();
                            return null;
                          }
                    });
                    compositeContext.getEndpointRegistry().addEndpoint(endpoint);
                }
            }
        }

        runtimeComponent.setStarted(true);
    }

    public void stop(CompositeContext compositeContext, Component component) {
        if (!((RuntimeComponent)component).isStarted()) {
            return;
        }
        if (logger.isLoggable(Level.FINE)) {
            logger.fine("Stopping component: " + component.getURI());
        }
        for (ComponentService service : component.getServices()) {
            if (logger.isLoggable(Level.FINE)) {
                logger.fine("Stopping component service: " + component.getURI() + "#" + service.getName());
            }
            for (Endpoint endpoint : service.getEndpoints()) {
                RuntimeEndpoint ep = (RuntimeEndpoint) endpoint;
                compositeContext.getEndpointRegistry().removeEndpoint(endpoint);
                final ServiceBindingProvider bindingProvider = ep.getBindingProvider();
                if (bindingProvider != null) {
                    // Allow bindings to read properties. Requires PropertyPermission read in security policy.
                    AccessController.doPrivileged(new PrivilegedAction<Object>() {
                        public Object run() {
                            bindingProvider.stop();
                            return null;
                          }
                    });
                }
                for (PolicyProvider policyProvider : ep.getPolicyProviders()) {
                    policyProvider.stop();
                }
            }
        }
        for (ComponentReference reference : component.getReferences()) {
            if (logger.isLoggable(Level.FINE)) {
                logger.fine("Stopping component reference: " + component.getURI() + "#" + reference.getName());
            }

            for (EndpointReference endpointReference : reference.getEndpointReferences()) {
                RuntimeEndpointReference epr = (RuntimeEndpointReference) endpointReference;
                compositeContext.getEndpointRegistry().removeEndpointReference(endpointReference);
                final ReferenceBindingProvider bindingProvider = epr.getBindingProvider();
                if (bindingProvider != null) {
                    // Allow bindings to read properties. Requires PropertyPermission read in security policy.
                    AccessController.doPrivileged(new PrivilegedAction<Object>() {
                        public Object run() {
                            bindingProvider.stop();
                            return null;
                          }
                    });
                }
                for (PolicyProvider policyProvider : epr.getPolicyProviders()) {
                    policyProvider.stop();
                }

            }
        }
        Implementation implementation = component.getImplementation();
        if (implementation instanceof Composite) {
            stop(compositeContext, (Composite)implementation);
        } else {
            final ImplementationProvider implementationProvider = ((RuntimeComponent)component).getImplementationProvider();
            if (implementationProvider != null) {
                // Allow bindings to read properties. Requires PropertyPermission read in security policy.
                AccessController.doPrivileged(new PrivilegedAction<Object>() {
                    public Object run() {
                        implementationProvider.stop();
                        return null;
                      }
                });
            }
            for (PolicyProvider policyProvider : ((RuntimeComponent)component).getPolicyProviders()) {
                policyProvider.stop();
            }
        }

        if (component instanceof ScopedRuntimeComponent) {
            ScopedRuntimeComponent runtimeComponent = (ScopedRuntimeComponent)component;
            if (runtimeComponent.getScopeContainer() != null &&
                    runtimeComponent.getScopeContainer().getLifecycleState() != ScopeContainer.STOPPED) {
                runtimeComponent.getScopeContainer().stop();
            }
        }

        ((RuntimeComponent)component).setStarted(false);
    }

    // Service start/stop

    // done as part of the component start above

    // Reference start/stop
    // Used by component context start

    public void start(CompositeContext compositeContext, RuntimeComponent component, RuntimeComponentReference componentReference) {
        synchronized (componentReference) {

            if (!(componentReference instanceof RuntimeComponentReference)) {
                return;
            }

            // create a wire for each endpoint reference. An endpoint reference says either that
            // - a target has been specified and hence the reference has been wired in some way.
            // - an unwired binding ha been specified
            // and endpoint reference representing a wired reference may not at this point
            // be resolved (the service to which it points may not be present in the
            // current composite). Endpoint reference resolution takes place when the wire
            // is first used (when the chains are created)
            for (EndpointReference endpointReference : componentReference.getEndpointReferences()){
                // addReferenceWire(compositeContext, endpointReference);
                compositeContext.getEndpointRegistry().addEndpointReference(endpointReference);
            }

        }
    }

    public void stop(Component component, ComponentReference reference) {
        if (logger.isLoggable(Level.FINE)) {
            logger.fine("Stopping component reference: " + component.getURI() + "#" + reference.getName());
        }
        RuntimeComponentReference runtimeRef = ((RuntimeComponentReference)reference);
        RuntimeComponent runtimeComponent = (RuntimeComponent) component;
        EndpointRegistry endpointRegistry = runtimeComponent.getComponentContext().getCompositeContext().getEndpointRegistry();
        for ( EndpointReference endpointReference : runtimeRef.getEndpointReferences()){
            RuntimeEndpointReference epr = (RuntimeEndpointReference) endpointReference;
            endpointRegistry.removeEndpointReference(endpointReference);
            ReferenceBindingProvider bindingProvider = epr.getBindingProvider();
            if (bindingProvider != null) {
                bindingProvider.stop();
            }
            for (PolicyProvider policyProvider : epr.getPolicyProviders()) {
                policyProvider.stop();
            }
        }
    }

    private void addReferenceWire(CompositeContext compositeContext, EndpointReference endpointReference) {
        RuntimeEndpointReference epr = (RuntimeEndpointReference) endpointReference;
        // create the wire
        // null endpoint passed in here as the endpoint reference may
        // not be resolved yet
        epr.bind(compositeContext);

        ComponentReference reference = endpointReference.getReference(); 
        InterfaceContract sourceContract = epr.getComponentTypeReferenceInterfaceContract();

        // TODO - EPR - interface contract seems to be null in the implementation.web
        //              case. Not introspecting the CT properly?
        if (sourceContract == null){
            // TODO - Can't do this with move of matching to wire
            // take the contract from the service to which the reference is connected
            sourceContract = ((RuntimeEndpoint) endpointReference.getTargetEndpoint()).getComponentTypeServiceInterfaceContract();
            reference.setInterfaceContract(sourceContract);
        }

        // endpointReference.setInterfaceContract(sourceContract.makeUnidirectional(false));
    }


}
