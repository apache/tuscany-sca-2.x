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

package org.apache.tuscany.sca.binding.rmi;

import java.lang.reflect.Method;

import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.interfacedef.java.JavaInterface;
import org.apache.tuscany.sca.interfacedef.java.impl.JavaInterfaceUtil;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.rmi.RMIHost;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentReference;
import org.apache.tuscany.sca.spi.InvokerFactory;
import org.osoa.sca.ServiceRuntimeException;

public class RMIReferenceInvokerFactory implements InvokerFactory {

    RuntimeComponentReference reference;
    RMIHost rmiHost;
    RMIBinding binding;

    public RMIReferenceInvokerFactory(RuntimeComponent rc, RuntimeComponentReference rcr, RMIBinding binding, RMIHost rmiHost) {
        this.reference = rcr;
        this.rmiHost = rmiHost;
        this.binding = binding;
    }

    public Invoker createInvoker(Operation operation) {
        try {

            Class<?> iface = ((JavaInterface)reference.getInterfaceContract().getInterface()).getJavaClass();
            Method remoteMethod = JavaInterfaceUtil.findMethod(iface, operation);

            return new RMIReferenceInvoker(rmiHost, binding.getHost(), binding.getPort(), binding.getServiceName(), remoteMethod);

        } catch (NoSuchMethodException e) {
            throw new ServiceRuntimeException(operation.toString(), e);
        }
    }

}
