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

import junit.framework.Assert;
import junit.framework.TestCase;

import org.apache.tuscany.databinding.DataBinding;
import org.apache.tuscany.databinding.DataBindingRegistry;
import org.apache.tuscany.databinding.xml.SAXContentHandlerBinding;
import org.apache.tuscany.databinding.xml.XMLStreamReaderBinding;
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
        DataBinding db1 = new SAXContentHandlerBinding();
        String name = db1.getName();
        registry.register(db1);
        registry.register(new XMLStreamReaderBinding());
        DataBinding db2 = registry.getDataBinding(name);
        Assert.assertTrue(db1 == db2);

        DataBinding dt = registry.introspectType(ContentHandler.class);
        Assert.assertEquals(db1, dt);
        Assert.assertTrue(dt.getName().equalsIgnoreCase(name));

        registry.unregister(name);
        DataBinding db3 = registry.getDataBinding(name);
        Assert.assertNull(db3);

        dt = registry.introspectType(ContentHandler.class);
        Assert.assertNull(dt);

    }

}
