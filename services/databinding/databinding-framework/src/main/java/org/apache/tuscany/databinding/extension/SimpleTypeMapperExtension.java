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

import static org.apache.ws.commons.schema.constants.Constants.XSD_BASE64;
import static org.apache.ws.commons.schema.constants.Constants.XSD_BOOLEAN;
import static org.apache.ws.commons.schema.constants.Constants.XSD_BYTE;
import static org.apache.ws.commons.schema.constants.Constants.XSD_DATE;
import static org.apache.ws.commons.schema.constants.Constants.XSD_DATETIME;
import static org.apache.ws.commons.schema.constants.Constants.XSD_DAY;
import static org.apache.ws.commons.schema.constants.Constants.XSD_DECIMAL;
import static org.apache.ws.commons.schema.constants.Constants.XSD_DOUBLE;
import static org.apache.ws.commons.schema.constants.Constants.XSD_DURATION;
import static org.apache.ws.commons.schema.constants.Constants.XSD_FLOAT;
import static org.apache.ws.commons.schema.constants.Constants.XSD_HEXBIN;
import static org.apache.ws.commons.schema.constants.Constants.XSD_INT;
import static org.apache.ws.commons.schema.constants.Constants.XSD_INTEGER;
import static org.apache.ws.commons.schema.constants.Constants.XSD_LONG;
import static org.apache.ws.commons.schema.constants.Constants.XSD_MONTH;
import static org.apache.ws.commons.schema.constants.Constants.XSD_MONTHDAY;
import static org.apache.ws.commons.schema.constants.Constants.XSD_NOTATION;
import static org.apache.ws.commons.schema.constants.Constants.XSD_QNAME;
import static org.apache.ws.commons.schema.constants.Constants.XSD_SHORT;
import static org.apache.ws.commons.schema.constants.Constants.XSD_STRING;
import static org.apache.ws.commons.schema.constants.Constants.XSD_TIME;
import static org.apache.ws.commons.schema.constants.Constants.XSD_UNSIGNEDBYTE;
import static org.apache.ws.commons.schema.constants.Constants.XSD_UNSIGNEDINT;
import static org.apache.ws.commons.schema.constants.Constants.XSD_UNSIGNEDLONG;
import static org.apache.ws.commons.schema.constants.Constants.XSD_UNSIGNEDSHORT;
import static org.apache.ws.commons.schema.constants.Constants.XSD_YEAR;
import static org.apache.ws.commons.schema.constants.Constants.XSD_YEARMONTH;

import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;

import org.apache.tuscany.databinding.idl.SimpleTypeMapper;
import org.apache.tuscany.databinding.util.XSDDataTypeConverter;
import org.apache.ws.commons.schema.XmlSchemaSimpleType;

public class SimpleTypeMapperExtension extends XSDDataTypeConverter implements SimpleTypeMapper {
    private DatatypeFactory factory;

    public SimpleTypeMapperExtension() {
        super();
        try {
            this.factory = DatatypeFactory.newInstance();
        } catch (DatatypeConfigurationException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public Object parse(XmlSchemaSimpleType schemaSimpleType, String value) {

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
         * <li>xsd:anySimpleType (for xsd:element of this type)a java.lang.Object
         * <li>xsd:anySimpleType (for xsd:attribute of this type) java.lang.String
         * <li>xsd:duration javax.xml.datatype.Duration
         * <li>xsd:NOTATION javax.xml.namespace.QName
         * </ul>
         */

        XmlSchemaSimpleType baseType = schemaSimpleType;
        while (baseType.getBaseSchemaType() != null) {
            baseType = (XmlSchemaSimpleType) baseType.getBaseSchemaType();
        }
        QName type = baseType.getQName();
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
            return parseQName(value, null);
        } else if (type.equals(XSD_NOTATION)) {
            return parseQName(value, null);
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
        } else if (type.equals(XSD_NOTATION)) {
            return parseQName(value, null);
        } else if (type.equals(XSD_NOTATION)) {
            return parseQName(value, null);
        } else {
            return value;
        }
    }

    public String toString(XmlSchemaSimpleType schemaSimpleType, Object obj) {
        if (obj instanceof Float || obj instanceof Double) {
            double data;
            if (obj instanceof Float) {
                data = ((Float) obj).doubleValue();
            } else {
                data = ((Double) obj).doubleValue();
            }
            if (Double.isNaN(data)) {
                return "NaN";
            } else if (data == Double.POSITIVE_INFINITY) {
                return "INF";
            } else if (data == Double.NEGATIVE_INFINITY) {
                return "-INF";
            } else {
                return obj.toString();
            }
        } else if (obj instanceof GregorianCalendar) {
            GregorianCalendar calendar = (GregorianCalendar) obj;
            /*
             * if (type.equals(XSD_DATE)) { return printDate(calendar); } else if (type.equals(XSD_TIME)) { return
             * printTime(calendar); } else if (type.equals(XSD_DATETIME)) { return printDateTime(calendar); } else {
             * return printDateTime(calendar); }
             */
            return toXMLGregorianCalendar(calendar).toXMLFormat();
        } else if (obj instanceof Date) {
            return toXMLGregorianCalendar((Date) obj).toXMLFormat();
        } else if (obj instanceof XMLGregorianCalendar) {
            XMLGregorianCalendar calendar = (XMLGregorianCalendar) obj;
            return calendar.toXMLFormat();
        }
        return obj.toString();
    }

    private XMLGregorianCalendar toXMLGregorianCalendar(GregorianCalendar calendar) {
        return factory.newXMLGregorianCalendar(calendar);
    }

    @SuppressWarnings("deprecation")
    private XMLGregorianCalendar toXMLGregorianCalendar(Date date) {
        GregorianCalendar c =
                new GregorianCalendar(date.getYear(), date.getMonth(), date.getDate(), date.getHours(), date
                        .getMinutes(), date.getSeconds());
        return factory.newXMLGregorianCalendar(c);
    }

}