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
package org.apache.tuscany.core.implementation.java;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.ScopeContainer;
import org.apache.tuscany.spi.model.Operation;
import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.spi.wire.OutboundInvocationChain;
import org.apache.tuscany.spi.wire.OutboundWire;
import org.apache.tuscany.spi.wire.WireService;

import junit.framework.TestCase;
import org.apache.tuscany.core.implementation.PojoConfiguration;
import org.apache.tuscany.core.implementation.java.mock.components.Source;
import org.apache.tuscany.core.implementation.java.mock.components.SourceImpl;
import org.apache.tuscany.core.implementation.java.mock.components.Target;
import org.apache.tuscany.core.implementation.java.mock.components.TargetImpl;
import org.apache.tuscany.core.injection.PojoObjectFactory;
import org.easymock.EasyMock;
import org.easymock.IAnswer;

/**
 * Validates wiring from a Java atomic context
 *
 * @version $$Rev$$ $$Date$$
 */
public class JavaReferenceWireTestCase extends TestCase {

    public void testReferenceSet() throws Exception {
        ScopeContainer scope = createMock();
        scope.start();
        final Target target = new TargetImpl();
        PojoConfiguration configuration = new PojoConfiguration();
        configuration.addReferenceSite("target", SourceImpl.class.getMethod("setTarget", Target.class));
        configuration.addServiceInterface(Source.class);
        Constructor<SourceImpl> ctr = SourceImpl.class.getConstructor();
        configuration.setInstanceFactory(new PojoObjectFactory<SourceImpl>(ctr));
        configuration.setScopeContainer(scope);
        OutboundWire wire = EasyMock.createMock(OutboundWire.class);
        wire.getInvocationChains();
        EasyMock.expectLastCall().andReturn(new HashMap<Operation<?>, OutboundInvocationChain>());
        EasyMock.expect(wire.getReferenceName()).andReturn("target").atLeastOnce();
        EasyMock.replay(wire);
        WireService service = EasyMock.createMock(WireService.class);
        EasyMock.expect(service.createProxy(EasyMock.eq(wire))).andAnswer(new IAnswer<Object>() {
            public Object answer() throws Throwable {
                OutboundWire wire = (OutboundWire) EasyMock.getCurrentArguments()[0];
                wire.getInvocationChains();
                return target;
            }

        }).atLeastOnce();
        EasyMock.replay(service);
        configuration.setWireService(service);
        JavaAtomicComponent sourceContext = new JavaAtomicComponent("source", configuration);
        sourceContext.addOutboundWire(wire);
        sourceContext.start();
        Source source = (Source) sourceContext.getServiceInstance();
        assertSame(target, source.getTarget());
        scope.stop();
        EasyMock.verify(wire);
        EasyMock.verify(scope);
        EasyMock.verify(service);
    }

    private ScopeContainer createMock() {
        ScopeContainer scope = EasyMock.createMock(ScopeContainer.class);
        scope.start();
        scope.stop();
        scope.register(EasyMock.isA(AtomicComponent.class));
        EasyMock.expectLastCall().atLeastOnce();
        EasyMock.expect(scope.getScope()).andReturn(Scope.MODULE).atLeastOnce();
        scope.getInstance(EasyMock.isA(AtomicComponent.class));
        EasyMock.expectLastCall().andAnswer(new IAnswer<Object>() {
            private Map<AtomicComponent, Object> cache = new HashMap<AtomicComponent, Object>();

            public Object answer() throws Throwable {
                AtomicComponent component = (AtomicComponent) EasyMock.getCurrentArguments()[0];
                Object instance = cache.get(component);
                if (instance == null) {
                    instance = component.createInstance();
                    cache.put(component, instance);
                }
                return instance;
            }
        }).anyTimes();
        EasyMock.replay(scope);
        return scope;
    }
}
