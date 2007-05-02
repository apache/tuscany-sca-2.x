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

import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.NamespaceContext;

import junit.framework.TestCase;

import org.apache.tuscany.databinding.TransformationContext;
import org.apache.tuscany.databinding.impl.SimpleTypeMapperImpl;
import org.apache.tuscany.interfacedef.util.TypeInfo;
import org.easymock.EasyMock;

/**
 * 
 */
public class SimpleTypeMapperExtensionTestCase extends TestCase {

    private static final Map<String, Object> SAMPLE_VALUES = new HashMap<String, Object>();

    static {
        SAMPLE_VALUES.put("anyURI", "http://www.w3.com");
        SAMPLE_VALUES.put("boolean", new String[] {"true", "false", "1", "0"});
        SAMPLE_VALUES.put("byte", new String[] {"-128", "127"});
        SAMPLE_VALUES.put("date", new String[] {"2004-03-15", "2002-09-24-06:00"});
        SAMPLE_VALUES.put("dateTime", "2003-12-25T08:30:00");
        SAMPLE_VALUES.put("decimal", "3.1415292");
        SAMPLE_VALUES.put("double", new String[] {"3.1415292", "INF", "NaN"});
        SAMPLE_VALUES.put("duration", new String[] {"P8M3DT7H33M2S", "P5Y2M10DT15H"});
        SAMPLE_VALUES.put("float", new String[] {"3.1415292", "INF", "NaN"});
        SAMPLE_VALUES.put("gDay", "---11");
        SAMPLE_VALUES.put("gMonth", "--02--");
        SAMPLE_VALUES.put("gMonthDay", "--02-14");
        SAMPLE_VALUES.put("gYear", "1999");
        SAMPLE_VALUES.put("gYearMonth", "1972-08");
        SAMPLE_VALUES.put("ID", "id-102");
        SAMPLE_VALUES.put("IDREF", "id-102");
        SAMPLE_VALUES.put("IDREFS", "id-102 id-103 id-100");
        SAMPLE_VALUES.put("int", "77");
        SAMPLE_VALUES.put("integer", "77");
        SAMPLE_VALUES.put("long", "214");
        SAMPLE_VALUES.put("negativeInteger", "-123");
        SAMPLE_VALUES.put("nonNegativeInteger", "2");
        SAMPLE_VALUES.put("nonPositiveInteger", "0");
        SAMPLE_VALUES.put("positiveInteger", "500");
        SAMPLE_VALUES.put("short", "476");
        SAMPLE_VALUES.put("string", "Joeseph");
        SAMPLE_VALUES.put("time", "13:02:00");
        SAMPLE_VALUES.put("base64Binary", "TWFu");
        SAMPLE_VALUES.put("hexBinary", "2CDB5F");
        SAMPLE_VALUES.put("QName", "f:foo");
        SAMPLE_VALUES.put("NOTATION", "f:bar");
    }

    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
    }

    public void testMap() throws Exception {
        SimpleTypeMapperImpl extension = new SimpleTypeMapperImpl();
        TransformationContext context = EasyMock.createMock(TransformationContext.class);
        Map<String, Object> metaData = new HashMap<String, Object>();
        EasyMock.expect(context.getMetadata()).andReturn(metaData).anyTimes();
        EasyMock.replay(context);

        NamespaceContext namespaceContext = EasyMock.createMock(NamespaceContext.class);
        EasyMock.expect(namespaceContext.getNamespaceURI(EasyMock.eq("f"))).andReturn("http://foo")
            .anyTimes();
        EasyMock.expect(namespaceContext.getPrefix(EasyMock.eq("http://foo"))).andReturn("f").anyTimes();
        EasyMock.replay(namespaceContext);
        context.getMetadata().put(NamespaceContext.class.getName(), namespaceContext);
        for (TypeInfo simpleType : SimpleTypeMapperImpl.XSD_SIMPLE_TYPES.values()) {
            String name = simpleType.getQName().getLocalPart();
            Object value = SAMPLE_VALUES.get(name);
            if (value instanceof String[]) {
                for (String s : (String[])value) {
                    Object obj = extension.toJavaObject(simpleType.getQName(), s, context);
                    String str = extension.toXMLLiteral(simpleType.getQName(), obj, context);
                    assertNotNull(str);
                    // assertTrue("[" + name + "] " + s + " " + str,
                    // str.contains((String) s));
                }
            } else if (value instanceof String) {
                Object obj = extension.toJavaObject(simpleType.getQName(), (String)value, context);
                String str = extension.toXMLLiteral(simpleType.getQName(), obj, context);
                assertNotNull(str);
                // assertTrue("[" + name + "] " + value + " " + str,
                // str.contains((String) value));
            }
        }
    }
}
