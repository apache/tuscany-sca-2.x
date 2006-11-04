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

import java.util.HashMap;
import java.util.Map;

import org.apache.tuscany.spi.component.ScopeContainer;
import org.apache.tuscany.spi.idl.java.JavaServiceContract;
import org.apache.tuscany.spi.model.Operation;
import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.spi.wire.InboundInvocationChain;
import org.apache.tuscany.spi.wire.InboundWire;

import junit.framework.TestCase;
import org.apache.tuscany.core.implementation.PojoConfiguration;
import org.apache.tuscany.core.implementation.java.mock.components.Target;
import org.apache.tuscany.core.implementation.java.mock.components.TargetImpl;
import org.apache.tuscany.core.injection.PojoObjectFactory;
import org.apache.tuscany.core.wire.jdk.JDKWireService;
import org.easymock.EasyMock;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;

/**
 * @version $$Rev$$ $$Date$$
 */
public class GetServiceByNameTestCase extends TestCase {

    public void testServiceLocate() throws Exception {
        ScopeContainer scope = createMock(ScopeContainer.class);
        scope.register(EasyMock.isA(JavaAtomicComponent.class));
        expect(scope.getScope()).andReturn(Scope.MODULE);
        replay(scope);
        PojoConfiguration configuration = new PojoConfiguration();
        configuration.setScopeContainer(scope);
        configuration.setInstanceFactory(new PojoObjectFactory<TargetImpl>(TargetImpl.class.getConstructor()));
        configuration.addServiceInterface(Target.class);
        configuration.setWireService(new JDKWireService());
        configuration.setName("target");
        final JavaAtomicComponent component = new JavaAtomicComponent(configuration);

        InboundWire wire = createMock(InboundWire.class);

        JavaServiceContract contract = new JavaServiceContract(Target.class);
        EasyMock.expect(wire.getServiceContract()).andReturn(contract).anyTimes();
        expect(wire.getServiceName()).andReturn("Target");
        Map<Operation<?>, InboundInvocationChain> chains = new HashMap<Operation<?>, InboundInvocationChain>();
        expect(wire.getInvocationChains()).andReturn(chains);
        expect(wire.getCallbackReferenceName()).andReturn(null);
        replay(wire);
        component.addInboundWire(wire);
        component.prepare();
        component.start();
        assertTrue(component.getServiceInstance("Target") instanceof Target);
    }
}
