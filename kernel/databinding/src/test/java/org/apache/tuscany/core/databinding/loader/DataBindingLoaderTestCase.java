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

package org.apache.tuscany.core.databinding.loader;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.apache.tuscany.core.databinding.loader.DataTypeLoader;
import org.apache.tuscany.spi.loader.InvalidValueException;
import org.apache.tuscany.spi.loader.LoaderException;
import org.apache.tuscany.spi.model.DataType;
import org.apache.tuscany.spi.model.ModelObject;
import org.easymock.EasyMock;

/**
 * Testcase for DataBindingLoader
 */
public class DataBindingLoaderTestCase extends TestCase {
    private XMLStreamReader reader;

    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
    }

    public final void testLoad() throws LoaderException, XMLStreamException {
        reader = EasyMock.createMock(XMLStreamReader.class);
        // EasyMock.expect(reader.getEventType()).andReturn(XMLStreamConstants.START_ELEMENT);
        EasyMock.expect(reader.hasNext()).andReturn(true).anyTimes();
        EasyMock.expect(reader.getName()).andReturn(DataTypeLoader.DATA_BINDING);
        EasyMock.expect(reader.getAttributeValue(null, "name")).andReturn("ABC");
        EasyMock.expect(reader.next()).andReturn(XMLStreamConstants.END_ELEMENT);
        EasyMock.replay(reader);

        ModelObject mo = new DataTypeLoader(null).load(null, reader, null);
        Assert.assertTrue(mo instanceof DataType);
        Assert.assertEquals("ABC", ((DataType<?>)mo).getDataBinding());
        EasyMock.verify(reader);

        EasyMock.reset(reader);

        // EasyMock.expect(reader.getEventType()).andReturn(XMLStreamConstants.START_ELEMENT);
        EasyMock.expect(reader.hasNext()).andReturn(true).anyTimes();
        EasyMock.expect(reader.getName()).andReturn(DataTypeLoader.DATA_BINDING);
        EasyMock.expect(reader.getAttributeValue(null, "name")).andReturn(null);
        EasyMock.expect(reader.next()).andReturn(XMLStreamConstants.END_ELEMENT);
        EasyMock.replay(reader);
        try {
            mo = new DataTypeLoader(null).load(null, reader, null);
            Assert.fail("InvalidValueException should have been thrown");
        } catch (InvalidValueException e) {
            Assert.assertTrue(true);
        }
        EasyMock.verify(reader);
    }

}
