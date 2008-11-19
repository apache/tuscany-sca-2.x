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
package org.apache.tuscany.sca.core.invocation;

import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.core.factory.ObjectCreationException;
import org.apache.tuscany.sca.core.factory.ObjectFactory;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentReference;
import org.osoa.sca.CallableReference;

/**
 * Uses a wire to return a CallableReference
 * 
 * @version $Rev$ $Date$
 */
public class CallableReferenceObjectFactory implements ObjectFactory<CallableReference<?>> {
    private Class<?> businessInterface;
    private RuntimeComponent component;
    private RuntimeComponentReference reference;
    private Binding binding;

    /**
     * Constructor.
     * 
     * To support the @Reference protected CallableReference<MyService> ref;
     * 
     * @param businessInterface the interface to inject
     * @param component the component defining the reference to be injected
     * @param reference the reference to be injected
     * @param binding the binding for the reference
     */
    public CallableReferenceObjectFactory(Class<?> businessInterface,
                                          RuntimeComponent component,
                                          RuntimeComponentReference reference,
                                          Binding binding) {
        this.businessInterface = businessInterface;
        this.component = component;
        this.reference = reference;
        this.binding = binding;
    }

    public CallableReference<?> getInstance() throws ObjectCreationException {
        return component.getComponentContext().getServiceReference(businessInterface, reference, binding);
    }

}
