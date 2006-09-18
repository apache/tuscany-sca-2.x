/**
 *
 * Copyright 2006 The Apache Software Foundation or its licensors as applicable
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

package org.apache.tuscany.databinding.impl;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;

import javax.xml.stream.XMLStreamReader;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.apache.tuscany.databinding.DataBinding;
import org.apache.tuscany.databinding.DataBindingRegistry;
import org.apache.tuscany.spi.model.DataType;
import org.easymock.EasyMock;
import org.xml.sax.ContentHandler;

/**
 * 
 */
public class DataBindingRegistryImplTestCase extends TestCase {
    private DataBindingRegistry registry;

    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        registry = new DataBindingRegistryImpl();
    }

    public void testRegistry() {
        DataBinding db1 = createMock(DataBinding.class);
        expect(db1.getName()).andReturn(ContentHandler.class.getName()).anyTimes();
        DataType<Class> dataType1 = new DataType<Class>(ContentHandler.class, ContentHandler.class);
        expect(db1.introspect(ContentHandler.class)).andReturn(dataType1);
        expect(db1.introspect((Class) EasyMock.anyObject())).andReturn(null).anyTimes();
        replay(db1);

        registry.register(db1);

        DataBinding db2 = createMock(DataBinding.class);
        expect(db2.getName()).andReturn(XMLStreamReader.class.getName()).anyTimes();
        DataType<Class> dataType2 = new DataType<Class>(XMLStreamReader.class, XMLStreamReader.class);
        expect(db2.introspect(XMLStreamReader.class)).andReturn(dataType2);
        expect(db2.introspect((Class) EasyMock.anyObject())).andReturn(null).anyTimes();
        replay(db2);

        registry.register(db2);

        String name = db1.getName();
        DataBinding db3 = registry.getDataBinding(name);
        Assert.assertTrue(db1 == db3);

        DataType<?> dt = registry.introspectType(ContentHandler.class);
        Assert.assertEquals(dataType1, dt);
        Assert.assertTrue(dt.getDataBinding().equalsIgnoreCase(name));

        registry.unregister(name);
        DataBinding db4 = registry.getDataBinding(name);
        Assert.assertNull(db4);

        dt = registry.introspectType(ContentHandler.class);
        Assert.assertNull(dt);

    }

}
