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
package org.apache.tuscany.container.script;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.apache.tuscany.spi.component.ScopeContainer;
import org.apache.tuscany.spi.component.TargetException;
import org.apache.tuscany.spi.model.Operation;
import static org.apache.tuscany.spi.model.Operation.NO_CONVERSATION;
import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.spi.model.ServiceContract;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.RuntimeWire;
import org.apache.tuscany.spi.wire.TargetInvoker;
import org.apache.tuscany.spi.wire.WireService;

import junit.framework.TestCase;
import org.easymock.EasyMock;
import org.easymock.IAnswer;

public class ScriptComponentTestCase extends TestCase {

    private ScopeContainer container;

    @SuppressWarnings("unchecked")
    public void testCreateTargetInvoker() {
        ComponentConfiguration config = new ComponentConfiguration();
        config.setName("foo");
        config.setScopeContainer(container);
        ScriptComponent component = new ScriptComponent(config);
        Operation<Type> operation = new Operation<Type>("hashCode", null, null, null, false, null, NO_CONVERSATION);
        operation.setServiceContract(new Contract<Type>(List.class));
        TargetInvoker invoker = component.createTargetInvoker("hashCode", operation, null);
        assertNotNull(invoker);
    }

    @SuppressWarnings("unchecked")
    public void testGetServiceInstance() {
        WireService wireService = EasyMock.createMock(WireService.class);
        EasyMock.expect(wireService.createProxy(EasyMock.isA(RuntimeWire.class))).andStubAnswer(new IAnswer() {
            public Object answer() throws Throwable {
                return "foo";
            }
        });
        EasyMock.replay(wireService);
        ComponentConfiguration config = new ComponentConfiguration();
        config.setName("foo");
        config.setScopeContainer(container);
        config.setWireService(wireService);
        ScriptComponent pc = new ScriptComponent(config);
        InboundWire wire = EasyMock.createMock(InboundWire.class);
        EasyMock.expect(wire.getServiceName()).andReturn("foo");
        EasyMock.replay(wire);
        pc.addInboundWire(wire);
        assertEquals("foo", pc.getServiceInstance());
        EasyMock.verify(wireService);
    }

    public void testGetServiceInstanceFail() {
        ComponentConfiguration config = new ComponentConfiguration();
        config.setName("foo");
        config.setScopeContainer(container);
        ScriptComponent pc = new ScriptComponent(config);
        try {
            pc.getServiceInstance();
            fail();
        } catch (TargetException e) {
            // expected
        }
    }

    public void testGetServiceInterfaces() {
        List<Class<?>> services = new ArrayList<Class<?>>();
        ComponentConfiguration config = new ComponentConfiguration();
        config.setName("foo");
        config.setScopeContainer(container);
        config.setServices(services);
        ScriptComponent pc = new ScriptComponent(config);
        assertEquals(services, pc.getServiceInterfaces());
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        container = EasyMock.createMock(ScopeContainer.class);
        EasyMock.expect(container.getScope()).andReturn(Scope.MODULE);
        EasyMock.replay(container);
    }

    private class Contract<T> extends ServiceContract<T> {

        public Contract(Class interfaceClass) {
            super(interfaceClass);
        }
    }

// TODO commented out the following test since it doesn't test refernences.
// TODO have a reference injeciton test in ScriptInstanceFactory that tests an actual invocation
//
//    @SuppressWarnings("unchecked")
//    public void testCreateInstanceWithRef() throws IOException {
//        WireService wireService = createMock(WireService.class);
//        expect(wireService.createProxy(isA(RuntimeWire.class))).andStubAnswer(new IAnswer() {
//            public Object answer() throws Throwable {
//                return Scope.MODULE;
//            }
//        });
//
//        ScriptComponent pc = new ScriptComponent(null, createBSFEasy(), new HashMap(), null, null,
//            scopeContainer, wireService, null, null);
//        OutboundWire wire = EasyMock.createMock(OutboundWire.class);
//        EasyMock.expect(wire.getReferenceName()).andReturn("foo").atLeastOnce();
//        EasyMock.replay(wire);
//        pc.addOutboundWire(wire);
//        Object o = pc.createInstance();
//        assertNotNull(o);
//        assertTrue(o instanceof ScriptInstance);
//    }
//


}
