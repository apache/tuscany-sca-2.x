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
package org.apache.tuscany.core.wire.jdk;

import java.lang.reflect.Proxy;

import org.osoa.sca.annotations.Constructor;
import org.osoa.sca.annotations.Init;

import org.apache.tuscany.spi.annotation.Autowire;
import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.model.ServiceContract;
import org.apache.tuscany.spi.policy.PolicyBuilderRegistry;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.OutboundWire;
import org.apache.tuscany.spi.wire.ProxyCreationException;
import org.apache.tuscany.spi.wire.RuntimeWire;
import org.apache.tuscany.spi.wire.WireInvocationHandler;

import org.apache.tuscany.core.wire.WireServiceExtension;

/**
 * the default implementation of a wire service that uses JDK dynamic proxies
 *
 * @version $$Rev$$ $$Date$$
 */
public class JDKWireService extends WireServiceExtension {

    public JDKWireService() {
        super(null, null);
    }

    @Constructor
    public JDKWireService(@Autowire WorkContext context, @Autowire PolicyBuilderRegistry policyRegistry) {
        super(context, policyRegistry);
    }

    @Init(eager = true)
    public void init() {
    }

    public <T> T createProxy(Class<T> interfaze, InboundWire wire) throws ProxyCreationException {
        JDKInboundInvocationHandler handler = new JDKInboundInvocationHandler(wire, context);
        ClassLoader cl = interfaze.getClassLoader();
        return interfaze.cast(Proxy.newProxyInstance(cl, new Class[]{interfaze}, handler));
    }

    public Object createProxy(RuntimeWire wire) throws ProxyCreationException {
        assert wire != null : "Wire was null";
        if (wire instanceof InboundWire) {
            InboundWire inbound = (InboundWire) wire;
            Class<?> interfaze = wire.getServiceContract().getInterfaceClass();
            JDKInboundInvocationHandler handler = new JDKInboundInvocationHandler(inbound, context);
            ClassLoader cl = interfaze.getClassLoader();
            //FIXME
            return Proxy.newProxyInstance(cl, new Class[]{interfaze}, handler);
        } else if (wire instanceof OutboundWire) {
            OutboundWire outbound = (OutboundWire) wire;
            JDKOutboundInvocationHandler handler = new JDKOutboundInvocationHandler(outbound, context);
            Class<?> interfaze = outbound.getServiceContract().getInterfaceClass();
            ClassLoader cl = interfaze.getClassLoader();
            return Proxy.newProxyInstance(cl, new Class[]{interfaze}, handler);
        } else {
            throw new ProxyCreationException("Invalid wire type", wire.getClass().getName());
        }
    }

    public Object createCallbackProxy(ServiceContract<?> contract, InboundWire wire) throws ProxyCreationException {
        Class<?> interfaze = contract.getCallbackClass();
        ClassLoader cl = interfaze.getClassLoader();
        JDKCallbackInvocationHandler handler = new JDKCallbackInvocationHandler(wire, context);
        return interfaze.cast(Proxy.newProxyInstance(cl, new Class[]{interfaze}, handler));
    }

    public WireInvocationHandler createHandler(RuntimeWire wire) {
        assert wire != null : "Wire was null";
        if (wire instanceof InboundWire) {
            InboundWire inbound = (InboundWire) wire;
            return new JDKInboundInvocationHandler(inbound, context);
        } else if (wire instanceof OutboundWire) {
            OutboundWire outbound = (OutboundWire) wire;
            return new JDKOutboundInvocationHandler(outbound, context);
        } else {
            throw new ProxyCreationException("Invalid wire type", wire.getClass().getName());
        }
    }

    public WireInvocationHandler createCallbackHandler(InboundWire wire) {
        return new JDKCallbackInvocationHandler(wire, context);
    }

}
