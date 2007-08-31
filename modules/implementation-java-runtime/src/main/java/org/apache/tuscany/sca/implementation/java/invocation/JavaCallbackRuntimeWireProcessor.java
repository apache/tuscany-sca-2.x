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

package org.apache.tuscany.sca.implementation.java.invocation;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.tuscany.sca.assembly.Contract;
import org.apache.tuscany.sca.assembly.Implementation;
import org.apache.tuscany.sca.core.invocation.CallbackInterfaceInterceptor;
import org.apache.tuscany.sca.implementation.java.JavaImplementation;
import org.apache.tuscany.sca.interfacedef.Interface;
import org.apache.tuscany.sca.interfacedef.InterfaceContractMapper;
import org.apache.tuscany.sca.interfacedef.InvalidInterfaceException;
import org.apache.tuscany.sca.interfacedef.java.JavaInterface;
import org.apache.tuscany.sca.interfacedef.java.JavaInterfaceFactory;
import org.apache.tuscany.sca.invocation.InvocationChain;
import org.apache.tuscany.sca.runtime.EndpointReference;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentReference;
import org.apache.tuscany.sca.runtime.RuntimeWire;
import org.apache.tuscany.sca.runtime.RuntimeWireProcessor;

/**
 * @version $Rev$ $Date$
 */
public class JavaCallbackRuntimeWireProcessor implements RuntimeWireProcessor {
    private final static Logger logger = Logger.getLogger(JavaCallbackRuntimeWireProcessor.class.getName());
    private InterfaceContractMapper interfaceContractMapper;
    private JavaInterfaceFactory javaInterfaceFactory;

    /**
     * @param interfaceContractMapper
     * @param javaInterfaceFactory
     */
    public JavaCallbackRuntimeWireProcessor(InterfaceContractMapper interfaceContractMapper,
                                            JavaInterfaceFactory javaInterfaceFactory) {
        super();
        this.interfaceContractMapper = interfaceContractMapper;
        this.javaInterfaceFactory = javaInterfaceFactory;
    }

    public void process(RuntimeWire wire) {
        addCallbackInterfaceInterceptors(wire);
    }

    private void addCallbackInterfaceInterceptors(RuntimeWire wire) {
        Contract contract = wire.getSource().getContract();
        if (!(contract instanceof RuntimeComponentReference)) {
            return;
        }
        RuntimeComponent component = wire.getSource().getComponent();
        Implementation implementation = component.getImplementation();
        if (!(implementation instanceof JavaImplementation)) {
            return;
        }
        JavaImplementation javaImpl = (JavaImplementation)implementation;
        EndpointReference callbackEndpoint = wire.getSource().getCallbackEndpoint();
        if (callbackEndpoint != null) {
            Interface iface = callbackEndpoint.getContract().getInterfaceContract().getInterface();
            if (!supportsCallbackInterface(iface, javaImpl)) {
                // callback to this impl is not possible, so ensure a callback object is set
                for (InvocationChain chain : wire.getInvocationChains()) {
                    chain.addInterceptor(0, new CallbackInterfaceInterceptor());
                }
            }
        }
    }

    private boolean supportsCallbackInterface(Interface iface, JavaImplementation impl) {
        if (iface instanceof JavaInterface) {
            Class<?> ifaceClass = ((JavaInterface)iface).getJavaClass();
            if (ifaceClass.isAssignableFrom(impl.getJavaClass())) {
                return true;
            }
        }
        try {
            Interface implType = javaInterfaceFactory.createJavaInterface(impl.getJavaClass());
            // Ignore the remotable/conversational testing
            implType.setRemotable(iface.isRemotable());
            implType.setConversational(iface.isConversational());
            return interfaceContractMapper.isCompatible(iface, implType);
        } catch (InvalidInterfaceException e) {
            logger.log(Level.WARNING, e.getMessage(), e);
            return false;
        }
    }
}
