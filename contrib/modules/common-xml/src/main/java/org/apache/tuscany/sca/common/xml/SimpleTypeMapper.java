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

package org.apache.tuscany.sca.common.xml;

import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

/**
 * Simple type mapper that maps from XSD types to Java Classes and Java Classes to XSD types.
 *
 * @version $Rev$ $Date$
 */
public class SimpleTypeMapper extends XSDDataTypeConverter {

    public static final Map<Class<?>, String> JAVA2XML = new HashMap<Class<?>, String>();

    public static final String URI_2001_SCHEMA_XSD = "http://www.w3.org/2001/XMLSchema";

    public static final Map<QName, Class<?>> XML2JAVA = new HashMap<QName, Class<?>>();

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

    public static final QName XSD_STRING = new QName(URI_2001_SCHEMA_XSD, "string");

    public static final QName XSD_TIME = new QName(URI_2001_SCHEMA_XSD, "time");

    public static final QName XSD_TOKEN = new QName(URI_2001_SCHEMA_XSD, "token");

    public static final QName XSD_UNSIGNEDBYTE = new QName(URI_2001_SCHEMA_XSD, "unsignedByte");

    public static final QName XSD_UNSIGNEDINT = new QName(URI_2001_SCHEMA_XSD, "unsignedInt");

    public static final QName XSD_UNSIGNEDLONG = new QName(URI_2001_SCHEMA_XSD, "unsignedLong");

    public static final QName XSD_UNSIGNEDSHORT = new QName(URI_2001_SCHEMA_XSD, "unsignedShort");

    public static final QName XSD_YEAR = new QName(URI_2001_SCHEMA_XSD, "gYear");

    public static final QName XSD_YEARMONTH = new QName(URI_2001_SCHEMA_XSD, "gYearMonth");

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
        XML2JAVA.put(new QName(URI_2001_SCHEMA_XSD, "string"), java.lang.String.class);
        XML2JAVA.put(new QName(URI_2001_SCHEMA_XSD, "integer"), java.math.BigInteger.class);
        XML2JAVA.put(new QName(URI_2001_SCHEMA_XSD, "int"), int.class);
        XML2JAVA.put(new QName(URI_2001_SCHEMA_XSD, "long"), long.class);
        XML2JAVA.put(new QName(URI_2001_SCHEMA_XSD, "short"), short.class);
        XML2JAVA.put(new QName(URI_2001_SCHEMA_XSD, "decimal"), java.math.BigDecimal.class);
        XML2JAVA.put(new QName(URI_2001_SCHEMA_XSD, "float"), float.class);
        XML2JAVA.put(new QName(URI_2001_SCHEMA_XSD, "double"), double.class);
        XML2JAVA.put(new QName(URI_2001_SCHEMA_XSD, "boolean"), boolean.class);
        XML2JAVA.put(new QName(URI_2001_SCHEMA_XSD, "byte"), byte.class);
        XML2JAVA.put(new QName(URI_2001_SCHEMA_XSD, "QName"), javax.xml.namespace.QName.class);
        XML2JAVA.put(new QName(URI_2001_SCHEMA_XSD, "dateTime"), javax.xml.datatype.XMLGregorianCalendar.class);
        XML2JAVA.put(new QName(URI_2001_SCHEMA_XSD, "base64Binary"), byte[].class);
        XML2JAVA.put(new QName(URI_2001_SCHEMA_XSD, "hexBinary"), byte[].class);
        XML2JAVA.put(new QName(URI_2001_SCHEMA_XSD, "unsignedInt"), long.class);
        XML2JAVA.put(new QName(URI_2001_SCHEMA_XSD, "unsignedShort"), int.class);
        XML2JAVA.put(new QName(URI_2001_SCHEMA_XSD, "unsignedByte"), short.class);
        XML2JAVA.put(new QName(URI_2001_SCHEMA_XSD, "time"), javax.xml.datatype.XMLGregorianCalendar.class);
        XML2JAVA.put(new QName(URI_2001_SCHEMA_XSD, "date"), javax.xml.datatype.XMLGregorianCalendar.class);
        XML2JAVA.put(new QName(URI_2001_SCHEMA_XSD, "gDay"), javax.xml.datatype.XMLGregorianCalendar.class);
        XML2JAVA.put(new QName(URI_2001_SCHEMA_XSD, "gMonth"), javax.xml.datatype.XMLGregorianCalendar.class);
        XML2JAVA.put(new QName(URI_2001_SCHEMA_XSD, "gYear"), javax.xml.datatype.XMLGregorianCalendar.class);
        XML2JAVA.put(new QName(URI_2001_SCHEMA_XSD, "gYearMonth"), javax.xml.datatype.XMLGregorianCalendar.class);
        XML2JAVA.put(new QName(URI_2001_SCHEMA_XSD, "gMonthDay"), javax.xml.datatype.XMLGregorianCalendar.class);
        XML2JAVA.put(new QName(URI_2001_SCHEMA_XSD, "anySimpleType"), java.lang.Object.class); // For elements
        // XML2JAVA.put(new QName(URI_2001_SCHEMA_XSD, "anySimpleType"), java.lang.String.class); // For
        // attributes
        XML2JAVA.put(new QName(URI_2001_SCHEMA_XSD, "duration"), javax.xml.datatype.Duration.class);
        XML2JAVA.put(new QName(URI_2001_SCHEMA_XSD, "NOTATION"), javax.xml.namespace.QName.class);
    }

    public SimpleTypeMapper() {
        super();
    }

    public static Class<?> getJavaType(QName xmlType) {
        if (xmlType != null && URI_2001_SCHEMA_XSD.equals(xmlType.getNamespaceURI())) {
            return XML2JAVA.get(xmlType.getLocalPart());
        } else {
            return null;
        }
    }

    public static QName getXMLType(Class<?> javaType) {
        return new QName(URI_2001_SCHEMA_XSD, JAVA2XML.get(javaType));
    }

}
