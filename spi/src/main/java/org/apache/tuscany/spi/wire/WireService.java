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
package org.apache.tuscany.spi.wire;

import java.lang.reflect.Method;

import org.apache.tuscany.spi.component.Component;
import org.apache.tuscany.spi.component.Reference;
import org.apache.tuscany.spi.component.Service;
import org.apache.tuscany.spi.model.BindlessServiceDefinition;
import org.apache.tuscany.spi.model.ComponentDefinition;
import org.apache.tuscany.spi.model.ReferenceDefinition;
import org.apache.tuscany.spi.model.ReferenceTarget;
import org.apache.tuscany.spi.model.ServiceDefinition;
import org.apache.tuscany.spi.model.BoundServiceDefinition;

/**
 * Creates proxies that implement Java interfaces and invocation handlers for fronting wires
 *
 * @version $$Rev$$ $$Date$$
 */

public interface WireService {

    <T> T createProxy(RuntimeWire<T> wire) throws ProxyCreationException;

    <T> T createCallbackProxy(Class<T> interfaze) throws ProxyCreationException;

    WireInvocationHandler createHandler(RuntimeWire<?> wire);

    WireInvocationHandler createCallbackHandler();

    OutboundWire createOutboundWire();

    InboundWire createInboundWire();

    OutboundInvocationChain createOutboundChain(Method operation);

    InboundInvocationChain createInboundChain(Method operation);

    InboundWire createWire(ServiceDefinition service);

    OutboundWire createWire(ReferenceTarget reference, ReferenceDefinition def);

    void createWires(Component component, ComponentDefinition<?> definition);

    void createWires(Reference<?> reference);

    void createWires(Service<?> service, BoundServiceDefinition<?> def);

    void createWires(Service<?> service, BindlessServiceDefinition def);

}
