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

package org.apache.tuscany.sca.core.assembly;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.apache.tuscany.sca.assembly.Endpoint;
import org.apache.tuscany.sca.assembly.Reference;
import org.apache.tuscany.sca.core.invocation.InvocationChainImpl;
import org.apache.tuscany.sca.endpointresolver.EndpointResolver;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.InvocationChain;
import org.apache.tuscany.sca.invocation.Message;
import org.apache.tuscany.sca.provider.ReferenceBindingProvider;
import org.apache.tuscany.sca.runtime.EndpointReference;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentReference;
import org.apache.tuscany.sca.runtime.RuntimeWire;
import org.osoa.sca.ServiceUnavailableException;

/**
 * @version $Rev$ $Date$
 */
public class EndpointWireImpl implements RuntimeWire {

    private Endpoint endpoint;
    private CompositeActivatorImpl compositeActivator;
    
    private EndpointResolver endpointResolver;
    private EndpointReference source;
    private RuntimeWire wire;
    private InvocationChain binidngInvocationChain;

    /**
     * @param endpoint
     */
    public EndpointWireImpl(Endpoint endpoint, CompositeActivator compositeActivator) {
        super();
        this.endpoint = endpoint;
        // TODO - improve the SPI to get rid of this cast
        this.compositeActivator = (CompositeActivatorImpl)compositeActivator;
        
        // store source configuration as we have most of this now. We don't though know what the 
        // target is yet. 
        Reference componentTypeRef = endpoint.getSourceComponentReference().getReference();
        InterfaceContract sourceContract =
            componentTypeRef == null ? endpoint.getSourceComponentReference().getInterfaceContract() : componentTypeRef.getInterfaceContract();
        sourceContract = sourceContract.makeUnidirectional(false);

        source = new EndpointReferenceImpl((RuntimeComponent)endpoint.getSourceComponent(), 
                                            endpoint.getSourceComponentReference(), 
                                            null, 
                                            sourceContract);
        
        RuntimeComponentReference runtimeRef = ((RuntimeComponentReference)endpoint.getSourceComponentReference());
        endpointResolver = runtimeRef.getEndpointResolver(endpoint);
        
    }

    public synchronized List<InvocationChain> getInvocationChains() {
        // where late binding happens. Find the endpoint provider and
        // ask it to do the endpoint resolution.
        if (endpoint.isUnresolved()){
            
            // this method should locate a viable target service and complete the 
            // endpoint configuration
            endpointResolver.resolve();
            
            if (endpoint.isUnresolved()){
                throw new ServiceUnavailableException("Unable to resolve service for component: " +
                        endpoint.getSourceComponent().getName() +
                        " reference: " + 
                        endpoint.getSourceComponentReference().getName() +
                        " target: " + 
                        endpoint.getTargetName());
            } 
        }
        
        if (wire == null){           
            RuntimeComponentReference runtimeRef = ((RuntimeComponentReference)endpoint.getSourceComponentReference());
            
            // add the resolved binding into the reference
            runtimeRef.getBindings().add(endpoint.getSourceBinding());
            
            // add a binding provider into the reference for the resolved binding 
            compositeActivator.addReferenceBindingProviderForEndpoint(endpoint);
            
            // extract the binding provider that has been created
            ReferenceBindingProvider bindingProvider = runtimeRef.getBindingProvider(endpoint.getSourceBinding());
            
            // start the binding provider  
            bindingProvider.start();
            
            // create the wire
            compositeActivator.addReferenceWireForEndpoint(endpoint);
            
            // extract the wire that has been created
            wire = runtimeRef.getRuntimeWire(endpoint.getSourceBinding());
        }            

        return wire.getInvocationChains();
    }

    public InvocationChain getInvocationChain(Operation operation) {
        if (wire ==null){
            return null;
        } else {
            return wire.getInvocationChain(operation);
        }
    }

    public Object invoke(Message msg) throws InvocationTargetException {
        // not called as the endpoint wire only appears on the reference side
        return null;
    }
    
    public Object invoke(Operation operation, Object[] args) throws InvocationTargetException {
        // not called as the endpoint wire only appears on the reference side
        return null;
    }

    public Object invoke(Operation operation, Message msg) throws InvocationTargetException {
        // not called as the endpoint wire only appears on the reference side
        return null;
    }


    public EndpointReference getSource() {
        return source;
    }

    public EndpointReference getTarget() {
        return null;
    }

    public void setTarget(EndpointReference target) {
    }

    public void rebuild() {
    }
    
    public synchronized InvocationChain getBindingInvocationChain() {
        if (binidngInvocationChain == null) {
            if (source instanceof RuntimeComponentReference) {
                binidngInvocationChain = new InvocationChainImpl(null, null, true);
            } else {
                binidngInvocationChain = new InvocationChainImpl(null, null, false);
            }
        }
        return binidngInvocationChain;
    }

    // TODO: TUSCANY-2580: give RuntimeComponentReferenceImpl a way to get at the endpoint
    public Endpoint getEndpoint() {
        return endpoint;
    }
    
    /**
     * @see java.lang.Object#clone()
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        EndpointWireImpl copy = (EndpointWireImpl)super.clone();
        return copy;
    }
}
