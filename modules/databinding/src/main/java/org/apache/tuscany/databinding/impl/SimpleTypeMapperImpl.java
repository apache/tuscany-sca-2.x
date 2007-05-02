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

package org.apache.tuscany.databinding.impl;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;

import org.apache.tuscany.databinding.SimpleTypeMapper;
import org.apache.tuscany.databinding.TransformationContext;
import org.apache.tuscany.interfacedef.util.TypeInfo;

public class SimpleTypeMapperImpl extends XSDDataTypeConverter implements SimpleTypeMapper {

    public static final Map<Class, String> JAVA2XML = new HashMap<Class, String>();

    public static final String URI_2001_SCHEMA_XSD = "http://www.w3.org/2001/XMLSchema";

    public static final Map<String, Class> XML2JAVA = new HashMap<String, Class>();

    public static final QName XSD_ANY = new QName(URI_2001_SCHEMA_XSD, "any");

    public static final QName XSD_ANYSIMPLETYPE = new QName(URI_2001_SCHEMA_XSD, "anySimpleType");

    public static final QName XSD_ANYTYPE = new QName(URI_2001_SCHEMA_XSD, "anyType");

    public static final QName XSD_ANYURI = new QName(URI_2001_SCHEMA_XSD, "anyURI");

    public static final QName XSD_BASE64 = new QName(URI_2001_SCHEMA_XSD, "base64Binary");

    public static final QName XSD_BOOLEAN = new QName(URI_2001_SCHEMA_XSD, "boolean");

    public static final QName XSD_BYTE = new QName(URI_2001_SCHEMA_XSD, "byte");

    public static final QName XSD_DATE = new QName(URI_2001_SCHEMA_XSD, "date");

    public static final QName XSD_DATETIME = new QName(URI_2001_SCHEMA_XSD, "dateTime");

    public static final QName XSD_DAY = new QName(URI_2001_SCHEMA_XSD, "gDay");

    public static final QName XSD_DECIMAL = new QName(URI_2001_SCHEMA_XSD, "decimal");

    public static final QName XSD_DOUBLE = new QName(URI_2001_SCHEMA_XSD, "double");

    public static final QName XSD_DURATION = new QName(URI_2001_SCHEMA_XSD, "duration");

    public static final QName XSD_ENTITIES = new QName(URI_2001_SCHEMA_XSD, "ENTITIES");

    public static final QName XSD_ENTITY = new QName(URI_2001_SCHEMA_XSD, "ENTITY");

    public static final QName XSD_FLOAT = new QName(URI_2001_SCHEMA_XSD, "float");

    public static final QName XSD_HEXBIN = new QName(URI_2001_SCHEMA_XSD, "hexBinary");

    public static final QName XSD_IDREF = new QName(URI_2001_SCHEMA_XSD, "IDREF");

    public static final QName XSD_IDREFS = new QName(URI_2001_SCHEMA_XSD, "IDREFS");

    public static final QName XSD_INT = new QName(URI_2001_SCHEMA_XSD, "int");

    public static final QName XSD_INTEGER = new QName(URI_2001_SCHEMA_XSD, "integer");

    public static final QName XSD_LONG = new QName(URI_2001_SCHEMA_XSD, "long");

    public static final QName XSD_MONTH = new QName(URI_2001_SCHEMA_XSD, "gMonth");

    public static final QName XSD_MONTHDAY = new QName(URI_2001_SCHEMA_XSD, "gMonthDay");

    public static final QName XSD_NAME = new QName(URI_2001_SCHEMA_XSD, "Name");

    public static final QName XSD_NCNAME = new QName(URI_2001_SCHEMA_XSD, "NCName");

    public static final QName XSD_NEGATIVEINTEGER = new QName(URI_2001_SCHEMA_XSD, "negativeInteger");

    public static final QName XSD_NMTOKEN = new QName(URI_2001_SCHEMA_XSD, "NMTOKEN");

    public static final QName XSD_NMTOKENS = new QName(URI_2001_SCHEMA_XSD, "NMTOKENS");

    public static final QName XSD_NONNEGATIVEINTEGER = new QName(URI_2001_SCHEMA_XSD, "nonNegativeInteger");

    public static final QName XSD_NONPOSITIVEINTEGER = new QName(URI_2001_SCHEMA_XSD, "nonPositiveInteger");

    public static final QName XSD_NORMALIZEDSTRING = new QName(URI_2001_SCHEMA_XSD, "normalizedString");

    public static final QName XSD_NOTATION = new QName(URI_2001_SCHEMA_XSD, "NOTATION");

    public static final QName XSD_POSITIVEINTEGER = new QName(URI_2001_SCHEMA_XSD, "positiveInteger");

    public static final QName XSD_QNAME = new QName(URI_2001_SCHEMA_XSD, "QName");

    public static final QName XSD_SHORT = new QName(URI_2001_SCHEMA_XSD, "short");

    public static final Map<String, TypeInfo> XSD_SIMPLE_TYPES = new HashMap<String, TypeInfo>();

    public static final QName XSD_STRING = new QName(URI_2001_SCHEMA_XSD, "string");

    public static final QName XSD_TIME = new QName(URI_2001_SCHEMA_XSD, "time");

    public static final QName XSD_TOKEN = new QName(URI_2001_SCHEMA_XSD, "token");

    public static final QName XSD_UNSIGNEDBYTE = new QName(URI_2001_SCHEMA_XSD, "unsignedByte");

    public static final QName XSD_UNSIGNEDINT = new QName(URI_2001_SCHEMA_XSD, "unsignedInt");

    public static final QName XSD_UNSIGNEDLONG = new QName(URI_2001_SCHEMA_XSD, "unsignedLong");

    public static final QName XSD_UNSIGNEDSHORT = new QName(URI_2001_SCHEMA_XSD, "unsignedShort");

    public static final QName XSD_YEAR = new QName(URI_2001_SCHEMA_XSD, "gYear");

    public static final QName XSD_YEARMONTH = new QName(URI_2001_SCHEMA_XSD, "gYearMonth");

    private static final String[] XSD_TYPE_NAMES =
    {"string", "boolean", "double", "float", "int", "integer", "long", "short", "byte", "decimal", "base64Binary",
     "hexBinary", "anySimpleType", "anyType", "any", "QName", "dateTime", "date", "time", "normalizedString",
     "token", "unsignedLong", "unsignedInt", "unsignedShort", "unsignedByte", "positiveInteger", "negativeInteger",
     "nonNegativeInteger", "nonPositiveInteger", "gYearMonth", "gMonthDay", "gYear", "gMonth", "gDay", "duration",
     "Name", "NCName", "NMTOKEN", "NMTOKENS", "NOTATION", "ENTITY", "ENTITIES", "IDREF", "IDREFS", "anyURI",
     "language", "ID"};

    static {
        for (String type : XSD_TYPE_NAMES) {
            TypeInfo simpleType = new TypeInfo(new QName(URI_2001_SCHEMA_XSD, type), true, null);
            XSD_SIMPLE_TYPES.put(type, simpleType);
        }
    }

    static {
        JAVA2XML.put(boolean.class, "boolean");
        JAVA2XML.put(byte.class, "byte");
        JAVA2XML.put(short.class, "short");
        JAVA2XML.put(int.class, "int");
        JAVA2XML.put(long.class, "long");
        JAVA2XML.put(float.class, "float");
        JAVA2XML.put(double.class, "double");
        JAVA2XML.put(Boolean.class, "boolean");
        JAVA2XML.put(Byte.class, "byte");
        JAVA2XML.put(Short.class, "short");
        JAVA2XML.put(Integer.class, "int");
        JAVA2XML.put(Long.class, "long");
        JAVA2XML.put(Float.class, "float");
        JAVA2XML.put(Double.class, "double");
        JAVA2XML.put(java.lang.String.class, "string");
        JAVA2XML.put(java.math.BigInteger.class, "integer");
        JAVA2XML.put(java.math.BigDecimal.class, "decimal");
        JAVA2XML.put(java.util.Calendar.class, "dateTime");
        JAVA2XML.put(java.util.Date.class, "dateTime");
        JAVA2XML.put(javax.xml.namespace.QName.class, "QName");
        JAVA2XML.put(java.net.URI.class, "string");
        JAVA2XML.put(javax.xml.datatype.XMLGregorianCalendar.class, "anySimpleType");
        JAVA2XML.put(javax.xml.datatype.Duration.class, "duration");
        JAVA2XML.put(java.lang.Object.class, "anyType");
        JAVA2XML.put(java.awt.Image.class, "base64Binary");
        JAVA2XML.put(byte[].class, "base64Binary");
        // java2XSD.put(javax.activation.DataHandler.class, "base64Binary");
        JAVA2XML.put(javax.xml.transform.Source.class, "base64Binary");
        JAVA2XML.put(java.util.UUID.class, "string");
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

    private DatatypeFactory factory;

    public SimpleTypeMapperImpl() {
        super();
        try {
            this.factory = DatatypeFactory.newInstance();
        } catch (DatatypeConfigurationException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static Class getJavaType(QName xmlType) {
        if (URI_2001_SCHEMA_XSD.equals(xmlType.getNamespaceURI())) {
            return XML2JAVA.get(xmlType.getLocalPart());
        } else {
            return null;
        }
    }

    public TypeInfo getXMLType(Class javaType) {
        return XSD_SIMPLE_TYPES.get(JAVA2XML.get(javaType));
    }

    public Object toJavaObject(QName simpleType, String literal, TransformationContext context) {
        /**
         * <ul>
         * <li>xsd:string --- java.lang.String
         * <li>xsd:integer --- java.math.BigInteger
         * <li>xsd:int --- int
         * <li>xsd:long --- long
         * <li>xsd:short --- short
         * <li>xsd:decimal --- java.math.BigDecimal
         * <li>xsd:float --- float
         * <li>xsd:double --- double
         * <li>xsd:boolean --- boolean
         * <li>xsd:byte --- byte
         * <li>xsd:QName --- javax.xml.namespace.QName
         * <li>xsd:dateTime --- javax.xml.datatype.XMLGregorianCalendar
         * <li>xsd:base64Binary --- byte[]
         * <li>xsd:hexBinary --- byte[]
         * <li>xsd:unsignedInt --- long
         * <li>xsd:unsignedShort --- int
         * <li>xsd:unsignedByte --- short
         * <li>xsd:time --- javax.xml.datatype.XMLGregorianCalendar
         * <li>xsd:date --- javax.xml.datatype.XMLGregorianCalendar
         * <li>xsd:g* --- javax.xml.datatype.XMLGregorianCalendar
         * <li>xsd:anySimpleType (for xsd:element of this type)a
         * java.lang.Object
         * <li>xsd:anySimpleType (for xsd:attribute of this type)
         * java.lang.String
         * <li>xsd:duration javax.xml.datatype.Duration
         * <li>xsd:NOTATION javax.xml.namespace.QName
         * </ul>
         */

        if (literal == null) {
            return null;
        }
        String value = literal.trim();

        QName type = simpleType;
        if (type.equals(XSD_STRING)) {
            return parseString(value);
        } else if (type.equals(XSD_INT)) {
            return parseInt(value);
        } else if (type.equals(XSD_INTEGER)) {
            return parseInteger(value);
        } else if (type.equals(XSD_INT)) {
            return parseInt(value);
        } else if (type.equals(XSD_FLOAT)) {
            return parseFloat(value);
        } else if (type.equals(XSD_DOUBLE)) {
            return parseDouble(value);
        } else if (type.equals(XSD_SHORT)) {
            return parseShort(value);
        } else if (type.equals(XSD_DECIMAL)) {
            return parseDecimal(value);
        } else if (type.equals(XSD_BOOLEAN)) {
            return parseBoolean(value);
        } else if (type.equals(XSD_BYTE)) {
            return parseByte(value);
        } else if (type.equals(XSD_LONG)) {
            return parseLong(value);
        } else if (type.equals(XSD_UNSIGNEDBYTE)) {
            return parseUnsignedShort(value);
        } else if (type.equals(XSD_UNSIGNEDSHORT)) {
            return parseUnsignedShort(value);
        } else if (type.equals(XSD_UNSIGNEDINT)) {
            return parseUnsignedInt(value);
        } else if (type.equals(XSD_UNSIGNEDLONG)) {
            return parseUnsignedInt(value);
        } else if (type.equals(XSD_DATETIME)) {
            return parseDateTime(value);
        } else if (type.equals(XSD_DATE)) {
            return parseDate(value);
        } else if (type.equals(XSD_TIME)) {
            return parseTime(value);
        } else if (type.equals(XSD_DURATION)) {
            return parseDuration(value);
        } else if (type.equals(XSD_HEXBIN)) {
            return parseHexBinary(value);
        } else if (type.equals(XSD_BASE64)) {
            return parseBase64Binary(value);
        } else if (type.equals(XSD_QNAME)) {
            NamespaceContext namespaceContext =
                (NamespaceContext)((context != null) ? context.getMetadata().get(NamespaceContext.class.getName()) : null);
            return parseQName(value, namespaceContext);
        } else if (type.equals(XSD_NOTATION)) {
            NamespaceContext namespaceContext =
                (NamespaceContext)((context != null) ? context.getMetadata().get(NamespaceContext.class.getName()) : null);
            return parseQName(value, namespaceContext);
        } else if (type.equals(XSD_YEAR)) {
            return factory.newXMLGregorianCalendar(value);
        } else if (type.equals(XSD_MONTH)) {
            return factory.newXMLGregorianCalendar(value);
        } else if (type.equals(XSD_DAY)) {
            return factory.newXMLGregorianCalendar(value);
        } else if (type.equals(XSD_YEARMONTH)) {
            return factory.newXMLGregorianCalendar(value);
        } else if (type.equals(XSD_MONTHDAY)) {
            return factory.newXMLGregorianCalendar(value);
        } else {
            return value;
        }
    }

    @SuppressWarnings("deprecation")
    private XMLGregorianCalendar toXMLGregorianCalendar(Date date) {
        GregorianCalendar c =
            new GregorianCalendar(date.getYear(), date.getMonth(), date.getDate(), date.getHours(), date.getMinutes(),
                                  date.getSeconds());
        return factory.newXMLGregorianCalendar(c);
    }

    private XMLGregorianCalendar toXMLGregorianCalendar(GregorianCalendar calendar) {
        return factory.newXMLGregorianCalendar(calendar);
    }

    public String toXMLLiteral(QName simpleType, Object obj, TransformationContext context) {
        if (obj instanceof Float || obj instanceof Double) {
            if (obj instanceof Float) {
                return printDouble(((Float)obj).floatValue());
            } else {
                return printDouble(((Double)obj).doubleValue());
            }
        } else if (obj instanceof GregorianCalendar) {
            GregorianCalendar calendar = (GregorianCalendar)obj;
            return toXMLGregorianCalendar(calendar).toXMLFormat();
        } else if (obj instanceof Date) {
            return toXMLGregorianCalendar((Date)obj).toXMLFormat();
        } else if (obj instanceof XMLGregorianCalendar) {
            return ((XMLGregorianCalendar)obj).toXMLFormat();
        } else if (obj instanceof byte[]) {
            if (simpleType != null) {
                if (simpleType.equals(XSD_BASE64)) {
                    return printBase64Binary((byte[])obj);
                } else if (simpleType.equals(XSD_HEXBIN)) {
                    return printHexBinary((byte[])obj);
                }
            }
        } else if (obj instanceof QName) {
            NamespaceContext namespaceContext =
                (NamespaceContext)((context != null) ? context.getMetadata().get(NamespaceContext.class.getName()) : null);
            return printQName((QName)obj, namespaceContext);
        }
        return obj.toString();
    }

    public static boolean isSimpleXSDType(QName typeName) {
        if (typeName == null) {
            return false;
        }
        return typeName.getNamespaceURI().equals(URI_2001_SCHEMA_XSD) 
            && XSD_SIMPLE_TYPES.get(typeName.getLocalPart()) != null;
    }

}
