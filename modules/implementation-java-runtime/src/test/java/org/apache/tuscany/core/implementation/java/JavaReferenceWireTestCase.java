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
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.apache.tuscany.core.implementation.PojoConfiguration;
import org.apache.tuscany.core.injection.PojoObjectFactory;
import org.apache.tuscany.interfacedef.Operation;
import org.apache.tuscany.spi.Scope;
import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.InstanceWrapper;
import org.apache.tuscany.spi.component.ScopeContainer;
import org.apache.tuscany.spi.component.TargetException;
import org.apache.tuscany.spi.wire.InvocationChain;
import org.apache.tuscany.spi.wire.ProxyService;
import org.apache.tuscany.spi.wire.Wire;
import org.easymock.EasyMock;
import org.easymock.IAnswer;

/**
 * Validates wiring from a Java atomic context
 *
 * @version $$Rev$$ $$Date$$
 */
public class JavaReferenceWireTestCase extends TestCase {

    @SuppressWarnings({"unchecked"})
    public void testReferenceSet() throws Exception {
        ScopeContainer scope = createMock();
        scope.start();
        final Target target = new TargetImpl();
        PojoConfiguration configuration = new PojoConfiguration();
        configuration.addReferenceSite("target", SourceImpl.class.getMethod("setTarget", Target.class));
        Constructor<SourceImpl> ctr = SourceImpl.class.getConstructor();
        configuration.setInstanceFactory(new PojoObjectFactory<SourceImpl>(ctr));
        Wire wire = EasyMock.createMock(Wire.class);
        wire.getInvocationChains();
        EasyMock.expectLastCall().andReturn(new HashMap<Operation, InvocationChain>()).atLeastOnce();
        URI uri = URI.create("#target");
        EasyMock.expect(wire.getSourceUri()).andReturn(uri).atLeastOnce();
        EasyMock.expect(wire.isOptimizable()).andReturn(false);
        EasyMock.replay(wire);
        ProxyService service = EasyMock.createMock(ProxyService.class);
        EasyMock.expect(service.createProxy(EasyMock.eq(Target.class), EasyMock.eq(wire), EasyMock.isA(Map.class)))
            .andAnswer(new IAnswer<Target>() {
                public Target answer() throws Throwable {
                    Wire wire = (Wire) EasyMock.getCurrentArguments()[1];
                    wire.getInvocationChains();
                    return target;
                }

            }).atLeastOnce();
        EasyMock.replay(service);
        configuration.setProxyService(service);
        configuration.setName(new URI("source"));
        configuration.setGroupId(URI.create("composite"));
        JavaAtomicComponent component = new JavaAtomicComponent(configuration);
        component.setScopeContainer(scope);
        component.attachWire(wire);
        component.start();
        Source source = (Source) component.getTargetInstance();
        assertSame(target, source.getTarget());
        scope.stop();
        EasyMock.verify(wire);
        EasyMock.verify(scope);
        EasyMock.verify(service);
    }

    private ScopeContainer createMock() throws TargetException {
        ScopeContainer scope = EasyMock.createMock(ScopeContainer.class);
        scope.start();
        scope.stop();
        scope.register(EasyMock.isA(AtomicComponent.class), EasyMock.eq(URI.create("composite")));
        EasyMock.expectLastCall().atLeastOnce();
        EasyMock.expect(scope.getScope()).andReturn(Scope.COMPOSITE).atLeastOnce();
        scope.getWrapper(EasyMock.isA(AtomicComponent.class), EasyMock.eq(URI.create("composite")));
        EasyMock.expectLastCall().andAnswer(new IAnswer<Object>() {
            private Map<AtomicComponent, InstanceWrapper> cache = new HashMap<AtomicComponent, InstanceWrapper>();

            public Object answer() throws Throwable {
                AtomicComponent component = (AtomicComponent) EasyMock.getCurrentArguments()[0];
                InstanceWrapper instance = cache.get(component);
                if (instance == null) {
                    instance = component.createInstanceWrapper();
                    cache.put(component, instance);
                }
                return instance;
            }
        }).anyTimes();
        EasyMock.replay(scope);
        return scope;
    }

    private interface Source {
        Target getTarget();
    }

    private static class SourceImpl implements Source {
        private Target target;

        public SourceImpl() {
        }

        public Target getTarget() {
            return target;
        }

        public void setTarget(Target target) {
            this.target = target;
        }
    }

    private interface Target {

        String getString();

        void setString(String val);
    }

    private static class TargetImpl implements Target {
        private String string;

        public TargetImpl() {
        }

        public String getString() {
            return string;
        }

        public void setString(String string) {
            this.string = string;
        }

    }
}
