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

package org.apache.tuscany.sca.databinding.xml;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

/**
 * @version $Rev$ $Date$
 */
public class SimpleXmlNodeImpl implements XmlNode {
    private static final String XSI_PREFIX = "xsi";
    private static final String XSI_NS = "http://www.w3.org/2001/XMLSchema-instance";
    private static final QName XSI_NIL = new QName(XSI_NS, "nil", XSI_PREFIX);
    private static final Map<String, String> NS_MAP = new HashMap<String, String>();
    static {
        NS_MAP.put(XSI_PREFIX, XSI_NS);
    }

    protected Type type;
    protected QName name;
    protected Object value;

    public SimpleXmlNodeImpl(QName name, Object value) {
        this(name, value, name != null ? Type.ELEMENT : Type.CHARACTERS);
    }

    public SimpleXmlNodeImpl(QName name, Object value, Type type) {
        super();
        this.type = type;
        this.name = name;
        this.value = value;
    }

    /**
     * @see org.apache.tuscany.sca.databinding.xml.XmlNode#attributes()
     */
    public List<XmlNode> attributes() {
        if (type == Type.ELEMENT && value == null) {
            // Nil element
            XmlNode attr = new SimpleXmlNodeImpl(XSI_NIL, "true");
            return Arrays.asList(attr);
        }
        return null;
    }

    /**
     * @see org.apache.tuscany.sca.databinding.xml.XmlNode#children()
     */
    public Iterator<XmlNode> children() {
        if (type == Type.ELEMENT && value != null) {
            // Nil element
            XmlNode node = new SimpleXmlNodeImpl(null, value);
            return Arrays.asList(node).iterator();
        }
        return null;
    }

    /**
     * @see org.apache.tuscany.sca.databinding.xml.XmlNode#getName()
     */
    public QName getName() {
        return name;
    }

    /**
     * @see org.apache.tuscany.sca.databinding.xml.XmlNode#getValue()
     */
    public String getValue() {
        return value == null ? null : String.valueOf(value);
    }

    /**
     * @see org.apache.tuscany.sca.databinding.xml.XmlNode#namespaces()
     */
    public Map<String, String> namespaces() {
        if (type == Type.ELEMENT && value == null) {
            return NS_MAP;
        }
        return null;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }
}
