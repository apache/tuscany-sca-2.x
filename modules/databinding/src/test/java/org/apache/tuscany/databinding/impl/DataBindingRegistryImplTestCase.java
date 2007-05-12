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
package org.apache.tuscany.databinding.impl;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;

import java.lang.annotation.Annotation;

import javax.xml.stream.XMLStreamReader;

import junit.framework.TestCase;

import org.apache.tuscany.databinding.DataBinding;
import org.apache.tuscany.databinding.DataBindingExtensionPoint;
import org.apache.tuscany.databinding.DefaultDataBindingExtensionPoint;
import org.apache.tuscany.interfacedef.DataType;
import org.apache.tuscany.sca.interfacedef.impl.DataTypeImpl;
import org.easymock.EasyMock;
import org.xml.sax.ContentHandler;

/**
 * 
 */
public class DataBindingRegistryImplTestCase extends TestCase {
    private DataBindingExtensionPoint registry;

    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        registry = new DefaultDataBindingExtensionPoint();
    }

    @SuppressWarnings("unchecked")
    public void testRegistry() {
        DataBinding db1 = createMock(DataBinding.class);
        expect(db1.getAliases()).andReturn(new String[] {"db1"}).anyTimes();
        expect(db1.getName()).andReturn(ContentHandler.class.getName()).anyTimes();
        DataType<Class> dataType1 = new DataTypeImpl<Class>(ContentHandler.class, ContentHandler.class);
        expect(db1.introspect(dataType1, null)).andReturn(true);
        expect(db1.introspect(EasyMock.not(EasyMock.same(dataType1)), (Annotation[])EasyMock.isNull()))
            .andReturn(false).anyTimes();
        replay(db1);

        registry.addDataBinding(db1);

        DataBinding db2 = createMock(DataBinding.class);
        expect(db2.getAliases()).andReturn(new String[] {"db2"}).anyTimes();
        expect(db2.getName()).andReturn(XMLStreamReader.class.getName()).anyTimes();
        DataType<Class> dataType2 = new DataTypeImpl<Class>(XMLStreamReader.class, XMLStreamReader.class);
        expect(db2.introspect(dataType2, null)).andReturn(true);
        expect(db2.introspect(EasyMock.not(EasyMock.same(dataType2)), (Annotation[])EasyMock.isNull()))
            .andReturn(false).anyTimes();
        replay(db2);

        registry.addDataBinding(db2);

        // Lookup by name
        String name = db1.getName();
        DataBinding db3 = registry.getDataBinding(name);
        assertSame(db1, db3);

        // Look up by alias
        DataBinding db5 = registry.getDataBinding("db1");
        assertSame(db1, db5);
        
        DataType dt = new DataTypeImpl<Class>(ContentHandler.class, null);
        registry.introspectType(dt, null);
        assertEquals(dataType1.getLogical(), ContentHandler.class);
        assertTrue(dt.getDataBinding().equalsIgnoreCase("java.lang.Object"));

        registry.removeDataBinding(name);
        DataBinding db4 = registry.getDataBinding(name);
        assertNull(db4);

        dt = new DataTypeImpl<Class>(null, String.class, null);
        registry.introspectType(dt, null);
        assertEquals("java.lang.Object", dt.getDataBinding());
    }

}
