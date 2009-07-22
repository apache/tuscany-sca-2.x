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
package org.apache.tuscany.sca.core.invocation.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


import org.apache.tuscany.sca.core.assembly.impl.RuntimeWireImpl;
import org.apache.tuscany.sca.core.context.impl.CallbackServiceReferenceImpl;
import org.apache.tuscany.sca.core.context.impl.ServiceReferenceImpl;
import org.apache.tuscany.sca.invocation.InvocationChain;
import org.apache.tuscany.sca.invocation.MessageFactory;
import org.apache.tuscany.sca.runtime.RuntimeWire;
import org.oasisopen.sca.ServiceRuntimeException;

/**
 * Responsible for dispatching to a callback through a wire. <p/> TODO cache
 * target invoker
 * 
 * @version $Rev$ $Date$
 */
public class JDKCallbackInvocationHandler extends JDKInvocationHandler {
    private static final long serialVersionUID = -3350283555825935609L;

    public JDKCallbackInvocationHandler(MessageFactory messageFactory, ServiceReferenceImpl ref) {
        super(messageFactory, ref);
        this.fixedWire = false;
    }

    @Override
    @SuppressWarnings( {"unchecked"})
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        
        if (Object.class == method.getDeclaringClass()) {
            return invokeObjectMethod(method, args);
        }
   
        // obtain a dedicated wire to be used for this callback invocation
        RuntimeWire wire = ((CallbackServiceReferenceImpl)callableReference).getCallbackWire();
        if (wire == null) {
            //FIXME: need better exception
            throw new ServiceRuntimeException("No callback wire found");
        }

        setEndpoint(((CallbackServiceReferenceImpl)callableReference).getResolvedEndpoint());

        InvocationChain chain = getInvocationChain(method, wire);
        if (chain == null) {
            throw new IllegalArgumentException("No matching operation is found: " + method);
        }

        try {
            return invoke(chain, args, wire, wire.getEndpointReference());
        } catch (InvocationTargetException e) {
            Throwable t = e.getCause();
            throw e;
        } finally {
            // allow the cloned wire to be reused by subsequent callbacks
            ((RuntimeWireImpl)wire).releaseWire();
        }
    }

}
