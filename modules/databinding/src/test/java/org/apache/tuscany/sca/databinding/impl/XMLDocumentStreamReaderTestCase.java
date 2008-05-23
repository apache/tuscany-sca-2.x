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

package org.apache.tuscany.sca.databinding.impl;

import java.io.StringReader;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;

import junit.framework.Assert;

import org.apache.tuscany.sca.databinding.xml.XMLDocumentStreamReader;
import org.apache.tuscany.sca.databinding.xml.XMLStreamReader2String;

/**
 * @version $Rev$ $Date$
 */
public class XMLDocumentStreamReaderTestCase {
    private static final String xml = "<e1><e2 a2=\"a2\"><e4>E4</e4></e2><e3 a3=\"a3\"/></e1>";

    @org.junit.Test
    public void testReader() throws Exception {
        XMLInputFactory factory = XMLInputFactory.newInstance();
        XMLStreamReader r1 = factory.createXMLStreamReader(new StringReader(xml));
        XMLDocumentStreamReader r2 = new XMLDocumentStreamReader(r1);
        XMLStreamReader2String t1 = new XMLStreamReader2String();
        String result = t1.transform(r2, null);
        System.out.println(result);
        XMLStreamReader r3 = factory.createXMLStreamReader(new StringReader(xml));
        r3.nextTag();
        r3.nextTag();
        Assert.assertEquals(XMLStreamConstants.START_ELEMENT, r3.getEventType());
        Assert.assertEquals(new QName(null, "e2"), r3.getName());
        XMLDocumentStreamReader r4 = new XMLDocumentStreamReader(r3);
        result = t1.transform(r4, null);
        System.out.println(result);
    }
}
