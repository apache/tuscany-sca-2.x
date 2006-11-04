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
package org.apache.tuscany.core.implementation.system.wire;

import org.apache.tuscany.spi.QualifiedName;
import org.apache.tuscany.spi.component.SystemAtomicComponent;
import org.apache.tuscany.spi.wire.OutboundWire;

import junit.framework.TestCase;
import org.apache.tuscany.core.component.scope.ModuleScopeContainer;
import org.apache.tuscany.core.implementation.PojoConfiguration;
import org.apache.tuscany.core.implementation.system.component.SystemAtomicComponentImpl;
import org.apache.tuscany.core.injection.PojoObjectFactory;
import org.apache.tuscany.core.mock.component.Source;
import org.apache.tuscany.core.mock.component.SourceImpl;
import org.apache.tuscany.core.mock.component.Target;
import org.apache.tuscany.core.mock.component.TargetImpl;
import org.easymock.EasyMock;

/**
 * Tests wiring from an system atomic context
 *
 * @version $$Rev$$ $$Date$$
 */
public class AtomicComponentWireInvocationTestCase extends TestCase {

    public void testWireResolution() throws NoSuchMethodException {
        ModuleScopeContainer scope = new ModuleScopeContainer(null);
        scope.start();
        Target target = new TargetImpl();
        SystemInboundWire inboundWire = EasyMock.createMock(SystemInboundWire.class);
        EasyMock.expect(inboundWire.getTargetService()).andReturn(target);
        EasyMock.replay(inboundWire);

        PojoConfiguration configuration = new PojoConfiguration();
        configuration.setScopeContainer(scope);
        configuration.addReferenceSite("setTarget", SourceImpl.class.getMethod("setTarget", Target.class));
        configuration.addServiceInterface(Source.class);
        configuration.setInstanceFactory(new PojoObjectFactory<SourceImpl>(SourceImpl.class.getConstructor()));
        configuration.setName("source");
        SystemAtomicComponent sourceContext = new SystemAtomicComponentImpl(configuration);
        QualifiedName qName = new QualifiedName("service");
        OutboundWire outboundWire = new SystemOutboundWireImpl("setTarget", qName, Target.class);
        outboundWire.setTargetWire(inboundWire);
        sourceContext.addOutboundWire(outboundWire);
        sourceContext.start();
        assertSame(((Source) sourceContext.getServiceInstance()).getTarget(), target);
        // wires should pass back direct ref
        EasyMock.verify(inboundWire);
    }
}
