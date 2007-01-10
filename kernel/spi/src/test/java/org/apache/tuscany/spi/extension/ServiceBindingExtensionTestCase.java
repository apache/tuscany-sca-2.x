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
package org.apache.tuscany.spi.extension;

import javax.xml.namespace.QName;

import org.apache.tuscany.spi.component.Service;
import org.apache.tuscany.spi.model.Scope;

import junit.framework.TestCase;
import org.easymock.EasyMock;

/**
 * @version $Rev$ $Date$
 */
public class ServiceBindingExtensionTestCase extends TestCase {

    public void testScope() throws Exception {
        ServiceBindingExtension binding = new ServiceBindingExtension(null, null) {
            public QName getBindingType() {
                return null;
            }
        };
        assertEquals(Scope.SYSTEM, binding.getScope());
    }

    public void testPrepare() throws Exception {
        ServiceBindingExtension binding = new ServiceBindingExtension(null, null) {
            public QName getBindingType() {
                return null;
            }
        };
        binding.prepare();
    }

    public void testIsSystemNoParent() throws Exception {
        ServiceBindingExtension binding = new ServiceBindingExtension(null, null) {
            public QName getBindingType() {
                return null;
            }
        };
        assertFalse(binding.isSystem());
    }

    public void testIsSystem() throws Exception {
        Service service = EasyMock.createMock(Service.class);
        EasyMock.expect(service.isSystem()).andReturn(true);
        EasyMock.replay(service);
        ServiceBindingExtension binding = new ServiceBindingExtension(null, null) {
            public QName getBindingType() {
                return null;
            }
        };
        binding.setService(service);
        assertTrue(binding.isSystem());
    }

    public void testIsNotSystem() throws Exception {
        Service service = EasyMock.createMock(Service.class);
        EasyMock.expect(service.isSystem()).andReturn(false);
        EasyMock.replay(service);
        ServiceBindingExtension binding = new ServiceBindingExtension(null, null) {
            public QName getBindingType() {
                return null;
            }
        };
        binding.setService(service);
        assertFalse(binding.isSystem());
    }

}
