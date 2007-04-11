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
package org.apache.tuscany.core.deployer;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;

import org.apache.tuscany.assembly.ComponentReference;
import org.apache.tuscany.assembly.ComponentService;
import org.apache.tuscany.assembly.Composite;
import org.apache.tuscany.assembly.Implementation;
import org.apache.tuscany.assembly.Multiplicity;
import org.apache.tuscany.assembly.SCABinding;
import org.apache.tuscany.assembly.impl.DefaultAssemblyFactory;
import org.apache.tuscany.core.builder.BuilderRegistryImpl;
import org.apache.tuscany.core.builder.ComponentNotFoundException;
import org.apache.tuscany.core.builder.WireCreationException;
import org.apache.tuscany.core.wire.InvocationChainImpl;
import org.apache.tuscany.core.wire.InvokerInterceptor;
import org.apache.tuscany.core.wire.WireImpl;
import org.apache.tuscany.core.wire.WireUtils;
import org.apache.tuscany.interfacedef.InterfaceContract;
import org.apache.tuscany.interfacedef.Operation;
import org.apache.tuscany.spi.Scope;
import org.apache.tuscany.spi.builder.Builder;
import org.apache.tuscany.spi.builder.BuilderException;
import org.apache.tuscany.spi.builder.BuilderInstantiationException;
import org.apache.tuscany.spi.builder.BuilderRegistry;
import org.apache.tuscany.spi.builder.WiringException;
import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.Component;
import org.apache.tuscany.spi.component.ComponentManager;
import org.apache.tuscany.spi.component.Invocable;
import org.apache.tuscany.spi.component.RegistrationException;
import org.apache.tuscany.spi.component.SCAObject;
import org.apache.tuscany.spi.component.ScopeContainer;
import org.apache.tuscany.spi.component.ScopeRegistry;
import org.apache.tuscany.spi.component.TargetInvokerCreationException;
import org.apache.tuscany.spi.deployer.Deployer;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.resolver.ResolutionException;
import org.apache.tuscany.spi.wire.InvocationChain;
import org.apache.tuscany.spi.wire.Wire;
import org.osoa.sca.annotations.Reference;

/**
 * Default implementation of Deployer.
 * 
 * @version $Rev$ $Date$
 */
public class DeployerImpl implements Deployer {
    private XMLInputFactory xmlFactory;
    private Builder builder;
    private ComponentManager componentManager;
    private ScopeRegistry scopeRegistry;

    public DeployerImpl(XMLInputFactory xmlFactory, Builder builder, ComponentManager componentManager) {
        this.xmlFactory = xmlFactory;
        this.builder = builder;
        this.componentManager = componentManager;
    }

    public DeployerImpl() {
        xmlFactory = XMLInputFactory.newInstance("javax.xml.stream.XMLInputFactory", getClass().getClassLoader());
    }

    @Reference
    public void setBuilder(BuilderRegistry builder) {
        this.builder = builder;
    }

    @Reference
    public void setComponentManager(ComponentManager componentManager) {
        this.componentManager = componentManager;
    }

    @Reference
    public void setScopeRegistry(ScopeRegistry scopeRegistry) {
        this.scopeRegistry = scopeRegistry;
    }

    public Collection<Component> deploy(Composite composite) throws BuilderException, ResolutionException {
        @SuppressWarnings("unchecked")
        ScopeContainer<URI> scopeContainer = scopeRegistry.getScopeContainer(Scope.COMPOSITE);
        URI groupId = URI.create(composite.getName().getLocalPart());
        URI componentId = URI.create("/");
        DeploymentContext deploymentContext = new RootDeploymentContext(null, groupId, componentId, xmlFactory,
                                                                        scopeContainer);

        org.apache.tuscany.assembly.Component componentDef = new DefaultAssemblyFactory().createComponent();
        componentDef.setName(composite.getName().getLocalPart());
        componentDef.setImplementation(composite);

        // build runtime artifacts
        build(componentDef, deploymentContext);

        Collection<Component> components = deploymentContext.getComponents().values();
        for (Component toRegister : components) {
            try {
                Map<SCAObject, Object> models = ((BuilderRegistryImpl)builder).getModels();
                Object model = models.get(toRegister);
                if (model instanceof org.apache.tuscany.assembly.Component) {
                    connect(models, (org.apache.tuscany.assembly.Component)model);
                }
                componentManager.register(toRegister);
            } catch (RegistrationException e) {
                throw new BuilderInstantiationException("Error registering component", e);
            }
        }
        return components;
    }

    /**
     * Build the runtime context for a loaded componentDefinition.
     * 
     * @param parent the context that will be the parent of the new sub-context
     * @param componentDefinition the componentDefinition being deployed
     * @param deploymentContext the current deployment context
     * @return the new runtime context
     */
    protected SCAObject build(org.apache.tuscany.assembly.Component componentDefinition,
                              DeploymentContext deploymentContext) throws BuilderException {
        return builder.build(componentDefinition, deploymentContext);
    }

    public static org.apache.tuscany.assembly.Reference getReference(Implementation type, String name) {
        for (org.apache.tuscany.assembly.Reference ref : type.getReferences()) {
            if (ref.getName().equals(name)) {
                return ref;
            }
        }
        return null;
    }

    public void connect(Map<SCAObject, Object> models, org.apache.tuscany.assembly.Component definition)
        throws WiringException {
        Component source = getComponent(models, definition);
        if (source == null) {
            throw new ComponentNotFoundException("Source not found", URI.create(definition.getName()));
        }

        for (ComponentReference ref : definition.getReferences()) {
            List<Wire> wires = new ArrayList<Wire>();
            String refName = ref.getName();
            org.apache.tuscany.assembly.Reference refDefinition = getReference(definition.getImplementation(), refName);
            assert refDefinition != null;
            List<ComponentService> services = ref.getTargets();
            for (ComponentService service : services) {
                org.apache.tuscany.assembly.Component targetCompoent = service.getBinding(SCABinding.class)
                    .getComponent();
                Component target = getComponent(models, targetCompoent);
                URI targetUri = URI.create(target.getUri() + "#" + service.getName());
                if (target == null && (refDefinition.getMultiplicity() == Multiplicity.ZERO_ONE || refDefinition
                        .getMultiplicity() == Multiplicity.ZERO_N)) {
                    // a non-required reference, just skip
                    continue;
                }
                if (target == null) {
                    throw new ComponentNotFoundException("Target not found", targetUri);
                }
                URI sourceURI = URI.create(source.getUri() + "#" + refName);
                Wire wire = createWire(sourceURI, targetUri, refDefinition.getInterfaceContract(), Wire.LOCAL_BINDING);
                try {
                    attachInvokers(refName, wire, source, target);
                } catch (TargetInvokerCreationException e) {
                    throw new WireCreationException("Error creating invoker", sourceURI, targetUri, e);
                }
                /*
                 * if (postProcessorRegistry != null) {
                 * postProcessorRegistry.process(wire); }
                 */
                optimize(source, target, wire);
                wires.add(wire);
                if (!wire.getCallbackInvocationChains().isEmpty()) {
                    target.attachCallbackWire(wire);
                }
            }
            if (wires.size() > 1) {
                // attach as a multiplicity
                source.attachWires(wires);
            } else if (wires.size() == 1) {
                // attach as a single wire
                Wire wire = wires.get(0);
                source.attachWire(wire);
            }
        }
    }

    private Component getComponent(Map<SCAObject, Object> models, org.apache.tuscany.assembly.Component definition) {
        Component source = null;
        for (Map.Entry<SCAObject, Object> e : models.entrySet()) {
            if (e.getValue() == definition) {
                source = (Component)e.getKey();
            }
        }
        return source;
    }

    protected Wire createWire(URI sourceURI, URI targetUri, InterfaceContract contract, QName bindingType) {
        Wire wire = new WireImpl(bindingType);
        wire.setSourceContract(contract);
        wire.setTargetContract(contract);
        wire.setSourceUri(sourceURI);
        wire.setTargetUri(targetUri);
        for (Operation operation : contract.getInterface().getOperations()) {
            InvocationChain chain = new InvocationChainImpl(operation);
            /*
             * if (operation.isNonBlocking()) { chain.addInterceptor(new
             * NonBlockingInterceptor(scheduler, workContext)); }
             */
            chain.addInterceptor(new InvokerInterceptor());
            wire.addInvocationChain(operation, chain);

        }
        if (contract.getCallbackInterface() != null) {
            for (Operation operation : contract.getCallbackInterface().getOperations()) {
                InvocationChain chain = new InvocationChainImpl(operation);
                /*
                 * if (operation.isNonBlocking()) { chain.addInterceptor(new
                 * NonBlockingInterceptor(scheduler, workContext)); }
                 */
                chain.addInterceptor(new InvokerInterceptor());
                wire.addCallbackInvocationChain(operation, chain);
            }
        }
        return wire;
    }

    private void attachInvokers(String name, Wire wire, Invocable source, Invocable target)
        throws TargetInvokerCreationException {
        // TODO section will deleted be replaced when we cut-over to the
        // physical marshallers
        for (InvocationChain chain : wire.getInvocationChains().values()) {
            chain.setTargetInvoker(target.createTargetInvoker(name, chain.getOperation(), false));
        }
        for (InvocationChain chain : wire.getCallbackInvocationChains().values()) {
            chain.setTargetInvoker(source.createTargetInvoker(null, chain.getOperation(), true));
        }
    }

    protected void optimize(Component source, Component target, Wire wire) {
        boolean optimizableScopes = isOptimizable(source.getScope(), target.getScope());
        if (optimizableScopes && target.isOptimizable() && WireUtils.isOptimizable(wire)) {
            wire.setOptimizable(true);
            wire.setTarget((AtomicComponent)target);
        } else {
            wire.setOptimizable(false);
        }
    }

    protected boolean isOptimizable(Scope pReferrer, Scope pReferee) {
        if (pReferrer == Scope.UNDEFINED || pReferee == Scope.UNDEFINED
            || pReferrer == Scope.CONVERSATION
            || pReferee == Scope.CONVERSATION) {
            return false;
        }
        if (pReferee == pReferrer) {
            return true;
        } else if (pReferrer == Scope.STATELESS) {
            return true;
        } else if (pReferee == Scope.STATELESS) {
            return false;
        } else if (pReferrer == Scope.REQUEST && pReferee == Scope.SESSION) {
            return true;
        } else if (pReferrer == Scope.REQUEST && pReferee == Scope.COMPOSITE) {
            return true;
        } else if (pReferrer == Scope.REQUEST && pReferee == Scope.SYSTEM) {
            return true;
        } else if (pReferrer == Scope.SESSION && pReferee == Scope.COMPOSITE) {
            return true;
        } else if (pReferrer == Scope.SESSION && pReferee == Scope.SYSTEM) {
            return true;
        } else // noinspection SimplifiableIfStatement
        if (pReferrer == Scope.SYSTEM && pReferee == Scope.COMPOSITE) {
            // case where a service context points to a composite scoped
            // component
            return true;
        } else {
            return pReferrer == Scope.COMPOSITE && pReferee == Scope.SYSTEM;
        }
    }
}
