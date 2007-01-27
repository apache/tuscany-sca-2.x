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
package org.apache.tuscany.service.discovery.jxta.stax;

import java.io.InputStream;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;

import junit.framework.TestCase;

/**
 * Test case for StaxHelper
 * 
 * @version $Revision$ $Date$
 *
 */
public class StaxHelperTestCase extends TestCase {

    public StaxHelperTestCase(String name) {
        super(name);
    }

    public void testSerialize() throws Exception {
        
        InputStream in = getClass().getClassLoader().getResourceAsStream("test.scdl");
        XMLStreamReader reader = StaxHelper.createReader(in);
        StaxHelper.serialize(reader);
        // TODO Do assertions
    }

    public void testGetDocumentElementQName() {
        InputStream in = getClass().getClassLoader().getResourceAsStream("test.scdl");
        XMLStreamReader reader = StaxHelper.createReader(in);
        String xml = StaxHelper.serialize(reader);
        QName qname = StaxHelper.getDocumentElementQName(xml);
        assertEquals("http://www.osoa.org/xmlns/sca/1.0", qname.getNamespaceURI());
        assertEquals("composite", qname.getLocalPart());
    }

}
