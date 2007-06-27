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
package org.apache.tuscany.sca.implementation.notification;

import java.util.ArrayList;
import java.util.List;

import org.apache.tuscany.sca.assembly.ComponentReference;
import org.apache.tuscany.sca.assembly.DefaultSCABindingFactory;
import org.apache.tuscany.sca.assembly.SCABinding;
import org.apache.tuscany.sca.implementation.notification.NotificationComponentInvoker;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.Interceptor;
import org.apache.tuscany.sca.invocation.InvocationChain;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.Message;
import org.apache.tuscany.sca.runtime.EndpointReference;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentReference;
import org.apache.tuscany.sca.runtime.RuntimeWire;
import org.easymock.EasyMock;

import junit.framework.Assert;
import junit.framework.TestCase;

/**
 * 
 * @version $Rev$ $Date$
 *
 */
public class NotificationComponentTestCase extends TestCase {

    public void testLocalNotificationComponent() throws Exception {
        try {
        Operation operation = EasyMock.createNiceMock(Operation.class);
        EasyMock.replay(operation);
        
        InvocationChain sub1Chain = EasyMock.createNiceMock(InvocationChain.class);
        EasyMock.expect(sub1Chain.getTargetOperation()).andReturn(operation);
        EasyMock.expect(sub1Chain.getHeadInvoker()).andReturn(new MockInterceptor());
        EasyMock.replay(sub1Chain);
        List<InvocationChain> sub1Chains = new ArrayList<InvocationChain>();
        sub1Chains.add(sub1Chain);
        SCABinding b1 = new DefaultSCABindingFactory().createSCABinding();
        EndpointReference epr1 = EasyMock.createNiceMock(EndpointReference.class);
        EasyMock.expect(epr1.getURI()).andReturn("wire1Target");
        EasyMock.expect(epr1.getBinding()).andReturn(b1);
        EasyMock.replay(epr1);
        RuntimeWire sub1Wire = EasyMock.createNiceMock(RuntimeWire.class);
        EasyMock.expect(sub1Wire.getInvocationChains()).andReturn(sub1Chains);
        EasyMock.expect(sub1Wire.getTarget()).andReturn(epr1).anyTimes();
        EasyMock.replay(sub1Wire);
        ArrayList<RuntimeWire> rtWires1 = new ArrayList<RuntimeWire>();
        rtWires1.add(sub1Wire);
        RuntimeComponentReference rtCompRef1 = EasyMock.createNiceMock(RuntimeComponentReference.class);
        EasyMock.expect(rtCompRef1.getName()).andReturn("sub1Reference");
        EasyMock.expect(rtCompRef1.getRuntimeWires()).andReturn(rtWires1);
        EasyMock.replay(rtCompRef1);
        
        InvocationChain sub2Chain = EasyMock.createNiceMock(InvocationChain.class);
        EasyMock.expect(sub2Chain.getTargetOperation()).andReturn(operation);
        EasyMock.expect(sub2Chain.getHeadInvoker()).andReturn(new MockInterceptor());
        EasyMock.replay(sub2Chain);
        List<InvocationChain> sub2Chains = new ArrayList<InvocationChain>();
        sub2Chains.add(sub2Chain);
        SCABinding b2 = new DefaultSCABindingFactory().createSCABinding();
        EndpointReference epr2 = EasyMock.createNiceMock(EndpointReference.class);
        EasyMock.expect(epr2.getURI()).andReturn("wire2Target");
        EasyMock.expect(epr2.getBinding()).andReturn(b2);
        EasyMock.replay(epr2);
        RuntimeWire sub2Wire = EasyMock.createNiceMock(RuntimeWire.class);
        EasyMock.expect(sub2Wire.getInvocationChains()).andReturn(sub2Chains);
        EasyMock.expect(sub2Wire.getTarget()).andReturn(epr2).anyTimes();
        EasyMock.replay(sub2Wire);
        ArrayList<RuntimeWire> rtWires2 = new ArrayList<RuntimeWire>();
        rtWires2.add(sub2Wire);
        RuntimeComponentReference rtCompRef2 = EasyMock.createNiceMock(RuntimeComponentReference.class);
        EasyMock.expect(rtCompRef2.getName()).andReturn("sub2Reference");
        EasyMock.expect(rtCompRef2.getRuntimeWires()).andReturn(rtWires2);
        EasyMock.replay(rtCompRef2);
        
        ArrayList<ComponentReference> references = new ArrayList<ComponentReference>();
        references.add(rtCompRef1);
        references.add(rtCompRef2);
        RuntimeComponent component = EasyMock.createNiceMock(RuntimeComponent.class);
        EasyMock.expect(component.getName()).andReturn("LocalNotificationComponentTest");
        EasyMock.expect(component.getReferences()).andReturn(references);
        EasyMock.replay(component);
        
        Invoker localNotificationInvoker = new NotificationComponentInvoker(operation, component);
        
        Message msg = EasyMock.createNiceMock(Message.class);
        EasyMock.expect(msg.getBody()).andReturn("msg").times(3);  // once per sub int + once in notif target invoker
        EasyMock.replay(msg);
        localNotificationInvoker.invoke(msg);
        EasyMock.verify(msg);
        } catch(Throwable e) {
            e.printStackTrace();
        }
    }
    
    class MockInterceptor implements Interceptor {
        
        public Message invoke(Message msg) {
            Assert.assertEquals("msg", msg.getBody());
            return msg;
        }
        
        public void setNext(Invoker next) {
            throw new AssertionError();
        }

        public Interceptor getNext() {
            throw new AssertionError();
        }

        public boolean isOptimizable() {
            throw new AssertionError();
        }
    }
}
