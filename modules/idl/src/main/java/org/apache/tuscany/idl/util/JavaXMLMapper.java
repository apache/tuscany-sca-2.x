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

package org.apache.tuscany.idl.util;

import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

public final class JavaXMLMapper {
    private static final Map<Class, QName> JAVA2XML = new HashMap<Class, QName>();

    private static final String URI_2001_SCHEMA_XSD = "http://www.w3.org/2001/XMLSchema";

    private static final Map<String, Class> XML2JAVA = new HashMap<String, Class>();

    private JavaXMLMapper() {
    }

    static {
        JAVA2XML.put(boolean.class, getTypeName("boolean"));
        JAVA2XML.put(byte.class, getTypeName("byte"));
        JAVA2XML.put(short.class, getTypeName("short"));
        JAVA2XML.put(int.class, getTypeName("int"));
        JAVA2XML.put(long.class, getTypeName("long"));
        JAVA2XML.put(float.class, getTypeName("float"));
        JAVA2XML.put(double.class, getTypeName("double"));
        JAVA2XML.put(Boolean.class, getTypeName("boolean"));
        JAVA2XML.put(Byte.class, getTypeName("byte"));
        JAVA2XML.put(Short.class, getTypeName("short"));
        JAVA2XML.put(Integer.class, getTypeName("int"));
        JAVA2XML.put(Long.class, getTypeName("long"));
        JAVA2XML.put(Float.class, getTypeName("float"));
        JAVA2XML.put(Double.class, getTypeName("double"));
        JAVA2XML.put(java.lang.String.class, getTypeName("string"));
        JAVA2XML.put(java.math.BigInteger.class, getTypeName("integer"));
        JAVA2XML.put(java.math.BigDecimal.class, getTypeName("decimal"));
        JAVA2XML.put(java.util.Calendar.class, getTypeName("dateTime"));
        JAVA2XML.put(java.util.Date.class, getTypeName("dateTime"));
        JAVA2XML.put(javax.xml.namespace.QName.class, getTypeName("QName"));
        JAVA2XML.put(java.net.URI.class, getTypeName("string"));
        JAVA2XML.put(javax.xml.datatype.XMLGregorianCalendar.class, getTypeName("anySimpleType"));
        JAVA2XML.put(javax.xml.datatype.Duration.class, getTypeName("duration"));
        JAVA2XML.put(java.lang.Object.class, getTypeName("anyType"));
        JAVA2XML.put(java.awt.Image.class, getTypeName("base64Binary"));
        JAVA2XML.put(byte[].class, getTypeName("base64Binary"));
        // java2XSD.put(javax.activation.DataHandler.class, getTypeName("base64Binary"));
        JAVA2XML.put(javax.xml.transform.Source.class, getTypeName("base64Binary"));
        JAVA2XML.put(java.util.UUID.class, getTypeName("string"));
    }

    static {
        XML2JAVA.put("string", java.lang.String.class);
        XML2JAVA.put("integer", java.math.BigInteger.class);
        XML2JAVA.put("int", int.class);
        XML2JAVA.put("long", long.class);
        XML2JAVA.put("short", short.class);
        XML2JAVA.put("decimal", java.math.BigDecimal.class);
        XML2JAVA.put("float", float.class);
        XML2JAVA.put("double", double.class);
        XML2JAVA.put("boolean", boolean.class);
        XML2JAVA.put("byte", byte.class);
        XML2JAVA.put("QName", javax.xml.namespace.QName.class);
        XML2JAVA.put("dateTime", javax.xml.datatype.XMLGregorianCalendar.class);
        XML2JAVA.put("base64Binary", byte[].class);
        XML2JAVA.put("hexBinary", byte[].class);
        XML2JAVA.put("unsignedInt", long.class);
        XML2JAVA.put("unsignedShort", int.class);
        XML2JAVA.put("unsignedByte", short.class);
        XML2JAVA.put("time", javax.xml.datatype.XMLGregorianCalendar.class);
        XML2JAVA.put("date", javax.xml.datatype.XMLGregorianCalendar.class);
        XML2JAVA.put("gDay", javax.xml.datatype.XMLGregorianCalendar.class);
        XML2JAVA.put("gMonth", javax.xml.datatype.XMLGregorianCalendar.class);
        XML2JAVA.put("gYear", javax.xml.datatype.XMLGregorianCalendar.class);
        XML2JAVA.put("gYearMonth", javax.xml.datatype.XMLGregorianCalendar.class);
        XML2JAVA.put("gMonthDay", javax.xml.datatype.XMLGregorianCalendar.class);
        XML2JAVA.put("anySimpleType", java.lang.Object.class); // For elements
        // XML2JAVA.put("anySimpleType", java.lang.String.class); // For
        // attributes
        XML2JAVA.put("duration", javax.xml.datatype.Duration.class);
        XML2JAVA.put("NOTATION", javax.xml.namespace.QName.class);
    }

    public static Class getJavaType(QName xmlType) {
        if (URI_2001_SCHEMA_XSD.equals(xmlType.getNamespaceURI())) {
            return XML2JAVA.get(xmlType.getLocalPart());
        } else {
            return null;
        }
    }

    private static QName getTypeName(String name) {
        return new QName(URI_2001_SCHEMA_XSD, name);
    }

    public static QName getXMLType(Class javaType) {
        return JAVA2XML.get(javaType);
    }

}
