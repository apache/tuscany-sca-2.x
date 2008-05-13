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
package org.apache.tuscany.sca.binding.ejb.provider;

import org.apache.tuscany.sca.binding.ejb.EJBBinding;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.interfacedef.java.JavaInterface;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.provider.ReferenceBindingProvider;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentReference;

/**
 * Implementation of the ReferenceBindingProvider for the EJBBinding.
 *
 * @version $Rev$ $Date$
 */
public class EJBBindingReferenceBindingProvider implements ReferenceBindingProvider {
    private EJBBinding ejbBinding;
    private RuntimeComponentReference reference;

    /**
     * Constructor
     * 
     * @param component
     * @param reference
     * @param binding
     */
    public EJBBindingReferenceBindingProvider(RuntimeComponent component,
                                              RuntimeComponentReference reference,
                                              EJBBinding binding) {
        this.reference = reference;
        this.ejbBinding = binding;
    }

    /**
     * {@inheritDoc}
     */
    public Invoker createInvoker(Operation operation) {
        return new EJBBindingInvoker(ejbBinding, ((JavaInterface)reference.getInterfaceContract().getInterface())
            .getJavaClass(), operation);
    }

    /**
     * {@inheritDoc}
     */
    public InterfaceContract getBindingInterfaceContract() {
        return reference.getInterfaceContract();
    }

    /**
     * {@inheritDoc}
     */
    public void start() {
    }

    /**
     * {@inheritDoc}
     */
    public void stop() {
    }

    /**
     * {@inheritDoc}
     */
    public boolean supportsOneWayInvocation() {
        return true;
    }
}
