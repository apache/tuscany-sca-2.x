/**
 *
 * Copyright 2006 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.tuscany.binding.rmi;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import junit.framework.TestCase;

import org.apache.tuscany.spi.model.BoundReferenceDefinition;
import org.apache.tuscany.spi.model.ServiceContract;

public class RMIBindingBuilderTestCase extends TestCase {
    
    public void testGetBindingType() {
        assertEquals(RMIBindingDefinition.class, new RMIBindingBuilder(null).getBindingType());
    }

    @SuppressWarnings("unchecked")
    public void testBuildService() {
        RMIBindingBuilder builder = new RMIBindingBuilder(null);
        BoundReferenceDefinition<RMIBindingDefinition> def = createMock(BoundReferenceDefinition.class);
        expect(def.getName()).andReturn("petra");
        RMIBindingDefinition binding = new RMIBindingDefinition();
        expect(def.getBinding()).andReturn(binding );
        expect(def.getBinding()).andReturn(binding );
        expect(def.getBinding()).andReturn(binding );
        ServiceContract sc = createMock(ServiceContract.class);
        expect(def.getServiceContract()).andReturn(sc);
        replay(def);
        Object ref = builder.build(null, def, null);
        assertTrue(ref instanceof RMIReference);
    }
}
