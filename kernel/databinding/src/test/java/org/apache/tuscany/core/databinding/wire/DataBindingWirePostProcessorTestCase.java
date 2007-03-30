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
import static org.easymock.EasyMock.verify;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;

import junit.framework.TestCase;

import org.apache.tuscany.core.databinding.processor.JavaInterfaceIntrospector;
import org.apache.tuscany.core.databinding.wire.DataBindingWirePostProcessor;
import org.apache.tuscany.databinding.xml.DOMDataBinding;
import org.apache.tuscany.databinding.xml.StAXDataBinding;
import org.apache.tuscany.spi.component.Component;
import org.apache.tuscany.spi.component.ComponentManager;
import org.apache.tuscany.spi.component.ReferenceBinding;
import org.apache.tuscany.spi.databinding.Mediator;
import org.apache.tuscany.spi.idl.InvalidServiceContractException;
import org.apache.tuscany.spi.model.Operation;
import org.apache.tuscany.spi.model.ServiceContract;
import org.apache.tuscany.spi.util.UriHelper;
import org.apache.tuscany.spi.wire.Interceptor;
import org.apache.tuscany.spi.wire.InvocationChain;
import org.apache.tuscany.spi.wire.Wire;
import org.easymock.EasyMock;
import org.osoa.sca.Constants;
import org.osoa.sca.annotations.Remotable;
import org.w3c.dom.Node;

/**
 * @version $Rev$ $Date$
 */
public class DataBindingWirePostProcessorTestCase extends TestCase {
    private static final QName BINDING_WS = new QName(Constants.SCA_NS, "binding.ws");
    private DataBindingWirePostProcessor processor;

    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
    }

    /**
     * Component.Reference --> Component.Service
     */
    public void testProcess1() throws Exception {
        URI sourceUri = URI.create("/composite1/component1/#reference1");
        URI targetUri = URI.create("/composite1/component2/#service1");
        
        Mediator mediator = createMock(Mediator.class);
        ComponentManager componentManager = createMock(ComponentManager.class);
        Component component1 = createMock(Component.class);
        // expect(component1.getReference("reference1")).andReturn(null);
        Component component2 = createMock(Component.class);
        // expect(component2.getService("service1")).andReturn(null);
        expect(componentManager.getComponent(UriHelper.getDefragmentedName(sourceUri))).andReturn(component1).anyTimes();
        expect(componentManager.getComponent(UriHelper.getDefragmentedName(targetUri))).andReturn(component2).anyTimes();
        replay(mediator, componentManager, component1, component2);
        DataBindingWirePostProcessor processor = new DataBindingWirePostProcessor(componentManager, mediator);

        Wire wire = createWire(sourceUri, targetUri, Wire.LOCAL_BINDING);
        processor.process(wire);
        
        verify(mediator, componentManager, component1, component2);
    }

    /**
     * Component.Reference --> Composite.Reference
     */
    public void testProcess2() throws Exception {
        ReferenceBinding referenceBinding = createMock(ReferenceBinding.class);
        Component composite = createMock(Component.class);
        expect(referenceBinding.getUri()).andReturn(URI.create("/compositeA/#ref1"));

        URI sourceUri = URI.create("/composite1/component1/#reference1");
        URI targetUri = URI.create("/composite1/#reference1");
        Mediator mediator = createMock(Mediator.class);
        ComponentManager componentManager = createMock(ComponentManager.class);
        Component component1 = createMock(Component.class);
        expect(component1.getReference("reference1")).andReturn(null);
        Component component2 = createMock(Component.class);
        expect(component2.getReference("reference1")).andReturn(null);
        expect(componentManager.getComponent(UriHelper.getDefragmentedName(sourceUri))).andReturn(component1).anyTimes();
        expect(componentManager.getComponent(UriHelper.getDefragmentedName(targetUri))).andReturn(component2).anyTimes();
        replay(mediator, componentManager, component1, component2);
        DataBindingWirePostProcessor processor = new DataBindingWirePostProcessor(componentManager, mediator);
        
        Wire wire = createWire(sourceUri, targetUri, BINDING_WS);
        processor.process(wire);
    }

    /**
     * Composite.Service --> Component Service
     */
    public void testProcess3() throws Exception {
        URI sourceUri = URI.create("/composite1/#service1");
        URI targetUri = URI.create("/composite1/component1/#service1");
        
        Mediator mediator = createMock(Mediator.class);
        ComponentManager componentManager = createMock(ComponentManager.class);
        Component component1 = createMock(Component.class);
        expect(component1.getService("service1")).andReturn(null);
        Component component2 = createMock(Component.class);
        expect(component2.getService("service1")).andReturn(null);
        expect(componentManager.getComponent(UriHelper.getDefragmentedName(sourceUri))).andReturn(component1).anyTimes();
        expect(componentManager.getComponent(UriHelper.getDefragmentedName(targetUri))).andReturn(component2).anyTimes();
        replay(mediator, componentManager, component1, component2);
        DataBindingWirePostProcessor processor = new DataBindingWirePostProcessor(componentManager, mediator);
        
        Wire wire = createWire(sourceUri, targetUri, BINDING_WS);
        processor.process(wire);
    }

    private Wire createWire(URI sourceUri, URI targetUri, QName bindingType) throws InvalidServiceContractException {
        ServiceContract<?> contract1 = new JavaInterfaceIntrospector().introspect(TestInterface1.class);
        contract1.setDataBinding(DOMDataBinding.NAME);
        ServiceContract<?> contract2 = new JavaInterfaceIntrospector().introspect(TestInterface2.class);
        contract2.setDataBinding(StAXDataBinding.NAME);
        Map<Operation<?>, InvocationChain> chains = new HashMap<Operation<?>, InvocationChain>();
        for (Operation<?> op : contract1.getOperations().values()) {
            InvocationChain chain = createMock(InvocationChain.class);
            chain.addInterceptor(EasyMock.anyInt(), EasyMock.isA(Interceptor.class));
            replay(chain);
            chains.put(op, chain);
        }        
        Map<Operation<?>, InvocationChain> callbackChains = new HashMap<Operation<?>, InvocationChain>();
        for (Operation<?> op : contract1.getCallbackOperations().values()) {
            InvocationChain chain = createMock(InvocationChain.class);
            chain.addInterceptor(EasyMock.anyInt(), EasyMock.isA(Interceptor.class));
            replay(chain);
            callbackChains.put(op, chain);
        }                
        Wire wire = EasyMock.createMock(Wire.class);
        expect(wire.getBindingType()).andReturn(bindingType).anyTimes();
        expect(wire.getSourceContract()).andReturn(contract1).anyTimes();
        expect(wire.getTargetContract()).andReturn(contract2).anyTimes();
        expect(wire.getInvocationChains()).andReturn(chains);
        expect(wire.getSourceUri()).andReturn(sourceUri).anyTimes();
        expect(wire.getTargetUri()).andReturn(targetUri).anyTimes();
        expect(wire.getCallbackInvocationChains()).andReturn(callbackChains).anyTimes();
        replay(wire);
        return wire;
    }

    @Remotable
    private static interface TestInterface1 {
        String test1(String str);

        Node test2(Node node);

        void test3(int i, String s) throws MyException;
    }

    @Remotable
    private static interface TestInterface2 {
        String test1(String str);

        XMLStreamReader test2(XMLStreamReader reader);

        void test3(int i, String s) throws MyException;
    }
    
    private static class MyException extends Exception {
        private static final long serialVersionUID = 7203411584939696390L;

        public MyException() {
            super();
        }

        /**
         * @param message
         * @param cause
         */
        public MyException(String message, Throwable cause) {
            super(message, cause);
        }

        /**
         * @param message
         */
        public MyException(String message) {
            super(message);
        }

        /**
         * @param cause
         */
        public MyException(Throwable cause) {
            super(cause);
        }

    }

}
