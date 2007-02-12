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
package org.apache.tuscany.core.implementation.composite;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tuscany.spi.ObjectCreationException;
import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.TargetDestructionException;
import org.apache.tuscany.spi.component.TargetInitializationException;
import org.apache.tuscany.spi.component.TargetResolutionException;
import org.apache.tuscany.spi.extension.AbstractComponentExtension;
import org.apache.tuscany.spi.idl.InvalidServiceContractException;
import org.apache.tuscany.spi.idl.java.JavaInterfaceProcessorRegistry;
import org.apache.tuscany.spi.idl.java.JavaServiceContract;
import org.apache.tuscany.spi.model.Operation;
import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.spi.model.ServiceContract;
import org.apache.tuscany.spi.model.ServiceDefinition;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.OutboundWire;
import org.apache.tuscany.spi.wire.TargetInvoker;
import org.apache.tuscany.spi.wire.WireService;

import org.apache.tuscany.core.idl.java.JavaInterfaceProcessorRegistryImpl;
import org.apache.tuscany.core.wire.jdk.JDKWireService;

/**
 * An {@link org.apache.tuscany.spi.component.AtomicComponent} used when registering objects directly into a composite
 *
 * @version $$Rev$$ $$Date$$
 */
public class SystemSingletonAtomicComponent<S, T extends S> extends AbstractComponentExtension
    implements AtomicComponent {
    private T instance;
    private Map<String, InboundWire> inboundWires;
    // JFM FIXME JDKWireService
    private WireService wireService = new JDKWireService();
    // JFM FIXME remove
    private JavaInterfaceProcessorRegistry interfaceProcessorRegistry = new JavaInterfaceProcessorRegistryImpl();
    private List<ServiceContract> serviceContracts = new ArrayList<ServiceContract>();

    public SystemSingletonAtomicComponent(URI name, Class<S> interfaze, T instance) {
        super(name);
        this.instance = instance;
        inboundWires = new HashMap<String, InboundWire>();
        try {
            initWire(interfaze);
        } catch (InvalidServiceContractException e) {
            // JFM FIXME
            e.printStackTrace();
        }
    }

    public SystemSingletonAtomicComponent(URI name, List<Class<?>> services, T instance) {
        super(name);
        this.instance = instance;
        inboundWires = new HashMap<String, InboundWire>();
        for (Class<?> interfaze : services) {
            try {
                initWire(interfaze);
            } catch (InvalidServiceContractException e) {
                // xcv ficme
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
    }

    public Scope getScope() {
        return Scope.COMPOSITE;
    }

    public boolean isEagerInit() {
        return false;
    }

    public boolean isDestroyable() {
        return false;
    }

    public int getInitLevel() {
        return 0;
    }

    public long getMaxIdleTime() {
        return -1;
    }

    public long getMaxAge() {
        return -1;
    }

    public T getTargetInstance() throws TargetResolutionException {
        return instance;
    }

    public void init(Object instance) throws TargetInitializationException {

    }

    public void destroy(Object instance) throws TargetDestructionException {

    }

    public Object createInstance() throws ObjectCreationException {
        throw new UnsupportedOperationException();
    }

    public void removeInstance() {
        throw new UnsupportedOperationException();
    }

    public void addInboundWire(InboundWire wire) {
        inboundWires.put(wire.getUri().getFragment(), wire);
    }

    public Collection<InboundWire> getInboundWires() {
        return Collections.unmodifiableCollection(inboundWires.values());
    }

    public InboundWire getInboundWire(String serviceName) {
        return inboundWires.get(serviceName);
    }


    public InboundWire getTargetWire(String targetName) {
        return getInboundWire(targetName);
    }

    public void addOutboundWire(OutboundWire wire) {
        throw new UnsupportedOperationException();
    }

    public void addOutboundWires(List<OutboundWire> wires) {
        throw new UnsupportedOperationException();
    }

    public Map<String, List<OutboundWire>> getOutboundWires() {
        return Collections.emptyMap();
    }


    public boolean isOptimizable() {
        return true;
    }

    public TargetInvoker createTargetInvoker(String targetName, Operation operation, InboundWire callbackWire) {
        return null;
    }

    public List<ServiceContract> getServiceContracts() {
        return serviceContracts;
    }

    private void initWire(Class<?> interfaze) throws InvalidServiceContractException {
        JavaServiceContract serviceContract = interfaceProcessorRegistry.introspect(interfaze);
        // create a relative URI
        URI uri = URI.create("#" + interfaze.getName());
        ServiceDefinition def = new ServiceDefinition(uri, serviceContract, false);
        InboundWire wire = wireService.createWire(def);
        wire.setContainer(this);
        inboundWires.put(wire.getUri().getFragment(), wire);
        serviceContracts.add(serviceContract);
    }

}
