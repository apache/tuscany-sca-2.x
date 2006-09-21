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

import org.apache.ws.commons.schema.XmlSchemaSimpleType;

/**
 * 
 */
public class SimpleTypeMapperExtensionTestCase extends TestCase {

    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
    }

    public void testMap() throws Exception {
        SimpleTypeMapperExtension extension = new SimpleTypeMapperExtension();
        for (XmlSchemaSimpleType simpleType : SimpleTypeMapperExtension.XSD_SIMPLE_TYPES.values()) {
            if (simpleType != null) {
                String name = simpleType.getName();
                String value = "12";
                if (name.equals("boolean")) {
                    value = "true";
                } else if (name.equals("QName") || name.endsWith("NOTATION")) {
                    value = "f:foo";
                    continue;
                } else if (name.equals("base64Binary")) {
                    value = "TWFu";
                } else if (name.equals("date")) {
                    value = "2002-09-24-06:00";
                } else if (name.equals("time")) {
                    value = "09:30:10+06:00";
                } else if (name.equals("dateTime")) {
                    value = "2002-05-30T09:30:10+06:00";
                } else if (name.equals("gYear")) {
                    value = "2001+00:00";
                } else if (name.equals("gYearMonth")) {
                    value = "2001-10+02:00";
                } else if (name.equals("gMonthDay")) {
                    value = "--11-01+02:00";
                } else if (name.equals("gMonth")) {
                    value = "--11+02:00";
                } else if (name.equals("gDay")) {
                    value = "---01+02:00";
                } else if (name.equals("duration")) {
                    value = "P5Y2M10DT15H";
                }
                Object obj = extension.toJavaObject(simpleType, value, null);
                extension.toXMLLiteral(simpleType, obj, null);
                // assertTrue("[" + type + "] " + str + " " + value, str.contains(value));
            }
        }
    }
}
