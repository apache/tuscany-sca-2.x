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

package org.apache.tuscany.databinding.extension;

import junit.framework.TestCase;

import org.apache.tuscany.databinding.DataBindingExtensionPoint;
import org.apache.tuscany.databinding.impl.BaseDataBinding;
import org.apache.tuscany.interfacedef.DataType;
import org.apache.tuscany.interfacedef.impl.DataTypeImpl;
import org.easymock.EasyMock;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * 
 */
public class DataBindingExtensionTestCase extends TestCase {

    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
    }

    @SuppressWarnings("unchecked")
    public void testExtension() {
        DataBinding1 binding1 = new DataBinding1(Node.class);
        assertEquals(Node.class.getName(), binding1.getName());
        DataType dt1 = new DataTypeImpl<Class>(Element.class, null);
        assertTrue(binding1.introspect(dt1, null));
        DataType dt2 = new DataTypeImpl<Class>(String.class, null);
        assertFalse(binding1.introspect(dt2, null));
        assertNull(binding1.getWrapperHandler());
        
        DataBindingExtensionPoint registry = EasyMock.createMock(DataBindingExtensionPoint.class);
        registry.addDataBinding(binding1);
        EasyMock.expect(registry.getDataBinding(Node.class.getName())).andReturn(binding1);
        EasyMock.replay(registry);
        
        binding1.setDataBindingRegistry(registry);
        binding1.init();
        assertNotNull(registry.getDataBinding(Node.class.getName()));

        DataBinding1 binding2 = new DataBinding1("dom", Node.class);
        assertEquals("dom", binding2.getName());
    }

    private static class DataBinding1 extends BaseDataBinding {

        /**
         * @param baseType
         */
        public DataBinding1(Class<?> baseType) {
            super(baseType);
        }

        /**
         * @param name
         * @param baseType
         */
        public DataBinding1(String name, Class<?> baseType) {
            super(name, baseType);
        }

    }

}
