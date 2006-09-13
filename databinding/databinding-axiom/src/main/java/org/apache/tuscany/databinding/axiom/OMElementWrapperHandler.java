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

package org.apache.tuscany.databinding.axiom;

import static org.apache.ws.commons.schema.constants.Constants.XSD_BOOLEAN;
import static org.apache.ws.commons.schema.constants.Constants.XSD_BYTE;
import static org.apache.ws.commons.schema.constants.Constants.XSD_DATETIME;
import static org.apache.ws.commons.schema.constants.Constants.XSD_DOUBLE;
import static org.apache.ws.commons.schema.constants.Constants.XSD_FLOAT;
import static org.apache.ws.commons.schema.constants.Constants.XSD_INT;
import static org.apache.ws.commons.schema.constants.Constants.XSD_LONG;
import static org.apache.ws.commons.schema.constants.Constants.XSD_SHORT;
import static org.apache.ws.commons.schema.constants.Constants.XSD_STRING;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.TimeZone;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.tuscany.databinding.TransformationContext;
import org.apache.tuscany.databinding.idl.WrapperHandler;
import org.apache.ws.commons.schema.XmlSchemaElement;
import org.apache.ws.commons.schema.XmlSchemaSimpleType;
import org.apache.ws.commons.schema.XmlSchemaType;

/**
 * OMElement wrapper handler implementation
 */
public class OMElementWrapperHandler implements WrapperHandler<OMElement> {

    private OMFactory factory;

    public OMElementWrapperHandler() {
        super();
        this.factory = OMAbstractFactory.getOMFactory();
    }

    public OMElement create(XmlSchemaElement element, TransformationContext context) {
        OMElement wrapper = factory.createOMElement(element.getQName(), null);
        return wrapper;
    }

    public Object getChild(OMElement wrapper, int i, XmlSchemaElement element) {
        int index = 0;
        for (Iterator e = wrapper.getChildElements(); e.hasNext();) {
            OMElement child = (OMElement) e.next();
            if (index != i) {
                continue;
            }
            if (child.getQName().equals(element.getQName())) {
                XmlSchemaType type = element.getSchemaType();
                if (type instanceof XmlSchemaSimpleType) {
                    return SimpleTypeMapper.getSimpleTypeObject(type.getQName(), child);
                } else {
                    return child;
                }
            }
        }
        return null;
    }

    public void setChild(OMElement wrapper, int i, XmlSchemaElement childElement, Object value) {
        if (childElement.getSchemaType() instanceof XmlSchemaSimpleType) {
            OMElement child = factory.createOMElement(childElement.getQName(), wrapper);
            factory.createOMText(child, SimpleTypeMapper.getStringValue(value));
        } else {
            wrapper.addChild((OMElement) value);
        }
    }

    public static class SimpleTypeMapper {
        private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        // 0123456789 0 123456789

        static {
            format.setTimeZone(TimeZone.getTimeZone("GMT"));
        }

        public static Object getSimpleTypeObject(QName type, OMElement value) {
            if (type.equals(XSD_STRING)) {
                return value.getText();
            } else if (type.equals(XSD_INT)) {
                return new Integer(value.getText());
            } else if (type.equals(XSD_BOOLEAN)) {
                return Boolean.valueOf(value.getText());
            } else if (type.equals(XSD_BYTE)) {
                return new Byte(value.getText());
            } else if (type.equals(XSD_DOUBLE)) {
                return new Double(value.getText());
            } else if (type.equals(XSD_SHORT)) {
                return new Short(value.getText());
            } else if (type.equals(XSD_LONG)) {
                return new Long(value.getText());
            } else if (type.equals(XSD_FLOAT)) {
                return new Float(value.getText());
            } else if (type.equals(XSD_DATETIME)) {
                return makeCalendar(value.getText(), false);
            } else {
                return value.getText();
            }
        }

        public static String getStringValue(Object obj) {
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
            } else if (obj instanceof Calendar) {
                return format.format(((Calendar) obj).getTime());
            }
            return obj.toString();
        }

        public static Object makeCalendar(String source, boolean returnDate) {
            Calendar calendar = Calendar.getInstance();
            Date date;
            boolean bc = false;

            // validate fixed portion of format
            if (source == null || source.length() == 0) {
                throw new NumberFormatException("badDateTime00");
            }
            if (source.charAt(0) == '+') {
                source = source.substring(1);
            }
            if (source.charAt(0) == '-') {
                source = source.substring(1);
                bc = true;
            }
            if (source.length() < 19) {
                throw new NumberFormatException("badDateTime00");
            }
            if (source.charAt(4) != '-' || source.charAt(7) != '-' || source.charAt(10) != 'T') {
                throw new NumberFormatException("badDate00");
            }
            if (source.charAt(13) != ':' || source.charAt(16) != ':') {
                throw new NumberFormatException("badTime00");
            }
            // convert what we have validated so far
            try {
                synchronized (format) {
                    date = format.parse(source.substring(0, 19) + ".000Z");
                }
            } catch (Exception e) {
                throw new NumberFormatException(e.toString());
            }
            int pos = 19;

            // parse optional milliseconds
            if (pos < source.length() && source.charAt(pos) == '.') {
                int milliseconds;
                int start = ++pos;
                while (pos < source.length() && Character.isDigit(source.charAt(pos))) {
                    pos++;
                }
                String decimal = source.substring(start, pos);
                if (decimal.length() == 3) {
                    milliseconds = Integer.parseInt(decimal);
                } else if (decimal.length() < 3) {
                    milliseconds = Integer.parseInt((decimal + "000").substring(0, 3));
                } else {
                    milliseconds = Integer.parseInt(decimal.substring(0, 3));
                    if (decimal.charAt(3) >= '5') {
                        ++milliseconds;
                    }
                }

                // add milliseconds to the current date
                date.setTime(date.getTime() + milliseconds);
            }

            // parse optional timezone
            if (pos + 5 < source.length() && (source.charAt(pos) == '+' || (source.charAt(pos) == '-'))) {
                if (!Character.isDigit(source.charAt(pos + 1)) || !Character.isDigit(source.charAt(pos + 2))
                        || source.charAt(pos + 3) != ':' || !Character.isDigit(source.charAt(pos + 4))
                        || !Character.isDigit(source.charAt(pos + 5))) {
                    throw new NumberFormatException("badTimezone00");
                }
                int hours = (source.charAt(pos + 1) - '0') * 10 + source.charAt(pos + 2) - '0';
                int mins = (source.charAt(pos + 4) - '0') * 10 + source.charAt(pos + 5) - '0';
                int milliseconds = (hours * 60 + mins) * 60 * 1000;

                // subtract milliseconds from current date to obtain GMT
                if (source.charAt(pos) == '+') {
                    milliseconds = -milliseconds;
                }
                date.setTime(date.getTime() + milliseconds);
                pos += 6;
            }
            if (pos < source.length() && source.charAt(pos) == 'Z') {
                pos++;
                calendar.setTimeZone(TimeZone.getTimeZone("GMT"));
            }
            if (pos < source.length()) {
                throw new NumberFormatException("badChars00");
            }
            calendar.setTime(date);

            // support dates before the Christian era
            if (bc) {
                calendar.set(Calendar.ERA, GregorianCalendar.BC);
            }

            if (returnDate) {
                return date;
            } else {
                return calendar;
            }
        }
    }

}
