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
package org.apache.tuscany.spi.util.stax;

import java.io.InputStream;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import junit.framework.TestCase;

/**
 * Test case for StaxHelper
 * 
 * @version $Revision$ $Date$
 *
 */
public class StaxUtilTestCase extends TestCase {

    public StaxUtilTestCase(String name) {
        super(name);
    }

    public void testSerialize() throws XMLStreamException {
        
        InputStream in = getClass().getResourceAsStream("test.composite");
        XMLStreamReader reader = StaxUtil.createReader(in);
        StaxUtil.serialize(reader);
        // TODO Do assertions
    }

    public void testGetDocumentElementQName() throws XMLStreamException {
        InputStream in = getClass().getResourceAsStream("test.composite");
        XMLStreamReader reader = StaxUtil.createReader(in);
        String xml = StaxUtil.serialize(reader);
        QName qname = StaxUtil.getDocumentElementQName(xml);
        assertEquals("http://www.osoa.org/xmlns/sca/1.0", qname.getNamespaceURI());
        assertEquals("composite", qname.getLocalPart());
    }

}
