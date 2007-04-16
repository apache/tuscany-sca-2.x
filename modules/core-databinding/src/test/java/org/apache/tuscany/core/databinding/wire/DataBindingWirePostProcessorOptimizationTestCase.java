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
package org.apache.tuscany.core.databinding.wire;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.apache.tuscany.databinding.impl.MediatorImpl;
import org.apache.tuscany.interfacedef.InterfaceContract;
import org.apache.tuscany.interfacedef.Operation;
import org.apache.tuscany.interfacedef.impl.OperationImpl;
import org.apache.tuscany.interfacedef.java.impl.JavaInterfaceContractImpl;
import org.apache.tuscany.interfacedef.java.impl.JavaInterfaceImpl;
import org.apache.tuscany.spi.component.Component;
import org.apache.tuscany.spi.component.ComponentManager;
import org.apache.tuscany.spi.databinding.Mediator;
import org.apache.tuscany.spi.wire.InvocationChain;
import org.apache.tuscany.spi.wire.Wire;
import org.easymock.EasyMock;

/**
 * Verifies that data binding interceptor is not added to invocation chains when
 * the data binding types are not set on service contracts
 * 
 * @version $Rev$ $Date$
 */
public class DataBindingWirePostProcessorOptimizationTestCase extends TestCase {
    private DataBindingWirePostProcessor processor;
    private InvocationChain chain;
    private Wire wire;

    public void testNoInterceptorInterposedOutboundToInbound() {
        processor.process(wire);
        EasyMock.verify(chain);
        EasyMock.verify(wire);
    }

    protected void setUp() throws Exception {
        super.setUp();

        URI sourceUri = URI.create("/componentA/#ref1");
        URI targetUri = URI.create("/componentB/#svc1");

        // FIXME: [rfeng] We should use Mocks here
        Mediator mediator = new MediatorImpl();
        ComponentManager componentManager = createMock(ComponentManager.class);
        Component component = createMock(Component.class);
        expect(componentManager.getComponent(URIHelper.getDefragmentedName(sourceUri))).andReturn(component);
        expect(componentManager.getComponent(URIHelper.getDefragmentedName(targetUri))).andReturn(component);

        replay(component, componentManager);
        processor = new DataBindingWirePostProcessor(componentManager, mediator);

        InterfaceContract contract = new JavaInterfaceContractImpl();
        contract.setInterface(new JavaInterfaceImpl());
        contract.setCallbackInterface(new JavaInterfaceImpl());
        Operation operation = new OperationImpl("test", null, null, null);
        contract.getInterface().getOperations().add(operation);
        contract.getCallbackInterface().getOperations().add(operation);

        chain = createMock(InvocationChain.class);
//        EasyMock.expect(chain.getSourceOperation()).andReturn(operation);
//        EasyMock.expect(chain.getTargetOperation()).andReturn(operation);
        replay(chain);
        List<InvocationChain> chains = new ArrayList<InvocationChain>();
        
        chains.add(chain);

        wire = EasyMock.createMock(Wire.class);
        expect(wire.getBindingType()).andReturn(Wire.LOCAL_BINDING).anyTimes();
        expect(wire.getSourceContract()).andReturn(contract).anyTimes();
        expect(wire.getTargetContract()).andReturn(contract).anyTimes();
        expect(wire.getInvocationChains()).andReturn(chains).anyTimes();
        expect(wire.getSourceUri()).andReturn(sourceUri).anyTimes();
        expect(wire.getTargetUri()).andReturn(targetUri).anyTimes();
        expect(wire.getCallbackInvocationChains()).andReturn(chains).anyTimes();
        replay(wire);

    }
}
