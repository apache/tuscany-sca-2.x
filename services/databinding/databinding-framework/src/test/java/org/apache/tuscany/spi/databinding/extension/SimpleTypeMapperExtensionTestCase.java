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

package org.apache.tuscany.spi.databinding.extension;

import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.NamespaceContext;

import junit.framework.TestCase;

import org.apache.tuscany.core.databinding.impl.TransformationContextImpl;
import org.apache.tuscany.spi.databinding.TransformationContext;
import org.apache.tuscany.spi.databinding.extension.SimpleTypeMapperExtension;
import org.apache.tuscany.spi.idl.TypeInfo;
import org.easymock.EasyMock;

/**
 * 
 */
public class SimpleTypeMapperExtensionTestCase extends TestCase {

    private static final Map<String, Object> sampleValues = new HashMap<String, Object>();

    static {
        sampleValues.put("anyURI", "http://www.w3.com");
        sampleValues.put("boolean", new String[] { "true", "false", "1", "0" });
        sampleValues.put("byte", new String[] { "-128", "127" });
        sampleValues.put("date", new String[] { "2004-03-15", "2002-09-24-06:00" });
        sampleValues.put("dateTime", "2003-12-25T08:30:00");
        sampleValues.put("decimal", "3.1415292");
        sampleValues.put("double", new String[] { "3.1415292", "INF", "NaN" });
        sampleValues.put("duration", new String[] { "P8M3DT7H33M2S", "P5Y2M10DT15H" });
        sampleValues.put("float", new String[] { "3.1415292", "INF", "NaN" });
        sampleValues.put("gDay", "---11");
        sampleValues.put("gMonth", "--02--");
        sampleValues.put("gMonthDay", "--02-14");
        sampleValues.put("gYear", "1999");
        sampleValues.put("gYearMonth", "1972-08");
        sampleValues.put("ID", "id-102");
        sampleValues.put("IDREF", "id-102");
        sampleValues.put("IDREFS", "id-102 id-103 id-100");
        sampleValues.put("int", "77");
        sampleValues.put("integer", "77");
        sampleValues.put("long", "214");
        sampleValues.put("negativeInteger", "-123");
        sampleValues.put("nonNegativeInteger", "2");
        sampleValues.put("nonPositiveInteger", "0");
        sampleValues.put("positiveInteger", "500");
        sampleValues.put("short", "476");
        sampleValues.put("string", "Joeseph");
        sampleValues.put("time", "13:02:00");
        sampleValues.put("base64Binary", "TWFu");
        sampleValues.put("hexBinary", "2CDB5F");
        sampleValues.put("QName", "f:foo");
        sampleValues.put("NOTATION", "f:bar");
    }

    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
    }

    public void testMap() throws Exception {
        SimpleTypeMapperExtension extension = new SimpleTypeMapperExtension();
        TransformationContext context = new TransformationContextImpl();
        NamespaceContext namespaceContext = EasyMock.createMock(NamespaceContext.class);
        EasyMock.expect(namespaceContext.getNamespaceURI(EasyMock.eq("f"))).andReturn("http://foo").anyTimes();
        EasyMock.expect(namespaceContext.getPrefix(EasyMock.eq("http://foo"))).andReturn("f").anyTimes();
        EasyMock.replay(namespaceContext);
        context.getMetadata().put(NamespaceContext.class, namespaceContext);
        for (TypeInfo simpleType : SimpleTypeMapperExtension.XSD_SIMPLE_TYPES.values()) {
            String name = simpleType.getQName().getLocalPart();
            Object value = sampleValues.get(name);
            if (value instanceof String[]) {
                for (String s : (String[]) value) {
                    Object obj = extension.toJavaObject(simpleType, s, context);
                    String str = extension.toXMLLiteral(simpleType, obj, context);
                    assertNotNull(str);
                    // assertTrue("[" + name + "] " + s + " " + str, str.contains((String) s));
                }
            } else if (value instanceof String) {
                Object obj = extension.toJavaObject(simpleType, (String) value, context);
                String str = extension.toXMLLiteral(simpleType, obj, context);
                assertNotNull(str);
                // assertTrue("[" + name + "] " + value + " " + str, str.contains((String) value));
            }
        }
    }
}
