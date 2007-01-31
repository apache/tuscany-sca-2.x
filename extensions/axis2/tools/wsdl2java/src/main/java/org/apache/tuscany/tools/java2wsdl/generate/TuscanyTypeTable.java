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
package org.apache.tuscany.tools.java2wsdl.generate;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMElement;
import org.apache.ws.java2wsdl.Java2WSDLUtils;
import org.codehaus.jam.JClass;

public class TuscanyTypeTable 
{
    public static final String XML_SCHEMA_URI = "http://www.w3.org/2001/XMLSchema";
    public static final String XS_URI_PREFIX = "xs";
    public static final QName XS_QNAME = new QName(XML_SCHEMA_URI, "schema", XS_URI_PREFIX);
    public static final String DELIMITER = "#";
    
    private Hashtable simpleXSDTypes;
    private Hashtable complexXSDTypes;
    
    public static String asQualifiedName(String uri, String typeName)
    {
        return (uri + DELIMITER + typeName);
    }

    
    public  TuscanyTypeTable() 
    {
        simpleXSDTypes = new Hashtable();
        complexXSDTypes = new Hashtable();
        populateSimpleXSDTypes();
        populateStdSDOTypes();
    }

    private void populateStdSDOTypes() 
    {
        simpleXSDTypes.put("Boolean",
                new QName(XML_SCHEMA_URI, "boolean", XS_URI_PREFIX));
        simpleXSDTypes.put("Byte",
                new QName(XML_SCHEMA_URI, "byte", XS_URI_PREFIX));
        simpleXSDTypes.put("Bytes",
                new QName(XML_SCHEMA_URI, "hexBinary", XS_URI_PREFIX));
        simpleXSDTypes.put("Character",
                new QName(XML_SCHEMA_URI, "string", XS_URI_PREFIX));
        simpleXSDTypes.put("DataObject",
                new QName(XML_SCHEMA_URI, "anyType", XS_URI_PREFIX));
        simpleXSDTypes.put("Date",
                new QName(XML_SCHEMA_URI, "dateTime", XS_URI_PREFIX));
        simpleXSDTypes.put("Day",
                new QName(XML_SCHEMA_URI, "gDay", XS_URI_PREFIX));
        simpleXSDTypes.put("Decimal",
                new QName(XML_SCHEMA_URI, "decimal", XS_URI_PREFIX));
        simpleXSDTypes.put("Double",
                new QName(XML_SCHEMA_URI, "double", XS_URI_PREFIX));
        simpleXSDTypes.put("Duration",
                new QName(XML_SCHEMA_URI, "duration", XS_URI_PREFIX));
        simpleXSDTypes.put("Float",
                new QName(XML_SCHEMA_URI, "float", XS_URI_PREFIX));
        simpleXSDTypes.put("Int",
                new QName(XML_SCHEMA_URI, "int", XS_URI_PREFIX));
        simpleXSDTypes.put("Integer",
                new QName(XML_SCHEMA_URI, "integer", XS_URI_PREFIX));
        simpleXSDTypes.put("Long",
                new QName(XML_SCHEMA_URI, "long", XS_URI_PREFIX));
        simpleXSDTypes.put("Month",
                new QName(XML_SCHEMA_URI, "gMonth", XS_URI_PREFIX));
        simpleXSDTypes.put("monthDay",
                new QName(XML_SCHEMA_URI, "gMonthDay", XS_URI_PREFIX));
        simpleXSDTypes.put("Object",
                new QName(XML_SCHEMA_URI, "anySimpleType", XS_URI_PREFIX));
        simpleXSDTypes.put("Short",
                new QName(XML_SCHEMA_URI, "short", XS_URI_PREFIX));
        simpleXSDTypes.put("String",
                new QName(XML_SCHEMA_URI, "string", XS_URI_PREFIX));
        simpleXSDTypes.put("Strings",
                new QName(XML_SCHEMA_URI, "string", XS_URI_PREFIX));
        simpleXSDTypes.put("Time",
                new QName(XML_SCHEMA_URI, "time", XS_URI_PREFIX));
        simpleXSDTypes.put("Year",
                new QName(XML_SCHEMA_URI, "gYear", XS_URI_PREFIX));
        simpleXSDTypes.put("YearMonth",
                new QName(XML_SCHEMA_URI, "gYearMonth", XS_URI_PREFIX));
        simpleXSDTypes.put("YearMonthDay",
                new QName(XML_SCHEMA_URI, "date", XS_URI_PREFIX));
        simpleXSDTypes.put("URI",
                new QName(XML_SCHEMA_URI, "anyURI", XS_URI_PREFIX));
    }
    
    private void populateSimpleXSDTypes() {
        //todo pls use the types from org.apache.ws.commons.schema.constants.Constants
        simpleXSDTypes.put("int",
                new QName(XML_SCHEMA_URI, "int", XS_URI_PREFIX));
        simpleXSDTypes.put("java.lang.String",
                new QName(XML_SCHEMA_URI, "string", XS_URI_PREFIX));
        simpleXSDTypes.put("boolean",
                new QName(XML_SCHEMA_URI, "boolean", XS_URI_PREFIX));
        simpleXSDTypes.put("float",
                new QName(XML_SCHEMA_URI, "float", XS_URI_PREFIX));
        simpleXSDTypes.put("double",
                new QName(XML_SCHEMA_URI, "double", XS_URI_PREFIX));
        simpleXSDTypes.put("short",
                new QName(XML_SCHEMA_URI, "short", XS_URI_PREFIX));
        simpleXSDTypes.put("long",
                new QName(XML_SCHEMA_URI, "long", XS_URI_PREFIX));
        simpleXSDTypes.put("byte",
                new QName(XML_SCHEMA_URI, "byte", XS_URI_PREFIX));
        simpleXSDTypes.put("char",
                new QName(XML_SCHEMA_URI, "anyType", XS_URI_PREFIX));
        simpleXSDTypes.put("java.lang.Integer",
                new QName(XML_SCHEMA_URI, "int", XS_URI_PREFIX));
        simpleXSDTypes.put("java.lang.Double",
                new QName(XML_SCHEMA_URI, "double", XS_URI_PREFIX));
        simpleXSDTypes.put("java.lang.Float",
                new QName(XML_SCHEMA_URI, "float", XS_URI_PREFIX));
        simpleXSDTypes.put("java.lang.Long",
                new QName(XML_SCHEMA_URI, "long", XS_URI_PREFIX));
        simpleXSDTypes.put("java.lang.Character",
                new QName(XML_SCHEMA_URI, "anyType", XS_URI_PREFIX));
        simpleXSDTypes.put("java.lang.Boolean",
                new QName(XML_SCHEMA_URI, "boolean", XS_URI_PREFIX));
        simpleXSDTypes.put("java.lang.Byte",
                new QName(XML_SCHEMA_URI, "byte", XS_URI_PREFIX));
        simpleXSDTypes.put("java.lang.Short",
                new QName(XML_SCHEMA_URI, "short", XS_URI_PREFIX));
        simpleXSDTypes.put("java.util.Date",
                new QName(XML_SCHEMA_URI, "dateTime", XS_URI_PREFIX));
        simpleXSDTypes.put("java.util.Calendar",
                new QName(XML_SCHEMA_URI, "dateTime", XS_URI_PREFIX));
        simpleXSDTypes.put("java.lang.Object",
                new QName(XML_SCHEMA_URI, "anyType", XS_URI_PREFIX));
        simpleXSDTypes.put("java.math.BigDecimal",
                new QName(XML_SCHEMA_URI, "decimal", XS_URI_PREFIX));

        // Any types
        simpleXSDTypes.put(OMElement.class.getName(),
                new QName(XML_SCHEMA_URI, "anyType", XS_URI_PREFIX));
        simpleXSDTypes.put(ArrayList.class.getName(),
                new QName(XML_SCHEMA_URI, "anyType", XS_URI_PREFIX));
        simpleXSDTypes.put(Vector.class.getName(),
                new QName(XML_SCHEMA_URI, "anyType", XS_URI_PREFIX));
        simpleXSDTypes.put(List.class.getName(),
                new QName(XML_SCHEMA_URI, "anyType", XS_URI_PREFIX));
    }
    
    public QName getStdSdoType(String typename) 
    {
        return (QName) simpleXSDTypes.get(typename);
    }

    public QName getComplexSchemaTypeName(String sdoURI, String sdoTypeName) 
    {
        return (QName) complexXSDTypes.get(asQualifiedName(sdoURI, sdoTypeName));
    }    
    
    public QName getComplexSchemaTypeName(JClass javaClass, ClassLoader cl) throws Exception 
    {
        String namespace = Java2WSDLUtils.schemaNamespaceFromClassName(
        		javaClass.getQualifiedName(), cl).toString();
        return (QName) complexXSDTypes.get(asQualifiedName(namespace, javaClass.getSimpleName()));
    } 
    
    public boolean isSimpleType(String typeName) 
    {
        Iterator keys = simpleXSDTypes.keySet().iterator();
        while (keys.hasNext()) {
            String s = (String) keys.next();
            if (s.equals(typeName)) {
                return true;
            }
        }
        return false;
    }
    
    public QName getSimpleSchemaTypeName(String typename) 
    {
        return (QName) simpleXSDTypes.get(typename);
    }
    
    public void addSimpleSchemaType(String typeName, QName schemaType) 
    {
        simpleXSDTypes.put(typeName, schemaType);
    }
    
    
    public void addComplexSchemaType(String namespaceURI, String name, QName schemaType) 
    {
        complexXSDTypes.put(asQualifiedName(namespaceURI, name), schemaType);
    }

    
    public QName getQNamefortheType(String namespaceURI, String typeName) 
    {
        if ( XML_SCHEMA_URI.equals(namespaceURI))
        {
            return getSimpleSchemaTypeName(typeName);
        }
        else
        {
            return getComplexSchemaTypeName(namespaceURI, typeName);
        }
    }
}

